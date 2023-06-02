package io.army.criteria;

import java.util.function.BiFunction;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link Expression}</li>
 *         <li>{@link RowExpression}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SQLExpression extends Item {


    /**
     * <p>
     * <strong>= ANY</strong> operator
     * </p>
     */
    CompoundPredicate equalAny(SubQuery subQuery);

    /**
     * <p>
     * <strong>= SOME</strong> operator
     * </p>
     */
    CompoundPredicate equalSome(SubQuery subQuery);

    CompoundPredicate equalAll(SubQuery subQuery);

    CompoundPredicate notEqualAny(SubQuery subQuery);

    CompoundPredicate notEqualSome(SubQuery subQuery);

    CompoundPredicate notEqualAll(SubQuery subQuery);

    CompoundPredicate lessAny(SubQuery subQuery);

    CompoundPredicate lessSome(SubQuery subQuery);

    CompoundPredicate lessAll(SubQuery subQuery);

    CompoundPredicate lessEqualAny(SubQuery subQuery);

    CompoundPredicate lessEqualSome(SubQuery subQuery);

    CompoundPredicate lessEqualAll(SubQuery subQuery);

    CompoundPredicate greaterAny(SubQuery subQuery);

    CompoundPredicate greaterSome(SubQuery subQuery);

    CompoundPredicate greaterAll(SubQuery subQuery);

    CompoundPredicate greaterEqualAny(SubQuery subQuery);

    CompoundPredicate greaterEqualSome(SubQuery subQuery);

    CompoundPredicate greaterEqualAll(SubQuery subQuery);


    CompoundPredicate in(RowElement row);

    CompoundPredicate notIn(RowElement row);


}
