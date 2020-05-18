package io.army.boot;

import io.army.GenericSessionFactory;
import io.army.cache.SessionCacheFactory;
import io.army.dialect.Dialect;

public interface InnerGenericSessionFaction extends GenericSessionFactory {

    Dialect dialect();

    SessionCacheFactory sessionCacheFactory();
}
