package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SQLModifier;
import io.army.criteria.Selection;

import java.util.List;

@DeveloperForbid
public interface InnerBasicQueryAble extends InnerSQLAble {

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
