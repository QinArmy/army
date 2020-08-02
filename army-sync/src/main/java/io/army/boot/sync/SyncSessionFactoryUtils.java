package io.army.boot.sync;

import io.army.*;
import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.dialect.UnSupportedDialectException;
import io.army.env.Environment;
import io.army.interceptor.DomainAdvice;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.sharding.Route;
import io.army.sharding.RouteCreateException;
import io.army.sharding.RouteMetaData;
import io.army.sharding.TableRoute;
import io.army.sync.GenericSyncSessionFactory;
import io.army.util.ClassUtils;
import io.army.util.CollectionUtils;
import io.army.util.ReflectionUtils;
import io.army.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

abstract class SyncSessionFactoryUtils extends GenericSessionFactoryUtils {

    static DataSource obtainPrimaryDataSource(final DataSource dataSource) {
        final String className = "org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource";

        DataSource primary = dataSource;
        try {
            if (ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader())) {
                Class<?> routingDataSourceClass = Class.forName(className);
                if (routingDataSourceClass.isInstance(dataSource)) {
                    Method method = ReflectionUtils.findMethod(dataSource.getClass(), "getPrimaryDataSource");
                    if (method != null) {
                        primary = (DataSource) method.invoke(dataSource);
                    }
                }
                if (primary == null) {
                    primary = dataSource;
                }
            }
        } catch (Exception e) {
            // no -op,primary = dataSource
        }
        return primary;
    }

    /**
     * @param database read from {@link Environment} with {@link ArmyConfigConstant#DATABASE}
     */
    static Dialect createDialectForSync(DataSource dataSource, SingleDatabaseSessionFactory sessionFactory) {
        try (Connection conn = dataSource.getConnection()) {
            Database database = readDatabase(sessionFactory);

            return createDialect(database, extractDatabase(conn), sessionFactory);
        } catch (SQLException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e, "get connection error.");
        }

    }

    static CurrentSessionContext buildCurrentSessionContext(GenericSyncSessionFactory sessionFactory) {
        Environment env = sessionFactory.environment();

        final String className = env.getProperty(
                String.format(ArmyConfigConstant.CURRENT_SESSION_CONTEXT_CLASS, sessionFactory.name()));

        if (!StringUtils.hasText(className)) {
            return DefaultCurrentSessionContext.build(sessionFactory);
        }

        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod("build", GenericSyncSessionFactory.class);

            if (!CurrentSessionContext.class.isAssignableFrom(clazz)) {
                throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                        , "%s isn't %s type", className
                        , CurrentSessionContext.class.getName());
            }
            if (!clazz.isAssignableFrom(method.getReturnType())) {
                throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                        , "%s return type isn't %s", className
                        , className);
            }
            // invoke static build method.
            return (CurrentSessionContext) method.invoke(null, sessionFactory);
        } catch (ClassNotFoundException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e
                    , "not found CurrentSessionContext class");
        } catch (NoSuchMethodException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e
                    , "%s no [public static %s build(SessionFactory)] method.", className
                    , className);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e, e.getMessage());
        }
    }

    static Map<TableMeta<?>, List<DomainAdvice>> createDomainInterceptorMap(
            Collection<DomainAdvice> domainInterceptors) {
        Map<TableMeta<?>, List<DomainAdvice>> interceptorMap = new HashMap<>();

        for (DomainAdvice interceptor : domainInterceptors) {
            for (TableMeta<?> tableMeta : interceptor.tableMetaSet()) {
                List<DomainAdvice> list = interceptorMap.computeIfAbsent(tableMeta, key -> new ArrayList<>());
                list.add(interceptor);
            }
        }

        final Comparator<DomainAdvice> comparator = Comparator.comparingInt(DomainAdvice::order);

        for (Map.Entry<TableMeta<?>, List<DomainAdvice>> e : interceptorMap.entrySet()) {
            e.getValue().sort(comparator);
            interceptorMap.replace(e.getKey(), Collections.unmodifiableList(e.getValue()));
        }
        return Collections.unmodifiableMap(interceptorMap);
    }

    static Database extractDatabase(Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            String productName = metaData.getDatabaseProductName();
            int major = metaData.getDatabaseMajorVersion();
            int minor = metaData.getDatabaseMinorVersion();

            Database database;
            switch (productName) {
                case "MySQL":
                    database = extractMysqlDialect(major, minor);
                    break;
                case "Oracle":
                    database = extractOracleDialect(major, minor);
                    break;
                case "PostgreSQL":
                    database = extractPostgreDialect(major, minor);
                    break;
                default:
                    throw new UnSupportedDialectException(ErrorCode.UNSUPPORT_DIALECT
                            , "%s is unsupported by army.", productName);
            }
            return database;
        } catch (SQLException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                    , e, "extract database version error.");
        }
    }


    protected static <T extends TableRoute> Map<TableMeta<?>, T> routeMap
            (AbstractGenericSessionFactory sessionFactory, Class<T> routeType, int databaseCount
                    , int tableCountPerDatabase) {
        final ShardingMode shardingMode = sessionFactory.shardingMode();
        if (shardingMode == ShardingMode.NO_SHARDING) {
            return Collections.emptyMap();
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

    /**
     * @return a unmodifiable map
     */
    static Map<TableMeta<?>, DomainAdvice> createDomainAdviceMap(@Nullable Collection<DomainAdvice> domainAdvices) {
        if (CollectionUtils.isEmpty(domainAdvices)) {
            return Collections.emptyMap();
        }
        Map<TableMeta<?>, DomainAdvice> map = new HashMap<>();

        for (DomainAdvice domainAdvice : domainAdvices) {
            for (TableMeta<?> tableMeta : domainAdvice.tableMetaSet()) {
                if (map.putIfAbsent(tableMeta, domainAdvice) != null) {
                    throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                            , "TableMeta[%s] domain advice duplication.", tableMeta);
                }
            }

        }
        return Collections.unmodifiableMap(map);
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

    private static Database extractPostgreDialect(int major, int minor) {
        Database database;
        switch (major) {
            case 11:
                database = Database.Postgre11;
                break;
            case 12:
                database = Database.Postgre12;
                break;
            default:
                throw createUnSupportedDialectException(major, minor);
        }
        return database;

    }

    private static Database extractOracleDialect(int major, int minor) {
        throw new UnSupportedDialectException(ErrorCode.UNSUPPORT_DIALECT
                , "%s is unsupported by army.", "Oracle");
    }

    private static Database extractMysqlDialect(int major, int minor) {
        Database sqlDialect;
        switch (major) {
            case 5:
                if (minor < 7) {
                    throw createUnSupportedDialectException(major, minor);
                }
                sqlDialect = Database.MySQL57;
                break;
            case 8:
                if (minor == 0) {
                    sqlDialect = Database.MySQL80;
                } else {
                    throw createUnSupportedDialectException(major, minor);
                }
                break;
            default:
                throw createUnSupportedDialectException(major, minor);
        }
        return sqlDialect;
    }


    private static UnSupportedDialectException createUnSupportedDialectException(int major, int minor) {
        return new UnSupportedDialectException(ErrorCode.UNSUPPORTED_DIALECT
                , "MySQL %s.%s.x is supported by army", major, minor);
    }
}
