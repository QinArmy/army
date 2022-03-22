package io.army.generator;

import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;

public abstract class FieldGeneratorUtils {


    protected FieldGeneratorUtils() {
        throw new UnsupportedOperationException();
    }


    public static GeneratorException dontSupportJavaType(Class<? extends FieldGenerator> generatorClass
            , FieldMeta<?> field) {
        String m = String.format("%s don't support java type[%s] of %s."
                , generatorClass.getName(), field.javaType().getName(), field);
        return new GeneratorException(m);
    }

    public static IllegalArgumentException noGeneratorMeta(FieldMeta<?> field) {
        String m = String.format("%s no %s.", field, GeneratorMeta.class.getName());
        return new IllegalArgumentException(m);
    }


}
