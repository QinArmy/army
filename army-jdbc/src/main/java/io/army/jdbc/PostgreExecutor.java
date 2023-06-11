package io.army.jdbc;

import io.army.session.DatabaseSessionHolder;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.*;

/**
 * <p>
 * This class is a implementation of {@link io.army.sync.executor.StmtExecutor} with postgre JDBC driver.
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreExecutor extends JdbcExecutor {

    static PostgreExecutor localExecutor(JdbcLocalExecutorFactory factory, Connection conn) {
        final PostgreExecutor executor;
        if (factory.databaseSessionHolder) {
            executor = new LocalSessionHolderExecutor(factory, conn);
        } else {
            executor = new LocalSessionExecutor(factory, conn);
        }
        return executor;
    }


    private static final Logger LOG = LoggerFactory.getLogger(PostgreExecutor.class);

    private PostgreExecutor(JdbcExecutorFactory factory, Connection conn) {
        super(factory, conn);
    }


    @Override
    final Logger getLogger() {
        return LOG;
    }

    @Override
    final void bind(final PreparedStatement stmt, final int indexBasedOne, final SqlType type, final Object nonNull)
            throws SQLException {

        switch ((PostgreSqlType) type) {
            case BOOLEAN:
                stmt.setBoolean(indexBasedOne, (Boolean) nonNull);
                break;
            case SMALLINT:
                stmt.setShort(indexBasedOne, (Short) nonNull);
                break;
            case INTEGER:
                stmt.setInt(indexBasedOne, (Integer) nonNull);
                break;
            case BIGINT:
                stmt.setLong(indexBasedOne, (Long) nonNull);
                break;
            case DECIMAL:
                stmt.setBigDecimal(indexBasedOne, (BigDecimal) nonNull);
                break;
            case FLOAT8:
                stmt.setDouble(indexBasedOne, (Double) nonNull);
                break;
            case REAL:
                stmt.setFloat(indexBasedOne, (Float) nonNull);
                break;
            case TIME: {
                if (!(nonNull instanceof LocalTime)) {
                    throw _Exceptions.beforeBindMethod(type, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull, Types.TIME);
            }
            break;
            case DATE: {
                if (!(nonNull instanceof LocalDate)) {
                    throw _Exceptions.beforeBindMethod(type, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull, Types.DATE);
            }
            break;
            case TIMETZ: {
                if (!(nonNull instanceof OffsetTime)) {
                    throw _Exceptions.beforeBindMethod(type, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull); // postgre jdbc stupid, ignore  Types.TIME_WITH_TIMEZONE ,use  Types.TIME. postgre jdbc 42.6.0.
            }
            break;
            case TIMESTAMP: {
                if (!(nonNull instanceof LocalDateTime)) {
                    throw _Exceptions.beforeBindMethod(type, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull, Types.TIMESTAMP);
            }
            break;
            case TIMESTAMPTZ: {
                if (!(nonNull instanceof OffsetDateTime)) {
                    throw _Exceptions.beforeBindMethod(type, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull, Types.TIMESTAMP_WITH_TIMEZONE);
            }
            break;
            case CHAR:
            case TEXT:
            case JSON:

            case BYTEA:
            case BIT:
            case VARBIT:

            case BOX:
            case XML:
            case CIDR:
            case INET:
            case LINE:
            case PATH:
            case UUID:
            case JSONB:
            case MONEY:
            case POINT:
            case CIRCLE:

            case MACADDR:
            case POLYGON:
            case TSQUERY:
            case TSRANGE:
            case VARCHAR:
            case INTERVAL:
            case MACADDR8:
            case NUMRANGE:

            case TSVECTOR:

            case DATERANGE:
            case BOX_ARRAY:
            case INT4RANGE:
            case INT8RANGE:
            case OID_ARRAY:

            case TSTZRANGE:

            case BIT_ARRAY:
            case XML_ARRAY:
            case CHAR_ARRAY:
            case CIDR_ARRAY:
            case DATE_ARRAY:
            case INET_ARRAY:
            case JSON_ARRAY:
            case LINE_ARRAY:
            case PATH_ARRAY:
            case REAL_ARRAY:
            case REF_CURSOR:
            case TEXT_ARRAY:
            case TIME_ARRAY:
            case UUID_ARRAY:
            case BYTEA_ARRAY:
            case JSONB_ARRAY:
            case MONEY_ARRAY:
            case POINT_ARRAY:
            case BIGINT_ARRAY:
            case DOUBLE_ARRAY:
            case TIMETZ_ARRAY:
            case VARBIT_ARRAY:
            case BOOLEAN_ARRAY:
            case CIRCLES_ARRAY:
            case DECIMAL_ARRAY:
            case INTEGER_ARRAY:
            case MACADDR_ARRAY:
            case POLYGON_ARRAY:
            case TSQUERY_ARRAY:
            case TSRANGE_ARRAY:
            case VARCHAR_ARRAY:
            case INTERVAL_ARRAY:
            case MACADDR8_ARRAY:
            case NUMRANGE_ARRAY:
            case SMALLINT_ARRAY:
            case TSVECTOR_ARRAY:
            case DATERANGE_ARRAY:
            case INT4RANGE_ARRAY:
            case INT8RANGE_ARRAY:
            case TIMESTAMP_ARRAY:
            case TSTZRANGE_ARRAY:
            case TIMESTAMPTZ_ARRAY:

        }
    }

    @Override
    final SqlType getSqlType(ResultSetMetaData metaData, int indexBasedOne) {
        return null;
    }

    @Override
    final Object get(ResultSet resultSet, int indexBasedOne, SqlType sqlType) throws SQLException {
        return null;
    }


    private static final class LocalSessionExecutor extends PostgreExecutor {

        private LocalSessionExecutor(JdbcExecutorFactory factory, Connection conn) {
            super(factory, conn);
        }


    }//LocalSessionExecutor

    private static final class LocalSessionHolderExecutor extends PostgreExecutor implements DatabaseSessionHolder {

        private LocalSessionHolderExecutor(JdbcExecutorFactory factory, Connection conn) {
            super(factory, conn);
        }

        @Override
        public Object databaseSession() {
            return this.conn;
        }

    }//LocalSessionHolderExecutor


}
