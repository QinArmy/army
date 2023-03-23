package io.army.criteria.impl.inner;

import io.army.criteria.CteItem;
import io.army.criteria.SubStatement;

import java.util.List;

public interface _Cte extends CteItem, _SelectionMap {


    String name();


    SubStatement subStatement();

    List<String> columnAliasList();

}
