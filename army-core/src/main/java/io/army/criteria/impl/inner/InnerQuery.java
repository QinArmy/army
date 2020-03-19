package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SQLModifier;
import io.army.criteria.SelectPart;

import java.util.List;

@DeveloperForbid
public interface InnerQuery extends InnerSQL {

    List<SQLModifier> modifierList();

    List<SelectPart> selectPartList();

    List<IPredicate> predicateList();

    List<Expression<?>> groupExpList();

    List<IPredicate> havingList();

    List<Expression<?>> sortExpList();

    int offset();

    int rowCount();

}
