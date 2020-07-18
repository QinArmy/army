package io.army.wrapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

final class BatchSimpleSQLWrapperImpl implements BatchSimpleSQLWrapper {

    private final String sql;

    private final Map<Integer, List<ParamWrapper>> paramGroupMap;

    private final boolean hasVersion;


    BatchSimpleSQLWrapperImpl(String sql, Map<Integer, List<ParamWrapper>> paramGroupMap) {
        this.sql = sql;
        this.paramGroupMap = Collections.unmodifiableMap(paramGroupMap);
        this.hasVersion = false;
    }

    BatchSimpleSQLWrapperImpl(String sql, Map<Integer, List<ParamWrapper>> paramGroupMap, boolean hasVersion) {
        this.sql = sql;
        this.paramGroupMap = Collections.unmodifiableMap(paramGroupMap);
        this.hasVersion = hasVersion;
    }

    @Override
    public final String sql() {
        return this.sql;
    }

    @Override
    public final Map<Integer, List<ParamWrapper>> paramGroupMap() {
        return this.paramGroupMap;
    }

    @Override
    public final boolean hasVersion() {
        return this.hasVersion;
    }
}
