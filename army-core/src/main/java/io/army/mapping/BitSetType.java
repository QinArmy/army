package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.util.BitSet;

public final class BitSetType extends AbstractMappingType {

    public static final BitSetType INSTANCE = new BitSetType();

    private BitSetType() {
    }

    @Override
    public Class<?> javaType() {
        return BitSet.class;
    }

    @Override
    public SqlType map(ServerMeta meta) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }


}
