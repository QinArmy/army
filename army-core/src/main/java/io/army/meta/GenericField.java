package io.army.meta;

import io.army.criteria.FieldExpression;
import io.army.criteria.FieldSelection;
import io.army.criteria.LogicalField;
import io.army.domain.IDomain;
import io.army.mapping.MappingType;

/**
 * This interface is base interface of below interface:
 * <ul>
 *     <li>{@link FieldMeta}</li>
 *      <li>{@link LogicalField}</li>
 * </ul>
 *
 * @param <T> Domain Java Type
 * @param <F> Domain property Java Type
 * @see FieldMeta
 * @see LogicalField
 */
public interface GenericField<T extends IDomain, F> extends FieldExpression<F>, FieldSelection {

    TableMeta<T> tableMeta();

    /**
     * @return domain mapping property java type.
     */
    Class<F> javaType();

    MappingType mappingMeta();

    String fieldName();
}
