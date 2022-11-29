package io.army.criteria.impl.inner;

import io.army.criteria.CteItem;
import io.army.criteria.DerivedTable;
import io.army.criteria.SubStatement;
import io.army.criteria.TabularItem;

public interface _Cte extends DerivedTable, CteItem, TabularItem._DerivedTableSpec {


    SubStatement subStatement();

}
