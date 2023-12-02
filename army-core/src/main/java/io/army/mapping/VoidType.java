package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

/**
 * <p>
 * This class representing the mapping from {@code  void} to nothing,that returned by some special {@link io.army.criteria.Expression}.
 * </p>
 *
 * @see Void
 */

public final class VoidType extends _ArmyBuildInMapping {

    public static final VoidType VOID = new VoidType();

    /**
     * private constructor
     */
    private VoidType() {
    }


    @Override
    public Class<?> javaType() {
        return void.class;
    }

    @Override
    public DataType map(ServerMeta meta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) {
        throw new UnsupportedOperationException();
    }


}
