package io.army.boot.sync;

import io.army.ArmyConfigConstant;
import io.army.DataAccessException;
import io.army.ErrorCode;
import io.army.SessionFactoryException;
import io.army.boot.SessionFactoryUtils;
import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.Dialect;
import io.army.dialect.DialectNotMatchException;
import io.army.dialect.SQLDialect;
import io.army.dialect.UnSupportedDialectException;
import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.env.Environment;
import io.army.interceptor.DomainInterceptor;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.sync.GenericSyncSessionFactory;
import io.army.util.ClassUtils;
import io.army.util.Pair;
import io.army.util.ReflectionUtils;
import io.army.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

abstract class SyncSessionFactoryUtils extends SessionFactoryUtils {

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

    static Pair<Dialect, SQLDialect> createDialect(DataSource dataSource,
                                                   RmSessionFactory sessionFactory) {

        SQLDialect sqlDialect = sessionFactory.environment().getProperty(
                String.format(ArmyConfigConstant.SQL_DIALECT, sessionFactory.name()), SQLDialect.class);

        final SQLDialect databaseSqlDialect = getSQLDialect(dataSource);

        SQLDialect actualSqlDialect = decideSQLDialect(sqlDialect, databaseSqlDialect);

        Dialect dialect;
        switch (actualSqlDialect) {
            case MySQL:
            case MySQL57:
            case MySQL80:
                dialect = MySQLDialectFactory.createMySQLDialect(actualSqlDialect, sessionFactory);
                break;
            case Db2:
            case Oracle:
            case Postgre:
            case OceanBase:
            case SQL_Server:
            default:
                throw new RuntimeException(String.format("unknown SQLDialect[%s]", actualSqlDialect));
        }
        return new Pair<>(dialect, databaseSqlDialect);
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

    static Map<TableMeta<?>, List<DomainInterceptor>> createDomainInterceptorMap(
            Collection<DomainInterceptor> domainInterceptors) {
        Map<TableMeta<?>, List<DomainInterceptor>> interceptorMap = new HashMap<>();

        for (DomainInterceptor interceptor : domainInterceptors) {
            for (TableMeta<?> tableMeta : interceptor.tableMetaSet()) {
                List<DomainInterceptor> list = interceptorMap.computeIfAbsent(tableMeta, key -> new ArrayList<>());
                list.add(interceptor);
            }
        }

        final Comparator<DomainInterceptor> comparator = Comparator.comparingInt(DomainInterceptor::order);

        for (Map.Entry<TableMeta<?>, List<DomainInterceptor>> e : interceptorMap.entrySet()) {
            e.getValue().sort(comparator);
            interceptorMap.replace(e.getKey(), Collections.unmodifiableList(e.getValue()));
        }
        return Collections.unmodifiableMap(interceptorMap);
    }

    /*################################## blow private method ##################################*/


    private static SQLDialect oracleDialect(int major, int minor) {
        throw new UnSupportedDialectException(ErrorCode.UNSUPPORT_DIALECT
                , "%s is unsupported by army.", "Oracle");
    }

    private static SQLDialect mysqlDialect(int major, int minor) {
        SQLDialect sqlDialect;
        switch (major) {
            case 5:
                if (minor < 7) {
                    throw createUnSupportedDialectException(major, minor);
                }
                sqlDialect = SQLDialect.MySQL57;
                break;
            case 8:
                switch (minor) {
                    case 0:
                        sqlDialect = SQLDialect.MySQL80;
                        break;
                    default:
                        throw createUnSupportedDialectException(major, minor);
                }
                break;
            default:
                throw createUnSupportedDialectException(major, minor);
        }
        return sqlDialect;
    }

    private static SQLDialect decideSQLDialect(@Nullable SQLDialect dialect, SQLDialect databaseSqlDialect) {
        SQLDialect actual = dialect;
        if (actual == null) {
            LOG.debug("extract dml dialect from database");
            actual = databaseSqlDialect;
        } else if (!SQLDialect.sameFamily(dialect, databaseSqlDialect)
                || dialect.ordinal() > databaseSqlDialect.ordinal()) {
            throw new DialectNotMatchException(ErrorCode.META_ERROR, "SQLDialect[%s] then database not match.", actual);
        }
        return actual;
    }

    private static SQLDialect getSQLDialect(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            String productName = metaData.getDatabaseProductName();
            int major = metaData.getDatabaseMajorVersion();
            int minor = metaData.getDatabaseMinorVersion();

            SQLDialect sqlDialect;
            switch (productName) {
                case "MySQL":
                    sqlDialect = mysqlDialect(major, minor);
                    break;
                case "Oracle":
                    sqlDialect = oracleDialect(major, minor);
                    break;
                default:
                    throw new UnSupportedDialectException(ErrorCode.UNSUPPORT_DIALECT
                            , "%s is unsupported by army.", productName);
            }
            return sqlDialect;
        } catch (SQLException e) {
            throw new DataAccessException(ErrorCode.ACCESS_ERROR, e, e.getMessage());
        }
    }


    private static UnSupportedDialectException createUnSupportedDialectException(int major, int minor) {
        return new UnSupportedDialectException(ErrorCode.UNSUPPORTED_DIALECT
                , "MySQL %s.%s.x is supported by army", major, minor);
    }
}
