package io.army.criteria.impl.inner;

import io.army.criteria.*;

import java.util.List;

@DeveloperForbid
public interface InnerBasicQueryAble {

    List<SQLModifier> modifierList();

    List<Selection> selectionList();

    List<TableWrapper> tableWrapperList();

    List<IPredicate> predicateList();

    List<Expression<?>> groupExpList();

    List<IPredicate> havingList();

    List<Expression<?>> sortExpList();

    int offset();

    int rowCount();

}
