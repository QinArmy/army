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

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.criteria.Selection;
import io.army.executor.DataAccessException;
import io.army.executor.DriverException;
import io.army.executor.ExecutorSupport;
import io.army.lang.Nullable;
import io.army.mapping.OffsetTimeType;
import io.army.meta.ServerMeta;
import io.army.option.Option;
import io.army.result.*;
import io.army.session.SyncSession;
import io.army.session.SyncStmtOption;
import io.army.sqltype.ArmyType;
import io.army.sqltype.DataType;
import io.army.stmt.DeclareCursorStmt;
import io.army.transaction.Isolation;
import io.army.type.BlobPath;
import io.army.type.TextPath;
import io.army.util.*;

import javax.sql.XAConnection;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>This class is base class of {@link JdbcExecutor}.
 *
 * @since 0.6.0
 */
abstract class JdbcExecutorSupport extends ExecutorSupport {

    static final String START_TRANSACTION_SPACE = "START TRANSACTION ";

    static final String COMMIT = "COMMIT";

    static final String ROLLBACK = "ROLLBACK";

    static final String READ_ONLY = "READ ONLY";

    static final String READ_WRITE = "READ WRITE";

    static final String ISOLATION_LEVEL_SPACE = "ISOLATION LEVEL ";

    static final Option<ServerMeta> SERVER_META = Option.from("SERVER_META", ServerMeta.class);

    static final Option<Warning> WARNING = Option.from("WARNING", Warning.class);

    static final Option<Long> RESULT_NO = Option.from("RESULT_NO", Long.class);

    static final Option<Long> ROW_COUNT = Option.from("ROW_COUNT", Long.class);

    static final Option<Long> AFFECTED_ROWS = Option.from("AFFECTED_ROWS", Long.class);

    static final Option<Boolean> HAS_MORE_FETCH = Option.from("HAS_MORE_FETCH", Boolean.class);

    static final Option<Boolean> HAS_COLUMN = Option.from("HAS_COLUMN", Boolean.class);

    static final Option<Boolean> HAS_MORE_RESULT = Option.from("HAS_MORE_RESULT", Boolean.class);

    static final Option<Integer> BATCH_SIZE = Option.from("BATCH_SIZE", Integer.class);

    static final Option<Integer> BATCH_NO = Option.from("BATCH_NO", Integer.class);

    static final Option<Long> LAST_INSERTED_ID = Option.from("LAST_INSERTED_ID", Long.class);


    JdbcExecutorSupport() {
    }

    static void standardIsolation(final Isolation isolation, final StringBuilder builder) {

        if (isolation == Isolation.READ_COMMITTED) {
            builder.append("READ COMMITTED");
        } else if (isolation == Isolation.REPEATABLE_READ) {
            builder.append("REPEATABLE READ");
        } else if (isolation == Isolation.SERIALIZABLE) {
            builder.append("SERIALIZABLE");
        } else if (isolation == Isolation.READ_UNCOMMITTED) {
            builder.append("READ UNCOMMITTED");
        } else {
            throw _Exceptions.unknownIsolation(isolation);
        }

    }


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

    static void closeResource(final @Nullable AutoCloseable resource)
            throws ArmyException {
        if (resource == null) {
            return;
        }
        try {
            resource.close();
        } catch (Exception e) {
            throw JdbcExecutor.wrapError(e);
        }

    }

    static void closeXaConnection(XAConnection conn) {
        try {
            conn.close();
        } catch (Exception e) {
            throw JdbcExecutor.wrapError(e);
        }
    }

    static void closeJdbcConnection(final @Nullable Object conn) {
        if (conn instanceof Connection) {
            closeResource((Connection) conn);
        } else if (conn instanceof XAConnection) {
            closeXaConnection((XAConnection) conn);
        } else if (conn != null) {
            throw new IllegalArgumentException();
        }
    }

    /*
     * not java doc
     * @see JdbcExecutor#executeMultiStmtBatchQuery(BatchStmt, SyncStmtOption, Function)
     * @see JdbcExecutor.MultiSmtBatchRowSpliterator#nextResultSet()
     */
    @Nullable
    static ResultSet multiStatementNextResultSet(final Statement statement, final int groupIndex,
                                                 final int expectedCount) throws SQLException {
        final ResultSet resultSet;
        if (statement.getMoreResults()) {
            resultSet = statement.getResultSet();
            if (resultSet == null) {
                throw jdbcMultiStmtGetResultSetError();
            } else if (groupIndex >= 0) {
                closeResource(resultSet);
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                throw multiStatementGreaterThanExpected(groupIndex, expectedCount);
            }
        } else if (statement.getUpdateCount() != -1) {
            statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
            if (groupIndex < expectedCount) {
                throw multiStatementPartNotQuery(groupIndex);
            }
            throw multiStatementGreaterThanExpected(groupIndex, expectedCount);
        } else if (groupIndex < expectedCount) {
            throw multiStatementLessThanExpected(groupIndex, expectedCount);
        } else {
            resultSet = null;
        }
        return resultSet;
    }


    static CriteriaException multiStatementGreaterThanExpected(int groupIndex, int expected) {
        String m = String.format("Multi-statement batch query ResultSet count[%s] greater than expected count[%s]",
                groupIndex, expected
        );  // here groupIndex don't plus 1 .
        throw new CriteriaException(m);
    }


    static CriteriaException multiStatementLessThanExpected(int count, int expected) {
        String m = String.format("Multi-statement batch query ResultSet count[%s] less than expected count[%s]",
                count, expected
        );  // here groupIndex don't plus 1 .
        throw new CriteriaException(m);
    }


    static CriteriaException multiStatementPartNotQuery(int groupIndex) {
        String m = String.format("Multi-statement batch query number %s result isn't ResultSet", groupIndex + 1);
        return new CriteriaException(m);
    }


    /*-------------------below private static methods  -------------------*/

    private static DataAccessException jdbcMultiStmtGetResultSetError() {
        String m = "Jdbc error statement.getMoreResults() is true ,but statement.getResultSet() is null";
        return new DataAccessException(m);
    }


    /*-------------------below static class  -------------------*/

    static abstract class JdbcRecordMeta extends ArmyResultRecordMeta {

        final JdbcExecutor executor;

        final ResultSetMetaData meta;

        JdbcRecordMeta(int resultNo, JdbcExecutor executor, DataType[] dataTypeArray, ResultSetMetaData meta) {
            super(resultNo, dataTypeArray, executor.factory.executorEnv);
            this.executor = executor;
            this.meta = meta;
        }


        @Nullable
        @Override
        public final <T> T getOf(int indexBasedZero, Option<T> option) throws DataAccessException {
            checkIndex(indexBasedZero);
            return null;
        }

        @Override
        public final Set<Option<?>> optionSet() {
            return Collections.emptySet();
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
                    javaType = BlobPath.class;
                    break;
                case LONGTEXT:
                    javaType = TextPath.class;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(armyType);
            }
            return javaType;
        }


    } // JdbcRecordMeta


    static final class JdbcStmtRecordMeta extends JdbcRecordMeta {

        private final List<? extends Selection> selectionList;

        private Map<String, Integer> aliasToIndexMap;

        private List<String> columnLabelList;

        JdbcStmtRecordMeta(int resultNo, JdbcExecutor executor, DataType[] dataTypeArray,
                           List<? extends Selection> selectionList, ResultSetMetaData meta) {
            super(resultNo, executor, dataTypeArray, meta);
            this.selectionList = selectionList;

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
            final List<? extends Selection> selectionList = this.selectionList;

            Map<String, Integer> aliasToIndexMap = this.aliasToIndexMap;
            if (aliasToIndexMap == null && selectionList.size() > 5) {
                this.aliasToIndexMap = aliasToIndexMap = createAliasToIndexMap(selectionList);
            }
            int index = -1;
            if (aliasToIndexMap == null) {

                final int columnSize = selectionList.size();
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


    static final class ArmyWarning implements Warning {

        private static final Set<Option<?>> OPTION_SET = ArrayUtils.asUnmodifiableSet(Option.SQL_STATE, Option.VENDOR_CODE);

        private final String message;

        private final String sqlState;

        private final int vendor;

        /**
         * @see JdbcExecutor#mapToArmyWarning(SQLWarning)
         */
        ArmyWarning(SQLWarning w) {
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


    static final class JdbcResultStates implements ResultStates {

        private final Function<Option<?>, ?> optionFunc;

        JdbcResultStates(Function<Option<?>, ?> optionFunc) {
            this.optionFunc = optionFunc;
        }

        @Override
        public long affectedRows() {
            final Object value;
            value = this.optionFunc.apply(AFFECTED_ROWS);
            if (value instanceof Long) {
                return (Long) value;
            }
            return 0;
        }

        @Override
        public int batchSize() {
            final Object value;
            value = this.optionFunc.apply(BATCH_SIZE);
            if (value instanceof Integer) {
                return (Integer) value;
            }
            return 0;
        }

        @Override
        public int batchNo() {
            final Object value;
            value = this.optionFunc.apply(BATCH_NO);
            if (value instanceof Integer) {
                return (Integer) value;
            }
            return 0;
        }

        @Override
        public boolean isSupportInsertId() {
            return this.optionFunc.apply(LAST_INSERTED_ID) instanceof Long;
        }

        @Override
        public long lastInsertedId() throws DataAccessException {
            final Object value;
            value = this.optionFunc.apply(LAST_INSERTED_ID);
            if (value instanceof Long) {
                return (Long) value;
            }
            throw dontSupportLastInsertedId();
        }

        @Override
        public boolean inTransaction() {
            final Object value;
            value = this.optionFunc.apply(Option.IN_TRANSACTION);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            return false;
        }

        @Override
        public String message() {
            // jdbc don't support
            return "";
        }

        @Override
        public boolean hasMoreResult() {
            final Object value;
            value = this.optionFunc.apply(HAS_MORE_RESULT);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            return false;
        }

        @Override
        public boolean isLastStates() {
            final int batchSize = batchSize();
            final boolean match;
            if (batchSize > 0) {
                match = batchNo() == batchSize && !hasMoreResult() && !hasMoreFetch();
            } else {
                match = !hasMoreResult() && !hasMoreFetch();
            }
            return match;
        }

        @Override
        public boolean hasMoreFetch() {
            final Object value;
            value = this.optionFunc.apply(HAS_MORE_FETCH);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            return false;
        }

        @Override
        public boolean hasColumn() {
            final Object value;
            value = this.optionFunc.apply(HAS_COLUMN);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            return false;
        }

        @Override
        public long rowCount() {
            final Object value;
            value = this.optionFunc.apply(ROW_COUNT);
            if (value instanceof Long) {
                return (Long) value;
            }
            return 0;
        }

        @Override
        public Warning warning() {
            final Object value;
            value = this.optionFunc.apply(WARNING);
            if (value instanceof Warning) {
                return (Warning) value;
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(Option<T> option) {
            final Object value;
            value = this.optionFunc.apply(option);
            if (option.javaType().isInstance(value)) {
                return (T) value;
            }
            return null;
        }

        @Override
        public int resultNo() {
            final Object value;
            value = this.optionFunc.apply(RESULT_NO);
            if (value instanceof Integer) {
                return (Integer) value;
            }
            return 1; // default 1
        }

        private DataAccessException dontSupportLastInsertedId() {
            final ServerMeta meta = (ServerMeta) this.optionFunc.apply(SERVER_META);
            assert meta != null;
            String m = String.format("database[%s  %s] don't support lastInsertedId() method.",
                    meta.serverDatabase().name(), meta.version());
            return new DataAccessException(m);
        }


    } // MyJdbcResultStates


    interface XaConnectionExecutor {

        XAConnection getXAConnection();

        void closeXaConnection() throws SQLException;

    }


    protected static class JdbcStmtCursor extends ArmyStmtCursor implements SyncStmtCursor {

        static final String INVALID_CURSOR_NAME = "34000";

        final JdbcExecutor executor;

        final SyncStmtOption stmtOption;

        final DataType[] dataTypeArray;

        final Statement statement;

        private Class<?> resultClass;

        private Function<DataRecord, ?> classRowFunc, objectRowFunc;

        private Supplier<?> objectConstructor;

        private boolean cursorClosed;

        JdbcStmtCursor(JdbcExecutor executor, DeclareCursorStmt stmt, SyncStmtOption stmtOption,
                       Function<Option<?>, ?> sessionFunc) throws SQLException {
            super(stmt, sessionFunc);
            this.executor = executor;
            this.stmtOption = stmtOption;
            this.dataTypeArray = new DataType[this.selectionList.size()];
            this.statement = executor.conn.createStatement();
        }

        @Nullable
        @Override
        public final <R> R next(Class<R> resultClass) {
            return fetchOne(Direction.NEXT, resultClass, ResultStates.IGNORE_STATES);
        }

        @Nullable
        @Override
        public final <R> R nextObject(Supplier<R> constructor) {
            return fetchOneObject(Direction.NEXT, constructor, ResultStates.IGNORE_STATES);
        }

        @Nullable
        @Override
        public final <R> R nextRecord(Function<CurrentRecord, R> function) {
            return fetchOneRecord(Direction.NEXT, function, ResultStates.IGNORE_STATES);
        }

        @Nullable
        @Override
        public final <R> R fetchOne(Direction direction, Class<R> resultClass, Consumer<ResultStates> consumer) {
            if (direction.isNotOneRow()) {
                throw _Exceptions.cursorDirectionNotOneRow(direction);
            }
            return executeFetch(direction, null, resultClass, consumer)
                    .reduce(StreamFunctions::atMostOne)
                    .orElse(null);
        }

        @Nullable
        @Override
        public final <R> R fetchOneObject(Direction direction, Supplier<R> constructor, Consumer<ResultStates> consumer) {
            if (direction.isNotOneRow()) {
                throw _Exceptions.cursorDirectionNotOneRow(direction);
            }
            return executeFetchObject(direction, null, constructor, consumer)
                    .reduce(StreamFunctions::atMostOne)
                    .orElse(null);
        }

        @Nullable
        @Override
        public final <R> R fetchOneRecord(Direction direction, Function<? super CurrentRecord, R> function, Consumer<ResultStates> consumer) {
            if (direction.isNotOneRow()) {
                throw _Exceptions.cursorDirectionNotOneRow(direction);
            }
            return executeFetchRecord(direction, null, function, consumer, false)
                    .reduce(StreamFunctions::atMostOne)
                    .orElse(null);
        }

        @Override
        public final <R> Stream<R> fetch(Direction direction, Class<R> resultClass, Consumer<ResultStates> consumer) {
            if (direction.isNotNoRowCount()) {
                throw _Exceptions.cursorDirectionNoRowCount(direction);
            }
            return executeFetch(direction, null, resultClass, consumer);
        }

        @Override
        public final <R> Stream<R> fetch(Direction direction, long count, Class<R> resultClass, Consumer<ResultStates> consumer) {
            if (direction.isNotSupportRowCount()) {
                throw _Exceptions.cursorDirectionDontSupportRowCount(direction);
            }
            return executeFetch(direction, count, resultClass, consumer);
        }

        @Override
        public final <R> Stream<R> fetchObject(Direction direction, Supplier<R> constructor, Consumer<ResultStates> consumer) {
            if (direction.isNotNoRowCount()) {
                throw _Exceptions.cursorDirectionNoRowCount(direction);
            }
            return executeFetchObject(direction, null, constructor, consumer);
        }

        @Override
        public final <R> Stream<R> fetchObject(Direction direction, long count, Supplier<R> constructor, Consumer<ResultStates> consumer) {
            if (direction.isNotSupportRowCount()) {
                throw _Exceptions.cursorDirectionDontSupportRowCount(direction);
            }
            return executeFetchObject(direction, count, constructor, consumer);
        }

        @Override
        public final <R> Stream<R> fetchRecord(Direction direction, Function<? super CurrentRecord, R> function, Consumer<ResultStates> consumer) {
            if (direction.isNotNoRowCount()) {
                throw _Exceptions.cursorDirectionNoRowCount(direction);
            }
            return executeFetchRecord(direction, null, function, consumer, false);
        }

        @Override
        public final <R> Stream<R> fetchRecord(Direction direction, long count, Function<? super CurrentRecord, R> function, Consumer<ResultStates> consumer) {
            if (direction.isNotSupportRowCount()) {
                throw _Exceptions.cursorDirectionDontSupportRowCount(direction);
            }
            return executeFetchRecord(direction, count, function, consumer, false);
        }

        @Override
        public final Stream<ResultItem> fetch(Direction direction) {
            if (direction.isNotNoRowCount()) {
                throw _Exceptions.cursorDirectionNoRowCount(direction);
            }
            try {
                return executeFetchRecord(direction, null, CurrentRecord::asResultRecord, ResultStates.IGNORE_STATES, true);
            } catch (DriverException e) {
                if (INVALID_CURSOR_NAME.equals(e.getSqlState())) {
                    this.cursorClosed = true;
                }
                throw e;
            }
        }

        @Override
        public final Stream<ResultItem> fetch(Direction direction, long count) {
            if (direction.isNotSupportRowCount()) {
                throw _Exceptions.cursorDirectionDontSupportRowCount(direction);
            }
            try {
                return executeFetchRecord(direction, count, CurrentRecord::asResultRecord, ResultStates.IGNORE_STATES, true);
            } catch (DriverException e) {
                if (INVALID_CURSOR_NAME.equals(e.getSqlState())) {
                    this.cursorClosed = true;
                }
                throw e;
            }
        }

        @Override
        public final ResultStates move(Direction direction) {
            if (direction.isNotNoRowCount()) {
                throw _Exceptions.cursorDirectionNoRowCount(direction);
            }
            try {
                return this.executor.executeCursorMove(this, direction, null);
            } catch (DriverException e) {
                if (INVALID_CURSOR_NAME.equals(e.getSqlState())) {
                    this.cursorClosed = true;
                }
                throw e;
            }
        }

        @Override
        public final ResultStates move(Direction direction, long count) {
            if (direction.isNotSupportRowCount()) {
                throw _Exceptions.cursorDirectionDontSupportRowCount(direction);
            }
            try {
                return this.executor.executeCursorMove(this, direction, count);
            } catch (DriverException e) {
                if (INVALID_CURSOR_NAME.equals(e.getSqlState())) {
                    this.cursorClosed = true;
                }
                throw e;
            }
        }

        @Nullable
        @Override
        public final <T> T valueOf(Option<T> option) {
            // return null
            return null;
        }


        @Override
        public final SyncSession session() {
            return (SyncSession) this.session;
        }

        @Override
        public final boolean isClosed() {
            return this.cursorClosed;
        }

        @Override
        public final void close() throws DataAccessException {
            if (this.cursorClosed) {
                return;
            }
            this.cursorClosed = true;
            this.executor.executeCursorClose(this);
        }


        @SuppressWarnings("unchecked")
        private <R> Stream<R> executeFetch(Direction direction, @Nullable Long count, @Nullable Class<R> resultClass,
                                           @Nullable Consumer<ResultStates> consumer) {
            if (resultClass == null) {
                throw new NullPointerException();
            } else if (consumer == null) {
                throw new NullPointerException();
            }

            Function<DataRecord, R> rowFunc = null;
            final Class<?> prevResultClass = this.resultClass;
            if (prevResultClass == null) {
                this.resultClass = resultClass;
            } else if (resultClass == prevResultClass) {
                rowFunc = (Function<DataRecord, R>) this.classRowFunc;
            }
            if (rowFunc == null) {
                this.classRowFunc = rowFunc = RowFunctions.classRowFunc(resultClass, this.stmt);
            }
            return executeFetchRecord(direction, count, rowFunc, consumer, false);
        }


        @SuppressWarnings("unchecked")
        private <R> Stream<R> executeFetchObject(Direction direction, @Nullable Long count, @Nullable Supplier<R> constructor,
                                                 @Nullable Consumer<ResultStates> consumer) {
            if (constructor == null) {
                throw new NullPointerException();
            } else if (consumer == null) {
                throw new NullPointerException();
            }

            Function<DataRecord, R> rowFunc = null;
            final Supplier<?> prevObjectConstructor = this.objectConstructor;
            if (prevObjectConstructor == null) {
                this.objectConstructor = constructor;
            } else if (prevObjectConstructor == constructor) {
                rowFunc = (Function<DataRecord, R>) this.objectRowFunc;
            }
            if (rowFunc == null) {
                this.objectRowFunc = rowFunc = RowFunctions.objectRowFunc(constructor, this.selectionList, true);
            }
            return executeFetchRecord(direction, count, rowFunc, consumer, false);
        }

        private <R> Stream<R> executeFetchRecord(Direction direction, @Nullable Long count, @Nullable Function<? super CurrentRecord, R> function,
                                                 @Nullable Consumer<ResultStates> consumer, boolean resultItemStream) {
            if (function == null) {
                throw new NullPointerException();
            } else if (consumer == null) {
                throw new NullPointerException();
            } else if (isClosed()) {
                throw _Exceptions.cursorHaveClosed(this.name());
            }
            try {
                return this.executor.executeCursorFetch(this, direction, count, function, consumer, resultItemStream);
            } catch (DriverException e) {
                if (INVALID_CURSOR_NAME.equals(e.getSqlState())) {
                    this.cursorClosed = true;
                }
                throw e;
            }
        }


    } // JdbcSyncStmtCursor


}
