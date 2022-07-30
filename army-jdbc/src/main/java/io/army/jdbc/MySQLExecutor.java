package io.army.jdbc;

import io.army.sqltype.MySqlType;
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

    static MySQLLocalExecutor localExecutor(JdbcLocalExecutorFactory factory, Connection conn) {
        return new MySQLLocalExecutor(factory, conn);
    }

    static MySQLRmExecutor rmExecutor(final JdbcRmExecutorFactory factory, final XAConnection xaConnection) {
        try {
            final Connection connection;
            connection = xaConnection.getConnection();
            return new MySQLRmExecutor(factory, xaConnection, connection);
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(MySQLExecutor.class);

    private MySQLExecutor(JdbcExecutorFactory factory, Connection conn) {
        super(factory, conn);
    }


    @Override
    Logger getLogger() {
        return LOG;
    }

    @Override
    void bind(final PreparedStatement stmt, final int index, final SqlType sqlDataType, final Object nonNull)
            throws SQLException {
        switch ((MySqlType) sqlDataType) {
            case BOOLEAN:
                stmt.setBoolean(index, (Boolean) nonNull);
                break;
            case TINYINT:
                stmt.setByte(index, (Byte) nonNull);
                break;
            case TINYINT_UNSIGNED:
            case SMALLINT:
                stmt.setShort(index, (Short) nonNull);
                break;
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INT:
            case YEAR:
                stmt.setInt(index, (Integer) nonNull);
                break;
            case INT_UNSIGNED:
            case BIGINT:
            case BIT:
                stmt.setLong(index, (Long) nonNull);
                break;
            case BIGINT_UNSIGNED: {
                if (!(nonNull instanceof BigInteger || nonNull instanceof BigDecimal)) {
                    throw beforeBindReturnError(sqlDataType, nonNull);
                }
                stmt.setObject(index, nonNull, com.mysql.cj.MysqlType.BIGINT_UNSIGNED);
            }
            break;
            case DECIMAL:
            case DECIMAL_UNSIGNED:
                stmt.setBigDecimal(index, (BigDecimal) nonNull);
                break;
            case FLOAT:
                stmt.setFloat(index, (Float) nonNull);
                break;
            case DOUBLE:
                stmt.setDouble(index, (Double) nonNull);
                break;
            case TIME: {
                final LocalTime value = (LocalTime) nonNull;
                if (this.factory.useSetObjectMethod) {
                    stmt.setObject(index, value, com.mysql.cj.MysqlType.TIME);
                } else {
                    stmt.setTime(index, Time.valueOf(value));
                }
            }
            break;
            case DATE: {
                final LocalDate value = (LocalDate) nonNull;
                if (this.factory.useSetObjectMethod) {
                    stmt.setObject(index, value, com.mysql.cj.MysqlType.DATE);
                } else {
                    stmt.setDate(index, Date.valueOf(value));
                }
            }
            break;
            case DATETIME: {
                final LocalDateTime value = (LocalDateTime) nonNull;
                if (this.factory.useSetObjectMethod) {
                    stmt.setObject(index, value, com.mysql.cj.MysqlType.DATETIME);
                } else {
                    stmt.setTimestamp(index, Timestamp.valueOf(value));
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
                stmt.setString(index, (String) nonNull);
                break;
            case JSON:
            case LONGTEXT: {
                setLongText(stmt, index, nonNull);
            }
            break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
                stmt.setBytes(index, (byte[]) nonNull);
                break;
            case LONGBLOB: {
                setLongBinary(stmt, index, nonNull);
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
                    stmt.setString(index, (String) nonNull);
                } else if (nonNull instanceof Reader) {
                    stmt.setCharacterStream(index, (Reader) nonNull);
                } else if (nonNull instanceof byte[]) {
                    stmt.setBytes(index, (byte[]) nonNull);
                } else if (nonNull instanceof InputStream) {
                    stmt.setBinaryStream(index, (InputStream) nonNull);
                } else if (nonNull instanceof Path) {
                    try (InputStream inputStream = Files.newInputStream((Path) nonNull, StandardOpenOption.READ)) {
                        stmt.setBinaryStream(index, inputStream);
                    } catch (IOException e) {
                        String m = String.format("Parameter[%s] %s[%s] read occur error."
                                , index, Path.class.getName(), nonNull);
                        throw new SQLException(m, e);
                    }
                }
            }
            default:
                throw _Exceptions.unexpectedEnum((MySqlType) sqlDataType);

        }

    }

    @Override
    Object get(final ResultSet resultSet, final String alias, final SqlType sqlType) throws SQLException {
        final Object value;
        switch ((MySqlType) sqlType) {
            case TINYINT:
            case TINYINT_UNSIGNED:
            case SMALLINT:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INT:
            case YEAR:
                value = resultSet.getObject(alias, Integer.class);
                break;
            case INT_UNSIGNED:
            case BIGINT:
            case BIT:
                value = resultSet.getObject(alias, Long.class);
                break;
            case DECIMAL:
            case DECIMAL_UNSIGNED:
                value = resultSet.getObject(alias, BigDecimal.class);
                break;
            case BOOLEAN:
                value = resultSet.getObject(alias, Boolean.class);
                break;
            case DATETIME:
                value = resultSet.getObject(alias, LocalDateTime.class);
                break;
            case DATE:
                value = resultSet.getObject(alias, LocalDate.class);
                break;
            case TIME:
                value = resultSet.getObject(alias, LocalTime.class);
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
                value = resultSet.getObject(alias, String.class);
                break;
            case FLOAT:
                value = resultSet.getObject(alias, Float.class);
                break;
            case DOUBLE:
                value = resultSet.getObject(alias, Double.class);
                break;
            case BIGINT_UNSIGNED:
                value = resultSet.getObject(alias, BigInteger.class);
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
                value = resultSet.getObject(alias, byte[].class);
                break;
            default:
                throw _Exceptions.unexpectedEnum((MySqlType) sqlType);
        }
        return value;
    }


    private static final class MySQLLocalExecutor extends MySQLExecutor implements LocalStmtExecutor {

        private MySQLLocalExecutor(JdbcLocalExecutorFactory factory, Connection conn) {
            super(factory, conn);
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
