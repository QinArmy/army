package io.army.criteria.impl;


import io.army.criteria.CompoundPredicate;
import io.army.criteria.RowElement;
import io.army.criteria.SubQuery;

/**
 * <p>
 * This class is a abstract implementation of {@link io.army.criteria.SQLExpression}. This class is base class of :
 *     <ul>
 *         <li>{@link OperationExpression}</li>
 *         <li>{@link OperationRowExpression}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
abstract class OperationSQLExpression implements ArmySQLExpression {

    /**
     * package constructor
     */
    OperationSQLExpression() {
    }

    @Override
    public final CompoundPredicate equalAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final CompoundPredicate equalSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.EQUAL, QueryOperator.SOME, subQuery);
    }

    @Override
    public final CompoundPredicate equalAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.EQUAL, QueryOperator.ALL, subQuery);
    }

    @Override
    public final CompoundPredicate notEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate notEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, QueryOperator.SOME, subQuery);
    }

    @Override
    public final CompoundPredicate notEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final CompoundPredicate lessAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, QueryOperator.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate lessSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, QueryOperator.SOME, subQuery);
    }

    @Override
    public final CompoundPredicate lessAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, QueryOperator.ALL, subQuery);
    }


    @Override
    public final CompoundPredicate lessEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate lessEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final CompoundPredicate lessEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, QueryOperator.ALL, subQuery);
    }

    @Override
    public final CompoundPredicate greaterAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, QueryOperator.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate greaterSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, QueryOperator.SOME, subQuery);
    }


    @Override
    public final CompoundPredicate greaterAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, QueryOperator.ALL, subQuery);
    }

    @Override
    public final CompoundPredicate greaterEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final CompoundPredicate greaterEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final CompoundPredicate greaterEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final CompoundPredicate in(RowElement row) {
        return Expressions.inPredicate(this, false, row);
    }

    @Override
    public final CompoundPredicate notIn(RowElement row) {
        return Expressions.inPredicate(this, true, row);
    }


}
