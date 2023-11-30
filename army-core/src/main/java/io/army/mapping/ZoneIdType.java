package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.time.*;
import java.util.function.BiFunction;

/**
 * <p>
 * This class is mapping class of {@link ZoneId}.
 * </p>
 *
 * @since 1.0
 */
public final class ZoneIdType extends _ArmyNoInjectionMapping {

    public static ZoneIdType from(final Class<?> javaType) {
        if (!ZoneId.class.isAssignableFrom(javaType)) {
            throw errorJavaType(ZoneIdType.class, javaType);
        }
        return INSTANCE;
    }

    public static final ZoneIdType INSTANCE = new ZoneIdType();

    /**
     * private constructor
     */
    private ZoneIdType() {
    }

    @Override
    public Class<?> javaType() {
        return ZoneId.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        return StringType.mapToSqlType(this, meta);
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public ZoneId convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return convertToZoneId(this, nonNull, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        ZoneId zoneId;
        zoneId = convertToZoneId(this, nonNull, PARAM_ERROR_HANDLER_0);
        if (!(zoneId instanceof ZoneOffset)) {
            zoneId = zoneId.getRules().getOffset(Instant.EPOCH);
        }
        return zoneId.normalized().getId();
    }

    @Override
    public ZoneId afterGet(final SqlType type, final MappingEnv env, final Object nonNull) {
        return convertToZoneId(this, nonNull, DATA_ACCESS_ERROR_HANDLER_0);
    }


    private static ZoneId convertToZoneId(final MappingType type, final Object nonNull,
                                          final BiFunction<MappingType, Object, ArmyException> errorHandler) {
        final ZoneId value;
        if (nonNull instanceof ZoneId) {
            value = (ZoneId) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = ZoneId.of((String) nonNull, ZoneOffset.SHORT_IDS);
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, nonNull);
            }
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).getOffset();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).getOffset();
        } else if (nonNull instanceof OffsetTime) {
            value = ((OffsetTime) nonNull).getOffset();
        } else {
            throw errorHandler.apply(type, nonNull);
        }
        return value;
    }


}
