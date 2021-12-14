package io.army.meta;

import io.army.criteria.MappingMetaAble;
import io.army.mapping.MappingType;

/**
 * @see MappingType
 * @see FieldMeta
 */
public interface ParamMeta extends Meta, MappingMetaAble {

    MappingType mappingMeta();
}
