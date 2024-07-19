/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.jdbc;

import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.executor.DataAccessException;
import io.army.executor.ExecutorSupport;
import io.army.executor.SyncLocalExecutor;
import io.army.executor.SyncRmExecutor;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.option.Option;
import io.army.result.DataRecord;
import io.army.session.*;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.transaction.*;
import io.army.util.HexUtils;
import io.army.util.NumberUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.sql.XAConnection;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.*;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

abstract class MySQLExecutor extends JdbcExecutor {

    static SyncLocalExecutor localExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        return new LocalExecutor(factory, conn, sessionName);
    }

    static SyncRmExecutor rmExecutor(JdbcExecutorFactory factory, final Object connObj, String sessionName) {
        final SyncRmExecutor executor;

        if (connObj instanceof Connection) {
            executor = new RmExecutor(factory, (Connection) connObj, sessionName);
        } else if (connObj instanceof XAConnection) {
            try {
                final XAConnection xaConn = (XAConnection) connObj;
                final Connection conn;
                conn = xaConn.getConnection();

                executor = new XaRmExecutor(factory, xaConn, conn, sessionName);
            } catch (SQLException e) {
                throw JdbcExecutor.wrapError(e);
            }
        } else {
            throw new IllegalArgumentException();
        }

        return executor;
    }

    private static final Logger LOG = LoggerFactory.getLogger(MySQLExecutor.class);


    /**
     * private constructor
     */
    private MySQLExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        super(factory, conn, sessionName);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/server-system-variables.html#sysvar_transaction_isolation">transaction_isolation</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/server-system-variables.html#sysvar_transaction_read_only">transaction_read_only</a>
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/server-system-variables.html#sysvar_tx_isolation">tx_isolation</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/server-system-variables.html#sysvar_tx_read_only">tx_read_only</a>
     */
    @Override
    public final TransactionInfo sessionTransactionCharacteristics(Function<Option<?>, ?> optionFunc) {
        final String sql;
        final ServerMeta serverMeta = this.factory.serverMeta;
        if (serverMeta.meetsMinimum(8, 0, 3)
                || (serverMeta.meetsMinimum(5, 7, 20) && !serverMeta.meetsMinimum(8, 0, 0))) {
            sql = "SELECT @@session.transaction_isolation AS txLevel,@@session.transaction_read_only AS txReadOnly";
        } else {
            sql = "SELECT @@session.tx_isolation AS txLevel,@@session.tx_read_only AS txReadOnly";
        }

        try (Statement statement = this.conn.createStatement()) {

            try (ResultSet resultSet = statement.executeQuery(sql)) {

                if (!resultSet.next()) {
                    throw new DataAccessException("jdbc error");
                }

                final Isolation isolation;
                final boolean readOnly;

                isolation = readIsolation(resultSet.getString(1));
                readOnly = resultSet.getBoolean(2);

                return TransactionInfo.notInTransaction(isolation, readOnly);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/set-transaction.html">SET TRANSACTION Statement</a>
     */
    @Override
    public final void setTransactionCharacteristics(final TransactionOption option) throws DataAccessException {
        final StringBuilder builder = new StringBuilder(30);
        builder.append("SET SESSION TRANSACTION ");

        final Isolation isolation;
        isolation = option.isolation();
        if (isolation != null) {
            builder.append("ISOLATION LEVEL ");
            standardIsolation(isolation, builder);
            builder.append(_Constant.SPACE_COMMA_SPACE);
        }
        if (option.isReadOnly()) {
            builder.append(READ_ONLY);
        } else {
            builder.append(READ_WRITE);
        }

        executeSimpleStaticStatement(builder.toString(), LOG);

    }


    @Override
    final Logger getLogger() {
        return LOG;
    }


    @Override
    final DataType getDataType(ResultSetMetaData meta, int indexBasedOne) throws SQLException {
        return getMySqlType(meta.getColumnTypeName(indexBasedOne));
    }

    @Override
    final void bind(final PreparedStatement stmt, final int indexBasedOne,
                    final MappingType type, final DataType dataType, final Object value) throws SQLException {

        if (!(dataType instanceof MySQLType)) {
            throw mapMethodError(type, dataType);
        }
        switch ((MySQLType) dataType) {
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INT: {
                if (!(value instanceof Integer)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setInt(indexBasedOne, (Integer) value);
            }
            break;
            case INT_UNSIGNED:
            case BIGINT:
            case BIT: {
                if (!(value instanceof Long)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setLong(indexBasedOne, (Long) value);
            }
            break;
            case TIME: {
                if (!(value instanceof LocalTime || value instanceof Duration || value instanceof OffsetTime)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, value);
            }
            break;
            case YEAR: {
                if (value instanceof Short) {
                    stmt.setShort(indexBasedOne, (Short) value);
                } else if (value instanceof Year) {
                    stmt.setShort(indexBasedOne, (short) ((Year) value).getValue());
                } else {
                    throw ExecutorSupport.beforeBindMethodError(type, dataType, value);
                }
            }
            break;
            default:
                bindArmyType(stmt, indexBasedOne, type, dataType, ((MySQLType) dataType).armyType(), value);
        }


    }


    @Override
    final Object get(final ResultSet resultSet, int indexBasedOne, final MappingType type, final DataType dataType)
            throws SQLException {
        final Object value;
        switch ((MySQLType) dataType) {
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
            case DATETIME: {
                if (type instanceof MappingType.SqlOffsetDateTimeType) {
                    value = resultSet.getObject(indexBasedOne, OffsetDateTime.class);
                } else {
                    value = resultSet.getObject(indexBasedOne, LocalDateTime.class);
                }
            }
            break;
            case DATE:
                value = resultSet.getObject(indexBasedOne, LocalDate.class);
                break;
            case TIME: {
                if (type instanceof MappingType.SqlLocalTimeType) {
                    value = resultSet.getObject(indexBasedOne, LocalTime.class);
                } else if (type instanceof MappingType.SqlOffsetTimeType) {
                    value = resultSet.getObject(indexBasedOne, OffsetTime.class);
                } else if (type instanceof MappingType.SqlDurationType) {
                    value = resultSet.getObject(indexBasedOne, Duration.class);
                } else {
                    Object v;
                    try {
                        v = resultSet.getObject(indexBasedOne, LocalTime.class);
                    } catch (SQLException e) {
                        v = resultSet.getObject(indexBasedOne, Duration.class);
                    }
                    value = v;
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
            case LONGTEXT:
            case JSON:
                value = resultSet.getString(indexBasedOne);
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
            case GEOMETRY: {
                if (type instanceof MappingType.SqlStringType) {
                    value = resultSet.getString(indexBasedOne);
                } else {
                    value = resultSet.getBytes(indexBasedOne);
                }
            }
            break;
            case NULL:
            case UNKNOWN:
                value = resultSet.getObject(indexBasedOne);
                break;
            default:
                throw _Exceptions.unexpectedEnum((MySQLType) dataType);
        }
        return value;
    }



    final Isolation readIsolation(final String level) {
        final Isolation isolation;
        switch (level.toUpperCase(Locale.ROOT)) {
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
                throw unknownIsolation(level);

        }
        return isolation;
    }




    /*-------------------below private static  -------------------*/


    private static final class LocalExecutor extends MySQLExecutor implements SyncLocalExecutor {

        private static final Option<Boolean> WITH_CONSISTENT_SNAPSHOT = Option.from("WITH CONSISTENT SNAPSHOT", Boolean.class);


        private TransactionInfo transactionInfo;

        private LocalExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
            super(factory, conn, sessionName);
        }

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/commit.html">START TRANSACTION Statement</a>
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/server-system-variables.html#sysvar_transaction_isolation">transaction_isolation</a>
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/server-system-variables.html#sysvar_tx_isolation">tx_isolation</a>
         */
        @Override
        public TransactionInfo startTransaction(final TransactionOption option, final HandleMode mode) {

            final StringBuilder builder = new StringBuilder(168);

            int stmtCount = 0;
            if (inTransaction()) {
                handleInTransaction(builder, mode);
                stmtCount++;
            }


            final Isolation isolation;
            isolation = option.isolation();

            final ServerMeta serverMeta;
            if (isolation != null) {
                builder.append("SET TRANSACTION ISOLATION LEVEL ");
                standardIsolation(isolation, builder);
                stmtCount++;

                builder.append(_Constant.SPACE_SEMICOLON_SPACE);
            } else if ((serverMeta = this.factory.serverMeta).meetsMinimum(8, 0, 3)
                    || (serverMeta.meetsMinimum(5, 7, 20) && !serverMeta.meetsMinimum(8, 0, 0))) {
                builder.append("SET @@transaction_isolation = @@SESSION.transaction_isolation ; "); // here,must guarantee isolation is session isolation , must tail space
                builder.append("SELECT @@SESSION.transaction_isolation AS txIsolationLevel ; ");
                stmtCount += 2;
            } else {
                builder.append("SET @@tx_isolation = @@SESSION.tx_isolation ; "); // here,must guarantee isolation is session isolation , must tail space
                builder.append("SELECT @@SESSION.tx_isolation AS txIsolationLevel ; ");
                stmtCount += 2;
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

            // execute start transaction statements
            final Isolation finalIsolation;
            finalIsolation = executeStartTransaction(stmtCount, isolation, builder.toString());

            final TransactionInfo.InfoBuilder infoBuilder;
            infoBuilder = TransactionInfo.builder(true, finalIsolation, readOnly);
            infoBuilder.option(option);
            if (consistentSnapshot != null) {
                infoBuilder.option(WITH_CONSISTENT_SNAPSHOT, consistentSnapshot);
            }

            final TransactionInfo info;
            this.transactionInfo = info = infoBuilder.build();
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

            final StringBuilder builder = new StringBuilder(20);
            if (commit) {
                builder.append(COMMIT);
            } else {
                builder.append(ROLLBACK);
            }

            final TransactionInfo currentInfo = this.transactionInfo;
            final boolean chain;
            chain = transactionChain(optionFunc, builder);
            if (chain && currentInfo == null) {
                throw notInTransactionAndChainConflict();
            }

            final Object release;
            if (optionFunc == Option.EMPTY_FUNC) {
                release = null;
            } else {
                release = optionFunc.apply(Option.RELEASE);
            }

            if (chain && Boolean.TRUE.equals(release)) {
                String m = String.format("%s[true] and %s[true] conflict", Option.CHAIN.name(), Option.RELEASE.name());
                throw new IllegalArgumentException(m);
            }

            if (release instanceof Boolean) {
                if (!((Boolean) release)) {
                    builder.append(" NO");
                }
                builder.append(" RELEASE");
            }

            executeSimpleStaticStatement(builder.toString(), LOG);

            final TransactionInfo newInfo;
            if (chain) {
                newInfo = TransactionInfo.forChain(currentInfo);
            } else {
                newInfo = null;
            }
            this.transactionInfo = newInfo;
            return newInfo;
        }


    } // LocalExecutor


    private static class RmExecutor extends MySQLExecutor implements SyncRmExecutor {

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
            finalIsolation = executeStartTransaction(stmtCount, isolation, builder.toString());

            final TransactionInfo.InfoBuilder infoBuilder;
            infoBuilder = TransactionInfo.builder(true, finalIsolation, readOnly);
            infoBuilder.option(xid, flags, XaStates.ACTIVE, option);

            final TransactionInfo info;
            this.transactionInfo = info = infoBuilder.build();
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

            executeSimpleStaticStatement(builder.toString(), LOG);

            final TransactionInfo newInfo;
            this.transactionInfo = newInfo = TransactionInfo.forXaEnd(flags, info);
            return newInfo;
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

            executeSimpleStaticStatement(builder.toString(), LOG);

            this.transactionInfo = null; // clear current transaction info
            return readOnly ? RmSession.XA_RDONLY : RmSession.XA_OK;
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


            executeSimpleStaticStatement(builder.toString(), LOG);

            if ((flags & RmSession.TM_ONE_PHASE) != 0) {
                this.transactionInfo = null; // clear for one phase
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
            if (info != null && (infoXid = info.nonNullOf(Option.XID)).equals(xid)) {
                // rollback current transaction
                if (info.nonNullOf(Option.XA_STATES) != XaStates.IDLE) {
                    throw _Exceptions.xaStatesDontSupportRollbackCommand(infoXid, info.nonNullOf(Option.XA_STATES));
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

            executeSimpleStaticStatement(builder.toString(), LOG);

            if (onePhaseRollback) {
                this.transactionInfo = null; // clear for one phase rollback
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
                return null;   // non-jdbd create xid
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
