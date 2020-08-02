package io.army.boot.sync;

import io.army.cache.SessionCacheFactory;
import io.army.context.spi.CurrentSessionContext;
import io.army.sync.SessionFactory;

interface InnerSessionFactory extends InnerGenericRmSessionFactory, SessionFactory {

    CurrentSessionContext currentSessionContext();

    SessionCacheFactory sessionCacheFactory();
}
