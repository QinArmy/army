package io.army.boot;

import io.army.SessionException;
import io.army.TmSession;
import io.army.TmSessionFactory;
import io.army.beans.DomainWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.cache.SessionCache;
import io.army.cache.UniqueKey;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerValuesInsert;
import io.army.dialect.TransactionOption;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.sharding.DataSourceRoute;
import io.army.tx.TmTransaction;
import io.army.tx.TransactionNotCloseException;
import io.army.tx.TransactionStatus;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.CriteriaUtils;

import java.util.*;


/**
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
    public <R extends IDomain> R get(TableMeta<R> tableMeta, Object id, Visible visible) {
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
        R domain = routeRmSession(select)
                .selectOne(select, tableMeta.javaType(), visible);
        if (domain != null && this.sessionCache != null) {
            // 3. cache
            actualReturn = this.sessionCache.cacheDomainById(tableMeta, domain);
        } else {
            actualReturn = domain;
        }
        return actualReturn;
    }


    @Override
    public <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible) {
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
        R domain = routeRmSession(select)
                .selectOne(select, tableMeta.javaType(), visible);
        if (domain != null && this.sessionCache != null) {
            // 3. cache
            actualReturn = this.sessionCache.cacheDomainByUnique(tableMeta, domain, uniqueKey);
        } else {
            actualReturn = domain;
        }
        return actualReturn;
    }

    @Override
    public <R> List<R> select(Select select, Class<R> resultClass, Visible visible) {
        return routeRmSession(select)
                .select(select, resultClass, visible);
    }

    @Override
    public void valueInsert(Insert insert, final Visible visible) {
        if (insert instanceof InnerValuesInsert) {
            InnerValuesInsert valuesInsert = (InnerValuesInsert) insert;
            if (valuesInsert.valueList().size() == 1) {
                processSingleInsert(valuesInsert, visible);
            } else {
                processMultiInsert(valuesInsert, visible);
            }
        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] not supported by valueInsert method.", insert));
        }
    }

    @Override
    public int subQueryInsert(Insert insert, Visible visible) {
        return routeRmSession(insert)
                .subQueryInsert(insert, visible);
    }

    @Override
    public long subQueryLargeInsert(Insert insert, Visible visible) {
        return routeRmSession(insert)
                .subQueryLargeInsert(insert, visible);
    }

    @Override
    public <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible) {
        // TODO if have with clause.
        throw new UnsupportedOperationException();
    }


    @Override
    public int update(Update update, Visible visible) {
        return routeRmSession(update)
                .update(update, null, visible);
    }

    @Override
    public <R> List<R> returningUpdate(Update update, Class<R> resultClass, Visible visible) {
        return routeRmSession(update)
                .returningUpdate(update, resultClass, visible);
    }

    @Override
    public int[] batchUpdate(Update update, Visible visible) {
        return routeRmSession(update)
                .batchUpdate(update, visible);
    }

    @Override
    public long largeUpdate(Update update, Visible visible) {
        return routeRmSession(update)
                .largeUpdate(update, visible);
    }

    @Override
    public long[] batchLargeUpdate(Update update, Visible visible) {
        return routeRmSession(update)
                .batchLargeUpdate(update, visible);
    }

    @Override
    public int delete(Delete delete, Visible visible) {
        return routeRmSession(delete)
                .delete(delete, visible);
    }

    @Override
    public <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible) {
        return routeRmSession(delete)
                .returningDelete(delete, resultClass, visible);
    }

    @Override
    public int[] batchDelete(Delete delete, Visible visible) {
        return routeRmSession(delete)
                .batchDelete(delete, visible);
    }

    @Override
    public long largeDelete(Delete delete, Visible visible) {
        return routeRmSession(delete)
                .largeDelete(delete, visible);
    }

    @Override
    public long[] batchLargeDelete(Delete delete, Visible visible) {
        return routeRmSession(delete)
                .batchLargeDelete(delete, visible);
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

    private RmSession routeRmSession(Update update) {
        throw new UnsupportedOperationException();
    }

    private RmSession routeRmSession(Delete delete) {
        throw new UnsupportedOperationException();
    }

    private RmSession routeRmSession(Select select) {
        throw new UnsupportedOperationException();
    }

    private RmSession obtainRmSession(int dataSourceIndex) {
        throw new UnsupportedOperationException();
    }


}
