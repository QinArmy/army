package io.army.criteria;

import io.army.annotation.UpdateMode;
import io.army.domain.IDomain;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;

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
public interface GenericField<T extends IDomain, F> extends FieldExpression<F>, FieldSelection, ParamMeta, SetTargetPart {

    TableMeta<T> tableMeta();

    /**
     * @return domain mapping property java type.
     */
    Class<F> javaType();

    MappingType mappingType();

    String fieldName();

    String columnName();

    UpdateMode updateMode();

    boolean codec();

    boolean databaseRoute();

    boolean tableRoute();

    boolean nullable();
}
