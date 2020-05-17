package io.army.boot;

import io.army.*;
import io.army.aop.DomainUpdateAdvice;
import io.army.beans.DomainWrapper;
import io.army.beans.PropertyAccessorFactory;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Dialect;
import io.army.dialect.TransactionOption;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.MappingMode;
import io.army.meta.TableMeta;
import io.army.tx.*;
import io.army.util.Assert;
import io.army.util.CriteriaUtils;
import io.army.util.Pair;
import io.army.wrapper.BatchSQLWrapper;
import io.army.wrapper.SQLWrapper;
import io.army.wrapper.SelectSQLWrapper;
import io.army.wrapper.SimpleSQLWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.TransactionalException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

final class SessionImpl implements InnerSession, InnerTxSession {

    private static final Logger LOG = LoggerFactory.getLogger(SessionImpl.class);


    private final InnerSessionFactory sessionFactory;

    private final Connection connection;

    private final ConnInitParam connInitParam;

    private final boolean readonly;

    private final boolean currentSession;

    private final Dialect dialect;

    private final Map<Object, Object> cacheById;

    private final Map<UniqueKey, Object> cacheByUnique;

    private final Map<Object, UniqueKey> idUniqueMap;

    private final Map<Object, DomainUpdateAdvice> adviceCacheById;


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
            this.cacheById = new HashMap<>();
            this.cacheByUnique = new HashMap<>();
            this.idUniqueMap = new HashMap<>();
            this.adviceCacheById = new HashMap<>();
        } else {
            this.cacheById = Collections.emptyMap();
            this.cacheByUnique = Collections.emptyMap();
            this.idUniqueMap = Collections.emptyMap();
            this.adviceCacheById = Collections.emptyMap();
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


    @Override
    public <T extends IDomain> void save(T domain) {
        @SuppressWarnings("unchecked")
        Class<T> javaType = (Class<T>) domain.getClass();
        TableMeta<T> tableMeta = this.sessionFactory.tableMeta(javaType);
        if (tableMeta == null) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "domain[%s] not load TableMeta.", javaType.getName());
        }
        // 1. create sql
        Insert insert = CriteriaUtils.createSingleInsert(tableMeta, domain);
        // 2. execute insert sql
        this.insert(insert, Visible.ONLY_VISIBLE);

    }

    @Nullable
    @Override
    public final <T extends IDomain> T get(TableMeta<T> tableMeta, Object id) {
        return get(tableMeta, id, Visible.ONLY_VISIBLE);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends IDomain> T get(TableMeta<T> tableMeta, final Object id, Visible visible) {
        // 1. create sql
        Select select = CriteriaUtils.createSelectDomainById(tableMeta, id);
        // 2. execute sql
        T domain = this.selectOne(select, tableMeta.javaType(), visible);
        T actualReturn = domain;
        if (this.sessionFactory.supportSessionCache()) {
            // 3. cache
            DomainWrapper domainWrapper = PropertyAccessorFactory.forDomainPropertyAccess(domain, tableMeta);
            Pair<Object, DomainUpdateAdvice> pair = this.sessionFactory.domainProxyFactory()
                    .createDomainProxy(domainWrapper);
            actualReturn = (T) pair.getFirst();
            this.cacheById.put(id, actualReturn);
            this.adviceCacheById.put(id, pair.getSecond());
        }
        return actualReturn;
    }

    @Nullable
    @Override
    public <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        return getByUnique(tableMeta, propNameList, valueList, Visible.ONLY_VISIBLE);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible) {
        final UniqueKey uniqueKey = new UniqueKey(propNameList, valueList);
        T actualReturn = (T) this.cacheByUnique.get(uniqueKey);

        // 1. create sql
        Select select = CriteriaUtils.createSelectDomainByUnique(tableMeta, propNameList, valueList);
        // 2. execute sql
        T domain = this.selectOne(select, tableMeta.javaType(), visible);

        actualReturn = domain;
        if (this.sessionFactory.supportSessionCache()) {
            // 3. cache
            DomainWrapper domainWrapper = PropertyAccessorFactory.forDomainPropertyAccess(domain, tableMeta);
            Pair<Object, DomainUpdateAdvice> pair = this.sessionFactory.domainProxyFactory()
                    .createDomainProxy(domainWrapper);
            actualReturn = (T) pair.getFirst();

            final Object id = domainWrapper.getPropertyValue(tableMeta.id().propertyName());

            this.cacheById.put(id, actualReturn);
            this.adviceCacheById.put(id, pair.getSecond());
            this.cacheByUnique.put(uniqueKey, actualReturn);
            this.idUniqueMap.put(id, uniqueKey);
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

        List<SelectSQLWrapper> wrapperList = this.dialect.select(select, visible);

        if (wrapperList.size() != 1) {
            // never this
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "dialect parse error.");
        }
        final SelectSQLExecutor executor = this.sessionFactory.selectSQLExecutor();

        List<T> resultList;
        // execute sql and extract result
        resultList = executor.select(this, wrapperList.get(0), resultClass);
        ((InnerSQL) select).clear();
        return resultList;
    }

    @Override
    public List<Integer> update(Update update) {
        return update(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public List<Integer> update(Update update, Visible visible) {
        List<SimpleSQLWrapper> sqlWrapperList = sessionFactory.dialect().update(update, visible);
        for (SimpleSQLWrapper wrapper : sqlWrapperList) {
            LOG.info("wrapper:{}", wrapper);
        }
        return Collections.emptyList();
    }

    @Override
    public void insert(Insert insert) {
        insert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public void insert(Insert insert, final Visible visible) {
        //firstly, check
        checkTransactionForInsert(insert);

        final boolean insertChild = ((InnerInsert) insert).tableMeta().mappingMode() == MappingMode.CHILD;

        try {
            if (insert instanceof InnerValuesInsert) {
                executeValuesInsert((InnerValuesInsert) insert, visible);
            } else if (insert instanceof InnerSubQueryInsert) {
                executeSubQueryInsert((InnerSubQueryInsert) insert, visible);
            } else {
                throw new IllegalStatementException(insert);
            }
        } catch (ArmyRuntimeException e) {
            if (insertChild && this.transaction != null) {
                this.transaction.markRollbackOnly();
            }
            throw e;
        } catch (Throwable e) {
            if (insertChild && this.transaction != null) {
                this.transaction.markRollbackOnly();
            }
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e, e.getMessage());
        }
        // finally, clear
        ((InnerInsert) insert).clear();
    }

    @Override
    public void delete(Delete delete) {

    }

    @Override
    public void delete(Delete delete, Visible visible) {

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
        // no-op
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

    private void checkTransactionForInsert(Insert insert) {
        checkSessionForUpdate();
        if (!(insert instanceof InnerInsert)) {
            throw new IllegalArgumentException(String.format("multiInsert[%S] is error instance.", insert));
        }
        InnerInsert innerInsert = (InnerInsert) insert;
        if (innerInsert.tableMeta() instanceof ChildTableMeta) {
            if (this.transaction == null
                    || this.transaction.isolation().level < Isolation.READ_COMMITTED.level) {
                throw new DenyBatchInsertException(
                        "ChildTableMeta multiInsert must be executed in transaction with READ_COMMITTED(or +).");
            }
        }

    }

    /*################################## blow private multiInsert method ##################################*/

    private void executeSubQueryInsert(InnerSubQueryInsert insert, final Visible visible) {
        /*List<SimpleSQLWrapper> wrapperList = this.dialect.multiInsert((Insert) multiInsert, visible);
        InsertSQLExecutor.build().multiInsert(this, wrapperList);*/
    }

    private void executeValuesInsert(InnerValuesInsert insert, final Visible visible) {

        List<Integer> insertedDomainList;
        if (insert instanceof InnerBatchInsert) {
            insertedDomainList = executeBatchInsert((InnerBatchInsert) insert, visible);
        } else if (insert instanceof InnerGenericInsert) {
            insertedDomainList = executeGenericInsert((InnerGenericInsert) insert, visible);
        } else {
            throw new IllegalStatementException((SQLStatement) insert);
        }

        final int domainCount = insert.valueList().size();
        if (insertedDomainList.size() != domainCount) {
            throw new InsertRowsNotMatchException("actual multiInsert domain count[%s] and domain count[%s] not match."
                    , insertedDomainList.size(), domainCount);
        }
    }

    private List<Integer> executeBatchInsert(InnerBatchInsert insert, final Visible visible
            /* , List<DomainInterceptor> parentInterceptors, List<DomainInterceptor> interceptors*/) {
        // 1. parse batch insert sql
        List<BatchSQLWrapper> wrapperList = this.dialect.batchInsert((Insert) insert, visible);

       /* if (!parentInterceptors.isEmpty() || !interceptors.isEmpty()) {
            invokeBeforePersist(wrapperList, parentInterceptors, interceptors);
        }*/

        return this.sessionFactory.insertSQLExecutor().batchInsert(this, wrapperList);
    }

    /*private void invokeBeforePersist(List<DomainBatchSQLWrapper> wrapperList
            , List<DomainInterceptor> parentInterceptors, List<DomainInterceptor> interceptors) {

        for (DomainBatchSQLWrapper wrapper : wrapperList) {
            TableMeta<?> parentMeta = null;
            if (wrapper.tableMeta() instanceof ChildTableMeta) {
                parentMeta = ((ChildTableMeta<?>) wrapper.tableMeta()).parentMeta();
            }
            for (BeanWrapper beanWrapper : wrapper.beanWrapperList()) {
                if (parentMeta != null) {
                    for (DomainInterceptor interceptor : parentInterceptors) {

                        interceptor.beforeInsert(parentMeta, beanWrapper.getReadonlyWrapper()
                                , this.sessionFactory.proxySession());
                    }
                }

                for (DomainInterceptor interceptor : interceptors) {

                    interceptor.beforeInsert(wrapper.tableMeta(), beanWrapper.getReadonlyWrapper()
                            , this.sessionFactory.proxySession());
                }

            }
        }


    }*/

    private List<Integer> executeGenericInsert(InnerGenericInsert insert, final Visible visible) {
        List<SQLWrapper> wrapperList = this.dialect.insert((Insert) insert, visible);
        return this.sessionFactory.insertSQLExecutor().multiInsert(this, wrapperList);
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

    private static class UniqueKey {

        private final List<String> propNameList;

        private final List<Object> valueList;

        private final int hash;

        private UniqueKey(List<String> propNameList, List<Object> valueList) {
            this.propNameList = Collections.unmodifiableList(propNameList);
            this.valueList = Collections.unmodifiableList(valueList);
            this.hash = Objects.hash(propNameList, valueList);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof UniqueKey)) {
                return false;
            }
            UniqueKey key = (UniqueKey) obj;
            return this.propNameList.equals(key.propNameList)
                    && this.valueList.equals(key.valueList);
        }
    }
}
