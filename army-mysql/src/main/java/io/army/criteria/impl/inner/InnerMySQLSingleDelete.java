package io.army.criteria.impl.inner;

import io.army.criteria.Expression;

import java.util.List;

@DeveloperForbid
public interface InnerMySQLSingleDelete extends InnerMySQLDelete {

    /**
     * @return a unmodifiable list
     */
    List<Expression<?>> sortExpList();

    int rowCount();
}
