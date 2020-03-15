package io.army.criteria.impl.inner;

import io.army.criteria.*;
import io.army.criteria.impl.TableWrapper;
import io.army.lang.Nullable;

import java.util.List;

@DeveloperForbid
public interface InnerQueryAble extends InnerSQLAble{

    List<SQLModifier> modifierList();

    List<Selection> selectionList();

    List<TableWrapper> tableWrapperList();

    List<IPredicate> predicateList();

    List<SortExpression<?>> groupExpList();

    List<IPredicate> havingList();

    List<SortExpression<?>> sortExpList();

    int offset();

    int rowCount();

    LockMode lockMode();


}
