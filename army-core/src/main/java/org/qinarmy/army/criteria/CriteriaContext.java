package org.qinarmy.army.criteria;

import org.springframework.lang.NonNull;

import java.util.List;

/**
 * created  on 2018/12/4.
 */
public interface CriteriaContext {

    @NonNull
    String getPrimaryId();

    @NonNull
    List<ParamExpression<?>> getParamExpressionList();

    @NonNull
    ManipulateType getManipulateType();


}
