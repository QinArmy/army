package io.army.meta;

import java.util.Map;

public interface GeneratorMeta {

    Class<?> type();

    /**
     *
     * @return prop name or empty.
     * @see io.army.generator.PreMultiGenerator#DEPEND_PROP_NAME
     */
    String dependPropName();

    /**
     * @return a immutable map
     */
    Map<String, String> params();
}
