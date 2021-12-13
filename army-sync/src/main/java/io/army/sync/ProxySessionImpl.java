package io.army.sync;

import io.army.context.spi.CurrentSessionContext;
import io.army.criteria.Delete;
import io.army.criteria.Select;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.tx.GenericTransaction;
import io.army.tx.NoSessionTransactionException;

import java.util.List;
import java.util.Map;

class ProxySessionImpl extends AbstractProxySyncSession implements ProxySession {

    private final SessionFactory sessionFactory;


    ProxySessionImpl(SessionFactory sessionFactory, CurrentSessionContext sessionContext) {
        super(sessionContext);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public SessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public ArmyEnvironment environment() {
        return this.sessionFactory.environment();
    }

    @Nullable
    @Override
    public <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass) {
        return this.sessionFactory.tableMeta(domainClass);
    }

    @Override
    public GenericTransaction sessionTransaction() throws NoSessionTransactionException {
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
    public List<Map<String, Object>> selectAsMap(Select select) {
        return null;
    }

    @Override
    public List<Map<String, Object>> selectAsMap(Select select, Visible visible) {
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
    public <R extends IDomain, F> R getByUnique(TableMeta<R> tableMeta, UniqueFieldMeta<R, F> fieldMeta, F fieldValue) {
        return null;
    }

    @Override
    public <R extends IDomain, F> R getByUnique(TableMeta<R> tableMeta, UniqueFieldMeta<R, F> fieldMeta, F fieldValue, Visible visible) {
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

    private Session obtainSessionForBatch() {
        return (Session) (this.sessionContext.currentSession());
    }

}
