package io.army.dialect;

import io.army.criteria.SetLeftItem;
import io.army.criteria.SetRightItem;
import io.army.criteria.TableField;

import java.util.List;

interface _SetClause {

    _SqlContext context();

    List<? extends SetLeftItem> leftItemList();

    List<? extends SetRightItem> rightItemList();


    String validateField(TableField field);


    boolean supportTableAlias();

    boolean supportRow();

}
