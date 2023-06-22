package io.army.jdbc;

import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.session.DatabaseSessionHolder;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;
import io.army.sync.executor.LocalStmtExecutor;
import io.army.util._Exceptions;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.XAConnection;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.Locale;
import java.util.UUID;

/**
 * <p>
 * This class is a implementation of {@link io.army.sync.executor.StmtExecutor} with postgre JDBC driver.
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreExecutor extends JdbcExecutor {

    static LocalStmtExecutor localExecutor(JdbcLocalExecutorFactory factory, Connection conn) {
        final LocalSessionExecutor executor;
        if (factory.databaseSessionHolder) {
            executor = new LocalSessionHolderExecutor(factory, conn);
        } else {
            executor = new LocalSessionExecutor(factory, conn);
        }
        return executor;
    }

    static PostgreExecutor rmExecutor(JdbcRmExecutorFactory factory, XAConnection xaConn) {
        throw new UnsupportedOperationException();
    }


    private static final Logger LOG = LoggerFactory.getLogger(PostgreExecutor.class);

    private PostgreExecutor(JdbcExecutorFactory factory, Connection conn) {
        super(factory, conn);
    }

    @Override
    public boolean isSupportClientStream() {
        //false ,postgre jdbc don't support
        return false;
    }

    @Override
    final Logger getLogger() {
        return LOG;
    }

    @Override
    final Object bind(final PreparedStatement stmt, final int indexBasedOne, final @Nullable Object attr,
                      final MappingType type, final SqlType sqlType, final Object nonNull)
            throws SQLException {
        PGobject pgObject;
        if (attr == null) {
            pgObject = null;
        } else {
            pgObject = (PGobject) attr;
        }
        switch ((PostgreSqlType) sqlType) {
            case BOOLEAN:
                stmt.setBoolean(indexBasedOne, (Boolean) nonNull);
                break;
            case SMALLINT:
                stmt.setShort(indexBasedOne, (Short) nonNull);
                break;
            case NO_CAST_INTEGER:
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
            case CHAR:
            case VARCHAR:
            case TEXT:
            case NO_CAST_TEXT: {
                if (nonNull instanceof String) {
                    stmt.setString(indexBasedOne, (String) nonNull);
                } else if (nonNull instanceof Reader) {
                    stmt.setCharacterStream(indexBasedOne, (Reader) nonNull);
                } else {
                    throw _Exceptions.beforeBindMethod(sqlType, type, nonNull);
                }
            }
            break;
            case BYTEA: {
                if (nonNull instanceof byte[]) {
                    stmt.setBytes(indexBasedOne, (byte[]) nonNull);
                } else if (nonNull instanceof InputStream) {
                    stmt.setBinaryStream(indexBasedOne, (InputStream) nonNull);
                } else {
                    throw _Exceptions.beforeBindMethod(sqlType, type, nonNull);
                }
            }
            break;
            case TIME: {
                if (!(nonNull instanceof LocalTime)) {
                    throw _Exceptions.beforeBindMethod(sqlType, type, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull, Types.TIME);
            }
            break;
            case DATE: {
                if (!(nonNull instanceof LocalDate)) {
                    throw _Exceptions.beforeBindMethod(sqlType, type, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull, Types.DATE);
            }
            break;
            case TIMETZ: {
                if (!(nonNull instanceof OffsetTime)) {
                    throw _Exceptions.beforeBindMethod(sqlType, type, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull); // postgre jdbc stupid, ignore  Types.TIME_WITH_TIMEZONE ,use  Types.TIME. postgre jdbc 42.6.0.
            }
            break;
            case TIMESTAMP: {
                if (!(nonNull instanceof LocalDateTime)) {
                    throw _Exceptions.beforeBindMethod(sqlType, type, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull, Types.TIMESTAMP);
            }
            break;
            case TIMESTAMPTZ: {
                if (!(nonNull instanceof OffsetDateTime)) {
                    throw _Exceptions.beforeBindMethod(sqlType, type, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull, Types.TIMESTAMP_WITH_TIMEZONE);
            }
            break;
            case UUID:
            case ACLITEM:
            case INTERVAL:
            case MONEY:

            case BIT:
            case VARBIT:

            case CIDR:
            case INET:
            case MACADDR8:
            case MACADDR:

            case BOX:
            case LSEG:
            case LINE:
            case PATH:
            case POINT:
            case CIRCLE:
            case POLYGON:

            case TSVECTOR:
            case TSQUERY:

            case INT4RANGE:
            case INT8RANGE:
            case NUMRANGE:
            case TSRANGE:
            case DATERANGE:
            case TSTZRANGE:

            case INT4MULTIRANGE:
            case INT8MULTIRANGE:
            case NUMMULTIRANGE:
            case TSMULTIRANGE:
            case DATEMULTIRANGE:
            case TSTZMULTIRANGE:

            case JSON:
            case JSONB:
            case JSONPATH:

            case PG_LSN:
            case PG_SNAPSHOT: {
                if (pgObject == null) {
                    pgObject = new PGobject();
                }
                pgObject.setType(sqlType.name().toLowerCase(Locale.ROOT));
                pgObject.setValue((String) nonNull);
                stmt.setObject(indexBasedOne, pgObject, Types.OTHER);
            }
            break;
            case XML: {
                if (!(nonNull instanceof String)) {
                    throw _Exceptions.beforeBindMethod(sqlType, type, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull, Types.SQLXML);
            }
            break;
            case BOOLEAN_ARRAY:
            case INTEGER_ARRAY:
            case SMALLINT_ARRAY:
            case BIGINT_ARRAY:
            case DECIMAL_ARRAY:
            case REAL_ARRAY:
            case FLOAT8_ARRAY:

            case CHAR_ARRAY:
            case VARCHAR_ARRAY:
            case TEXT_ARRAY:

            case BYTEA_ARRAY:

            case DATE_ARRAY:
            case TIME_ARRAY:
            case TIMETZ_ARRAY:
            case TIMESTAMP_ARRAY:
            case TIMESTAMPTZ_ARRAY:
            case INTERVAL_ARRAY:

            case BIT_ARRAY:
            case VARBIT_ARRAY:
            case UUID_ARRAY:

            case CIDR_ARRAY:
            case INET_ARRAY:
            case MACADDR_ARRAY:
            case MACADDR8_ARRAY:

            case JSON_ARRAY:
            case JSONB_ARRAY:
            case JSONPATH_ARRAY:
            case XML_ARRAY:

            case POINT_ARRAY:
            case LINE_ARRAY:
            case LSEG_ARRAY:
            case PATH_ARRAY:
            case BOX_ARRAY:
            case CIRCLE_ARRAY:
            case POLYGON_ARRAY:

            case TSQUERY_ARRAY:
            case TSVECTOR_ARRAY:

            case INT4RANGE_ARRAY:
            case INT8RANGE_ARRAY:
            case NUMRANGE_ARRAY:
            case DATERANGE_ARRAY:
            case TSRANGE_ARRAY:
            case TSTZRANGE_ARRAY:

            case INT4MULTIRANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY:
            case TSMULTIRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY:

            case MONEY_ARRAY:
            case ACLITEM_ARRAY:
            case PG_LSN_ARRAY:
            case PG_SNAPSHOT_ARRAY: {
                if (!(nonNull instanceof String)) {
                    throw _Exceptions.beforeBindMethod(sqlType, type, nonNull);
                }
                final String name, typeName;
                name = sqlType.name();
                typeName = name.substring(0, name.lastIndexOf(_Constant.UNDERSCORE_ARRAY))
                        .toLowerCase(Locale.ROOT);
                if (pgObject == null) {
                    pgObject = new PGobject();
                }
                pgObject.setType(typeName + "[]");
                pgObject.setValue((String) nonNull);
                stmt.setObject(indexBasedOne, pgObject, Types.OTHER);
            }
            break;
            case USER_DEFINED:
            case USER_DEFINED_ARRAY: {
                if (!(nonNull instanceof String)) {
                    throw _Exceptions.beforeBindMethod(sqlType, type, nonNull);
                } else if (!(type instanceof MappingType.SqlUserDefinedType)) {
                    throw _Exceptions.mapMethodError(type, PostgreSqlType.class);
                }
                final String typeName;
                typeName = ((MappingType.SqlUserDefinedType) type).sqlTypeName(this.factory.serverMeta);
                if (pgObject == null) {
                    pgObject = new PGobject();
                }
                if (sqlType == PostgreSqlType.USER_DEFINED_ARRAY) {
                    pgObject.setType(typeName + "[]");
                } else {
                    pgObject.setType(typeName);
                }
                pgObject.setValue((String) nonNull);
                stmt.setObject(indexBasedOne, pgObject, Types.OTHER);
            }
            break;
            case UNKNOWN:
            case REF_CURSOR:
            default:
                throw _Exceptions.unexpectedEnum((PostgreSqlType) sqlType);

        }
        return pgObject;
    }

    @Override
    final SqlType getSqlType(final ResultSetMetaData metaData, final int indexBasedOne) throws SQLException {

        final PostgreSqlType type;
        switch (metaData.getColumnTypeName(indexBasedOne).toLowerCase(Locale.ROOT)) {
            case "boolean":
            case "bool":
                type = PostgreSqlType.BOOLEAN;
                break;
            case "int2":
            case "smallint":
            case "smallserial":
                type = PostgreSqlType.SMALLINT;
                break;
            case "int":
            case "int4":
            case "serial":
            case "integer":
            case "xid":  // https://www.postgresql.org/docs/current/datatype-oid.html
            case "cid":  // https://www.postgresql.org/docs/current/datatype-oid.html
                type = PostgreSqlType.INTEGER;
                break;
            case "int8":
            case "bigint":
            case "bigserial":
            case "serial8":
            case "xid8":  // https://www.postgresql.org/docs/current/datatype-oid.html  TODO what's tid ?
                type = PostgreSqlType.BIGINT;
                break;
            case "numeric":
            case "decimal":
                type = PostgreSqlType.DECIMAL;
                break;
            case "float8":
            case "double precision":
            case "float":
                type = PostgreSqlType.FLOAT8;
                break;
            case "float4":
            case "real":
                type = PostgreSqlType.REAL;
                break;
            case "char":
            case "character":
                type = PostgreSqlType.CHAR;
                break;
            case "varchar":
            case "character varying":
                type = PostgreSqlType.VARCHAR;
                break;
            case "text":
            case "txid_snapshot":  // TODO txid_snapshot is text?
                type = PostgreSqlType.TEXT;
                break;
            case "bytea":
                type = PostgreSqlType.BYTEA;
                break;
            case "date":
                type = PostgreSqlType.DATE;
                break;
            case "time":
            case "time without time zone":
                type = PostgreSqlType.TIME;
                break;
            case "timetz":
            case "time with time zone":
                type = PostgreSqlType.TIMETZ;
                break;
            case "timestamp":
            case "timestamp without time zone":
                type = PostgreSqlType.TIMESTAMP;
                break;
            case "timestamptz":
            case "timestamp with time zone":
                type = PostgreSqlType.TIMESTAMPTZ;
                break;
            case "interval":
                type = PostgreSqlType.INTERVAL;
                break;

            case "json":
                type = PostgreSqlType.JSON;
                break;
            case "jsonb":
                type = PostgreSqlType.JSONB;
                break;
            case "jsonpath":
                type = PostgreSqlType.JSONPATH;
                break;
            case "xml":
                type = PostgreSqlType.XML;
                break;

            case "bit":
                type = PostgreSqlType.BIT;
                break;
            case "bit varying":
            case "varbit":
                type = PostgreSqlType.VARBIT;
                break;

            case "cidr":
                type = PostgreSqlType.CIDR;
                break;
            case "inet":
                type = PostgreSqlType.INET;
                break;
            case "macaddr8":
                type = PostgreSqlType.MACADDR8;
                break;
            case "macaddr":
                type = PostgreSqlType.MACADDR;
                break;

            case "box":
                type = PostgreSqlType.BOX;
                break;
            case "lseg":
                type = PostgreSqlType.LSEG;
                break;
            case "line":
                type = PostgreSqlType.LINE;
                break;
            case "path":
                type = PostgreSqlType.PATH;
                break;
            case "point":
                type = PostgreSqlType.POINT;
                break;
            case "circle":
                type = PostgreSqlType.CIRCLE;
                break;
            case "polygon":
                type = PostgreSqlType.POLYGON;
                break;

            case "tsvector":
                type = PostgreSqlType.TSVECTOR;
                break;
            case "tsquery":
                type = PostgreSqlType.TSQUERY;
                break;

            case "int4range":
                type = PostgreSqlType.INT4RANGE;
                break;
            case "int8range":
                type = PostgreSqlType.INT8RANGE;
                break;
            case "numrange":
                type = PostgreSqlType.NUMRANGE;
                break;
            case "tsrange":
                type = PostgreSqlType.TSRANGE;
                break;
            case "daterange":
                type = PostgreSqlType.DATERANGE;
                break;
            case "tstzrange":
                type = PostgreSqlType.TSTZRANGE;
                break;

            case "int4multirange":
                type = PostgreSqlType.INT4MULTIRANGE;
                break;
            case "int8multirange":
                type = PostgreSqlType.INT8MULTIRANGE;
                break;
            case "nummultirange":
                type = PostgreSqlType.NUMMULTIRANGE;
                break;
            case "datemultirange":
                type = PostgreSqlType.DATEMULTIRANGE;
                break;
            case "tsmultirange":
                type = PostgreSqlType.TSMULTIRANGE;
                break;
            case "tstzmultirange":
                type = PostgreSqlType.TSTZMULTIRANGE;
                break;

            case "uuid":
                type = PostgreSqlType.UUID;
                break;
            case "money":
                type = PostgreSqlType.MONEY;
                break;
            case "aclitem":
                type = PostgreSqlType.ACLITEM;
                break;
            case "pg_lsn":
                type = PostgreSqlType.PG_LSN;
                break;
            case "pg_snapshot":
                type = PostgreSqlType.PG_SNAPSHOT;
                break;

            case "boolean[]":
            case "bool[]":
                type = PostgreSqlType.BOOLEAN_ARRAY;
                break;
            case "int2[]":
            case "smallint[]":
            case "smallserial[]":
                type = PostgreSqlType.SMALLINT_ARRAY;
                break;
            case "int[]":
            case "int4[]":
            case "integer[]":
            case "serial[]":
                type = PostgreSqlType.INTEGER_ARRAY;
                break;
            case "int8[]":
            case "bigint[]":
            case "serial8[]":
            case "bigserial[]":
                type = PostgreSqlType.BIGINT_ARRAY;
                break;
            case "numeric[]":
            case "decimal[]":
                type = PostgreSqlType.DECIMAL_ARRAY;
                break;
            case "float8[]":
            case "float[]":
            case "double precision[]":
                type = PostgreSqlType.FLOAT8_ARRAY;
                break;
            case "float4[]":
            case "real[]":
                type = PostgreSqlType.REAL_ARRAY;
                break;

            case "char[]":
            case "character[]":
                type = PostgreSqlType.CHAR_ARRAY;
                break;
            case "varchar[]":
            case "character varying[]":
                type = PostgreSqlType.VARCHAR_ARRAY;
                break;
            case "text[]":
            case "txid_snapshot[]":
                type = PostgreSqlType.TEXT_ARRAY;
                break;
            case "bytea[]":
                type = PostgreSqlType.BYTEA_ARRAY;
                break;

            case "date[]":
                type = PostgreSqlType.DATE_ARRAY;
                break;
            case "time[]":
            case "time without time zone[]":
                type = PostgreSqlType.TIME_ARRAY;
                break;
            case "timetz[]":
            case "time with time zone[]":
                type = PostgreSqlType.TIMETZ_ARRAY;
                break;
            case "timestamp[]":
            case "timestamp without time zone[]":
                type = PostgreSqlType.TIMESTAMP_ARRAY;
                break;
            case "timestamptz[]":
            case "timestamp with time zone[]":
                type = PostgreSqlType.TIMESTAMPTZ_ARRAY;
                break;
            case "interval[]":
                type = PostgreSqlType.INTERVAL_ARRAY;
                break;

            case "json[]":
                type = PostgreSqlType.JSON_ARRAY;
                break;
            case "jsonb[]":
                type = PostgreSqlType.JSONB_ARRAY;
                break;
            case "jsonpath[]":
                type = PostgreSqlType.JSONPATH_ARRAY;
                break;
            case "xml[]":
                type = PostgreSqlType.XML_ARRAY;
                break;

            case "varbit[]":
            case "bit varying[]":
                type = PostgreSqlType.VARBIT_ARRAY;
                break;
            case "bit[]":
                type = PostgreSqlType.BIT_ARRAY;
                break;

            case "uuid[]":
                type = PostgreSqlType.UUID_ARRAY;
                break;

            case "cidr[]":
                type = PostgreSqlType.CIDR_ARRAY;
                break;
            case "inet[]":
                type = PostgreSqlType.INET_ARRAY;
                break;
            case "macaddr[]":
                type = PostgreSqlType.MACADDR_ARRAY;
                break;
            case "macaddr8[]":
                type = PostgreSqlType.MACADDR8_ARRAY;
                break;

            case "box[]":
                type = PostgreSqlType.BOX_ARRAY;
                break;
            case "lseg[]":
                type = PostgreSqlType.LSEG_ARRAY;
                break;
            case "line[]":
                type = PostgreSqlType.LINE_ARRAY;
                break;
            case "path[]":
                type = PostgreSqlType.PATH_ARRAY;
                break;
            case "point[]":
                type = PostgreSqlType.POINT_ARRAY;
                break;
            case "circle[]":
                type = PostgreSqlType.CIRCLE_ARRAY;
                break;
            case "polygon[]":
                type = PostgreSqlType.POLYGON_ARRAY;
                break;

            case "tsquery[]":
                type = PostgreSqlType.TSQUERY_ARRAY;
                break;
            case "tsvector[]":
                type = PostgreSqlType.TSVECTOR_ARRAY;
                break;

            case "int4range[]":
                type = PostgreSqlType.INT4RANGE_ARRAY;
                break;
            case "int8range[]":
                type = PostgreSqlType.INT8RANGE_ARRAY;
                break;
            case "numrange[]":
                type = PostgreSqlType.NUMRANGE_ARRAY;
                break;
            case "daterange[]":
                type = PostgreSqlType.DATERANGE_ARRAY;
                break;
            case "tsrange[]":
                type = PostgreSqlType.TSRANGE_ARRAY;
                break;
            case "tstzrange[]":
                type = PostgreSqlType.TSTZRANGE_ARRAY;
                break;

            case "int4multirange[]":
                type = PostgreSqlType.INT4MULTIRANGE_ARRAY;
                break;
            case "int8multirange[]":
                type = PostgreSqlType.INT8MULTIRANGE_ARRAY;
                break;
            case "nummultirange[]":
                type = PostgreSqlType.NUMMULTIRANGE_ARRAY;
                break;
            case "datemultirange[]":
                type = PostgreSqlType.DATEMULTIRANGE_ARRAY;
                break;
            case "tsmultirange[]":
                type = PostgreSqlType.TSMULTIRANGE_ARRAY;
                break;
            case "tstzmultirange[]":
                type = PostgreSqlType.TSTZMULTIRANGE_ARRAY;
                break;

            case "money[]":
                type = PostgreSqlType.MONEY_ARRAY;
                break;
            case "pg_lsn[]":
                type = PostgreSqlType.PG_LSN_ARRAY;
                break;
            case "pg_snapshot[]":
                type = PostgreSqlType.PG_SNAPSHOT_ARRAY;
                break;
            case "aclitem[]":
                type = PostgreSqlType.ACLITEM_ARRAY;
                break;
            default:
                type = PostgreSqlType.UNKNOWN;


        }
        return type;
    }

    @Override
    final Object get(final ResultSet resultSet, final int indexBasedOne, final SqlType sqlType) throws SQLException {
        final Object value;

        switch ((PostgreSqlType) sqlType) {
            case BOOLEAN:
                value = resultSet.getObject(indexBasedOne, Boolean.class);
                break;
            case SMALLINT:
                value = resultSet.getObject(indexBasedOne, Short.class);
                break;
            case NO_CAST_INTEGER:
            case INTEGER:
                value = resultSet.getObject(indexBasedOne, Integer.class);
                break;
            case BIGINT:
                value = resultSet.getObject(indexBasedOne, Long.class);
                break;
            case DECIMAL:
                value = resultSet.getObject(indexBasedOne, BigDecimal.class);
                break;
            case FLOAT8:
                value = resultSet.getObject(indexBasedOne, Double.class);
                break;
            case REAL:
                value = resultSet.getObject(indexBasedOne, Float.class);
                break;

            case BYTEA: // postgre client protocol body must less than 2^32 byte
                value = resultSet.getObject(indexBasedOne, byte[].class);
                break;
            case TIME:
                value = resultSet.getObject(indexBasedOne, LocalTime.class);
                break;
            case DATE:
                value = resultSet.getObject(indexBasedOne, LocalDate.class);
                break;
            case TIMETZ:
                value = resultSet.getObject(indexBasedOne, OffsetTime.class);
                break;
            case TIMESTAMP:
                value = resultSet.getObject(indexBasedOne, LocalDateTime.class);
                break;
            case TIMESTAMPTZ:
                value = resultSet.getObject(indexBasedOne, OffsetDateTime.class);
                break;
            case UUID:
                value = resultSet.getObject(indexBasedOne, UUID.class);
                break;
            case CHAR:
            case VARCHAR:
            case TEXT:
            case NO_CAST_TEXT:  // postgre client protocol body must less than 2^32 byte

            case JSON:
            case JSONB:
            case JSONPATH:
            case XML:

            case BIT:
            case VARBIT:

            case INTERVAL:

            case TSVECTOR:
            case TSQUERY:

            case INT4RANGE:
            case INT8RANGE:
            case NUMRANGE:
            case TSRANGE:
            case DATERANGE:
            case TSTZRANGE:

            case INT4MULTIRANGE:
            case INT8MULTIRANGE:
            case NUMMULTIRANGE:
            case TSMULTIRANGE:
            case DATEMULTIRANGE:
            case TSTZMULTIRANGE:

            case PG_SNAPSHOT:

            case BOX:
            case LSEG:
            case LINE:
            case PATH:
            case POINT:
            case CIRCLE:
            case POLYGON:

            case CIDR:
            case INET:
            case MACADDR8:
            case MACADDR:
            case ACLITEM:

            case MONEY:

            case BOOLEAN_ARRAY:
            case INTEGER_ARRAY:
            case SMALLINT_ARRAY:
            case BIGINT_ARRAY:
            case DECIMAL_ARRAY:
            case REAL_ARRAY:
            case FLOAT8_ARRAY:

            case CHAR_ARRAY:
            case VARCHAR_ARRAY:
            case TEXT_ARRAY:

            case BYTEA_ARRAY:

            case DATE_ARRAY:
            case TIME_ARRAY:
            case TIMETZ_ARRAY:
            case TIMESTAMP_ARRAY:
            case TIMESTAMPTZ_ARRAY:
            case INTERVAL_ARRAY:

            case BIT_ARRAY:
            case VARBIT_ARRAY:
            case UUID_ARRAY:

            case CIDR_ARRAY:
            case INET_ARRAY:
            case MACADDR_ARRAY:
            case MACADDR8_ARRAY:

            case JSON_ARRAY:
            case JSONB_ARRAY:
            case JSONPATH_ARRAY:
            case XML_ARRAY:

            case POINT_ARRAY:
            case LINE_ARRAY:
            case LSEG_ARRAY:
            case PATH_ARRAY:
            case BOX_ARRAY:
            case CIRCLE_ARRAY:
            case POLYGON_ARRAY:

            case TSQUERY_ARRAY:
            case TSVECTOR_ARRAY:

            case INT4RANGE_ARRAY:
            case INT8RANGE_ARRAY:
            case NUMRANGE_ARRAY:
            case DATERANGE_ARRAY:
            case TSRANGE_ARRAY:
            case TSTZRANGE_ARRAY:

            case INT4MULTIRANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY:
            case TSMULTIRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY:

            case MONEY_ARRAY:
            case ACLITEM_ARRAY:
            case PG_LSN_ARRAY:
            case PG_SNAPSHOT_ARRAY:

            case USER_DEFINED:
            case USER_DEFINED_ARRAY:
                value = resultSet.getString(indexBasedOne);
                break;
            case PG_LSN: {
                final long v;
                v = resultSet.getLong(indexBasedOne);
                if (v != 0 || resultSet.getObject(indexBasedOne) != null) {
                    value = v;
                } else {
                    value = null;
                }
            }
            break;
            case UNKNOWN:
            case REF_CURSOR:
            default:
                throw _Exceptions.unexpectedEnum((PostgreSqlType) sqlType);

        }

        return value;
    }


    private static class LocalSessionExecutor extends PostgreExecutor implements LocalStmtExecutor {

        private LocalSessionExecutor(JdbcExecutorFactory factory, Connection conn) {
            super(factory, conn);
        }


    }//LocalSessionExecutor

    private static final class LocalSessionHolderExecutor extends LocalSessionExecutor
            implements DatabaseSessionHolder {

        private LocalSessionHolderExecutor(JdbcExecutorFactory factory, Connection conn) {
            super(factory, conn);
        }

        @Override
        public Object databaseSession() {
            return this.conn;
        }

    }//LocalSessionHolderExecutor


}
