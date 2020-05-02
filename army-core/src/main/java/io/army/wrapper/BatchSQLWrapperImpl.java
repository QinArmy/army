package io.army.wrapper;

import java.util.List;

final class BatchSQLWrapperImpl implements BatchSQLWrapper {

    private final String sql;

    private final List<List<ParamWrapper>> paramGroupList;

    BatchSQLWrapperImpl(String sql, List<List<ParamWrapper>> paramGroupList) {
        this.sql = sql;
        this.paramGroupList = paramGroupList;
    }

    @Override
    public final String sql() {
        return this.sql;
    }


    @Override
    public final List<List<ParamWrapper>> paramGroupList() {
        return this.paramGroupList;
    }

}
