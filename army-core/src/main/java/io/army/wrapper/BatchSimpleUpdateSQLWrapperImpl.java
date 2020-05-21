package io.army.wrapper;

import java.util.List;

final class BatchSimpleUpdateSQLWrapperImpl implements BatchSimpleUpdateSQLWrapper {

    private final String sql;

    private final List<List<ParamWrapper>> paramGroupList;

    private final boolean hasVersion;

    BatchSimpleUpdateSQLWrapperImpl(String sql, List<List<ParamWrapper>> paramGroupList, boolean hasVersion) {
        this.sql = sql;
        this.paramGroupList = paramGroupList;
        this.hasVersion = hasVersion;
    }

    @Override
    public final String sql() {
        return this.sql;
    }

    @Override
    public final List<List<ParamWrapper>> paramGroupList() {
        return this.paramGroupList;
    }

    @Override
    public final boolean hasVersion() {
        return this.hasVersion;
    }
}
