package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;

public final class CharacterType extends _ArmyInnerMapping implements MappingType.SqlStringType {


    public static final CharacterType INSTANCE = new CharacterType();

    public static CharacterType from(final Class<?> javaType) {
        if (javaType != Character.class) {
            throw errorJavaType(CharacterType.class, javaType);
        }
        return INSTANCE;
    }


    private CharacterType() {
    }

    @Override
    public Class<?> javaType() {
        return Character.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.TINY;
    }

    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
