package io.army.criteria.impl.inner;

import io.army.criteria.Expression;

import java.util.List;

@DeveloperForbid
public interface InnerMySQLSingleUpdate extends InnerMySQLUpdate {

    /**
     * @return a unmodifiable list
     */
    List<Expression<?>> sortExpList();

    int rowCount();
}
