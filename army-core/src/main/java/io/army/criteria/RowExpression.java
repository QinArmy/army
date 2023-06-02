package io.army.criteria;

import io.army.criteria.impl.SQLs;

import java.util.Collection;

import static io.army.dialect.Database.MySQL;

/**
 * <p>
 * This interface representing one row in SQL.
 * </p>
 *
 * @since 1.0
 */
public interface RowExpression extends SQLExpression, RowElement {


    /**
     * <p>
     * <strong>=</strong> operator
     * </p>
     *
     * @param operand non-null
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    CompoundPredicate equal(RowElement operand);

    CompoundPredicate notEqual(RowElement operand);


    /**
     * <p>
     * <strong>&lt;</strong> operator
     * </p>
     *
     * @param operand non-null
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    CompoundPredicate less(RowElement operand);


    CompoundPredicate lessEqual(RowElement operand);


    CompoundPredicate greater(RowElement operand);

    CompoundPredicate greaterEqual(RowElement operand);


}
