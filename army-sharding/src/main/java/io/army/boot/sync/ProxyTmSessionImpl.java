package io.army.boot.sync;

import io.army.context.spi.CurrentSessionContext;
import io.army.sync.TmSessionFactory;

class ProxyTmSessionImpl {

    private final TmSessionFactory sessionFactory;

    ProxyTmSessionImpl(TmSessionFactory sessionFactory, CurrentSessionContext sessionContext) {
        //super(sessionContext);
        this.sessionFactory = sessionFactory;
    }
//
//    @Override
//    public TmSessionFactory sessionFactory() {
//        return this.sessionFactory;
//    }
//
//    @Override
//    public ArmyEnvironment environment() {
//        return this.sessionFactory.environment();
//    }
//
//    @Nullable
//    @Override
//    public <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass) {
//        return this.sessionFactory.tableMeta(domainClass);
//    }
}
