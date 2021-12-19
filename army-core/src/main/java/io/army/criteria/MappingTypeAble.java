package io.army.criteria;

import io.army.mapping.MappingType;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link Expression}</li>
 *         <li>{@link Selection}</li>
 *         <li>{@link io.army.meta.ParamMeta}</li>
 *     </ul>
 * </p>
 */
public interface MappingTypeAble {

    MappingType mappingType();
}
