package io.army.wrapper;

import java.util.List;

public interface SimpleUpdateSQLWrapper extends SimpleSQLWrapper {

    boolean hasVersion();

    static SimpleUpdateSQLWrapper build(String sql, List<ParamWrapper> paramList, boolean hasVersion) {
        return new SimpleUpdateSQLWrapperImpl(sql, paramList, hasVersion);
    }

}
