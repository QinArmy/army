package io.army.sync;

import io.army.ArmyException;
import io.army.criteria.*;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.proxy._SessionCache;
import io.army.session.*;
import io.army.stmt.*;
import io.army.sync.executor.StmtExecutor;
import io.army.tx.CannotCreateTransactionException;
import io.army.tx.Isolation;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.TransactionOptions;
import io.army.type.ImmutableSpec;
import io.army.util._Collections;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

final class ArmySyncLocalSession extends ArmySyncSession implements SyncLocalSession {

    private static final Logger LOG = LoggerFactory.getLogger(ArmySyncLocalSession.class);

    final SyncLocalSessionFactory factory;

    final StmtExecutor stmtExecutor;

    private final _SessionCache sessionCache;

    private SyncLocalTransaction transaction;


    private boolean closed;

    ArmySyncLocalSession(final SyncLocalSessionFactory.LocalSessionBuilder builder) {
        super(builder);
        this.factory = builder.factory;
        this.stmtExecutor = builder.stmtExecutor;
        assert this.stmtExecutor != null;
        this.sessionCache = this.factory.sessionCacheFactory.createCache();
    }


    @Override
    public <T> T valueOf(Option<T> option) {
        return null;
    }

    @Override
    public LocalSessionFactory sessionFactory() {
        return this.factory;
    }

    @Override
    public boolean isReadOnlyStatus() {
        final LocalTransaction tx = this.transaction;
        return this.readonly || (tx != null && tx.readOnly());
    }


    @Override
    public <R> List<R> query(final SimpleDqlStatement statement, final @Nullable Class<R> resultClass,
                             final Supplier<List<R>> listConstructor, final Visible visible) {
        if (resultClass == null) {
            throw new NullPointerException();
        }

        final QueryFunction<List<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.query(stmt, timeout, resultClass, listConstructor);
        return this.queryList(statement, queryFunc, visible);
    }


    @Override
    public <R> List<R> queryObject(final SimpleDqlStatement statement, final @Nullable Supplier<R> constructor,
                                   final Supplier<List<R>> listConstructor, final Visible visible) {
        if (constructor == null) {
            throw new NullPointerException();
        }
        final QueryFunction<List<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.queryObject(stmt, timeout, constructor, listConstructor);
        return this.queryList(statement, queryFunc, visible);
    }

    @Override
    public <R> List<R> queryRecord(final SimpleDqlStatement statement, final @Nullable Function<CurrentRecord, R> function,
                                   final Supplier<List<R>> listConstructor, final Visible visible) {
        if (function == null) {
            throw new NullPointerException();
        }
        final QueryFunction<List<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.queryRecord(stmt, timeout, function, listConstructor);
        return this.queryList(statement, queryFunc, visible);
    }


    @Override
    public <R> Stream<R> queryStream(final SimpleDqlStatement statement, final @Nullable Class<R> resultClass,
                                     final StreamOptions options, final Visible visible) {
        if (resultClass == null) {
            throw new NullPointerException();
        }
        final QueryFunction<Stream<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.queryStream(stmt, timeout, resultClass, options);
        return this.queryAsStream(statement, queryFunc, visible);
    }


    @Override
    public <R> Stream<R> queryObjectStream(final SimpleDqlStatement statement, final @Nullable Supplier<R> constructor,
                                           final StreamOptions options, final Visible visible) {
        if (constructor == null) {
            throw new NullPointerException();
        }
        final QueryFunction<Stream<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.queryObjectStream(stmt, timeout, constructor, options);
        return this.queryAsStream(statement, queryFunc, visible);
    }


    @Override
    public <R> Stream<R> queryRecardStream(SimpleDqlStatement statement,
                                           final @Nullable Function<CurrentRecord, R> function,
                                           final StreamOptions options, final Visible visible) {
        if (function == null) {
            throw new NullPointerException();
        }
        final QueryFunction<Stream<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.queryRecordStream(stmt, timeout, function, options);
        return this.queryAsStream(statement, queryFunc, visible);
    }


    @Override
    public <R> List<R> batchQuery(final BatchDqlStatement statement, final Class<R> resultClass,
                                  final @Nullable R terminator, final Supplier<List<R>> listConstructor,
                                  final boolean useMultiStmt, final Visible visible) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        } else if (terminator == Collections.EMPTY_MAP) {
            throw new IllegalArgumentException();
        }
        final BatchQueryFunction<List<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.batchQuery(stmt, timeout, resultClass, terminator,
                listConstructor, useMultiStmt
        );
        return batchQueryList(statement, queryFunc, useMultiStmt, visible);
    }


    @Override
    public <R> List<R> batchQueryObject(final BatchDqlStatement statement, final Supplier<R> constructor,
                                        final @Nullable R terminator, final Supplier<List<R>> listConstructor,
                                        final boolean useMultiStmt, final Visible visible) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        } else if (terminator == Collections.EMPTY_MAP) {
            throw new IllegalArgumentException();
        }
        final BatchQueryFunction<List<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.batchQueryObject(stmt, timeout, constructor, terminator,
                listConstructor, useMultiStmt
        );
        return batchQueryList(statement, queryFunc, useMultiStmt, visible);
    }


    @Override
    public <R> List<R> batchQueryRecord(final BatchDqlStatement statement, final Function<CurrentRecord, R> function,
                                        final @Nullable R terminator, final Supplier<List<R>> listConstructor,
                                        final boolean useMultiStmt, final Visible visible) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        } else if (terminator == Collections.EMPTY_MAP) {
            throw new IllegalArgumentException();
        }
        final BatchQueryFunction<List<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.batchQueryRecord(stmt, timeout, function, terminator,
                listConstructor, useMultiStmt
        );
        return batchQueryList(statement, queryFunc, useMultiStmt, visible);
    }

    @Override
    public <R> Stream<R> batchQueryStream(final BatchDqlStatement statement, final Class<R> resultClass,
                                          final @Nullable R terminator, final StreamOptions options,
                                          final boolean useMultiStmt, final Visible visible) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        } else if (terminator == Collections.EMPTY_MAP) {
            throw new IllegalArgumentException();
        }
        final BatchQueryFunction<Stream<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.batchQueryStream(stmt, timeout, resultClass, terminator,
                options, useMultiStmt
        );
        return batchQueryAsStream(statement, queryFunc, useMultiStmt, visible);
    }

    @Override
    public <R> Stream<R> batchQueryObjectStream(final BatchDqlStatement statement, final Supplier<R> constructor,
                                                final @Nullable R terminator, final StreamOptions options,
                                                final boolean useMultiStmt, final Visible visible) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        } else if (terminator == Collections.EMPTY_MAP) {
            throw new IllegalArgumentException();
        }
        final BatchQueryFunction<Stream<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.batchQueryObjectStream(stmt, timeout, constructor, terminator,
                options, useMultiStmt
        );
        return batchQueryAsStream(statement, queryFunc, useMultiStmt, visible);
    }

    @Override
    public <R> Stream<R> batchQueryRecordStream(final BatchDqlStatement statement,
                                                final Function<CurrentRecord, R> function,
                                                final @Nullable R terminator, final StreamOptions options,
                                                final boolean useMultiStmt, final Visible visible) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        } else if (terminator == Collections.EMPTY_MAP) {
            throw new IllegalArgumentException();
        }
        final BatchQueryFunction<Stream<R>> queryFunc;
        queryFunc = (stmt, timeout) -> this.stmtExecutor.batchQueryRecordStream(stmt, timeout, function, terminator,
                options, useMultiStmt
        );
        return batchQueryAsStream(statement, queryFunc, useMultiStmt, visible);
    }

    @Override
    public long update(final SimpleDmlStatement statement, final Visible visible) {
        if (statement instanceof _BatchStatement) {
            // no bug,never here
            throw _Exceptions.unexpectedStatement(statement);
        }
        final long affectedRows;
        if (statement instanceof InsertStatement) {
            affectedRows = this.insert((InsertStatement) statement, visible);
        } else {
            affectedRows = this.simpleUpdate(statement, visible);
        }
        return affectedRows;
    }


    @Override
    public List<Long> batchUpdate(final BatchDmlStatement statement, final IntFunction<List<Long>> listConstructor,
                                  final boolean useMultiStmt, final Visible visible) {
        if (!(statement instanceof _BatchStatement)) {
            // no bug,never here
            throw _Exceptions.unexpectedStatement(statement);
        }
        //1. assert session status
        assertSession(statement, visible);
        try {
            //2. parse statement to stmt
            final Stmt stmt;
            stmt = this.parseDmlStatement(statement, useMultiStmt, visible);

            //3. execute stmt
            final int timeout;
            timeout = this.getTxTimeout();
            final TableMeta<?> domainTable;
            domainTable = getBatchUpdateDomainTable(statement);
            final List<Long> resultList;
            if (stmt instanceof BatchStmt) {
                resultList = this.stmtExecutor.batchUpdate((BatchStmt) stmt, timeout, listConstructor, useMultiStmt,
                        domainTable, null
                );
            } else if (!(stmt instanceof PairBatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (!this.inTransaction()) {
                throw updateChildNoTransaction();
            } else {
                final long startTime;
                startTime = System.currentTimeMillis();
                // firstStmt update child, because army update child and update parent
                resultList = this.stmtExecutor.batchUpdate(((PairBatchStmt) stmt).firstStmt(), timeout,
                        listConstructor, useMultiStmt, domainTable, null
                );
                assert domainTable instanceof ChildTableMeta; // fail, bug.

                // secondStmt update parent, because army update child and update parent
                this.stmtExecutor.batchUpdate(((PairBatchStmt) stmt).secondStmt(),
                        restSeconds((ChildTableMeta<?>) domainTable, startTime, timeout), listConstructor,
                        useMultiStmt, domainTable, resultList
                );
            }

            return resultList;
        } catch (ChildUpdateException e) {
            final SyncLocalTransaction tx = this.transaction;
            if (tx != null) {
                tx.markRollbackOnly();
            }
            throw e;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", statement.getClass().getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    @Override
    public MultiResult multiStmt(final MultiResultStatement statement, final StreamOptions options,
                                 final Visible visible) {
        return null;
    }

    @Override
    public MultiStream multiStmtStream(final MultiResultStatement statement, final StreamOptions options,
                                       final Visible visible) {
        return null;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() throws SessionException {
        if (this.closed) {
            return;
        }
        try {
            final LocalTransaction tx = this.transaction;
            if (tx != null) {
                String m = String.format("%s %s not end.", this, tx);
                throw new TransactionNotCloseException(m);
            }
            this.stmtExecutor.close();
            this.sessionCache.clearOnSessionCLose();
            this.closed = true;
        } catch (SessionException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new SessionCloseFailureException(e, "session close connection error.");
        }
    }

    @Override
    public LocalTransaction currentTransaction() throws NoSessionTransactionException {
        final LocalTransaction transaction = this.transaction;
        if (transaction == null) {
            throw new NoSessionTransactionException("no session transaction.");
        }
        return transaction;
    }

    @Override
    public TransactionBuilder builder() {
        if (this.transaction != null) {
            throw duplicationTransaction(this);
        }
        return new LocalTransactionBuilder(this);
    }

    @Override
    public boolean inTransaction() {
        return this.transaction != null;
    }

    @Override
    protected String transactionName() {
        final SyncLocalTransaction tx = this.transaction;
        return tx == null ? null : tx.name();
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    /*################################## blow package method ##################################*/

    void clearChangedCache(final SyncLocalTransaction transaction) {
        if (this.transaction != transaction) {
            String m = String.format("%s and %s not match.", transaction, this.transaction);
            throw new IllegalArgumentException(m);
        }
        this.sessionCache.clearChangedOnRollback();
    }

    void endTransaction(final SyncLocalTransaction transaction) {
        if (this.transaction != transaction) {
            throw new IllegalArgumentException("transaction not match.");
        }
//        final TransactionInfo status = transaction.status();
//        switch (status) {
//            case ROLLED_BACK:
//            case COMMITTED:
//            case FAILED_ROLLBACK:
//                this.transaction = null;
//                break;
//            default:
//                throw new IllegalArgumentException(String.format("transaction status[%s] error.", status));
//        }

    }


    /**
     * @see #update(SimpleDmlStatement, Visible)
     */
    private long insert(final InsertStatement statement, final Visible visible) {
        //1. assert session status
        assertSession(statement, visible);

        try {

            //2. parse statement to stmt

            final SyncLocalSessionFactory factory = this.factory;
            final Stmt stmt;
            stmt = factory.dialectParser.insert(statement, visible);

            this.printSqlIfNeed(stmt);

            //3. execute stmt

            final long affectedRows;
            if (stmt instanceof SimpleStmt) {
                affectedRows = this.stmtExecutor.insert((SimpleStmt) stmt, this.getTxTimeout());
            } else {
                affectedRows = this.insertPairStmt((ChildTableMeta<?>) ((_Insert) statement).table(),
                        (PairStmt) stmt, this.getTxTimeout());
            }

            return affectedRows;
        } catch (ChildUpdateException e) {
            final LocalTransaction tx = this.transaction;
            if (tx != null) {
                tx.markRollbackOnly();
            }
            throw e;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", InsertStatement.class.getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    /**
     * @see #insert(InsertStatement, Visible)
     */
    private long insertPairStmt(final ChildTableMeta<?> domainTable, final PairStmt stmt, final int timeout) {
        final long startTime = System.currentTimeMillis();

        final long insertRows;
        insertRows = this.stmtExecutor.insert(stmt.firstStmt(), timeout);

        final int restSeconds;
        restSeconds = restSeconds(domainTable, startTime, timeout);

        try {
            final long childRows;
            childRows = this.stmtExecutor.insert(stmt.secondStmt(), restSeconds);

            if (childRows != insertRows) {
                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, insertRows, childRows);
            }
            return insertRows;
        } catch (ChildUpdateException e) {
            throw e;
        } catch (Throwable e) {
            throw _Exceptions.childInsertError(this, domainTable, e);
        }

    }

    /**
     * @see #query(SimpleDqlStatement, Class, Supplier, Visible)
     * @see #queryObject(SimpleDqlStatement, Supplier, Supplier, Visible)
     * @see #queryRecord(SimpleDqlStatement, Function, Supplier, Visible)
     */
    private <R> List<R> queryList(final SimpleDqlStatement statement, final QueryFunction<List<R>> queryFunc,
                                  final Visible visible) {
        //1.assert session status
        assertSession(statement, visible);

        try {

            final Stmt stmt;
            stmt = this.parseDqlStatement(statement, false, visible);

            final List<R> resultList;
            final int timeout = this.getTxTimeout();
            if (stmt instanceof SimpleStmt) {
                resultList = queryFunc.query((SimpleStmt) stmt, timeout);
            } else if (!(stmt instanceof PairStmt)) {
                // no bug,never here
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (statement instanceof InsertStatement) {
                resultList = this.returningInsertPairStmt((InsertStatement) statement, queryFunc, (PairStmt) stmt,
                        timeout);
            } else {
                //TODO add DmlStatement code for firebird
                // no bug,never here
                throw _Exceptions.unexpectedStatement(statement);
            }
            if (!this.factory.buildInExecutor) {
                Objects.requireNonNull(resultList);
            }
            return resultList;
        } catch (ChildUpdateException e) {
            final LocalTransaction tx = this.transaction;
            if (tx != null) {
                tx.markRollbackOnly();
            }
            throw e;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            throw _Exceptions.unknownSessionError(this, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    /**
     * @see #query(SimpleDqlStatement, Class, Supplier, Visible)
     */
    private <R> List<R> returningInsertPairStmt(final InsertStatement statement, final QueryFunction<List<R>> queryFunc,
                                                final PairStmt stmt, final int timeout) {

        final _Insert._ChildInsert childInsert = (_Insert._ChildInsert) statement;
        final boolean firstStmtIsQuery = childInsert.parentStmt() instanceof _ReturningDml;

        final long startTime;
        startTime = System.currentTimeMillis();

        long rows = 0;
        List<R> resultList = null;
        if (firstStmtIsQuery) {
            resultList = queryFunc.query(stmt.firstStmt(), timeout);
        } else {
            rows = this.stmtExecutor.insert(stmt.firstStmt(), timeout);
        }

        if (resultList == null && rows == 0) {
            // exists conflict clause
            return _Collections.emptyList();
        }

        final ChildTableMeta<?> childTable;
        childTable = (ChildTableMeta<?>) childInsert.table();

        final int restTimeout;
        restTimeout = restSeconds(childTable, startTime, timeout);

        try {
            if (firstStmtIsQuery) {
                rows = this.stmtExecutor.secondQuery((TwoStmtQueryStmt) stmt.secondStmt(), restTimeout, resultList);
                if (resultList instanceof ImmutableSpec && resultList.get(0) instanceof Map) {
                    resultList = _Collections.unmodifiableListForDeveloper(resultList);
                }
            } else {
                resultList = queryFunc.query(stmt.firstStmt(), timeout);
            }

            if (rows == resultList.size()) {
                return resultList;
            }

            if (firstStmtIsQuery) {
                throw _Exceptions.parentChildRowsNotMatch(this, childTable, resultList.size(), rows);
            } else {
                throw _Exceptions.parentChildRowsNotMatch(this, childTable, rows, resultList.size());
            }
        } catch (ChildUpdateException e) {
            throw e;
        } catch (Throwable e) {
            throw _Exceptions.childInsertError(this, childTable, e);
        }
    }


    /**
     * @see #queryStream(SimpleDqlStatement, Class, StreamOptions, Visible)
     * @see #queryObjectStream(SimpleDqlStatement, Supplier, StreamOptions, Visible)
     * @see #queryRecardStream(SimpleDqlStatement, Function, StreamOptions, Visible)
     */
    private <R> Stream<R> queryAsStream(final SimpleDqlStatement statement, final QueryFunction<Stream<R>> queryFunc,
                                        final Visible visible) {
        if (statement instanceof _Statement._ChildStatement) {
            throw _Exceptions.streamApiDontSupportTwoStmtMode();
        }
        //1.assert session status
        assertSession(statement, visible);

        try {
            final SimpleStmt stmt;
            stmt = (SimpleStmt) this.parseDqlStatement(statement, false, visible);
            return queryFunc.query(stmt, this.getTxTimeout());
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            throw _Exceptions.unknownSessionError(this, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }

    /**
     * @see #batchQuery(BatchDqlStatement, Class, Object, Supplier, boolean, Visible)
     * @see #batchQueryObject(BatchDqlStatement, Supplier, Object, Supplier, boolean, Visible)
     * @see #batchQueryRecord(BatchDqlStatement, Function, Object, Supplier, boolean, Visible)
     */
    private <R> List<R> batchQueryList(final BatchDqlStatement statement, final BatchQueryFunction<List<R>> queryFunc,
                                       final boolean useMultiStmt, final Visible visible) {
        //1. assert session status
        assertSession(statement, visible);
        try {
            final Stmt stmt;
            stmt = this.parseDqlStatement(statement, useMultiStmt, visible);
            final List<R> resultList;
            if (stmt instanceof BatchStmt) {
                resultList = queryFunc.query((BatchStmt) stmt, this.getTxTimeout());
            } else {//TODO firebird
                throw _Exceptions.unexpectedStatement(statement);
            }
            if (!this.factory.buildInExecutor) {
                Objects.requireNonNull(resultList);
            }
            return resultList;
        } catch (ChildUpdateException e) {
            final LocalTransaction tx = this.transaction;
            if (tx != null) {
                tx.markRollbackOnly();
            }
            throw e;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            throw _Exceptions.unknownError(e.getMessage(), e);
        } finally {
            ((_Statement) statement).clear();
        }
    }

    /**
     * @see #batchQueryStream(BatchDqlStatement, Class, Object, StreamOptions, boolean, Visible)
     * @see #batchQueryObjectStream(BatchDqlStatement, Supplier, Object, StreamOptions, boolean, Visible)
     * @see #batchQueryRecordStream(BatchDqlStatement, Function, Object, StreamOptions, boolean, Visible)
     */
    private <R> Stream<R> batchQueryAsStream(final BatchDqlStatement statement,
                                             final BatchQueryFunction<Stream<R>> queryFunc, final boolean useMultiStmt,
                                             final Visible visible) {
        //1. assert session status
        assertSession(statement, visible);
        try {
            final BatchStmt stmt;
            stmt = (BatchStmt) this.parseDqlStatement(statement, useMultiStmt, visible);
            return queryFunc.query(stmt, this.getTxTimeout());
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            throw _Exceptions.unknownError(e.getMessage(), e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    /**
     * @see #update(SimpleDmlStatement, Visible)
     */
    private long simpleUpdate(final SimpleDmlStatement statement, final Visible visible) {
        if (statement instanceof _BatchStatement) {
            // no bug,never here
            throw _Exceptions.unexpectedStatement(statement);
        }
        //1. assert session status
        assertSession(statement, visible);
        final LocalTransaction tx = this.transaction;
        try {

            //2. parse statement to stmt
            final Stmt stmt;
            stmt = this.parseDmlStatement(statement, false, visible);
            //3. execute stmt
            final int timeout = tx == null ? 0 : tx.nextTimeout();
            final long affectedRows;
            if (stmt instanceof SimpleStmt) {
                affectedRows = this.stmtExecutor.update((SimpleStmt) stmt, timeout);
            } else {
                affectedRows = this.updatePairStmt((ChildTableMeta<?>) ((_SingleUpdate._ChildUpdate) statement).table(),
                        (PairStmt) stmt, timeout);
            }

            //4. assert optimistic lock
            if (affectedRows < 1 && stmt.hasOptimistic()) {
                throw _Exceptions.optimisticLock(affectedRows);
            }
            return affectedRows;
        } catch (ChildUpdateException e) {
            if (tx != null) {
                tx.markRollbackOnly();
            }
            throw e;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", statement.getClass().getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    /**
     * @see #simpleUpdate(SimpleDmlStatement, Visible)
     */
    private long updatePairStmt(final ChildTableMeta<?> domainTable, final PairStmt stmt, final int timeout) {
        final long startTime;
        startTime = System.currentTimeMillis();

        final long affectedRows;
        affectedRows = this.stmtExecutor.update(stmt.firstStmt(), timeout);

        final int restSeconds;
        restSeconds = restSeconds(domainTable, startTime, timeout);

        try {
            final long childRows;
            childRows = this.stmtExecutor.update(stmt.secondStmt(), restSeconds);

            if (childRows != affectedRows) {
                throw _Exceptions.parentChildRowsNotMatch(this, domainTable, affectedRows, childRows);
            }
            return affectedRows;
        } catch (ChildUpdateException e) {
            throw e;
        } catch (Exception e) {
            String m = String.format("%s parent update completion,but child insert occur error.", domainTable);
            throw new ChildUpdateException(m, e);
        }

    }


    /**
     * @see #query(SimpleDqlStatement, Class, Supplier, Visible)
     * @see #queryObject(SimpleDqlStatement, Supplier, Supplier, Visible)
     * @see #queryStream(SimpleDqlStatement, Class, StreamOptions, Visible)
     * @see #queryObjectStream(SimpleDqlStatement, Supplier, StreamOptions, Visible)
     */
    private Stmt parseDqlStatement(final DqlStatement statement, final boolean useMultiStmt, final Visible visible) {
        final Stmt stmt;
        if (statement instanceof SelectStatement) {
            stmt = this.factory.dialectParser.select((SelectStatement) statement, useMultiStmt, visible);
        } else if (!(statement instanceof DmlStatement)) {
            stmt = this.factory.dialectParser.dialectDql(statement, visible);
        } else if (statement instanceof InsertStatement) {
            stmt = this.factory.dialectParser.insert((InsertStatement) statement, visible);
        } else if (statement instanceof _Statement._ChildStatement) {
            throw new ArmyException("current api don't support child dml statement.");
        } else if (statement instanceof UpdateStatement) {
            stmt = this.factory.dialectParser.update((UpdateStatement) statement, useMultiStmt, visible);
        } else if (statement instanceof DeleteStatement) {
            stmt = this.factory.dialectParser.delete((DeleteStatement) statement, useMultiStmt, visible);
        } else {
            stmt = this.factory.dialectParser.dialectDml((DmlStatement) statement, visible);
        }
        this.printSqlIfNeed(stmt);
        return stmt;
    }

    /**
     * @see #simpleUpdate(SimpleDmlStatement, Visible)
     * @see #batchUpdate(BatchDmlStatement, IntFunction, boolean, Visible)
     */
    private Stmt parseDmlStatement(final DmlStatement statement, final boolean useMultiStmt, final Visible visible) {
        final Stmt stmt;
        if (statement instanceof UpdateStatement) {
            stmt = this.factory.dialectParser.update((UpdateStatement) statement, useMultiStmt, visible);
        } else if (statement instanceof DeleteStatement) {
            stmt = this.factory.dialectParser.delete((DeleteStatement) statement, useMultiStmt, visible);
        } else {
            stmt = this.factory.dialectParser.dialectDml(statement, visible);
        }
        this.printSqlIfNeed(stmt);
        return stmt;
    }


    private void assertSession(final Statement statement, final Visible visible) throws SessionException {
        if (this.closed) {
            throw _Exceptions.sessionClosed(this);
        }

        if (statement instanceof DmlStatement) {
            if (this.readonly) {
                throw _Exceptions.readOnlySession(this);
            }
            final LocalTransaction tx = this.transaction;
            if (tx != null && tx.readOnly()) {
                throw _Exceptions.readOnlyTransaction(this);
            }

            if (tx == null && statement instanceof _Statement._ChildStatement) {
                final TableMeta<?> domainTable;
                domainTable = ((_Statement._ChildStatement) statement).table();
                throw _Exceptions.childDmlNoTransaction(this, (ChildTableMeta<?>) domainTable);
            }
            if (statement instanceof _Insert._QueryInsert && !this.allowQueryInsert) {
                throw _Exceptions.dontSupportSubQueryInsert(this);
            }
        }

        if (!this.visible.isSupport(visible)) {
            throw _Exceptions.dontSupportNonVisible(this, visible);
        }

    }

    private int getTxTimeout() {
        final LocalTransaction tx = this.transaction;
        return tx == null ? 0 : tx.nextTimeout();
    }


    private static DuplicationSessionTransaction duplicationTransaction(ArmySyncLocalSession session) {
        String m = String.format("%s duplication transaction.", session);
        throw new DuplicationSessionTransaction(m);
    }


    /*################################## blow private multiInsert method ##################################*/



    /*################################## blow instance inner class  ##################################*/



    /*################################## blow static inner class ##################################*/


    static final class LocalTransactionBuilder extends TransactionOptions implements SyncLocalSession.TransactionBuilder {

        final ArmySyncLocalSession session;


        private LocalTransactionBuilder(ArmySyncLocalSession session) {
            this.session = session;
        }

        @Override
        public TransactionBuilder name(@Nullable String txName) {
            this.name = txName;
            return this;
        }

        @Override
        public TransactionBuilder isolation(Isolation isolation) {
            this.isolation = isolation;
            return this;
        }

        @Override
        public TransactionBuilder readonly(boolean readOnly) {
            this.readonly = readOnly;
            return this;
        }

        @Override
        public TransactionBuilder timeout(int timeoutSeconds) {
            this.timeout = timeoutSeconds;
            return this;
        }

        @Override
        public LocalTransaction build() throws SessionException {
            if (this.isolation == null) {
                String m = String.format("No specified %s,couldn't create transaction.", Isolation.class.getName());
                throw new CannotCreateTransactionException(m);
            }
            final ArmySyncLocalSession session = this.session;
            if (!this.readonly && session.readonly) {
                String m = String.format("Session[%s] is readonly,couldn't create non-readonly transaction."
                        , this.session.name);
                throw new CannotCreateTransactionException(m);
            }
            if (session.transaction != null) {
                throw duplicationTransaction(session);
            }
            final SyncLocalTransaction transaction;
            transaction = new SyncLocalTransaction(this);
            session.transaction = transaction;
            return transaction;
        }

    }//LocalTransactionBuilder


    private interface QueryFunction<R> {

        R query(SimpleStmt stmt, int timeout);

    }

    private interface BatchQueryFunction<R> {

        R query(BatchStmt stmt, int timeout);

    }


}
