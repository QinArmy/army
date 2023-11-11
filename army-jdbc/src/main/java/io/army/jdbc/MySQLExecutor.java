package io.army.jdbc;

import com.mysql.cj.MysqlType;
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.mapping.MappingType;
import io.army.session.*;
import io.army.session.record.DataRecord;
import io.army.sqltype.MySQLType;
import io.army.sqltype.SqlType;
import io.army.sync.StreamOption;
import io.army.sync.executor.SyncLocalStmtExecutor;
import io.army.sync.executor.SyncRmStmtExecutor;
import io.army.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.sql.XAConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
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


    final Isolation executeStartTransaction(final int stmtCount, final @Nullable Isolation isolation,
                                            final StringBuilder builder) throws DataAccessException {

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

    /*-------------------below private static  -------------------*/

    private static Isolation getMySqlIsolation(final ResultSet rs) throws SQLException {
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


    private static final class LocalExecutor extends MySQLExecutor implements SyncLocalStmtExecutor {

        private static final Option<Boolean> WITH_CONSISTENT_SNAPSHOT = Option.from("WITH CONSISTENT SNAPSHOT", Boolean.class);


        private TransactionInfo transactionInfo;

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
                                .append(_Constant.SPACE_SEMICOLON_SPACE);
                        stmtCount++;
                        break;
                    case ROLLBACK_IF_EXISTS:
                        builder.append(ROLLBACK)
                                .append(_Constant.SPACE_SEMICOLON_SPACE);
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
                builder.append(_Constant.SPACE_SEMICOLON_SPACE);
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
                builder.append(_Constant.SPACE_COMMA_SPACE)
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
            this.transactionInfo = info = TransactionInfo.info(true, finalIsolation, readOnly, optionFunc);
            return info;
        }

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/commit.html">COMMIT Statement</a>
         */
        @Nullable
        @Override
        public TransactionInfo commit(final Function<Option<?>, ?> optionFunc) {
            return commitOrRollback(true, optionFunc);
        }

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/commit.html">ROLLBACK Statement</a>
         */
        @Nullable
        @Override
        public TransactionInfo rollback(final Function<Option<?>, ?> optionFunc) {
            return commitOrRollback(false, optionFunc);
        }

        @Nullable
        @Override
        TransactionInfo obtainTransaction() {
            return this.transactionInfo;
        }


        @Nullable
        private TransactionInfo commitOrRollback(final boolean commit, final Function<Option<?>, ?> optionFunc)
                throws DataAccessException {

            final Object chain, release;
            if (optionFunc == Option.EMPTY_OPTION_FUNC) {
                chain = release = null;
            } else {
                chain = optionFunc.apply(Option.CHAIN);
                release = optionFunc.apply(Option.RELEASE);
            }
            if (Boolean.TRUE.equals(chain) && Boolean.TRUE.equals(release)) {
                String m = String.format("%s[true] and %s[true] conflict", Option.CHAIN.name(), Option.RELEASE.name());
                throw new IllegalArgumentException(m);
            }

            final StringBuilder builder = new StringBuilder(20);
            if (commit) {
                builder.append(COMMIT);
            } else {
                builder.append(ROLLBACK);
            }

            final TransactionInfo newInfo;
            if (chain instanceof Boolean) {
                builder.append(_Constant.SPACE_AND);
                if (!((Boolean) chain)) {
                    builder.append(" NO");
                    newInfo = null;
                } else if ((newInfo = this.transactionInfo) == null) {
                    throw new DataAccessException("CHAIN option can only be used in transaction blocks ");
                }
                builder.append(" CHAIN");
            } else {
                newInfo = null;
            }

            if (release instanceof Boolean) {
                if (!((Boolean) release)) {
                    builder.append(" NO");
                }
                builder.append(" RELEASE");
            }

            try (Statement statement = this.conn.createStatement()) {
                statement.executeUpdate(builder.toString());

                this.transactionInfo = newInfo;
                return newInfo;
            } catch (Exception e) {
                throw handleException(e);
            }
        }


    } // LocalExecutor


    private static class RmExecutor extends MySQLExecutor implements SyncRmStmtExecutor {

        private TransactionInfo transactionInfo;

        private RmExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
            super(factory, conn, sessionName);
        }

        /**
         * <p>the conversion process of xid is same with MySQL Connector/J .
         * <br/>
         *
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xa-statements.html">XA Transaction SQL Statements</a>
         */
        @Override
        public final TransactionInfo start(final Xid xid, final int flags, final TransactionOption option)
                throws RmSessionException {

            if (this.transactionInfo != null) {
                throw _Exceptions.xaBusyOnOtherTransaction();
            }

            final StringBuilder builder = new StringBuilder(140);
            final Isolation isolation;
            isolation = option.isolation();

            int stmtCount = 0;
            final String setTransactionSpace = "SET TRANSACTION ";
            if (isolation == null) {
                builder.append("SET @@transaction_isolation =  @@SESSION.transaction_isolation ; ") // here,must guarantee isolation is session isolation
                        .append("SELECT @@SESSION.transaction_isolation AS txIsolationLevel ; ")
                        .append(setTransactionSpace);
                stmtCount += 2;
            } else {
                builder.append(setTransactionSpace)
                        .append("ISOLATION LEVEL ");
                standardIsolation(isolation, builder);
                builder.append(_Constant.SPACE_COMMA_SPACE);
            }

            final boolean readOnly;
            readOnly = option.isReadOnly();

            if (readOnly) {
                builder.append(READ_ONLY);
            } else {
                builder.append(READ_WRITE);
            }
            stmtCount++;

            builder.append(_Constant.SPACE_SEMICOLON_SPACE)
                    .append("XA START");

            xidToString(xid, builder);

            if ((flags & (~startSupportFlags())) != 0) {
                throw _Exceptions.xaInvalidFlag(flags, "start");
            } else if ((flags & RmSession.TM_JOIN) != 0) {
                builder.append(" JOIN");
            } else if ((flags & RmSession.TM_RESUME) != 0) {
                builder.append(" RESUME");
            }

            stmtCount++;

            final Isolation finalIsolation;
            finalIsolation = executeStartTransaction(stmtCount, isolation, builder);

            final Map<Option<?>, Object> map = _Collections.hashMap(6);

            map.put(Option.XID, xid);
            map.put(Option.XA_FLAGS, flags);
            map.put(Option.XA_STATES, XaStates.ACTIVE);

            final TransactionInfo info;
            this.transactionInfo = info = TransactionInfo.info(true, finalIsolation, readOnly, map::get);

            return info;
        }

        /**
         * <p>the conversion process of xid is same with MySQL Connector/J .
         * <br/>
         *
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xa-statements.html">XA Transaction SQL Statements</a>
         */
        @Override
        public final TransactionInfo end(final Xid xid, final int flags, Function<Option<?>, ?> optionFunc) {

            final TransactionInfo info = this.transactionInfo;

            final Xid infoXid;
            if (info == null || (infoXid = info.valueOf(Option.XID)) == null || !infoXid.equals(xid)) {
                throw _Exceptions.xaNonCurrentTransaction(xid); // here use xid
            } else if (info.nonNullOf(Option.XA_STATES) != XaStates.ACTIVE) {
                throw _Exceptions.xaTransactionDontSupportEndCommand(infoXid, info.nonNullOf(Option.XA_STATES));
            } else if (((~endSupportFlags()) & flags) != 0) {
                throw _Exceptions.xaInvalidFlag(flags, "end");
            }

            final StringBuilder builder = new StringBuilder(140);
            builder.append("XA END");

            xidToString(infoXid, builder);

            if ((flags & RmSession.TM_SUSPEND) != 0) {
                builder.append(" SUSPEND");
            }

            try (Statement statement = this.conn.createStatement()) {

                statement.executeUpdate(builder.toString());

                final Map<Option<?>, Object> map = _Collections.hashMap(6);

                map.put(Option.XID, infoXid); // same instance
                map.put(Option.XA_FLAGS, flags);
                map.put(Option.XA_STATES, XaStates.IDLE);

                final TransactionInfo newInfo;
                this.transactionInfo = newInfo = TransactionInfo.info(true, info.isolation(), info.isReadOnly(), map::get);

                return newInfo;
            } catch (Exception e) {
                throw handleException(e);
            }
        }

        /**
         * <p>the conversion process of xid is same with MySQL Connector/J .
         * <br/>
         *
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xa-statements.html">XA Transaction SQL Statements</a>
         */
        @Override
        public final int prepare(final Xid xid, Function<Option<?>, ?> optionFunc) {

            final TransactionInfo info = this.transactionInfo;

            final Xid infoXid;
            if (info == null || (infoXid = info.valueOf(Option.XID)) == null || !infoXid.equals(xid)) {
                throw _Exceptions.xaNonCurrentTransaction(xid); // here use xid
            } else if (info.nonNullOf(Option.XA_STATES) != XaStates.IDLE) {
                throw _Exceptions.xaTransactionDontSupportEndCommand(infoXid, info.nonNullOf(Option.XA_STATES));
            } else if ((info.nonNullOf(Option.XA_FLAGS) & RmSession.TM_FAIL) != 0) {
                throw _Exceptions.xaTransactionRollbackOnly(infoXid);
            }

            final StringBuilder builder = new StringBuilder(140);

            final boolean readOnly = info.isReadOnly();
            if (readOnly) {
                builder.append("XA COMMIT");
            } else {
                builder.append("XA PREPARE");
            }

            xidToString(infoXid, builder);

            if (readOnly) {
                builder.append(" ONE PHASE");
            }

            try (Statement statement = this.conn.createStatement()) {

                statement.executeUpdate(builder.toString());

                this.transactionInfo = null; // clear current transaction info
                return readOnly ? RmSession.XA_RDONLY : RmSession.XA_OK;
            } catch (Exception e) {
                throw handleException(e);
            }
        }

        /**
         * <p>the conversion process of xid is same with MySQL Connector/J .
         * <br/>
         *
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xa-statements.html">XA Transaction SQL Statements</a>
         */
        @Override
        public final void commit(final Xid xid, final int flags, Function<Option<?>, ?> optionFunc) {

            if (((~commitSupportFlags()) & flags) != 0) {
                throw _Exceptions.xaInvalidFlag(flags, "commit");
            }

            final StringBuilder builder = new StringBuilder(140);
            builder.append("XA COMMIT");

            final TransactionInfo info;
            final Xid infoXid;

            if ((flags & RmSession.TM_ONE_PHASE) == 0) { // two phase commit
                xidToString(xid, builder);
            } else if ((info = this.transactionInfo) == null
                    || (infoXid = info.valueOf(Option.XID)) == null
                    || !infoXid.equals(xid)) {
                throw _Exceptions.xaNonCurrentTransaction(xid); // here use xid
            } else if (info.nonNullOf(Option.XA_STATES) != XaStates.IDLE) {
                throw _Exceptions.xaTransactionDontSupportEndCommand(infoXid, info.nonNullOf(Option.XA_STATES));
            } else if ((info.nonNullOf(Option.XA_FLAGS) & RmSession.TM_FAIL) != 0) {
                throw _Exceptions.xaTransactionRollbackOnly(infoXid);
            } else {
                xidToString(infoXid, builder);
                builder.append(" ONE PHASE");
            }

            try (Statement statement = this.conn.createStatement()) {

                statement.executeUpdate(builder.toString());

                if ((flags & RmSession.TM_ONE_PHASE) != 0) {
                    this.transactionInfo = null; // clear for one phase
                }
            } catch (Exception e) {
                throw handleException(e);
            }

        }

        /**
         * <p>the conversion process of xid is same with MySQL Connector/J .
         * <br/>
         *
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xa-statements.html">XA Transaction SQL Statements</a>
         */
        @Override
        public final void rollback(final Xid xid, Function<Option<?>, ?> optionFunc) {

            final TransactionInfo info = this.transactionInfo;

            final Xid infoXid, actualXid;
            final boolean onePhaseRollback;
            if (info != null
                    && (infoXid = info.valueOf(Option.XID)) != null
                    && infoXid.equals(xid)) {
                // rollback current transaction
                if (infoXid.nonNullOf(Option.XA_STATES) != XaStates.IDLE) {
                    throw _Exceptions.xaStatesDontSupportRollbackCommand(infoXid, infoXid.nonNullOf(Option.XA_STATES));
                }
                actualXid = infoXid;
                onePhaseRollback = true;
            } else {
                actualXid = xid;
                onePhaseRollback = false;
            }


            final StringBuilder builder = new StringBuilder(140);
            builder.append("XA ROLLBACK");

            xidToString(actualXid, builder);

            try (Statement statement = this.conn.createStatement()) {

                statement.executeUpdate(builder.toString());

                if (onePhaseRollback) {
                    this.transactionInfo = null; // clear for one phase rollback
                }
            } catch (Exception e) {
                throw handleException(e);
            }
        }

        @Override
        public final void forget(Xid xid, Function<Option<?>, ?> optionFunc) throws RmSessionException {
            throw _Exceptions.xaDontSupportForget(Database.MySQL);
        }

        @Override
        public final Stream<Xid> recover(final int flags, Function<Option<?>, ?> optionFunc, StreamOption option) {

            if (((~recoverSupportFlags()) & flags) != 0) {
                throw _Exceptions.xaInvalidFlag(flags, "recover");
            }

            if ((flags & RmSession.TM_START_RSCAN) != 0) {
                return Stream.empty();
            }
            return jdbcRecover("XA RECOVER CONVERT XID", this::recordToXid, option);
        }

        @Override
        public final boolean isSupportForget() {
            // always false, MySQL don't support forget
            return false;
        }

        @Override
        public final int startSupportFlags() {
            return (RmSession.TM_JOIN | RmSession.TM_RESUME);
        }

        @Override
        public final int endSupportFlags() {
            return (RmSession.TM_SUCCESS | RmSession.TM_SUSPEND | RmSession.TM_FAIL);
        }

        @Override
        public final int commitSupportFlags() {
            return RmSession.TM_ONE_PHASE;
        }

        @Override
        public final int recoverSupportFlags() {
            return (RmSession.TM_START_RSCAN | RmSession.TM_END_RSCAN | RmSession.TM_NO_FLAGS);
        }

        @Override
        public final boolean isSameRm(final Session.XaTransactionSupportSpec s) throws SessionException {
            try {
                return s instanceof XaRmExecutor
                        && this instanceof XaRmExecutor
                        && ((XaRmExecutor) this).xaCon.getXAResource().isSameRM(((XaRmExecutor) s).xaCon.getXAResource());
            } catch (Exception e) {
                throw handleException(e);
            }
        }

        @Nullable
        @Override
        TransactionInfo obtainTransaction() {
            return this.transactionInfo;
        }


        private void xidToString(final Xid xid, final StringBuilder builder) {
            final String gtrid, bqual;
            gtrid = xid.getGtrid();
            bqual = xid.getBqual();

            final byte[] gtridBytes, bqualBytes, formatIdBytes;

            if (!_StringUtils.hasText(gtrid)) {
                throw _Exceptions.xaGtridNoText();
            } else if ((gtridBytes = gtrid.getBytes(StandardCharsets.UTF_8)).length > 64) {
                throw _Exceptions.xaGtridBeyond64Bytes();
            }

            builder.append(" 0x")
                    .append(HexUtils.hexEscapesText(true, gtridBytes, gtridBytes.length));

            builder.append(_Constant.COMMA);
            if (bqual != null) {
                if ((bqualBytes = bqual.getBytes(StandardCharsets.UTF_8)).length > 64) {
                    throw _Exceptions.xaBqualBeyond64Bytes();
                }
                builder.append("0x")
                        .append(HexUtils.hexEscapesText(true, bqualBytes, bqualBytes.length));
            }
            final int formatId;
            formatId = xid.getFormatId();

            builder.append(",0x");
            if (formatId == 0) {
                builder.append('0');
            } else {
                formatIdBytes = NumberUtils.toBinaryBytes(formatId, true);
                int offset = 0;
                for (; offset < formatIdBytes.length; offset++) {
                    if (formatIdBytes[offset] != 0) {
                        break;
                    }
                }
                builder.append(HexUtils.hexEscapesText(true, formatIdBytes, offset, formatIdBytes.length));
            }

        }


        @Nullable
        private Xid recordToXid(final DataRecord row) {
            final int formatId, gtridLength, bqualLength;

            formatId = row.getNonNull(0, Integer.class); // formatID
            gtridLength = row.getNonNull(1, Integer.class); // gtrid_length
            bqualLength = row.getNonNull(2, Integer.class); // bqual_length

            final String hexString;
            hexString = row.getNonNull(3, String.class); // data

            assert hexString.startsWith("0x") : "mysql XA RECOVER convert xid response error";

            final byte[] idBytes;
            idBytes = HexUtils.decodeHex(hexString.substring(2).getBytes(StandardCharsets.UTF_8));

            final String gtrid, bqual;
            if (gtridLength == 0) {
                return null;  // non-jdbd create xid
            }
            gtrid = new String(idBytes, 0, gtridLength);
            if (bqualLength == 0) {
                bqual = null;
            } else {
                bqual = new String(idBytes, gtridLength, bqualLength);
            }
            return Xid.from(gtrid, bqual, formatId);
        }


    } // RmExecutor


    private static final class XaRmExecutor extends RmExecutor implements XaConnectionExecutor {

        private final XAConnection xaCon;

        private XaRmExecutor(JdbcExecutorFactory factory, XAConnection xaConn, Connection conn, String sessionName) {
            super(factory, conn, sessionName);

            this.xaCon = xaConn;
        }

        @Override
        public XAConnection getXAConnection() {
            return this.xaCon;
        }

        @Override
        public void closeXaConnection() throws SQLException {
            this.xaCon.close();
        }


    } // XaRmExecutor


}
