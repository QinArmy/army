package io.army.wrapper;

import io.army.meta.TableMeta;

import java.util.List;

public interface BatchSimpleSQLWrapper extends BatchSQLWrapper {

    String sql();

    List<List<ParamWrapper>> paramGroupList();

    TableMeta<?> tableMeta();


    static BatchSimpleSQLWrapper build(String sql, List<List<ParamWrapper>> paramGroupList, TableMeta<?> tableMeta) {
        return new BatchSQLWrapperImpl(sql, paramGroupList, tableMeta);
    }


}
