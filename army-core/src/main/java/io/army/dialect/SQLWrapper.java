package io.army.dialect;

import java.util.List;

public interface SQLWrapper {

    String sql();

    /**
     * @return a unmodifiable list
     */
    List<ParamWrapper> paramList();

    @Override
    String toString();

    static SQLWrapper build(String sql, List<ParamWrapper> paramList) {
        return new SQLWrapperImpl(sql, paramList);
    }
}
