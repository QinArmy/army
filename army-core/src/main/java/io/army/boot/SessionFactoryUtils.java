package io.army.boot;

import io.army.ArmyAccessException;
import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.asm.TableMetaLoader;
import io.army.dialect.Dialect;
import io.army.dialect.DialectNotMatchException;
import io.army.dialect.SQLDialect;
import io.army.dialect.UnSupportedDialectException;
import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @see SessionFactory
 */
abstract class SessionFactoryUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryUtils.class);


    static Pair<Dialect, SQLDialect> createDialect(final @Nullable SQLDialect sqlDialect, DataSource dataSource,
                                                   SessionFactory sessionFactory) {
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

    static Map<Class<?>, TableMeta<?>> scanPackagesForMeta(SchemaMeta schemaMeta, List<String> packagesToScan) {
        return TableMetaLoader.build()
                .scanTableMeta(schemaMeta, packagesToScan);
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

    private static SQLDialect decideSQLDialect(SQLDialect dialect, SQLDialect databaseSqlDialect) {
        SQLDialect actual = dialect;
        if (actual == null) {
            LOG.debug("extract sql dialect from database");
            actual = databaseSqlDialect;
        } else if (!SQLDialect.sameFamily(dialect, databaseSqlDialect)
                || dialect.ordinal() > databaseSqlDialect.ordinal()) {
            throw new DialectNotMatchException(ErrorCode.META_ERROR, "SQLDialect[%s] and database not match.", actual);
        }
        return actual;
    }


    private static boolean validateSQLDialect(final SQLDialect sqlDialect, DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            String productName = conn.getMetaData().getDatabaseProductName().toUpperCase();
            SQLDialect database;
            switch (productName) {
                case "MySQL":
                    database = SQLDialect.MySQL;
                    break;
                case "Oracle":
                    database = SQLDialect.Oracle;
                    break;
                default:
                    throw new UnSupportedDialectException(ErrorCode.UNSUPPORT_DIALECT
                            , "%s is unsupported by army.", productName);
            }
            return SQLDialect.sameFamily(database, sqlDialect);
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e, e.getMessage());
        }
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
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e, e.getMessage());
        }
    }


    private static UnSupportedDialectException createUnSupportedDialectException(int major, int minor) {
        return new UnSupportedDialectException(ErrorCode.UNSUPPORTED_DIALECT
                , "MySQL %s.%s.x is supported by army", major, minor);
    }

}
