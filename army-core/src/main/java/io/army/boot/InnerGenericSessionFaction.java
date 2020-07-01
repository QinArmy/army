package io.army.boot;

import io.army.GenericSessionFactory;
import io.army.cache.SessionCacheFactory;

public interface InnerGenericSessionFaction extends GenericSessionFactory {


    SessionCacheFactory sessionCacheFactory();
}
