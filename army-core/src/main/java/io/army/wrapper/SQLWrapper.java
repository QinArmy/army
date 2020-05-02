package io.army.wrapper;

import java.util.List;

public interface SQLWrapper {

    String sql();

    /**
     * @return a unmodifiable list
     */
    List<ParamWrapper> paramList();

    static SQLWrapper build(String sql, List<ParamWrapper> paramList) {
        return new SQLWrapperImpl(sql, paramList);
    }

}
