package io.army.jdbc;

import com.mysql.cj.MysqlType;
import io.army.mapping.MappingType;
import io.army.session.*;
import io.army.sqltype.MySQLType;
import io.army.sqltype.SqlType;
import io.army.sync.executor.SyncLocalStmtExecutor;
import io.army.sync.executor.SyncRmStmtExecutor;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
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
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

abstract class MySQLExecutor extends JdbcExecutor {

    static SyncLocalStmtExecutor localExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        return new LocalExecutor(factory, conn, sessionName);
    }

    static SyncRmStmtExecutor rmExecutor(JdbcExecutorFactory factory, Object conn, String sessionName) {
        try {
            final Connection connection;
            connection = xaConnection.getConnection();
            return new MySQLRmExecutor(factory, xaConnection, connection);
        } catch (SQLException e) {
            throw JdbcExecutor.wrapError(e);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(MySQLExecutor.class);


    private TransactionInfo info;

    private MySQLExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        super(factory, conn, sessionName);
    }


    @Override
    public final TransactionInfo transactionInfo() throws DataAccessException {
        return null;
    }

    @Override
    public final void setTransactionCharacteristics(TransactionOption option) throws DataAccessException {

    }

    @Nullable
    @Override
    public final <T> T valueOf(Option<T> option) {
        return null;
    }

    @Override
    final Logger getLogger() {
        return LOG;
    }

    @Nullable
    @Override
    final TransactionInfo obtainTransaction() {
        return this.info;
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


    private static class LocalExecutor extends MySQLExecutor implements SyncLocalStmtExecutor {

        private LocalExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
            super(factory, conn, sessionName);
        }


        @Override
        public TransactionInfo startTransaction(TransactionOption option, HandleMode mode) {
            return null;
        }

        @Nullable
        @Override
        public TransactionInfo commit(Function<Option<?>, ?> optionFunc) {
            return null;
        }

        @Nullable
        @Override
        public TransactionInfo rollback(Function<Option<?>, ?> optionFunc) {
            return null;
        }


    } // MySQLLocalExecutor


    private static final class MySQLRmExecutor extends MySQLExecutor implements SyncRmStmtExecutor {


        public MySQLRmExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
            super(factory, conn, sessionName);
        }

        @Override
        public TransactionInfo start(Xid xid, int flags, TransactionOption option) {
            return null;
        }

        @Override
        public TransactionInfo end(Xid xid, int flags, Function<Option<?>, ?> optionFunc) {
            return null;
        }

        @Override
        public int prepare(Xid xid, Function<Option<?>, ?> optionFunc) {
            return 0;
        }

        @Override
        public void commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc) {

        }

        @Override
        public void rollback(Xid xid, Function<Option<?>, ?> optionFunc) {

        }

        @Override
        public void forget(Xid xid, Function<Option<?>, ?> optionFunc) {

        }

        @Override
        public List<Xid> recover(int flags, Function<Option<?>, ?> optionFunc) {
            return null;
        }

        @Override
        public Stream<Xid> recoverStream(int flags, Function<Option<?>, ?> optionFunc) {
            return null;
        }

        @Override
        public boolean isSupportForget() {
            return false;
        }

        @Override
        public int startSupportFlags() {
            return 0;
        }

        @Override
        public int endSupportFlags() {
            return 0;
        }

        @Override
        public int recoverSupportFlags() {
            return 0;
        }

        @Override
        public boolean isSameRm(Session.XaTransactionSupportSpec s) throws SessionException {
            return false;
        }


    }//MySQLRmExecutor


}
