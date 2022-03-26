package io.army.meta;

import java.util.Map;

public interface GeneratorMeta extends Meta {

    FieldMeta<?> field();

    Class<?> javaType();

    /**
     * @return a immutable map
     */
    Map<String, String> params();
}
