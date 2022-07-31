package io.army.proxy;


import io.army.meta.TableMeta;
import io.army.session.SessionFactory;

public interface _SessionCacheFactory {

    <T> Class<? extends T> getProxyClass(TableMeta<T> table);


    _SessionCache createCache();


    static _SessionCacheFactory create(SessionFactory sessionFactory) {
        return SessionCacheFactory.create(sessionFactory);
    }


}
