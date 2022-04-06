package io.army.criteria;

import io.army.annotation.UpdateMode;
import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;

import java.lang.reflect.Field;

/**
 * <p>
 * This interface representing column of database table. This interface is base interface of below interfaces:
 * <ul>
 *     <li>{@link FieldMeta}</li>
 *      <li>{@link QualifiedField}</li>
 * </ul>
 * </p>
 *
 * @param <T> Domain Java Type
 * @see FieldMeta
 * @see QualifiedField
 */
@SuppressWarnings("unused")
public interface TableField<T extends IDomain> extends Expression, FieldSelection, ParamMeta, SetLeftItem {

    TableMeta<T> tableMeta();

    /**
     * @return domain mapping property java type.
     */
    Class<?> javaType();

    MappingType mappingType();

    /**
     * @return mapping  field name,see {@link Field#getName()}.
     */
    String fieldName();

    /**
     * @return column name(lower case).
     */
    String columnName();

    UpdateMode updateMode();

    boolean codec();

    boolean nullable();


    /**
     * relational operate with {@code =} {@link SQLs#nullableNamedParam(TableField)}
     */
    IPredicate equalNamed();

    IPredicate lessThanNamed();

    IPredicate lessEqualNamed();

    IPredicate greatThanNamed();

    IPredicate greatEqualNamed();

    IPredicate notEqualNamed();

    IPredicate likeNamed();

    IPredicate notLikeNamed();

    IPredicate inNamed();

    IPredicate notInNamed();

    Expression modNamed();

    Expression plusNamed();

    Expression minusNamed();

    Expression multiplyNamed();

    Expression divideNamed();

    Expression bitwiseAndNamed();

    Expression bitwiseOrNamed();

    Expression xorNamed();

    Expression rightShiftNamed();

    Expression leftShiftNamed();


    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();


}
