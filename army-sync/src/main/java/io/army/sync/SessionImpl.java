package io.army.sync;

import io.army.*;
import io.army.cache.DomainUpdateAdvice;
import io.army.cache.SessionCache;
import io.army.cache.UniqueKey;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.session.FactoryMode;
import io.army.tx.*;
import io.army.util.CriteriaUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

final class SessionImpl extends AbstractGenericSyncRmSession implements InnerSession {

    private final InnerSessionFactory sessionFactory;

    private final boolean currentSession;

    private final SessionCache sessionCache;

    private final ConnInitParam connInitParam;

    private final boolean readonly;

    private final FactoryMode factoryMode;

    private Transaction transaction;

    private boolean closed;

    SessionImpl(InnerSessionFactory sessionFactory, Connection connection
            , SessionFactoryImpl.SessionBuilderImpl builder) throws SessionException {
        super(sessionFactory, connection);

        this.sessionFactory = sessionFactory;
        this.currentSession = builder.currentSession();
        this.connInitParam = createConnInitParam(connection, builder.resetConnection());
        this.readonly = builder.readOnly();
        if (sessionFactory.supportSessionCache()) {
            this.sessionCache = sessionFactory.sessionCacheFactory().createSessionCache(this);
        } else {
            this.sessionCache = null;
        }
        this.factoryMode = sessionFactory.shardingMode();
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
    public Map<String, Object> selectOneAsUnmodifiableMap(Select select) {
        return null;
    }

    @Override
    public Map<String, Object> selectOneAsUnmodifiableMap(Select select, Visible visible) {
        return null;
    }

    @Override
    public List<Map<String, Object>> selectAsUnmodifiableMap(Select select) {
        return null;
    }

    @Override
    public List<Map<String, Object>> selectAsUnmodifiableMap(Select select, Visible visible) {
        return null;
    }

    @Override
    public void valueInsert(Insert insert) {

    }

    @Override
    public void valueInsert(Insert insert, Visible visible) {

    }

    @Override
    public List<Integer> batchUpdate(Update update) {
        return null;
    }

    @Override
    public List<Integer> batchUpdate(Update update, Visible visible) {
        return null;
    }

    @Override
    public List<Long> batchLargeUpdate(Update update) {
        return null;
    }

    @Override
    public List<Long> batchLargeUpdate(Update update, Visible visible) {
        return null;
    }

    @Override
    public List<Integer> batchDelete(Delete delete) {
        return null;
    }

    @Override
    public List<Integer> batchDelete(Delete delete, Visible visible) {
        return null;
    }

    @Override
    public List<Long> batchLargeDelete(Delete delete) {
        return null;
    }

    @Override
    public List<Long> batchLargeDelete(Delete delete, Visible visible) {
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
            throws TransactionException {
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

    @Override
    public final String toString() {
        String text = "SessionFactory[" + this.sessionFactory.name() + "]'s Session";
        if (this.transaction != null) {
            String txName = this.transaction.name();
            if (txName != null) {
                text += ("[" + txName + "]");
            }
        }
        return text;
    }

    /*################################## blow package method ##################################*/


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


    private void setSessionTransaction(Transaction transaction) throws TransactionException {
        checkSessionTransaction();
        this.transaction = transaction;
    }

    private void checkSessionTransaction() throws TransactionException {
        if (this.transaction != null) {
            throw new DuplicationSessionTransaction(
                    "create transaction failure,session[%s] duplication transaction.", this);
        }
    }

    private void assertSupportBatch() {
        if (this.factoryMode != FactoryMode.NO_SHARDING) {
            throw new SessionUsageException("not support batch operation in SHARDING mode.");
        }

    }

    /*################################## blow private multiInsert method ##################################*/



    /*################################## blow instance inner class  ##################################*/

    final class TransactionBuilderImpl implements TransactionBuilder, TransactionOption {

        private Isolation isolation;

        private int timeout = -1;

        private boolean readOnly;

        private long endMills = -1;

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
            if (timeout > 0) {
                this.endMills = (System.currentTimeMillis() + timeout + 1000L);
            } else {
                this.endMills = -1;
            }
            return this;
        }

        @Override
        public long endMills() {
            return this.endMills;
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
