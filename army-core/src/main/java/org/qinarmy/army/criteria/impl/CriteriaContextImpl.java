package org.qinarmy.army.criteria.impl;

import org.qinarmy.army.criteria.ManipulateType;
import org.qinarmy.army.criteria.ParamExpression;
import org.qinarmy.army.criteria.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * created  on 2018-12-24.
 */
public final class CriteriaContextImpl implements InnerCriteriaContext {

    private final String primarySqlId;

    private final List<String> subSqlId = new ArrayList<>();

    private final List<ParamExpression<?>> paramExpressionList = new ArrayList<>();

    public CriteriaContextImpl(String primarySqlId) {
        this.primarySqlId = primarySqlId;
    }


    @Override
    public String getPrimaryId() {
        return primarySqlId;
    }

    @Override
    public InnerCriteriaContext addParam(ParamExpression<?> paramExpression) {
        paramExpressionList.add(paramExpression);
        return this;
    }

    @Override
    public List<ParamExpression<?>> getParamExpressionList() {
        return Collections.unmodifiableList(paramExpressionList);
    }

    @Override
    public InnerCriteriaContext where(List<Predicate> predicateList) {
        return null;
    }

    @Override
    public ManipulateType getManipulateType() {
        return null;
    }
}
