package io.army.criteria;

import io.army.criteria.impl.SQLs;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * <p>
 * This interface representing one row in SQL.
*
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/row-constructor-optimization.html">MySQL Row Constructor Expression Optimization</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/range-optimization.html#row-constructor-range-optimization">Range Optimization of Row Constructor Expressions</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/row-subqueries.html">MySQL Row Subqueries</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SQL-SYNTAX-ROW-CONSTRUCTORS">Row Constructors</a>
 * @see <a href="https://www.postgresql.org/docs/current/functions-comparisons.html#ROW-WISE-COMPARISON">Row Constructor Comparison</a>
 * @since 1.0
 */
public interface RowExpression extends SQLExpression, SQLColumnSet {


    /**
     * <p>
     * <strong>=</strong> operator
     *
     *
     * @param operand non-null
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    CompoundPredicate equal(SQLColumnSet operand);

    CompoundPredicate notEqual(SQLColumnSet operand);

    CompoundPredicate nullSafeEqual(SQLColumnSet operand);

    /**
     * <p>
     * <strong>&lt;</strong> operator
     *
     *
     * @param operand non-null
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    CompoundPredicate less(SQLColumnSet operand);


    CompoundPredicate lessEqual(SQLColumnSet operand);


    CompoundPredicate greater(SQLColumnSet operand);

    CompoundPredicate greaterEqual(SQLColumnSet operand);


    /**
     * <p>
     * This method is designed for dialect operator.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.isDistinctFrom(RowExpression,RowExpression)}
     * @param right   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */
    CompoundPredicate space(BiFunction<RowExpression, RowExpression, CompoundPredicate> funcRef, RowExpression right);


}
