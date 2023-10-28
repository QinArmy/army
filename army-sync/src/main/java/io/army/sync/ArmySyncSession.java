package io.army.sync;

import io.army.ArmyException;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.session.*;
import io.army.stmt.*;
import io.army.sync.executor.SyncStmtExecutor;
import io.army.util.ArmyCriteria;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link ArmySyncLocalSession}</li>
 *     <li>{@link ArmySyncRmSession}</li>
 * </ul>
 * <p>This class extends {@link _ArmySession} and implements of {@link SyncSession}.
 *
 * @since 1.0
 */
abstract class ArmySyncSession extends _ArmySession implements SyncSession {

    private static final AtomicIntegerFieldUpdater<ArmySyncSession> SESSION_CLOSED =
            AtomicIntegerFieldUpdater.newUpdater(ArmySyncSession.class, "sessionClosed");


    final SyncStmtExecutor stmtExecutor;

    private volatile int sessionClosed;

    protected ArmySyncSession(ArmySyncSessionFactory.SyncSessionBuilder<?, ?> builder) {
        super(builder);
        this.stmtExecutor = builder.stmtExecutor;
        assert this.stmtExecutor != null;
    }

    @Override
    public final boolean isReactiveSession() {
        // always false
        return false;
    }

    @Override
    public final boolean isSyncSession() {
        // always true
        return true;
    }

    @Override
    public final boolean inTransaction() {
        if (isClosed()) {
            throw _Exceptions.sessionClosed(this);
        }
        if (((ArmySyncLocalSessionFactory) this.factory).jdbcDriver) {
            return hasTransaction();
        }
        return this.stmtExecutor.inTransaction();
    }


    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass) {
        return onlyRow(this.query(statement, resultClass, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return onlyRow(this.query(statement, resultClass, _Collections::arrayList, option));
    }

    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return onlyRow(this.queryObject(statement, constructor, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return onlyRow(this.queryObject(statement, constructor, _Collections::arrayList, option));
    }

    @Override
    public final <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return onlyRow(this.queryRecord(statement, function, _Collections::arrayList, defaultOption()));
    }

    @Override
    public final <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return onlyRow(this.queryRecord(statement, function, _Collections::arrayList, option));
    }

    @Override
    public final <R> List<R> query(DqlStatement statement, Class<R> resultClass) {
        return this.query(statement, resultClass, _Collections::arrayList, defaultOption());
    }

    @Override
    public final <R> List<R> query(DqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return this.query(statement, resultClass, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> query(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return this.query(statement, resultClass, listConstructor, defaultOption());
    }

    @Override
    public final <R> List<R> query(DqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        return this.executeQueryList(statement, listConstructor, option, s -> this.stmtExecutor.query(s, resultClass, listConstructor, option));
    }

    @Override
    public final <R> List<R> queryObject(DqlStatement statement, Supplier<R> constructor) {
        return this.queryObject(statement, constructor, _Collections::arrayList, defaultOption());
    }

    @Override
    public final <R> List<R> queryObject(DqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return this.queryObject(statement, constructor, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryObject(DqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor) {
        return this.queryObject(statement, constructor, listConstructor, defaultOption());
    }

    @Override
    public final <R> List<R> queryObject(DqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        return this.executeQueryList(statement, listConstructor, option, s -> this.stmtExecutor.queryObject(s, constructor, listConstructor, option));
    }

    @Override
    public final <R> List<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecord(statement, function, _Collections::arrayList, defaultOption());
    }

    @Override
    public final <R> List<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return this.queryRecord(statement, function, _Collections::arrayList, option);
    }

    @Override
    public final <R> List<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor) {
        return this.queryRecord(statement, function, listConstructor, defaultOption());
    }

    @Override
    public final <R> List<R> queryRecord(DqlStatement statement, Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor, SyncStmtOption option) {
        return this.executeQueryList(statement, listConstructor, option, s -> this.stmtExecutor.queryRecord(s, function, listConstructor, option));
    }

    @Override
    public final <R> Stream<R> queryStream(DqlStatement statement, Class<R> resultClass) {
        return this.queryStream(statement, resultClass, defaultOption());
    }

    @Override
    public final <R> Stream<R> queryStream(DqlStatement statement, Class<R> resultClass, SyncStmtOption option) {
        return this.executeQueryStream(statement, option, s -> this.stmtExecutor.queryStream(s, resultClass, option));
    }

    @Override
    public final <R> Stream<R> queryObjectStream(DqlStatement statement, Supplier<R> constructor) {
        return this.queryObjectStream(statement, constructor, defaultOption());
    }

    @Override
    public final <R> Stream<R> queryObjectStream(DqlStatement statement, Supplier<R> constructor, SyncStmtOption option) {
        return this.executeQueryStream(statement, option, s -> this.stmtExecutor.queryObjectStream(s, constructor, option));
    }

    @Override
    public final <R> Stream<R> queryRecordStream(DqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecordStream(statement, function, defaultOption());
    }

    @Override
    public final <R> Stream<R> queryRecordStream(DqlStatement statement, Function<CurrentRecord, R> function, SyncStmtOption option) {
        return this.executeQueryStream(statement, option, s -> this.stmtExecutor.queryRecordStream(s, function, option));
    }

    @Override
    public final <T> long save(T domain) {
        return this.update(ArmyCriteria.insertStmt(this, domain), defaultOption());
    }

    @Override
    public final <T> long save(T domain, SyncStmtOption option) {
        return this.update(ArmyCriteria.insertStmt(this, domain), option);
    }

    @Override
    public final long update(SimpleDmlStatement statement) {
        return this.update(statement, defaultOption());
    }

    @Override
    public final long update(SimpleDmlStatement statement, SyncStmtOption option) {
        try {
            if (statement instanceof _BatchStatement) {
                throw _Exceptions.unexpectedStatement(statement);
            }

            assertSession(statement);

            final long rows;
            if (statement instanceof InsertStatement) {
                rows = this.executeInsert((InsertStatement) statement, option);
            } else {
                rows = this.executeUpdate(statement, option);
            }
            return rows;
        } catch (ChildUpdateException e) {
            if (hasTransaction()) {
                markRollbackOnly();
            }
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
    public final <T> long batchSave(List<T> domainList) {
        return this.update(ArmyCriteria.batchInsertStmt(this, domainList), defaultOption());
    }

    @Override
    public final <T> long batchSave(List<T> domainList, SyncStmtOption option) {
        return this.update(ArmyCriteria.batchInsertStmt(this, domainList), option);
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
                resultList = this.stmtExecutor.batchUpdate((BatchStmt) stmt, listConstructor, option, domainTable, null);
            } else if (!(stmt instanceof PairBatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (!this.inTransaction()) {
                throw updateChildNoTransaction();
            } else {
                assert domainTable instanceof ChildTableMeta; // fail, bug.
                final PairBatchStmt pairStmt = (PairBatchStmt) stmt;

                final List<Long> childList;
                childList = this.stmtExecutor.batchUpdate(pairStmt.firstStmt(), listConstructor, option, domainTable, null);
                resultList = this.stmtExecutor.batchUpdate(pairStmt.secondStmt(), listConstructor, option, domainTable, childList);
            }
            return resultList;
        } catch (ChildUpdateException e) {
            if (hasTransaction()) {
                markRollbackOnly();
            }
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
        return null;
    }

    @Override
    public final boolean isClosed() {
        return this.sessionClosed != 0;
    }

    @Override
    public final void close() throws SessionException {
        if (SESSION_CLOSED.compareAndSet(this, 0, 1)) {
            this.stmtExecutor.close();
        }
    }


    /*-------------------below private methods -------------------*/

    /**
     * @see #query(DqlStatement, Class, Supplier, SyncStmtOption)
     * @see #queryObject(DqlStatement, Supplier, Supplier, SyncStmtOption)
     * @see #queryRecord(DqlStatement, Function, Supplier, SyncStmtOption)
     */
    private <R> List<R> executeQueryList(final DqlStatement statement, final Supplier<List<R>> listConstructor,
                                         final SyncStmtOption option, final Function<SimpleStmt, List<R>> exeFunc) {
        try {

            assertSession(statement);

            final Stmt stmt;
            stmt = this.parseDqlStatement(statement, option);

            final List<R> resultList;
            if (stmt instanceof SimpleStmt) {
                resultList = exeFunc.apply((SimpleStmt) stmt);
            } else if (!(stmt instanceof PairStmt)) {
                // no bug,never here
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (statement instanceof InsertStatement) {
                resultList = executePairInsertQueryList((InsertStatement) statement, listConstructor, option, (PairStmt) stmt, exeFunc);
            } else {
                //TODO add DmlStatement code for firebird
                // no bug,never here
                throw _Exceptions.unexpectedStatement(statement);
            }
            return resultList;
        } catch (ChildUpdateException e) {
            if (hasTransaction()) {
                markRollbackOnly();
            }
            throw e;
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).close();
            }
        }
    }


    private <R> Stream<R> executeQueryStream(final DqlStatement statement, final SyncStmtOption option,
                                             final Function<SimpleStmt, Stream<R>> exeFunc) {
        try {
            assertSession(statement);

            final Stmt stmt;
            stmt = this.parseDqlStatement(statement, option);
            if (!(stmt instanceof SimpleStmt)) {
                throw new SessionException("army stream api don't support child pair statement.");
            }
            return exeFunc.apply((SimpleStmt) stmt);
        } catch (Exception e) {
            throw wrapSessionError(e);
        } finally {
            if (statement instanceof _Statement) {
                ((_Statement) statement).close();
            }
        }

    }

    /**
     * @see #executeQueryList(DqlStatement, Supplier, SyncStmtOption, Function)
     */
    private <R> List<R> executePairInsertQueryList(InsertStatement statement, Supplier<List<R>> listConstructor,
                                                   SyncStmtOption option, PairStmt stmt,
                                                   Function<SimpleStmt, List<R>> exeFunc) {
        final _Insert._ChildInsert childInsert = (_Insert._ChildInsert) statement;
        final boolean firstStmtIsQuery = childInsert.parentStmt() instanceof _ReturningDml;

        long rows = 0;
        List<R> resultList = null;
        if (firstStmtIsQuery) {
            resultList = exeFunc.apply(stmt.firstStmt());
            if (resultList.size() == 0) {
                // exists conflict clause
                return resultList;
            }
        } else {
            rows = this.stmtExecutor.insert(stmt.firstStmt(), option);
            if (rows == 0) {
                // exists conflict clause
                resultList = listConstructor.get();
                if (resultList == null) {
                    throw _Exceptions.listConstructorError();
                }
                return resultList;
            }
        }

        final ChildTableMeta<?> childTable;
        childTable = (ChildTableMeta<?>) childInsert.table();

        try {
            if (firstStmtIsQuery) {
                rows = this.stmtExecutor.secondQuery((TwoStmtQueryStmt) stmt.secondStmt(), option, resultList);
            } else {
                resultList = exeFunc.apply(stmt.secondStmt());
            }
        } catch (Throwable e) {
            throw _Exceptions.childInsertError(this, childTable, e);
        }

        if (rows != resultList.size()) {
            if (firstStmtIsQuery) {
                throw _Exceptions.parentChildRowsNotMatch(this, childTable, resultList.size(), rows);
            } else {
                throw _Exceptions.parentChildRowsNotMatch(this, childTable, rows, resultList.size());
            }
        }

        if (firstStmtIsQuery && _Collections.isSecondQueryList(resultList)) {
            final List<R> tempList = listConstructor.get();
            if (tempList == null) {
                throw _Exceptions.listConstructorError();
            }
            tempList.addAll(resultList);
            resultList = tempList;
        }

        return resultList;
    }


    /**
     * @see #update(SimpleDmlStatement, SyncStmtOption)
     */
    private long executeInsert(InsertStatement statement, SyncStmtOption option) throws ArmyException {
        final Stmt stmt;
        stmt = this.parseInsertStatement(statement);

        final long affectedRows;

        if (stmt instanceof SimpleStmt) {
            affectedRows = this.stmtExecutor.insert((SimpleStmt) statement, option);
        } else if (!(stmt instanceof PairStmt)) {
            throw _Exceptions.unexpectedStmt(stmt);
        } else {
            final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_Insert) statement).table();
            final PairStmt pairStmt = (PairStmt) stmt;

            final long parentRows;
            parentRows = this.stmtExecutor.insert(pairStmt.firstStmt(), option);

            try {
                affectedRows = this.stmtExecutor.insert(pairStmt.secondStmt(), option);
            } catch (Throwable e) {
                throw _Exceptions.childInsertError(this, domainTable, e);
            }
            if (affectedRows != parentRows) {
                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, parentRows, affectedRows);
            }
        }
        return affectedRows;
    }

    private long executeUpdate(SimpleDmlStatement statement, SyncStmtOption option) throws ArmyException {
        final Stmt stmt;
        stmt = parseDmlStatement(statement, option);

        final long affectedRows;

        if (stmt instanceof SimpleStmt) {
            affectedRows = this.stmtExecutor.update((SimpleStmt) stmt, option);
        } else if (!(stmt instanceof PairStmt)) {
            throw _Exceptions.unexpectedStmt(stmt);
        } else {
            final ChildTableMeta<?> domainTable = (ChildTableMeta<?>) ((_SingleUpdate._ChildUpdate) statement).table();
            final PairStmt pairStmt = (PairStmt) stmt;

            final long childRows;
            childRows = this.stmtExecutor.update(pairStmt.firstStmt(), option);

            try {
                affectedRows = this.stmtExecutor.update(pairStmt.secondStmt(), option);
            } catch (Throwable e) {
                throw _Exceptions.childUpdateError(this, domainTable, e);
            }

            if (affectedRows != childRows) {
                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, affectedRows, childRows);
            }

        }
        return affectedRows;
    }


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


    private SyncStmtOption defaultOption() {
        throw new UnsupportedOperationException();
    }


}
