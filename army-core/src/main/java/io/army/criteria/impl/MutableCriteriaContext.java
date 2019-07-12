package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * created  on 2018-12-24.
 */
public final class MutableCriteriaContext implements InnerCriteriaContext {

    private final String primarySqlId;

    private final List<String> subSqlId = new ArrayList<>();

    private final List<ParamExpression<?>> paramExpressionList = new ArrayList<>();

    public MutableCriteriaContext(String primarySqlId) {
        this.primarySqlId = primarySqlId;
    }


    @Override
    public List<Selection> getSelectionList() {
        return null;
    }

    @Override
    public List<Expression<?>> getTableList() {
        return null;
    }

    @Override
    public List<Predicate> getPredicateList() {
        return null;
    }

    @Override
    public List<OrderExpression<?>> getOrderList() {
        return null;
    }

    @Override
    public Pair<Integer, Integer> getLimitPair() {
        return null;
    }

    @Override
    public void addParam(ParamExpression<?> paramExpression) {

    }

    @Override
    public void addParamList(List<ParamExpression<?>> list) {

    }

    @Override
    public void where(List<Predicate> predicateList) {

    }

    @Override
    public List<ParamExpression<?>> getParamExpressionList() {
        return Collections.unmodifiableList(paramExpressionList);
    }


    @Override
    public ManipulateType getManipulateType() {
        return null;
    }
}
