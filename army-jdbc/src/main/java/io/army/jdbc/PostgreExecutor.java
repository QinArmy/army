package io.army.jdbc;

import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.mapping.MappingType;
import io.army.session.*;
import io.army.session.record.DataRecord;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.sync.StreamOption;
import io.army.sync.executor.SyncLocalStmtExecutor;
import io.army.sync.executor.SyncRmStmtExecutor;
import io.army.sync.executor.SyncStmtExecutor;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.sql.XAConnection;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * <p>
 * This class is a implementation of {@link SyncStmtExecutor} with postgre JDBC driver.
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreExecutor extends JdbcExecutor {

    static SyncLocalStmtExecutor localExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        return new LocalExecutor(factory, conn, sessionName);
    }

    static SyncRmStmtExecutor rmExecutor(JdbcExecutorFactory factory, final Object connObj, String sessionName) {
        final SyncRmStmtExecutor executor;
        if (connObj instanceof Connection) {
            executor = new RmExecutor(factory, (Connection) connObj, sessionName);
        } else if (connObj instanceof XAConnection) {
            try {
                final XAConnection xaConn = (XAConnection) connObj;
                final Connection conn;
                conn = xaConn.getConnection();

                executor = new XaConnRmExecutor(factory, xaConn, conn, sessionName);
            } catch (SQLException e) {
                throw JdbcExecutor.wrapError(e);
            }
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return executor;
    }


    private static final Logger LOG = LoggerFactory.getLogger(PostgreExecutor.class);

    private static final Option<Boolean> DEFERRABLE = Option.from("DEFERRABLE", Boolean.class);

    /**
     * private constructor
     */
    private PostgreExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        super(factory, conn, sessionName);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-set-transaction.html">SET TRANSACTION Statement</a>
     */
    @Override
    public final void setTransactionCharacteristics(final TransactionOption option) throws DataAccessException {
        final StringBuilder builder = new StringBuilder(35);
        builder.append("SET SESSION CHARACTERISTICS AS TRANSACTION ");

        if (option.isReadOnly()) {
            builder.append(READ_ONLY);
        } else {
            builder.append(READ_WRITE);
        }

        final Isolation isolation;
        isolation = option.isolation();
        if (isolation != null) {
            builder.append(_Constant.SPACE_COMMA_SPACE);
            builder.append("ISOLATION LEVEL ");
            standardIsolation(isolation, builder);
        }

        appendDeferrable(option, builder);

        try (Statement statement = this.conn.createStatement()) {

            statement.executeUpdate(builder.toString());
        } catch (Exception e) {
            throw handleException(e);
        }

    }

    @Override
    final Logger getLogger() {
        return LOG;
    }


    final void bind(final PreparedStatement stmt, final int indexBasedOne, final MappingType type,
                    final DataType dataType, final Object nonNull)
            throws SQLException {

        final PGobject pgObject;

        if (!(dataType instanceof SqlType)) {
            pgObject = new PGobject();

            pgObject.setType(dataType.typeName().toLowerCase(Locale.ROOT));
            pgObject.setValue((String) nonNull);

            stmt.setObject(indexBasedOne, pgObject, Types.OTHER);
        } else if (!(dataType instanceof PostgreType)) {
            throw mapMethodError(type, dataType);
        } else switch ((PostgreType) dataType) {
            case UUID: {
                if (!(nonNull instanceof UUID)) {
                    throw beforeBindMethodError(type, dataType, nonNull);
                }
                stmt.setObject(indexBasedOne, nonNull);
            }
            break;
            case MONEY: {
                if (nonNull instanceof String) {
                    stmt.setString(indexBasedOne, (String) nonNull);
                } else if (nonNull instanceof BigDecimal) {
                    stmt.setBigDecimal(indexBasedOne, (BigDecimal) nonNull);
                } else {
                    throw beforeBindMethodError(type, dataType, nonNull);
                }
            }
            break;
            case BIT:
            case VARBIT: {
                pgObject = new PGobject();

                pgObject.setType(dataType.typeName().toLowerCase(Locale.ROOT));

                if (nonNull instanceof BitSet) {
                    pgObject.setValue(_StringUtils.bitSetToBitString((BitSet) nonNull, true));
                } else if (nonNull instanceof String) {
                    pgObject.setValue((String) nonNull);
                } else {
                    throw beforeBindMethodError(type, dataType, nonNull);
                }

                stmt.setObject(indexBasedOne, pgObject, Types.OTHER);
            }
            break;

            case ACLITEM:
            case INTERVAL:

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

            case PG_LSN:
            case PG_SNAPSHOT:

            case JSONPATH: {
                if (!(nonNull instanceof String)) {
                    throw beforeBindMethodError(type, dataType, nonNull);
                }
                pgObject = new PGobject();
                pgObject.setType(dataType.typeName().toLowerCase(Locale.ROOT));
                pgObject.setValue((String) nonNull);

                stmt.setObject(indexBasedOne, pgObject, Types.OTHER);
            }
            break;
            default:
                bindArmyType(stmt, indexBasedOne, type, dataType, ((PostgreType) dataType).armyType(), nonNull);

        }


    }

    @Override
    final DataType getDataType(final ResultSetMetaData meta, final int indexBasedOne) throws SQLException {
        return getPostgreType(meta.getColumnTypeName(indexBasedOne));
    }

    @Override
    final Object get(final ResultSet resultSet, final int indexBasedOne, final MappingType type, final DataType dataType)
            throws SQLException {
        final Object value;

        switch ((PostgreType) dataType) {
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
                throw _Exceptions.unexpectedEnum((PostgreType) dataType);

        }

        return value;
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-set-transaction.html">SET TRANSACTION</a>
     * @see <a href="https://www.postgresql.org/docs/current/runtime-config-client.html#GUC-TRANSACTION-ISOLATION">transaction_isolation</a>
     * @see <a href="https://www.postgresql.org/docs/current/runtime-config-client.html#GUC-TRANSACTION-READ-ONLY">transaction_read_only</a>
     * @see <a href="https://www.postgresql.org/docs/current/runtime-config-client.html#GUC-TRANSACTION-DEFERRABLE">transaction_deferrable</a>
     */
    @Override
    final TransactionInfo sessionTransactionCharacteristics() {
        final String sql = "SHOW transaction_isolation ; SHOW transaction_read_only ; SHOW transaction_deferrable ";
        try (final Statement statement = this.conn.createStatement()) {

            if (!statement.execute(sql)) {
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                throw driverError();
            }
            final Isolation isolation;
            isolation = readIsolationAndClose(statement.getResultSet());

            final boolean readOnly, deferrable;
            readOnly = readBooleanFromMultiResult(statement);
            deferrable = readBooleanFromMultiResult(statement);

            return TransactionInfo.info(false, isolation, readOnly, Option.singleFunc(DEFERRABLE, deferrable));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/runtime-config-client.html#GUC-DEFAULT-TRANSACTION-ISOLATION">default_transaction_isolation</a>
     */
    final Isolation readIsolation(final String level) {
        final Isolation isolation;
        switch (level.toLowerCase(Locale.ROOT)) {
            case "read committed":
                isolation = Isolation.READ_COMMITTED;
                break;
            case "repeatable read":
                isolation = Isolation.REPEATABLE_READ;
                break;
            case "serializable":
                isolation = Isolation.SERIALIZABLE;
                break;
            case "read uncommitted":
                isolation = Isolation.READ_UNCOMMITTED;
                break;
            default:
                throw unknownIsolation(level);

        }
        return isolation;
    }


    /*-------------------below private static  -------------------*/


    /**
     * @see #sessionTransactionCharacteristics()
     */
    private static boolean readBooleanFromMultiResult(Statement statement) throws SQLException {
        if (!statement.getMoreResults()) {
            statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
            throw driverError();
        }

        try (ResultSet resultSet = statement.getResultSet()) {
            if (!resultSet.next()) {
                throw driverError();
            }
            return resultSet.getBoolean(1);
        }
    }


    @Nullable
    private static Boolean appendDeferrable(final TransactionOption option, final StringBuilder builder) {
        final Boolean deferrable;
        deferrable = option.valueOf(DEFERRABLE);
        if (deferrable != null) {
            builder.append(_Constant.SPACE_COMMA_SPACE);
            if (!deferrable) {
                builder.append("NOT ");
            }
            builder.append("DEFERRABLE");
        }
        return deferrable;
    }


    private static final class LocalExecutor extends PostgreExecutor implements SyncLocalStmtExecutor {

        private TransactionInfo transactionInfo;

        private LocalExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
            super(factory, conn, sessionName);
        }

        /**
         * @see <a href="https://www.postgresql.org/docs/current/sql-start-transaction.html">START TRANSACTION Statement</a>
         * @see <a href="https://www.postgresql.org/docs/current/runtime-config-client.html#GUC-DEFAULT-TRANSACTION-ISOLATION">default_transaction_isolation</a>
         */
        @Override
        public TransactionInfo startTransaction(final TransactionOption option, final HandleMode mode) {
            final StringBuilder builder = new StringBuilder(168);

            int stmtCount = 0;
            if (this.transactionInfo != null) {
                handleInTransaction(builder, mode);
                stmtCount++;
            }

            final Isolation isolation;
            isolation = option.isolation();

            if (isolation == null) {
                builder.append("SHOW default_transaction_isolation ; ");
                stmtCount++;
            }

            builder.append("START TRANSACTION ");
            final boolean readOnly = option.isReadOnly();
            if (readOnly) {
                builder.append(READ_ONLY);
            } else {
                builder.append(READ_WRITE);
            }

            if (isolation != null) {
                builder.append(_Constant.SPACE_COMMA_SPACE);
                standardIsolation(isolation, builder);
            }

            final Boolean deferrable;
            deferrable = appendDeferrable(option, builder);
            stmtCount++;

            // execute start transaction statements
            final Isolation finalIsolation;
            finalIsolation = executeStartTransaction(stmtCount, isolation, builder);

            final Function<Option<?>, ?> optionFunc;
            if (deferrable != null) {
                optionFunc = Option.singleFunc(DEFERRABLE, deferrable);
            } else {
                optionFunc = Option.EMPTY_OPTION_FUNC;
            }

            final TransactionInfo info;
            this.transactionInfo = info = TransactionInfo.info(true, finalIsolation, readOnly, optionFunc);
            return info;
        }


        @Nullable
        @Override
        public TransactionInfo commit(Function<Option<?>, ?> optionFunc) {
            return commitOrRollback(true, optionFunc);
        }

        @Nullable
        @Override
        public TransactionInfo rollback(Function<Option<?>, ?> optionFunc) {
            return commitOrRollback(false, optionFunc);
        }

        @Nullable
        @Override
        TransactionInfo obtainTransaction() {
            return this.transactionInfo;
        }


        /**
         * @see <a href="https://www.postgresql.org/docs/current/sql-commit.html">COMMIT Statement</a>
         * @see <a href="https://www.postgresql.org/docs/current/sql-rollback.html">ROLLBACK Statement</a>
         */
        @Nullable
        private TransactionInfo commitOrRollback(final boolean commit, final Function<Option<?>, ?> optionFunc)
                throws DataAccessException {

            final StringBuilder builder = new StringBuilder(20);
            if (commit) {
                builder.append(COMMIT);
            } else {
                builder.append(ROLLBACK);
            }

            final boolean chain;
            chain = transactionChain(optionFunc, builder);
            final TransactionInfo newInfo;
            if (chain) {
                newInfo = this.transactionInfo;
                assert newInfo != null;
            } else {
                newInfo = null;
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

    private static class RmExecutor extends PostgreExecutor implements SyncRmStmtExecutor {

        private TransactionInfo transactionInfo;

        private RmExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
            super(factory, conn, sessionName);
        }

        /**
         * @see <a href="https://www.postgresql.org/docs/current/sql-start-transaction.html">START TRANSACTION Statement</a>
         * @see <a href="https://www.postgresql.org/docs/current/runtime-config-client.html#GUC-DEFAULT-TRANSACTION-ISOLATION">default_transaction_isolation</a>
         */
        @Override
        public final TransactionInfo start(final Xid xid, final int flags, TransactionOption option)
                throws RmSessionException {

            final TransactionInfo info = this.transactionInfo;
            final XaStates states;
            final Xid infoXid;
            if (info == null) {
                states = null;
                infoXid = null;
            } else if ((states = info.valueOf(Option.XA_STATES)) == XaStates.ACTIVE) {
                throw _Exceptions.xaBusyOnOtherTransaction();
            } else {
                infoXid = info.valueOf(Option.XID);
            }


            final TransactionInfo newInfo;
            if (!_StringUtils.hasText(xid.getGtrid())) {
                throw _Exceptions.xaGtridNoText();
            } else if ((flags & RmSession.TM_SUSPEND) != 0) {
                throw new RmSessionException("suspend/resume not implemented", RmSessionException.XAER_RMERR);
            } else if (flags == RmSession.TM_NO_FLAGS) {
                newInfo = startLocalTransaction(xid, flags, option);  // postgre use local transaction
            } else if (flags != RmSession.TM_JOIN) {
                throw _Exceptions.xaInvalidFlag(flags, "start");
            } else if (states == XaStates.IDLE && infoXid != null && infoXid.equals(xid)) { // It's ok to join an ended transaction. WebLogic does that.
                final Map<Option<?>, Object> map;
                map = cloneOption(info);
                map.put(Option.XA_STATES, XaStates.ACTIVE); // modify states
                map.put(Option.XA_FLAGS, flags); // modify flags

                newInfo = TransactionInfo.info(info.inTransaction(), info.isolation(), info.isReadOnly(), map::get);
            } else {
                String m = String.format("Invalid protocol state requested. Attempted transaction interleaving is not supported. xid=%s, currentXid=%s, state=%s, flags=%s",
                        xid, infoXid, states, flags);
                throw new RmSessionException(m, RmSessionException.XAER_RMERR);
            }

            this.transactionInfo = newInfo;
            return newInfo;
        }

        @Override
        public final TransactionInfo end(final Xid xid, final int flags, Function<Option<?>, ?> optionFunc)
                throws RmSessionException {

            final TransactionInfo info = this.transactionInfo;
            final Xid infoXid;
            if (info == null || (infoXid = info.valueOf(Option.XID)) == null || !infoXid.equals(xid)) {
                throw _Exceptions.xaNonCurrentTransaction(xid);
            } else if (info.valueOf(Option.XA_STATES) != XaStates.ACTIVE) {
                throw _Exceptions.xaTransactionDontSupportEndCommand(infoXid, info.nonNullOf(Option.XA_STATES));
            } else if ((flags & RmSession.TM_SUSPEND) != 0) {
                throw new RmSessionException("suspend/resume not implemented", RmSessionException.XAER_RMERR);
            } else if (flags != RmSession.TM_SUCCESS && flags != RmSession.TM_FAIL) {
                throw _Exceptions.xaInvalidFlag(flags, "end");
            }

            final Map<Option<?>, Object> map;
            map = cloneOption(info);
            map.put(Option.XA_STATES, XaStates.IDLE); // modify states
            map.put(Option.XA_FLAGS, flags); // modify flags

            final TransactionInfo newInfo;
            newInfo = TransactionInfo.info(info.inTransaction(), info.isolation(), info.isReadOnly(), map::get);
            this.transactionInfo = newInfo;
            return newInfo;
        }


        /**
         * @see <a href="https://www.postgresql.org/docs/current/sql-prepare-transaction.html">PREPARE TRANSACTION</a>
         */
        @Override
        public final int prepare(final Xid xid, Function<Option<?>, ?> optionFunc) throws RmSessionException {

            final TransactionInfo info = this.transactionInfo;

            final Xid infoXid;
            if (info == null || (infoXid = info.valueOf(Option.XID)) == null || !infoXid.equals(xid)) {
                throw _Exceptions.xaNonCurrentTransaction(xid); // here use xid
            } else if (info.nonNullOf(Option.XA_STATES) != XaStates.IDLE) {
                throw _Exceptions.xaTransactionDontSupportEndCommand(infoXid, info.nonNullOf(Option.XA_STATES));
            } else if ((info.nonNullOf(Option.XA_FLAGS) & RmSession.TM_FAIL) != 0
                    || Boolean.TRUE.equals(info.valueOf(Option.ROLLBACK_ONLY))) {
                throw _Exceptions.xaTransactionRollbackOnly(infoXid);
            }


            final StringBuilder builder = new StringBuilder(140);

            final boolean readOnly = info.isReadOnly();
            if (readOnly) {
                builder.append("COMMIT");
            } else {
                builder.append("PREPARE TRANSACTION");
                xidToString(infoXid, builder);
            }

            try (Statement statement = this.conn.createStatement()) {

                statement.executeUpdate(builder.toString());

                this.transactionInfo = null; // clear current transaction info
                return readOnly ? RmSession.XA_RDONLY : RmSession.XA_OK;
            } catch (Exception e) {
                throw handleRmException(e);
            }
        }

        /**
         * @see <a href="https://www.postgresql.org/docs/current/sql-commit-prepared.html">COMMIT PREPARED</a>
         * @see <a href="https://www.postgresql.org/docs/current/sql-commit.html">COMMIT</a>
         */
        @Override
        public final void commit(final Xid xid, final int flags, Function<Option<?>, ?> optionFunc)
                throws RmSessionException {

            if (flags != RmSession.TM_ONE_PHASE && flags != RmSession.TM_NO_FLAGS) {
                throw _Exceptions.xaInvalidFlag(flags, "commit");
            }

            final StringBuilder builder = new StringBuilder(140);

            final TransactionInfo info;
            final Xid infoXid;

            if ((flags & RmSession.TM_ONE_PHASE) == 0) { // two phase commit
                builder.append("COMMIT PREPARED");
                xidToString(xid, builder);
            } else if ((info = this.transactionInfo) == null
                    || (infoXid = info.valueOf(Option.XID)) == null
                    || !infoXid.equals(xid)) {
                throw _Exceptions.xaNonCurrentTransaction(xid); // here use xid
            } else if (info.nonNullOf(Option.XA_STATES) != XaStates.IDLE) {
                throw _Exceptions.xaTransactionDontSupportEndCommand(infoXid, info.nonNullOf(Option.XA_STATES));
            } else if ((info.nonNullOf(Option.XA_FLAGS) & RmSession.TM_FAIL) != 0
                    || Boolean.TRUE.equals(info.valueOf(Option.ROLLBACK_ONLY))) {
                throw _Exceptions.xaTransactionRollbackOnly(infoXid);
            } else {
                builder.append(COMMIT);
            }

            try (Statement statement = this.conn.createStatement()) {

                statement.executeUpdate(builder.toString());

                if ((flags & RmSession.TM_ONE_PHASE) != 0) {
                    this.transactionInfo = null; // clear for one phase
                }
            } catch (Exception e) {
                throw handleRmException(e);
            }
        }

        /**
         * @see <a href="https://www.postgresql.org/docs/current/sql-rollback-prepared.html">ROLLBACK PREPARED</a>
         * @see <a href="https://www.postgresql.org/docs/current/sql-rollback.html">ROLLBACK</a>
         */
        @Override
        public final void rollback(final Xid xid, Function<Option<?>, ?> optionFunc) throws RmSessionException {

            final TransactionInfo info = this.transactionInfo;

            final StringBuilder builder = new StringBuilder(140);

            final Xid infoXid;
            final boolean onePhaseRollback;
            if (info != null
                    && (infoXid = info.valueOf(Option.XID)) != null
                    && infoXid.equals(xid)) {
                // rollback current transaction
                if (infoXid.nonNullOf(Option.XA_STATES) != XaStates.IDLE) {
                    throw _Exceptions.xaStatesDontSupportRollbackCommand(infoXid, infoXid.nonNullOf(Option.XA_STATES));
                }
                onePhaseRollback = true;
                builder.append(ROLLBACK);
            } else {
                onePhaseRollback = false;
                builder.append("ROLLBACK PREPARED");
                xidToString(xid, builder);
            }

            try (Statement statement = this.conn.createStatement()) {

                statement.executeUpdate(builder.toString());

                if (onePhaseRollback) {
                    this.transactionInfo = null; // clear for one phase
                }
            } catch (Exception e) {
                throw handleRmException(e);
            }

        }

        @Override
        public final void forget(Xid xid, Function<Option<?>, ?> optionFunc) throws RmSessionException {
            throw _Exceptions.xaDontSupportForget(Database.PostgreSQL);
        }

        @Override
        public final Stream<Xid> recover(int flags, Function<Option<?>, ?> optionFunc, StreamOption option)
                throws RmSessionException {

            final Stream<Xid> stream;
            if (flags == RmSession.TM_END_RSCAN) {
                final String sql = "SELECT gid FROM pg_prepared_xacts where database = current_database()";
                stream = jdbcRecover(sql, this::recordToXid, option);
            } else if (flags == RmSession.TM_START_RSCAN) {
                stream = Stream.empty();
            } else {
                throw _Exceptions.xaInvalidFlag(flags, "recover");
            }
            return stream;
        }


        @Override
        public final boolean isSupportForget() {
            // always false , postgre don't support forget
            return false;
        }

        @Override
        public final int startSupportFlags() {
            return RmSession.TM_JOIN;
        }

        @Override
        public final int endSupportFlags() {
            return (RmSession.TM_SUCCESS | RmSession.TM_FAIL);
        }

        @Override
        public final int commitSupportFlags() {
            return RmSession.TM_ONE_PHASE;
        }

        @Override
        public final int recoverSupportFlags() {
            return (RmSession.TM_START_RSCAN | RmSession.TM_END_RSCAN);
        }

        @Override
        public final boolean isSameRm(final Session.XaTransactionSupportSpec s) throws SessionException {
            try {
                return s instanceof XaConnRmExecutor
                        && this instanceof XaConnRmExecutor
                        && ((XaConnRmExecutor) this).xaConn.getXAResource().isSameRM(((XaConnRmExecutor) s).xaConn.getXAResource());
            } catch (Exception e) {
                throw handleRmException(e);
            }
        }

        @Nullable
        @Override
        final TransactionInfo obtainTransaction() {
            return this.transactionInfo;
        }


        private TransactionInfo startLocalTransaction(final Xid xid, final int flags, TransactionOption option) {

            final StringBuilder builder = new StringBuilder(140);

            final Isolation isolation;
            isolation = option.isolation();
            int stmtCount = 0;
            if (isolation == null) {
                builder.append("SHOW default_transaction_isolation ; ");
                stmtCount++;
            }

            builder.append("START TRANSACTION ");
            final boolean readOnly = option.isReadOnly();
            if (readOnly) {
                builder.append(READ_ONLY);
            } else {
                builder.append(READ_WRITE);
            }

            if (isolation != null) {
                builder.append(_Constant.SPACE_COMMA_SPACE);
                standardIsolation(isolation, builder);
            }

            final Boolean deferrable;
            deferrable = appendDeferrable(option, builder);
            stmtCount++;

            // execute start transaction statements
            final Isolation finalIsolation;
            finalIsolation = executeStartTransaction(stmtCount, isolation, builder);

            final Map<Option<?>, Object> map = _Collections.hashMap(7);

            map.put(Option.XID, xid);
            map.put(Option.XA_FLAGS, flags);
            map.put(Option.XA_STATES, XaStates.ACTIVE);
            if (deferrable != null) {
                map.put(DEFERRABLE, deferrable);
            }
            return TransactionInfo.info(true, finalIsolation, readOnly, map::get);
        }

        private Map<Option<?>, Object> cloneOption(final TransactionInfo info) {
            final Map<Option<?>, Object> map = _Collections.hashMap(8);

            map.put(Option.XID, info.nonNullOf(Option.XID));
            map.put(Option.XA_FLAGS, info.nonNullOf(Option.XA_FLAGS));
            map.put(Option.XA_STATES, info.nonNullOf(Option.XA_STATES));
            final Boolean deferrable = info.valueOf(DEFERRABLE);
            if (deferrable != null) {
                map.put(DEFERRABLE, deferrable);
            }
            final Boolean rollbackOnly = info.valueOf(Option.ROLLBACK_ONLY);
            if (rollbackOnly != null) {
                map.put(Option.ROLLBACK_ONLY, rollbackOnly);
            }

            return map;
        }


        private void xidToString(final Xid xid, final StringBuilder builder) {
            final String gtrid, bqual;
            gtrid = xid.getGtrid();
            bqual = xid.getBqual();


            final byte[] gtridBytes, bqualBytes;

            if (!_StringUtils.hasText(gtrid)) {
                throw _Exceptions.xaGtridNoText();
            } else if ((gtridBytes = gtrid.getBytes(StandardCharsets.UTF_8)).length > 64) {
                throw _Exceptions.xaGtridBeyond64Bytes();
            }

            if (bqual == null) {
                bqualBytes = new byte[0];
            } else if (!_StringUtils.hasText(bqual)) {
                throw _Exceptions.xaBqualNonNullAndNoText();
            } else if ((bqualBytes = bqual.getBytes(StandardCharsets.UTF_8)).length > 64) {
                throw _Exceptions.xaBqualBeyond64Bytes();
            }

            final Base64.Encoder encoder = Base64.getEncoder();

            builder.append(_Constant.SPACE)
                    .append(_Constant.QUOTE)
                    .append(xid.getFormatId())
                    .append('_')
                    .append(encoder.encodeToString(gtridBytes))
                    .append('_')
                    .append(encoder.encodeToString(bqualBytes))
                    .append(_Constant.QUOTE);

        }


        @Nullable
        private Xid recordToXid(final DataRecord row) {

            final String xidStr;
            xidStr = row.getNonNull(0, String.class);

            Xid xid;
            try {
                final int leftHyphen, rightHyphen;
                leftHyphen = xidStr.indexOf('_');
                rightHyphen = xidStr.indexOf('_', leftHyphen + 1);

                final int formatId;
                formatId = Integer.parseInt(xidStr.substring(0, leftHyphen));

                final Base64.Decoder decoder = Base64.getMimeDecoder();
                final byte[] gtridBytes, bqualBytes;

                gtridBytes = decoder.decode(xidStr.substring(leftHyphen + 1, rightHyphen));
                bqualBytes = decoder.decode(xidStr.substring(rightHyphen + 1));

                final String gtrid, bqual;
                gtrid = new String(gtridBytes, StandardCharsets.UTF_8);
                if (bqualBytes.length == 0) {
                    bqual = null;
                } else {
                    bqual = new String(bqualBytes, StandardCharsets.UTF_8);
                }

                xid = Xid.from(gtrid, bqual, formatId);
            } catch (Exception e) {
                xid = null;
            }

            return xid;
        }


    } // RmExecutor

    private static final class XaConnRmExecutor extends RmExecutor implements XaConnectionExecutor {

        private final XAConnection xaConn;

        private XaConnRmExecutor(JdbcExecutorFactory factory, XAConnection xaConn, Connection conn, String sessionName) {
            super(factory, conn, sessionName);
            this.xaConn = xaConn;
        }

        @Override
        public XAConnection getXAConnection() {
            return this.xaConn;
        }

        @Override
        public void closeXaConnection() throws SQLException {
            this.xaConn.close();
        }

    } // XaConnRmExecutor


}
