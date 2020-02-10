package io.army.criteria;

import io.army.util.Pair;

import java.util.List;

/**
 * created  on 2018/12/4.
 */
public interface CriteriaContext {


    List<Selection> getSelectionList();

    List<Expression<?>> getTableList();

    List<IPredicate> getPredicateList();

    List<SortExpression<?>> getOrderList();

    Pair<Integer, Integer> getLimitPair();

    List<ParamExpression<?>> getParamExpressionList();

    ManipulateType getManipulateType();


}
