package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SQLType;

/**
 * <p>
 * This class representing the mapping from {@code  void} to nothing,that returned by some special {@link io.army.criteria.Expression}.
 * </p>
 *
 * @see Void
 */

public final class VoidType extends _ArmyBuildInMapping {

    public static final VoidType VOID = new VoidType();


    private VoidType() {
    }


    @Override
    public Class<?> javaType() {
        return void.class;
    }

    @Override
    public SQLType map(ServerMeta meta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(SQLType type, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SQLType type, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }


}
