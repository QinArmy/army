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

package io.army.sqltype;

import io.army.ArmyException;
import io.army.dialect.Database;
import io.army.mapping.*;
import io.army.mapping.mysql.MySqlBitType;
import io.army.mapping.spatial.*;
import io.army.type.BlobPath;
import io.army.type.TextPath;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.function.Supplier;

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
    public final MappingType mapType(final Supplier<? extends ArmyException> errorHandler) {
        final MappingType type;
        switch (this) {
            case BOOLEAN:
                type = BooleanType.INSTANCE;
                break;
            case TINYINT:
                type = ByteType.INSTANCE;
                break;
            case SMALLINT:
                type = ShortType.INSTANCE;
                break;
            case MEDIUMINT:
                type = MediumIntType.INSTANCE;
                break;
            case INT:
                type = IntegerType.INSTANCE;
                break;
            case BIGINT:
                type = LongType.INSTANCE;
                break;
            case DECIMAL:
                type = BigDecimalType.INSTANCE;
                break;
            case DOUBLE:
                type = DoubleType.INSTANCE;
                break;
            case FLOAT:
                type = FloatType.INSTANCE;
                break;

            case TINYINT_UNSIGNED:
                type = UnsignedTinyIntType.INSTANCE;
                break;
            case SMALLINT_UNSIGNED:
                type = UnsignedSmallIntType.INSTANCE;
                break;
            case MEDIUMINT_UNSIGNED:
                type = UnsignedMediumIntType.INSTANCE;
                break;
            case INT_UNSIGNED:
                type = UnsignedSqlIntType.INSTANCE;
                break;
            case BIGINT_UNSIGNED:
                type = UnsignedBigintType.INSTANCE;
                break;
            case DECIMAL_UNSIGNED:
                type = UnsignedBigDecimalType.INSTANCE;
                break;

            case TIME:
                type = LocalTimeType.INSTANCE;
                break;
            case DATE:
                type = LocalDateType.INSTANCE;
                break;
            case DATETIME:
                type = LocalDateTimeType.INSTANCE;
                break;
            case YEAR:
                type = YearType.INSTANCE;
                break;

            case CHAR:
                type = SqlCharType.INSTANCE;
                break;
            case VARCHAR:
            case SET:
            case ENUM:

            case NULL:
            case UNKNOWN:
                type = StringType.INSTANCE;
                break;
            case TINYTEXT:
                type = TinyTextType.INSTANCE;
                break;
            case TEXT:
                type = TextType.INSTANCE;
                break;
            case MEDIUMTEXT:
                type = MediumTextType.INSTANCE;
                break;
            case LONGTEXT:
                type = LongText.STRING;
                break;

            case JSON:
                type = JsonType.TEXT;
                break;
            case BIT:
                type = MySqlBitType.INSTANCE;
                break;

            case BINARY:
                type = BinaryType.INSTANCE;
                break;
            case VARBINARY:
                type = VarBinaryType.INSTANCE;
                break;
            case TINYBLOB:
                type = TinyBlobType.INSTANCE;
                break;
            case BLOB:
                type = BlobType.INSTANCE;
                break;
            case MEDIUMBLOB:
                type = MediumBlobType.INSTANCE;
                break;
            case LONGBLOB:
                type = LongBlobType.BYTE_ARRAY;
                break;

            case GEOMETRY:
                type = GeometryType.BINARY;
                break;
            case POINT:
                type = PointType.BINARY;
                break;
            case LINESTRING:
                type = LineStringType.BINARY;
                break;
            case POLYGON:
                type = PolygonType.BINARY;
                break;
            case MULTIPOINT:
                type = MultiPointType.BINARY;
                break;
            case MULTIPOLYGON:
                type = MultiPolygonType.BINARY;
                break;
            case MULTILINESTRING:
                type = MultiLineStringType.BINARY;
                break;
            case GEOMETRYCOLLECTION:
                type = GeometryCollectionType.BINARY;
                break;
            default:
                throw errorHandler.get();
        }
        return type;
    }


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


//            case BOOLEAN:
//            case TINYINT:
//
//            case SMALLINT:
//            case MEDIUMINT:
//            case INT:
//            case BIGINT:
//            case DECIMAL:
//
//            case DOUBLE:
//            case FLOAT:
//
//            case TINYINT_UNSIGNED:
//            case SMALLINT_UNSIGNED:
//            case MEDIUMINT_UNSIGNED:
//            case INT_UNSIGNED:
//            case BIGINT_UNSIGNED:
//            case DECIMAL_UNSIGNED:
//
//            case TIME:
//            case DATE:
//            case DATETIME:
//            case YEAR:
//
//            case CHAR:
//            case VARCHAR:
//            case TINYTEXT:
//            case TEXT:
//            case MEDIUMTEXT:
//            case LONGTEXT:
//            case JSON:
//
//            case SET:
//            case ENUM:
//
//            case BIT:
//
//            case BINARY:
//            case VARBINARY:
//            case TINYBLOB:
//            case BLOB:
//            case MEDIUMBLOB:
//            case LONGBLOB:
//
//            case GEOMETRY:
//            case POINT:
//            case LINESTRING:
//            case POLYGON:
//            case MULTIPOINT:
//            case MULTIPOLYGON:
//            case MULTILINESTRING:
//            case GEOMETRYCOLLECTION:
//
//            case NULL:
//            case UNKNOWN:


}
