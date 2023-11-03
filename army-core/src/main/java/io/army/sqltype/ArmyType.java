package io.army.sqltype;

import io.army.session.Cursor;
import io.army.session.Option;
import io.army.session.record.DataRecord;
import io.army.session.record.ResultRecordMeta;
import io.army.session.record.ResultStates;

/**
 * <p>This enum is a implementation of {@link DataType} for the convenience that application get column type meta by ResultRecordMeta#getArmyType(int) .
 *
 * @see ResultRecordMeta#getArmyType(int)
 * @since 1.0
 */
public enum ArmyType implements DataType {

    /**
     * Identifies the generic SQL type {@code BOOLEAN}.
     */
    BOOLEAN,

    /**
     * Identifies the generic SQL type {@code BIT}, not boolean.
     * {@code BIT} are strings of 1's and 0's. They can be used to store or visualize bit masks.
     * {@code BIT} is similar to {@link #VARBIT}, except that must match fixed length.
     * <p>
     * {@link DataRecord#get(int, Class)} support following java type:
     * <ul>
     *     <li>{@link Integer}</li>
     *     <li>{@link Long}</li>
     *     <li>{@link String}</li>
     *     <li>{@link java.util.BitSet}</li>
     * </ul>
     * <br/>
     */
    BIT,

    /**
     * Identifies the generic SQL type {@code VARBIT}, not boolean.
     * {@code VARBIT} are strings of 1's and 0's. They can be used to store or visualize bit masks.
     * <p>
     * {@link DataRecord#get(int, Class)} support following java type:
     * <ul>
     *     <li>{@link Integer}</li>
     *     <li>{@link Long}</li>
     *     <li>{@link String}</li>
     *     <li>{@link java.util.BitSet}</li>
     * </ul>
     * <br/>
     */
    VARBIT,

    /**
     * Identifies the generic SQL type {@code TINYINT}, one byte integer number.
     */
    TINYINT,

    /**
     * Identifies the generic SQL type {@code SMALLINT}, two byte integer number.
     */
    SMALLINT,

    /**
     * Identifies the generic SQL type {@code MEDIUMINT}, three byte integer number.
     */
    MEDIUMINT,
    /**
     * Identifies the generic SQL type {@code INTEGER}, four byte integer number.
     */
    INTEGER,
    /**
     * Identifies the generic SQL type {@code BIGINT}, eight byte integer number.
     */
    BIGINT,

    /**
     * Identifies the generic SQL type {@code TINYINT_UNSIGNED}, one byte integer number.
     */
    TINYINT_UNSIGNED,

    /**
     * Identifies the generic SQL type {@code SMALLINT_UNSIGNED}, two byte integer number.
     */
    SMALLINT_UNSIGNED,

    /**
     * Identifies the generic SQL type {@code MEDIUMINT_UNSIGNED}, three byte integer number.
     */
    MEDIUMINT_UNSIGNED,
    /**
     * Identifies the generic SQL type {@code INTEGER_UNSIGNED}, four byte integer number.
     */
    INTEGER_UNSIGNED,
    /**
     * Identifies the generic SQL type {@code BIGINT_UNSIGNED}, eight byte integer number.
     */
    BIGINT_UNSIGNED,

    /**
     * Identifies the generic SQL type {@code FLOAT}.
     */
    FLOAT,
    /**
     * Identifies the generic SQL type {@code REAL}.
     */
    REAL,
    /**
     * Identifies the generic SQL type {@code DOUBLE}.
     */
    DOUBLE,
    /**
     * Identifies the generic SQL type {@code NUMERIC}.
     */
    NUMERIC,
    /**
     * Identifies the generic SQL type {@code DECIMAL}.
     */
    DECIMAL,

    /**
     * Identifies the generic SQL type {@code DECIMAL}.
     */
    DECIMAL_UNSIGNED,

    /**
     * Identifies the generic SQL type {@code CHAR}.
     */
    CHAR,
    /**
     * Identifies the generic SQL type {@code VARCHAR}.
     */
    VARCHAR,

    /**
     * Identifies the generic SQL type {@code ENUM}.
     */
    ENUM,

    /**
     * min precision text type
     */
    TINYTEXT,

    /**
     * precision greater than {@link #TINYTEXT} text type
     */
    TEXT,

    /**
     * precision greater than {@link #TEXT} text type
     */
    MEDIUMTEXT,

    /**
     * max precision text type
     */
    LONGTEXT,

    /**
     * Identifies the generic SQL type {@code BINARY}.
     */
    BINARY,
    /**
     * Identifies the generic SQL type {@code VARBINARY}.
     */
    VARBINARY,

    /**
     * min precision blob type
     */
    TINYBLOB,

    /**
     * precision greater than {@link #TINYBLOB} blob type
     */
    BLOB,

    /**
     * precision greater than {@link #BLOB} blob type
     */
    MEDIUMBLOB,
    /**
     * max precision blob type
     */
    LONGBLOB,

    /**
     * Identifies the generic SQL type {@code TIME}.
     */
    TIME,

    YEAR,

    YEAR_MONTH,

    MONTH_DAY,

    /**
     * Identifies the generic SQL type {@code DATE}.
     */
    DATE,

    /**
     * Identifies the generic SQL type {@code TIMESTAMP}.
     */
    TIMESTAMP,

    /**
     * Identifies the generic SQL type {@code TIME_WITH_TIMEZONE}.
     */
    TIME_WITH_TIMEZONE,

    /**
     * Identifies the generic SQL type {@code TIMESTAMP_WITH_TIMEZONE}.
     */
    TIMESTAMP_WITH_TIMEZONE,

    /**
     * A time-based amount of time, such as '34.5 seconds'.
     */
    DURATION,

    /**
     * A date-based amount of time.
     */
    PERIOD,

    /**
     * A date-time-based amount of time.
     */
    INTERVAL,


    /**
     * Identifies the SQL type {@code ROWID}.
     */
    ROWID,

    /**
     * Identifies the generic SQL type {@code XML}.
     */
    XML,

    JSON,

    JSONB,

    /**
     * Identifies the generic SQL type {@code GEOMETRY}, for example Point , LineString,polygon
     *
     * @see <a href="https://www.ogc.org/standards/sfa">Simple Feature Access - Part 1: Common Architecture PDF</a>
     */
    GEOMETRY,


    /*-------------------  -------------------*/

    UNKNOWN,


    /**
     * Identifies the generic SQL type {@code REF_CURSOR}.
     * <p>If {@link ResultRecordMeta#getArmyType(int)} is this enum instance,then {@link DataRecord#get(int)} always is {@link String} instance.
     * <p>Application developer can get the instance of {@link Cursor} by {@link ResultStates#valueOf(Option)}
     *
     * @see Cursor
     */
    REF_CURSOR,

    /**
     * <p>Identifies the generic SQL type {@code ARRAY}.
     * <p> This enum instance is only returned by {@link ResultRecordMeta#getArmyType(int)}
     */
    ARRAY,

    /**
     * <p>Identifies the generic SQL type {@code COMPOSITE}.
     * <p> This enum instance is only returned by {@link ResultRecordMeta#getArmyType(int)}
     */
    COMPOSITE,


    /**
     * Indicates that the dialect data type  .
     * <p> This enum instance is only returned by {@link ResultRecordMeta#getArmyType(int)}
     */
    DIALECT_TYPE;

    @Override
    public final String typeName() {
        return name();
    }


    @Override
    public final boolean isUnknown() {
        return this == UNKNOWN;
    }

    @Override
    public final boolean isArray() {
        return this == ARRAY;
    }


    public final boolean isDecimalType() {
        final boolean match;
        switch (this) {
            case DECIMAL:
            case DECIMAL_UNSIGNED:
            case NUMERIC:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }


    public final boolean isTextString() {
        final boolean match;
        switch (this) {
            case CHAR:
            case VARCHAR:
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

    public final boolean isBinaryString() {
        final boolean match;
        switch (this) {
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }

    public final boolean isNumberType() {
        return isSigned() || isUnsigned();
    }

    public final boolean isTimeType() {
        final boolean match;
        switch (this) {
            case TIME:
            case TIME_WITH_TIMEZONE:
            case DATE:
            case TIMESTAMP:
            case TIMESTAMP_WITH_TIMEZONE:
            case YEAR:
            case MONTH_DAY:
            case YEAR_MONTH:
            case INTERVAL:
            case PERIOD:
            case DURATION:
                match = true;
                break;
            default:
                match = false;
                break;
        }
        return match;
    }


    /**
     * @return true : unsigned number
     */
    public final boolean isUnsigned() {
        final boolean match;
        switch (this) {
            case TINYINT_UNSIGNED:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT_UNSIGNED:
            case INTEGER_UNSIGNED:
            case BIGINT_UNSIGNED:
            case DECIMAL_UNSIGNED:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }

    /**
     * @return true :  signed number
     */
    public final boolean isSigned() {
        final boolean match;
        switch (this) {
            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case INTEGER:
            case BIGINT:
            case DECIMAL:
            case NUMERIC:
            case FLOAT:
            case REAL:
            case DOUBLE:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }


}
