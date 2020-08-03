package io.army.boot.sync;

import io.army.*;
import io.army.cache.DomainUpdateAdvice;
import io.army.cache.SessionCache;
import io.army.cache.UniqueKey;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSQL;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.sync.SessionFactory;
import io.army.tx.*;
import io.army.util.CriteriaUtils;
import io.army.wrapper.SQLWrapper;

import javax.transaction.TransactionalException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

final class SessionImpl extends AbstractGenericSyncRmSession implements InnerSession {

    private final InnerSessionFactory sessionFactory;

    private final boolean currentSession;

    private final SessionCache sessionCache;

    private final ConnInitParam connInitParam;

    private final boolean readonly;

    private Transaction transaction;

    private boolean closed;

    SessionImpl(InnerSessionFactory sessionFactory, Connection connection
            , SingleDatabaseSessionFactory.SessionBuilderImpl builder) throws SessionException {
        super(sessionFactory, connection);

        this.sessionFactory = sessionFactory;
        this.currentSession = builder.currentSession();
        this.connInitParam = createConnInitParam(connection, builder.resetConnection());
        this.readonly = sessionFactory.readonly();
        if (sessionFactory.supportSessionCache()) {
            this.sessionCache = sessionFactory.sessionCacheFactory().createSessionCache(this);
        } else {
            this.sessionCache = null;
        }
    }

    @Nullable
    private ConnInitParam createConnInitParam(Connection conn, boolean resetConnection) {
        try {
            ConnInitParam initParam;
            if (resetConnection) {
                initParam = new ConnInitParam(
                        conn.getTransactionIsolation()
                        , conn.getAutoCommit()
                        , conn.isReadOnly());
            } else {
                initParam = null;
            }
            return initParam;
        } catch (SQLException e) {
            throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR, e, "connection query occur error.");
        }

    }

    @Override
    public final SessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public final boolean readonly() {
        return this.readonly || (this.transaction != null && this.transaction.readOnly());
    }


    @Nullable
    @Override
    public <T extends IDomain> T get(TableMeta<T> tableMeta, final Object id, Visible visible) {
        T actualReturn;
        if (this.sessionCache != null) {
            // try obtain cache
            actualReturn = this.sessionCache.getDomain(tableMeta, id);
            if (actualReturn != null) {
                return actualReturn;
            }
        }

        // 1. create sql
        Select select = CriteriaUtils.createSelectDomainById(tableMeta, id);
        // 2. execute sql
        T domain = this.selectOne(select, tableMeta.javaType(), visible);

        if (domain != null && this.sessionCache != null) {
            // 3. cache
            actualReturn = this.sessionCache.cacheDomainById(tableMeta, domain);
        } else {
            actualReturn = domain;
        }
        return actualReturn;
    }

    @Nullable
    @Override
    public <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible) {
        final UniqueKey uniqueKey = new UniqueKey(propNameList, valueList);
        T actualReturn;
        if (this.sessionCache != null) {
            // try obtain cache
            actualReturn = this.sessionCache.getDomain(tableMeta, uniqueKey);
            if (actualReturn != null) {
                return actualReturn;
            }
        }
        // 1. create sql
        Select select = CriteriaUtils.createSelectDomainByUnique(tableMeta, propNameList, valueList);
        // 2. execute sql
        T domain = this.selectOne(select, tableMeta.javaType(), visible);
        if (domain != null && this.sessionCache != null) {
            // 3. cache
            actualReturn = this.sessionCache.cacheDomainByUnique(tableMeta, domain, uniqueKey);
        } else {
            actualReturn = domain;
        }
        return actualReturn;
    }

    @Override
    public final int[] batchUpdate(Update update) {
        return this.batchUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public int[] batchUpdate(Update update, final Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchUpdate(this, sqlWrapper, true);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public long[] batchLargeUpdate(Update update) {
        return this.batchLargeUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public long[] batchLargeUpdate(Update update, final Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchLargeUpdate(this, sqlWrapper, true);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public int[] batchDelete(Delete delete) {
        return this.batchDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public int[] batchDelete(Delete delete, final Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchUpdate(this, sqlWrapper, false);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public long[] batchLargeDelete(Delete delete) {
        return this.batchLargeDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public long[] batchLargeDelete(Delete delete, final Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchLargeUpdate(this, sqlWrapper, false);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public final void valueInsert(Insert insert) {
        valueInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public final void valueInsert(Insert insert, final Visible visible) {
        //1. parse update sql
        List<SQLWrapper> sqlWrapperList = parseValueInsert(insert, null, visible);
        try {
            //2. execute sql by connection
            this.sessionFactory.insertSQLExecutor()
                    .valueInsert(this, sqlWrapperList);
        } catch (Exception e) {
            markRollbackOnlyForChildInsert(sqlWrapperList);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
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
                throw new TransactionNotCloseException("Transaction not close.");
            }
            if (this.currentSession) {
                this.sessionFactory.currentSessionContext().removeCurrentSession(this);
            }
            this.connection.close();
            this.closed = true;
        } catch (SessionException e) {
            throw e;
        } catch (Exception e) {
            throw new SessionCloseFailureException(e, "session close connection error.");
        }
    }

    @Override
    public final Transaction sessionTransaction() throws NoSessionTransactionException {
        if (this.transaction == null) {
            throw new NoSessionTransactionException("no session transaction.");
        }
        return this.transaction;
    }

    @Override
    public TransactionBuilder builder()
            throws TransactionalException {
        checkSessionTransaction();
        return new TransactionBuilderImpl();
    }

    @Override
    public final boolean hasTransaction() {
        return this.transaction != null;
    }

    @Override
    public final void flush() throws SessionException {
        if (this.sessionCache == null) {
            return;
        }
        final boolean readOnly = this.readonly();
        for (DomainUpdateAdvice advice : this.sessionCache.updateAdvices()) {
            if (!advice.hasUpdate()) {
                continue;
            }
            if (readOnly) {
                throw new ReadOnlySessionException("Session is read only,can't update Domain cache.");
            }
            int updateRows;
            updateRows = update(CacheDomainUpdate.build(advice), Visible.ONLY_VISIBLE);
            if (updateRows != 1) {
                throw new OptimisticLockException("TableMeta[%s] maybe updated by other transaction."
                        , advice.readonlyWrapper().tableMeta());
            }
            advice.updateFinish();
        }
    }



    /*################################## blow package method ##################################*/

    @Nullable
    @Override
    final GenericTransaction obtainTransaction() {
        return this.transaction;
    }

    /**
     * invoke by {@link Transaction#close()}
     */
    @Override
    public void closeTransaction(GenericSyncTransaction transaction) {
        if (this.transaction != transaction) {
            throw new IllegalArgumentException("transaction not match,can't close.");
        }
        ((LocalTransaction) transaction).assertCanClose();
        try {
            if (this.connInitParam != null) {
                // reset connection.
                this.connection.setReadOnly(this.connInitParam.readonly);
                this.connection.setTransactionIsolation(this.connInitParam.isolation);
                this.connection.setAutoCommit(this.connInitParam.autoCommit);
            }
            this.transaction = null;
        } catch (SQLException e) {
            throw new TransactionSystemException(e, "army reset connection failure after transaction end.");
        }
    }


    private void setSessionTransaction(Transaction transaction) throws TransactionalException {
        checkSessionTransaction();
        this.transaction = transaction;
    }

    private void checkSessionTransaction() throws TransactionalException {
        if (this.transaction != null) {
            throw new DuplicationSessionTransaction(
                    "create transaction failure,session[%s] duplication transaction.", this);
        }
    }


    /*################################## blow private multiInsert method ##################################*/



    /*################################## blow instance inner class  ##################################*/

    final class TransactionBuilderImpl implements TransactionBuilder, TransactionOption {

        private Isolation isolation;

        private int timeout = 0;

        private boolean readOnly;

        private String name;

        private TransactionBuilderImpl() {

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
        public TransactionBuilder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        @Override
        public TransactionBuilder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public final boolean readOnly() {
            return this.readOnly;
        }

        @Override
        public final Isolation isolation() {
            return this.isolation;
        }

        @Override
        public int timeout() {
            return this.timeout;
        }

        @Override
        public Transaction build() throws TransactionException {
            if (this.isolation == null) {
                throw new CannotCreateTransactionException(ErrorCode.TRANSACTION_ERROR, "not specified isolation.");
            }
            if (!this.readOnly && SessionImpl.this.readonly) {
                throw new CannotCreateTransactionException(ErrorCode.TRANSACTION_ERROR
                        , "Readonly session can't create non-readonly transaction.");
            }
            if (this.timeout == 0) {
                throw new CannotCreateTransactionException(ErrorCode.TRANSACTION_ERROR
                        , "not specified transaction timeout.");
            }
            Transaction tx = new LocalTransaction(SessionImpl.this, TransactionBuilderImpl.this);
            SessionImpl.this.setSessionTransaction(tx);
            return tx;
        }
    }

    /*################################## blow static inner class ##################################*/


    private static class ConnInitParam {

        private final int isolation;

        private final boolean autoCommit;

        private final boolean readonly;

        ConnInitParam(int isolation, boolean autoCommit, boolean readonly) {
            this.isolation = isolation;
            this.autoCommit = autoCommit;
            this.readonly = readonly;
        }


    }

}
