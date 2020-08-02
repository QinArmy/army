package io.army.boot.sync;

import io.army.*;
import io.army.boot.CacheDomainUpdate;
import io.army.cache.DomainUpdateAdvice;
import io.army.cache.SessionCache;
import io.army.cache.UniqueKey;
import io.army.criteria.Select;
import io.army.criteria.Visible;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.tx.*;
import io.army.util.Assert;
import io.army.util.CriteriaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.TransactionalException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

final class SessionImpl extends AbstractSession {

    private static final Logger LOG = LoggerFactory.getLogger(SessionImpl.class);

    private final boolean currentSession;

    private final SessionCache sessionCache;

    private final ConnInitParam connInitParam;

    private final boolean readonly;

    private Transaction transaction;

    private boolean closed;

    SessionImpl(InnerGenericRmSessionFactory sessionFactory, Connection connection
            , boolean currentSession, boolean resetConnection) throws SessionException {
        super(sessionFactory, connection);

        this.currentSession = currentSession;
        this.connInitParam = createConnInitParam(connection, resetConnection);
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
    public boolean closed() {
        return this.closed;
    }

    @Override
    public void close() throws SessionException {
        if (this.closed) {
            return;
        }
        try {
            if (this.currentSession) {
                sessionFactory.currentSessionContext().removeCurrentSession(this);
            }
            if (this.transaction != null) {
                throw new TransactionNotCloseException("Transaction not close.");
            }
            connection.close();
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
    public TransactionBuilder builder(boolean readOnly, Isolation isolation, int timeoutSeconds)
            throws TransactionalException {
        checkSessionTransaction();
        if (this.readonly && !readOnly) {
            throw new CannotCreateTransactionException(ErrorCode.READ_ONLY_SESSION
                    , "session[%s] is read only,can't create not read only transaction.", this);
        }
        return new TransactionBuilderImpl(readOnly, isolation, timeoutSeconds);
    }

    @Override
    public final boolean hasTransaction() {
        return this.transaction != null;
    }

    @Override
    public void flush() throws SessionException {
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
            updateOne(CacheDomainUpdate.build(advice), Visible.ONLY_VISIBLE);
            advice.updateFinish();
        }
    }

    /*################################## blow package method ##################################*/

    @Nullable
    @Override
    Transaction obtainSessionTransaction() {
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

    private final class TransactionBuilderImpl implements TransactionBuilder, TransactionOption {

        private final boolean readOnly;

        private final Isolation isolation;

        private final int timeout;

        private String name;

        private TransactionBuilderImpl(boolean readOnly, Isolation isolation, int timeout) {
            Assert.notNull(isolation, "isolation required");
            this.readOnly = readOnly;
            this.isolation = isolation;
            this.timeout = timeout;
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
        public TransactionBuilder name(@Nullable String txName) {
            this.name = txName;
            return this;
        }

        @Override
        public Transaction build() throws TransactionalException {
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
