package io.army.wrapper;

import io.army.criteria.Selection;

import java.util.List;

final class SelectSQLWrapperImpl extends SQLWrapperImpl implements SelectSQLWrapper {

    private final List<Selection> selectionList;

    SelectSQLWrapperImpl(String sql, List<ParamWrapper> paramList, List<Selection> selectionList) {
        super(sql, paramList);
        this.selectionList = selectionList;
    }

    @Override
    public final List<Selection> selectionList() {
        return this.selectionList;
    }
}
