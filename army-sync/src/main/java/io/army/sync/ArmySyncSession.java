package io.army.sync;

import io.army.ArmyException;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.session.*;
import io.army.session.executor.DriverSpiHolder;
import io.army.session.record.CurrentRecord;
import io.army.session.record.ResultStates;
import io.army.stmt.*;
import io.army.sync.executor.SyncExecutor;
import io.army.util.ArmyCriteria;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link ArmySyncLocalSession}</li>
 *     <li>{@link ArmySyncRmSession}</li>
 * </ul>
 * <p>This class extends {@link _ArmySession} and implements of {@link SyncSession}.
 *
 * @since 0.6.0
 */
abstract class ArmySyncSession extends _ArmySession implements SyncSession {


    final SyncExecutor stmtExecutor;

    private boolean sessionClosed;

    ArmySyncSession(ArmySyncSessionFactory.SyncBuilder<?, ?> builder) {
        super(builder);
        this.stmtExecutor = builder.stmtExecutor;
        assert this.stmtExecutor != null;
    }

    @Override
    public final boolean isReactive() {
        // always false
        return false;
    }

    @Override
    public final boolean isSync() {
        // always true
        return true;
    }

    @Override
    public final SyncSessionFactory sessionFactory() {
        return (SyncSessionFactory) this.factory;
    }

    @Override
    public final long sessionIdentifier() throws SessionException {
        if (((ArmySyncSessionFactory) this.factory).sessionIdentifierEnable) {
            return this.stmtExecutor.sessionIdentifier();
        }
        return 0L;
    }

    @Override
    public final boolean inTransaction() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        if (((ArmySyncSessionFactory) this.factory).jdbcDriver || !(this instanceof DriverSpiHolder)) {
            // JDBC don't support to get the state from database client protocol.
            final TransactionInfo info = obtainTransactionInfo();
            return info != null && info.inTransaction();
        }
        // due to session open driver spi to application developer, TransactionInfo probably error.
        return this.stmtExecutor.inTransaction();
    }


    @Override
    public final TransactionInfo transactionInfo() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        final TransactionInfo info;
        info = obtainTransactionInfo();
        if (info != null) {
            return info;
        }
        try {
            return this.stmtExecutor.transactionInfo();
        } catch (Exception e) {
            throw _ArmySession.wrapSessionError(e);
        }
    }


    @Override
    public final Object setSavePoint() {
        return setSavePoint(Option.EMPTY_FUNC);
    }

    @Override
    public final Object setSavePoint(Function<Option<?>, ?> optionFunc) {
        if (inPseudoTransaction()) {
            return PSEUDO_SAVE_POINT;
        }
        try {
            return this.stmtExecutor.setSavePoint(optionFunc);
        } catch (Exception e) {
            throw wrapSessionError(e);
        }
    }

    @Override
    public final void releaseSavePoint(Object savepoint) {
        releaseSavePoint(savepoint, Option.EMPTY_FUNC);
    }

    @Override
    public final void releaseSavePoint(final Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!inPseudoTransaction()) {
            try {
                this.stmtExecutor.releaseSavePoint(savepoint, optionFunc);
            } catch (Exception e) {
                throw wrapSessionError(e);
            }
        } else if (!PSEUDO_SAVE_POINT.equals(savepoint)) {
            throw _Exceptions.unknownSavePoint(savepoint);
        }

    }

    @Override
    public final void rollbackToSavePoint(Object savepoint) {
        rollbackToSavePoint(savepoint, Option.EMPTY_FUNC);
    }

    @Override
    public final void rollbackToSavePoint(final Object savepoint, Function<Option<?>, ?> optionFunc) {
        if (!inPseudoTransaction()) {
            try {
                this.stmtExecutor.rollbackToSavePoint(savepoint, optionFunc);
            } catch (Exception e) {
                throw wrapSessionError(e);
            }
        } else if (!PSEUDO_SAVE_POINT.equals(savepoint)) {
            throw _Exceptions.unknownSavePoint(savepoint);
        }

    }

    @Override
    public final void setTransactionCharacteristics(TransactionOption option) {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        this.stmtExecutor.setTransactionCharacteristics(option);
    }

    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass) {
        return onlyRow(this.queryList(statement, resultClass, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return onlyRow(this.queryList(statement, resultClass, _Collections::arrayList, option));
    }

    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return onlyRow(this.queryObjectList(statement, constructor, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return onlyRow(this.queryObjectList(statement, constructor, _Collections::arrayList, option));
    }

    @Override
    public final <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return onlyRow(this.queryRecordList(statement, function, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return onlyRow(this.queryRecordList(statement, function, _Collections::arrayList, option));
    }

    @Override
    public final <R> List<R> queryList(DqlStatement statement, Class<R> resultClass) {
        return queryList(statement, resultClass, _Collections::arrayList, defaultOption());
    }

    @Override
    public final <R> List<R> queryList(DqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return queryList(statement, resultClass, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryList(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return queryList(statement, resultClass, listConstructor, defaultOption());
    }

    @Override
    public final <R> List<R> queryList(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        try (Stream<R> stream = query(statement, resultClass, option)) {

            return stream.collect(Collectors.toCollection(listConstructor));
        }
    }


    @Override
    public final <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor) {
        return this.queryObjectList(statement, constructor, _Collections::arrayList, defaultOption());
    }

    @Override
    public final <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return this.queryObjectList(statement, constructor, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor) {
        return this.queryObjectList(statement, constructor, listConstructor, defaultOption());
    }

    @Override
    public final <R> List<R> queryObjectList(DqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        try (Stream<R> stream = queryObject(statement, constructor, option)) {
            return stream.collect(Collectors.toCollection(listConstructor));
        }
    }

    @Override
    public final <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecordList(statement, function, _Collections::arrayList, defaultOption());
    }

    @Override
    public final <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return this.queryRecordList(statement, function, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor) {
        return this.queryRecordList(statement, function, listConstructor, defaultOption());
    }

    @Override
    public final <R> List<R> queryRecordList(DqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        try (Stream<R> stream = queryRecord(statement, function, option)) {
            return stream.collect(Collectors.toCollection(listConstructor));
        }
    }

    @Override
    public final <R> Stream<R> query(DqlStatement statement, Class<R> resultClass) {
        return this.query(statement, resultClass, defaultOption());
    }

    @Override
    public final <R> Stream<R> query(DqlStatement statement, final Class<R> resultClass, SyncStmtOption option) {
        return this.executeQuery(statement, option, (s, o) -> this.stmtExecutor.query(s, resultClass, o)); // here ,use o not option
    }

    @Override
    public final <R> Stream<R> queryObject(DqlStatement statement, Supplier<R> constructor) {
        return this.queryObject(statement, constructor, defaultOption());
    }

    @Override
    public final <R> Stream<R> queryObject(DqlStatement statement, final Supplier<R> constructor, SyncStmtOption option) {
        return this.executeQuery(statement, option, (s, o) -> this.stmtExecutor.queryObject(s, constructor, o)); // here ,use o not option
    }

    @Override
    public final <R> Stream<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecord(statement, function, defaultOption());
    }

    @Override
    public final <R> Stream<R> queryRecord(DqlStatement statement, final Function<CurrentRecord, R> function, SyncStmtOption option) {
        if (statement instanceof _Statement._ChildStatement) {
            throw new SessionException("queryRecord api don't support two statement mode");
        }
        return this.executeQuery(statement, option, (s, o) -> this.stmtExecutor.queryRecord(s, function, o)); // here ,use o not option
    }

    @Override
    public final <T> int save(T domain) {
        return save(domain, defaultOption());
    }

    @Override
    public final <T> int save(T domain, SyncStmtOption option) {
        final long rowCount;
        rowCount = update(ArmyCriteria.insertStmt(this, domain), option);
        if (rowCount > 1) {
            throw new DataAccessException(String.format("insert row count[%s] great than one ", rowCount));
        }
        return (int) rowCount;
    }

    @Override
    public final long update(SimpleDmlStatement statement) {
        return updateAsResult(statement, defaultOption(), Long.class);
    }

    @Override
    public final long update(SimpleDmlStatement statement, SyncStmtOption option) {
        return updateAsResult(statement, option, Long.class);
    }


    @Override
    public final ResultStates updateAsStates(SimpleDmlStatement statement) {
        return updateAsResult(statement, defaultOption(), ResultStates.class);
    }

    @Override
    public final ResultStates updateAsStates(SimpleDmlStatement statement, SyncStmtOption option) {
        return updateAsResult(statement, option, ResultStates.class);
    }

    @Override
    public final <T> int batchSave(List<T> domainList) {
        return batchSave(domainList, defaultOption());
    }

    @Override
    public final <T> int batchSave(List<T> domainList, SyncStmtOption option) {
        final long rowCount;
        rowCount = this.update(ArmyCriteria.batchInsertStmt(this, domainList), option);

        if (rowCount > domainList.size()) {
            String m = String.format("insert row count[%s] and expected row count[%s] not match.",
                    rowCount, domainList.size());
            throw new DataAccessException(m);
        }
        return (int) rowCount;
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement) {
        return this.batchUpdate(statement, _Collections::arrayList, defaultOption());
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, SyncStmtOption option) {
        return this.batchUpdate(statement, _Collections::arrayList, option);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor) {
        return this.batchUpdate(statement, listConstructor, defaultOption());
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, SyncStmtOption option) {
        try {
            if (!(statement instanceof _BatchStatement)) {
                throw _Exceptions.unexpectedStatement(statement);
            }

            assertSession(statement);

            final Stmt stmt;
            stmt = this.parseDmlStatement(statement, option);

            final TableMeta<?> domainTable;
            domainTable = getBatchUpdateDomainTable(statement);

            final List<Long> resultList;
            if (stmt instanceof BatchStmt) {
                resultList = this.stmtExecutor.batchUpdateList((BatchStmt) stmt, listConstructor, option, Long.class, domainTable, null);
            } else if (!(stmt instanceof PairBatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (!this.inTransaction()) {
                throw updateChildNoTransaction();
            } else {
                assert domainTable instanceof ChildTableMeta; // fail, bug.
                final PairBatchStmt pairStmt = (PairBatchStmt) stmt;

                final List<Long> childList;
                childList = this.stmtExecutor.batchUpdateList(pairStmt.firstStmt(), listConstructor, option, Long.class, domainTable, null);
                resultList = this.stmtExecutor.batchUpdateList(pairStmt.secondStmt(), listConstructor, option, Long.class, domainTable, childList);
            }
            return resultList;
        } catch (ChildUpdateException e) {
            rollbackOnlyOnError(e);
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).close();
            }
        }

    }

    @Override
    public final Stream<ResultStates> batchUpdateAsStates(BatchDmlStatement statement) {
        return batchUpdateAsStates(statement, defaultOption());
    }

    @Override
    public final Stream<ResultStates> batchUpdateAsStates(BatchDmlStatement statement, SyncStmtOption option) {
        try {
            if (!(statement instanceof _BatchStatement)) {
                throw _Exceptions.unexpectedStatement(statement);
            }

            assertSession(statement);

            final Stmt stmt;
            stmt = this.parseDmlStatement(statement, option);

            final TableMeta<?> domainTable;
            domainTable = getBatchUpdateDomainTable(statement);

            final Stream<ResultStates> stream;
            if (stmt instanceof BatchStmt) {
                stream = this.stmtExecutor.batchUpdate((BatchStmt) stmt, option, ResultStates.class, domainTable, null);
            } else if (!(stmt instanceof PairBatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (!this.inTransaction()) {
                throw updateChildNoTransaction();
            } else {
                assert domainTable instanceof ChildTableMeta; // fail, bug.
                final PairBatchStmt pairStmt = (PairBatchStmt) stmt;

                final List<ResultStates> childList;
                childList = this.stmtExecutor.batchUpdate(pairStmt.firstStmt(), option, ResultStates.class, domainTable, null)
                        .collect(Collectors.toCollection(_Collections::arrayList));

                stream = this.stmtExecutor.batchUpdate(pairStmt.secondStmt(), option, ResultStates.class, domainTable, childList);
            }
            return stream;
        } catch (ChildUpdateException e) {
            rollbackOnlyOnError(e);
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).close();
            }
        }
    }

    @Override
    public final <T> T valueOf(Option<T> option) {
        return this.stmtExecutor.valueOf(option);
    }

    @Override
    public final boolean isClosed() {
        return this.sessionClosed;
    }

    @Override
    public final void close() throws SessionException {
        if (this.sessionClosed) {
            return;
        }

        synchronized (this) {
            if (this.sessionClosed) {
                return;
            }
            try {
                this.sessionClosed = true;
                this.stmtExecutor.close();
            } catch (Exception e) {
                throw wrapSessionError(e);
            }
        }

    }

    final void releaseSession() {
        this.sessionClosed = true;
    }



    /*-------------------below private methods -------------------*/

    private SyncStmtOption defaultOption() {
        final TransactionInfo info;
        info = obtainTransactionInfo();

        final SyncStmtOption option;
        if (info == null) {
            option = ArmySyncStmtOptions.DEFAULT;
        } else {
            option = ArmySyncStmtOptions.overrideOptionIfNeed(ArmySyncStmtOptions.DEFAULT, info);
        }
        return option;
    }

    private SyncStmtOption replaceIfNeed(final SyncStmtOption option) {
        final TransactionInfo info;

        final SyncStmtOption newOption;
        if (option instanceof ArmySyncStmtOptions.TransactionOverrideOption
                || (info = obtainTransactionInfo()) == null) {
            newOption = option;
        } else {
            newOption = ArmySyncStmtOptions.overrideOptionIfNeed(option, info);
        }
        return newOption;
    }


    private <R> Stream<R> executeQuery(final DqlStatement statement, final SyncStmtOption unsafeOption,
                                       final BiFunction<SimpleStmt, SyncStmtOption, Stream<R>> exeFunc) {
        try {
            assertSession(statement);

            final SyncStmtOption option;
            option = replaceIfNeed(unsafeOption);

            final Stmt stmt;
            stmt = parseDqlStatement(statement, option);

            final Stream<R> stream;
            if (stmt instanceof SimpleStmt) {
                stream = exeFunc.apply((SimpleStmt) stmt, option);
            } else if (!(stmt instanceof PairStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (!inTransaction()) {
                throw updateChildNoTransaction();
            } else if (statement instanceof InsertStatement) {
                stream = executePairInsertQuery((InsertStatement) statement, option, (PairStmt) stmt, exeFunc);
            } else {
                // TODO add for firebird
                throw _Exceptions.unexpectedStmt(stmt);
            }
            return stream;
        } catch (ChildUpdateException e) {
            rollbackOnlyOnError(e);
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).close();
            }
        }

    }

    /**
     * @param option the instance is returned by {@link #replaceIfNeed(SyncStmtOption)}
     * @see #executeQuery(DqlStatement, SyncStmtOption, BiFunction)
     */
    private <R> Stream<R> executePairInsertQuery(InsertStatement statement,
                                                 final SyncStmtOption option, PairStmt stmt,
                                                 BiFunction<SimpleStmt, SyncStmtOption, Stream<R>> exeFunc) {
        final _Insert._ChildInsert childInsert = (_Insert._ChildInsert) statement;
        final boolean firstStmtIsQuery = childInsert.parentStmt() instanceof _ReturningDml;

        long rows = 0;
        List<R> resultList = null;
        if (firstStmtIsQuery) {
            resultList = exeFunc.apply(stmt.firstStmt(), option)
                    .collect(Collectors.toCollection(_Collections::arrayList));
            if (resultList.size() == 0) {
                // exists conflict clause
                return Stream.empty();
            }
        } else {
            rows = this.stmtExecutor.insert(stmt.firstStmt(), option, Long.class);
            if (rows == 0) {
                // exists conflict clause
                return Stream.empty();
            }
        }

        final ChildTableMeta<?> childTable;
        childTable = (ChildTableMeta<?>) childInsert.table();

        try {

            final Stream<R> stream;
            if (firstStmtIsQuery) {
                stream = this.stmtExecutor.secondQuery((TwoStmtQueryStmt) stmt.secondStmt(), option, resultList);
            } else {
                final long parentRows = rows;
                final Consumer<ResultStates> statesConsumer;
                statesConsumer = states -> {
                    if (states.rowCount() != parentRows) {
                        throw _Exceptions.parentChildRowsNotMatch(this, childTable, parentRows, states.rowCount());
                    }
                };
                stream = exeFunc.apply(stmt.secondStmt(), ArmySyncStmtOptions.replaceStateConsumer(option, statesConsumer));
            }
            return stream;
        } catch (Throwable e) {
            throw _Exceptions.childInsertError(this, childTable, e);
        }
    }


    /**
     * @param <R> the class of {@link Long} or {@link ResultStates}
     * @see #update(SimpleDmlStatement, SyncStmtOption)
     * @see #updateAsStates(SimpleDmlStatement, SyncStmtOption)
     */
    private <R> R updateAsResult(final SimpleDmlStatement statement, final SyncStmtOption unsafeOption, Class<R> resultClass) {
        try {
            if (statement instanceof _BatchStatement) {
                throw _Exceptions.unexpectedStatement(statement);
            }

            assertSession(statement);

            final SyncStmtOption option;
            option = replaceIfNeed(unsafeOption);

            final R result;
            if (statement instanceof InsertStatement) {
                result = executeInsert((InsertStatement) statement, option, resultClass);
            } else {
                result = executeUpdate(statement, option, resultClass);
            }
            return result;
        } catch (ChildUpdateException e) {
            rollbackOnlyOnError(e);
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).close();
            }
        }
    }


    /**
     * @param option the instance is returned by {@link #replaceIfNeed(SyncStmtOption)}
     * @see #updateAsResult(SimpleDmlStatement, SyncStmtOption, Class)
     */
    private <R> R executeInsert(InsertStatement statement, SyncStmtOption option, Class<R> resultClass)
            throws ArmyException {
        final Stmt stmt;
        stmt = this.parseInsertStatement(statement);

        final R states;

        if (stmt instanceof SimpleStmt) {
            states = this.stmtExecutor.insert((SimpleStmt) stmt, option, resultClass);
        } else if (!(stmt instanceof PairStmt)) {
            throw _Exceptions.unexpectedStmt(stmt);
        } else if (inTransaction()) {
            final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_Insert) statement).table();
            final PairStmt pairStmt = (PairStmt) stmt;

            final R parentStates;
            parentStates = this.stmtExecutor.insert(pairStmt.firstStmt(), option, resultClass);

            try {
                states = this.stmtExecutor.insert(pairStmt.secondStmt(), option, resultClass);
            } catch (Throwable e) {
                throw _Exceptions.childInsertError(this, domainTable, e);
            }

            final long childRows, parentRows;
            if (resultClass == Long.class) {
                childRows = (Long) states;
                parentRows = (Long) parentStates;
            } else {
                childRows = ((ResultStates) states).affectedRows();
                parentRows = ((ResultStates) parentStates).affectedRows();
            }
            if (childRows != parentRows) {
                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, parentRows, childRows);
            }
        } else {
            throw updateChildNoTransaction();
        }
        return states;
    }

    /**
     * @param option the instance is returned by {@link #replaceIfNeed(SyncStmtOption)}
     * @see #updateAsResult(SimpleDmlStatement, SyncStmtOption, Class)
     */
    private <R> R executeUpdate(SimpleDmlStatement statement, SyncStmtOption option, final Class<R> resultClass)
            throws ArmyException {

        final Stmt stmt;
        stmt = parseDmlStatement(statement, option);

        final R result;
        if (stmt instanceof SimpleStmt) {
            result = this.stmtExecutor.update((SimpleStmt) stmt, option, resultClass, Option.EMPTY_FUNC);

            if (stmt.hasOptimistic() && obtainAffectedRows(result) == 0) {
                throw _Exceptions.optimisticLock();
            }
        } else if (!(stmt instanceof PairStmt)) {
            throw _Exceptions.unexpectedStmt(stmt);
        } else if (inTransaction()) {
            final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_SingleUpdate._ChildUpdate) statement).table();
            final PairStmt pairStmt = (PairStmt) stmt;

            final R childResult;
            childResult = this.stmtExecutor.update(pairStmt.firstStmt(), option, resultClass, Option.EMPTY_FUNC);

            if (stmt.hasOptimistic() && obtainAffectedRows(childResult) == 0) {
                throw _Exceptions.optimisticLock();
            }
            try {
                result = this.stmtExecutor.update(pairStmt.secondStmt(), option, resultClass, Option.EMPTY_FUNC);
            } catch (Throwable e) {
                throw _Exceptions.childUpdateError(this, domainTable, e);
            }

            if (obtainAffectedRows(result) != obtainAffectedRows(childResult)) {
                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, obtainAffectedRows(result),
                        obtainAffectedRows(childResult));
            }

        } else {
            throw updateChildNoTransaction();
        }
        return result;
    }




    /*-------------------below private static method -------------------*/

    @Nullable
    private static <R> R onlyRow(final List<R> resultList) {
        final R result;
        switch (resultList.size()) {
            case 1:
                result = resultList.get(0);
                break;
            case 0:
                result = null;
                break;
            default:
                throw _Exceptions.nonUnique(resultList);
        }
        return result;
    }

    private static long obtainAffectedRows(final Object result) {
        if (result instanceof Long) {
            return (Long) result;
        }
        return ((ResultStates) result).affectedRows();
    }


}
