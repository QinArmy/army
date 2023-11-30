package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
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
 * @see TextType
 * @since 1.0
 */
public final class MediumTextType extends _ArmyBuildInMapping implements MappingType.SqlTextType {


    public static MediumTextType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(MediumTextType.class, javaType);
        }
        return INSTANCE;
    }

    public static final MediumTextType INSTANCE = new MediumTextType();

    /**
     * private constructor
     */
    private MediumTextType() {
    }


    @Override
    public Class<?> javaType() {
        return String.class;
    }


    @Override
    public LengthType lengthType() {
        return LengthType.MEDIUM;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.MEDIUMTEXT;
                break;
            case PostgreSQL:
                type = PostgreType.TEXT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public String convert(MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return StringType.toString(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


}
