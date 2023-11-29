package io.army.session.executor;

import io.army.ArmyException;
import io.army.bean.ObjectAccessException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ReadAccessor;
import io.army.criteria.CriteriaException;
import io.army.criteria.Selection;
import io.army.criteria.TypeInfer;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.MetaException;
import io.army.meta.TypeMeta;
import io.army.session.DataAccessException;
import io.army.session.Isolation;
import io.army.session.Option;
import io.army.session.record.*;
import io.army.sqltype.*;
import io.army.util._ClassUtils;
import io.army.util._Collections;

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

    protected final ArmyException unsupportedIsolation(Isolation isolation) {
        return new ArmyException(String.format("%s don't support %s", this, isolation));
    }


    protected static MappingType compatibleTypeFrom(final TypeInfer infer, final @Nullable Class<?> resultClass,
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
                compatibleType = type.compatibleFor(resultClass);
            }
        } else if (accessor.isWritable(fieldName, type.javaType())) {
            compatibleType = type;
        } else {
            compatibleType = type.compatibleFor(accessor.getJavaType(fieldName));
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
        switch (typeName.toLowerCase(Locale.ROOT)) {
            case "boolean":
            case "bool":
                type = PostgreType.BOOLEAN;
                break;
            case "int2":
            case "smallint":
            case "smallserial":
                type = PostgreType.SMALLINT;
                break;
            case "int":
            case "int4":
            case "serial":
            case "integer":
            case "xid":  // https://www.postgresql.org/docs/current/datatype-oid.html
            case "cid":  // https://www.postgresql.org/docs/current/datatype-oid.html
                type = PostgreType.INTEGER;
                break;
            case "int8":
            case "bigint":
            case "bigserial":
            case "serial8":
            case "xid8":  // https://www.postgresql.org/docs/current/datatype-oid.html  TODO what's tid ?
                type = PostgreType.BIGINT;
                break;
            case "numeric":
            case "decimal":
                type = PostgreType.DECIMAL;
                break;
            case "float8":
            case "double precision":
            case "float":
                type = PostgreType.FLOAT8;
                break;
            case "float4":
            case "real":
                type = PostgreType.REAL;
                break;
            case "char":
            case "character":
                type = PostgreType.CHAR;
                break;
            case "varchar":
            case "character varying":
                type = PostgreType.VARCHAR;
                break;
            case "text":
            case "txid_snapshot":  // TODO txid_snapshot is text?
                type = PostgreType.TEXT;
                break;
            case "bytea":
                type = PostgreType.BYTEA;
                break;
            case "date":
                type = PostgreType.DATE;
                break;
            case "time":
            case "time without time zone":
                type = PostgreType.TIME;
                break;
            case "timetz":
            case "time with time zone":
                type = PostgreType.TIMETZ;
                break;
            case "timestamp":
            case "timestamp without time zone":
                type = PostgreType.TIMESTAMP;
                break;
            case "timestamptz":
            case "timestamp with time zone":
                type = PostgreType.TIMESTAMPTZ;
                break;
            case "interval":
                type = PostgreType.INTERVAL;
                break;

            case "json":
                type = PostgreType.JSON;
                break;
            case "jsonb":
                type = PostgreType.JSONB;
                break;
            case "jsonpath":
                type = PostgreType.JSONPATH;
                break;
            case "xml":
                type = PostgreType.XML;
                break;

            case "bit":
                type = PostgreType.BIT;
                break;
            case "bit varying":
            case "varbit":
                type = PostgreType.VARBIT;
                break;

            case "cidr":
                type = PostgreType.CIDR;
                break;
            case "inet":
                type = PostgreType.INET;
                break;
            case "macaddr8":
                type = PostgreType.MACADDR8;
                break;
            case "macaddr":
                type = PostgreType.MACADDR;
                break;

            case "box":
                type = PostgreType.BOX;
                break;
            case "lseg":
                type = PostgreType.LSEG;
                break;
            case "line":
                type = PostgreType.LINE;
                break;
            case "path":
                type = PostgreType.PATH;
                break;
            case "point":
                type = PostgreType.POINT;
                break;
            case "circle":
                type = PostgreType.CIRCLE;
                break;
            case "polygon":
                type = PostgreType.POLYGON;
                break;

            case "tsvector":
                type = PostgreType.TSVECTOR;
                break;
            case "tsquery":
                type = PostgreType.TSQUERY;
                break;

            case "int4range":
                type = PostgreType.INT4RANGE;
                break;
            case "int8range":
                type = PostgreType.INT8RANGE;
                break;
            case "numrange":
                type = PostgreType.NUMRANGE;
                break;
            case "tsrange":
                type = PostgreType.TSRANGE;
                break;
            case "daterange":
                type = PostgreType.DATERANGE;
                break;
            case "tstzrange":
                type = PostgreType.TSTZRANGE;
                break;

            case "int4multirange":
                type = PostgreType.INT4MULTIRANGE;
                break;
            case "int8multirange":
                type = PostgreType.INT8MULTIRANGE;
                break;
            case "nummultirange":
                type = PostgreType.NUMMULTIRANGE;
                break;
            case "datemultirange":
                type = PostgreType.DATEMULTIRANGE;
                break;
            case "tsmultirange":
                type = PostgreType.TSMULTIRANGE;
                break;
            case "tstzmultirange":
                type = PostgreType.TSTZMULTIRANGE;
                break;

            case "uuid":
                type = PostgreType.UUID;
                break;
            case "money":
                type = PostgreType.MONEY;
                break;
            case "aclitem":
                type = PostgreType.ACLITEM;
                break;
            case "pg_lsn":
                type = PostgreType.PG_LSN;
                break;
            case "pg_snapshot":
                type = PostgreType.PG_SNAPSHOT;
                break;

            case "boolean[]":
            case "bool[]":
                type = PostgreType.BOOLEAN_ARRAY;
                break;
            case "int2[]":
            case "smallint[]":
            case "smallserial[]":
                type = PostgreType.SMALLINT_ARRAY;
                break;
            case "int[]":
            case "int4[]":
            case "integer[]":
            case "serial[]":
                type = PostgreType.INTEGER_ARRAY;
                break;
            case "int8[]":
            case "bigint[]":
            case "serial8[]":
            case "bigserial[]":
                type = PostgreType.BIGINT_ARRAY;
                break;
            case "numeric[]":
            case "decimal[]":
                type = PostgreType.DECIMAL_ARRAY;
                break;
            case "float8[]":
            case "float[]":
            case "double precision[]":
                type = PostgreType.FLOAT8_ARRAY;
                break;
            case "float4[]":
            case "real[]":
                type = PostgreType.REAL_ARRAY;
                break;

            case "char[]":
            case "character[]":
                type = PostgreType.CHAR_ARRAY;
                break;
            case "varchar[]":
            case "character varying[]":
                type = PostgreType.VARCHAR_ARRAY;
                break;
            case "text[]":
            case "txid_snapshot[]":
                type = PostgreType.TEXT_ARRAY;
                break;
            case "bytea[]":
                type = PostgreType.BYTEA_ARRAY;
                break;

            case "date[]":
                type = PostgreType.DATE_ARRAY;
                break;
            case "time[]":
            case "time without time zone[]":
                type = PostgreType.TIME_ARRAY;
                break;
            case "timetz[]":
            case "time with time zone[]":
                type = PostgreType.TIMETZ_ARRAY;
                break;
            case "timestamp[]":
            case "timestamp without time zone[]":
                type = PostgreType.TIMESTAMP_ARRAY;
                break;
            case "timestamptz[]":
            case "timestamp with time zone[]":
                type = PostgreType.TIMESTAMPTZ_ARRAY;
                break;
            case "interval[]":
                type = PostgreType.INTERVAL_ARRAY;
                break;

            case "json[]":
                type = PostgreType.JSON_ARRAY;
                break;
            case "jsonb[]":
                type = PostgreType.JSONB_ARRAY;
                break;
            case "jsonpath[]":
                type = PostgreType.JSONPATH_ARRAY;
                break;
            case "xml[]":
                type = PostgreType.XML_ARRAY;
                break;

            case "varbit[]":
            case "bit varying[]":
                type = PostgreType.VARBIT_ARRAY;
                break;
            case "bit[]":
                type = PostgreType.BIT_ARRAY;
                break;

            case "uuid[]":
                type = PostgreType.UUID_ARRAY;
                break;

            case "cidr[]":
                type = PostgreType.CIDR_ARRAY;
                break;
            case "inet[]":
                type = PostgreType.INET_ARRAY;
                break;
            case "macaddr[]":
                type = PostgreType.MACADDR_ARRAY;
                break;
            case "macaddr8[]":
                type = PostgreType.MACADDR8_ARRAY;
                break;

            case "box[]":
                type = PostgreType.BOX_ARRAY;
                break;
            case "lseg[]":
                type = PostgreType.LSEG_ARRAY;
                break;
            case "line[]":
                type = PostgreType.LINE_ARRAY;
                break;
            case "path[]":
                type = PostgreType.PATH_ARRAY;
                break;
            case "point[]":
                type = PostgreType.POINT_ARRAY;
                break;
            case "circle[]":
                type = PostgreType.CIRCLE_ARRAY;
                break;
            case "polygon[]":
                type = PostgreType.POLYGON_ARRAY;
                break;

            case "tsquery[]":
                type = PostgreType.TSQUERY_ARRAY;
                break;
            case "tsvector[]":
                type = PostgreType.TSVECTOR_ARRAY;
                break;

            case "int4range[]":
                type = PostgreType.INT4RANGE_ARRAY;
                break;
            case "int8range[]":
                type = PostgreType.INT8RANGE_ARRAY;
                break;
            case "numrange[]":
                type = PostgreType.NUMRANGE_ARRAY;
                break;
            case "daterange[]":
                type = PostgreType.DATERANGE_ARRAY;
                break;
            case "tsrange[]":
                type = PostgreType.TSRANGE_ARRAY;
                break;
            case "tstzrange[]":
                type = PostgreType.TSTZRANGE_ARRAY;
                break;

            case "int4multirange[]":
                type = PostgreType.INT4MULTIRANGE_ARRAY;
                break;
            case "int8multirange[]":
                type = PostgreType.INT8MULTIRANGE_ARRAY;
                break;
            case "nummultirange[]":
                type = PostgreType.NUMMULTIRANGE_ARRAY;
                break;
            case "datemultirange[]":
                type = PostgreType.DATEMULTIRANGE_ARRAY;
                break;
            case "tsmultirange[]":
                type = PostgreType.TSMULTIRANGE_ARRAY;
                break;
            case "tstzmultirange[]":
                type = PostgreType.TSTZMULTIRANGE_ARRAY;
                break;

            case "money[]":
                type = PostgreType.MONEY_ARRAY;
                break;
            case "pg_lsn[]":
                type = PostgreType.PG_LSN_ARRAY;
                break;
            case "pg_snapshot[]":
                type = PostgreType.PG_SNAPSHOT_ARRAY;
                break;
            case "aclitem[]":
                type = PostgreType.ACLITEM_ARRAY;
                break;
            default:
                type = DataType.from(typeName);
        }
        return type;
    }


    /*-------------------below Exception  -------------------*/


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

    protected static DataAccessException driverError() {
        // driver no bug,never here
        return new DataAccessException("driver error");
    }


    public static MetaException beforeBindMethodError(MappingType type, DataType dataType,
                                                      @Nullable Object returnValue) {
        String m = String.format("%s beforeBind() method return type %s and %s type not match.",
                type.getClass().getName(), _ClassUtils.safeClassName(returnValue), dataType);
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
        public final int getResultNo() {
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
            if (dataType instanceof SqlType) {
                armyType = ((SqlType) dataType).armyType();
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
        public final int getResultNo() {
            return getRecordMeta().getResultNo();
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


}
