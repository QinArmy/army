package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.mapping.DoubleType;

public abstract class MySQLFunctions {

    /**
     * private constructor
     */
    private MySQLFunctions() {
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLFuncSyntax._AggregateWindowFunc<Expression, Selection> avg(Expression expr) {
        return MySQLFunctionUtils.oneArgAggregateWindow("AVG", expr, DoubleType.INSTANCE, SQLs::_asExp, SQLs::_identity);
    }
    
    











}
