package io.army.criteria.impl;


import io.army.criteria.SqlValueParam;
import io.army.dialect._SqlContext;

/**
 * <p>
 * Package interface
 * </p>
 */
@Deprecated
interface MultiValueExpression extends SqlValueParam.MultiValue {

    void appendSqlWithParens(_SqlContext context);

}
