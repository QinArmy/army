package io.army.meta;

import io.army.generator.PreFieldGenerator;

import java.util.Map;

public interface GeneratorMeta extends Meta {

    FieldMeta<?> field();

    Class<?> javaType();

    /**
     * @return prop name or empty.
     * @see PreFieldGenerator#DEPEND_FIELD_NAME
     */
    @Deprecated
    String dependFieldName();

    /**
     * @return a immutable map
     */
    Map<String, String> params();
}
