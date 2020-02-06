package io.army.dialect;

import java.util.List;

public interface SQLWrapper {

    String sql();

    /**
     * @return a unmodifiable list
     */
    List<ParamWrapper> paramList();

    String toString(Dialect dialect);

    @Override
    String toString();

    static SQLWrapper build(String sql, List<ParamWrapper> paramList) {
        return new SQLWrapperImpl(sql, paramList);
    }

    static SQLWrapper build(String sql, List<ParamWrapper> paramList,boolean hasVersion) {
        return new SQLWrapperImpl(sql, paramList,hasVersion);
    }
}
