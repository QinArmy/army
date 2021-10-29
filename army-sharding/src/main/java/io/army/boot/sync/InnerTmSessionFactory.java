package io.army.boot.sync;

import io.army.boot.DomainValuesGenerator;
import io.army.cache.SessionCacheFactory;
import io.army.context.spi.CurrentSessionContext;
import io.army.sync.InnerGenericSyncApiSessionFactory;
import io.army.sync.TmSessionFactory;

import java.util.List;

interface InnerTmSessionFactory extends TmSessionFactory, InnerGenericSyncApiSessionFactory {

    DomainValuesGenerator domainValuesGenerator();

    CurrentSessionContext currentSessionContext();

    SessionCacheFactory sessionCacheFactory();

    /**
     * @return a unmodifiable list
     */
    List<RmSessionFactory> rmSessionFactoryList();

}
