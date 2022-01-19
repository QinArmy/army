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
public interface GenericField<T extends IDomain, F> extends Expression<F>, FieldSelection, ParamMeta, SetTargetPart {

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

    Expression<F> modNamed();

    Expression<F> plusNamed();

    Expression<F> minusNamed();

    Expression<F> multiplyNamed();

    Expression<F> divideNamed();

    Expression<F> bitwiseAndNamed();

    Expression<F> bitwiseOrNamed();

    Expression<F> xorNamed();

    Expression<F> rightShiftNamed();

    Expression<F> leftShiftNamed();

    IPredicate likeNamed();

    IPredicate notLikeNamed();

}
