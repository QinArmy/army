package io.army.criteria;

import java.util.List;

public interface MySQLGroup {

    List<Expression<?>> groupExpList();

    boolean withRollUp();

    static MySQLGroup build(List<Expression<?>> groupExpList, boolean withRollUp) {
        return new MySQLGroupImpl(groupExpList, withRollUp);
    }
}
