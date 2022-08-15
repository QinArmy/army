package io.army.criteria.mysql;


import io.army.criteria.FuncExpression;
import io.army.criteria.Window;


public interface MySQLWindowFunc {


    interface _OverSpec extends MySQLWindowFunc, Window._OverClause {

        Window._SimpleOverLestParenSpec over();

    }

    interface _AggregateOverSpec extends _OverSpec, FuncExpression {

    }


}
