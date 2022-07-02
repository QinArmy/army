package io.army.stmt;


import io.army.criteria.Selection;

import java.util.List;

public interface StmtParams {

    String sql();

    /**
     * @return a unmodified list
     */
    List<ParamValue> paramList();

    /**
     * @return a unmodified list
     */
    List<Selection> selectionList();


}