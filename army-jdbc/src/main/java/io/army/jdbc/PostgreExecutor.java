package io.army.jdbc;

import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.session.DatabaseSessionHolder;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;
import io.army.util._Exceptions;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.Locale;

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
            case JSONPATH: {
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
            case BOX_ARRAY:
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
            case ACLITEM_ARRAY:
            case LSEG_ARRAY:
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
            case TSMULTIRANGE_ARRAY:
            case NUMMULTIRANGE_ARRAY:
            case DATEMULTIRANGE_ARRAY:
            case INT4MULTIRANGE_ARRAY:
            case INT8MULTIRANGE_ARRAY:
            case TSTZMULTIRANGE_ARRAY:
            case TIMESTAMPTZ_ARRAY: {
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

        final String typeName;
        final PostgreSqlType type;
        switch ((typeName = metaData.getColumnTypeName(indexBasedOne).toLowerCase(Locale.ROOT))) {
            case "boolean":
            case "bool":
                type = PostgreSqlType.BOOLEAN;
                break;
            case "int2":
            case "smallserial":
            case "smallint":
                type = PostgreSqlType.SMALLINT;
                break;
            case "int":
            case "int4":
            case "serial":
            case "integer":
                type = PostgreSqlType.INTEGER;
                break;
            case "int8":
            case "bigint":
            case "bigserial":
            case "serial8":
                type = PostgreSqlType.BIGINT;
                break;
            case "numeric":
            case "decimal":
                type = PostgreSqlType.DECIMAL;
                break;
            case "double precision":
            case "float8":
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
                type = PostgreSqlType.TEXT;
                break;
            case "bytea":
                type = PostgreSqlType.BYTEA;
                break;
            case "date":
                type = PostgreSqlType.DATE;
                break;
            case "time":
                type = PostgreSqlType.TIME;
                break;
            case "bit":
                type = PostgreSqlType.BIT;
                break;
            case "bit varying":
            case "varbit":
                type = PostgreSqlType.VARBIT;
                break;
            default:
                type = PostgreSqlType.UNKNOWN;


        }
        return type;
    }

    @Override
    final Object get(final ResultSet resultSet, final int indexBasedOne, final SqlType sqlType) throws SQLException {

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
