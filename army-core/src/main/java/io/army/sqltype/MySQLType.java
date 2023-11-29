package io.army.sqltype;

import io.army.dialect.Database;
import io.army.type.BlobPath;
import io.army.type.TextPath;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;

/**
 * <p>This enum representing MySQL build-in data type
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/data-types.html">MySQL Data Types</a>
 */
public enum MySQLType implements SqlType {

    NULL("NULL", ArmyType.NULL, Object.class),
    BOOLEAN("BOOLEAN", ArmyType.BOOLEAN, Boolean.class),

    TINYINT("TINYINT", ArmyType.TINYINT, Byte.class),
    TINYINT_UNSIGNED("TINYINT UNSIGNED", ArmyType.TINYINT_UNSIGNED, Short.class),
    SMALLINT("SMALLINT", ArmyType.SMALLINT, Short.class),
    SMALLINT_UNSIGNED("SMALLINT UNSIGNED", ArmyType.SMALLINT_UNSIGNED, Integer.class),

    MEDIUMINT("MEDIUMINT", ArmyType.BOOLEAN, Integer.class),
    MEDIUMINT_UNSIGNED("MEDIUMINT UNSIGNED", ArmyType.MEDIUMINT_UNSIGNED, Integer.class),
    INT("INT", ArmyType.INTEGER, Integer.class),

    INT_UNSIGNED("INT UNSIGNED", ArmyType.INTEGER_UNSIGNED, Long.class),
    BIGINT("BIGINT", ArmyType.BIGINT, Long.class),
    BIGINT_UNSIGNED("BIGINT UNSIGNED", ArmyType.BIGINT_UNSIGNED, BigInteger.class),

    DECIMAL("DECIMAL", ArmyType.DECIMAL, BigDecimal.class),
    DECIMAL_UNSIGNED("DECIMAL UNSIGNED", ArmyType.DECIMAL_UNSIGNED, BigDecimal.class),

    FLOAT("FLOAT", ArmyType.FLOAT, Float.class),
    DOUBLE("DOUBLE", ArmyType.DOUBLE, Double.class),

    TIME("TIME", ArmyType.TIME, LocalTime.class),
    DATE("DATE", ArmyType.DATE, LocalDate.class),
    YEAR("YEAR", ArmyType.YEAR, Year.class),
    DATETIME("DATETIME", ArmyType.TIMESTAMP, LocalDateTime.class),

    CHAR("CHAR", ArmyType.CHAR, String.class),
    VARCHAR("VARCHAR", ArmyType.VARCHAR, String.class),

    TINYTEXT("TINYTEXT", ArmyType.TINYTEXT, String.class),
    TEXT("TEXT", ArmyType.TEXT, String.class),
    MEDIUMTEXT("MEDIUMTEXT", ArmyType.MEDIUMTEXT, String.class),
    LONGTEXT("LONGTEXT", ArmyType.LONGTEXT, String.class),

    BINARY("BINARY", ArmyType.BINARY, byte[].class),
    VARBINARY("VARBINARY", ArmyType.VARBINARY, byte[].class),

    TINYBLOB("TINYBLOB", ArmyType.TINYBLOB, byte[].class),
    BLOB("BLOB", ArmyType.BLOB, byte[].class),
    MEDIUMBLOB("MEDIUMBLOB", ArmyType.MEDIUMBLOB, byte[].class),
    LONGBLOB("LONGBLOB", ArmyType.LONGBLOB, byte[].class),

    BIT("BIT", ArmyType.BIT, Long.class),
    ENUM("ENUM", ArmyType.ENUM, String.class),
    SET("SET", ArmyType.DIALECT_TYPE, String.class),
    JSON("JSON", ArmyType.JSON, String.class),


    // https://dev.mysql.com/doc/refman/8.0/en/gis-geometry-class-hierarchy.html
    POINT("POINT", ArmyType.GEOMETRY, byte[].class),
    LINESTRING("LINESTRING", ArmyType.GEOMETRY, byte[].class),
    GEOMETRY("GEOMETRY", ArmyType.GEOMETRY, byte[].class),
    POLYGON("POLYGON", ArmyType.GEOMETRY, byte[].class),
    MULTIPOINT("MULTIPOINT", ArmyType.GEOMETRY, byte[].class),
    MULTIPOLYGON("MULTIPOLYGON", ArmyType.GEOMETRY, byte[].class),
    MULTILINESTRING("MULTILINESTRING", ArmyType.GEOMETRY, byte[].class),

    GEOMETRYCOLLECTION("GEOMETRYCOLLECTION", ArmyType.GEOMETRY, byte[].class),


    UNKNOWN("UNKNOWN", ArmyType.UNKNOWN, Object.class);


    private final String typeName;

    private final ArmyType armyType;

    private final Class<?> javaType;

    MySQLType(String typeName, ArmyType armyType, Class<?> javaType) {

        this.typeName = typeName;
        this.armyType = armyType;
        this.javaType = javaType;
    }


    @Override
    public final Database database() {
        return Database.MySQL;
    }

    @Override
    public final ArmyType armyType() {
        return this.armyType;
    }

    @Override
    public final Class<?> firstJavaType() {
        return this.javaType;
    }

    @Nullable
    @Override
    public final Class<?> secondJavaType() {
        final Class<?> javaType;
        switch (this) {
            case TIME:
                javaType = Duration.class;
                break;
            case LONGBLOB:
            case GEOMETRY:
                javaType = BlobPath.class;
                break;
            case LONGTEXT:
                javaType = TextPath.class;
                break;
            default:
                javaType = null;
        }
        return javaType;
    }

    @Nullable
    @Override
    public final SqlType elementType() {
        // MySQL don't support array
        return null;
    }


    @Override
    public final String typeName() {
        return this.typeName;
    }

    @Override
    public final boolean isUnknown() {
        return this == UNKNOWN;
    }

    @Override
    public final boolean isArray() {
        // false ,MySQL don't support array
        return false;
    }


    @Override
    public final boolean isNoPrecision() {
        final boolean match;
        switch (this) {
            case VARCHAR:
            case VARBINARY:
                match = false;
                break;
            default:
                match = true;
        }
        return match;
    }

    @Override
    public final boolean isSupportPrecision() {
        final boolean match;
        switch (this) {
            case VARCHAR:
            case VARBINARY:
            case DECIMAL:
            case DECIMAL_UNSIGNED:
            case TIME:
            case DATETIME:
            case TEXT:
            case BLOB:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }

    @Override
    public final boolean isSupportPrecisionScale() {
        final boolean match;
        switch (this) {
            case DECIMAL:
            case DECIMAL_UNSIGNED:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }

    @Override
    public final boolean isSupportCharset() {
        final boolean match;
        switch (this) {
            case VARCHAR:
            case CHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }

    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


}
