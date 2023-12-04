package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.MediumTextType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

public final class MediumTextArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    public static MediumTextArrayType from(final Class<?> javaType) {
        final MediumTextArrayType instance;
        if (javaType == String[].class) {
            instance = LINEAR;
        } else if (javaType.isArray() && ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new MediumTextArrayType(javaType);
        } else {
            throw errorJavaType(MediumTextArrayType.class, javaType);
        }
        return instance;
    }

    public static MediumTextArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final MediumTextArrayType UNLIMITED = new MediumTextArrayType(Object.class);

    public static final MediumTextArrayType LINEAR = new MediumTextArrayType(String[].class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private MediumTextArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return String.class;
    }


    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == String[].class) {
            instance = MediumTextType.INSTANCE;
        } else {
            instance = from(javaType.getComponentType());
        }
        return instance;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            return this;
        }
        return from(ArrayUtils.arrayClassOf(javaType));
    }


    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        return TextArrayType.mapToSqlType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, PostgreArrays::decodeElement,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, TextArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                PostgreArrays::decodeElement, ACCESS_ERROR_HANDLER
        );
    }


}
