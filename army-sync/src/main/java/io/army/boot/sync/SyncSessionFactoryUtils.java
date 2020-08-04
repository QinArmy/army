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
import io.army.sync.GenericSyncApiSessionFactory;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    static void assertTableCountOfSharding(final int tableCountOfSharding, GenericSessionFactory sessionFactory) {
        switch (sessionFactory.shardingMode()) {
            case NO_SHARDING:
                if (tableCountOfSharding != 1) {
                    throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                            , "%s tableCountOfSharding must equals 1 in NO_SHARDING mode.", sessionFactory);
                }
                break;
            case SINGLE_DATABASE_SHARDING:
            case SHARDING:
                if (tableCountOfSharding < 1) {
                    throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                            , "%s tableCountOfSharding must great than 0 in SHARDING mode.", sessionFactory);
                }
            default:
                throw new IllegalArgumentException(String.format("not support %s", sessionFactory.shardingMode()));
        }
    }


    static Dialect createDialectForSync(DataSource dataSource, SessionFactoryImpl sessionFactory) {
        try (Connection conn = dataSource.getConnection()) {
            Database database = readDatabase(sessionFactory);

            return createDialect(database, extractDatabase(conn), sessionFactory);
        } catch (SQLException e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR, e
                    , "SessionFactory[%s] get connection error.", sessionFactory.name());
        }

    }

    static CurrentSessionContext buildCurrentSessionContext(GenericSyncApiSessionFactory sessionFactory) {
        Environment env = sessionFactory.environment();

        final String className = env.getProperty(
                String.format(ArmyConfigConstant.CURRENT_SESSION_CONTEXT_CLASS, sessionFactory.name()));

        if (!StringUtils.hasText(className)) {
            return DefaultCurrentSessionContext.build(sessionFactory);
        }

        try {
            Class<?> clazz = Class.forName(className);
            if (!CurrentSessionContext.class.isAssignableFrom(clazz)) {
                throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                        , "%s isn't %s type", className
                        , CurrentSessionContext.class.getName());
            }
            Method method = clazz.getMethod("build", GenericSyncApiSessionFactory.class);

            if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
                if (!clazz.isAssignableFrom(method.getReturnType())) {
                    throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                            , "%s return type isn't %s", className
                            , className);
                }
                // invoke static build method.
                return (CurrentSessionContext) method.invoke(null, sessionFactory);
            } else {
                throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                        , "%s not found build method definition", className);
            }

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
