package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

/**
 * <p>
 * This class representing the mapping from {@link Void} to nothing,that returned by some special {@link io.army.criteria.Expression}.
 * </p>
 *
 * @see Void
 * @deprecated use {@link StringType}
 */
@Deprecated
public final class VoidType extends _ArmyInnerMapping {

    public static final VoidType INSTANCE = new VoidType();


    private VoidType() {
    }


    @Override
    public Class<?> javaType() {
        return Void.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }


}
