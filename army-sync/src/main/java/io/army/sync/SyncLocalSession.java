package io.army.sync;

import io.army.ArmyException;
import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Statement;
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
import java.util.function.Supplier;

final class SyncLocalSession extends _AbstractSyncSession implements LocalSession {

    private static final Logger LOG = LoggerFactory.getLogger(SyncLocalSession.class);

    final String name;

    final SyncLocalSessionFactory sessionFactory;

    final StmtExecutor stmtExecutor;

    final boolean readonly;

    private final _SessionCache sessionCache;

    private boolean onlySupportVisible;

    private boolean dontSupportSubQueryInsert;

    private SyncLocalTransaction transaction;

    private boolean closed;

    SyncLocalSession(final SyncLocalSessionFactory.LocalSessionBuilder builder) {
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
    public LocalSessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public <T> TableMeta<T> tableMeta(Class<T> domainClass) {
        final TableMeta<T> table;
        table = this.sessionFactory.getTable(domainClass);
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


    @Nullable
    @Override
    public <R> R get(final TableMeta<R> table, final Object id, final Visible visible) {
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
    public <R> R getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value
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
            stmt = this.sessionFactory.dialectParser.select((Select) statement, visible);

            sessionFactory.printSqlIfNeed(stmt);

            //3. execute stmt
            final LocalTransaction tx = this.transaction;
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
            stmt = this.sessionFactory.dialectParser.select((Select) statement, visible);
            sessionFactory.printSqlIfNeed(stmt);
            //3. execute stmt
            final LocalTransaction tx = this.transaction;
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
    public <T> void save(final T domain, final boolean preferLiteral
            , final NullMode mode, final Visible visible) {
        final TableMeta<T> table;
        table = (TableMeta<T>) this.sessionFactory.getTable(domain.getClass());
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
            affectedRows = this.insert((_Insert) dml, visible);
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
    public <T> void batchSave(final List<T> domainList, final boolean preferLiteral
            , final NullMode mode, final Visible visible) {
        final Class<T> domainClass;
        domainClass = (Class<T>) domainList.get(0).getClass();
        final TableMeta<T> table;
        table = this.sessionFactory.getTable(domainClass);
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
                stmt = (BatchStmt) this.sessionFactory.dialectParser.update((Update) dml, visible);
            } else if (dml instanceof DeleteStatement) {
                stmt = (BatchStmt) this.sessionFactory.dialectParser.delete((DeleteStatement) dml, visible);
            } else {
                throw _Exceptions.unexpectedStatement(dml);
            }
            sessionFactory.printSqlIfNeed(stmt);
            //3. execute stmt
            final LocalTransaction tx = this.transaction;
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
                , SyncLocalSession.class.getName(), this.name
                , this.sessionFactory.name(), System.identityHashCode(this)
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


    private long insert(final _Insert insert, final Visible visible) {
        try {
            //1. assert session status
            assertSession(true, visible);
            assertSessionForChildInsert(insert);

            //2. parse statement to stmt
            if (insert instanceof _Insert._QueryInsert && this.dontSupportSubQueryInsert) {
                throw _Exceptions.dontSupportSubQueryInsert(this);
            }
            final SyncLocalSessionFactory sessionFactory = this.sessionFactory;
            final Stmt stmt;
            stmt = sessionFactory.dialectParser.insert((Insert) insert, visible);

            sessionFactory.printSqlIfNeed(stmt);

            //3. execute stmt
            final LocalTransaction tx = this.transaction;
            final long affectedRows;
            affectedRows = this.stmtExecutor.insert(stmt, tx == null ? 0 : tx.nextTimeout());

            //4. validate value insert affected rows
            if (insert instanceof _Insert._DomainInsert
                    && affectedRows != ((_Insert._DomainInsert) insert).domainList().size()) {
                String m = String.format("value list size is %s,but affected %s rows."
                        , ((_Insert._DomainInsert) insert).domainList().size(), affectedRows);
                throw new ExecutorExecutionException(m);
            }
            return affectedRows;
        } catch (ChildInsertException e) {
            final LocalTransaction tx = this.transaction;
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
                stmt = (SimpleStmt) this.sessionFactory.dialectParser.update((Update) dml, visible);
            } else if (dml instanceof DeleteStatement) {
                stmt = (SimpleStmt) this.sessionFactory.dialectParser.delete((DeleteStatement) dml, visible);
            } else {
                throw _Exceptions.unexpectedStatement(dml);
            }

            sessionFactory.printSqlIfNeed(stmt);

            //3. execute stmt
            final LocalTransaction tx = this.transaction;
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
