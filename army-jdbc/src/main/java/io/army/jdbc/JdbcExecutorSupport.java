package io.army.jdbc;

import io.army.ArmyException;
import io.army.session.DataAccessException;
import io.army.session.Option;
import io.army.session.TransactionInfo;
import io.army.session.Warning;
import io.army.session.executor.ExecutorSupport;
import io.army.session.record.FieldType;
import io.army.session.record.KeyType;
import io.army.session.record.ResultStates;
import io.army.sqltype.DataType;
import io.army.stmt.SimpleStmt;
import io.army.sync.SyncStmtOption;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.function.Function;

/**
 * <p>This class is base class of {@link JdbcExecutor}.
 *
 * @since 1.0
 */
abstract class JdbcExecutorSupport extends ExecutorSupport {

    JdbcExecutorSupport() {
    }

    /**
     * @see JdbcExecutor#insert(SimpleStmt, SyncStmtOption, Class)
     * @see JdbcExecutor#update(SimpleStmt, SyncStmtOption, Class, Function)
     */
    @Nullable
    static Warning mapToArmyWarning(final @Nullable SQLWarning warning) {
        if (warning == null) {
            return null;
        }
        return new ArmyWarning(warning);
    }

    static void closeResultSetAndStatement(final @Nullable ResultSet resultSet,
                                           final @Nullable Statement statement) {

        if (resultSet != null) {
            try {
                closeResource(resultSet);
            } finally {
                if (statement != null) {
                    closeResource(statement);
                }
            }
        } else if (statement != null) {
            closeResource(statement);
        }
    }

    static void closeResource(final AutoCloseable resource)
            throws ArmyException {
        try {
            resource.close();
        } catch (Exception e) {
            throw JdbcExecutor.wrapError(e);
        }

    }


    /*-------------------below static class  -------------------*/

    static abstract class JdbcRecordMeta extends ArmyResultRecordMeta {

        private final JdbcExecutor executor;

        private final ResultSetMetaData meta;

        JdbcRecordMeta(int resultNo, JdbcExecutor executor, DataType[] dataTypeArray, ResultSetMetaData meta) {
            super(resultNo, dataTypeArray);
            this.executor = executor;
            this.meta = meta;
        }


        @Nullable
        @Override
        public final <T> T getOf(int indexBasedZero, Option<T> option) throws DataAccessException {
            return null;
        }

        @Override
        public final <T> T getNonNullOf(int indexBasedZero, Option<T> option) throws DataAccessException {
            return null;
        }

        @Nullable
        @Override
        public final String getCatalogName(int indexBasedZero) throws DataAccessException {
            try {
                final String name;
                name = this.meta.getCatalogName(checkIndexBasedOne(indexBasedZero));
                return _StringUtils.isEmpty(name) ? null : name;   // avoid bug
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Nullable
        @Override
        public final String getSchemaName(int indexBasedZero) throws DataAccessException {
            try {
                final String name;
                name = this.meta.getSchemaName(checkIndexBasedOne(indexBasedZero));
                return _StringUtils.isEmpty(name) ? null : name;   // avoid bug
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Nullable
        @Override
        public final String getTableName(int indexBasedZero) throws DataAccessException {
            try {
                final String name;
                name = this.meta.getTableName(checkIndexBasedOne(indexBasedZero));
                return _StringUtils.isEmpty(name) ? null : name;   // avoid bug
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Nullable
        @Override
        public final String getColumnName(int indexBasedZero) throws DataAccessException {
            try {
                final String name;
                name = this.meta.getColumnName(checkIndexBasedOne(indexBasedZero));
                return _StringUtils.isEmpty(name) ? null : name;   // avoid bug
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Override
        public final FieldType getFieldType(final int indexBasedZero) throws DataAccessException {
            final FieldType fieldType;
            switch (this.executor.factory.serverDataBase) {
                case MySQL:
                case SQLite:
                case H2:
                    if (getTableName(indexBasedZero) == null) {
                        fieldType = FieldType.EXPRESSION;
                    } else {
                        fieldType = FieldType.FIELD;
                    }
                    break;
                case PostgreSQL: // postgre client protocol don't support
                    fieldType = FieldType.UNKNOWN;
                    break;
                case Oracle:
                default:
                    throw _Exceptions.unexpectedEnum(this.executor.factory.serverDataBase);
            }
            return fieldType;
        }

        @Override
        public final int getPrecision(final int indexBasedZero) throws DataAccessException {
            try {
                final int precision;
                precision = this.meta.getPrecision(checkIndexBasedOne(indexBasedZero));
                return 0;
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Override
        public final int getScale(int indexBasedZero) throws DataAccessException {
            return 0;
        }

        @Nullable
        @Override
        public final Boolean getAutoIncrementMode(int indexBasedZero) throws DataAccessException {
            return null;
        }

        @Override
        public final KeyType getKeyMode(int indexBasedZero) throws DataAccessException {
            return null;
        }

        @Nullable
        @Override
        public Boolean getNullableMode(int indexBasedZero) throws DataAccessException {
            return null;
        }

        @Override
        public Class<?> getFirstJavaType(int indexBasedZero) throws DataAccessException {
            return null;
        }

        @Nullable
        @Override
        public Class<?> getSecondJavaType(int indexBasedZero) throws DataAccessException {
            return null;
        }


    } // JdbcRecordMeta


    private static final class ArmyWarning implements Warning {

        private final String message;

        private final String sqlState;

        private final int vendor;

        /**
         * @see JdbcExecutor#mapToArmyWarning(SQLWarning)
         */
        private ArmyWarning(SQLWarning w) {
            final String m;
            m = w.getMessage();
            this.message = m == null ? "" : m;
            this.sqlState = w.getSQLState();
            this.vendor = w.getErrorCode();
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T valueOf(final Option<T> option) {
            final Object value;
            if (option == Option.MESSAGE) {
                value = this.message;
            } else if (option == Option.SQL_STATE) {
                value = this.sqlState;
            } else if (option == Option.VENDOR_CODE) {
                value = this.vendor;
            } else {
                value = null;
            }
            return (T) value;
        }

        @Override
        public String message() {
            return this.message;
        }

        @Override
        public String toString() {
            return _StringUtils.builder(50)
                    .append(getClass().getName())
                    .append("[message:")
                    .append(this.message)
                    .append(",sqlState:")
                    .append(this.sqlState)
                    .append(",vendor:")
                    .append(this.vendor)
                    .append(",hash:")
                    .append(System.identityHashCode(this))
                    .append(']')
                    .toString();
        }


    } // ArmyWarning


    private static abstract class JdbcResultStates implements ResultStates {

        private final TransactionInfo info;

        private final Warning warning;


        private JdbcResultStates(@Nullable TransactionInfo info, @Nullable Warning warning) {
            this.info = info;
            this.warning = warning;
        }

        @Override
        public final boolean inTransaction() {
            final TransactionInfo info = this.info;
            return info != null && info.inTransaction();
        }

        @Override
        public final String message() {
            // JDBC always empty
            return "";
        }

        @Nullable
        @Override
        public final Warning warning() {
            return this.warning;
        }

        @Nullable
        @Override
        public final <T> T valueOf(final Option<T> option) {
            final TransactionInfo info = this.info;
            final T value;
            if (info == null) {
                value = null;
            } else if (option == Option.IN_TRANSACTION || option == Option.READ_ONLY) {
                value = info.valueOf(option);
            } else {
                value = null;
            }
            return value;
        }


    } // JdbcResultStates

    private static abstract class SimpleResultStates extends JdbcResultStates {

        private SimpleResultStates(@Nullable TransactionInfo info, @Nullable Warning warning) {
            super(info, warning);
        }

        @Override
        public final int getResultNo() {
            // simple statement always 1
            return 1;
        }

        @Override
        public final boolean hasColumn() {
            return this instanceof SimpleQueryStates;
        }


        @Override
        public final boolean hasMoreResult() {
            // simple statement always false
            return false;
        }


    } // SimpleResultStates

    static final class SimpleUpdateStates extends SimpleResultStates {

        private final long affectedRows;

        /**
         * @see JdbcExecutor#insert(SimpleStmt, SyncStmtOption, Class)
         * @see JdbcExecutor#update(SimpleStmt, SyncStmtOption, Class, Function)
         */
        SimpleUpdateStates(@Nullable TransactionInfo info, @Nullable Warning warning, long affectedRows) {
            super(info, warning);
            this.affectedRows = affectedRows;
        }

        @Override
        public long affectedRows() {
            return this.affectedRows;
        }

        @Override
        public long rowCount() {
            return 0L;
        }

        @Override
        public boolean hasMoreFetch() {
            return false;
        }


    } // SimpleUpdateStates


    static final class SimpleQueryStates extends SimpleResultStates {

        private final long rowCount;

        private final boolean moreFetch;

        SimpleQueryStates(@Nullable TransactionInfo info, @Nullable Warning warning, long rowCount,
                          boolean moreFetch) {
            super(info, warning);
            this.rowCount = rowCount;
            this.moreFetch = moreFetch;
        }


        @Override
        public long affectedRows() {
            return this.rowCount;
        }

        @Override
        public boolean hasMoreFetch() {
            return this.moreFetch;
        }

        @Override
        public long rowCount() {
            return this.rowCount;
        }


    } // SimpleQueryStates


}
