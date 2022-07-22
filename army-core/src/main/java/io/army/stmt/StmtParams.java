package io.army.stmt;


import io.army.criteria.Selection;
import io.army.criteria.SqlParam;

import java.util.List;

public interface StmtParams {

    String sql();

    /**
     * @return a unmodified list
     */
    List<SqlParam> paramList();

    /**
     * @return a unmodified list
     */
    List<Selection> selectionList();


}
