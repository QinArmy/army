package io.army.boot.sync;

import io.army.SessionException;
import io.army.cache.SessionCache;
import io.army.cache.SessionCacheException;
import io.army.cache.UniqueKey;
import io.army.criteria.*;
import io.army.criteria.impl.inner._MultiDML;
import io.army.criteria.impl.inner._SingleDml;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.sync.TmSession;
import io.army.sync.TmSessionFactory;
import io.army.tx.TmTransaction;
import io.army.tx.XaTransactionOption;
import io.army.util.CriteriaUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class is a implementation of {@linkplain TmSession}.
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
final class TmSessionImpl implements InnerTmSession {

    private final InnerTmSessionFactory sessionFactory;

    private final SessionCache sessionCache;

    private final SyncCommitTransactionManager tmTransaction;

    private final boolean current;

    private final Map<Integer, RmSession> rmSessionMap = new HashMap<>();

    /**
     * unmodifiable object
     */
    private final XaTransactionOption transactionOption;


    private boolean closed;

    TmSessionImpl(InnerTmSessionFactory sessionFactory, TmSessionFactoryImpl.SessionBuilderImpl builder) {
        this.sessionFactory = sessionFactory;
        this.current = builder.current();
        this.transactionOption = TmSessionUtils.createXaTransactionOption(builder);
        this.tmTransaction = new SyncCommitTransactionManager(this, this.transactionOption);

        if (sessionFactory.supportSessionCache()) {
            this.sessionCache = sessionFactory.sessionCacheFactory().createSessionCache(this);
        } else {
            this.sessionCache = null;
        }
    }


    @Override
    public <R extends IDomain> R get(TableMeta<R> tableMeta, Object id) {
        return null;
    }

    // @Override
    public <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList, List<Object> valueList) {
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
    public List<Long> batchLargeDelete(Delete delete) {
        return null;
    }

    @Override
    public List<Long> batchLargeDelete(Delete delete, Visible visible) {
        return null;
    }

    @Override
    public <R extends IDomain, F> R getByUnique(TableMeta<R> tableMeta, UniqueFieldMeta<R, F> fieldMeta, F fieldValue) {
        return null;
    }

    @Override
    public <R extends IDomain, F> R getByUnique(TableMeta<R> tableMeta, UniqueFieldMeta<R, F> fieldMeta, F fieldValue, Visible visible) {
        return null;
    }

    @Override
    public <R> R selectOne(Select select, Class<R> resultClass) {
        return null;
    }

    @Override
    public <R> R selectOne(Select select, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public Map<String, Object> selectOneAsMap(Select select) {
        return null;
    }

    @Override
    public Map<String, Object> selectOneAsMap(Select select, Visible visible) {
        return null;
    }

    @Override
    public <R> List<R> select(Select select, Class<R> resultClass) {
        return null;
    }

    @Override
    public List<Map<String, Object>> selectAsMap(Select select) {
        return null;
    }

    @Override
    public List<Map<String, Object>> selectAsMap(Select select, Visible visible) {
        return null;
    }

    // @Override
    public int subQueryInsert(Insert insert) {
        return 0;
    }

    //@Override
    public long subQueryLargeInsert(Insert insert) {
        return 0;
    }

    @Override
    public <R> List<R> returningInsert(Insert insert, Class<R> resultClass) {
        return null;
    }

    @Override
    public int update(Update update) {
        return 0;
    }

    @Override
    public long largeUpdate(Update update) {
        return 0;
    }

    @Override
    public <R> List<R> returningUpdate(Update update, Class<R> resultClass) {
        return null;
    }

    @Override
    public int delete(Delete delete) {
        return 0;
    }

    @Override
    public long largeDelete(Delete delete) {
        return 0;
    }

    @Override
    public <R> List<R> returningDelete(Delete delete, Class<R> resultClass) {
        return null;
    }

    @Override
    public final TmSessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public final TmTransaction sessionTransaction() {
        // return this.tmTransaction;
        return null;
    }


    @Override
    public final boolean readonly() {
        return this.transactionOption.readOnly();
    }

    @Override
    public final boolean closed() {
        return this.closed;
    }

    @Override
    public final boolean hasTransaction() {
        return this.tmTransaction != null;
    }


    @Override
    public final <R extends IDomain> R get(TableMeta<R> tableMeta, Object id, Visible visible) {
        R actualReturn;
        if (this.sessionCache != null) {
            // try obtain cache
            actualReturn = this.sessionCache.getDomain(tableMeta, id);
            if (actualReturn != null) {
                return actualReturn;
            }
        }
        if (TmSessionUtils.notSupportCache(tableMeta)) {
            throw new SessionCacheException("TableMeta[%s]'s id isn't  route key.", tableMeta);
        }
        // 1. create sql
        Select select = CriteriaUtils.createSelectDomainById(tableMeta, id);
        // 2. route rm session and  execute sql
        R domain = this.selectOne(select, tableMeta.javaType(), visible);
        if (domain != null && this.sessionCache != null) {
            // 3. cache
            actualReturn = this.sessionCache.cacheDomainById(tableMeta, domain);
        } else {
            actualReturn = domain;
        }
        return actualReturn;
    }


    // @Override
    public final <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList, final Visible visible) {
        final UniqueKey uniqueKey = new UniqueKey(propNameList, valueList);
        R actualReturn;
        if (this.sessionCache != null) {
            // try obtain cache
            actualReturn = this.sessionCache.getDomain(tableMeta, uniqueKey);
            if (actualReturn != null) {
                return actualReturn;
            }
        }
        if (TmSessionUtils.notSupportCache(tableMeta, propNameList)) {
            throw new SessionCacheException("TableMeta[%s]'s %s don't contains route key.", tableMeta, propNameList);
        }
        // 1. create sql
        Select select = CriteriaUtils.createSelectDomainByUnique(tableMeta, propNameList, valueList);
        // 2. route rm session and  execute sql
        R domain = this.selectOne(select, tableMeta.javaType(), visible);
        if (domain != null && this.sessionCache != null) {
            // 3. cache
            actualReturn = this.sessionCache.cacheDomainByUnique(tableMeta, domain, uniqueKey);
        } else {
            actualReturn = domain;
        }
        return actualReturn;
    }


    @Override
    public final <R> List<R> select(Select select, Class<R> resultClass, final Visible visible) {
//        assertSessionActive();
//
//        _Select innerSelect = (_Select) select;
//        //1. try find route
//        RouteWrapper routeWrapper = DatabaseRouteUtils.findRouteForSelect(innerSelect);
//
//        if (routeWrapper == null) {
//            throw new NotFoundRouteException("Select[%s]not found sharding route.", select);
//        }
//        //2. obtain route index
//        int databaseIndex;
//        if (routeWrapper.routeIndex()) {
//            databaseIndex = routeWrapper.routeIndexValue();
//        } else {
//            databaseIndex = this.sessionFactory.dataSourceRoute(routeWrapper.tableMeta())
//                    .database(routeWrapper.routeKey());
//        }
//        //3. obtain target session and execute
//        return obtainRmSession(databaseIndex)
//                .select(select, resultClass, visible);

        return Collections.emptyList();
    }

    @Override
    public final void valueInsert(Insert insert) {
        this.valueInsert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public final void valueInsert(Insert insert, final Visible visible) {
        assertSessionActive();

        if (insert instanceof _ValuesInsert) {
            _ValuesInsert valuesInsert = (_ValuesInsert) insert;
            if (valuesInsert.domainList().size() == 1) {
                processSingleInsert(valuesInsert, visible);
            } else {
                processMultiInsert(valuesInsert, visible);
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("Insert[%s] not supported by valueInsert method.", insert));
        }
    }


    // @Override
    public final int subQueryInsert(Insert insert, final Visible visible) {
//        assertSessionActive();
//
//        _SubQueryInsert subQueryInsert = (_SubQueryInsert) insert;
//        int routeIndex = subQueryInsert.databaseIndex();
//        if (routeIndex < 0) {
//            throw new NotFoundRouteException("SubQuery insert ,TableMeta[%s] not found data source route."
//                    , subQueryInsert.table());
//        }
//        return obtainRmSession(routeIndex)
//                .subQueryInsert(insert, visible);
        return 0;
    }



    @Override
    public final <R> List<R> returningInsert(Insert insert, Class<R> resultClass, final Visible visible) {
//        assertSessionActive();
//
//        if (!(insert instanceof _ReturningInsert)) {
//            throw new IllegalArgumentException(String.format("Inert[%s] isn't supported by returningInsert.", insert));
//        }
//        _ReturningInsert returningInsert = (_ReturningInsert) insert;
//        DomainWrapper wrapper = returningInsert.wrapper();
//        TableMeta<?> tableMeta = returningInsert.tableMeta();
//        // 1. create required properties value.
//        this.sessionFactory.domainValuesGenerator().createValues(wrapper, returningInsert.migrationData());
//        // 2. route index
//        Object routeKey = DatabaseRouteUtils.findRouteKeyInsert(tableMeta, wrapper);
//        if (routeKey == null) {
//            throw new NotFoundRouteException("Insert TableMeta[%s] not found database route.", tableMeta);
//        }
//        int dataSourceIndex = this.sessionFactory.dataSourceRoute(tableMeta)
//                .database(routeKey);
//
//        // 3. obtain target rm session by data source index.
//        return obtainRmSession(dataSourceIndex)
//                // 4. execute insert sql with domain index set .
//                .returningInsert(insert, resultClass, visible);
        return Collections.emptyList();
    }


    @Override
    public final int update(Update update, final Visible visible) {
        assertSessionActive();

        int updateRow;
        if (update instanceof _SingleDml) {
            updateRow = obtainRmSession(processSingleDml((_SingleDml) update))
                    .update(update, visible);
        } else if (update instanceof _MultiDML) {
            updateRow = obtainRmSession(processMultiDml((_MultiDML) update))
                    .update(update, visible);
        } else {
            throw new IllegalArgumentException(String.format("Update[%s] isn't supported by update method.", update));
        }
        return updateRow;
    }

    @Override
    public final long largeUpdate(Update update, final Visible visible) {
        assertSessionActive();

        long updateRow;
        if (update instanceof _SingleDml) {
            updateRow = obtainRmSession(processSingleDml((_SingleDml) update))
                    .largeUpdate(update, visible);
        } else if (update instanceof _MultiDML) {
            updateRow = obtainRmSession(processMultiDml((_MultiDML) update))
                    .largeUpdate(update, visible);
        } else {
            throw new IllegalArgumentException(String.format("Update[%s] isn't supported by update largeUpdate."
                    , update));
        }
        return updateRow;
    }

    @Override
    public final <R> List<R> returningUpdate(Update update, Class<R> resultClass, final Visible visible) {
        assertSessionActive();

        List<R> list;
        if (update instanceof _SingleDml) {
            list = obtainRmSession(processSingleDml((_SingleDml) update))
                    .returningUpdate(update, resultClass, visible);
        } else if (update instanceof _MultiDML) {
            list = obtainRmSession(processMultiDml((_MultiDML) update))
                    .returningUpdate(update, resultClass, visible);
        } else {
            throw new IllegalArgumentException(String.format("Update[%s] isn't supported by returningUpdate method."
                    , update));
        }
        return list;
    }

    @Override
    public final int delete(Delete delete, Visible visible) {
        assertSessionActive();

        int deleteRow;
        if (delete instanceof _SingleDml) {
            deleteRow = obtainRmSession(processSingleDml((_SingleDml) delete))
                    .delete(delete, visible);
        } else if (delete instanceof _MultiDML) {
            deleteRow = obtainRmSession(processMultiDml((_MultiDML) delete))
                    .delete(delete, visible);
        } else {
            throw new IllegalArgumentException(String.format("Delete[%s] isn't supported by delete method.", delete));
        }
        return deleteRow;
    }

    @Override
    public final long largeDelete(Delete delete, Visible visible) {
        assertSessionActive();

        long deleteRow;
        if (delete instanceof _SingleDml) {
            deleteRow = obtainRmSession(processSingleDml((_SingleDml) delete))
                    .largeDelete(delete, visible);
        } else if (delete instanceof _MultiDML) {
            deleteRow = obtainRmSession(processMultiDml((_MultiDML) delete))
                    .largeDelete(delete, visible);
        } else {
            throw new IllegalArgumentException(String.format("Delete[%s] isn't supported by largeDelete method."
                    , delete));
        }
        return deleteRow;
    }

    @Override
    public final <R> List<R> returningDelete(Delete delete, Class<R> resultClass, final Visible visible) {
        assertSessionActive();

        List<R> list;
        if (delete instanceof _SingleDml) {
            list = obtainRmSession(processSingleDml((_SingleDml) delete))
                    .returningDelete(delete, resultClass, visible);
        } else if (delete instanceof _MultiDML) {
            list = obtainRmSession(processMultiDml((_MultiDML) delete))
                    .returningDelete(delete, resultClass, visible);
        } else {
            throw new IllegalArgumentException(String.format("Delete[%s] isn't supported by returningDelete method."
                    , delete));
        }
        return list;
    }

    @Override
    public final void flush() throws SessionException {

    }

    @Override
    public final void close() throws SessionException {
//        if (this.closed) {
//            return;
//        }
//        if (!TransactionStatus.END_STATUS_SET.contains(this.tmTransaction.status())) {
//            throw new TransactionNotCloseException("Transaction[%s] not close.", this.tmTransaction.name());
//        }
//
//        Map<Integer, SessionException> failSessionMap = null;
//
//        for (Map.Entry<Integer, RmSession> entry : this.rmSessionMap.entrySet()) {
//            try {
//                entry.getValue().close();
//            } catch (SessionException e) {
//                if (failSessionMap == null) {
//                    failSessionMap = new HashMap<>();
//                }
//                failSessionMap.put(entry.getKey(), e);
//            }
//        }
//
//        if (CollectionUtils.isEmpty(failSessionMap)) {
//            if (this.current) {
//                this.sessionFactory.currentSessionContext().removeCurrentSession(this);
//            }
//            this.closed = true;
//        } else {
//            throw new TmSessionCloseException(failSessionMap
//                    , "%s TmSession[%s] close occur error,%s RmSession close error."
//                    , this.sessionFactory, this.transactionOption.name(), failSessionMap.size());
//        }
    }


    /*################################## blow private method ##################################*/


    /**
     * process single insert.
     * <p>
     * Single insert is High frequency operation, this method is for avoid create redundant object.
     * </p>
     *
     * @see #processMultiInsert(_ValuesInsert, Visible)
     */
    private void processSingleInsert(final _ValuesInsert insert, final Visible visible) {

//        final List<ObjectWrapper> wrapperList = insert.domainList();
//        Assert.isTrue(wrapperList.size() == 1, "wrapperList size isn't 1 .");
//        final TableMeta<?> tableMeta = insert.table();
//        ObjectWrapper domainWrapper = wrapperList.get(0);
//        // 1. create required properties value.
//        this.sessionFactory.domainValuesGenerator().createValues(domainWrapper, insert.migrationData());
//        // 2. route index
//        Object routeKey = DatabaseRouteUtils.findRouteKeyInsert(tableMeta, domainWrapper);
//        if (routeKey == null) {
//            throw new NotFoundRouteException("Insert TableMeta[%s] not found database route.", tableMeta);
//        }
//        int dataSourceIndex = this.sessionFactory.dataSourceRoute(tableMeta)
//                .database(routeKey);
//        // 3. obtain target rm session by data source index.
//        obtainRmSession(dataSourceIndex)
//                // 4. execute insert sql with domain index set .
//                .valueInsert((Insert) insert, null, visible);
    }


    /**
     * process multi insert.
     *
     * @see #processSingleInsert(_ValuesInsert, Visible)
     */
    private void processMultiInsert(final _ValuesInsert insert, final Visible visible) {
//
//        final DomainValuesGenerator generator = this.sessionFactory.domainValuesGenerator();
//        final TableMeta<?> tableMeta = insert.table();
//        final List<DomainWrapper> wrapperList = insert.domainList();
//        final int size = wrapperList.size();
//
//        final boolean migrationData = insert.migrationData();
//        final DatabaseRoute route = this.sessionFactory.dataSourceRoute(tableMeta);
//
//        final Map<Integer, Set<Integer>> domainIndexSetMap = new HashMap<>();
//        for (int i = 0; i < size; i++) {
//            // 1. create required properties value.
//            DomainWrapper wrapper = wrapperList.get(i);
//            generator.createValues(wrapper, migrationData);
//            // 2. route target data source index.
//            Object routeKey = DatabaseRouteUtils.findRouteKeyInsert(tableMeta, wrapper);
//            if (routeKey == null) {
//                throw new NotFoundRouteException("Insert TableMeta[%s] index[%s] not found database route."
//                        , tableMeta, i);
//            }
//            int dataSourceIndex = route.database(routeKey);
//            // 3. cache data source index and domain index
//            Set<Integer> domainIndexSet = domainIndexSetMap.computeIfAbsent(dataSourceIndex, k -> new HashSet<>());
//            domainIndexSet.add(i);
//        }
//
//        for (Map.Entry<Integer, Set<Integer>> e : domainIndexSetMap.entrySet()) {
//            // 4. obtain target rm session by data source index.
//            obtainRmSession(e.getKey())
//                    // 5. execute insert sql with domain index set .
//                    .valueInsert((Insert) insert, Collections.unmodifiableSet(e.getValue()), visible);
//        }
    }

    private int processSingleDml(_SingleDml singleDML) throws NotFoundRouteException {
        //1. try find route
//        RouteWrapper routeWrapper = DatabaseRouteUtils.findRouteForSingleDML(singleDML);
//        if (routeWrapper == null) {
//            throw new NotFoundRouteException("Single dml ,TableMeta[%s] not found data source route."
//                    , singleDML.tableMeta());
//        }
//        //2. obtain route index
//        int routeIndex;
//        if (routeWrapper.routeIndex()) {
//            routeIndex = routeWrapper.routeIndexValue();
//        } else {
//            routeIndex = this.sessionFactory.dataSourceRoute(routeWrapper.tableMeta())
//                    .database(routeWrapper.routeKey());
//        }
        return 0;
    }

    private int processMultiDml(_MultiDML multiDML) throws NotFoundRouteException {
//        //1. try find route
//        RouteWrapper routeWrapper = DatabaseRouteUtils.findRouteForMultiDML(multiDML);
//        if (routeWrapper == null) {
//            throw new NotFoundRouteException("Multi dml table list %s not found data source route."
//                    , multiDML.tableWrapperList());
//        }
//        //2. obtain route index
//        int routeIndex;
//        if (routeWrapper.routeIndex()) {
//            routeIndex = routeWrapper.routeIndexValue();
//        } else {
//            routeIndex = this.sessionFactory.dataSourceRoute(routeWrapper.tableMeta())
//                    .database(routeWrapper.routeKey());
//        }
        return 0;
    }

    private void assertSessionActive() {
//        if (this.closed || this.tmTransaction.nonActive()) {
//            throw new SessionUsageException(ErrorCode.SESSION_CLOSED, "TmSession[%s] closed or Transaction[%s] ended."
//                    , transactionOption.name(), this.tmTransaction.name());
//        }
    }


    private RmSession obtainRmSession(int dataSourceIndex) {
        return this.rmSessionMap.computeIfAbsent(dataSourceIndex, this::createRmSession);
    }

    private RmSession createRmSession(int databaseIndex) {
//        List<RmSessionFactory> rmSessionFactoryList = this.sessionFactory.rmSessionFactoryList();
//        if (databaseIndex >= rmSessionFactoryList.size()) {
//            throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR
//                    , "%s TmSession create RmSession occur error,databaseIndex[%s] error."
//                    , this.sessionFactory, databaseIndex);
//        }
//        RmSessionFactory rmSessionFactory = rmSessionFactoryList.get(databaseIndex);
//        if (rmSessionFactory == null) {
//            throw new CreateSessionException(ErrorCode.SESSION_CREATE_ERROR
//                    , "%s TmSession create RmSession occur error,databaseIndex[%s] error."
//                    , this.sessionFactory, databaseIndex);
//        }
//        RmSession rmSession = rmSessionFactory.build(this.transactionOption);
//        // rm xa transaction add to tm and start rm xa transaction
//        this.tmTransaction.addXaTransaction(this, rmSession.sessionTransaction());
        return null;
    }


}
