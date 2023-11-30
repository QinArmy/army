package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

public final class NoCastTextType extends _ArmyBuildInMapping implements MappingType.SqlTextType {


    public static NoCastTextType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(NoCastTextType.class, javaType);
        }
        return INSTANCE;
    }

    public static final NoCastTextType INSTANCE = new NoCastTextType();

    /**
     * private constructor
     */
    private NoCastTextType() {
    }


    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                type = PostgreType.NO_CAST_TEXT;
                break;
            case MySQL:
            case Oracle:
            case H2:
            default:
                type = TextType.mapToSqlType(this, meta);
        }
        return type;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public String convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return StringType.toString(this, map(env.serverMeta()), nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object nonNull) throws CriteriaException {
        return StringType.toString(this, dataType, nonNull, PARAM_ERROR_HANDLER);
    }

    @Override
    public String afterGet(DataType dataType, MappingEnv env, Object nonNull) throws DataAccessException {
        return StringType.toString(this, dataType, nonNull, ACCESS_ERROR_HANDLER);
    }


}
