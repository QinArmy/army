package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

import java.time.*;

public final class ZoneOffsetType extends _ArmyNoInjectionType implements MappingType.SqlStringType {


    public static ZoneOffsetType from(final Class<?> javaType) {
        if (javaType != ZoneOffset.class) {
            throw errorJavaType(ZoneOffsetType.class, javaType);
        }
        return INSTANCE;
    }

    public static final ZoneOffsetType INSTANCE = new ZoneOffsetType();

    /**
     * private constructor
     */
    private ZoneOffsetType() {
    }

    @Override
    public Class<?> javaType() {
        return ZoneOffset.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return StringType.mapToDataType(this, meta);
    }

    @Override
    public ZoneOffset convert(MappingEnv env, Object source) throws CriteriaException {
        return toZoneOffset(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) {
        return toZoneOffset(this, dataType, source, PARAM_ERROR_HANDLER)
                .normalized().getId();
    }

    @Override
    public ZoneOffset afterGet(final DataType dataType, final MappingEnv env, final Object source) {
        return toZoneOffset(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


    static ZoneOffset toZoneOffset(final MappingType type, final DataType dataType, final Object source,
                                   final ErrorHandler errorHandler) {
        final ZoneOffset value;
        if (source instanceof ZoneOffset) {
            value = (ZoneOffset) source;
        } else if (source instanceof String) {
            try {
                final ZoneId v;
                v = ZoneOffset.of((String) source, ZoneOffset.SHORT_IDS);
                if (v instanceof ZoneOffset) {
                    value = (ZoneOffset) v;
                } else {
                    value = v.getRules().getOffset(Instant.EPOCH);
                }
            } catch (Exception e) {
                throw errorHandler.apply(type, dataType, source, e);
            }
        } else if (source instanceof OffsetDateTime) {
            value = ((OffsetDateTime) source).getOffset();
        } else if (source instanceof ZonedDateTime) {
            value = ((ZonedDateTime) source).getOffset();
        } else if (source instanceof OffsetTime) {
            value = ((OffsetTime) source).getOffset();
        } else {
            throw errorHandler.apply(type, dataType, source, null);
        }
        return value;
    }

}
