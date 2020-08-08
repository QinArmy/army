package io.army.dialect.postgre;

import io.army.criteria.MetaException;
import io.army.dialect.DDLUtils;
import io.army.meta.FieldMeta;

import java.math.BigInteger;
import java.sql.JDBCType;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

abstract class Postgre11DDLUtils extends DDLUtils {

    static void assertSupportTimeType(FieldMeta<?, ?> fieldMeta) {
        if (fieldMeta.javaType() == java.util.Date.class) {
            throw new MetaException("not support %s java type.", fieldMeta);
        }
    }

    /**
     * @return a unmodifiable map
     */
    static Map<JDBCType, Function<FieldMeta<?, ?>, String>> createJdbcFunctionMap() {
        Map<JDBCType, Function<FieldMeta<?, ?>, String>> map;
        map = new EnumMap<>(JDBCType.class);

        map.put(JDBCType.BIT, Postgre11DDLUtils::bitFunction);
        map.put(JDBCType.TINYINT, Postgre11DDLUtils::tinyIntFunction);
        map.put(JDBCType.SMALLINT, Postgre11DDLUtils::smallIntFunction);
        map.put(JDBCType.INTEGER, Postgre11DDLUtils::integerFunction);

        map.put(JDBCType.BIGINT, Postgre11DDLUtils::bitIntFunction);
        map.put(JDBCType.FLOAT, Postgre11DDLUtils::floatFunction);
        map.put(JDBCType.DOUBLE, Postgre11DDLUtils::doubleFunction);
        map.put(JDBCType.DECIMAL, Postgre11DDLUtils::decimalFunction);

        map.put(JDBCType.CHAR, Postgre11DDLUtils::charFunction);
        map.put(JDBCType.VARCHAR, Postgre11DDLUtils::varcharFunction);
        map.put(JDBCType.LONGVARCHAR, Postgre11DDLUtils::longVarcharFunction);
        map.put(JDBCType.NCHAR, Postgre11DDLUtils::ncharFunction);

        map.put(JDBCType.NVARCHAR, Postgre11DDLUtils::nvarcharFunction);
        map.put(JDBCType.BINARY, Postgre11DDLUtils::binaryFunction);
        map.put(JDBCType.VARBINARY, Postgre11DDLUtils::varbinaryFunction);
        map.put(JDBCType.LONGVARBINARY, Postgre11DDLUtils::longVarbinaryFunction);

        map.put(JDBCType.BLOB, Postgre11DDLUtils::blobFunction);
        map.put(JDBCType.DATE, Postgre11DDLUtils::dateFunction);
        map.put(JDBCType.TIME, Postgre11DDLUtils::timeFunction);
        map.put(JDBCType.TIME_WITH_TIMEZONE, Postgre11DDLUtils::timeWithTimeZoneFunction);

        map.put(JDBCType.TIMESTAMP, Postgre11DDLUtils::timestampFunction);
        map.put(JDBCType.TIMESTAMP_WITH_TIMEZONE, Postgre11DDLUtils::timestampWithTimeZoneFunction);
        map.put(JDBCType.BOOLEAN, Postgre11DDLUtils::booleanFunction);

        return Collections.unmodifiableMap(map);
    }

    /*################################## blow private method ##################################*/



    /*################################## blow  JDBC function method ##################################*/

    private static String bitFunction(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 1;
        }
        return "BIT VARYING(" + precision + ")";
    }

    private static String tinyIntFunction(FieldMeta<?, ?> fieldMeta) {
        return smallIntFunction(fieldMeta);
    }

    private static String smallIntFunction(FieldMeta<?, ?> fieldMeta) {
        return "SMALLINT";
    }

    private static String integerFunction(FieldMeta<?, ?> fieldMeta) {
        return "INTEGER";
    }

    private static String bitIntFunction(FieldMeta<?, ?> fieldMeta) {
        return "BIGINT";
    }

    private static String floatFunction(FieldMeta<?, ?> fieldMeta) {
        return "REAL";
    }

    private static String doubleFunction(FieldMeta<?, ?> fieldMeta) {
        return "DOUBLE PRECISION";
    }

    private static String decimalFunction(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();

        if (precision == 0 || precision > 1000) {
            throw new MetaException("%s The value range of Postgre DECIMAL precision is [1,1000] .", fieldMeta);
        } else if (precision < 0) {
            if (fieldMeta.javaType() == BigInteger.class) {
                // double of Long
                precision = 38;
            } else {
                precision = 14;
            }
        }

        int scale = fieldMeta.scale();
        if (fieldMeta.javaType() == BigInteger.class) {
            scale = 0;
        } else if (scale > 1000) {
            throw new MetaException("%s, The value range of Postgre DECIMAL precision is [0,1000] .", fieldMeta);
        } else if (scale < 0) {
            scale = 2;
        }
        return "DECIMAL(" + precision + "," + scale + ")";
    }

    private static String charFunction(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 225;
        } else if (precision > 10485760) {
            // 10485760 is experiment value with postgre
            throw new MetaException("%s, The value range of Postgre CHAR precision is [1,10485760] .", fieldMeta);
        }
        return "CHAR(" + precision + ")";
    }

    private static String varcharFunction(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 225;
        } else if (precision > 10485760) {
            //10485760 is experiment value with postgre
            throw new MetaException("%s, The value range of Postgre VARCHAR precision is [1,10485760] .", fieldMeta);
        }
        return "VARCHAR(" + precision + ")";
    }

    private static String longVarcharFunction(FieldMeta<?, ?> fieldMeta) {
        return "TEXT";
    }

    private static String ncharFunction(FieldMeta<?, ?> fieldMeta) {
        return charFunction(fieldMeta);
    }

    private static String nvarcharFunction(FieldMeta<?, ?> fieldMeta) {
        return varcharFunction(fieldMeta);
    }

    private static String binaryFunction(FieldMeta<?, ?> fieldMeta) {
        return "BYTEA";
    }

    private static String longVarbinaryFunction(FieldMeta<?, ?> fieldMeta) {
        return binaryFunction(fieldMeta);
    }

    private static String blobFunction(FieldMeta<?, ?> fieldMeta) {
        return binaryFunction(fieldMeta);
    }

    private static String dateFunction(FieldMeta<?, ?> fieldMeta) {
        return "DATE";
    }

    private static String timeFunction(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 0;
        } else if (precision > 6) {
            throw new MetaException("%s, The value range of Postgre TIME WITHOUT TIME ZONE precision is [0,6] ."
                    , fieldMeta);
        }
        return "TIME(" + precision + ") WITHOUT TIME ZONE";
    }

    private static String timeWithTimeZoneFunction(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 0;
        } else if (precision > 6) {
            throw new MetaException("%s, The value range of Postgre TIME WITH TIME ZONE precision is [0,6] ."
                    , fieldMeta);
        }
        return "TIME(" + precision + ") WITH TIME ZONE";
    }

    private static String varbinaryFunction(FieldMeta<?, ?> fieldMeta) {
        return binaryFunction(fieldMeta);
    }

    private static String timestampFunction(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 0;
        } else if (precision > 6) {
            throw new MetaException("%s, The value range of Postgre TIMESTAMP WITHOUT TIME ZONE precision is [0,6] ."
                    , fieldMeta);
        }
        return "TIMESTAMP(" + precision + ") WITHOUT TIME ZONE";
    }

    private static String timestampWithTimeZoneFunction(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 0;
        } else if (precision > 6) {
            throw new MetaException("%s, The value range of Postgre TIMESTAMP WITH TIME ZONE precision is [0,6] ."
                    , fieldMeta);
        }
        return "TIMESTAMP(" + precision + ") WITH TIME ZONE";
    }

    private static String booleanFunction(FieldMeta<?, ?> fieldMeta) {
        return "BOOLEAN";
    }


}
