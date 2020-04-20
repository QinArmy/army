package io.army.dialect;

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
    public String toString(Dialect dialect) {
        //TODO zoro implement sql parse.
        return "";
    }

    @Override
    public final List<List<ParamWrapper>> paramGroupList() {
        return this.paramGroupList;
    }
}
