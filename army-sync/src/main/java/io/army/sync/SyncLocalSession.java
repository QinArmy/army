package io.army.sync;

import io.army.ArmyException;
import io.army.criteria.*;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.criteria.dialect.ReturningDelete;
import io.army.criteria.dialect.ReturningInsert;
import io.army.criteria.dialect.ReturningUpdate;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.proxy._SessionCache;
import io.army.session.*;
import io.army.stmt.*;
import io.army.sync.executor.StmtExecutor;
import io.army.tx.*;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

final class SyncLocalSession extends _ArmySyncSession implements LocalSession {

    private static final Logger LOG = LoggerFactory.getLogger(SyncLocalSession.class);

    final SyncLocalSessionFactory factory;

    final StmtExecutor stmtExecutor;

    final boolean readonly;

    private final _SessionCache sessionCache;

    private boolean onlySupportVisible;

    private boolean dontSupportSubQueryInsert;

    private SyncLocalTransaction transaction;

    private final Visible visible;

    private boolean closed;

    SyncLocalSession(final SyncLocalSessionFactory.LocalSessionBuilder builder) {
        super(builder.name, builder.readonly);

        this.factory = builder.factory;
        this.stmtExecutor = builder.stmtExecutor;
        assert this.stmtExecutor != null;
        this.readonly = builder.readonly;
        this.visible = builder.visible;
        this.sessionCache = this.factory.sessionCacheFactory.createCache();
    }

    @Override
    public Visible visible() {
        return this.visible;
    }

    @Override
    public LocalSessionFactory sessionFactory() {
        return this.factory;
    }

    @Override
    public <T> TableMeta<T> tableMeta(Class<T> domainClass) {
        final TableMeta<T> table;
        table = this.factory.getTable(domainClass);
        if (table == null) {
            String m = String.format("Not found %s for %s.", TableMeta.class.getName(), domainClass.getName());
            throw new IllegalArgumentException(m);
        }
        return table;
    }

    @Override
    public boolean isReadOnlyStatus() {
        final LocalTransaction tx = this.transaction;
        return this.readonly || (tx != null && tx.readOnly());
    }


    @Override
    public <R> List<R> query(final SimpleDqlStatement statement, final Class<R> resultClass,
                             final Supplier<List<R>> listConstructor, final Visible visible) {

        //1.assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {
            final int timeOut;
            timeOut = this.getTxTimeout();

            final SyncLocalSessionFactory factory = this.factory;

            final SimpleStmt stmt;
            final List<R> resultList;

            if (statement instanceof Select) {
                stmt = factory.dialectParser.select((Select) statement, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.query(stmt, timeOut, resultClass, listConstructor);
            } else if (!(statement instanceof DmlStatement)) {
                stmt = factory.dialectParser.dialectDql(statement, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.query(stmt, timeOut, resultClass, listConstructor);
            } else if (statement instanceof _Statement._ChildStatement) {
                throw new ArmyException("don't support"); //TODO
            } else if (statement instanceof ReturningUpdate) {
                stmt = (SimpleStmt) factory.dialectParser.update((ReturningUpdate) statement, false, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.query(stmt, timeOut, resultClass, listConstructor);
            } else if (statement instanceof ReturningInsert) {
                stmt = (SimpleStmt) factory.dialectParser.insert((ReturningInsert) statement, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.query(stmt, timeOut, resultClass, listConstructor);
            } else if (statement instanceof ReturningDelete) {
                stmt = (SimpleStmt) factory.dialectParser.delete((ReturningDelete) statement, false, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.query(stmt, timeOut, resultClass, listConstructor);
            } else {
                stmt = (SimpleStmt) factory.dialectParser.dialectDml((DmlStatement) statement, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.query(stmt, timeOut, resultClass, listConstructor);
            }

            return resultList;
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
    public List<Map<String, Object>> queryAsMap(final SimpleDqlStatement statement,
                                                final Supplier<Map<String, Object>> mapConstructor,
                                                final Supplier<List<Map<String, Object>>> listConstructor,
                                                final Visible visible) {
        //1.assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {

            final int timeOut;
            timeOut = this.getTxTimeout();

            final SyncLocalSessionFactory factory = this.factory;

            final SimpleStmt stmt;
            final List<Map<String, Object>> resultList;

            if (statement instanceof Select) {
                stmt = factory.dialectParser.select((Select) statement, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.queryAsMap(stmt, timeOut, mapConstructor, listConstructor);
            } else if (!(statement instanceof DmlStatement)) {
                stmt = factory.dialectParser.dialectDql(statement, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.queryAsMap(stmt, timeOut, mapConstructor, listConstructor);
            } else if (statement instanceof _Statement._ChildStatement) {
                throw new ArmyException("don't support"); //TODO
            } else if (statement instanceof ReturningUpdate) {
                stmt = (SimpleStmt) factory.dialectParser.update((ReturningUpdate) statement, false, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.queryAsMap(stmt, timeOut, mapConstructor, listConstructor);
            } else if (statement instanceof ReturningInsert) {
                stmt = (SimpleStmt) factory.dialectParser.insert((ReturningInsert) statement, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.queryAsMap(stmt, timeOut, mapConstructor, listConstructor);
            } else if (statement instanceof ReturningDelete) {
                stmt = (SimpleStmt) factory.dialectParser.delete((ReturningDelete) statement, false, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.queryAsMap(stmt, timeOut, mapConstructor, listConstructor);
            } else {
                stmt = (SimpleStmt) factory.dialectParser.dialectDml((DmlStatement) statement, visible);
                factory.printSqlIfNeed(stmt);
                resultList = this.stmtExecutor.queryAsMap(stmt, timeOut, mapConstructor, listConstructor);
            }
            return resultList;
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
    public long update(final SimpleDmlStatement statement, final Visible visible) {
        if (statement instanceof _BatchDml) {
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
    public List<Long> batchUpdate(final BatchDmlStatement statement, final Supplier<List<Long>> listConstructor,
                                  final boolean useMultiStmt, final Visible visible) {
        if (!(statement instanceof _BatchDml)) {
            // no bug,never here
            throw _Exceptions.unexpectedStatement(statement);
        }
        //1. assert session status
        assertSession(true, visible);
        try {
            //2. parse statement to stmt
            final Stmt stmt;
            if (statement instanceof UpdateStatement) {
                stmt = this.factory.dialectParser.update((UpdateStatement) statement, useMultiStmt, visible);
            } else if (statement instanceof DeleteStatement) {
                stmt = this.factory.dialectParser.delete((DeleteStatement) statement, useMultiStmt, visible);
            } else {
                throw _Exceptions.unexpectedStatement(statement);
            }
            this.factory.printSqlIfNeed(stmt);

            //3. execute stmt
            final int timeout;
            timeout = this.getTxTimeout();
            final List<Long> resultList;
            if (useMultiStmt) {
                resultList = this.stmtExecutor.multiStmtBatchUpdate((MultiStmt) stmt, timeout, listConstructor);
            } else if (stmt instanceof BatchStmt) {
                resultList = this.stmtExecutor.batchUpdate((BatchStmt) stmt, timeout, listConstructor, null, null);
            } else if (!this.hasTransaction()) {
                throw new ArmyException("update/delete child must in transaction.");
            } else {
                final long startTime;
                startTime = System.currentTimeMillis();

                resultList = this.stmtExecutor.batchUpdate(((PairBatchStmt) stmt).firstStmt(), timeout,
                        listConstructor, null, null
                );

                final ChildTableMeta<?> domainTable;
                domainTable = (ChildTableMeta<?>) ((_Statement._ChildStatement) statement).table();

                this.stmtExecutor.batchUpdate(((PairBatchStmt) stmt).secondStmt(),
                        restSecond(domainTable, startTime, timeout), listConstructor, domainTable,
                        resultList
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
    public <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                  Supplier<List<R>> listConstructor, boolean useMultiStmt, Visible visible) {
        return null;
    }

    @Override
    public List<Map<String, Object>> batchQueryAsMap(BatchDqlStatement statement,
                                                     Supplier<Map<String, Object>> mapConstructor,
                                                     Map<String, Object> terminator,
                                                     Supplier<List<Map<String, Object>>> listConstructor,
                                                     boolean useMultiStmt, Visible visible) {
        return null;
    }

    @Override
    public <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                          StreamOptions options, boolean useMultiStmt, Visible visible) {
        return null;
    }

    @Override
    public Stream<Map<String, Object>> batchQueryMapStream(BatchDqlStatement statement,
                                                           Supplier<Map<String, Object>> mapConstructor,
                                                           Map<String, Object> terminator, StreamOptions options,
                                                           boolean useMultiStmt, Visible visible) {
        return null;
    }

    @Override
    public <R> Stream<R> queryStream(final SimpleDqlStatement statement, final Class<R> resultClass,
                                     final StreamOptions options, final Visible visible) {
        //1. assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {
            final SimpleStmt stmt;
            stmt = this.parseSimpleDqlStatement(statement, visible);
            return this.stmtExecutor.queryStream(stmt, this.getTxTimeout(), resultClass, options);
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", SimpleDqlStatement.class.getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    @Override
    public Stream<Map<String, Object>> queryMapStream(final SimpleDqlStatement statement,
                                                      final Supplier<Map<String, Object>> mapConstructor,
                                                      final StreamOptions options, final Visible visible) {
        //1. assert session status
        assertSession(statement instanceof DmlStatement, visible);
        try {
            final SimpleStmt stmt;
            stmt = this.parseSimpleDqlStatement(statement, visible);
            return this.stmtExecutor.queryMapStream(stmt, this.getTxTimeout(), mapConstructor, options);
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", SimpleDqlStatement.class.getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }


    @Override
    public MultiResult multiStmt(MultiStatement statement, Visible visible) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public MultiResult call(CallableStatement callable) {
        //TODO
        throw new UnsupportedOperationException();
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
    public boolean hasTransaction() {
        return this.transaction != null;
    }


    @Override
    public String toString() {
        return String.format("[%s name:%s,factory:%s,hash:%s,readonly:%s,transaction non-null:%s]"
                , SyncLocalSession.class.getName(), this.name
                , this.factory.name(), System.identityHashCode(this)
                , this.readonly, this.transaction != null);
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
        final TransactionStatus status = transaction.status();
        switch (status) {
            case ROLLED_BACK:
            case COMMITTED:
            case FAILED_ROLLBACK:
                this.transaction = null;
                break;
            default:
                throw new IllegalArgumentException(String.format("transaction status[%s] error.", status));
        }

    }


    /**
     * @see #update(SimpleDmlStatement, Visible)
     */
    private long insert(final InsertStatement statement, final Visible visible) {

        try {
            //1. assert session status
            assertSession(true, visible);
            assertSessionForChildInsert(statement);

            //2. parse statement to stmt
            if (statement instanceof _Insert._QueryInsert && this.dontSupportSubQueryInsert) {
                throw _Exceptions.dontSupportSubQueryInsert(this);
            }
            final SyncLocalSessionFactory factory = this.factory;
            final Stmt stmt;
            stmt = factory.dialectParser.insert(statement, visible);

            factory.printSqlIfNeed(stmt);

            //3. execute stmt

            final long affectedRows;
            if (stmt instanceof SimpleStmt) {
                affectedRows = this.stmtExecutor.insert((SimpleStmt) stmt, this.getTxTimeout());
            } else {
                affectedRows = this.insertPairStmt((ChildTableMeta<?>) ((_Insert) statement).table(),
                        (PairStmt) stmt, this.getTxTimeout());
            }

            //4. validate value insert affected rows
            if (!(statement instanceof _Insert._QueryInsert)
                    && affectedRows != ((_Insert) statement).insertRowCount()) {
                String m = String.format("value list size is %s,but affected %s rows."
                        , ((_Insert._DomainInsert) statement).domainList().size(), affectedRows);
                throw new DataAccessException(m);
            } else if (affectedRows < 1) {
                String m = String.format("%s insert rows is %s .", ((_Insert) statement).table(), affectedRows);
                throw new DataAccessException(m);
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
        restSeconds = _ArmySession.restSecond(domainTable, startTime, timeout);

        try {
            final long childRows;
            childRows = this.stmtExecutor.insert(stmt.secondStmt(), restSeconds);

            if (childRows != insertRows) {
                throw _Exceptions.parentChildRowsNotMatch(domainTable, insertRows, childRows);
            }
            return insertRows;
        } catch (ChildUpdateException e) {
            throw e;
        } catch (Exception e) {
            throw new ChildUpdateException("Parent insert completion,but child insert occur error.", e);
        }

    }

    /**
     * @see #update(SimpleDmlStatement, Visible)
     */
    private long simpleUpdate(final SimpleDmlStatement statement, final Visible visible) {
        if (statement instanceof _BatchDml) {
            // no bug,never here
            throw _Exceptions.unexpectedStatement(statement);
        }
        final LocalTransaction tx = this.transaction;
        try {
            //1. assert session status
            assertSession(true, visible);
            //2. parse statement to stmt
            final Stmt stmt;
            if (statement instanceof Update) {
                stmt = this.factory.dialectParser.update((Update) statement, false, visible);
            } else if (statement instanceof Delete) {
                stmt = this.factory.dialectParser.delete((Delete) statement, false, visible);
            } else {
                stmt = this.factory.dialectParser.dialectDml(statement, visible);
            }

            this.factory.printSqlIfNeed(stmt);
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
        restSeconds = _ArmySession.restSecond(domainTable, startTime, timeout);

        try {
            final long childRows;
            childRows = this.stmtExecutor.update(stmt.secondStmt(), restSeconds);

            if (childRows != affectedRows) {
                throw _Exceptions.parentChildRowsNotMatch(domainTable, affectedRows, childRows);
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
     * @see #queryAsMap(SimpleDqlStatement, Supplier, Supplier, Visible)
     * @see #queryStream(SimpleDqlStatement, Class, StreamOptions, Visible)
     * @see #queryMapStream(SimpleDqlStatement, Supplier, StreamOptions, Visible)
     */
    private SimpleStmt parseSimpleDqlStatement(final SimpleDqlStatement statement, final Visible visible) {
        final SimpleStmt stmt;
        if (statement instanceof Select) {
            stmt = this.factory.dialectParser.select((Select) statement, visible);
        } else if (!(statement instanceof DmlStatement)) {
            stmt = this.factory.dialectParser.dialectDql(statement, visible);
        } else if (statement instanceof _Statement._ChildStatement) {
            throw new ArmyException("stream api don't support child statement.");
        } else if (statement instanceof ReturningUpdate) {
            stmt = (SimpleStmt) this.factory.dialectParser.update((ReturningUpdate) statement, false, visible);
        } else if (statement instanceof ReturningDelete) {
            stmt = (SimpleStmt) this.factory.dialectParser.delete((ReturningDelete) statement, false, visible);
        } else {
            stmt = (SimpleStmt) this.factory.dialectParser.dialectDml((DmlStatement) statement, visible);
        }
        this.factory.printSqlIfNeed(stmt);
        return stmt;
    }


    private void assertSession(final boolean dmlStmt, final Visible visible) throws SessionException {
        if (this.closed) {
            throw _Exceptions.sessionClosed(this);
        }

        if (dmlStmt) {
            if (this.readonly) {
                throw _Exceptions.readOnlySession(this);
            }
            final LocalTransaction tx = this.transaction;
            if (tx != null && tx.readOnly()) {
                throw _Exceptions.readOnlyTransaction(this);
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

    private void assertSessionForChildInsert(final InsertStatement statement) {
        final TableMeta<?> domainTable;
        domainTable = ((_Insert) statement).table();

        if (domainTable instanceof ChildTableMeta && this.transaction == null) {
            throw _Exceptions.childDmlNoTransaction(this, (ChildTableMeta<?>) domainTable);
        }
    }


    private static DuplicationSessionTransaction duplicationTransaction(SyncLocalSession session) {
        String m = String.format("%s duplication transaction.", session);
        throw new DuplicationSessionTransaction(m);
    }


    /*################################## blow private multiInsert method ##################################*/



    /*################################## blow instance inner class  ##################################*/



    /*################################## blow static inner class ##################################*/


    static final class LocalTransactionBuilder extends TransactionOptions implements LocalSession.TransactionBuilder {

        final SyncLocalSession session;


        private LocalTransactionBuilder(SyncLocalSession session) {
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
            final SyncLocalSession session = this.session;
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


}
