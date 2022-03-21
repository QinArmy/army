package io.army.meta;

import io.army.generator.FieldGenerator;

import java.util.Map;

public interface GeneratorMeta extends Meta {

    FieldMeta<?> field();

    Class<?> javaType();

    /**
     * @return prop name or empty.
     * @see FieldGenerator#DEPEND_FIELD_NAME
     */
    @Deprecated
    String dependFieldName();

    /**
     * @return a immutable map
     */
    Map<String, String> params();
}
