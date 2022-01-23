package io.army.meta;

import io.army.criteria.QualifiedField;
import io.army.mapping.MappingType;

/**
 * <p>
 * This interface representing the meta data of parameter.
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link MappingType}</li>
 *         <li>{@link FieldMeta}</li>
 *         <li>{@link QualifiedField}</li>
 *     </ul>
 * </p>
 *
 * @see MappingType
 * @see FieldMeta
 * @see QualifiedField
 */
public interface ParamMeta extends Meta {

    MappingType mappingType();
}
