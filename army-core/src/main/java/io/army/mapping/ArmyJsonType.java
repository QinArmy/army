package io.army.mapping;


import io.army.criteria.CriteriaException;
import io.army.sqltype.DataType;

/**
 * <p>Package class
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link JsonType}</li>
 *     <li>{@link JsonbType}</li>
 * </ul>
 *
 * @since 1.0
 */
abstract class ArmyJsonType extends _ArmyBuildInMapping {

    final Class<?> javaType;

    /**
     * Package constructor
     */
    ArmyJsonType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }


    @Override
    public final Object convert(MappingEnv env, final Object source) throws CriteriaException {
        if (this.javaType.isInstance(source)) {
            return source;
        }
        if (!(source instanceof String)) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), source, null);
        }
        return env.jsonCodec().decode((String) source, this.javaType);
    }

    @Override
    public final String beforeBind(DataType dataType, MappingEnv env, final Object source) {
        final String value;
        if (source instanceof String) {
            value = (String) source;
        } else if (this.javaType.isInstance(source)) {
            try {
                value = env.jsonCodec().encode(source);
            } catch (Exception e) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, e);
            }
        } else {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public final Object afterGet(DataType dataType, MappingEnv env, Object source) {
        if (!(source instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return env.jsonCodec().decode((String) source, this.javaType);
    }


}
