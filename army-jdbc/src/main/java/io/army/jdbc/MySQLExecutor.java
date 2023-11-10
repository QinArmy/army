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
import java.util.function.Function;
import java.util.stream.Stream;

abstract class MySQLExecutor extends JdbcExecutor {

    static SyncLocalStmtExecutor localExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        return new LocalExecutor(factory, conn, sessionName);
    }

    static SyncRmStmtExecutor rmExecutor(JdbcExecutorFactory factory, final Object connObj, String sessionName) {
        try {
            final SyncRmStmtExecutor executor;

            if (connObj instanceof Connection) {
                executor = new RmExecutor(factory, (Connection) connObj, sessionName);
            } else if (connObj instanceof XAConnection) {
                final XAConnection xaConn = (XAConnection) connObj;
                final Connection conn;
                conn = ((XAConnection) connObj).getConnection();

                executor = new XaRmExecutor(factory, xaConn, conn, sessionName);
            } else {
                throw new IllegalArgumentException();
            }

            return executor;
        } catch (SQLException e) {
            throw JdbcExecutor.wrapError(e);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(MySQLExecutor.class);

    private static final String SEMICOLON = ";";
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


    final Isolation getMySqlIsolation(final ResultSet rs) throws SQLException {
        try (ResultSet resultSet = rs) {
            if (!resultSet.next()) {
                // no bug,never here
                throw new DataAccessException("isolation no result");
            }
            final Isolation isolation;
            switch (resultSet.getString(1)) {
                case "READ-COMMITTED":
                    isolation = Isolation.READ_COMMITTED;
                    break;
                case "REPEATABLE-READ":
                    isolation = Isolation.REPEATABLE_READ;
                    break;
                case "SERIALIZABLE":
                    isolation = Isolation.SERIALIZABLE;
                    break;
                case "READ-UNCOMMITTED":
                    isolation = Isolation.READ_UNCOMMITTED;
                    break;
                default:
                    throw unknownIsolation(resultSet.getString(1));

            }
            return isolation;
        }
    }


    private static class LocalExecutor extends MySQLExecutor implements SyncLocalStmtExecutor {

        private static final Option<Boolean> WITH_CONSISTENT_SNAPSHOT = Option.from("WITH CONSISTENT SNAPSHOT", Boolean.class);

        private LocalExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
            super(factory, conn, sessionName);
        }

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/commit.html">START TRANSACTION Statement</a>
         */
        @Override
        public TransactionInfo startTransaction(final TransactionOption option, final HandleMode mode) {

            final StringBuilder builder = new StringBuilder(168);

            int stmtCount = 0;
            if (inTransaction()) {
                switch (mode) {
                    case ERROR_IF_EXISTS:
                        throw transactionExistsRejectStart(this.sessionName);
                    case COMMIT_IF_EXISTS:
                        builder.append(COMMIT)
                                .append(SPACE_SEMICOLON_SPACE);
                        stmtCount++;
                        break;
                    case ROLLBACK_IF_EXISTS:
                        builder.append(ROLLBACK)
                                .append(SPACE_SEMICOLON_SPACE);
                        stmtCount++;
                        break;
                    default:
                        throw _Exceptions.unexpectedEnum(mode);
                }
            }


            final Isolation isolation;
            isolation = option.isolation();

            if (isolation == null) {
                builder.append("SET @@transaction_isolation = @@SESSION.transaction_isolation ; "); // here,must guarantee isolation is session isolation , must tail space
                builder.append("SELECT @@SESSION.transaction_isolation AS txIsolationLevel ; ");
                stmtCount += 2;
            } else {
                builder.append("SET TRANSACTION ISOLATION LEVEL ");
                standardIsolation(isolation, builder);
                builder.append(SPACE_SEMICOLON_SPACE);
                stmtCount++;
            }

            builder.append(START_TRANSACTION_SPACE);
            final boolean readOnly = option.isReadOnly();
            if (readOnly) {
                builder.append(READ_ONLY);
            } else {
                builder.append(READ_WRITE);
            }

            final Boolean consistentSnapshot;
            consistentSnapshot = option.valueOf(WITH_CONSISTENT_SNAPSHOT);

            if (Boolean.TRUE.equals(consistentSnapshot)) {
                builder.append(SPACE_COMMA_SPACE)
                        .append("WITH CONSISTENT SNAPSHOT");
            }

            stmtCount++;

            final Function<Option<?>, ?> optionFunc;
            if (consistentSnapshot != null && consistentSnapshot) {
                optionFunc = Option.singleFunc(WITH_CONSISTENT_SNAPSHOT, Boolean.TRUE);
            } else {
                optionFunc = Option.EMPTY_OPTION_FUNC;
            }

            // execute start transaction statements
            final Isolation finalIsolation;
            finalIsolation = executeStartTransaction(stmtCount, isolation, builder);

            final TransactionInfo info;
            ((MySQLExecutor) this).info = info = TransactionInfo.info(true, finalIsolation, readOnly, optionFunc);
            return info;
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


        private Isolation executeStartTransaction(final int stmtCount, final @Nullable Isolation isolation,
                                                  final StringBuilder builder) {

            try (final Statement statement = this.conn.createStatement()) {

                Isolation sessionIsolation = null;
                int batchSize = 0;
                if (this.factory.useMultiStmt) {
                    if (statement.execute(builder.toString())) {
                        statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                        // no bug never here
                        throw new IllegalStateException("sql error");
                    } else if (statement.getUpdateCount() == -1) {
                        throw multiStatementLessThanExpected(0, stmtCount); // no result
                    }
                    batchSize++;
                    while (true) {
                        if (statement.getMoreResults()) {
                            assert sessionIsolation == null;
                            sessionIsolation = getMySqlIsolation(statement.getResultSet());
                        } else if (statement.getUpdateCount() == -1) {
                            break;
                        }
                        batchSize++;
                    }
                } else if (isolation == null) {
                    int start = 0;
                    String sql;

                    for (int semicolon; (semicolon = builder.indexOf(SEMICOLON, start)) > 0; start = semicolon + 1) {
                        sql = builder.substring(start, semicolon);
                        batchSize++;
                        if (sql.startsWith(" SELECT")) {
                            assert sessionIsolation == null;
                            sessionIsolation = getMySqlIsolation(statement.executeQuery(sql));
                        } else {
                            statement.executeUpdate(sql);
                        }
                    }
                    statement.executeUpdate(builder.substring(start, builder.length()));
                    batchSize++;
                    assert sessionIsolation != null;
                } else {
                    int start = 0;
                    for (int semicolon; (semicolon = builder.indexOf(SEMICOLON, start)) > 0; start = semicolon + 1) {
                        statement.addBatch(builder.substring(start, semicolon));
                        batchSize++;
                    }
                    statement.addBatch(builder.substring(start, builder.length()));
                    batchSize++;
                    statement.executeBatch();
                }

                assert batchSize == stmtCount;

                final Isolation finalIsolation;
                if (isolation == null) {
                    assert sessionIsolation != null;
                    finalIsolation = sessionIsolation;
                } else {
                    finalIsolation = isolation;
                }
                return finalIsolation;
            } catch (Exception e) {
                throw handleException(e);
            }
        }


    } // LocalExecutor


    private static class RmExecutor extends MySQLExecutor implements SyncRmStmtExecutor {


        private RmExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
            super(factory, conn, sessionName);
        }

        @Override
        public final TransactionInfo start(Xid xid, int flags, TransactionOption option) {
            return null;
        }

        @Override
        public final TransactionInfo end(Xid xid, int flags, Function<Option<?>, ?> optionFunc) {
            return null;
        }

        @Override
        public final int prepare(Xid xid, Function<Option<?>, ?> optionFunc) {
            return 0;
        }

        @Override
        public final void commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc) {

        }

        @Override
        public final void rollback(Xid xid, Function<Option<?>, ?> optionFunc) {

        }

        @Override
        public final void forget(Xid xid, Function<Option<?>, ?> optionFunc) {

        }

        @Override
        public final Stream<Xid> recoverStream(int flags, Function<Option<?>, ?> optionFunc) {
            return null;
        }

        @Override
        public final boolean isSupportForget() {
            return false;
        }

        @Override
        public final int startSupportFlags() {
            return 0;
        }

        @Override
        public final int endSupportFlags() {
            return 0;
        }

        @Override
        public final int recoverSupportFlags() {
            return 0;
        }

        @Override
        public final boolean isSameRm(Session.XaTransactionSupportSpec s) throws SessionException {
            return false;
        }


    } // RmExecutor


    private static final class XaRmExecutor extends RmExecutor implements XaConnectionExecutor {

        private final XAConnection xaCon;

        private XaRmExecutor(JdbcExecutorFactory factory, XAConnection xaConn, Connection conn, String sessionName) {
            super(factory, conn, sessionName);

            this.xaCon = xaConn;
        }


        @Override
        public void closeXaConnection() throws SQLException {
            this.xaCon.close();
        }


    } // XaRmExecutor


}
