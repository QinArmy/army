package io.army.boot.sync;

import io.army.*;
import io.army.advice.sync.DomainAdvice;
import io.army.context.spi.CurrentSessionContext;
import io.army.datasource.RoutingDataSource;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.sharding.Route;
import io.army.sharding.RouteCreateException;
import io.army.sharding.RouteMetaData;
import io.army.sharding.TableRoute;
import io.army.sync.GenericSyncApiSessionFactory;
import io.army.util.CollectionUtils;
import io.army.util.Pair;
import io.army.util.ReflectionUtils;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

abstract class SyncSessionFactoryUtils extends GenericSessionFactoryUtils {

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

    static void assertSyncTableCountOfSharding(int tableCountPerDatabase, AbstractGenericSessionFactory factory) {
        assertTableCountOfSharding(tableCountPerDatabase, factory);
    }

    static DatabaseMetaForSync obtainDatabaseMeta(DataSource dataSource) {
        try (Connection conn = obtainPrimaryDataSource(dataSource).getConnection()) {

            String catalog, schema, productName;

            catalog = conn.getCatalog();
            schema = conn.getSchema();
            DatabaseMetaData metaData = conn.getMetaData();
            productName = metaData.getDatabaseProductName();

            int major, minor;
            major = metaData.getDatabaseMajorVersion();
            minor = metaData.getDatabaseMinorVersion();

            boolean supportSavePoint;
            supportSavePoint = metaData.supportsSavepoints();

            return new DatabaseMetaForSync(productName, major, minor, catalog, schema, supportSavePoint);
        } catch (SQLException e) {
            throw new SessionFactoryException(e, "obtain database meta data occur error.");
        }
    }


    static Pair<Dialect, Boolean> getDatabaseMetaForSync(DataSource dataSource, SessionFactoryImpl sessionFactory) {
        try (Connection conn = dataSource.getConnection()) {
            Database database = readDatabase(sessionFactory);

            DatabaseMetaData databaseMetaData = conn.getMetaData();

            Dialect dialect;
            dialect = createDialect(database, extractDatabase(databaseMetaData), sessionFactory);
            boolean supportSavePoints;
            supportSavePoints = databaseMetaData.supportsSavepoints();

            return new Pair<>(dialect, supportSavePoints);
        } catch (SQLException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e
                    , "SessionFactory[%s] get connection error.", sessionFactory.name());
        }

    }

    static CurrentSessionContext buildCurrentSessionContext(InnerGenericSyncApiSessionFactory sessionFactory) {

        final String className = "io.army.boot.sync.SpringCurrentSessionContext";

        try {
            CurrentSessionContext sessionContext;
            if (sessionFactory.springApplication()) {
                // spring application environment
                Class<?> contextClass = Class.forName(className);
                Method method;
                method = ReflectionUtils.findMethod(contextClass, "build", GenericSyncApiSessionFactory.class);
                if (method != null
                        && Modifier.isPublic(method.getModifiers())
                        && Modifier.isStatic(method.getModifiers())
                        && contextClass.isAssignableFrom(method.getReturnType())) {
                    sessionContext = (CurrentSessionContext) method.invoke(null, sessionFactory);
                } else {
                    throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                            , "%s definition error.", className);
                }
            } else {
                sessionContext = DefaultCurrentSessionContext.build(sessionFactory);
            }
            return sessionContext;
        } catch (ClassNotFoundException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e
                    , "not found %s class", className);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e, e.getMessage());
        }
    }

    /**
     * @return a unmodifiable map
     */
    static Map<TableMeta<?>, DomainAdvice> createDomainAdviceMap(
            @Nullable Collection<DomainAdvice> domainAdvices) {

        if (CollectionUtils.isEmpty(domainAdvices)) {
            return Collections.emptyMap();
        }
        Map<TableMeta<?>, DomainAdvice> domainAdviceMap = new HashMap<>();

        for (DomainAdvice domainAdvice : domainAdvices) {
            for (TableMeta<?> tableMeta : domainAdvice.tableMetaSet()) {
                if (domainAdviceMap.putIfAbsent(tableMeta, domainAdvice) != null) {
                    throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                            , "TableMeta[%s] DomainAdvice duplication.", tableMeta);
                }
            }
        }

        return Collections.unmodifiableMap(domainAdviceMap);
    }

    static Database extractDatabase(DatabaseMetaData metaData) {
        try {

            String productName = metaData.getDatabaseProductName();
            int major = metaData.getDatabaseMajorVersion();
            int minor = metaData.getDatabaseMinorVersion();

            Database database;

            return database;
        } catch (SQLException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                    , e, "extract database version error.");
        }
    }


    /**
     * @return a unmodifiable map
     */
    protected static <T extends TableRoute> Map<TableMeta<?>, T> routeMap
    (AbstractGenericSessionFactory sessionFactory, Class<T> routeType, final int databaseCount
            , final int tableCountPerDatabase) {
        final ShardingMode shardingMode = sessionFactory.shardingMode();
        if (shardingMode == ShardingMode.NO_SHARDING) {
            return Collections.emptyMap();
        }
        if (tableCountPerDatabase < 1) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                    , "tableCountPerDatabase[%s] must great than 0 .", tableCountPerDatabase);
        }
        if (databaseCount < 2) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                    , "databaseCount[%s] must great than 1 .", tableCountPerDatabase);
        }
        final RouteMetaData routeMetaData = new RouteMetaDataImpl(shardingMode, databaseCount
                , tableCountPerDatabase);

        Map<TableMeta<?>, T> tableRouteMap = new HashMap<>();
        for (TableMeta<?> tableMeta : sessionFactory.tableMetaMap().values()) {
            Class<? extends Route> routeClass = tableMeta.routeClass();
            if (routeClass == null) {
                continue;
            }
            tableRouteMap.put(tableMeta, createTableRoute(routeClass, routeMetaData, routeType));
        }
        return Collections.unmodifiableMap(tableRouteMap);
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







    static final class DatabaseMetaForSync extends DatabaseMeta {

        public DatabaseMetaForSync(String productName, int majorVersion, int minorVersion
                , String catalog, String schema, boolean supportSavePoint) {
            super(productName, majorVersion, minorVersion, catalog, schema, supportSavePoint);
        }
    }
}
