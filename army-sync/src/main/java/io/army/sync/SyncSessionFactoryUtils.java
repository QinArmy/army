package io.army.sync;

import io.army.ErrorCode;
import io.army.SessionFactoryException;
import io.army.datasource.RoutingDataSource;
import io.army.meta.TableMeta;
import io.army.session.AbstractSessionFactory;
import io.army.sharding.Route;
import io.army.sharding.RouteCreateException;
import io.army.sharding.RouteMetaData;
import io.army.sharding.TableRoute;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;

abstract class SyncSessionFactoryUtils {

    @SuppressWarnings("unchecked")
    static <T extends CommonDataSource> T obtainPrimaryDataSource(final T dataSource) {

        T primary = null;
        if (dataSource instanceof RoutingDataSource) {
            primary = ((RoutingDataSource<T>) dataSource).getPrimaryDataSource();

            if (dataSource instanceof XADataSource && !(primary instanceof XADataSource)) {
                throw new SessionFactoryException("%s getPrimaryDataSource() return error."
                        , dataSource.getClass().getName());
            } else if (dataSource instanceof DataSource && !(primary instanceof DataSource)) {
                throw new SessionFactoryException("%s getPrimaryDataSource() return error."
                        , dataSource.getClass().getName());
            }
        }
        if (primary == null) {
            primary = dataSource;
        }
        return primary;
    }



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

    @SuppressWarnings("unchecked")
    private static <T extends TableRoute> T createTableRoute(Class<? extends Route> routeClass
            , RouteMetaData routeMetaData, Class<T> routeType) {
        Method method;

        try {
            method = routeClass.getMethod("build", RouteMetaData.class);
        } catch (NoSuchMethodException e) {
            throw new RouteCreateException(ErrorCode.ROUTE_ERROR, "Class[%s] not found build(RouteMetaData) method.");
        }

        if (Modifier.isStatic(method.getModifiers())
                && routeClass.isAssignableFrom(method.getReturnType())
                && routeType.isAssignableFrom(routeClass)) {
            try {
                return (T) method.invoke(null, routeMetaData);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RouteCreateException(ErrorCode.ROUTE_ERROR, e
                        , "Class[%s] build(ShardingMode) method invoke error.");
            }
        } else {
            throw new RouteCreateException(ErrorCode.ROUTE_ERROR, "Class[%s] build(RouteMetaData) method error.");
        }
    }


}
