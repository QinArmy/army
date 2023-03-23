package io.army.criteria.impl.inner;

import io.army.criteria.SubStatement;
import io.army.criteria.TabularItem;

import java.util.List;

public interface _Cte extends TabularItem, _SelectionMap {


    String name();


    SubStatement subStatement();

    List<String> columnAliasList();

}
