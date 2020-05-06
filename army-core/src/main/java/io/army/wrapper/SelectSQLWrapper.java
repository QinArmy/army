package io.army.wrapper;

import io.army.criteria.Selection;

import java.util.List;

public interface SelectSQLWrapper extends SimpleSQLWrapper {

    List<Selection> selectionList();


    static SelectSQLWrapper build(String sql, List<ParamWrapper> paramList, List<Selection> selectionList) {
        return new SelectSQLWrapperImpl(sql, paramList, selectionList);
    }
}
