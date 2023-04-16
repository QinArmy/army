package io.army.criteria.dialect;

import io.army.criteria.SimpleExpression;

public interface VarExpression extends SimpleExpression {

    /**
     * @return session variable name
     */
    String name();


}
