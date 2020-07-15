package io.army.boot;

import io.army.RmSession;
import io.army.SessionException;
import io.army.TmSession;
import io.army.TmSessionFactory;
import io.army.cache.SessionCache;
import io.army.cache.UniqueKey;
import io.army.criteria.*;
import io.army.dialect.TransactionOption;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.tx.TmTransaction;
import io.army.tx.TransactionNotCloseException;
import io.army.tx.TransactionStatus;
import io.army.util.CriteriaUtils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class TmSessionImpl extends AbstractGenericSession implements TmSession {

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
    public <R> R selectOne(Select select, Class<R> resultClass, Visible visible) {
        return routeRmSession(select)
                .selectOne(select, resultClass, visible);
    }


    @Override
    public <R> List<R> select(Select select, Class<R> resultClass, Visible visible) {
        return routeRmSession(select)
                .select(select, resultClass, visible);
    }


    @Override
    public int update(Update update, Visible visible) {
        return routeRmSession(update)
                .update(update, visible);
    }


    @Override
    public void updateOne(Update update, Visible visible) {
        routeRmSession(update)
                .updateOne(update, visible);
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
    public void insert(Insert insert, Visible visible) {
        routeRmSession(insert)
                .insert(insert, visible);
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
        return routeRmSession(insert)
                .returningInsert(insert, resultClass, visible);
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
        for (RmSession session : rmSessionMap.values()) {
            session.close();
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

    private RmSession routeRmSession(Update update) {
        throw new UnsupportedOperationException();
    }

    private RmSession routeRmSession(Delete delete) {
        throw new UnsupportedOperationException();
    }

    private RmSession routeRmSession(Select select) {
        throw new UnsupportedOperationException();
    }
}
