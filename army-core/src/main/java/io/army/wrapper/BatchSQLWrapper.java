package io.army.wrapper;

import java.util.List;

public interface BatchSQLWrapper {

    String sql();

    List<List<ParamWrapper>> paramGroupList();


    static BatchSQLWrapper build(String sql, List<List<ParamWrapper>> paramGroupList) {
        return new BatchSQLWrapperImpl(sql, paramGroupList);
    }


}
