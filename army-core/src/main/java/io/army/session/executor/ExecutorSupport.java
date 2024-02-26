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

package io.army.session.executor;

import io.army.ArmyException;
import io.army.bean.ObjectAccessException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadAccessor;
import io.army.criteria.CriteriaException;
import io.army.criteria.Selection;
import io.army.criteria.TypeInfer;
import io.army.env.ArmyKey;
import io.army.env.SqlLogMode;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.MetaException;
import io.army.meta.TypeMeta;
import io.army.session.*;
import io.army.session.record.*;
import io.army.sqltype.*;
import io.army.stmt.DeclareCursorStmt;
import io.army.util.ClassUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ExecutorSupport {


    protected static final ObjectAccessor SINGLE_COLUMN_PSEUDO_ACCESSOR = new PseudoWriterAccessor();

    protected static final ObjectAccessor RECORD_PSEUDO_ACCESSOR = new PseudoWriterAccessor();


    protected ExecutorSupport() {

    }


    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }


    protected final SqlLogMode readSqlLogMode(ExecutorFactorySupport factory) {
        final SqlLogMode mode;
        if (factory.sqlLogDynamic) {
            mode = factory.armyEnv.getOrDefault(ArmyKey.SQL_LOG_MODE);
        } else {
            mode = factory.sqlLogMode;
        }
        return mode;
    }


    protected final void printSqlIfNeed(final ExecutorFactorySupport factory, final String sessionName, final Logger log,
                                        final String sql) {
        final SqlLogMode mode;
        if (factory.sqlLogDynamic) {
            mode = factory.armyEnv.getOrDefault(ArmyKey.SQL_LOG_MODE);
        } else {
            mode = factory.sqlLogMode;
        }

        final String format = "session[name : {} , executorHash : {}]\n{}";
        switch (mode) {
            case OFF:
                break;
            case SIMPLE:
            case BEAUTIFY:
                log.info(format, sessionName, System.identityHashCode(this), sql);
                break;
            case DEBUG:
            case BEAUTIFY_DEBUG: {
                if (log.isDebugEnabled()) {
                    log.debug(format, sessionName, System.identityHashCode(this), sql);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }

    }

    protected final ArmyException unsupportedIsolation(Isolation isolation) {
        return new ArmyException(String.format("%s don't support %s", this, isolation));
    }


    protected static MappingType compatibleTypeFrom(final TypeInfer infer, final DataType dataType,
                                                    final @Nullable Class<?> resultClass,
                                                    final ObjectAccessor accessor, final String fieldName)
            throws NoMatchMappingException {
        final MappingType type;
        if (infer instanceof MappingType) {
            type = (MappingType) infer;
        } else if (infer instanceof TypeMeta) {
            type = ((TypeMeta) infer).mappingType();
        } else {
            final TypeMeta meta = infer.typeMeta();
            if (meta instanceof MappingType) {
                type = (MappingType) meta;
            } else {
                type = meta.mappingType();
            }
        }

        final MappingType compatibleType;
        if (accessor == SINGLE_COLUMN_PSEUDO_ACCESSOR) {
            assert resultClass != null;
            if (resultClass.isAssignableFrom(type.javaType())) {
                compatibleType = type;
            } else {
                compatibleType = type.compatibleFor(dataType, resultClass);
            }
        } else if (accessor == RECORD_PSEUDO_ACCESSOR || accessor == ObjectAccessorFactory.MAP_ACCESSOR) {
            compatibleType = type;
        } else if (accessor.isWritable(fieldName, type.javaType())) {
            compatibleType = type;
        } else {
            compatibleType = type.compatibleFor(dataType, accessor.getJavaType(fieldName));
        }
        return compatibleType;
    }

    @SuppressWarnings("unchecked")
    protected static <R> Class<R> rowResultClass(R row) {
        final Class<?> resultClass;
        if (row instanceof Map) {
            resultClass = Map.class;
        } else {
            resultClass = row.getClass();
        }
        return (Class<R>) resultClass;
    }


    /**
     * @return a unmodified map
     */
    protected static Map<String, Integer> createAliasToIndexMap(final List<? extends Selection> selectionList) {
        final int selectionSize = selectionList.size();
        Map<String, Integer> map = _Collections.hashMap((int) (selectionSize / 0.75f));
        for (int i = 0; i < selectionSize; i++) {
            map.put(selectionList.get(i).label(), i); // If alias duplication,then override.
        }
        return _Collections.unmodifiableMap(map);
    }

    protected static Map<String, Selection> createLabelToSelectionMap(final List<? extends Selection> selectionList) {
        final int selectionSize = selectionList.size();
        final Map<String, Selection> map = _Collections.hashMapForSize(selectionSize);
        Selection selection;
        for (int i = 0; i < selectionSize; i++) {
            selection = selectionList.get(i);
            map.put(selection.label(), selection); // If alias duplication,then override.
        }
        return _Collections.unmodifiableMap(map);
    }

    /**
     * This method is designed for second query,so :
     * <ul>
     *     <li>resultList should be {@link java.util.ArrayList}</li>
     *     <li>If accessor is {@link ExecutorSupport#SINGLE_COLUMN_PSEUDO_ACCESSOR} ,then resultList representing single column row</li>
     * </ul>
     */
    protected static <R> Map<Object, R> createIdToRowMap(final List<R> resultList, final String idFieldName,
                                                         final ObjectAccessor accessor) {
        final int rowSize = resultList.size();
        final Map<Object, R> map = _Collections.hashMap((int) (rowSize / 0.75f));
        final boolean singleColumnRow = accessor == SINGLE_COLUMN_PSEUDO_ACCESSOR;

        Object id;
        R row;
        for (int i = 0; i < rowSize; i++) {

            row = resultList.get(i);

            if (row == null) {
                // no bug,never here
                throw new NullPointerException(String.format("%s row is null", i + 1));
            }

            if (singleColumnRow) {
                id = row;
            } else {
                id = accessor.get(row, idFieldName);
            }

            if (id == null) {
                // no bug,never here
                throw new NullPointerException(String.format("%s row id is null", i + 1));
            }

            if (map.putIfAbsent(id, row) != null) {
                throw new CriteriaException(String.format("%s row id[%s] duplication", i + 1, id));
            }

        } // for loop


        return _Collections.unmodifiableMap(map);
    }

    protected static <T> T convertToTarget(Object source, Class<T> targetClass) {
        throw new UnsupportedOperationException();
    }


    protected static MySQLType getMySqlType(final String typeName) {
        final MySQLType type;

        switch (typeName.toUpperCase(Locale.ROOT)) {
            case "BOOL":
            case "BOOLEAN":
                type = MySQLType.BOOLEAN;
                break;
            case "TINYINT":
                type = MySQLType.TINYINT;
                break;
            case "TINYINT UNSIGNED":
                type = MySQLType.TINYINT_UNSIGNED;
                break;
            case "SMALLINT":
                type = MySQLType.SMALLINT;
                break;
            case "SMALLINT UNSIGNED":
                type = MySQLType.SMALLINT_UNSIGNED;
                break;
            case "MEDIUMINT":
                type = MySQLType.MEDIUMINT;
                break;
            case "MEDIUMINT UNSIGNED":
                type = MySQLType.MEDIUMINT_UNSIGNED;
                break;
            case "INT":
            case "INTEGER":
                type = MySQLType.INT;
                break;
            case "INT UNSIGNED":
            case "INTEGER UNSIGNED":
                type = MySQLType.INT_UNSIGNED;
                break;
            case "BIGINT":
                type = MySQLType.BIGINT;
                break;
            case "BIGINT UNSIGNED":
                type = MySQLType.BIGINT_UNSIGNED;
                break;
            case "DECIMAL":
            case "DEC":
            case "NUMERIC":
                type = MySQLType.DECIMAL;
                break;
            case "DECIMAL UNSIGNED":
            case "DEC UNSIGNED":
            case "NUMERIC UNSIGNED":
                type = MySQLType.DECIMAL_UNSIGNED;
                break;
            case "FLOAT":
            case "FLOAT UNSIGNED":
                type = MySQLType.FLOAT;
                break;
            case "DOUBLE":
            case "DOUBLE UNSIGNED":
                type = MySQLType.DOUBLE;
                break;
            case "TIME":
                type = MySQLType.TIME;
                break;
            case "DATE":
                type = MySQLType.DATE;
                break;
            case "YEAR":
                type = MySQLType.YEAR;
                break;
            case "TIMESTAMP":
            case "DATETIME":
                type = MySQLType.DATETIME;
                break;
            case "CHAR":
                type = MySQLType.CHAR;
                break;
            case "VARCHAR":
                type = MySQLType.VARCHAR;
                break;
            case "BIT":
                type = MySQLType.BIT;
                break;
            case "ENUM":
                type = MySQLType.ENUM;
                break;
            case "SET":
                type = MySQLType.SET;
                break;
            case "JSON":
                type = MySQLType.JSON;
                break;
            case "TINYTEXT":
                type = MySQLType.TINYTEXT;
                break;
            case "MEDIUMTEXT":
                type = MySQLType.MEDIUMTEXT;
                break;
            case "TEXT":
                type = MySQLType.TEXT;
                break;
            case "LONGTEXT":
                type = MySQLType.LONGTEXT;
                break;
            case "BINARY":
                type = MySQLType.BINARY;
                break;
            case "VARBINARY":
                type = MySQLType.VARBINARY;
                break;
            case "TINYBLOB":
                type = MySQLType.TINYBLOB;
                break;
            case "MEDIUMBLOB":
                type = MySQLType.MEDIUMBLOB;
                break;
            case "BLOB":
                type = MySQLType.BLOB;
                break;
            case "LONGBLOB":
                type = MySQLType.LONGBLOB;
                break;
            case "GEOMETRY":
                type = MySQLType.GEOMETRY;
                break;
            case "NULL":
                type = MySQLType.NULL;
                break;
            case "UNKNOWN":
            default:
                type = MySQLType.UNKNOWN;
        }

        return type;
    }


    protected static DataType getPostgreType(final String typeName) {
        final DataType type;
        switch (typeName.toUpperCase(Locale.ROOT)) {
            case "BOOLEAN":
            case "BOOL":
                type = PostgreType.BOOLEAN;
                break;
            case "INT2":
            case "SMALLINT":
            case "SMALLSERIAL":
                type = PostgreType.SMALLINT;
                break;
            case "INT":
            case "INT4":
            case "SERIAL":
            case "INTEGER":
            case "XID":  // https://www.postgresql.org/docs/current/datatype-oid.html
            case "CID":  // https://www.postgresql.org/docs/current/datatype-oid.html
                type = PostgreType.INTEGER;
                break;
            case "INT8":
            case "BIGINT":
            case "BIGSERIAL":
            case "SERIAL8":
            case "XID8":  // https://www.postgresql.org/docs/current/datatype-oid.html  TODO what's tid ?
                type = PostgreType.BIGINT;
                break;
            case "NUMERIC":
            case "DECIMAL":
                type = PostgreType.DECIMAL;
                break;
            case "FLOAT8":
            case "DOUBLE PRECISION":
            case "FLOAT":
                type = PostgreType.FLOAT8;
                break;
            case "FLOAT4":
            case "REAL":
                type = PostgreType.REAL;
                break;
            case "CHAR":
            case "CHARACTER":
                type = PostgreType.CHAR;
                break;
            case "VARCHAR":
            case "CHARACTER VARYING":
                type = PostgreType.VARCHAR;
                break;
            case "BPCHAR":
                type = PostgreType.BPCHAR;
                break;
            case "TEXT":
            case "TXID_SNAPSHOT":  // TODO txid_snapshot is text?
                type = PostgreType.TEXT;
                break;
            case "BYTEA":
                type = PostgreType.BYTEA;
                break;
            case "DATE":
                type = PostgreType.DATE;
                break;
            case "TIME":
            case "TIME WITHOUT TIME ZONE":
                type = PostgreType.TIME;
                break;
            case "TIMETZ":
            case "TIME WITH TIME ZONE":
                type = PostgreType.TIMETZ;
                break;
            case "TIMESTAMP":
            case "TIMESTAMP WITHOUT TIME ZONE":
                type = PostgreType.TIMESTAMP;
                break;
            case "TIMESTAMPTZ":
            case "TIMESTAMP WITH TIME ZONE":
                type = PostgreType.TIMESTAMPTZ;
                break;
            case "INTERVAL":
                type = PostgreType.INTERVAL;
                break;

            case "JSON":
                type = PostgreType.JSON;
                break;
            case "JSONB":
                type = PostgreType.JSONB;
                break;
            case "JSONPATH":
                type = PostgreType.JSONPATH;
                break;
            case "XML":
                type = PostgreType.XML;
                break;

            case "BIT":
                type = PostgreType.BIT;
                break;
            case "BIT VARYING":
            case "VARBIT":
                type = PostgreType.VARBIT;
                break;

            case "CIDR":
                type = PostgreType.CIDR;
                break;
            case "INET":
                type = PostgreType.INET;
                break;
            case "MACADDR8":
                type = PostgreType.MACADDR8;
                break;
            case "MACADDR":
                type = PostgreType.MACADDR;
                break;

            case "BOX":
                type = PostgreType.BOX;
                break;
            case "LSEG":
                type = PostgreType.LSEG;
                break;
            case "LINE":
                type = PostgreType.LINE;
                break;
            case "PATH":
                type = PostgreType.PATH;
                break;
            case "POINT":
                type = PostgreType.POINT;
                break;
            case "CIRCLE":
                type = PostgreType.CIRCLE;
                break;
            case "POLYGON":
                type = PostgreType.POLYGON;
                break;

            case "TSVECTOR":
                type = PostgreType.TSVECTOR;
                break;
            case "TSQUERY":
                type = PostgreType.TSQUERY;
                break;

            case "INT4RANGE":
                type = PostgreType.INT4RANGE;
                break;
            case "INT8RANGE":
                type = PostgreType.INT8RANGE;
                break;
            case "NUMRANGE":
                type = PostgreType.NUMRANGE;
                break;
            case "TSRANGE":
                type = PostgreType.TSRANGE;
                break;
            case "DATERANGE":
                type = PostgreType.DATERANGE;
                break;
            case "TSTZRANGE":
                type = PostgreType.TSTZRANGE;
                break;

            case "INT4MULTIRANGE":
                type = PostgreType.INT4MULTIRANGE;
                break;
            case "INT8MULTIRANGE":
                type = PostgreType.INT8MULTIRANGE;
                break;
            case "NUMMULTIRANGE":
                type = PostgreType.NUMMULTIRANGE;
                break;
            case "DATEMULTIRANGE":
                type = PostgreType.DATEMULTIRANGE;
                break;
            case "TSMULTIRANGE":
                type = PostgreType.TSMULTIRANGE;
                break;
            case "TSTZMULTIRANGE":
                type = PostgreType.TSTZMULTIRANGE;
                break;

            case "UUID":
                type = PostgreType.UUID;
                break;
            case "MONEY":
                type = PostgreType.MONEY;
                break;
            case "RECORD":
                type = PostgreType.RECORD;
                break;
            case "ACLITEM":
                type = PostgreType.ACLITEM;
                break;
            case "PG_LSN":
                type = PostgreType.PG_LSN;
                break;
            case "PG_SNAPSHOT":
                type = PostgreType.PG_SNAPSHOT;
                break;

            case "BOOLEAN[]":
            case "BOOL[]":
                type = PostgreType.BOOLEAN_ARRAY;
                break;
            case "INT2[]":
            case "SMALLINT[]":
            case "SMALLSERIAL[]":
                type = PostgreType.SMALLINT_ARRAY;
                break;
            case "INT[]":
            case "INT4[]":
            case "INTEGER[]":
            case "SERIAL[]":
                type = PostgreType.INTEGER_ARRAY;
                break;
            case "INT8[]":
            case "BIGINT[]":
            case "SERIAL8[]":
            case "BIGSERIAL[]":
                type = PostgreType.BIGINT_ARRAY;
                break;
            case "NUMERIC[]":
            case "DECIMAL[]":
                type = PostgreType.DECIMAL_ARRAY;
                break;
            case "FLOAT8[]":
            case "FLOAT[]":
            case "DOUBLE PRECISION[]":
                type = PostgreType.FLOAT8_ARRAY;
                break;
            case "FLOAT4[]":
            case "REAL[]":
                type = PostgreType.REAL_ARRAY;
                break;

            case "CHAR[]":
            case "CHARACTER[]":
                type = PostgreType.CHAR_ARRAY;
                break;
            case "VARCHAR[]":
            case "CHARACTER VARYING[]":
                type = PostgreType.VARCHAR_ARRAY;
                break;
            case "TEXT[]":
            case "TXID_SNAPSHOT[]":
                type = PostgreType.TEXT_ARRAY;
                break;
            case "BYTEA[]":
                type = PostgreType.BYTEA_ARRAY;
                break;

            case "DATE[]":
                type = PostgreType.DATE_ARRAY;
                break;
            case "TIME[]":
            case "TIME WITHOUT TIME ZONE[]":
                type = PostgreType.TIME_ARRAY;
                break;
            case "TIMETZ[]":
            case "TIME WITH TIME ZONE[]":
                type = PostgreType.TIMETZ_ARRAY;
                break;
            case "TIMESTAMP[]":
            case "TIMESTAMP WITHOUT TIME ZONE[]":
                type = PostgreType.TIMESTAMP_ARRAY;
                break;
            case "TIMESTAMPTZ[]":
            case "TIMESTAMP WITH TIME ZONE[]":
                type = PostgreType.TIMESTAMPTZ_ARRAY;
                break;
            case "INTERVAL[]":
                type = PostgreType.INTERVAL_ARRAY;
                break;

            case "JSON[]":
                type = PostgreType.JSON_ARRAY;
                break;
            case "JSONB[]":
                type = PostgreType.JSONB_ARRAY;
                break;
            case "JSONPATH[]":
                type = PostgreType.JSONPATH_ARRAY;
                break;
            case "XML[]":
                type = PostgreType.XML_ARRAY;
                break;

            case "VARBIT[]":
            case "BIT VARYING[]":
                type = PostgreType.VARBIT_ARRAY;
                break;
            case "BIT[]":
                type = PostgreType.BIT_ARRAY;
                break;

            case "UUID[]":
                type = PostgreType.UUID_ARRAY;
                break;

            case "CIDR[]":
                type = PostgreType.CIDR_ARRAY;
                break;
            case "INET[]":
                type = PostgreType.INET_ARRAY;
                break;
            case "MACADDR[]":
                type = PostgreType.MACADDR_ARRAY;
                break;
            case "MACADDR8[]":
                type = PostgreType.MACADDR8_ARRAY;
                break;

            case "BOX[]":
                type = PostgreType.BOX_ARRAY;
                break;
            case "LSEG[]":
                type = PostgreType.LSEG_ARRAY;
                break;
            case "LINE[]":
                type = PostgreType.LINE_ARRAY;
                break;
            case "PATH[]":
                type = PostgreType.PATH_ARRAY;
                break;
            case "POINT[]":
                type = PostgreType.POINT_ARRAY;
                break;
            case "CIRCLE[]":
                type = PostgreType.CIRCLE_ARRAY;
                break;
            case "POLYGON[]":
                type = PostgreType.POLYGON_ARRAY;
                break;

            case "TSQUERY[]":
                type = PostgreType.TSQUERY_ARRAY;
                break;
            case "TSVECTOR[]":
                type = PostgreType.TSVECTOR_ARRAY;
                break;

            case "INT4RANGE[]":
                type = PostgreType.INT4RANGE_ARRAY;
                break;
            case "INT8RANGE[]":
                type = PostgreType.INT8RANGE_ARRAY;
                break;
            case "NUMRANGE[]":
                type = PostgreType.NUMRANGE_ARRAY;
                break;
            case "DATERANGE[]":
                type = PostgreType.DATERANGE_ARRAY;
                break;
            case "TSRANGE[]":
                type = PostgreType.TSRANGE_ARRAY;
                break;
            case "TSTZRANGE[]":
                type = PostgreType.TSTZRANGE_ARRAY;
                break;

            case "INT4MULTIRANGE[]":
                type = PostgreType.INT4MULTIRANGE_ARRAY;
                break;
            case "INT8MULTIRANGE[]":
                type = PostgreType.INT8MULTIRANGE_ARRAY;
                break;
            case "NUMMULTIRANGE[]":
                type = PostgreType.NUMMULTIRANGE_ARRAY;
                break;
            case "DATEMULTIRANGE[]":
                type = PostgreType.DATEMULTIRANGE_ARRAY;
                break;
            case "TSMULTIRANGE[]":
                type = PostgreType.TSMULTIRANGE_ARRAY;
                break;
            case "TSTZMULTIRANGE[]":
                type = PostgreType.TSTZMULTIRANGE_ARRAY;
                break;

            case "MONEY[]":
                type = PostgreType.MONEY_ARRAY;
                break;
            case "RECORD[]":
            case "_RECORD":
                type = PostgreType.RECORD_ARRAY;
                break;
            case "PG_LSN[]":
                type = PostgreType.PG_LSN_ARRAY;
                break;
            case "PG_SNAPSHOT[]":
                type = PostgreType.PG_SNAPSHOT_ARRAY;
                break;
            case "ACLITEM[]":
                type = PostgreType.ACLITEM_ARRAY;
                break;
            default:
                type = DataType.from(typeName);
        }
        return type;
    }


    /**
     * @see <a href="https://sqlite.org/datatype3.html">Datatypes In SQLite</a>
     * @see <a href="https://sqlite.org/datatypes.html">Datatypes In SQLite Version 2</a>
     */
    protected final DataType getSQLiteType(final String typeName) {
        final SQLiteType dataType;
        switch (typeName.toUpperCase(Locale.ROOT)) {
            case "BOOLEAN":
                dataType = SQLiteType.BOOLEAN;
                break;
            case "TINYINT":
                dataType = SQLiteType.TINYINT;
                break;
            case "SMALLINT":
                dataType = SQLiteType.SMALLINT;
                break;
            case "MEDIUMINT":
                dataType = SQLiteType.MEDIUMINT;
                break;
            case "INTEGER":
            case "INT":
                dataType = SQLiteType.INTEGER;
                break;
            case "BIGINT":
                dataType = SQLiteType.BIGINT;
                break;
            case "DECIMAL":
            case "NUMERIC":
                dataType = SQLiteType.DECIMAL;
                break;
            case "FLOAT":
                dataType = SQLiteType.FLOAT;
                break;
            case "DOUBLE":
            case "REAL":
                dataType = SQLiteType.DOUBLE;
                break;
            case "TIME":
                dataType = SQLiteType.TIME;
                break;
            case "TIME WITH TIMEZONE":
                dataType = SQLiteType.TIME_WITH_TIMEZONE;
                break;
            case "TIMESTAMP":
                dataType = SQLiteType.TIMESTAMP;
                break;
            case "TIMESTAMP WITH TIMEZONE":
                dataType = SQLiteType.TIMESTAMP_WITH_TIMEZONE;
                break;
            case "DATE":
                dataType = SQLiteType.DATE;
                break;
            case "YEAR":
                dataType = SQLiteType.YEAR;
                break;
            case "YEAR MONTH":
                dataType = SQLiteType.YEAR_MONTH;
                break;
            case "MONTH DAY":
                dataType = SQLiteType.MONTH_DAY;
                break;
            case "DURATION":
                dataType = SQLiteType.DURATION;
                break;
            case "PERIOD":
                dataType = SQLiteType.PERIOD;
                break;
            case "VARCHAR":
            case "CHAR":
                dataType = SQLiteType.VARCHAR;
                break;
            case "TEXT":
            case "CLOB":
                dataType = SQLiteType.TEXT;
                break;
            case "VARBINARY":
                dataType = SQLiteType.VARBINARY;
                break;
            case "BLOB":
                dataType = SQLiteType.BLOB;
                break;
            case "BIT":
                dataType = SQLiteType.BIT;
                break;
            case "JSON":
                dataType = SQLiteType.JSON;
                break;
            case "":
                dataType = SQLiteType.DYNAMIC;
                break;
            case "UNKNOWN":
            default:
                dataType = SQLiteType.UNKNOWN;
        }
        return dataType;
    }

    /*-------------------below Exception  -------------------*/

    protected static IllegalArgumentException notInTransactionAndChainConflict() {
        String m = String.format("session not in transaction block,don't support %s option", Option.CHAIN);
        return new IllegalArgumentException(m);
    }


    protected static NullPointerException currentRecordColumnIsNull(int indexBasedZero, String columnLabel) {
        String m = String.format("value is null of current record index[%s] column label[%s] ",
                indexBasedZero, columnLabel);
        return new NullPointerException(m);
    }

    protected static NullPointerException currentRecordDefaultValueNonNull() {
        return new NullPointerException("current record default must non-null");
    }

    protected static NullPointerException currentRecordSupplierReturnNull(Supplier<?> supplier) {
        String m = String.format("current record %s %s return null", Supplier.class.getName(), supplier);
        return new NullPointerException(m);
    }


    protected static DataAccessException secondQueryRowCountNotMatch(final int firstRowCount, final int secondRowCount) {
        String m = String.format("second query row count[%s] and first query row[%s] not match.",
                secondRowCount, firstRowCount);
        return new DataAccessException(m);
    }

    protected static DataAccessException transactionExistsRejectStart(String sessionName) {
        String m = String.format("Session[%s] in transaction ,reject start a new transaction before commit or rollback.", sessionName);
        return new DataAccessException(m);
    }

    protected static DataAccessException unknownIsolation(String isolation) {
        String m = String.format("unknown isolation %s", isolation);
        return new DataAccessException(m);
    }

    public static MetaException mapMethodError(MappingType type, DataType dataType) {
        String m = String.format("%s map(ServerMeta) method error,return %s ", type.getClass(), dataType);
        return new MetaException(m);
    }

    public static DataAccessException driverError() {
        // driver no bug,never here
        return new DataAccessException("driver error");
    }

    public static MetaException beforeBindMethodError(MappingType type, DataType dataType,
                                                      @Nullable Object returnValue) {
        String m = String.format("%s beforeBind() method return type %s and %s type not match.",
                type.getClass().getName(), ClassUtils.safeClassName(returnValue), dataType);
        return new MetaException(m);
    }

    public static MetaException afterGetMethodError(MappingType type, DataType dataType,
                                                    @Nullable Object returnValue) {
        String m = String.format("%s afterGet() method return type %s and %s type not match.",
                type.getClass().getName(), ClassUtils.safeClassName(returnValue), dataType);
        return new MetaException(m);
    }


    public static ArmyException executorFactoryClosed(ExecutorFactory factory) {
        String m = String.format("%s have closed.", factory);
        return new ArmyException(m);
    }


    protected static abstract class ArmyResultRecordMeta implements ResultRecordMeta {

        private final int resultNo;

        final DataType[] dataTypeArray;

        protected ArmyResultRecordMeta(int resultNo, DataType[] dataTypeArray) {
            assert resultNo > 0;
            this.resultNo = resultNo;
            this.dataTypeArray = dataTypeArray;
        }

        @Override
        public final int resultNo() {
            return this.resultNo;
        }

        @Override
        public final int getColumnCount() {
            return this.dataTypeArray.length;
        }

        @Override
        public final DataType getDataType(int indexBasedZero) throws DataAccessException {
            return this.dataTypeArray[checkIndex(indexBasedZero)];
        }

        @Override
        public final <T> T getNonNullOf(int indexBasedZero, Option<T> option) throws DataAccessException {
            final T value;
            value = getOf(indexBasedZero, option);
            if (value == null) {
                throw new NullPointerException();
            }
            return value;
        }

        @Override
        public final ArmyType getArmyType(final int indexBasedZero) throws DataAccessException {
            final DataType dataType;
            dataType = this.dataTypeArray[checkIndex(indexBasedZero)];
            final ArmyType armyType;
            if (dataType instanceof SQLType) {
                armyType = ((SQLType) dataType).armyType();
            } else {
                armyType = ArmyType.UNKNOWN;
            }
            return armyType;
        }



        /*-------------------below label methods -------------------*/

        @Override
        public final Selection getSelection(String columnLabel) throws DataAccessException {
            return getSelection(getColumnIndex(columnLabel));
        }

        @Override
        public final DataType getDataType(String columnLabel) throws DataAccessException {
            return getDataType(getColumnIndex(columnLabel));
        }

        @Override
        public final ArmyType getArmyType(String columnLabel) throws DataAccessException {
            return getArmyType(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final <T> T getOf(String columnLabel, Option<T> option) throws DataAccessException {
            return getOf(getColumnIndex(columnLabel), option);
        }

        @Override
        public final <T> T getNonNullOf(String columnLabel, Option<T> option) throws DataAccessException {
            return getNonNullOf(getColumnIndex(columnLabel), option);
        }

        @Nullable
        @Override
        public final String getCatalogName(String columnLabel) throws DataAccessException {
            return getCatalogName(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final String getSchemaName(String columnLabel) throws DataAccessException {
            return getSchemaName(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final String getTableName(String columnLabel) throws DataAccessException {
            return getTableName(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final String getColumnName(String columnLabel) throws DataAccessException {
            return getColumnName(getColumnIndex(columnLabel));
        }

        @Override
        public final int getPrecision(String columnLabel) throws DataAccessException {
            return getPrecision(getColumnIndex(columnLabel));
        }

        @Override
        public final int getScale(String columnLabel) throws DataAccessException {
            return getScale(getColumnIndex(columnLabel));
        }

        @Override
        public final FieldType getFieldType(String columnLabel) throws DataAccessException {
            return getFieldType(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final Boolean getAutoIncrementMode(String columnLabel) throws DataAccessException {
            return getAutoIncrementMode(getColumnIndex(columnLabel));
        }

        @Override
        public final KeyType getKeyMode(String columnLabel) throws DataAccessException {
            return getKeyMode(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final Boolean getNullableMode(String columnLabel) throws DataAccessException {
            return getNullableMode(getColumnIndex(columnLabel));
        }

        @Override
        public final Class<?> getFirstJavaType(String columnLabel) throws DataAccessException {
            return getFirstJavaType(getColumnIndex(columnLabel));
        }

        @Nullable
        @Override
        public final Class<?> getSecondJavaType(String columnLabel) throws DataAccessException {
            return getSecondJavaType(getColumnIndex(columnLabel));
        }

        public final int checkIndex(final int indexBasedZero) {
            if (indexBasedZero < 0 || indexBasedZero >= this.dataTypeArray.length) {
                String m = String.format("index not in [0,%s)", this.dataTypeArray.length);
                throw new DataAccessException(m);
            }
            return indexBasedZero;
        }

        public final int checkIndexAndToBasedOne(final int indexBasedZero) {
            if (indexBasedZero < 0 || indexBasedZero >= this.dataTypeArray.length) {
                String m = String.format("index not in [0,%s)", this.dataTypeArray.length);
                throw new DataAccessException(m);
            }
            return indexBasedZero + 1;
        }

    } // ArmyResultRecordMeta


    private static abstract class ArmyDataRecord implements DataRecord {


        @Override
        public final int resultNo() {
            return getRecordMeta().resultNo();
        }

        @Override
        public final int getColumnCount() {
            return getRecordMeta().getColumnCount();
        }

        @Override
        public final String getColumnLabel(int indexBasedZero) throws IllegalArgumentException {
            return getRecordMeta().getColumnLabel(indexBasedZero);
        }

        @Override
        public final int getColumnIndex(String columnLabel) throws IllegalArgumentException {
            return getRecordMeta().getColumnIndex(columnLabel);
        }

        @Override
        public final Object getNonNull(final int indexBasedZero) {
            final Object value;
            value = get(indexBasedZero);
            if (value == null) {
                throw currentRecordColumnIsNull(indexBasedZero, getColumnLabel(indexBasedZero));
            }
            return value;
        }

        @Override
        public final Object getOrDefault(int indexBasedZero, @Nullable Object defaultValue) {
            if (defaultValue == null) {
                throw currentRecordDefaultValueNonNull();
            }
            Object value;
            value = get(indexBasedZero);
            if (value == null) {
                value = defaultValue;
            }
            return value;
        }

        @Override
        public final Object getOrSupplier(int indexBasedZero, Supplier<?> supplier) {
            Object value;
            value = get(indexBasedZero);
            if (value == null) {
                if ((value = supplier.get()) == null) {
                    throw currentRecordSupplierReturnNull(supplier);
                }
            }
            return value;
        }


        @Override
        public final <T> T getNonNull(int indexBasedZero, Class<T> columnClass) {
            final T value;
            value = get(indexBasedZero, columnClass);
            if (value == null) {
                throw currentRecordColumnIsNull(indexBasedZero, getColumnLabel(indexBasedZero));
            }
            return value;
        }

        @Override
        public final <T> T getOrDefault(int indexBasedZero, Class<T> columnClass, final @Nullable T defaultValue) {
            if (defaultValue == null) {
                throw currentRecordDefaultValueNonNull();
            }
            T value;
            value = get(indexBasedZero, columnClass);
            if (value == null) {
                value = defaultValue;
            }
            return value;
        }

        @Override
        public final <T> T getOrSupplier(int indexBasedZero, Class<T> columnClass, Supplier<T> supplier) {
            T value;
            value = get(indexBasedZero, columnClass);
            if (value == null) {
                if ((value = supplier.get()) == null) {
                    throw currentRecordSupplierReturnNull(supplier);
                }
            }
            return value;
        }

        /*-------------------below label methods -------------------*/

        @Override
        public final Object get(String columnLabel) {
            return get(getRecordMeta().getColumnIndex(columnLabel));
        }

        @Override
        public final Object getNonNull(String columnLabel) {
            return getNonNull(getRecordMeta().getColumnIndex(columnLabel));
        }

        @Override
        public final Object getOrDefault(String columnLabel, Object defaultValue) {
            return getOrDefault(getRecordMeta().getColumnIndex(columnLabel), defaultValue);
        }

        @Override
        public final Object getOrSupplier(String columnLabel, Supplier<?> supplier) {
            return getOrSupplier(getRecordMeta().getColumnIndex(columnLabel), supplier);
        }

        @Override
        public final <T> T get(String columnLabel, Class<T> columnClass) {
            return get(getRecordMeta().getColumnIndex(columnLabel), columnClass);
        }

        @Override
        public final <T> T getNonNull(String columnLabel, Class<T> columnClass) {
            return getNonNull(getRecordMeta().getColumnIndex(columnLabel), columnClass);
        }

        @Override
        public final <T> T getOrDefault(String columnLabel, Class<T> columnClass, T defaultValue) {
            return getOrDefault(getRecordMeta().getColumnIndex(columnLabel), columnClass, defaultValue);
        }

        @Override
        public final <T> T getOrSupplier(String columnLabel, Class<T> columnClass, Supplier<T> supplier) {
            return getOrSupplier(getRecordMeta().getColumnIndex(columnLabel), columnClass, supplier);
        }


    } // ArmyDataRecord


    private static abstract class ArmyStmtDataRecord extends ArmyDataRecord {

        @SuppressWarnings("unchecked")
        @Override
        public final <T> T get(int indexBasedZero, Class<T> columnClass) {
            final Object value;
            value = get(indexBasedZero);
            if (value == null || columnClass.isInstance(value)) {
                return (T) value;
            }
            return convertToTarget(value, columnClass);
        }

    } // ArmyStmtDataRecord


    protected static abstract class ArmyDriverCurrentRecord extends ArmyDataRecord implements CurrentRecord {

        @Override
        public abstract ArmyResultRecordMeta getRecordMeta();

        @Override
        public final ResultRecord asResultRecord() {
            return new ArmyResultRecord(this);
        }

        protected abstract Object[] copyValueArray();


    } // ArmyDriverCurrentRecord


    protected static abstract class ArmyStmtCurrentRecord extends ArmyDataRecord implements CurrentRecord {


        @Override
        public abstract ArmyResultRecordMeta getRecordMeta();

        @Override
        public final ResultRecord asResultRecord() {
            return new ArmyResultRecord(this);
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <T> T get(int indexBasedZero, Class<T> columnClass) {
            final Object value;
            value = get(indexBasedZero);
            if (value == null || columnClass.isInstance(value)) {
                return (T) value;
            }
            return convertToTarget(value, columnClass);
        }

        protected abstract Object[] copyValueArray();


    }// ArmyStmtCurrentRecord


    private static final class ArmyResultRecord extends ArmyStmtDataRecord implements ResultRecord {


        private final ArmyResultRecordMeta meta;

        private final Object[] valueArray;

        private ArmyResultRecord(ArmyStmtCurrentRecord currentRecord) {
            this.meta = currentRecord.getRecordMeta();
            this.valueArray = currentRecord.copyValueArray();
            assert this.valueArray.length == this.meta.getColumnCount();
        }

        private ArmyResultRecord(ArmyDriverCurrentRecord currentRecord) {
            this.meta = currentRecord.getRecordMeta();
            this.valueArray = currentRecord.copyValueArray();
            assert this.valueArray.length == this.meta.getColumnCount();
        }

        @Override
        public ResultRecordMeta getRecordMeta() {
            return this.meta;
        }

        @Override
        public Object get(int indexBasedZero) {
            return this.valueArray[this.meta.checkIndex(indexBasedZero)];
        }

    }// ArmyResultRecord


    private static final class PseudoWriterAccessor implements ObjectAccessor {

        @Override
        public boolean isWritable(String propertyName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWritable(String propertyName, Class<?> valueType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class<?> getJavaType(String propertyName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(Object target, String propertyName, @Nullable Object value) throws ObjectAccessException {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadAccessor getReadAccessor() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReadable(String propertyName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object get(Object target, String propertyName) throws ObjectAccessException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class<?> getAccessedType() {
            throw new UnsupportedOperationException();
        }

    }// PseudoWriterAccessor


    protected static abstract class ArmyStmtCursor implements StmtCursor {

        protected final DeclareCursorStmt stmt;

        protected final Session session;

        protected final List<? extends Selection> selectionList;

        private Map<String, Selection> selectionMap;

        protected ArmyStmtCursor(DeclareCursorStmt stmt, Session session) {
            this.stmt = stmt;
            this.session = session;
            this.selectionList = stmt.selectionList();
        }


        @Override
        public final String name() {
            return this.stmt.cursorName();
        }

        @Override
        public final String safeName() {
            return this.stmt.safeCursorName();
        }

        @Override
        public final List<? extends Selection> selectionList() {
            return this.selectionList;
        }

        @Override
        public final Selection selection(final int indexBasedZero) {
            if (indexBasedZero < 0 || indexBasedZero >= this.selectionList.size()) {
                String m = String.format("index[%s] not in [0,%s)", indexBasedZero, this.selectionList.size());
                throw new IllegalArgumentException(m);
            }
            return this.selectionList.get(indexBasedZero);
        }

        @Override
        public final Selection selection(final String name) {
            Map<String, Selection> map = this.selectionMap;
            if (map == null) {
                this.selectionMap = map = createLabelToSelectionMap(this.selectionList);
            }
            return map.get(name);
        }


    } // ArmyStmtCursor


}
