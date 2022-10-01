package io.army.criteria.impl.inner;

import io.army.criteria.CteItem;
import io.army.criteria.DerivedTable;
import io.army.criteria.SubStatement;

import java.util.List;

public interface _Cte extends DerivedTable, CteItem {

    List<String> columnNameList();

    SubStatement subStatement();

}