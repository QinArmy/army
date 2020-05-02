package io.army.meta;

import io.army.generator.PreFieldGenerator;

import java.util.Map;

public interface GeneratorMeta extends Meta {

    FieldMeta<?, ?> fieldMeta();

    Class<?> type();

    /**
     * @return prop name or empty.
     * @see PreFieldGenerator#DEPEND_PROP_NAME
     */
    String dependPropName();

    /**
     * @return a immutable map
     */
    Map<String, String> params();
}
