package io.army.criteria.impl;

import io.army.criteria.CriteriaContext;
import io.army.criteria.ParamExpression;
import io.army.criteria.IPredicate;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * created  on 2019-02-01.
 */
interface InnerCriteriaContext extends CriteriaContext {

    void addParam(@NonNull ParamExpression<?> paramExpression);

    void addParamList(List<ParamExpression<?>> list);

    void where(List<IPredicate> IPredicateList);

}
