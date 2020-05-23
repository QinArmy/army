package io.army.wrapper;

import io.army.criteria.Selection;

import java.util.List;

public interface SimpleSQLWrapper extends SQLWrapper {

    String sql();

    /**
     * @return a unmodifiable list
     */
    List<ParamWrapper> paramList();

    boolean hasVersion();

    /**
     * @return a unmodifiable list
     */
    List<Selection> selectionList();

    static SimpleSQLWrapper build(String sql, List<ParamWrapper> paramList) {
        return new SimpleSQLWrapperImpl(sql, paramList);
    }

    static SimpleSQLWrapper build(String sql, List<ParamWrapper> paramList, boolean hasVersion) {
        return new SimpleSQLWrapperImpl(sql, paramList, hasVersion);
    }

    static SimpleSQLWrapper build(String sql, List<ParamWrapper> paramList, List<Selection> selectionList) {
        return new SimpleSQLWrapperImpl(sql, paramList, false, selectionList);
    }

    static SimpleSQLWrapper build(String sql, List<ParamWrapper> paramList, boolean hasVersion
            , List<Selection> selectionList) {
        return new SimpleSQLWrapperImpl(sql, paramList, hasVersion, selectionList);
    }

}
