package io.army.sqltype;

import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.util._StringUtils;

/**
 * @see <a href="https://www.postgresql.org/docs/11/datatype.html">Postgre Data Types</a>
 */
public enum PostgreSqlType implements SqlType {


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
    NO_CAST_INTEGER,
    BIGINT,
    DECIMAL,

    REAL,
    FLOAT8,

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
    PG_LSN,
    PG_SNAPSHOT,


    NO_CAST_TEXT,

    TSVECTOR,
    TSQUERY,

    INTERVAL,
    UUID,
    XML,
    POINT,
    CIRCLE,
    PATH,

    // below Geometries use ResultRow.get
    BOX,
    LINE,
    LSEG,
    POLYGON,

    JSON,
    JSONB,
    JSONPATH,
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

    INT4MULTIRANGE,
    INT8MULTIRANGE,
    NUMMULTIRANGE,
    DATEMULTIRANGE,
    TSMULTIRANGE,
    TSTZMULTIRANGE,

    REF_CURSOR,

    ACLITEM,

    ACLITEM_ARRAY,
    BOOLEAN_ARRAY,

    PG_LSN_ARRAY,
    PG_SNAPSHOT_ARRAY,
    SMALLINT_ARRAY,
    INTEGER_ARRAY,
    BIGINT_ARRAY,
    DECIMAL_ARRAY,
    REAL_ARRAY,
    FLOAT8_ARRAY,
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
    JSONPATH_ARRAY,

    TSVECTOR_ARRAY,
    TSQUERY_ARRAY,


    POINT_ARRAY,
    LINE_ARRAY,
    LSEG_ARRAY,
    BOX_ARRAY,

    PATH_ARRAY,
    POLYGON_ARRAY,
    CIRCLE_ARRAY,

    UUID_ARRAY,

    CIDR_ARRAY,
    INET_ARRAY,
    MACADDR_ARRAY,
    MACADDR8_ARRAY,

    INT4RANGE_ARRAY,
    INT4MULTIRANGE_ARRAY,

    INT8RANGE_ARRAY,
    INT8MULTIRANGE_ARRAY,

    TSRANGE_ARRAY,
    TSMULTIRANGE_ARRAY,

    TSTZRANGE_ARRAY,

    TSTZMULTIRANGE_ARRAY,
    DATERANGE_ARRAY,

    DATEMULTIRANGE_ARRAY,

    NUMRANGE_ARRAY,
    NUMMULTIRANGE_ARRAY,

    USER_DEFINED,

    USER_DEFINED_ARRAY,

    UNKNOWN;


    @Override
    public final Database database() {
        return Database.PostgreSQL;
    }

    @Override
    public final boolean isUserDefined() {
        return this == USER_DEFINED
                || this == USER_DEFINED_ARRAY;
    }

    @Override
    public final boolean isUnknown() {
        return this == UNKNOWN;
    }


    @Override
    public final boolean isArray() {
        return this.name().contains(_Constant.UNDERSCORE_ARRAY);
    }


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


}
