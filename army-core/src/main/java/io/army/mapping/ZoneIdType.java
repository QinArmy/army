package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

import java.time.*;

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
    public DataType map(final ServerMeta meta) {
        return StringType.mapToDataType(this, meta);
    }

    @Override
    public ZoneId convert(MappingEnv env, Object source) throws CriteriaException {
        return toZoneId(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) {
        ZoneId zoneId;
        zoneId = toZoneId(this, dataType, source, PARAM_ERROR_HANDLER);
        if (!(zoneId instanceof ZoneOffset)) {
            zoneId = zoneId.getRules().getOffset(Instant.EPOCH);
        }
        return zoneId.normalized().getId();
    }

    @Override
    public ZoneId afterGet(final DataType dataType, final MappingEnv env, final Object source) {
        return toZoneId(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static ZoneId toZoneId(final MappingType type, final DataType dataType, final Object nonNull,
                           final ErrorHandler errorHandler) {
        final ZoneId value;
        if (nonNull instanceof ZoneId) {
            value = (ZoneId) nonNull;
        } else if (nonNull instanceof String) {
            try {
                value = ZoneId.of((String) nonNull, ZoneOffset.SHORT_IDS);
            } catch (DateTimeException e) {
                throw errorHandler.apply(type, dataType, nonNull, e);
            }
        } else if (nonNull instanceof OffsetDateTime) {
            value = ((OffsetDateTime) nonNull).getOffset();
        } else if (nonNull instanceof ZonedDateTime) {
            value = ((ZonedDateTime) nonNull).getOffset();
        } else if (nonNull instanceof OffsetTime) {
            value = ((OffsetTime) nonNull).getOffset();
        } else {
            throw errorHandler.apply(type, dataType, nonNull, null);
        }
        return value;
    }


}
