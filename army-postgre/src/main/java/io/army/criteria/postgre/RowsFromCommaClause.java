package io.army.criteria.postgre;

import io.army.criteria.SimpleExpression;
import io.army.criteria.SimplePredicate;
import io.army.criteria.UndoneFunction;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;

/**
 * <p>
 * This interface not start with underscore, so this interface can present in application developer code.
 * </p>
 *
 * @since 1.0
 */
public interface RowsFromCommaClause {

    RowsFromCommaClause comma(SimpleExpression func);

    RowsFromCommaClause comma(SimplePredicate func);

    RowsFromCommaClause comma(SQLs._TabularFunction func);

    Postgres._RowsFromAsClause comma(UndoneFunction func);


}
