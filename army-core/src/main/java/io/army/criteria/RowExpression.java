package io.army.criteria;

import io.army.criteria.impl.SQLs;

import java.util.Collection;

/**
 * <p>
 * This interface representing one row in SQL.
 * </p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/row-constructor-optimization.html">MySQL Row Constructor Expression Optimization</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/range-optimization.html#row-constructor-range-optimization">Range Optimization of Row Constructor Expressions</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/row-subqueries.html">MySQL Row Subqueries</a>
 * @since 1.0
 */
public interface RowExpression extends SQLExpression, SQLColumnSet {


    /**
     * <p>
     * <strong>=</strong> operator
     * </p>
     *
     * @param operand non-null
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    CompoundPredicate equal(SQLColumnSet operand);

    CompoundPredicate notEqual(SQLColumnSet operand);


    /**
     * <p>
     * <strong>&lt;</strong> operator
     * </p>
     *
     * @param operand non-null
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    CompoundPredicate less(SQLColumnSet operand);


    CompoundPredicate lessEqual(SQLColumnSet operand);


    CompoundPredicate greater(SQLColumnSet operand);

    CompoundPredicate greaterEqual(SQLColumnSet operand);


}
