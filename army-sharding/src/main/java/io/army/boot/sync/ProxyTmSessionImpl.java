package io.army.boot.sync;

import io.army.context.spi.CurrentSessionContext;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.sync.AbstractProxySyncSession;
import io.army.sync.ProxyTmSession;
import io.army.sync.TmSessionFactory;

class ProxyTmSessionImpl extends AbstractProxySyncSession implements ProxyTmSession {

    private final TmSessionFactory sessionFactory;

    ProxyTmSessionImpl(TmSessionFactory sessionFactory, CurrentSessionContext sessionContext) {
        super(sessionContext);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public TmSessionFactory sessionFactory() {
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
}
