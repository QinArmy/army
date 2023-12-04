package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.array.TextArrayType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;

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
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return TextArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.NO_CAST_TEXT;
                break;
            case MySQL:
            case Oracle:
            case H2:
            default:
                dataType = TextType.mapToDataType(this, meta);
        }
        return dataType;
    }

    @Override
    public String convert(MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return StringType.toString(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


}
