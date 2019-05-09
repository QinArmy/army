package org.qinarmy.army.criteria;

import java.sql.JDBCType;

/**
 * created  on 2018/12/4.
 */
public interface ParamExpression<E> extends Expression<E> {


    Expression<E> getExpression();


    E getParam();

    JDBCType getJdbcType();


}
