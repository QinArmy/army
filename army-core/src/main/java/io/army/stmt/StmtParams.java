package io.army.stmt;


import io.army.criteria.SQLParam;
import io.army.criteria.Selection;

import java.util.List;

public interface StmtParams {

    String sql();

    boolean hasOptimistic();

    StmtType stmtType();

    /**
     * @return a unmodified list
     */
    List<SQLParam> paramList();

    /**
     * @return a unmodified list
     */
    List<? extends Selection> selectionList();


}
