package io.army.criteria;

import java.util.List;

final class MySQLGroupImpl implements MySQLGroup {

    private final List<Expression<?>> groupExpList;

    private final boolean withRollUp;

    MySQLGroupImpl(List<Expression<?>> groupExpList, boolean withRollUp) {
        this.groupExpList = groupExpList;
        this.withRollUp = withRollUp;
    }

    @Override
    public List<Expression<?>> groupExpList() {
        return this.groupExpList;
    }

    @Override
    public boolean withRollUp() {
        return this.withRollUp;
    }
}
