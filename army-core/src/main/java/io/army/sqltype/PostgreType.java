package io.army.sqltype;

import io.army.dialect.Database;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.*;
import java.util.BitSet;

/**
 * @see <a href="https://www.postgresql.org/docs/11/datatype.html">Postgre Data Types</a>
 */
public enum PostgreType implements SqlType {


    /**
     * <p>
     *     <ul>
     *         <li>{@link MappingType#beforeBind(DataType, MappingEnv, Object)} must return {@link Boolean}</li>
     *         <li>{@link MappingType#afterGet(DataType, MappingEnv, Object)} nonNull parameter must be {@link Boolean}</li>
     *     </ul>
     * </p>
     */
    BOOLEAN("BOOLEAN", ArmyType.BOOLEAN, Boolean.class),

    SMALLINT("SMALLINT", ArmyType.SMALLINT, Short.class),
    INTEGER("INTEGER", ArmyType.INTEGER, Integer.class),
    NO_CAST_INTEGER(INTEGER.typeName, INTEGER.armyType, INTEGER.javaType),

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

    BYTEA("BYTEA", ArmyType.BLOB, byte[].class),

    CHAR("CHAR", ArmyType.CHAR, String.class),
    VARCHAR("VARCHAR", ArmyType.VARCHAR, String.class),
    MONEY("MONEY", ArmyType.DIALECT_TYPE, String.class),

    TEXT("TEXT", ArmyType.MEDIUMTEXT, String.class),
    PG_LSN("PG_LSN", ArmyType.DIALECT_TYPE, String.class),
    PG_SNAPSHOT("PG_SNAPSHOT", ArmyType.DIALECT_TYPE, String.class),


    NO_CAST_TEXT(TEXT.typeName, TEXT.armyType, TEXT.javaType),

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
    public final SqlType elementType() {
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
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


}
