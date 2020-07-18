package io.army.wrapper;

import java.util.List;
import java.util.Map;

public interface BatchSimpleSQLWrapper extends SQLWrapper {

    String sql();

    Map<Integer, List<ParamWrapper>> paramGroupMap();

    boolean hasVersion();


    static BatchSimpleSQLWrapper build(String sql, Map<Integer, List<ParamWrapper>> paramGroupMap) {
        return new BatchSimpleSQLWrapperImpl(sql, paramGroupMap);
    }

    static BatchSimpleSQLWrapper build(String sql, Map<Integer, List<ParamWrapper>> paramGroupMap, boolean hasVersion) {
        return new BatchSimpleSQLWrapperImpl(sql, paramGroupMap, hasVersion);
    }

}
