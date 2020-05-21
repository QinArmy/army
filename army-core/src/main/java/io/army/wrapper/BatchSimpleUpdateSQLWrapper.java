package io.army.wrapper;


import java.util.List;

public interface BatchSimpleUpdateSQLWrapper extends BatchSQLWrapper {

    String sql();

    List<List<ParamWrapper>> paramGroupList();

    boolean hasVersion();

    static BatchSimpleUpdateSQLWrapper build(String sql, List<List<ParamWrapper>> paramGroupList, boolean hasVersion) {
        return new BatchSimpleUpdateSQLWrapperImpl(sql, paramGroupList, hasVersion);
    }
}
