package io.army.criteria.dialect;

import io.army.criteria.Expression;

public interface VarExpression extends Expression {

    /**
     * @return session variable name
     */
    String name();


}
