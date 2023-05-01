package io.army.sqltype;

import io.army.dialect.Database;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.util._ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.List;

/**
 * @see <a href="https://www.postgresql.org/docs/11/datatype.html">Postgre Data Types</a>
 */
public enum PgSqlType implements SqlType {


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
    LSEG_ARRAY,
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
    public final void sqlTypeName(final MappingType type, final StringBuilder builder) {
        switch (this) {
            case BOOLEAN:
            case SMALLINT:
            case INTEGER:
            case BIGINT:
            case DECIMAL:
            case DOUBLE:
            case REAL:
            case TIME:
            case DATE:
            case TIMESTAMP:
            case TIMETZ:
            case TIMESTAMPTZ:
            case CHAR:
            case VARCHAR:
            case TEXT:
            case JSON:
            case JSONB:
            case JSONPATH:
            case BYTEA:
            case BIT:
            case VARBIT:
            case XML:
            case CIDR:
            case INET:
            case LINE:
            case PATH:
            case UUID:
            case MONEY:
            case MACADDR8:
            case POINT:
            case BOX:
            case POLYGON:
            case CIRCLE:
            case TSQUERY:
            case TSVECTOR:
            case LSEG:
            case TSRANGE:
            case INTERVAL:
            case NUMRANGE:
            case DATERANGE:
            case INT4RANGE:
            case INT8RANGE:
            case TSTZRANGE:
            case MACADDR:
                builder.append(this.name());
                break;
            case NO_CAST_TEXT:
                builder.append(TEXT.name());
                break;
            case BOX_ARRAY:
            case OID_ARRAY:
            case BIT_ARRAY:
            case XML_ARRAY:
            case CHAR_ARRAY:
            case CIDR_ARRAY:
            case DATE_ARRAY:
            case INET_ARRAY:
            case JSON_ARRAY:
            case LINE_ARRAY:
            case PATH_ARRAY:
            case REAL_ARRAY:
            case REF_CURSOR:
            case TEXT_ARRAY:
            case TIME_ARRAY:
            case UUID_ARRAY:
            case BYTEA_ARRAY:
            case JSONB_ARRAY:
            case MONEY_ARRAY:
            case POINT_ARRAY:
            case BIGINT_ARRAY:
            case DOUBLE_ARRAY:
            case TIMETZ_ARRAY:
            case VARBIT_ARRAY:
            case BOOLEAN_ARRAY:
            case CIRCLES_ARRAY:
            case DECIMAL_ARRAY:
            case INTEGER_ARRAY:
            case MACADDR_ARRAY:
            case POLYGON_ARRAY:
            case TSQUERY_ARRAY:
            case TSRANGE_ARRAY:
            case VARCHAR_ARRAY:
            case INTERVAL_ARRAY:
            case MACADDR8_ARRAY:
            case NUMRANGE_ARRAY:
            case SMALLINT_ARRAY:
            case TSVECTOR_ARRAY:
            case DATERANGE_ARRAY:
            case INT4RANGE_ARRAY:
            case INT8RANGE_ARRAY:
            case TIMESTAMP_ARRAY:
            case TSTZRANGE_ARRAY:
            case TIMESTAMPTZ_ARRAY:
            case LSEG_ARRAY: {

                final int dimension;
                final Class<?> javaType;
                javaType = type.javaType();
                if (javaType == Object.class) {
                    throw _Exceptions.unknownArrayDimension(this, type);
                } else if (List.class.isAssignableFrom(javaType)) {
                    dimension = 1;
                } else if (javaType.isArray()) {
                    dimension = _ArrayUtils.dimensionOf(javaType);
                } else {
                    throw _Exceptions.javaTypeMethodNotArray(type);
                }
                String name;
                name = this.name();
                name = name.substring(0, name.indexOf("_ARRAY"));
                builder.append(name);

                for (int i = 0; i < dimension; i++) {
                    builder.append("[]");
                }
            }
            break;
            case UNKNOWN:
                throw _Exceptions.mapMethodError(type, PgSqlType.class);
            default:
                // no bug,never here
                throw _Exceptions.unexpectedEnum(this);
        }

    }


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


}
