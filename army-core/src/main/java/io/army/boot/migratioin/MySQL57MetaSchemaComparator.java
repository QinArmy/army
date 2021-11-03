package io.army.boot.migratioin;

import io.army.GenericRmSessionFactory;
import io.army.dialect.Database;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.schema.SchemaInfoException;
import io.army.sqldatatype.MySQLDataType;
import io.army.sqldatatype.SqlType;
import io.army.util.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class MySQL57MetaSchemaComparator extends AbstractMetaSchemaComparator {


    static final Map<MySQLDataType, List<String>> SYNONYMS_MAP = createSynonymsMap();

    static final Set<MySQLDataType> STRING_TYPE_SET = createStringTypeSet();

    static final Set<MySQLDataType> NUMERIC_TYPE_SET = createNumericTypeSet();

    static final Set<MySQLDataType> SPATIAL_TYPE_SET = createSpatialTypeSet();

    static final Set<MySQLDataType> TIME_TYPE_SET = createTimeTypeSet();

    static final Set<MySQLDataType> MYSQL57_NO_DEFAULT_TYPE_SET = create57NoDefaultType();

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * @return a unmodifiable map
     */
    private static Map<MySQLDataType, List<String>> createSynonymsMap() {

        EnumMap<MySQLDataType, List<String>> map = new EnumMap<>(MySQLDataType.class);

        map.put(MySQLDataType.BOOLEAN, ArrayUtils.asUnmodifiableList("TINYINT", "BOOL"));
        map.put(MySQLDataType.DOUBLE, Collections.singletonList("DOUBLE PRECISION"));
        map.put(MySQLDataType.INT, Collections.singletonList("INTEGER"));
        return Collections.unmodifiableMap(map);
    }

    /**
     * @return a unmodifiable set
     */
    private static Set<MySQLDataType> createStringTypeSet() {
        EnumSet<MySQLDataType> set = EnumSet.of(
                MySQLDataType.CHAR,
                MySQLDataType.NCHAR,
                MySQLDataType.VARCHAR,
                MySQLDataType.NVARCHAR,

                MySQLDataType.BINARY,
                MySQLDataType.VARBINARY,
                MySQLDataType.TINYBLOB,
                MySQLDataType.BLOB,

                MySQLDataType.MEDIUMBLOB,
                MySQLDataType.TINYTEXT,
                MySQLDataType.TEXT,
                MySQLDataType.MEDIUMTEXT,

                MySQLDataType.ENUM
        );
        return Collections.unmodifiableSet(set);
    }

    /**
     * @return a unmodifiable set
     */
    private static Set<MySQLDataType> createNumericTypeSet() {
        EnumSet<MySQLDataType> set = EnumSet.of(
                MySQLDataType.BIT,
                MySQLDataType.TINYINT,
                MySQLDataType.BOOLEAN,
                MySQLDataType.SMALLINT,

                MySQLDataType.MEDIUMINT,
                MySQLDataType.INT,
                MySQLDataType.BIGINT,
                MySQLDataType.DECIMAL,

                MySQLDataType.FLOAT,
                MySQLDataType.DOUBLE
        );
        return Collections.unmodifiableSet(set);
    }

    /**
     * @return a unmodifiable set
     */
    private static Set<MySQLDataType> createTimeTypeSet() {
        EnumSet<MySQLDataType> set = EnumSet.of(
                MySQLDataType.DATE,
                MySQLDataType.TIME,
                MySQLDataType.DATETIME,
                MySQLDataType.YEAR
        );
        return Collections.unmodifiableSet(set);
    }

    /**
     * @return a unmodifiable set
     */
    private static Set<MySQLDataType> createSpatialTypeSet() {
        EnumSet<MySQLDataType> set = EnumSet.of(
                MySQLDataType.GEOMETRY,
                MySQLDataType.POINT,
                MySQLDataType.LINESTRING,
                MySQLDataType.POLYGON,

                MySQLDataType.MULTIPOINT,
                MySQLDataType.MULTILINESTRING,
                MySQLDataType.MULTIPOLYGON,
                MySQLDataType.GEOMETRYCOLLECTION
        );
        return Collections.unmodifiableSet(set);
    }

    /**
     * @return a unmodifiable set
     */
    private static Set<MySQLDataType> create57NoDefaultType() {
        EnumSet<MySQLDataType> set = EnumSet.of(
                MySQLDataType.TINYBLOB,
                MySQLDataType.BLOB,
                MySQLDataType.MEDIUMBLOB,
                MySQLDataType.TINYTEXT,

                MySQLDataType.TEXT,
                MySQLDataType.MEDIUMTEXT
        );
        set.addAll(SPATIAL_TYPE_SET);
        set.add(MySQLDataType.JSON);

        return Collections.unmodifiableSet(set);
    }


    MySQL57MetaSchemaComparator(GenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected boolean needModifyPrecisionOrScale(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {
        MySQLDataType sqlDataType = (MySQLDataType) fieldMeta.mappingMeta().sqlDataType(database());
        boolean alter = false;
        if (STRING_TYPE_SET.contains(sqlDataType)) {
            alter = fieldMeta.precision() > columnInfo.columnSize();
        } else if (sqlDataType == MySQLDataType.DECIMAL) {
            alter = fieldMeta.precision() > columnInfo.columnSize() || fieldMeta.scale() > columnInfo.scale();
        } else if (sqlDataType == MySQLDataType.TIME) {
            alter = fieldMeta.precision() > (columnInfo.columnSize() - 9);
        } else if (sqlDataType == MySQLDataType.DATETIME) {
            alter = fieldMeta.precision() > (columnInfo.columnSize() - 20);
        }
        return alter;
    }

    protected Database database() {
        return Database.MySQL57;
    }

    @Override
    protected boolean needModifyDefault(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {
        MySQLDataType mysqlType = (MySQLDataType) fieldMeta.mappingMeta().sqlDataType(database());
        if (MYSQL57_NO_DEFAULT_TYPE_SET.contains(mysqlType)) {
            return false;
        }
        final String defaultValue = obtainDefaultValue(fieldMeta);
        boolean need = false;
        if (NUMERIC_TYPE_SET.contains(mysqlType)) {
            if (mysqlType == MySQLDataType.BIT) {
                need = !defaultValue.equals(columnInfo.defaultValue())
                        && bitValueLiteral(defaultValue);
            } else {
                need = !defaultValue.equals(columnInfo.defaultValue())
                        && (numericLiteral(defaultValue)
                        || hexadecimalLiterals(defaultValue));
            }
        } else if (STRING_TYPE_SET.contains(mysqlType)) {
            need = !defaultValue.equals(columnInfo.defaultValue())
                    && stringLiteral(defaultValue);
        } else if (TIME_TYPE_SET.contains(mysqlType)) {
            String timeLiteralValue = tryExtractDateOrTimeLiteralValue(mysqlType, defaultValue);
            if (timeLiteralValue == null) {
                need = mysqlType == MySQLDataType.DATETIME && equalsCurrentTimestamp(fieldMeta, defaultValue);
            } else {
                need = !timeLiteralValue.equals(columnInfo.defaultValue());
            }
        }
        return need;
    }

    @Override
    protected boolean synonyms(FieldMeta<?, ?> fieldMeta, String sqlTypeName) {
        String upperCaseTypName = sqlTypeName.toUpperCase();
        SqlType fieldDataType = fieldMeta.mappingMeta().sqlDataType(database());
        boolean match = fieldDataType.typeName().equals(upperCaseTypName);
        if (!match && fieldDataType instanceof MySQLDataType) {
            List<String> synonymsList = SYNONYMS_MAP.get(fieldDataType);
            match = synonymsList != null && synonymsList.contains(upperCaseTypName);
        }
        return match;
    }


    @Nullable
    String tryExtractDateOrTimeLiteralValue(MySQLDataType dataType, String defaultValue) {
        boolean match = false;
        switch (dataType) {
            case DATE:
                match = DATE_FORMAT_PATTERN.matcher(defaultValue).matches();
                break;
            case TIME:
                match = TIME_WITHOUT_ZONE_FORMAT_PATTERN.matcher(defaultValue).matches();
                break;
            case DATETIME:
                match = DATE_TIME_WITHOUT_ZONE_FORMAT_PATTERN.matcher(defaultValue).matches();
                break;
            case YEAR:
                match = YEAR_FORMAT_PATTERN.matcher(defaultValue).matches();
                break;
        }
        return match ? defaultValue.substring(1, defaultValue.length() - 1) : null;
    }

    final boolean equalsCurrentTimestamp(FieldMeta<?, ?> fieldMeta, String defaultValue) {
        String upperCaseDefault = defaultValue.toUpperCase();
        boolean match = false;
        if (fieldMeta.precision() < 1) {
            match = "CURRENT_TIMESTAMP".equals(upperCaseDefault);
        }
        if (!match) {
            String func = "CURRENT_TIMESTAMP(" + fieldMeta.precision() + ")";
            match = func.equals(upperCaseDefault);
        }
        return match;
    }


}
