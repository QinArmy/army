package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.TextType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.function.Consumer;

public final class TextArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {


    public static TextArrayType from(final Class<?> javaType) {
        final TextArrayType instance;
        if (javaType == String[].class) {
            instance = LINEAR;
        } else if (javaType.isArray() && ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new TextArrayType(javaType);
        } else {
            throw errorJavaType(TextArrayType.class, javaType);
        }
        return instance;
    }

    public static TextArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final TextArrayType UNLIMITED = new TextArrayType(Object.class);

    public static final TextArrayType LINEAR = new TextArrayType(String[].class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private TextArrayType(Class<?> javaType) {
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
            instance = TextType.INSTANCE;
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
        return mapToSqlType(this, meta);
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


    /*-------------------below static methods -------------------*/

    static SqlType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.TEXT_ARRAY;
                break;
            case Oracle:
            case H2:
            case MySQL:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return dataType;
    }

    static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof String)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }

        PostgreArrays.escapeElement((String) element, appender);

    }


}
