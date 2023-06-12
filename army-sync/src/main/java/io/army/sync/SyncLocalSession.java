package io.army.sync;

import io.army.ArmyException;
import io.army.criteria.*;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.criteria.dialect.ReturningDelete;
import io.army.criteria.dialect.ReturningInsert;
import io.army.criteria.dialect.ReturningUpdate;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.proxy._SessionCache;
import io.army.session.*;
import io.army.stmt.BatchStmt;
import io.army.stmt.PairStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
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

    final String name;

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
        final String name = builder.name;
        if (name == null) {
            this.name = "unnamed";
        } else {
            this.name = name;
        }
        this.factory = builder.sessionFactory;
        this.stmtExecutor = builder.stmtExecutor;
        assert this.stmtExecutor != null;
        this.readonly = builder.readonly;
        this.visible = builder.visible;
        this.sessionCache = this.factory.sessionCacheFactory.createCache();
    }

    @Override
    public String name() {
        return this.name;
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
    public boolean isReadonlySession() {
        return this.readonly;
    }


    @Override
    public <R> List<R> query(final SimpleDqlStatement statement, final Class<R> resultClass,
                             final Supplier<List<R>> listConstructor, final Visible visible) {
        try {
            //1.assert session status
            assertSession(statement instanceof DmlStatement, visible);

            final LocalTransaction tx = this.transaction;
            final int timeOut;
            timeOut = tx == null ? 0 : tx.nextTimeout();

            final SyncLocalSessionFactory factory = this.factory;

            final SimpleStmt queryStmt;
            final Stmt dmlStmt;
            final List<R> resultList;

            if (statement instanceof Select) {
                queryStmt = factory.dialectParser.select((Select) statement, visible);
                factory.printSqlIfNeed(queryStmt);
                resultList = this.stmtExecutor.query(queryStmt, timeOut, resultClass, listConstructor);
            } else if (!(statement instanceof DmlStatement)) {
                queryStmt = factory.dialectParser.dialectDql(statement, visible);
                factory.printSqlIfNeed(queryStmt);
                resultList = this.stmtExecutor.query(queryStmt, timeOut, resultClass, listConstructor);
            } else if (statement instanceof ReturningUpdate) {
                dmlStmt = factory.dialectParser.update((ReturningUpdate) statement, visible);
                factory.printSqlIfNeed(dmlStmt);
                resultList = this.stmtExecutor.returningUpdate(dmlStmt, timeOut, resultClass);
            } else if (statement instanceof ReturningInsert) {
                dmlStmt = factory.dialectParser.insert((ReturningInsert) statement, visible);
                factory.printSqlIfNeed(dmlStmt);
                resultList = this.stmtExecutor.returningUpdate(dmlStmt, timeOut, resultClass);
            } else if (statement instanceof ReturningDelete) {
                dmlStmt = factory.dialectParser.delete((ReturningDelete) statement, visible);
                factory.printSqlIfNeed(dmlStmt);
                resultList = this.stmtExecutor.returningUpdate(dmlStmt, timeOut, resultClass);
            } else {
                dmlStmt = factory.dialectParser.dialectDml((DmlStatement) statement, visible);
                factory.printSqlIfNeed(dmlStmt);
                resultList = this.stmtExecutor.returningUpdate(dmlStmt, timeOut, resultClass);
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
        try {
            //1.assert session status
            assertSession(statement instanceof DmlStatement, visible);

            final LocalTransaction tx = this.transaction;
            final int timeOut;
            timeOut = tx == null ? 0 : tx.nextTimeout();

            final SyncLocalSessionFactory factory = this.factory;

            final SimpleStmt queryStmt;
            final Stmt dmlStmt;
            final List<Map<String, Object>> resultList;

            if (statement instanceof Select) {
                queryStmt = factory.dialectParser.select((Select) statement, visible);
                factory.printSqlIfNeed(queryStmt);
                resultList = this.stmtExecutor.queryAsMap(queryStmt, timeOut, mapConstructor, listConstructor);
            } else if (!(statement instanceof DmlStatement)) {
                queryStmt = factory.dialectParser.dialectDql(statement, visible);
                factory.printSqlIfNeed(queryStmt);
                resultList = this.stmtExecutor.queryAsMap(queryStmt, timeOut, mapConstructor, listConstructor);
            } else if (statement instanceof ReturningUpdate) {
                dmlStmt = factory.dialectParser.update((ReturningUpdate) statement, visible);
                factory.printSqlIfNeed(dmlStmt);
                resultList = this.stmtExecutor.returningUpdateAsMap(dmlStmt, timeOut, mapConstructor);
            } else if (statement instanceof ReturningInsert) {
                dmlStmt = factory.dialectParser.insert((ReturningInsert) statement, visible);
                factory.printSqlIfNeed(dmlStmt);
                resultList = this.stmtExecutor.returnInsertAsMap(dmlStmt, timeOut, mapConstructor);
            } else if (statement instanceof ReturningDelete) {
                dmlStmt = factory.dialectParser.delete((ReturningDelete) statement, visible);
                factory.printSqlIfNeed(dmlStmt);
                resultList = this.stmtExecutor.returningUpdateAsMap(dmlStmt, timeOut, mapConstructor);
            } else {
                dmlStmt = factory.dialectParser.dialectDml((DmlStatement) statement, visible);
                factory.printSqlIfNeed(dmlStmt);
                resultList = this.stmtExecutor.returningUpdateAsMap(dmlStmt, timeOut, mapConstructor);
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
        final long affectedRows;
        if (statement instanceof InsertStatement) {
            affectedRows = this.insert((InsertStatement) statement, visible);
        } else {
            affectedRows = this.dmlUpdate(statement, visible);
        }
        return affectedRows;
    }


    @Override
    public List<Long> batchUpdate(final BatchDmlStatement dml, final Visible visible) {
        try {
            if (!(dml instanceof _BatchDml)) {
                throw _Exceptions.unexpectedStatement(dml);
            }
            //1. assert session status
            assertSession(true, visible);
            //2. parse statement to stmt
            final BatchStmt stmt;
            if (dml instanceof UpdateStatement) {
                stmt = (BatchStmt) this.factory.dialectParser.update((UpdateStatement) dml, visible);
            } else if (dml instanceof DeleteStatement) {
                stmt = (BatchStmt) this.factory.dialectParser.delete((DeleteStatement) dml, visible);
            } else {
                throw _Exceptions.unexpectedStatement(dml);
            }
            factory.printSqlIfNeed(stmt);
            //3. execute stmt
            final LocalTransaction tx = this.transaction;
            return this.stmtExecutor.batchUpdate(stmt, tx == null ? 0 : tx.nextTimeout());
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", UpdateStatement.class.getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) dml).clear();
        }
    }


    @Override
    public QueryResult batchQuery(BatchDqlStatement statement, Visible visible) {
        return null;
    }

    @Override
    public MultiResult multiStmt(MultiStatement statement, Visible visible) {
        return null;
    }

    @Override
    public MultiResult call(CallableStatement callable) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean closed() {
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

    @Override
    protected <R> Stream<R> doQueryStream(final SimpleDqlStatement statement, final Class<R> resultClass,
                                          final boolean serverStream, final int fetchSize,
                                          final @Nullable Comparable<? super R> comparator, final boolean parallel,
                                          final Visible visible) {
        return null;
    }

    @Override
    protected Stream<Map<String, Object>> doQueryMapStream(final SimpleDqlStatement statement,
                                                           final Supplier<Map<String, Object>> mapConstructor,
                                                           final boolean serverStream, final int fetchSize,
                                                           final @Nullable Comparable<Map<String, Object>> comparator,
                                                           final boolean parallel, final Visible visible) {
        return null;
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
        final LocalTransaction tx = this.transaction;
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

            final int timeout = tx == null ? 0 : tx.nextTimeout();
            final long affectedRows;
            if (stmt instanceof SimpleStmt) {
                affectedRows = this.stmtExecutor.insert((SimpleStmt) stmt, timeout);
            } else {
                affectedRows = this.insertPairStmt((ChildTableMeta<?>) ((_Insert) statement).insertTable(),
                        (PairStmt) stmt, timeout);
            }

            //4. validate value insert affected rows
            if (!(statement instanceof _Insert._QueryInsert)
                    && affectedRows != ((_Insert) statement).insertRowCount()) {
                String m = String.format("value list size is %s,but affected %s rows."
                        , ((_Insert._DomainInsert) statement).domainList().size(), affectedRows);
                throw new DataAccessException(m);
            }
            return affectedRows;
        } catch (ChildInsertException e) {
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
        final long restMills;
        if (timeout == 0) {
            restSeconds = 0;
        } else if ((restMills = (timeout * 1000L) - (System.currentTimeMillis() - startTime)) < 1L) {
            String m = "Parent insert completion,but timeout,so no time insert child.";
            throw new ChildInsertException(m, _Exceptions.timeout(timeout, restMills));
        } else if ((restMills % 1000L) == 0) {
            restSeconds = (int) (restMills / 1000L);
        } else {
            restSeconds = (int) (restMills / 1000L) + 1;
        }

        try {
            final long childRows;
            childRows = this.stmtExecutor.insert(stmt.secondStmt(), restSeconds);

            if (childRows != insertRows) {
                throw _Exceptions.parentChildRowsNotMatch(domainTable, insertRows, childRows);
            }
            return insertRows;
        } catch (ChildInsertException e) {
            throw e;
        } catch (Exception e) {
            throw new ChildInsertException("Parent insert completion,but child insert occur error.", e);
        }

    }


    private long dmlUpdate(final SimpleDmlStatement statement, final Visible visible) {
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
                stmt = this.factory.dialectParser.update((Update) statement, visible);
            } else if (statement instanceof Delete) {
                stmt = this.factory.dialectParser.delete((Delete) statement, visible);
            } else {
                stmt = this.factory.dialectParser.dialectDml(statement, visible);
            }

            this.factory.printSqlIfNeed(stmt);
            //3. execute stmt
            final int timeout = tx == null ? 0 : tx.nextTimeout();
            final long affectedRows;
            if (stmt instanceof SimpleStmt) {
                affectedRows = this.stmtExecutor.update((SimpleStmt) stmt, timeout);
            }

            //4. assert optimistic lock
            if (affectedRows < 1 && stmt.hasOptimistic()) {
                throw _Exceptions.optimisticLock(affectedRows);
            }
            return affectedRows;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", statement.getClass().getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
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

    private void assertSessionForChildInsert(final InsertStatement statement) {
        final TableMeta<?> domainTable;
        domainTable = ((_Insert) statement).insertTable();
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
