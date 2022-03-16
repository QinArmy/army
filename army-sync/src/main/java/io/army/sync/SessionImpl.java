package io.army.sync;

import io.army.ArmyException;
import io.army.DuplicationSessionTransaction;
import io.army.SessionCloseFailureException;
import io.army.SessionException;
import io.army.cache.SessionCache;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._SubQueryInsert;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.stmt.Stmt;
import io.army.sync.executor.StmtExecutor;
import io.army.tx.*;
import io.army.util.CriteriaUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

final class SessionImpl extends AbstractSyncSession implements Session {

    private final String name;

    final SessionFactoryImpl sessionFactory;

    final StmtExecutor stmtExecutor;

    private final boolean currentSession;

    private final SessionCache sessionCache;

    private final boolean readonly;

    private boolean onlySupportVisible;

    private boolean dontSupportSubQueryInsert;

    private Transaction transaction;

    private boolean closed;

    SessionImpl(final SessionFactoryImpl sessionFactory, final SessionFactoryImpl.SessionBuilderImpl builder) {
        this.name = "unnamed";
        this.sessionFactory = sessionFactory;
        this.stmtExecutor = sessionFactory.executorFactory.createStmtExecutor();
        this.currentSession = builder.currentSession();
        this.readonly = builder.readOnly();
        if (sessionFactory.supportSessionCache()) {
            this.sessionCache = sessionFactory.createSessionCache(this);
        } else {
            this.sessionCache = null;
        }
    }


    @Override
    public SessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public boolean isReadonlySession() {
        final Transaction tx = this.transaction;
        return this.readonly || (tx != null && tx.readOnly());
    }


    @Nullable
    @Override
    public <T extends IDomain> T get(TableMeta<T> table, final Object id, Visible visible) {
        T actualReturn;
        if (this.sessionCache != null) {
            // try obtain cache
            actualReturn = this.sessionCache.getDomain(table, id);
            if (actualReturn != null) {
                return actualReturn;
            }
        }

        // 1. create sql
        Select select = CriteriaUtils.createSelectDomainById(table, id);
        // 2. execute sql
        T domain = this.selectOne(select, table.javaType(), visible);

        if (domain != null && this.sessionCache != null) {
            // 3. cache
            actualReturn = this.sessionCache.cacheDomainById(table, domain);
        } else {
            actualReturn = domain;
        }
        return actualReturn;
    }


    @Override
    public <R extends IDomain> R getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value
            , final Visible visible) {
        return null;
    }


    @Override
    public Map<String, Object> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor
            , Visible visible) {
        return Collections.emptyMap();
    }

    @Override
    public <R> List<R> select(Select select, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible) {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> selectAsMap(Select select, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor, Visible visible) {
        return Collections.emptyList();
    }


    @Override
    public long insert(final Insert insert, final Visible visible) {
        try {
            assertSessionForDml(visible);
            assertSessionForChildInsert((_Insert) insert);
            final Stmt stmt;
            if (insert instanceof _ValuesInsert) {
                stmt = this.sessionFactory.dialect.insert(insert, visible);
            } else if (insert instanceof _SubQueryInsert) {
                if (this.dontSupportSubQueryInsert) {
                    throw _Exceptions.dontSupportSubQueryInsert(this);
                }
                stmt = this.sessionFactory.dialect.insert(insert, visible);
            } else {
                throw _Exceptions.unexpectedStatement(insert);
            }
            final Transaction tx = this.transaction;
            return stmtExecutor.insert(stmt, tx == null ? 0L : tx.timeToLiveInMillis());
        } catch (ArmyException e) {
            throw this.sessionFactory.exceptionFunction().apply(e);
        } catch (RuntimeException e) {
            throw this.sessionFactory.exceptionFunction().apply(new ArmyException(e));
        } finally {
            ((_Statement) insert).clear();
        }
    }


    @Override
    public <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Supplier<List<R>> listConstructor
            , Visible visible) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> List<R> returningUpdate(Update update, Class<R> resultClass
            , Supplier<List<R>> listConstructor, Visible visible) {
        return Collections.emptyList();
    }

    @Override
    public <R> List<R> returningDelete(Delete delete, Class<R> resultClass
            , Supplier<List<R>> listConstructor, Visible visible) {
        return Collections.emptyList();
    }

    @Override
    public long delete(Delete delete, Visible visible) {
        return 0;
    }

    @Override
    public long update(Update update, Visible visible) {
        return 0;
    }

    @Override
    public List<Long> batchUpdate(Update update, Visible visible) {
        return null;
    }

    @Override
    public List<Long> batchDelete(Delete delete, Visible visible) {
        return null;
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
            if (this.transaction != null) {
                throw new TransactionNotCloseException("Transaction not end.");
            }
            this.stmtExecutor.close();
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
            String m = String.format("%s duplication transaction.", this);
            throw new DuplicationSessionTransaction(m);
        }
        return new LocalTransactionBuilder(this);
    }

    @Override
    public boolean hasTransaction() {
        return this.transaction != null;
    }

    @Override
    public void flush() throws SessionException {
//        if (this.sessionCache == null) {
//            return;
//        }
//        final boolean readOnly = this.isReadonlySession();
//        for (DomainUpdateAdvice advice : this.sessionCache.updateAdvices()) {
//            if (!advice.hasUpdate()) {
//                continue;
//            }
//            if (readOnly) {
//                throw new ReadOnlySessionException("Session is read only,can't update Domain cache.");
//            }
////            int updateRows;
////           // updateRows = update(CacheDomainUpdate.build(advice), Visible.ONLY_VISIBLE);
////            if (updateRows != 1) {
////                throw new OptimisticLockException("TableMeta[%s] maybe updated by other transaction."
////                        , advice.readonlyWrapper().tableMeta());
////            }
//            advice.updateFinish();
//        }
    }

    @Override
    public String toString() {
        return String.format("%s[%s] readonly[%s] %s.", Session.class.getName()
                , this.name, this.readonly, this.transaction);
    }

    /*################################## blow package method ##################################*/

    void clearChangedCache(final LocalTransaction transaction) {

        //TODO
    }


    /**
     * @see #insert(Insert, Visible)
     */
    private void assertSessionForDml(final Visible visible) throws SessionException {
        if (this.closed) {
            throw _Exceptions.sessionClosed(this);
        }
        if (this.readonly) {
            throw _Exceptions.readOnlySession(this);
        }
        final Transaction tx = this.transaction;
        if (tx != null && tx.readOnly()) {
            throw _Exceptions.readOnlyTransaction(this);
        }
        if (visible != Visible.ONLY_VISIBLE && this.onlySupportVisible) {
            throw _Exceptions.dontSupportNonVisible(this, visible);
        }

    }

    /**
     * @see #insert(Insert, Visible)
     * @see #returningInsert(Insert, Class, Supplier, Visible)
     */
    private void assertSessionForChildInsert(final _Insert insert) {
        final TableMeta<?> table;
        table = insert.table();
        if (table instanceof ChildTableMeta && this.transaction == null) {
            throw _Exceptions.childDmlNoTransaction(this, (ChildTableMeta<?>) table);
        }
    }


    /*################################## blow private multiInsert method ##################################*/



    /*################################## blow instance inner class  ##################################*/



    /*################################## blow static inner class ##################################*/


    static final class LocalTransactionBuilder extends TransactionOptions implements Session.TransactionBuilder {

        final SessionImpl session;


        private LocalTransactionBuilder(SessionImpl session) {
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
        public Transaction build() throws TransactionException {
            if (this.isolation == null) {
                String m = String.format("No specified %s,couldn't create transaction.", Isolation.class.getName());
                throw new CannotCreateTransactionException(m);
            }
            if (!this.readonly && this.session.readonly) {
                String m = String.format("Session[%s] is readonly,couldn't create non-readonly transaction."
                        , this.session.name);
                throw new CannotCreateTransactionException(m);
            }
            return new LocalTransaction(this);
        }

    }//LocalTransactionBuilder


}
