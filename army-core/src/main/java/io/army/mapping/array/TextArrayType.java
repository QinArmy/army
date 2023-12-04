package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.mapping.MappingType;
import io.army.mapping.TextType;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.function.Consumer;

public final class TextArrayType extends ArmyTextArrayType implements MappingType.SqlArrayType {


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

    /**
     * private constructor
     */
    private TextArrayType(Class<?> javaType) {
        super(javaType);
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

        PostgreArrays.encodeElement((String) element, appender);

    }


}
