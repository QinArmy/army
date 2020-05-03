package io.army.wrapper;

import java.util.List;

public interface SimpleSQLWrapper extends SQLWrapper {

    String sql();

    /**
     * @return a unmodifiable list
     */
    List<ParamWrapper> paramList();

    static SimpleSQLWrapper build(String sql, List<ParamWrapper> paramList) {
        return new SQLWrapperImpl(sql, paramList);
    }

}
