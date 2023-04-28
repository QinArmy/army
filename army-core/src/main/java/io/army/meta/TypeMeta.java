package io.army.meta;

import io.army.criteria.CriteriaException;
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

    /**
     * @throws CriteriaException throw when this is {@link DelayTypeMeta} and {@link DelayTypeMeta#isDelay()} is true.
     */
    MappingType mappingType();


    interface DelayTypeMeta extends TypeMeta {


        boolean isDelay();
    }


}
