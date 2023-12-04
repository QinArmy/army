package io.army.mapping;

import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.array.TextArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.struct.CodeEnum;
import io.army.struct.TextEnum;

import java.time.*;


/**
 * <p>
 * This class is mapping class of {@link String}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Number}</li>
 *     <li>{@link Boolean} </li>
 *     <li>{@link CodeEnum} </li>
 *     <li>{@link TextEnum} </li>
 *     <li>{@link Enum} </li>
 *     <li>{@link LocalDate} </li>
 *     <li>{@link LocalDateTime} </li>
 *     <li>{@link LocalTime} </li>
 *     <li>{@link OffsetDateTime} </li>
 *     <li>{@link ZonedDateTime} </li>
 *     <li>{@link OffsetTime} </li>
 *     <li>{@link Year}  to {@link Year} string or {@link LocalDate} string</li>
 *     <li>{@link YearMonth}  to {@link LocalDate} string </li>
 *     <li>{@link MonthDay} to {@link LocalDate} string</li>
 *     <li>{@link Instant} to {@link Instant#getEpochSecond()} string</li>
 *     <li>{@link java.time.Duration} </li>
 *     <li>{@link java.time.Period} </li>
 * </ul>
 *  to {@link String},if error,throw {@link io.army.ArmyException}
 * </p>
 *
 * @see StringType
 * @see MediumTextType
 * @since 1.0
 */
public final class TextType extends ArmyTextType {

    public static TextType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(TextType.class, javaType);
        }
        return INSTANCE;
    }

    public static final TextType INSTANCE = new TextType();

    /**
     * private constructor
     */
    private TextType() {
    }


    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public MappingType arrayTypeOfThis() {
        return TextArrayType.LINEAR;
    }


    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        return mapToDataType(this, meta);
    }

    static DataType mapToDataType(final MappingType type, final ServerMeta meta) {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.TEXT;
                break;
            case PostgreSQL:
                dataType = PostgreType.TEXT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return dataType;
    }


}
