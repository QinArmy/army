package io.army.meta;

import io.army.meta.mapping.MappingMeta;

/**
 * @see io.army.meta.mapping.MappingMeta
 * @see FieldMeta
 */
public interface ParamMeta extends Meta {

    MappingMeta mappingMeta();
}
