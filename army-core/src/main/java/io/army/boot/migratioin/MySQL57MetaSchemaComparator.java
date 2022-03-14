package io.army.boot.migratioin;

import io.army.Database;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.schema.SchemaInfoException;
import io.army.session.DialectSessionFactory;
import io.army.sqltype.MySqlType;
import io.army.util.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class MySQL57MetaSchemaComparator extends AbstractMetaSchemaComparator {


    static final Map<MySqlType, List<String>> SYNONYMS_MAP = createSynonymsMap();

    static final Set<MySqlType> STRING_TYPE_SET = createStringTypeSet();

    static final Set<MySqlType> NUMERIC_TYPE_SET = createNumericTypeSet();

    static final Set<MySqlType> SPATIAL_TYPE_SET = createSpatialTypeSet();

    static final Set<MySqlType> TIME_TYPE_SET = createTimeTypeSet();

    static final Set<MySqlType> MYSQL57_NO_DEFAULT_TYPE_SET = create57NoDefaultType();

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * @return a unmodifiable map
     */
    private static Map<MySqlType, List<String>> createSynonymsMap() {

        EnumMap<MySqlType, List<String>> map = new EnumMap<>(MySqlType.class);

        map.put(MySqlType.BOOLEAN, ArrayUtils.asUnmodifiableList("TINYINT", "BOOL"));
        map.put(MySqlType.DOUBLE, Collections.singletonList("DOUBLE PRECISION"));
        map.put(MySqlType.INT, Collections.singletonList("INTEGER"));
        return Collections.unmodifiableMap(map);
    }

    /**
     * @return a unmodifiable set
     */
    private static Set<MySqlType> createStringTypeSet() {
        EnumSet<MySqlType> set = EnumSet.of(
                MySqlType.CHAR,
                //MySQLDataType.NCHAR,
                MySqlType.VARCHAR,
                // MySQLDataType.NVARCHAR,

                MySqlType.BINARY,
                MySqlType.VARBINARY,
                MySqlType.TINYBLOB,
                MySqlType.BLOB,

                MySqlType.MEDIUMBLOB,
                MySqlType.TINYTEXT,
                MySqlType.TEXT,
                MySqlType.MEDIUMTEXT,

                MySqlType.ENUM
        );
        return Collections.unmodifiableSet(set);
    }

    /**
     * @return a unmodifiable set
     */
    private static Set<MySqlType> createNumericTypeSet() {
        EnumSet<MySqlType> set = EnumSet.of(
                MySqlType.BIT,
                MySqlType.TINYINT,
                MySqlType.BOOLEAN,
                MySqlType.SMALLINT,

                MySqlType.MEDIUMINT,
                MySqlType.INT,
                MySqlType.BIGINT,
                MySqlType.DECIMAL,

                MySqlType.FLOAT,
                MySqlType.DOUBLE
        );
        return Collections.unmodifiableSet(set);
    }

    /**
     * @return a unmodifiable set
     */
    private static Set<MySqlType> createTimeTypeSet() {
        EnumSet<MySqlType> set = EnumSet.of(
                MySqlType.DATE,
                MySqlType.TIME,
                MySqlType.DATETIME,
                MySqlType.YEAR
        );
        return Collections.unmodifiableSet(set);
    }

    /**
     * @return a unmodifiable set
     */
    private static Set<MySqlType> createSpatialTypeSet() {
        EnumSet<MySqlType> set = EnumSet.of(
                // MySQLDataType.GEOMETRY,
                MySqlType.POINT,
                MySqlType.LINESTRING,
                MySqlType.POLYGON,

                MySqlType.MULTIPOINT,
                MySqlType.MULTILINESTRING,
                MySqlType.MULTIPOLYGON,
                MySqlType.GEOMETRYCOLLECTION
        );
        return Collections.unmodifiableSet(set);
    }

    /**
     * @return a unmodifiable set
     */
    private static Set<MySqlType> create57NoDefaultType() {
        EnumSet<MySqlType> set = EnumSet.of(
                MySqlType.TINYBLOB,
                MySqlType.BLOB,
                MySqlType.MEDIUMBLOB,
                MySqlType.TINYTEXT,

                MySqlType.TEXT,
                MySqlType.MEDIUMTEXT
        );
        set.addAll(SPATIAL_TYPE_SET);
        set.add(MySqlType.JSON);

        return Collections.unmodifiableSet(set);
    }


    MySQL57MetaSchemaComparator(DialectSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected boolean needModifyPrecisionOrScale(FieldMeta<?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {
        // MySQLDataType sqlDataType = (MySQLDataType) fieldMeta.mappingMeta().sqlDataType(database());
//        boolean alter = false;
//        if (STRING_TYPE_SET.contains(sqlDataType)) {
//            alter = fieldMeta.precision() > columnInfo.columnSize();
//        } else if (sqlDataType == MySQLDataType.DECIMAL) {
//            alter = fieldMeta.precision() > columnInfo.columnSize() || fieldMeta.scale() > columnInfo.scale();
//        } else if (sqlDataType == MySQLDataType.TIME) {
//            alter = fieldMeta.precision() > (columnInfo.columnSize() - 9);
//        } else if (sqlDataType == MySQLDataType.DATETIME) {
//            alter = fieldMeta.precision() > (columnInfo.columnSize() - 20);
//        }
//        return alter;
        return false;
    }

    protected Database database() {
        return Database.MySQL;
    }

    @Override
    protected boolean needModifyDefault(FieldMeta<?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {
        //MySQLDataType mysqlType = (MySQLDataType) fieldMeta.mappingMeta().sqlDataType(database());
//        if (MYSQL57_NO_DEFAULT_TYPE_SET.contains(mysqlType)) {
//            return false;
//        }
//        final String defaultValue = obtainDefaultValue(fieldMeta);
//        boolean need = false;
//        if (NUMERIC_TYPE_SET.contains(mysqlType)) {
//            if (mysqlType == MySQLDataType.BIT) {
//                need = !defaultValue.equals(columnInfo.defaultValue())
//                        && bitValueLiteral(defaultValue);
//            } else {
//                need = !defaultValue.equals(columnInfo.defaultValue())
//                        && (numericLiteral(defaultValue)
//                        || hexadecimalLiterals(defaultValue));
//            }
//        } else if (STRING_TYPE_SET.contains(mysqlType)) {
//            need = !defaultValue.equals(columnInfo.defaultValue())
//                    && stringLiteral(defaultValue);
//        } else if (TIME_TYPE_SET.contains(mysqlType)) {
//            String timeLiteralValue = tryExtractDateOrTimeLiteralValue(mysqlType, defaultValue);
//            if (timeLiteralValue == null) {
//                need = mysqlType == MySQLDataType.DATETIME && equalsCurrentTimestamp(fieldMeta, defaultValue);
//            } else {
//                need = !timeLiteralValue.equals(columnInfo.defaultValue());
//            }
//        }
//        return need;
        return false;
    }

    @Override
    protected boolean synonyms(FieldMeta<?> fieldMeta, String sqlTypeName) {
//        String upperCaseTypName = sqlTypeName.toUpperCase();
//        SqlDataType fieldDataType = fieldMeta.mappingMeta().sqlDataType(database());
//        boolean match = fieldDataType.typeName().equals(upperCaseTypName);
//        if (!match && fieldDataType instanceof MySQLDataType) {
//            List<String> synonymsList = SYNONYMS_MAP.get(fieldDataType);
//            match = synonymsList != null && synonymsList.contains(upperCaseTypName);
//        }
        return false;
    }


    @Nullable
    String tryExtractDateOrTimeLiteralValue(MySqlType dataType, String defaultValue) {
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

    final boolean equalsCurrentTimestamp(FieldMeta<?> fieldMeta, String defaultValue) {
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
