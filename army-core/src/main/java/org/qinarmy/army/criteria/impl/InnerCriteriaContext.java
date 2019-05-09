package org.qinarmy.army.criteria.impl;

import org.qinarmy.army.criteria.CriteriaContext;
import org.qinarmy.army.criteria.ParamExpression;
import org.qinarmy.army.criteria.Predicate;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * created  on 2019-02-01.
 */
interface InnerCriteriaContext extends CriteriaContext {

    InnerCriteriaContext addParam(@NonNull ParamExpression<?> paramExpression);

    InnerCriteriaContext where(@NonNull List<Predicate> predicateList);
}
