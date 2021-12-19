package io.army.meta;

import io.army.criteria.MappingTypeAble;
import io.army.mapping.MappingType;

/**
 * @see MappingType
 * @see FieldMeta
 */
public interface ParamMeta extends Meta, MappingTypeAble {

    MappingType mappingType();
}
