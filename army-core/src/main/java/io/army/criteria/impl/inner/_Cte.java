package io.army.criteria.impl.inner;

import io.army.criteria.CteItem;
import io.army.criteria.SubStatement;
import io.army.criteria.TabularItem;

public interface _Cte extends _DerivedTable, CteItem, TabularItem._DerivedTableSpec {


    SubStatement subStatement();

}
