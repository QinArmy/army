package io.army.boot;

import io.army.GenericSessionFactory;
import io.army.cache.SessionCacheFactory;

 interface InnerGenericSessionFaction extends GenericSessionFactory {

     SessionCacheFactory sessionCacheFactory();
 }
