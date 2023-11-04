package io.army.jdbc;

import io.army.ArmyException;
import io.army.criteria.Selection;
import io.army.mapping.optional.OffsetTimeType;
import io.army.session.DataAccessException;
import io.army.session.Option;
import io.army.session.TransactionInfo;
import io.army.session.Warning;
import io.army.session.executor.ExecutorSupport;
import io.army.session.record.FieldType;
import io.army.session.record.KeyType;
import io.army.session.record.ResultStates;
import io.army.sqltype.ArmyType;
import io.army.sqltype.DataType;
import io.army.stmt.SimpleStmt;
import io.army.sync.SyncProcCursor;
import io.army.sync.SyncStmtCursor;
import io.army.sync.SyncStmtOption;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.*;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
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

        final JdbcExecutor executor;

        final ResultSetMetaData meta;

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
                name = this.meta.getCatalogName(checkIndexAndToBasedOne(indexBasedZero));
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
                name = this.meta.getSchemaName(checkIndexAndToBasedOne(indexBasedZero));
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
                name = this.meta.getTableName(checkIndexAndToBasedOne(indexBasedZero));
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
                name = this.meta.getColumnName(checkIndexAndToBasedOne(indexBasedZero));
                return _StringUtils.isEmpty(name) ? null : name;   // avoid bug
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Override
        public final FieldType getFieldType(final int indexBasedZero) throws DataAccessException {
            final FieldType fieldType;
            switch (this.executor.factory.serverDatabase) {
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
                    throw _Exceptions.unexpectedEnum(this.executor.factory.serverDatabase);
            }
            return fieldType;
        }

        @Override
        public final int getPrecision(final int indexBasedZero) throws DataAccessException {
            try {
                final ArmyType armyType;
                armyType = getArmyType(indexBasedZero);

                final int precision;
                if (!armyType.isDecimalType() && (armyType.isNumberType() || armyType.isTimeType())) {
                    precision = 0;
                } else {
                    precision = this.meta.getPrecision(indexBasedZero);
                }
                return precision;
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Override
        public final int getScale(final int indexBasedZero) throws DataAccessException {
            try {
                final ArmyType armyType;
                armyType = getArmyType(indexBasedZero);
                final int scale;
                if (armyType.isDecimalType()) {
                    scale = this.meta.getScale(checkIndexAndToBasedOne(indexBasedZero));
                } else if (armyType.isNumberType() || armyType.isTextString() || armyType.isBinaryString()) {
                    scale = 0;
                } else switch (armyType) {
                    case TIME:
                    case TIME_WITH_TIMEZONE:
                    case TIMESTAMP:
                    case TIMESTAMP_WITH_TIMEZONE:
                        scale = -1; // JDBC don't support
                        break;
                    default:
                        scale = this.meta.getScale(checkIndexAndToBasedOne(indexBasedZero));
                        break;
                }
                return scale;
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Nullable
        @Override
        public final Boolean getAutoIncrementMode(final int indexBasedZero) throws DataAccessException {
            final Boolean mode;
            switch (this.executor.factory.serverDatabase) {
                case MySQL:
                case SQLite:
                case H2:
                    try {
                        mode = this.meta.isAutoIncrement(checkIndexAndToBasedOne(indexBasedZero));
                    } catch (Exception e) {
                        throw this.executor.handleException(e);
                    }
                    break;
                case PostgreSQL:  // postgre client protocol don't support
                case Oracle:
                    mode = null;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(this.executor.factory.serverDatabase);
            }
            return mode;
        }

        @Override
        public final KeyType getKeyMode(final int indexBasedZero) throws DataAccessException {

            try {
                final KeyType keyType;
                if (this.meta.isAutoIncrement(checkIndexAndToBasedOne(indexBasedZero))) {
                    keyType = KeyType.PRIMARY_KEY;
                } else {
                    keyType = KeyType.UNKNOWN;
                }
                return keyType;
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Nullable
        @Override
        public final Boolean getNullableMode(final int indexBasedZero) throws DataAccessException {
            try {
                final Boolean mode;
                switch (this.meta.isNullable(checkIndexAndToBasedOne(indexBasedZero))) {
                    case ResultSetMetaData.columnNullable:
                        mode = Boolean.TRUE;
                        break;
                    case ResultSetMetaData.columnNoNulls:
                        mode = Boolean.FALSE;
                        break;
                    case ResultSetMetaData.columnNullableUnknown:
                        mode = null;
                        break;
                    default:
                        throw new DataAccessException("unknown null mode");
                }
                return mode;
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Override
        public final Class<?> getFirstJavaType(final int indexBasedZero) throws DataAccessException {
            final ArmyType armyType;
            armyType = getArmyType(indexBasedZero);
            final Class<?> javaType;
            switch (armyType) {
                case BOOLEAN:
                    javaType = Boolean.class;
                    break;
                case TINYINT:
                    javaType = Byte.class;
                    break;
                case SMALLINT:
                case TINYINT_UNSIGNED:
                    javaType = Short.class;
                    break;
                case SMALLINT_UNSIGNED:
                case MEDIUMINT:
                case MEDIUMINT_UNSIGNED:
                case INTEGER:
                    javaType = Integer.class;
                    break;
                case INTEGER_UNSIGNED:
                case BIGINT:
                    javaType = Long.class;
                    break;
                case DECIMAL:
                case NUMERIC:
                case DECIMAL_UNSIGNED:
                    javaType = BigDecimal.class;
                    break;
                case FLOAT:
                    javaType = Float.class;
                    break;
                case DOUBLE:
                    javaType = Double.class;
                    break;
                case BIGINT_UNSIGNED:
                    javaType = BigInteger.class;
                    break;
                case CHAR:
                case VARCHAR:
                case ENUM:
                case TINYTEXT:
                case MEDIUMTEXT:
                case TEXT:
                case LONGTEXT:

                case JSON:
                case JSONB:
                case XML:
                case INTERVAL:
                case COMPOSITE:
                    javaType = String.class;
                    break;
                case BINARY:
                case VARBINARY:
                case TINYBLOB:
                case MEDIUMBLOB:
                case BLOB:
                case LONGBLOB:
                    javaType = byte[].class;
                    break;
                case YEAR:
                    javaType = Year.class;
                    break;
                case MONTH_DAY:
                    javaType = MonthDay.class;
                    break;
                case YEAR_MONTH:
                    javaType = YearMonth.class;
                    break;
                case TIME:
                    javaType = LocalTime.class;
                    break;
                case TIME_WITH_TIMEZONE:
                    javaType = OffsetTime.class;
                    break;
                case DATE:
                    javaType = LocalDate.class;
                    break;
                case TIMESTAMP:
                    javaType = LocalDateTime.class;
                    break;
                case TIMESTAMP_WITH_TIMEZONE:
                    javaType = OffsetTimeType.class;
                    break;
                case BIT:
                    switch (this.executor.factory.serverDatabase) {
                        case MySQL:
                            javaType = Long.class;
                            break;
                        case PostgreSQL:
                            javaType = BitSet.class;
                            break;
                        case H2:
                        case SQLite:
                        case Oracle:
                        default:
                            throw _Exceptions.unexpectedEnum(this.executor.factory.serverDatabase);
                    }
                    break;
                case VARBIT:
                    javaType = BitSet.class;
                    break;
                case DURATION:
                    javaType = Duration.class;
                    break;
                case PERIOD:
                    javaType = Period.class;
                    break;
                case GEOMETRY:
                    switch (this.executor.factory.serverDatabase) {
                        case MySQL:
                            javaType = byte[].class;
                            break;
                        case PostgreSQL:
                            javaType = String.class;
                            break;
                        case H2: //TODO
                        case SQLite:
                        case Oracle:
                        default:
                            throw _Exceptions.unexpectedEnum(this.executor.factory.serverDatabase);
                    }
                    break;
                case REF_CURSOR:
                    if (this instanceof JdbcStmtRecordMeta) {
                        javaType = SyncStmtCursor.class;
                    } else {
                        javaType = SyncProcCursor.class;
                    }
                    break;
                case ROWID: // TODO oracle
                case DIALECT_TYPE:
                case ARRAY:
                case UNKNOWN:
                    javaType = Object.class;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(armyType);

            }
            return javaType;
        }

        @Nullable
        @Override
        public final Class<?> getSecondJavaType(int indexBasedZero) throws DataAccessException {
            final ArmyType armyType;
            armyType = getArmyType(indexBasedZero);
            final Class<?> javaType;
            switch (armyType) {
                case TIME:
                    switch (this.executor.factory.serverDatabase) {
                        case MySQL:
                            javaType = Duration.class;
                            break;
                        case PostgreSQL:
                        case SQLite:
                        default:
                            javaType = null;
                    }
                    break;
                case LONGBLOB:
                    javaType = io.army.session.record.BlobPath.class;
                    break;
                case LONGTEXT:
                    javaType = io.army.session.record.TextPath.class;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(armyType);
            }
            return javaType;
        }


    } // JdbcRecordMeta


    static final class JdbcStmtRecordMeta extends JdbcRecordMeta {

        private final List<? extends Selection> selectionList;

        private final Map<String, Integer> aliasToIndexMap;

        private List<String> columnLabelList;

        JdbcStmtRecordMeta(int resultNo, JdbcExecutor executor, DataType[] dataTypeArray,
                           List<? extends Selection> selectionList, ResultSetMetaData meta) {
            super(resultNo, executor, dataTypeArray, meta);
            this.selectionList = selectionList;
            if (selectionList.size() < 6) {
                this.aliasToIndexMap = null;
            } else {
                this.aliasToIndexMap = createAliasToIndexMap(selectionList);
            }
        }

        @Override
        public String getColumnLabel(final int indexBasedZero) throws DataAccessException {
            return this.selectionList.get(checkIndex(indexBasedZero)).label();
        }

        @Override
        public int getColumnIndex(final @Nullable String columnLabel) throws DataAccessException {
            if (columnLabel == null) {
                throw new NullPointerException("columnLabel is null");
            }
            int index = -1;
            final Map<String, Integer> aliasToIndexMap = this.aliasToIndexMap;
            if (aliasToIndexMap == null) {
                final List<? extends Selection> selectionList = this.selectionList;
                final int columnSize = getColumnCount();
                for (int i = columnSize - 1; i > -1; i--) {  // If alias duplication,then override.
                    if (columnLabel.equals(selectionList.get(i).label())) {
                        index = i;
                        break;
                    }
                }
            } else {
                index = aliasToIndexMap.getOrDefault(columnLabel, -1);
            }
            if (index < 0) {
                throw _Exceptions.unknownSelectionAlias(columnLabel);
            }
            return index;
        }

        @Override
        public List<String> columnLabelList() {
            List<String> list = this.columnLabelList;
            if (list != null) {
                return list;
            }
            final List<? extends Selection> selectionList = this.selectionList;
            list = _Collections.arrayList(selectionList.size());
            for (Selection selection : selectionList) {
                list.add(selection.label());
            }
            this.columnLabelList = list = _Collections.unmodifiableList(list);
            return list;
        }

        @Override
        public List<? extends Selection> selectionList() throws DataAccessException {
            return this.selectionList;
        }

        @Override
        public Selection getSelection(final int indexBasedZero) throws DataAccessException {
            return this.selectionList.get(checkIndex(indexBasedZero));
        }


    } // JdbcStmtRecordMeta


    static final class JdbcProcRecordMeta extends JdbcRecordMeta {

        private Map<String, Integer> labelToIndexMap;

        private List<String> columnLabelList;


        JdbcProcRecordMeta(int resultNo, JdbcExecutor executor, DataType[] dataTypeArray, ResultSetMetaData meta) {
            super(resultNo, executor, dataTypeArray, meta);
        }

        @Override
        public String getColumnLabel(final int indexBasedZero) throws DataAccessException {
            try {
                return this.meta.getColumnLabel(checkIndexAndToBasedOne(indexBasedZero));
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Override
        public int getColumnIndex(final String columnLabel) throws DataAccessException {
            try {
                Map<String, Integer> map = this.labelToIndexMap;

                if (map == null) {
                    this.labelToIndexMap = map = createLabelToIndexMap();
                }
                Integer index;
                index = map.get(columnLabel);
                if (index == null) {
                    throw _Exceptions.unknownColumnLabel(columnLabel);
                }
                return index;
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }


        @Override
        public List<String> columnLabelList() {
            List<String> list = this.columnLabelList;
            if (list != null) {
                return list;
            }

            try {
                final ResultSetMetaData meta = this.meta;
                final int columnCount;
                columnCount = meta.getColumnCount();
                list = _Collections.arrayList(columnCount);
                for (int i = 0; i < columnCount; i++) {
                    list.add(meta.getColumnLabel(i + 1));
                }

                this.columnLabelList = list = _Collections.unmodifiableList(list);
                return list;
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Override
        public List<? extends Selection> selectionList() throws DataAccessException {
            throw noSelectionError();
        }

        @Override
        public Selection getSelection(final int indexBasedZero) throws DataAccessException {
            checkIndex(indexBasedZero);
            throw noSelectionError();
        }

        private Map<String, Integer> createLabelToIndexMap() throws SQLException {
            final ResultSetMetaData meta = this.meta;
            final int columnCount;
            columnCount = meta.getColumnCount();
            final Map<String, Integer> map = _Collections.hashMap((int) (columnCount / 0.75f));
            for (int i = 0; i < columnCount; i++) {
                map.put(meta.getColumnLabel(i + 1), i);
            }
            return _Collections.unmodifiableMap(map);
        }

        private DataAccessException noSelectionError() {
            String m = String.format("this %s is returned by store procedure,army don't known %s",
                    ResultSetMetaData.class.getName(), Selection.class);
            return new DataAccessException(m);
        }

    } // JdbcProcRecordMeta


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

    static abstract class SimpleResultStates extends JdbcResultStates {

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