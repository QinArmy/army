package io.army.boot;

import io.army.SessionException;
import io.army.TmSession;
import io.army.TmSessionFactory;
import io.army.beans.DomainWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.cache.SessionCache;
import io.army.cache.UniqueKey;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.dialect.TransactionOption;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.sharding.DataSourceRoute;
import io.army.sharding.DataSourceRouteUtils;
import io.army.sharding.RouteWrapper;
import io.army.tx.TmTransaction;
import io.army.tx.TransactionNotCloseException;
import io.army.tx.TransactionStatus;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.CriteriaUtils;
import io.army.wrapper.SQLWrapper;

import java.util.*;


/**
 * This class is a implementation of {@linkplain TmSession}.
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
final class TmSessionImpl extends AbstractSyncApiSession implements TmSession {

    private static final EnumSet<TransactionStatus> TX_END_STATUS = EnumSet.of(
            TransactionStatus.COMMITTED
            , TransactionStatus.ROLLED_BACK);


    private final InnerTmSessionFactory sessionFactory;

    private final SessionCache sessionCache;

    private final boolean readonly;

    private final boolean currentSession;

    private final TmTransaction tmTransaction;

    private final Map<String, RmSession> rmSessionMap = new HashMap<>();

    private boolean closed;

    TmSessionImpl(InnerTmSessionFactory sessionFactory, TransactionOption option, boolean currentSession) {
        this.sessionFactory = sessionFactory;
        this.currentSession = currentSession;
        this.readonly = sessionFactory.readonly();
        this.tmTransaction = new SyncCommitTransactionManager(this, option);

        this.sessionCache = null;
    }

    @Override
    public TmSessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public TmTransaction sessionTransaction() {
        return this.tmTransaction;
    }


    @Override
    public boolean readonly() {
        return this.readonly;
    }

    @Override
    public boolean closed() {
        return this.closed;
    }

    @Override
    public boolean hasTransaction() {
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


    @Override
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
        InnerSelect innerSelect = (InnerSelect) select;
        //1. try find route
        RouteWrapper routeWrapper = DatabaseRouteUtils.findRouteForSelect(innerSelect.tableWrapperList()
                , innerSelect.predicateList(), true);

        if (routeWrapper == null) {
            throw new NotFoundRouteException("not found sharding route.");
        }
        //2. obtain route index
        int routeIndex;
        if (routeWrapper.routeIndex()) {
            routeIndex = routeWrapper.routeIndexValue();
        } else {
            routeIndex = this.sessionFactory.dataSourceRoute(routeWrapper.tableMeta())
                    .dataSourceRoute(routeWrapper.routeKey());
        }
        //3. obtain target session and execute
        return obtainRmSession(routeIndex)
                .select(select, resultClass, visible);
    }

    @Override
    public final void valueInsert(Insert insert, final Visible visible) {
        if (insert instanceof InnerValuesInsert) {
            InnerValuesInsert valuesInsert = (InnerValuesInsert) insert;
            if (valuesInsert.valueList().size() == 1) {
                processSingleInsert(valuesInsert, visible);
            } else {
                processMultiInsert(valuesInsert, visible);
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("Insert[%s] not supported by valueInsert method.", insert));
        }
    }

    @Override
    public final int subQueryInsert(Insert insert, Visible visible) {
        InnerSubQueryInsert subQueryInsert = (InnerSubQueryInsert) insert;
        int routeIndex = subQueryInsert.dataSourceIndex();
        if (routeIndex < 0) {
            throw new NotFoundRouteException("SubQuery insert ,TableMeta[%s] not found data source route."
                    , subQueryInsert.tableMeta());
        }
        return obtainRmSession(routeIndex)
                .subQueryInsert(insert, visible);
    }

    @Override
    public final long largeSubQueryInsert(Insert insert, Visible visible) {
        InnerSubQueryInsert subQueryInsert = (InnerSubQueryInsert) insert;
        int routeIndex = subQueryInsert.dataSourceIndex();
        if (routeIndex < 0) {
            throw new NotFoundRouteException("SubQuery insert ,TableMeta[%s] not found data source route."
                    , subQueryInsert.tableMeta());
        }
        return obtainRmSession(routeIndex)
                .largeSubQueryInsert(insert, visible);
    }

    @Override
    public final <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible) {
        //TODO do returninng insert route
        throw new UnsupportedOperationException();
    }


    @Override
    public final int update(Update update, Visible visible) {
        //TODO 考虑 mysql 的 multi table update 和 postgre 的可 join update.
        return obtainRmSession(processDml((InnerSingleDML) update))
                .update(update, visible);
    }

    @Override
    public final long largeUpdate(Update update, Visible visible) {
        //TODO 考虑 mysql 的 multi table delete 和 postgre 的可 join delete.
        return obtainRmSession(processDml((InnerSingleDML) update))
                .largeUpdate(update, visible);
    }

    @Override
    public final <R> List<R> returningUpdate(Update update, Class<R> resultClass, Visible visible) {
        //TODO do returninng update route
        throw new UnsupportedOperationException();
    }

    @Override
    public final Map<Integer, Integer> batchUpdate(Update update, final Visible visible) {
        //TODO 考虑 mysql 的 multi table update 和 postgre 的可 join update.
        Map<Integer, Integer> batchResultMap;
        batchResultMap = doBatchSingleDml((InnerBatchSingleDML) update, Integer.class, visible);
        if (batchResultMap.isEmpty()) {
            throw new NotFoundRouteException("Batch update[%s] not found route.", update);
        }
        return batchResultMap;
    }

    @Override
    public final Map<Integer, Long> batchLargeUpdate(Update update, Visible visible) {
        //TODO 考虑 mysql 的 multi table update 和 postgre 的可 join update.
        Map<Integer, Long> batchResultMap;
        batchResultMap = doBatchSingleDml((InnerBatchSingleDML) update, Long.class, visible);
        if (batchResultMap.isEmpty()) {
            throw new NotFoundRouteException("Batch update[%s] not found route.", update);
        }
        return batchResultMap;
    }

    @Override
    public final int delete(Delete delete, Visible visible) {
        //TODO 考虑 mysql 的 multi table delete 和 postgre 的可 join delete.
        return obtainRmSession(processDml((InnerSingleDML) delete))
                .delete(delete, visible);
    }

    @Override
    public final long largeDelete(Delete delete, Visible visible) {
        //TODO 考虑 mysql 的 multi table delete 和 postgre 的可 join delete.
        return obtainRmSession(processDml((InnerSingleDML) delete))
                .delete(delete, visible);
    }

    @Override
    public final <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible) {
        //TODO do returninng delete route
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<Integer, Integer> batchDelete(Delete delete, Visible visible) {
        //TODO 考虑 mysql 的 multi table delete 和 postgre 的可 join delete.
        Map<Integer, Integer> batchResultMap;
        batchResultMap = doBatchSingleDml((InnerBatchSingleDML) delete, Integer.class, visible);
        if (batchResultMap.isEmpty()) {
            throw new NotFoundRouteException("Batch delete[%s] not found route.", delete);
        }
        return batchResultMap;
    }

    @Override
    public Map<Integer, Long> batchLargeDelete(Delete delete, Visible visible) {
        //TODO 考虑 mysql 的 multi table delete 和 postgre 的可 join delete.
        Map<Integer, Long> batchResultMap;
        batchResultMap = doBatchSingleDml((InnerBatchSingleDML) delete, Long.class, visible);
        if (batchResultMap.isEmpty()) {
            throw new NotFoundRouteException("Batch delete[%s] not found route.", delete);
        }
        return batchResultMap;
    }

    @Override
    public void close() throws SessionException {
        if (this.closed) {
            return;
        }
        if (!TX_END_STATUS.contains(this.tmTransaction.status())) {
            throw new TransactionNotCloseException("Transaction[%s] not close.", this.tmTransaction.name());
        }
        Map<RmSession, SessionException> failSessionMap = null;
        for (RmSession session : this.rmSessionMap.values()) {
            try {
                session.close();
            } catch (SessionException e) {
                if (failSessionMap == null) {
                    failSessionMap = new HashMap<>();
                }
                failSessionMap.put(session, e);
            }
        }
        if (!CollectionUtils.isEmpty(failSessionMap)) {
            //TODO throw exception
        }
        this.closed = true;
    }

    @Override
    public void flush() throws SessionException {

    }



    /*################################## blow private method ##################################*/

    private RmSession routeRmSession(Insert insert) {
        throw new UnsupportedOperationException();
    }


    /**
     * process single insert.
     * <p>
     * Single insert is High frequency operation, this method is for avoid create redundant object.
     * </p>
     *
     * @see #processMultiInsert(InnerValuesInsert, Visible)
     */
    private void processSingleInsert(final InnerValuesInsert insert, final Visible visible) {

        final List<IDomain> domainList = insert.valueList();
        Assert.isTrue(domainList.size() == 1, "domain size isn't 1 .");
        final TableMeta<?> tableMeta = insert.tableMeta();

        // 1. create required properties value.
        ReadonlyWrapper wrapper = this.sessionFactory.domainValuesGenerator().createValues(tableMeta
                , domainList.get(0)
                , insert.migrationData());
        // 2. route target data source index.
        int dataSourceIndex = this.sessionFactory.dataSourceRoute(tableMeta)
                .dataSourceRoute(obtainRouteKeyValueForInsert(tableMeta, wrapper, true));
        // 3. obtain target rm session by data source index.
        obtainRmSession(dataSourceIndex)
                // 4. execute insert sql with domain index set .
                .valueInsert((Insert) insert, null, visible);
    }

    /**
     * process multi insert.
     *
     * @see #processSingleInsert(InnerValuesInsert, Visible)
     */
    private void processMultiInsert(final InnerValuesInsert insert, final Visible visible) {

        final DomainValuesGenerator generator = this.sessionFactory.domainValuesGenerator();
        final TableMeta<?> tableMeta = insert.tableMeta();
        final List<IDomain> domainList = insert.valueList();
        final int size = domainList.size();

        final boolean migrationData = insert.migrationData();
        final DataSourceRoute route = this.sessionFactory.dataSourceRoute(tableMeta);

        final Map<Integer, Set<Integer>> domainIndexSetMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            // 1. create required properties value.
            DomainWrapper wrapper = generator.createValues(tableMeta, domainList.get(i), migrationData);
            // 2. route target data source index.
            int dataSourceIndex = route.dataSourceRoute(obtainRouteKeyValueForInsert(tableMeta, wrapper, true));
            // 3. cache data source index and domain index
            Set<Integer> domainIndexSet = domainIndexSetMap.computeIfAbsent(dataSourceIndex, k -> new HashSet<>());
            domainIndexSet.add(i);
        }

        for (Map.Entry<Integer, Set<Integer>> e : domainIndexSetMap.entrySet()) {
            // 4. obtain target rm session by data source index.
            obtainRmSession(e.getKey())
                    // 5. execute insert sql with domain index set .
                    .valueInsert((Insert) insert, Collections.unmodifiableSet(e.getValue()), visible);
        }
    }

    private int processDml(InnerSingleDML singleDML) throws NotFoundRouteException {
        //1. try find route
        RouteWrapper routeWrapper = DatabaseRouteUtils.findRouteForSingleDML(singleDML, true);
        if (routeWrapper == null) {
            throw new NotFoundRouteException("Single update ,TableMeta[%s] not found data source route."
                    , singleDML.tableMeta());
        }
        //2. obtain route index
        int routeIndex;
        if (routeWrapper.routeIndex()) {
            routeIndex = routeWrapper.routeIndexValue();
        } else {
            routeIndex = this.sessionFactory.dataSourceRoute(routeWrapper.tableMeta())
                    .dataSourceRoute(routeWrapper.routeKey());
        }
        return routeIndex;
    }

    /*################################## blow private method about single update ##################################*/

    /**
     * @return a unmodifiable map. key : index of {@linkplain InnerBatchSingleDML#namedParamList()}
     * ,value : batch update rows of named param. if empty ,then not found route.
     * @see #batchUpdate(Update, Visible)
     * @see io.army.boot.UpdateSQLExecutor#batchUpdate(InnerSession, SQLWrapper, Class, boolean)
     */
    private <V extends Number> Map<Integer, V> doBatchSingleDml(InnerBatchSingleDML dml, Class<V> valueType
            , final Visible visible) {
        DataSourceRoute router = this.sessionFactory.dataSourceRoute(dml.tableMeta());
        Map<Integer, V> batchResultMap;
        // 1. try find route from non named param predicates
        batchResultMap = doBatchSingleDmlWithNonNamedPredicates(dml, router, valueType, visible);
        if (!batchResultMap.isEmpty()) {
            return batchResultMap;
        }
        //2. step 1 failure, try find route from named param predicates
        batchResultMap = doBatchSingleDmlWithNamedPredicate(dml, router, valueType, visible);
        if (!batchResultMap.isEmpty()) {
            return batchResultMap;
        }
        //3. step 2 failure, try find route from table wrapper info.
        int dataSourceIndex = dml.dataSourceIndex();
        if (dataSourceIndex < 0) {
            // not found route
            batchResultMap = Collections.emptyMap();
        } else {
            // step 3 success.
            batchResultMap = executeSingleDml(dml, dataSourceIndex, null, valueType, visible);

        }
        return batchResultMap;
    }

    /**
     * @return a unmodifiable map, key : index of {@linkplain InnerBatchSingleDML#namedParamList()}
     * ,value : batch update rows of named param. if empty ,then not found route.
     * @see #doBatchSingleDml(InnerBatchSingleDML, Class, Visible)
     * @see io.army.boot.UpdateSQLExecutor#batchUpdate(InnerSession, SQLWrapper, Class, boolean)
     */
    private <V extends Number> Map<Integer, V> doBatchSingleDmlWithNonNamedPredicates(InnerBatchSingleDML dml
            , DataSourceRoute route, Class<V> valueType, final Visible visible) {
        //  try find route from non named param predicate
        Object routeKey = DatabaseRouteUtils.findRouteFromNonNamedPredicates(dml, true);
        Map<Integer, V> batchResultMap;
        if (routeKey == null) {
            // not found route
            batchResultMap = Collections.emptyMap();
        } else {
            batchResultMap = executeSingleDml(dml, route.dataSourceRoute(routeKey), null, valueType, visible);
        }
        return batchResultMap;
    }


    /**
     * @return a unmodifiable map, key : index of {@linkplain InnerBatchSingleDML#namedParamList()}
     * ,value : batch update rows of named param. if empty ,then not found route.
     * @see #doBatchSingleDml(InnerBatchSingleDML, Class, Visible)
     * @see io.army.boot.UpdateSQLExecutor#batchUpdate(InnerSession, SQLWrapper, Class, boolean)
     */
    private <V extends Number> Map<Integer, V> doBatchSingleDmlWithNamedPredicate(InnerBatchSingleDML dml
            , DataSourceRoute route, Class<V> valueType, final Visible visible) {
        // try find route form named param predicate
        Map<Integer, Set<Integer>> routeIndexSetMap = DatabaseRouteUtils.findRouteFromNamedPredicates(dml, route, true);

        Map<Integer, V> batchResultMap;
        if (routeIndexSetMap.isEmpty()) {
            // not found route
            batchResultMap = Collections.emptyMap();
        } else {
            batchResultMap = new HashMap<>();
            for (Map.Entry<Integer, Set<Integer>> e : routeIndexSetMap.entrySet()) {
                // put all to resultMap
                batchResultMap.putAll(
                        // obtain target session and execute
                        executeSingleDml(dml, e.getKey(), e.getValue(), valueType, visible)
                );
            }
            batchResultMap = Collections.unmodifiableMap(batchResultMap);
        }
        return batchResultMap;
    }

    /**
     * @return a unmodifiable map, key : index of {@linkplain InnerBatchSingleDML#namedParamList()}
     * ,value : batch update rows of named param.
     * @see #doBatchSingleDmlWithNamedPredicate(InnerBatchSingleDML, DataSourceRoute, Class, Visible)
     * @see #doBatchSingleDmlWithNonNamedPredicates(InnerBatchSingleDML, DataSourceRoute, Class, Visible)
     * @see #doBatchSingleDml(InnerBatchSingleDML, Class, Visible)
     * @see io.army.boot.UpdateSQLExecutor#batchUpdate(InnerSession, SQLWrapper, Class, boolean)
     */
    private <V extends Number> Map<Integer, V> executeSingleDml(InnerBatchSingleDML dml, int dataSourceIndex
            , @Nullable Set<Integer> paramIndexSet, Class<V> valueType, final Visible visible) {
        RmSession session = obtainRmSession(dataSourceIndex);
        Map<Integer, V> batchResultMap;
        if (dml instanceof Update) {
            batchResultMap = session.batchUpdate((Update) dml, paramIndexSet, valueType, visible);
        } else if (dml instanceof Delete) {
            batchResultMap = session.batchDelete((Delete) dml, paramIndexSet, valueType, visible);
        } else {
            throw new IllegalArgumentException(
                    String.format("dml[%s] isn't Update object or Delete object.", dml));
        }
        return batchResultMap;
    }


    private RmSession obtainRmSession(int dataSourceIndex) {
        throw new UnsupportedOperationException();
    }


}
