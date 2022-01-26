package io.army.criteria;

import io.army.annotation.UpdateMode;
import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;

/**
 * This interface is base interface of below interface:
 * <ul>
 *     <li>{@link FieldMeta}</li>
 *      <li>{@link QualifiedField}</li>
 * </ul>
 *
 * @param <T> Domain Java Type
 * @param <F> Domain property Java Type
 * @see FieldMeta
 * @see QualifiedField
 */
public interface GenericField<T extends IDomain, F> extends Expression, FieldSelection, ParamMeta, SetLeftItem {

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

    boolean nullable();


    /**
     * relational operate with {@code =} {@link SQLs#nullableNamedParam(GenericField)}
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



}
