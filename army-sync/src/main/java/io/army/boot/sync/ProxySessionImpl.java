package io.army.boot.sync;

import io.army.context.spi.CurrentSessionContext;
import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.domain.IDomain;
import io.army.env.Environment;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.sync.ProxySession;
import io.army.sync.SessionFactory;

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
    public Environment environment() {
        return this.sessionFactory.environment();
    }

    @Nullable
    @Override
    public <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass) {
        return this.sessionFactory.tableMeta(domainClass);
    }

    @Override
    public int[] batchUpdate(Update update) {
        return this.sessionContext.currentSession().batchUpdate(update);
    }

    @Override
    public int[] batchUpdate(Update update, Visible visible) {
        return this.sessionContext.currentSession().batchUpdate(update, visible);
    }

    @Override
    public long[] batchLargeUpdate(Update update) {
        return this.sessionContext.currentSession().batchLargeUpdate(update);
    }

    @Override
    public long[] batchLargeUpdate(Update update, Visible visible) {
        return this.sessionContext.currentSession().batchLargeUpdate(update, visible);
    }

    @Override
    public int[] batchDelete(Delete delete) {
        return this.sessionContext.currentSession().batchDelete(delete);
    }

    @Override
    public int[] batchDelete(Delete delete, Visible visible) {
        return this.sessionContext.currentSession().batchDelete(delete, visible);
    }

    @Override
    public long[] batchLargeDelete(Delete delete) {
        return this.sessionContext.currentSession().batchLargeDelete(delete);
    }

    @Override
    public long[] batchLargeDelete(Delete delete, Visible visible) {
        return this.sessionContext.currentSession().batchLargeDelete(delete, visible);
    }



}
