package io.army.wrapper;

import java.util.List;

public interface BatchSimpleSQLWrapper extends SQLWrapper {

    String sql();

    List<List<ParamWrapper>> paramGroupList();

    boolean hasVersion();


    static BatchSimpleSQLWrapper build(String sql, List<List<ParamWrapper>> paramGroupList) {
        return new BatchSimpleSQLWrapperImpl(sql, paramGroupList);
    }

    static BatchSimpleSQLWrapper build(String sql, List<List<ParamWrapper>> paramGroupList, boolean hasVersion) {
        return new BatchSimpleSQLWrapperImpl(sql, paramGroupList, hasVersion);
    }

}
