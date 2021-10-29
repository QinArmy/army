package io.army.sync;

import io.army.cache.SessionCacheFactory;
import io.army.context.spi.CurrentSessionContext;

interface InnerSessionFactory extends InnerGenericRmSessionFactory, InnerGenericSyncApiSessionFactory, SessionFactory {

    CurrentSessionContext currentSessionContext();

    SessionCacheFactory sessionCacheFactory();

}
