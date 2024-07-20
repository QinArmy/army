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
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.type.SqlRecord;
import io.army.util._StringUtils;

import io.army.lang.Nullable;
import java.math.BigDecimal;
import java.time.*;
import java.util.BitSet;

/**
 * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Postgre Data Types</a>
 */
public enum PostgreType implements SQLType {


    /**
     * <p>
     * <ul>
     *     <li>{@link MappingType#beforeBind(DataType, MappingEnv, Object)} must return {@link Boolean}</li>
     *     <li>{@link MappingType#afterGet(DataType, MappingEnv, Object)} nonNull parameter must be {@link Boolean}</li>
     * </ul>
     */
    BOOLEAN("BOOLEAN", ArmyType.BOOLEAN, Boolean.class),

    SMALLINT("SMALLINT", ArmyType.SMALLINT, Short.class),
    INTEGER("INTEGER", ArmyType.INTEGER, Integer.class),

    BIGINT("BIGINT", ArmyType.BIGINT, Long.class),

    DECIMAL("DECIMAL", ArmyType.DECIMAL, BigDecimal.class),

    REAL("REAL", ArmyType.FLOAT, Float.class),
    FLOAT8("FLOAT8", ArmyType.DOUBLE, Double.class),

    BIT("BIT", ArmyType.BIT, BitSet.class),
    VARBIT("VARBIT", ArmyType.VARBIT, BitSet.class),

    TIME("TIME", ArmyType.TIME, LocalTime.class),
    DATE("DATE", ArmyType.DATE, LocalDate.class),
    TIMESTAMP("TIMESTAMP", ArmyType.TIMESTAMP, LocalDateTime.class),
    TIMETZ("TIMETZ", ArmyType.TIME_WITH_TIMEZONE, OffsetTime.class),
    TIMESTAMPTZ("TIMESTAMPTZ", ArmyType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.class),

    BYTEA("BYTEA", ArmyType.MEDIUMBLOB, byte[].class),

    CHAR("CHAR", ArmyType.CHAR, String.class),

    /**
     * <p>As of postgre 16 .
     *
     * @see <a href="https://www.postgresql.org/docs/current/datatype-character.html">bpchar</a>
     */
    BPCHAR("BPCHAR", ArmyType.CHAR, String.class),
    VARCHAR("VARCHAR", ArmyType.VARCHAR, String.class),
    MONEY("MONEY", ArmyType.DIALECT_TYPE, String.class),

    TEXT("TEXT", ArmyType.MEDIUMTEXT, String.class),
    PG_LSN("PG_LSN", ArmyType.DIALECT_TYPE, String.class),
    PG_SNAPSHOT("PG_SNAPSHOT", ArmyType.DIALECT_TYPE, String.class),
    TSVECTOR("TSVECTOR", ArmyType.DIALECT_TYPE, String.class),
    TSQUERY("TSQUERY", ArmyType.DIALECT_TYPE, String.class),

    INTERVAL("INTERVAL", ArmyType.INTERVAL, String.class),
    UUID("UUID", ArmyType.DIALECT_TYPE, java.util.UUID.class),
    XML("XML", ArmyType.XML, String.class),
    POINT("POINT", ArmyType.DIALECT_TYPE, String.class), // not standard point
    CIRCLE("CIRCLE", ArmyType.DIALECT_TYPE, String.class),
    PATH("PATH", ArmyType.DIALECT_TYPE, String.class),

    // below Geometries use ResultRow.get
    BOX("BOX", ArmyType.DIALECT_TYPE, String.class),
    LINE("LINE", ArmyType.DIALECT_TYPE, String.class),
    LSEG("LSEG", ArmyType.DIALECT_TYPE, String.class),
    POLYGON("POLYGON", ArmyType.DIALECT_TYPE, String.class),

    JSON("JSON", ArmyType.JSON, String.class),
    JSONB("JSONB", ArmyType.JSONB, String.class),
    JSONPATH("JSONPATH", ArmyType.DIALECT_TYPE, String.class),
    MACADDR("MACADDR", ArmyType.DIALECT_TYPE, String.class),
    MACADDR8("MACADDR8", ArmyType.DIALECT_TYPE, String.class),
    INET("INET", ArmyType.DIALECT_TYPE, String.class),
    CIDR("CIDR", ArmyType.DIALECT_TYPE, String.class),

    INT4RANGE("INT4RANGE", ArmyType.DIALECT_TYPE, String.class),
    INT8RANGE("INT8RANGE", ArmyType.DIALECT_TYPE, String.class),
    NUMRANGE("NUMRANGE", ArmyType.DIALECT_TYPE, String.class),
    TSRANGE("TSRANGE", ArmyType.DIALECT_TYPE, String.class),

    TSTZRANGE("TSTZRANGE", ArmyType.DIALECT_TYPE, String.class),

    DATERANGE("DATERANGE", ArmyType.DIALECT_TYPE, String.class),

    INT4MULTIRANGE("INT4MULTIRANGE", ArmyType.DIALECT_TYPE, String.class),
    INT8MULTIRANGE("INT8MULTIRANGE", ArmyType.DIALECT_TYPE, String.class),
    NUMMULTIRANGE("NUMMULTIRANGE", ArmyType.DIALECT_TYPE, String.class),
    DATEMULTIRANGE("DATEMULTIRANGE", ArmyType.DIALECT_TYPE, String.class),
    TSMULTIRANGE("TSMULTIRANGE", ArmyType.DIALECT_TYPE, String.class),
    TSTZMULTIRANGE("TSTZMULTIRANGE", ArmyType.DIALECT_TYPE, String.class),


    RECORD("RECORD", ArmyType.DIALECT_TYPE, SqlRecord.class),

    REF_CURSOR("REF_CURSOR", ArmyType.REF_CURSOR, String.class),

    ACLITEM("ACLITEM", ArmyType.DIALECT_TYPE, String.class),

    ACLITEM_ARRAY(ACLITEM),
    BOOLEAN_ARRAY(BOOLEAN),

    PG_LSN_ARRAY(PG_LSN),
    PG_SNAPSHOT_ARRAY(PG_SNAPSHOT),
    SMALLINT_ARRAY(SMALLINT),
    INTEGER_ARRAY(INTEGER),
    BIGINT_ARRAY(BIGINT),
    DECIMAL_ARRAY(DECIMAL),
    REAL_ARRAY(REAL),
    FLOAT8_ARRAY(FLOAT8),
    MONEY_ARRAY(MONEY),

    TIME_ARRAY(TIME),
    DATE_ARRAY(DATE),
    TIMESTAMP_ARRAY(TIMESTAMP),
    TIMETZ_ARRAY(TIMETZ),

    TIMESTAMPTZ_ARRAY(TIMESTAMPTZ),
    INTERVAL_ARRAY(INTERVAL),

    BYTEA_ARRAY(BYTEA),

    CHAR_ARRAY(CHAR),
    VARCHAR_ARRAY(VARCHAR),
    TEXT_ARRAY(TEXT),
    BIT_ARRAY(BIT),

    VARBIT_ARRAY(VARBIT),
    XML_ARRAY(XML),
    JSON_ARRAY(JSON),
    JSONB_ARRAY(JSONB),
    JSONPATH_ARRAY(JSONPATH),

    TSVECTOR_ARRAY(TSVECTOR),
    TSQUERY_ARRAY(TSQUERY),

    POINT_ARRAY(POINT),
    LINE_ARRAY(LINE),
    LSEG_ARRAY(LSEG),
    BOX_ARRAY(BOX),

    PATH_ARRAY(PATH),
    POLYGON_ARRAY(POLYGON),
    CIRCLE_ARRAY(CIRCLE),

    UUID_ARRAY(UUID),

    CIDR_ARRAY(CIDR),
    INET_ARRAY(INET),
    MACADDR_ARRAY(MACADDR),
    MACADDR8_ARRAY(MACADDR8),

    INT4RANGE_ARRAY(INT4RANGE),
    INT4MULTIRANGE_ARRAY(INT4MULTIRANGE),

    INT8RANGE_ARRAY(INT8RANGE),
    INT8MULTIRANGE_ARRAY(INT8MULTIRANGE),

    TSRANGE_ARRAY(TSRANGE),

    TSMULTIRANGE_ARRAY(TSMULTIRANGE),

    TSTZRANGE_ARRAY(TSTZRANGE),

    TSTZMULTIRANGE_ARRAY(TSTZMULTIRANGE),
    DATERANGE_ARRAY(DATERANGE),

    DATEMULTIRANGE_ARRAY(DATEMULTIRANGE),

    NUMRANGE_ARRAY(NUMRANGE),
    NUMMULTIRANGE_ARRAY(NUMMULTIRANGE),

    RECORD_ARRAY(RECORD),

    UNKNOWN("UNKNOWN", ArmyType.UNKNOWN, Object.class);

    private final String typeName;

    private final ArmyType armyType;

    private final Class<?> javaType;

    private final PostgreType elementType;

    PostgreType(String typeName, ArmyType armyType, Class<?> javaType) {
        assert armyType != ArmyType.ARRAY;

        this.typeName = typeName;
        this.armyType = armyType;
        this.javaType = javaType;
        this.elementType = null;
    }

    PostgreType(PostgreType elementType) {
        this.typeName = elementType.typeName + "[]";
        this.armyType = ArmyType.ARRAY;
        this.javaType = Object.class;
        this.elementType = elementType;
    }


    @Override
    public final Database database() {
        return Database.PostgreSQL;
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
    public final Class<?> firstJavaType() {
        return this.javaType;
    }

    @Nullable
    @Override
    public final Class<?> secondJavaType() {
        // postgre client packet size always less than 2G
        return null;
    }

    @Nullable
    @Override
    public final SQLType elementType() {
        return this.elementType;
    }


    @Override
    public final boolean isUnknown() {
        return this == UNKNOWN;
    }


    @Override
    public final boolean isArray() {
        return this.armyType == ArmyType.ARRAY;
    }

    @Override
    public final TypeDef._TypeDefCollateClause parens(final long precision) {
        final TypeDef._TypeDefCollateClause typeDef;
        switch (this) {
            case CHAR:
            case BPCHAR:
            case VARCHAR:
                typeDef = _SQLConsultant.precision(this, true, precision, 10485760);
                break;
            case DECIMAL:
                typeDef = _SQLConsultant.precision(this, false, precision, 131072);
                break;
            case BIT:
            case VARBIT:
                typeDef = _SQLConsultant.precision(this, false, precision, Integer.MAX_VALUE);
                break;
            case TIME:
            case TIMETZ:
            case TIMESTAMP:
            case TIMESTAMPTZ:
                typeDef = _SQLConsultant.precision(this, false, precision, 6);
                break;
            default:
                throw _SQLConsultant.dontSupportPrecision(this);
        }
        return typeDef;
    }

    @Override
    public final TypeDef parens(final int precision, final int scale) {
        final TypeDef typeDef;
        if (this == PostgreType.DECIMAL) {
            typeDef = _SQLConsultant.precisionAndScale(this, precision, scale, 131072 + 16383, 16383);
        } else {
            throw _SQLConsultant.dontSupportPrecisionAndScale(this);
        }
        return typeDef;
    }


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


    //            case BOOLEAN:
//            case INTEGER:
//            case BIGINT:
//            case DECIMAL:
//            case FLOAT8:
//            case REAL:
//            case TIME:
//            case DATE:
//            case TIMETZ:
//            case TIMESTAMP:
//            case TIMESTAMPTZ:
//            case SMALLINT:
//            case CHAR:
//            case TEXT:
//            case JSON:
//
//            case BYTEA:
//            case BIT:
//            case VARBIT:
//
//            case BOX:
//            case XML:
//            case CIDR:
//            case INET:
//            case LINE:
//            case PATH:
//            case UUID:
//            case JSONB:
//            case MONEY:
//            case POINT:
//            case CIRCLE:
//
//            case MACADDR:
//            case POLYGON:
//            case TSQUERY:
//            case TSRANGE:
//            case VARCHAR:
//            case INTERVAL:
//            case MACADDR8:
//            case NUMRANGE:
//
//            case TSVECTOR:
//
//            case DATERANGE:
//            case BOX_ARRAY:
//            case INT4RANGE:
//            case INT8RANGE:
//            case OID_ARRAY:
//            case LINE_SEGMENT:
//
//            case TSTZRANGE:
//           case INT4MULTIRANGE:
//            case INT8MULTIRANGE:
//            case NUMMULTIRANGE:
//            case DATEMULTIRANGE:
//            case TSMULTIRANGE:
//            case TSTZMULTIRANGE:
//
//            case PG_SNAPSHOT:
//            case PG_LSN:
//            case LSEG:
//            case ACLITEM:
//
//            case BIT_ARRAY:
//            case XML_ARRAY:
//            case CHAR_ARRAY:
//            case CIDR_ARRAY:
//            case DATE_ARRAY:
//            case INET_ARRAY:
//            case JSON_ARRAY:
//            case LINE_ARRAY:
//            case PATH_ARRAY:
//            case REAL_ARRAY:
//            case REF_CURSOR:
//            case TEXT_ARRAY:
//            case TIME_ARRAY:
//            case UUID_ARRAY:
//            case BYTEA_ARRAY:
//            case JSONB_ARRAY:
//            case MONEY_ARRAY:
//            case POINT_ARRAY:
//            case BIGINT_ARRAY:
//            case DOUBLE_ARRAY:
//            case TIMETZ_ARRAY:
//            case VARBIT_ARRAY:
//            case BOOLEAN_ARRAY:
//            case CIRCLES_ARRAY:
//            case DECIMAL_ARRAY:
//            case INTEGER_ARRAY:
//            case MACADDR_ARRAY:
//            case POLYGON_ARRAY:
//            case TSQUERY_ARRAY:
//            case TSRANGE_ARRAY:
//            case VARCHAR_ARRAY:
//            case INTERVAL_ARRAY:
//            case MACADDR8_ARRAY:
//            case NUMRANGE_ARRAY:
//            case SMALLINT_ARRAY:
//            case TSVECTOR_ARRAY:
//            case DATERANGE_ARRAY:
//            case INT4RANGE_ARRAY:
//            case INT8RANGE_ARRAY:
//            case TIMESTAMP_ARRAY:
//            case TSTZRANGE_ARRAY:
//            case TIMESTAMPTZ_ARRAY:

//    case INT4MULTIRANGE_ARRAY:
//            case INT8MULTIRANGE_ARRAY:
//            case NUMMULTIRANGE_ARRAY:
//            case DATEMULTIRANGE_ARRAY:
//            case TSMULTIRANGE_ARRAY:
//            case TSTZMULTIRANGE_ARRAY:
//                break;
//            default:
//                throw _Exceptions.unexpectedEnum((PostgreType) type);


}
