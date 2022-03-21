package io.army.generator;

import io.army.meta.FieldMeta;

public interface FieldGeneratorFactory {

    FieldGenerator get(FieldMeta<?> field);

}
