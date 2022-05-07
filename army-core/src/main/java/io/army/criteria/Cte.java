package io.army.criteria;

import java.util.List;

public interface Cte extends DerivedTable {

    String name();

    List<String> columnNameList();

    SubStatement subStatement();

}
