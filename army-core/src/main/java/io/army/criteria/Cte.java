package io.army.criteria;

import java.util.List;

public interface Cte extends DerivedTable, CteItem {

    List<String> columnNameList();

    SubStatement subStatement();

}
