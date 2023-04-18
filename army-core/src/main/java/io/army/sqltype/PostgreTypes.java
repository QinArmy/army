package io.army.sqltype;

import io.army.dialect.Database;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;

/**
 * @see <a href="https://www.postgresql.org/docs/11/datatype.html">Postgre Data Types</a>
 */
public enum PostgreTypes implements SqlType {


    /**
     * <p>
     *     <ul>
     *         <li>{@link MappingType#beforeBind(SqlType, MappingEnv, Object)} must return {@link Boolean}</li>
     *         <li>{@link MappingType#afterGet(SqlType, MappingEnv, Object)} nonNull parameter must be {@link Boolean}</li>
     *     </ul>
     * </p>
     */
    BOOLEAN,

    SMALLINT,
    INTEGER,
    BIGINT,
    DECIMAL,

    REAL,
    DOUBLE,

    BIT,
    VARBIT,

    TIME,
    DATE,
    TIMESTAMP,
    TIMETZ,
    TIMESTAMPTZ,

    BYTEA,

    CHAR,
    VARCHAR,
    MONEY,
    TEXT,

    TSVECTOR,
    TSQUERY,

    INTERVAL,
    UUID,
    XML,
    POINT,
    CIRCLES,
    @Deprecated
    LINE_SEGMENT,
    PATH,

    // below Geometries use ResultRow.get
    BOX,
    LINE,
    LSEG,
    POLYGON,

    JSON,
    JSONB,
    MACADDR,
    MACADDR8,
    INET,
    CIDR,

    INT4RANGE,
    INT8RANGE,
    NUMRANGE,
    TSRANGE,
    TSTZRANGE,
    DATERANGE,

    REF_CURSOR,


    BOOLEAN_ARRAY,


    SMALLINT_ARRAY,
    INTEGER_ARRAY,
    BIGINT_ARRAY,
    DECIMAL_ARRAY,

    OID_ARRAY,
    REAL_ARRAY,
    DOUBLE_ARRAY,
    MONEY_ARRAY,

    TIME_ARRAY,
    DATE_ARRAY,
    TIMESTAMP_ARRAY,
    TIMETZ_ARRAY,

    TIMESTAMPTZ_ARRAY,
    INTERVAL_ARRAY,


    BYTEA_ARRAY,


    CHAR_ARRAY,
    VARCHAR_ARRAY,
    TEXT_ARRAY,
    BIT_ARRAY,

    VARBIT_ARRAY,
    XML_ARRAY,
    JSON_ARRAY,
    JSONB_ARRAY,

    TSVECTOR_ARRAY,
    TSQUERY_ARRAY,


    POINT_ARRAY,
    LINE_ARRAY,
    LINE_SEGMENT_ARRAY,
    BOX_ARRAY,

    PATH_ARRAY,
    POLYGON_ARRAY,
    CIRCLES_ARRAY,


    UUID_ARRAY,


    CIDR_ARRAY,
    INET_ARRAY,
    MACADDR_ARRAY,
    MACADDR8_ARRAY,

    INT4RANGE_ARRAY,
    TSRANGE_ARRAY,
    TSTZRANGE_ARRAY,
    DATERANGE_ARRAY,
    INT8RANGE_ARRAY,
    NUMRANGE_ARRAY,

    ;


    @Override
    public final Database database() {
        return Database.PostgreSQL;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", PostgreTypes.class.getName(), this.name());
    }


}