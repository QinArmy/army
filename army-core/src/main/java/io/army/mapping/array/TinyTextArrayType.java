package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.mapping.MappingType;
import io.army.mapping.TinyTextType;
import io.army.util.ArrayUtils;

public final class TinyTextArrayType extends ArmyTextArrayType {


    public static TinyTextArrayType from(final Class<?> javaType) {
        final TinyTextArrayType instance;
        if (javaType == String[].class) {
            instance = LINEAR;
        } else if (javaType.isArray() && ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new TinyTextArrayType(javaType);
        } else {
            throw errorJavaType(TinyTextArrayType.class, javaType);
        }
        return instance;
    }

    public static TinyTextArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final TinyTextArrayType UNLIMITED = new TinyTextArrayType(Object.class);

    public static final TinyTextArrayType LINEAR = new TinyTextArrayType(String[].class);

    /**
     * private constructor
     */
    private TinyTextArrayType(Class<?> javaType) {
        super(javaType);
    }

    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == String[].class) {
            instance = TinyTextType.INSTANCE;
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


}
