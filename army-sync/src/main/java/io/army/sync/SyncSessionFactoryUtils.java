package io.army.sync;

import io.army.meta.TableMeta;
import io.army.session.AbstractSessionFactory;
import io.army.sharding.TableRoute;

import java.util.Collections;
import java.util.Map;

abstract class SyncSessionFactoryUtils {




    /**
     * @return a unmodifiable map
     */
    protected static <T extends TableRoute> Map<TableMeta<?>, T> routeMap
    (AbstractSessionFactory sessionFactory, Class<T> routeType, final int databaseCount
            , final int tableCountPerDatabase) {
//        if (tableCountPerDatabase < 1) {
//            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
//                    , "tableCountPerDatabase[%s] must great than 0 .", tableCountPerDatabase);
//        }
//        if (databaseCount < 2) {
//            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
//                    , "databaseCount[%s] must great than 1 .", tableCountPerDatabase);
//        }
//        final RouteMetaData routeMetaData = new RouteMetaDataImpl(factoryMode, databaseCount
//                , tableCountPerDatabase);
//
//        Map<TableMeta<?>, T> tableRouteMap = new HashMap<>();
//        for (TableMeta<?> tableMeta : sessionFactory.tableMetaMap().values()) {
//            Class<? extends Route> routeClass = tableMeta.routeClass();
//            if (routeClass == null) {
//                continue;
//            }
//            tableRouteMap.put(tableMeta, createTableRoute(routeClass, routeMetaData, routeType));
//        }
        return Collections.emptyMap();
    }


    /*################################## blow private method ##################################*/



}
