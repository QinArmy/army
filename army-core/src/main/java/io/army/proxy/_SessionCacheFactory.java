package io.army.proxy;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.session.GenericSessionFactory;

public interface _SessionCacheFactory {

    <T extends IDomain> Class<? extends T> getProxyClass(TableMeta<T> table);


    _SessionCache createCache();


    static _SessionCacheFactory create(GenericSessionFactory sessionFactory) {
        return SessionCacheFactory.create(sessionFactory);
    }


}
