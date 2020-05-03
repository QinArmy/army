package io.army.wrapper;

import io.army.meta.TableMeta;

import java.util.List;

class BatchSQLWrapperImpl implements SimpleBatchSQLWrapper {

    private final String sql;

    private final List<List<ParamWrapper>> paramGroupList;

    private final TableMeta<?> tableMeta;

    BatchSQLWrapperImpl(String sql, List<List<ParamWrapper>> paramGroupList, TableMeta<?> tableMeta) {
        this.sql = sql;
        this.paramGroupList = paramGroupList;
        this.tableMeta = tableMeta;
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
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
    }
}
