package io.army.wrapper;

import io.army.meta.TableMeta;

import java.util.List;

public interface SimpleBatchSQLWrapper extends BatchSQLWrapper {

    String sql();

    List<List<ParamWrapper>> paramGroupList();

    TableMeta<?> tableMeta();


    static SimpleBatchSQLWrapper build(String sql, List<List<ParamWrapper>> paramGroupList, TableMeta<?> tableMeta) {
        return new BatchSQLWrapperImpl(sql, paramGroupList, tableMeta);
    }


}
