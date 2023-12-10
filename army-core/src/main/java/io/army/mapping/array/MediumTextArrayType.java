package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.mapping.MappingType;
import io.army.mapping.MediumTextType;
import io.army.util.ArrayUtils;

public final class MediumTextArrayType extends ArmyTextArrayType {

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


    /**
     * private constructor
     */
    private MediumTextArrayType(Class<?> javaType) {
        super(javaType);
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


}