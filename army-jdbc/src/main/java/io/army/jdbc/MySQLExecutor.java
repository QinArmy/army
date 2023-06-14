package io.army.jdbc;

import com.mysql.cj.MysqlType;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.session.DatabaseSessionHolder;
import io.army.sqltype.MySQLType;
import io.army.sqltype.SqlType;
import io.army.sync.executor.LocalStmtExecutor;
import io.army.sync.executor.RmStmtExecutor;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.XAConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

abstract class MySQLExecutor extends JdbcExecutor {

    static LocalStmtExecutor localExecutor(JdbcLocalExecutorFactory factory, Connection conn) {
        final MySQLLocalExecutor executor;
        if (factory.databaseSessionHolder) {
            executor = new MySQLLocalHolderExecutor(factory, conn);
        } else {
            executor = new MySQLLocalExecutor(factory, conn);
        }
        return executor;
    }

    static MySQLRmExecutor rmExecutor(final JdbcRmExecutorFactory factory, final XAConnection xaConnection) {
        try {
            final Connection connection;
            connection = xaConnection.getConnection();
            return new MySQLRmExecutor(factory, xaConnection, connection);
        } catch (SQLException e) {
            throw JdbcExecutor.wrapError(e);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(MySQLExecutor.class);

    private MySQLExecutor(JdbcExecutorFactory factory, Connection conn) {
        super(factory, conn);
    }

    @Override
    public boolean isSupportClientStream() {
        //true, MySQL jdbc support
        return true;
    }

    @Override
    Logger getLogger() {
        return LOG;
    }


    @Override
    final Object bind(final PreparedStatement stmt, final int indexBasedOne, final @Nullable Object attr,
                      final MappingType type, final SqlType sqlType, final Object nonNull)
            throws SQLException {
        switch ((MySQLType) sqlType) {
            case BOOLEAN:
                stmt.setBoolean(indexBasedOne, (Boolean) nonNull);
                break;
            case TINYINT:
                stmt.setByte(indexBasedOne, (Byte) nonNull);
                break;
            case TINYINT_UNSIGNED:
            case SMALLINT:
                stmt.setShort(indexBasedOne, (Short) nonNull);
                break;
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INT:
            case YEAR:
                stmt.setInt(indexBasedOne, (Integer) nonNull);
                break;
            case INT_UNSIGNED:
            case BIGINT:
            case BIT:
                stmt.setLong(indexBasedOne, (Long) nonNull);
                break;
            case BIGINT_UNSIGNED: {
                if (!(nonNull instanceof BigInteger || nonNull instanceof BigDecimal)) {
                    throw beforeBindReturnError(sqlType, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull, MysqlType.BIGINT_UNSIGNED);
            }
            break;
            case DECIMAL:
            case DECIMAL_UNSIGNED:
                stmt.setBigDecimal(indexBasedOne, (BigDecimal) nonNull);
                break;
            case FLOAT:
                stmt.setFloat(indexBasedOne, (Float) nonNull);
                break;
            case DOUBLE:
                stmt.setDouble(indexBasedOne, (Double) nonNull);
                break;
            case TIME: {
                final LocalTime value = (LocalTime) nonNull;
                if (this.factory.useSetObjectMethod) {
                    stmt.setObject(indexBasedOne, value, com.mysql.cj.MysqlType.TIME);
                } else {
                    stmt.setTime(indexBasedOne, Time.valueOf(value));
                }
            }
            break;
            case DATE: {
                final LocalDate value = (LocalDate) nonNull;
                if (this.factory.useSetObjectMethod) {
                    stmt.setObject(indexBasedOne, value, com.mysql.cj.MysqlType.DATE);
                } else {
                    stmt.setDate(indexBasedOne, Date.valueOf(value));
                }
            }
            break;
            case DATETIME: {
                final LocalDateTime value = (LocalDateTime) nonNull;
                if (this.factory.useSetObjectMethod) {
                    stmt.setObject(indexBasedOne, value, com.mysql.cj.MysqlType.DATETIME);
                } else {
                    stmt.setTimestamp(indexBasedOne, Timestamp.valueOf(value));
                }
            }
            break;
            case CHAR:
            case VARCHAR:
            case ENUM:
            case SET:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
                stmt.setString(indexBasedOne, (String) nonNull);
                break;
            case JSON:
            case LONGTEXT: {
                setLongText(stmt, indexBasedOne, nonNull);
            }
            break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
                stmt.setBytes(indexBasedOne, (byte[]) nonNull);
                break;
            case LONGBLOB: {
                setLongBinary(stmt, indexBasedOne, nonNull);
            }
            break;
            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTILINESTRING:
            case MULTIPOLYGON:
            case GEOMETRYCOLLECTION: {
                if (nonNull instanceof String) {
                    stmt.setString(indexBasedOne, (String) nonNull);
                } else if (nonNull instanceof Reader) {
                    stmt.setCharacterStream(indexBasedOne, (Reader) nonNull);
                } else if (nonNull instanceof byte[]) {
                    stmt.setBytes(indexBasedOne, (byte[]) nonNull);
                } else if (nonNull instanceof InputStream) {
                    stmt.setBinaryStream(indexBasedOne, (InputStream) nonNull);
                } else if (nonNull instanceof Path) {
                    try (InputStream inputStream = Files.newInputStream((Path) nonNull, StandardOpenOption.READ)) {
                        stmt.setBinaryStream(indexBasedOne, inputStream);
                    } catch (IOException e) {
                        String m = String.format("Parameter[%s] %s[%s] read occur error."
                                , indexBasedOne, Path.class.getName(), nonNull);
                        throw new SQLException(m, e);
                    }
                }
            }
            default:
                throw _Exceptions.unexpectedEnum((MySQLType) sqlType);

        }
        return attr;
    }


    @Override
    SqlType getSqlType(ResultSetMetaData metaData, int indexBasedOne) {
        throw new UnsupportedOperationException();
    }

    @Override
    Object get(final ResultSet resultSet, int indexBasedOne, final SqlType sqlType) throws SQLException {
        final Object value;
        switch ((MySQLType) sqlType) {
            case TINYINT:
            case TINYINT_UNSIGNED:
            case SMALLINT:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INT:
            case YEAR:
                value = resultSet.getObject(indexBasedOne, Integer.class);
                break;
            case INT_UNSIGNED:
            case BIGINT:
            case BIT:
                value = resultSet.getObject(indexBasedOne, Long.class);
                break;
            case DECIMAL:
            case DECIMAL_UNSIGNED:
                value = resultSet.getObject(indexBasedOne, BigDecimal.class);
                break;
            case BOOLEAN:
                value = resultSet.getObject(indexBasedOne, Boolean.class);
                break;
            case DATETIME:
                value = resultSet.getObject(indexBasedOne, LocalDateTime.class);
                break;
            case DATE:
                value = resultSet.getObject(indexBasedOne, LocalDate.class);
                break;
            case TIME:
                value = resultSet.getObject(indexBasedOne, LocalTime.class);
                break;
            case CHAR:
            case VARCHAR:
            case ENUM:
            case SET:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
            case JSON:
                value = resultSet.getObject(indexBasedOne, String.class);
                break;
            case FLOAT:
                value = resultSet.getObject(indexBasedOne, Float.class);
                break;
            case DOUBLE:
                value = resultSet.getObject(indexBasedOne, Double.class);
                break;
            case BIGINT_UNSIGNED:
                value = resultSet.getObject(indexBasedOne, BigInteger.class);
                break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
                //
            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTIPOLYGON:
            case MULTILINESTRING:
            case GEOMETRYCOLLECTION:
                value = resultSet.getObject(indexBasedOne, byte[].class);
                break;
            default:
                throw _Exceptions.unexpectedEnum((MySQLType) sqlType);
        }
        return value;
    }


    private static class MySQLLocalExecutor extends MySQLExecutor implements LocalStmtExecutor {

        private MySQLLocalExecutor(JdbcLocalExecutorFactory factory, Connection conn) {
            super(factory, conn);
        }


    }//MySQLLocalExecutor

    private static class MySQLLocalHolderExecutor extends MySQLLocalExecutor implements DatabaseSessionHolder {

        private MySQLLocalHolderExecutor(JdbcLocalExecutorFactory factory, Connection conn) {
            super(factory, conn);
        }

        @Override
        public Object databaseSession() {
            return this.conn;
        }

    }//MySQLLocalExecutor


    private static final class MySQLRmExecutor extends MySQLExecutor implements RmStmtExecutor {

        private final XAConnection xaConnection;


        private MySQLRmExecutor(JdbcRmExecutorFactory factory, XAConnection xaConnection, Connection connection) {
            super(factory, connection);
            this.xaConnection = xaConnection;
        }


    }//MySQLRmExecutor


}
