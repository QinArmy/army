package io.army.stmt;


import io.army.criteria.SQLParam;
import io.army.criteria.Selection;

import java.util.List;

public interface _StmtParams {

    String sql();

    /**
     * @return a unmodified list
     */
    List<SQLParam> paramList();

    /**
     * @return a unmodified list
     */
    List<Selection> selectionList();


}
