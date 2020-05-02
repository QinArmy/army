package io.army.wrapper;

import io.army.beans.DomainWrapper;

import java.util.List;

public interface ChildSQLWrapper extends DomainSQLWrapper {

    String parentSql();

    /**
     * @return a unmodifiable list
     */
    List<ParamWrapper> parentParamList();


    static ChildSQLWrapper build(String sql
            , List<ParamWrapper> paramWrapperList
            , DomainWrapper domainWrapper
                                 // blow parent
            , String parentSql
            , List<ParamWrapper> parentParamList) {
        return new ChildSQLWrapperImpl(sql, paramWrapperList, domainWrapper
                , parentSql, parentParamList);
    }
}
