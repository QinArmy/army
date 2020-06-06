package io.army.boot;

import io.army.*;
import io.army.cache.DomainUpdateAdvice;
import io.army.cache.SessionCache;
import io.army.cache.UniqueKey;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSQL;
import io.army.dialect.Dialect;
import io.army.dialect.TransactionOption;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.tx.*;
import io.army.util.Assert;
import io.army.util.CriteriaUtils;
import io.army.wrapper.ChildBatchSQLWrapper;
import io.army.wrapper.ChildSQLWrapper;
import io.army.wrapper.SQLWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.TransactionalException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

final class SessionImpl implements InnerSession, InnerTxSession {

    private static final Logger LOG = LoggerFactory.getLogger(SessionImpl.class);


    private final InnerSessionFactory sessionFactory;

    private final Connection connection;

    private final ConnInitParam connInitParam;

    private final boolean readonly;

    private final boolean currentSession;

    private final SessionCache sessionCache;

    private final Dialect dialect;

    private Transaction transaction;

    private boolean closed;


    SessionImpl(InnerSessionFactory sessionFactory, Connection connection,
                boolean currentSession) throws SessionException {
        this.sessionFactory = sessionFactory;
        this.connection = connection;
        this.currentSession = currentSession;
        this.readonly = sessionFactory.readonly();

        this.dialect = sessionFactory.dialect();

        try {
            this.connInitParam = new ConnInitParam(
                    connection.getTransactionIsolation()
                    , connection.getAutoCommit()
                    , this.connection.isReadOnly());
        } catch (SQLException e) {
            throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR, e, "connection query occur error.");
        }

        if (sessionFactory.supportSessionCache()) {
            this.sessionCache = sessionFactory.sessionCacheFactory().createSessionCache(this);
        } else {
            this.sessionCache = null;
        }

    }

    @Override
    public SessionOptions options() {
        return null;
    }

    @Override
    public final boolean readonly() {
        return this.readonly
                || (this.transaction != null && this.transaction.readOnly());
    }



    @Nullable
    @Override
    public final <T extends IDomain> T get(TableMeta<T> tableMeta, Object id) {
        return get(tableMeta, id, Visible.ONLY_VISIBLE);
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
            , List<Object> valueList) {
        return getByUnique(tableMeta, propNameList, valueList, Visible.ONLY_VISIBLE);
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

    @Nullable
    @Override
    public <T> T selectOne(Select select, Class<T> resultClass) {
        return this.selectOne(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public <T> T selectOne(Select select, Class<T> resultClass, Visible visible) {
        List<T> list = select(select, resultClass, visible);
        T t;
        if (list.size() == 1) {
            t = list.get(0);
        } else if (list.size() == 0) {
            t = null;
        } else {
            throw new NonUniqueException("select result more than 1.");
        }
        return t;
    }

    @Override
    public <T> List<T> select(Select select, Class<T> resultClass) {
        return this.select(select, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public <T> List<T> select(Select select, Class<T> resultClass, Visible visible) {
        try {
            List<T> resultList;
            // execute sql and extract result
            resultList = this.sessionFactory.selectSQLExecutor()
                    .select(this, this.dialect.select(select, visible), resultClass);

            return resultList;
        } finally {
            ((InnerSQL) select).clear();
        }
    }

    @Override
    public int update(Update update) {
        return update(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public int update(Update update, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .update(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public <T> List<T> returningUpdate(Update update, Class<T> resultClass) {
        return returningUpdate(update, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public <T> List<T> returningUpdate(Update update, Class<T> resultClass, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {   //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .returningUpdate(this, sqlWrapper, resultClass);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public int[] batchUpdate(Update update) {
        return batchUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public int[] batchUpdate(Update update, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchUpdate(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public long largeUpdate(Update update) {
        return largeUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public long largeUpdate(Update update, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .largeUpdate(this, sqlWrapper);
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
        return batchLargeUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public long[] batchLargeUpdate(Update update, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchLargeUpdate(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public void insert(Insert insert) {
        insert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public void insert(Insert insert, final Visible visible) {
        //1. parse update sql
        final List<SQLWrapper> sqlWrapperList = parseInsert(insert, visible);
        try {
            //2. execute sql by connection
            this.sessionFactory.insertSQLExecutor()
                    .insert(this, sqlWrapperList);
        } catch (Exception e) {
            markRollbackOnlyForChildInsert(sqlWrapperList);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }

    @Override
    public int subQueryInsert(Insert insert) {
        return subQueryInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public int subQueryInsert(Insert insert, Visible visible) {
        //1. parse update sql
        final List<SQLWrapper> sqlWrapperList = parseInsert(insert, visible);
        if (sqlWrapperList.size() != 1) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "insert isn't sub query insert.");
        }
        try {
            //2. execute sql by connection
            return this.sessionFactory.insertSQLExecutor()
                    .subQueryInsert(this, sqlWrapperList.get(0));
        } catch (Exception e) {
            markRollbackOnlyForChildInsert(sqlWrapperList);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }

    @Override
    public long subQueryLargeInsert(Insert insert) {
        return subQueryLargeInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public long subQueryLargeInsert(Insert insert, Visible visible) {
        //1. parse update sql
        final List<SQLWrapper> sqlWrapperList = parseInsert(insert, visible);
        if (sqlWrapperList.size() != 1) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "insert isn't sub query insert.");
        }
        try {
            //2. execute sql by connection
            return this.sessionFactory.insertSQLExecutor()
                    .subQueryLargeInsert(this, sqlWrapperList.get(0));
        } catch (Exception e) {
            markRollbackOnlyForChildInsert(sqlWrapperList);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }

    @Override
    public <T> List<T> returningInsert(Insert insert, Class<T> resultClass) {
        return returningInsert(insert, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public <T> List<T> returningInsert(Insert insert, Class<T> resultClass, Visible visible) {
        //1. parse update sql
        final List<SQLWrapper> sqlWrapperList = parseInsert(insert, visible);
        if (sqlWrapperList.size() != 1) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "insert isn't returning");
        }
        try {
            //2. execute sql by connection
            return this.sessionFactory.insertSQLExecutor()
                    .returningInsert(this, sqlWrapperList.get(0), resultClass);
        } catch (Exception e) {
            markRollbackOnlyForChildInsert(sqlWrapperList);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }

    @Override
    public int delete(Delete delete) {
        return delete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public int delete(Delete delete, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .update(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public <T> List<T> returningDelete(Delete delete, Class<T> resultClass) {
        return returningDelete(delete, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public <T> List<T> returningDelete(Delete delete, Class<T> resultClass, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .returningUpdate(this, sqlWrapper, resultClass);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public int[] batchDelete(Delete delete) {
        return batchDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public int[] batchDelete(Delete delete, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchUpdate(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public long largeDelete(Delete delete) {
        return largeDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public long largeDelete(Delete delete, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .largeUpdate(this, sqlWrapper);
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
        return batchLargeDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public long[] batchLargeDelete(Delete delete, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchLargeUpdate(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public final SessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public boolean closed() {
        return this.closed;
    }

    @Override
    public void close() throws SessionException {
        try {
            if (this.currentSession) {
                sessionFactory.currentSessionContext().removeCurrentSession(this);
            }
            connection.close();
            this.closed = true;
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
        for (DomainUpdateAdvice advice : this.sessionCache.updateAdvices()) {
            if (!advice.hasUpdate()) {
                continue;
            }
            update(CacheDomainUpdate.build(advice), Visible.ONLY_VISIBLE);
            advice.updateFinish();
        }
    }

    /*################################## blow InnerSession method ##################################*/

    @Override
    public PreparedStatement createStatement(String sql, boolean generatedKey)
            throws SQLException {
        int type;
        if (generatedKey) {
            type = Statement.RETURN_GENERATED_KEYS;
        } else {
            type = Statement.NO_GENERATED_KEYS;
        }
        return connection.prepareStatement(sql, type);
    }

    @Override
    public PreparedStatement createStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }

    @Override
    public PreparedStatement createStatement(String sql, String[] columnNames) throws SQLException {
        return this.connection.prepareStatement(sql, columnNames);
    }

    /*################################## blow InnerTxSession method ##################################*/

    @Override
    public Connection connection() {
        return this.connection;
    }

    @Override
    public void closeTransaction(Transaction transaction) {
        if (this.transaction != transaction) {
            throw new IllegalArgumentException("transaction not match,can't close.");
        }
        if (!TransactionImpl.END_ABLE_SET.contains(transaction.status())) {
            throw new IllegalTransactionStateException("transaction status[%s] not in %s,can't close."
                    , transaction.status(), TransactionImpl.END_ABLE_SET);
        }
        try {
            // reset connection.
            this.connection.setReadOnly(connInitParam.readonly);
            this.connection.setTransactionIsolation(connInitParam.isolation);
            this.connection.setAutoCommit(connInitParam.autoCommit);
            this.transaction = null;
        } catch (SQLException e) {
            throw new TransactionSystemException(e, "army reset connection failure after transaction end.");
        }
    }

    @Override
    public Dialect dialect() {
        return this.dialect;
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

    private void checkSessionForUpdate() {
        if (this.readonly) {
            throw new ReadOnlySessionException("session[%s] is read only ,can't execute update.");
        }
    }

    /*################################## blow private multiInsert method ##################################*/


    private void assertChildDomain() {
        if (this.transaction == null
                || this.transaction.isolation().level < Isolation.READ_COMMITTED.level) {
            throw new DomainUpdateException("Child domain update must in READ_COMMITTED transaction.");
        }
    }

    private void markRollbackOnlyForChildUpdate(SQLWrapper sqlWrapper) {
        if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
            if (this.transaction != null) {
                this.transaction.markRollbackOnly();
            }
        }
    }

    private void markRollbackOnlyForChildInsert(List<SQLWrapper> sqlWrapperList) {
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
                if (this.transaction != null) {
                    this.transaction.markRollbackOnly();
                }
                break;
            }
        }
    }

    private SQLWrapper parseUpdate(Update update, Visible visible) {
        //1. parse update sql
        SQLWrapper sqlWrapper = this.dialect.update(update, visible);
        if (sqlWrapper instanceof ChildSQLWrapper
                || sqlWrapper instanceof ChildBatchSQLWrapper) {
            // 2. assert child update
            assertChildDomain();
        }
        return sqlWrapper;
    }

    private SQLWrapper parseDelete(Delete delete, Visible visible) {
        //1. parse update sql
        SQLWrapper sqlWrapper = this.dialect.delete(delete, visible);
        if (sqlWrapper instanceof ChildSQLWrapper
                || sqlWrapper instanceof ChildBatchSQLWrapper) {
            // 2. assert child update
            assertChildDomain();
        }
        return sqlWrapper;
    }

    private List<SQLWrapper> parseInsert(Insert insert, Visible visible) {
        //1. parse update sql
        List<SQLWrapper> sqlWrapperList = this.dialect.insert(insert, visible);
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
                assertChildDomain();
                break;
            }
        }
        return sqlWrapperList;
    }


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
            Transaction tx = new TransactionImpl(SessionImpl.this, TransactionBuilderImpl.this);
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
