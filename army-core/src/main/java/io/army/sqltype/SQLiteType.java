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

import io.army.criteria.TypeDef;
import io.army.criteria.impl._SQLConsultant;
import io.army.dialect.Database;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.*;


/**
 * @see <a href="https://sqlite.org/datatype3.html">Datatypes In SQLite</a>
 * @see <a href="https://sqlite.org/datatypes.html">Datatypes In SQLite Version 2</a>
 */
public enum SQLiteType implements SQLType {

    NULL("NULL", ArmyType.NULL, Object.class),
    BOOLEAN("BOOLEAN", ArmyType.BOOLEAN, Boolean.class), // alias of INTEGER class

    TINYINT("TINYINT", ArmyType.TINYINT, Byte.class),  // alias of INTEGER class
    SMALLINT("SMALLINT", ArmyType.SMALLINT, Short.class), // alias of INTEGER class
    MEDIUMINT("MEDIUMINT", ArmyType.MEDIUMINT, Integer.class), // alias of INTEGER class
    INTEGER("INTEGER", ArmyType.INTEGER, Integer.class), // alias of INTEGER class

    BIGINT("BIGINT", ArmyType.BIGINT, Long.class), // alias of INTEGER class

    UNSIGNED_BIG_INT("UNSIGNED BIG INT", ArmyType.BIGINT_UNSIGNED, Long.class), // alias of INTEGER class

    DECIMAL("DECIMAL", ArmyType.DECIMAL, BigDecimal.class), // alias of TEXT class


    FLOAT("FLOAT", ArmyType.FLOAT, Float.class), // alias of REAL class

    DOUBLE("DOUBLE", ArmyType.DOUBLE, Double.class), // alias of REAL class

    TIME("TIME", ArmyType.TIME, LocalTime.class), // alias of TEXT class
    TIME_WITH_TIMEZONE("TIME WITH TIMEZONE", ArmyType.TIME_WITH_TIMEZONE, OffsetTime.class), // alias of TEXT class
    TIMESTAMP("TIMESTAMP", ArmyType.TIMESTAMP, LocalDateTime.class), // alias of TEXT class
    TIMESTAMP_WITH_TIMEZONE("TIMESTAMP WITH TIMEZONE", ArmyType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.class), // alias of TEXT class

    DATE("DATE", ArmyType.DATE, LocalDate.class), // alias of TEXT class

    YEAR("YEAR", ArmyType.YEAR, Year.class), // alias of INTEGER class
    YEAR_MONTH("YEAR MONTH", ArmyType.YEAR, YearMonth.class), // alias of TEXT class
    MONTH_DAY("MONTH DAY", ArmyType.YEAR, MonthDay.class), // alias of TEXT class

    DURATION("DURATION", ArmyType.DURATION, Duration.class), // alias of TEXT class

    PERIOD("PERIOD", ArmyType.PERIOD, Period.class), // alias of TEXT class

    CHAR("CHAR", ArmyType.CHAR, String.class), // alias of TEXT class
    VARCHAR("VARCHAR", ArmyType.VARCHAR, String.class), // alias of TEXT class
    TEXT("TEXT", ArmyType.TEXT, String.class),

    BINARY("BINARY", ArmyType.BINARY, byte[].class), // alias of BLOB class
    VARBINARY("VARBINARY", ArmyType.VARBINARY, byte[].class), // alias of BLOB class
    BLOB("BLOB", ArmyType.BLOB, byte[].class), // alias of BLOB class

    BIT("BIT", ArmyType.BIT, Long.class), // alias of INTEGER class
    JSON("JSON", ArmyType.JSON, String.class), // alias of TEXT class

    DYNAMIC("", ArmyType.DIALECT_TYPE, Object.class), // alias of empty class

    UNKNOWN("UNKNOWN", ArmyType.UNKNOWN, Object.class);


    private final String typeName;

    private final ArmyType armyType;

    private final Class<?> javaType;

    SQLiteType(String typeName, ArmyType armyType, Class<?> javaType) {
        this.typeName = typeName;
        this.armyType = armyType;
        this.javaType = javaType;
    }


    @Override
    public final String typeName() {
        return this.typeName;
    }

    @Override
    public final ArmyType armyType() {
        return this.armyType;
    }

    @Override
    public final boolean isUnknown() {
        return this == UNKNOWN;
    }

    @Override
    public final Database database() {
        return Database.SQLite;
    }


    @Override
    public final boolean isArray() {
        // SQLite don't support array operation
        return false;
    }

    @Override
    public final TypeDef parens(final long precision) {
        final _TypeDefCharacterSetSpec typeDef;
        switch (this) {
            case VARCHAR:
            case TEXT:
                typeDef = _SQLConsultant.precision(this, true, precision, 0xFFFFL);
                break;
            case DECIMAL:
                typeDef = _SQLConsultant.precision(this, false, precision, 65L);
                break;
            case TIME:
            case TIME_WITH_TIMEZONE:
            case TIMESTAMP:
            case TIMESTAMP_WITH_TIMEZONE:
                typeDef = _SQLConsultant.precision(this, true, precision, 6L);
                break;
            case VARBINARY:
            case BLOB:
                typeDef = _SQLConsultant.precision(this, false, precision, 0xFFFFL);
                break;
            case BIT:
                typeDef = _SQLConsultant.precision(this, false, precision, 64L);
                break;
            default:
                throw _SQLConsultant.dontSupportPrecision(this);
        }
        return typeDef;
    }

    @Override
    public final TypeDef parens(final int precision, final int scale) {
        if (this != SQLiteType.DECIMAL) {
            throw _SQLConsultant.dontSupportPrecisionAndScale(this);
        }
        return _SQLConsultant.precisionAndScale(this, precision, scale, 65, 30);
    }


    @Override
    public final Class<?> firstJavaType() {
        return this.javaType;
    }

    @Nullable
    @Override
    public final Class<?> secondJavaType() {
        // no second java type
        return null;
    }

    @Nullable
    @Override
    public final SQLType elementType() {
        // SQLite don't support array operation
        return null;
    }

//              case BOOLEAN:
//
//            case TINYINT:
//            case SMALLINT:
//            case MEDIUMINT:
//            case INTEGER:
//            case BIGINT:
//
//            case DOUBLE:
//            case DECIMAL:
//
//
//            case BIT:
//
//            case VARCHAR:
//            case TEXT:
//            case JSON:
//
//            case VARBINARY:
//            case BLOB:
//
//            case TIME:
//            case TIME_WITH_TIMEZONE:
//            case DATE:
//            case YEAR:
//
//            case TIMESTAMP:
//            case TIMESTAMP_WITH_TIMEZONE:
//
//            case MONTH_DAY:
//            case YEAR_MONTH:
//            case PERIOD:
//            case DURATION:
//
//            case DYNAMIC:
//            case NULL:
//
//            case UNKNOWN:


}
