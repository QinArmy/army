package io.army.wrapper;


import io.army.criteria.Selection;

import java.util.Collections;
import java.util.List;

final class SimpleSQLWrapperImpl implements SimpleSQLWrapper {

    private final String sql;

    private final List<ParamWrapper> paramList;

    private final boolean hasVersion;

    private final List<Selection> selectionList;

    SimpleSQLWrapperImpl(String sql, List<ParamWrapper> paramList) {
        this.sql = sql;
        this.paramList = paramList;
        this.hasVersion = false;
        this.selectionList = Collections.emptyList();
    }

    SimpleSQLWrapperImpl(String sql, List<ParamWrapper> paramList, boolean hasVersion) {
        this.sql = sql;
        this.paramList = paramList;
        this.hasVersion = hasVersion;
        this.selectionList = Collections.emptyList();
    }

    SimpleSQLWrapperImpl(String sql, List<ParamWrapper> paramList
            , boolean hasVersion, List<Selection> selectionList) {
        this.sql = sql;
        this.paramList = paramList;
        this.hasVersion = hasVersion;
        this.selectionList = Collections.unmodifiableList(selectionList);
    }

    @Override
    public final String sql() {
        return sql;
    }

    @Override
    public final List<ParamWrapper> paramList() {
        return paramList;
    }

    @Override
    public final boolean hasVersion() {
        return this.hasVersion;
    }

    @Override
    public final List<Selection> selectionList() {
        return this.selectionList;
    }
}
