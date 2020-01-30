package io.army.meta;

import java.util.Map;

public interface GeneratorMeta {

    Class<?> type();

    String dependPropName();

    /**
     * @return a immutable map
     */
    Map<String, String> params();
}
