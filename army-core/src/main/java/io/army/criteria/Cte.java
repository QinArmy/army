package io.army.criteria;

import java.util.List;

public interface Cte {

    String name();

    List<String> columnNameList();

    SubQuery subQuery();

}
