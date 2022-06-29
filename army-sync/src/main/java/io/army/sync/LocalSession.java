package io.army.sync;

import io.army.ArmyException;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.domain.IDomain;
import io.army.env.ArmyKey;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.proxy._CacheBlock;
import io.army.proxy._SessionCache;
import io.army.session.*;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.sync.executor.StmtExecutor;
import io.army.tx.*;
import io.army.util.Criteria;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

final class LocalSession extends _AbstractSyncSession implements Session {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSession.class);

    final String name;

    final LocalSessionFactory sessionFactory;

    final StmtExecutor stmtExecutor;

    final boolean readonly;

    private final _SessionCache sessionCache;

    private boolean onlySupportVisible;

    private boolean dontSupportSubQueryInsert;

    private LocalTransaction transaction;

    private boolean closed;

    LocalSession(final LocalSessionFactory.LocalSessionBuilder builder) {
        final String name = builder.name;
        if (name == null) {
            this.name = "unnamed";
        } else {
            this.name = name;
        }
        this.sessionFactory = builder.sessionFactory;
        this.stmtExecutor = builder.stmtExecutor;
        assert this.stmtExecutor != null;
        this.readonly = builder.readonly;

        this.sessionCache = this.sessionFactory.sessionCacheFactory.createCache();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public SessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass) {
        final TableMeta<T> table;
        table = this.sessionFactory.tableMeta(domainClass);
        if (table == null) {
            String m = String.format("Not found %s for %s.", TableMeta.class.getName(), domainClass.getName());
            throw new IllegalArgumentException(m);
        }
        return table;
    }

    @Override
    public boolean isReadOnlyStatus() {
        final Transaction tx = this.transaction;
        return this.readonly || (tx != null && tx.readOnly());
    }

    @Override
    public boolean isReadonlySession() {
        return this.readonly;
    }


    @Nullable
    @Override
    public <R extends IDomain> R get(final TableMeta<R> table, final Object id, final Visible visible) {
        if (!this.sessionFactory.tableMap.containsKey(table.javaType())) {
            throw _Exceptions.tableDontBelongOf(table, this.sessionFactory);
        }
        final _SessionCache sessionCache = this.sessionCache;
        R domain;
        domain = sessionCache.get(table, id);
        if (domain != null) {
            return domain;
        }
        // 1. create sql
        final Select stmt;
        stmt = Criteria.createSelectDomainById(table, id);
        //2. get proxy class;
        final Class<? extends R> proxyClass;
        proxyClass = this.sessionFactory.sessionCacheFactory.getProxyClass(table);
        // 3. execute stmt
        domain = this.queryOne(stmt, proxyClass, visible);
        if (domain != null) {
            if (domain.getClass() != proxyClass) {
                String m = String.format("%s error implementation.", this.stmtExecutor.getClass().getName());
                throw new IllegalStateException(m);
            }
            //4. add to session cache
            domain = sessionCache.putIfAbsent(table, domain);
        }
        return domain;
    }


    @Override
    public <R extends IDomain> R getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value
            , final Visible visible) {
        if (!this.sessionFactory.uniqueCache) {
            throw _Exceptions.dontSupportUniqueCache(this.sessionFactory);
        }
        if (!this.sessionFactory.tableMap.containsKey(table.javaType())) {
            throw _Exceptions.tableDontBelongOf(table, this.sessionFactory);
        }
        final _SessionCache sessionCache = this.sessionCache;
        R domain;
        domain = sessionCache.get(table, field, value);
        if (domain != null) {
            return domain;
        }
        // 1. create sql
        final Select stmt;
        stmt = Criteria.createSelectDomainByUnique(table, field, value);
        //2. get proxy class;
        final Class<? extends R> proxyClass;
        proxyClass = this.sessionFactory.sessionCacheFactory.getProxyClass(table);
        // 3. execute stmt
        domain = this.queryOne(stmt, proxyClass, visible);
        if (domain != null) {
            if (domain.getClass() != proxyClass) {
                String m = String.format("%s error implementation.", this.stmtExecutor.getClass().getName());
                throw new IllegalStateException(m);
            }
            //4. add to session cache
            domain = sessionCache.putIfAbsent(table, domain);
        }
        return domain;
    }


    @Override
    public <R> List<R> query(final DqlStatement statement, final Class<R> resultClass
            , final Supplier<List<R>> listConstructor, final Visible visible) {
        try {
            //1.assert session status
            assertSession(false, visible);
            //2. parse statement to stmt
            final SimpleStmt stmt;
            stmt = this.sessionFactory.dialect.select((Select) statement, visible);

            printSqlIfNeed(stmt);

            //3. execute stmt
            final Transaction tx = this.transaction;
            return this.stmtExecutor.select(stmt, tx == null ? 0 : tx.nextTimeout(), resultClass, listConstructor);
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", Select.class.getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }

    @Override
    public List<Map<String, Object>> queryAsMap(DqlStatement statement, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor, Visible visible) {
        try {
            //1.assert session status
            assertSession(false, visible);
            //2. parse statement to stmt
            final SimpleStmt stmt;
            stmt = this.sessionFactory.dialect.select((Select) statement, visible);
            printSqlIfNeed(stmt);
            //3. execute stmt
            final Transaction tx = this.transaction;
            final int timeout = tx == null ? 0 : tx.nextTimeout();
            return this.stmtExecutor.selectAsMap(stmt, timeout, mapConstructor, listConstructor);
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", Select.class.getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) statement).clear();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IDomain> void save(final T domain, final boolean preferLiteral
            , final NullHandleMode mode, final Visible visible) {
        final TableMeta<T> table;
        table = (TableMeta<T>) this.sessionFactory.tableMeta(domain.getClass());
        if (table == null) {
            String m = String.format("Not found %s for %s.", TableMeta.class.getName(), domain.getClass().getName());
            throw new IllegalArgumentException(m);
        }
//        final Insert stmt;
//        stmt = SQLs.domainInsert(table)
//                .nullHandle(mode)
//                .preferLiteral(preferLiteral)
//                .insertInto(table)
//                .value(domain)
//                .asInsert();
//
//        this.insert(stmt, visible);
    }


    @Override
    public long update(final DmlStatement dml, final Visible visible) {
        final long affectedRows;
        if (dml instanceof Insert) {
            affectedRows = this.insert((Insert) dml, visible);
        } else if (dml instanceof NarrowDmlStatement) {
            affectedRows = this.dmlUpdate((NarrowDmlStatement) dml, visible);
        } else {
            throw _Exceptions.unexpectedStatement(dml);
        }
        return affectedRows;
    }

    @Override
    public <R> List<R> returningUpdate(final DmlStatement dml, final Class<R> resultClass
            , final Supplier<List<R>> listConstructor, final Visible visible) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Map<String, Object>> returningUpdateAsMap(final DmlStatement dml
            , final Supplier<Map<String, Object>> mapConstructor
            , final Supplier<List<Map<String, Object>>> listConstructor, final Visible visible) {
        throw new UnsupportedOperationException();
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T extends IDomain> void batchSave(final List<T> domainList, final boolean preferLiteral
            , final NullHandleMode mode, final Visible visible) {
        final Class<T> domainClass;
        domainClass = (Class<T>) domainList.get(0).getClass();
        final TableMeta<T> table;
        table = this.sessionFactory.tableMeta(domainClass);
        if (table == null) {
            String m = String.format("Not found %s for %s.", TableMeta.class.getName(), domainClass);
            throw new IllegalArgumentException(m);
        }
//        final Insert stmt;
//        stmt = SQLs.domainInsert(table)
//                .preferLiteral(preferLiteral)
//                .nullHandle(mode)
//                .insertInto(table)
//                .values(domainList)
//                .asInsert();
//        this.insert(stmt, visible);
    }

    @Override
    public List<Long> batchUpdate(final NarrowDmlStatement dml, final Visible visible) {
        try {
            if (!(dml instanceof _BatchDml)) {
                throw _Exceptions.unexpectedStatement(dml);
            }
            //1. assert session status
            assertSession(true, visible);
            //2. parse statement to stmt
            final BatchStmt stmt;
            if (dml instanceof Update) {
                stmt = (BatchStmt) this.sessionFactory.dialect.update((Update) dml, visible);
            } else if (dml instanceof Delete) {
                stmt = (BatchStmt) this.sessionFactory.dialect.delete((Delete) dml, visible);
            } else {
                throw _Exceptions.unexpectedStatement(dml);
            }
            printSqlIfNeed(stmt);
            //3. execute stmt
            final Transaction tx = this.transaction;
            return this.stmtExecutor.batchUpdate(stmt, tx == null ? 0 : tx.nextTimeout());
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", Update.class.getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) dml).clear();
        }
    }


    @Override
    public MultiResult multiStmt(List<Statement> statementList, Visible visible) {
        throw new UnsupportedOperationException();
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
            final Transaction tx = this.transaction;
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
    public Transaction sessionTransaction() throws NoSessionTransactionException {
        final Transaction transaction = this.transaction;
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
    public void flush() throws SessionException {
        long affectedRows;
        Update update;
        TableMeta<?> table;
        for (_CacheBlock block : this.sessionCache.getChangedList()) {
            update = block.statement();
            table = ((_SingleUpdate) update).table();
            affectedRows = this.dmlUpdate(update, Visible.ONLY_VISIBLE);
            if (affectedRows != 1L) {
                throw _Exceptions.notMatchRow(this, table, block.id());
            }
            block.success();
        }

    }


    @Override
    public String toString() {
        return String.format("[%s name:%s,factory:%s,hash:%s,readonly:%s,transaction non-null:%s]"
                , LocalSession.class.getName(), this.name
                , this.sessionFactory.name(), System.identityHashCode(this)
                , this.readonly, this.transaction != null);
    }

    /*################################## blow package method ##################################*/

    void clearChangedCache(final LocalTransaction transaction) {
        if (this.transaction != transaction) {
            String m = String.format("%s and %s not match.", transaction, this.transaction);
            throw new IllegalArgumentException(m);
        }
        this.sessionCache.clearChangedOnRollback();
    }

    void endTransaction(final LocalTransaction transaction) {
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


    private long insert(final Insert insert, final Visible visible) {
        try {
            //1. assert session status
            assertSession(true, visible);
            assertSessionForChildInsert((_Insert) insert);

            //2. parse statement to stmt
            if (insert instanceof _RowSetInsert && this.dontSupportSubQueryInsert) {
                throw _Exceptions.dontSupportSubQueryInsert(this);
            }
            final Stmt stmt;
            stmt = this.sessionFactory.dialect.insert(insert, visible);

            printSqlIfNeed(stmt);

            //3. execute stmt
            final Transaction tx = this.transaction;
            final long affectedRows;
            affectedRows = this.stmtExecutor.insert(stmt, tx == null ? 0 : tx.nextTimeout());

            //4. validate value insert affected rows
            if (insert instanceof _DomainInsert
                    && affectedRows != ((_DomainInsert) insert).domainList().size()) {
                String m = String.format("value list size is %s,but affected %s rows."
                        , ((_DomainInsert) insert).domainList().size(), affectedRows);
                throw new ExecutorExecutionException(m);
            }
            return affectedRows;
        } catch (ChildInsertException e) {
            final Transaction tx = this.transaction;
            if (tx != null) {
                tx.markRollbackOnly();
            }
            throw e;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", Insert.class.getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) insert).clear();
        }
    }


    private long dmlUpdate(final NarrowDmlStatement dml, final Visible visible) {
        try {
            if (dml instanceof _BatchDml) {
                throw _Exceptions.unexpectedStatement(dml);
            }
            //1. assert session status
            assertSession(true, visible);
            //2. parse statement to stmt
            final SimpleStmt stmt;
            if (dml instanceof Update) {
                stmt = (SimpleStmt) this.sessionFactory.dialect.update((Update) dml, visible);
            } else if (dml instanceof Delete) {
                stmt = (SimpleStmt) this.sessionFactory.dialect.delete((Delete) dml, visible);
            } else {
                throw _Exceptions.unexpectedStatement(dml);
            }

            printSqlIfNeed(stmt);

            //3. execute stmt
            final Transaction tx = this.transaction;
            final long affectedRows;
            affectedRows = this.stmtExecutor.update(stmt, tx == null ? 0 : tx.nextTimeout());
            //4. assert optimistic lock
            if (affectedRows < 1 && stmt.hasOptimistic()) {
                throw _Exceptions.optimisticLock(affectedRows);
            }
            return affectedRows;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Army execute %s occur error.", dml.getClass().getName());
            throw _Exceptions.unknownError(m, e);
        } finally {
            ((_Statement) dml).clear();
        }
    }


    /**
     * @see #insert(Insert, Visible)
     */
    private void assertSession(final boolean dmlStmt, final Visible visible) throws SessionException {
        if (this.closed) {
            throw _Exceptions.sessionClosed(this);
        }

        if (dmlStmt) {
            if (this.readonly) {
                throw _Exceptions.readOnlySession(this);
            }
            final Transaction tx = this.transaction;
            if (tx != null && tx.readOnly()) {
                throw _Exceptions.readOnlyTransaction(this);
            }
        }
        if (visible != Visible.ONLY_VISIBLE && this.onlySupportVisible) {
            throw _Exceptions.dontSupportNonVisible(this, visible);
        }

    }

    private void assertSessionForChildInsert(final _Insert insert) {
        final TableMeta<?> table;
        table = insert.table();
        if (table instanceof ChildTableMeta && this.transaction == null) {
            throw _Exceptions.childDmlNoTransaction(this, (ChildTableMeta<?>) table);
        }
    }

    private void printSqlIfNeed(final Stmt stmt) {
        final LocalSessionFactory sessionFactory = this.sessionFactory;
        final Function<String, String> sqlFormat;
        if ((sqlFormat = sessionFactory.getSqlFormatter()) != null) {
            if ((sessionFactory.sqlLogDynamic && sessionFactory.env.getOrDefault(ArmyKey.SQL_LOG_DEBUG))
                    || (!sessionFactory.sqlLogDynamic && sessionFactory.sqlLogDebug)) {
                LOG.debug(SQL_LOG_FORMAT, stmt.printSql(sqlFormat));
            } else {
                LOG.info(SQL_LOG_FORMAT, stmt.printSql(sqlFormat));
            }
        }

    }


    private static DuplicationSessionTransaction duplicationTransaction(LocalSession session) {
        String m = String.format("%s duplication transaction.", session);
        throw new DuplicationSessionTransaction(m);
    }


    /*################################## blow private multiInsert method ##################################*/



    /*################################## blow instance inner class  ##################################*/



    /*################################## blow static inner class ##################################*/


    static final class LocalTransactionBuilder extends TransactionOptions implements Session.TransactionBuilder {

        final LocalSession session;


        private LocalTransactionBuilder(LocalSession session) {
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
        public Transaction build() throws SessionException {
            if (this.isolation == null) {
                String m = String.format("No specified %s,couldn't create transaction.", Isolation.class.getName());
                throw new CannotCreateTransactionException(m);
            }
            final LocalSession session = this.session;
            if (!this.readonly && session.readonly) {
                String m = String.format("Session[%s] is readonly,couldn't create non-readonly transaction."
                        , this.session.name);
                throw new CannotCreateTransactionException(m);
            }
            if (session.transaction != null) {
                throw duplicationTransaction(session);
            }
            final LocalTransaction transaction;
            transaction = new LocalTransaction(this);
            session.transaction = transaction;
            return transaction;
        }

    }//LocalTransactionBuilder


}
