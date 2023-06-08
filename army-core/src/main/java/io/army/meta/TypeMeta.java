package io.army.meta;

import io.army.criteria.TableField;
import io.army.mapping.MappingType;

/**
 * <p>
 * This interface representing the meta data of parameter.
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link MappingType}</li>
 *         <li>{@link TableField}</li>
 *     </ul>
 * </p>
 *
 * @see MappingType
 * @see FieldMeta
 */
public interface TypeMeta extends Meta {

    MappingType mappingType();



}
