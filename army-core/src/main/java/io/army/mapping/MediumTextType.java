package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreTypes;
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
public final class MediumTextType extends _SQLStringType {

    public static final MediumTextType INSTANCE = new MediumTextType();

    public static MediumTextType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(MediumTextType.class, javaType);
        }
        return INSTANCE;
    }

    private MediumTextType() {
    }


    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public int _length() {
        return 3;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.MEDIUMTEXT;
                break;
            case PostgreSQL:
                type = PostgreTypes.TEXT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public String convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return StringType._convertToString(this, this.map(env.serverMeta()), nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        return StringType._convertToString(this, type, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public String afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return StringType._convertToString(this, type, nonNull, DATA_ACCESS_ERROR_HANDLER);
    }


}
