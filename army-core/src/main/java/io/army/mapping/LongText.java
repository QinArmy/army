package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.type.TextPath;
import io.army.util.ClassUtils;

import java.io.Reader;

/**
 * @see TinyTextType
 * @see TextType
 * @see MediumTextType
 */
public final class LongText extends _ArmyBuildInMapping implements MappingType.SqlTextType {

    public static LongText from(final Class<?> javaType) {
        final LongText instance;
        final String clobClasName = "io.army.reactive.type.Clob";
        if (javaType == String.class) {
            instance = STRING;
        } else if (Reader.class.isAssignableFrom(javaType) || TextPath.class.isAssignableFrom(javaType)) {
            instance = new LongText(javaType);
        } else if (!ClassUtils.isPresent(clobClasName, null)) {
            throw errorJavaType(LongText.class, javaType);
        } else if (ClassUtils.isAssignableFrom(clobClasName, null, javaType)) {
            instance = new LongText(javaType);
        } else {
            throw errorJavaType(LongText.class, javaType);
        }
        return instance;
    }

    public static final LongText STRING = new LongText(String.class);


    private final Class<?> javaType;

    /**
     * private constructor
     */
    private LongText(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.LONG;
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.LONGTEXT;
                break;
            case PostgreSQL:
                dataType = PostgreType.TEXT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public Object convert(MappingEnv env, final Object source) throws CriteriaException {
        return convertToObject(map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        if (source instanceof String && this.javaType == String.class) {
            return source;
        }
        // TODO
        throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return convertToObject(dataType, source, ACCESS_ERROR_HANDLER);
    }


    private Object convertToObject(DataType dataType, final Object source, ErrorHandler errorHandler) {
        if (source instanceof String && this.javaType == String.class) {
            return source;
        }
        // TODO
        throw errorHandler.apply(this, dataType, source, null);
    }


}
