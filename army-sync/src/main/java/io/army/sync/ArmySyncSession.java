/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.util.*;
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
 * @see ArmySyncSessionFactory
 * @since 0.6.0
 */
abstract class ArmySyncSession extends _ArmySession<ArmySyncSessionFactory> implements SyncSession {


    final SyncExecutor executor;

    private boolean sessionClosed;

    private Map<Object, Object> attributeMap;

    ArmySyncSession(ArmySyncSessionFactory.SyncBuilder<?, ?> builder) {
        super(builder);
        this.executor = builder.stmtExecutor;
        assert this.executor != null;
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
        if (this.factory.sessionIdentifierEnable) {
            return this.executor.sessionIdentifier();
        }
        return 0L;
    }

    @Override
    public final boolean inTransaction() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }

        if (this.factory.jdbcDriver || !(this instanceof DriverSpiHolder)) {
            // JDBC don't support to get the state from database client protocol.
            final TransactionInfo info = obtainTransactionInfo();
            return info != null && info.inTransaction();
        }
        // due to session open driver spi to application developer, TransactionInfo probably error.
        return this.executor.inTransaction();
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
            return this.executor.transactionInfo();
        } catch (Exception e) {
            throw _ArmySession.wrapSessionError(e);
        }
    }

    @Override
    public final TransactionInfo sessionTransactionCharacteristics() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        try {
            return this.executor.sessionTransactionCharacteristics(Option.EMPTY_FUNC);
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
            return this.executor.setSavePoint(optionFunc);
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
                this.executor.releaseSavePoint(savepoint, optionFunc);
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
                this.executor.rollbackToSavePoint(savepoint, optionFunc);
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
        this.executor.setTransactionCharacteristics(option);
    }

    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass) {
        return onlyOneRow(this.queryList(statement, resultClass, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return onlyOneRow(this.queryList(statement, resultClass, _Collections::arrayList, option));
    }

    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return onlyOneRow(this.queryObjectList(statement, constructor, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return onlyOneRow(this.queryObjectList(statement, constructor, _Collections::arrayList, option));
    }

    @Override
    public final <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return onlyOneRow(this.queryRecordList(statement, function, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return onlyOneRow(this.queryRecordList(statement, function, _Collections::arrayList, option));
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
        return this.executeQuery(statement, option, (s, o) -> this.executor.query(s, resultClass, o)); // here ,use o not option
    }

    @Override
    public final <R> Stream<R> queryObject(DqlStatement statement, Supplier<R> constructor) {
        return this.queryObject(statement, constructor, defaultOption());
    }

    @Override
    public final <R> Stream<R> queryObject(DqlStatement statement, final Supplier<R> constructor, SyncStmtOption option) {
        return this.executeQuery(statement, option, (s, o) -> this.executor.queryObject(s, constructor, o)); // here ,use o not option
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
        return this.executeQuery(statement, option, (s, o) -> this.executor.queryRecord(s, function, o)); // here ,use o not option
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
        return this.batchUpdate(statement, _Collections::arrayList, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, SyncStmtOption option) {
        return this.batchUpdate(statement, _Collections::arrayList, option);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor) {
        return this.batchUpdate(statement, listConstructor, ArmySyncStmtOptions.DEFAULT);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, SyncStmtOption option) {
        final List<Long> resultList;
        if (this.factory.resultItemDriverSpi) {
            if (!(statement instanceof _BatchStatement)) {
                throw _ArmySession.wrapSessionError(_Exceptions.unexpectedStatement(statement));
            }

            final int batchSize;
            batchSize = ((_BatchStatement) statement).paramList().size();

            // and execute
            try (Stream<ResultStates> stream = batchUpdateAsStates(statement, option)) {

                final List<Long> tempList;
                tempList = stream.map(ResultStates::affectedRows)
                        .collect(Collectors.toCollection(() -> listConstructor.apply(batchSize)));

                resultList = Collections.unmodifiableList(tempList);
            } catch (Exception e) {
                throw _ArmySession.wrapSessionError(e);
            }
        } else {
            // JDBC driver here
            resultList = executeBatchUpdateAsLong(statement, listConstructor, option);
        }
        return resultList;
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

            final Consumer<ResultStates> optimisticLockValidator;
            if (stmt.hasOptimistic()) {
                optimisticLockValidator = statesOptimisticLockValidator(domainTable);
            } else {
                optimisticLockValidator = null;
            }

            final Stream<ResultStates> stream, tempSteam;
            if (stmt instanceof BatchStmt) {
                tempSteam = this.executor.batchUpdate((BatchStmt) stmt, option, Option.EMPTY_FUNC);
                if (optimisticLockValidator == null) {
                    stream = tempSteam;
                } else {
                    stream = tempSteam.peek(optimisticLockValidator);
                }
            } else if (!(stmt instanceof PairBatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (!inTransaction()) {
                throw updateChildNoTransaction();
            } else {
                assert domainTable instanceof ChildTableMeta; // fail, bug.
                final PairBatchStmt pairStmt = (PairBatchStmt) stmt;

                tempSteam = this.executor.batchUpdate(pairStmt.firstStmt(), option, Option.EMPTY_FUNC);

                final List<ResultStates> childList;
                if (optimisticLockValidator == null) {
                    childList = tempSteam.collect(Collectors.toCollection(_Collections::arrayList));
                } else {
                    childList = tempSteam.peek(optimisticLockValidator)
                            .collect(Collectors.toCollection(_Collections::arrayList));
                }

                stream = this.executor.batchUpdate(pairStmt.secondStmt(), option, Option.EMPTY_FUNC)
                        .peek(batchUpdateStatesValidateParentRows(childList, (ChildTableMeta<?>) domainTable));
            }
            return stream;
        } catch (ChildUpdateException e) {
            rollbackOnlyOnError(e);
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
    }

    @Override
    public final <T> T valueOf(Option<T> option) {
        return this.executor.valueOf(option);
    }

    @Override
    public final Set<Option<?>> optionSet() {
        return this.executor.optionSet();
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
                this.executor.close();
            } catch (Exception e) {
                throw wrapSessionError(e);
            }
        }

    }

    /*-------------------below protected template methods -------------------*/

    @Nullable
    @Override
    protected final Map<Object, Object> obtainAttributeMap() {
        return this.attributeMap;
    }

    @Override
    protected final Map<Object, Object> obtainOrCreateAttributeMap() {
        Map<Object, Object> map = this.attributeMap;
        if (map == null) {
            this.attributeMap = map = _Collections.hashMap();
        }
        return map;
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

    private SyncStmtOption replaceForQueryIfNeed(final boolean hasOptimistic,
                                                 final SyncStmtOption option) {
        final TransactionInfo info;

        final SyncStmtOption newOption;
        if (hasOptimistic) {
            newOption = ArmySyncStmtOptions.overrideOptionWithOptimisticLockIfNeed(option, OPTIMISTIC_LOCK_VALIDATOR,
                    obtainTransactionInfo());
        } else if (option instanceof ArmySyncStmtOptions.TransactionOverrideOption
                || (info = obtainTransactionInfo()) == null) {
            newOption = option;
        } else {
            newOption = ArmySyncStmtOptions.overrideOptionIfNeed(option, info);
        }
        return newOption;
    }


    private <R> Stream<R> executeQuery(final DqlStatement statement, final SyncStmtOption optionOfUser,
                                       final BiFunction<SingleSqlStmt, SyncStmtOption, Stream<R>> exeFunc) {
        try {
            assertSession(statement);

            final Stmt stmt;
            stmt = parseDqlStatement(statement, optionOfUser);

            final SyncStmtOption option;
            option = replaceForQueryIfNeed(stmt.hasOptimistic(), optionOfUser); // for transaction timeout and optimistic lock

            final Stream<R> stream;
            if (stmt instanceof SimpleStmt) {
                stream = exeFunc.apply((SimpleStmt) stmt, option);
            } else if (stmt instanceof BatchStmt) { // batch SELECT or postgre batch dml with RETURNING clause
                stream = exeFunc.apply((BatchStmt) stmt, option);
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
                ((_Statement) statement).clear();
            }
        }

    }

    /**
     * @param option the instance is returned by {@link #replaceIfNeed(SyncStmtOption)}
     * @see #executeQuery(DqlStatement, SyncStmtOption, BiFunction)
     */
    private <R> Stream<R> executePairInsertQuery(InsertStatement statement,
                                                 final SyncStmtOption option, PairStmt stmt,
                                                 BiFunction<SingleSqlStmt, SyncStmtOption, Stream<R>> exeFunc) {
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
            rows = this.executor.insert(stmt.firstStmt(), option, Long.class);
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
                stream = this.executor.secondQuery((TwoStmtQueryStmt) stmt.secondStmt(), option, resultList);
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
                ((_Statement) statement).clear();
            }
        }
    }


    /**
     * @param userOption the instance is returned by {@link #replaceIfNeed(SyncStmtOption)}
     * @see #updateAsResult(SimpleDmlStatement, SyncStmtOption, Class)
     */
    private <R> R executeInsert(InsertStatement statement, SyncStmtOption userOption, Class<R> resultClass)
            throws ArmyException {

        final Stmt stmt;
        stmt = this.parseInsertStatement(statement);

        final SyncStmtOption option;
        option = replaceIfNeed(userOption);

        final long executionStartNanoSecond;
        if (this.factory.sqlExecutionCostTime) {
            executionStartNanoSecond = System.nanoTime();
        } else {
            executionStartNanoSecond = -1L;
        }


        final R states;

        if (stmt instanceof SimpleStmt) {
            states = this.executor.insert((SimpleStmt) stmt, option, resultClass);
        } else if (!(stmt instanceof PairStmt)) {
            throw _Exceptions.unexpectedStmt(stmt);
        } else if (inTransaction()) {
            final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_Insert) statement).table();
            final PairStmt pairStmt = (PairStmt) stmt;

            final R parentStates;
            parentStates = this.executor.insert(pairStmt.firstStmt(), option, resultClass);

            try {
                states = this.executor.insert(pairStmt.secondStmt(), option, resultClass);
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
            if (childRows != parentRows
                    && !(statement instanceof _Insert._SupportConflictClauseSpec
                    && ((_Insert._SupportConflictClauseSpec) statement).hasConflictAction())) {
                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, parentRows, childRows);
            }
        } else {
            throw updateChildNoTransaction();
        }

        if (executionStartNanoSecond > 0L) {
            printExecutionCostTimeLog(getLogger(), stmt, executionStartNanoSecond);
        }
        return states;
    }


    /**
     * @param userOption the instance is returned by {@link #replaceIfNeed(SyncStmtOption)}
     * @see #updateAsResult(SimpleDmlStatement, SyncStmtOption, Class)
     */
    private <R> R executeUpdate(SimpleDmlStatement statement, SyncStmtOption userOption, final Class<R> resultClass)
            throws ArmyException {

        final SyncStmtOption option;
        option = replaceIfNeed(userOption);

        final Stmt stmt;
        stmt = parseDmlStatement(statement, option);

        final long executionStartNanoSecond;
        if (this.factory.sqlExecutionCostTime) {
            executionStartNanoSecond = System.nanoTime();
        } else {
            executionStartNanoSecond = -1L;
        }

        final R result;
        if (stmt instanceof SimpleStmt) {
            result = this.executor.update((SimpleStmt) stmt, option, resultClass, Option.EMPTY_FUNC);

            if (stmt.hasOptimistic() && obtainAffectedRows(result) == 0) {
                throw _Exceptions.optimisticLock();
            }
        } else if (!(stmt instanceof PairStmt)) {
            throw _Exceptions.unexpectedStmt(stmt);
        } else if (inTransaction()) {
            final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_SingleUpdate._ChildUpdate) statement).table();
            final PairStmt pairStmt = (PairStmt) stmt;

            final R childResult;
            childResult = this.executor.update(pairStmt.firstStmt(), option, resultClass, Option.EMPTY_FUNC);

            if (stmt.hasOptimistic() && obtainAffectedRows(childResult) == 0) {
                throw _Exceptions.optimisticLock();
            }
            try {
                result = this.executor.update(pairStmt.secondStmt(), option, resultClass, Option.EMPTY_FUNC);
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

        if (executionStartNanoSecond > 0L) {
            printExecutionCostTimeLog(getLogger(), stmt, executionStartNanoSecond);
        }
        return result;
    }


    /**
     * @see #batchUpdate(BatchDmlStatement, IntFunction, SyncStmtOption)
     */
    private List<Long> executeBatchUpdateAsLong(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, SyncStmtOption option) {
        try {

            Objects.requireNonNull(listConstructor);

            if (!(statement instanceof _BatchStatement)) {
                throw _Exceptions.unexpectedStatement(statement);
            }

            assertSession(statement);

            final Stmt stmt;
            stmt = this.parseDmlStatement(statement, option);

            final TableMeta<?> domainTable;
            domainTable = getBatchUpdateDomainTable(statement);

            final LongConsumer optimisticLockValidator;
            if (stmt.hasOptimistic()) {
                optimisticLockValidator = rowsOptimisticLockValidator(domainTable);
            } else {
                optimisticLockValidator = null;
            }

            final List<Long> resultList;
            if (stmt instanceof BatchStmt) {
                resultList = this.executor.batchUpdateList((BatchStmt) stmt, listConstructor, option, optimisticLockValidator, Option.EMPTY_FUNC);
            } else if (!(stmt instanceof PairBatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (!inTransaction()) {
                throw updateChildNoTransaction();
            } else {
                assert domainTable instanceof ChildTableMeta; // fail, bug.
                final PairBatchStmt pairStmt = (PairBatchStmt) stmt;

                resultList = this.executor.batchUpdateList(pairStmt.firstStmt(), listConstructor, option, optimisticLockValidator, Option.EMPTY_FUNC);

                final LongConsumer parentValidator;
                parentValidator = batchUpdateValidateParentRows(resultList, (ChildTableMeta<?>) domainTable);
                this.executor.batchUpdateList(pairStmt.secondStmt(), null, option, parentValidator, Option.EMPTY_FUNC);
            }
            return resultList;
        } catch (ChildUpdateException e) {
            rollbackOnlyOnError(e);
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).clear();
            }
        }
    }


    /*-------------------below private static method -------------------*/

    @Nullable
    private static <R> R onlyOneRow(final List<R> resultList) {
        final R result;
        switch (resultList.size()) {
            case 1:
                result = resultList.get(0);
                break;
            case 0:
                result = null;
                break;
            default:
                throw _Exceptions.nonSingleRow(resultList);
        }
        return result;
    }

    private static long obtainAffectedRows(final Object result) {
        if (result instanceof Long) {
            return (Long) result;
        }
        return ((ResultStates) result).affectedRows();
    }


    /**
     * @see #batchUpdate(BatchDmlStatement, IntFunction, SyncStmtOption)
     */
    private static LongConsumer rowsOptimisticLockValidator(final @Nullable TableMeta<?> domainTable) {
        final int[] indexHolder = new int[]{0};
        return rows -> {
            final int index = indexHolder[0]++;
            if (rows == 0L) {
                throw _Exceptions.batchOptimisticLock(domainTable, index + 1, rows);
            }
        };
    }


    /**
     * @see #batchUpdate(BatchDmlStatement, IntFunction, SyncStmtOption)
     */
    private static LongConsumer batchUpdateValidateParentRows(final List<Long> childList, final ChildTableMeta<?> domainTable) {
        final int batchSize = childList.size();

        final int[] indexHolder = new int[]{0};
        return parentRows -> {
            final int index = indexHolder[0]++;
            if (index >= batchSize) {
                throw _Exceptions.childBatchSizeError(domainTable, batchSize, index + 1);
            }
            if (parentRows != childList.get(index)) {
                throw _Exceptions.batchChildUpdateRowsError(domainTable, index + 1, childList.get(index),
                        parentRows);
            }

        };

    }

    /**
     * @see #batchUpdateAsStates(BatchDmlStatement, SyncStmtOption)
     */
    private static Consumer<ResultStates> statesOptimisticLockValidator(final @Nullable TableMeta<?> domainTable) {
        return states -> {
            if (states.affectedRows() == 0L) {
                throw _Exceptions.batchOptimisticLock(domainTable, states.getResultNo(), states.affectedRows());
            }
        };
    }

    /**
     * @see #batchUpdateAsStates(BatchDmlStatement, SyncStmtOption)
     */
    private static Consumer<ResultStates> batchUpdateStatesValidateParentRows(final List<ResultStates> childList, final ChildTableMeta<?> domainTable) {
        final int batchSize = childList.size();

        return parentStates -> {
            final int batchNo = parentStates.getResultNo();
            if (batchNo > batchSize || !parentStates.hasMoreResult()) {
                throw _Exceptions.childBatchSizeError(domainTable, batchSize, batchNo);
            }
            if (parentStates.affectedRows() != childList.get(batchNo - 1).affectedRows()) {
                throw _Exceptions.batchChildUpdateRowsError(domainTable, batchNo, childList.get(batchNo - 1).affectedRows(),
                        parentStates.affectedRows());
            }

        };

    }


}
