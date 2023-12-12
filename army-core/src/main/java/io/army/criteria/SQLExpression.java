package io.army.criteria;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link Expression}</li>
 *         <li>{@link RowExpression}</li>
 *     </ul>
*
 * @since 1.0
 */
public interface SQLExpression extends RowElement, RightOperand {


    /**
     * <p>
     * <strong>= ANY</strong> operator
     *
     */
    CompoundPredicate equalAny(SubQuery subQuery);

    /**
     * <p>
     * <strong>= SOME</strong> operator
     *
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


    CompoundPredicate in(SQLColumnSet row);

    CompoundPredicate notIn(SQLColumnSet row);


}
