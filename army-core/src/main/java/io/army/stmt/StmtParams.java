package io.army.stmt;


import io.army.criteria.Selection;

import java.util.List;

public interface StmtParams {

    String sql();

    List<ParamValue> paramList();

    List<Selection> selectionList();


}
