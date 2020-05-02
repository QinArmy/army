package io.army.wrapper;


import io.army.beans.BeanWrapper;
import io.army.meta.ChildTableMeta;

import java.util.List;

public interface ChildBatchSQLWrapper extends DomainBatchSQLWrapper {

    String parentSql();

    List<List<ParamWrapper>> parentParamGroupList();

    @Override
    ChildTableMeta<?> tableMeta();

    static ChildBatchSQLWrapper build(String sql
            , List<List<ParamWrapper>> paramGroupList
            , List<BeanWrapper> domainWrapperList
            , ChildTableMeta<?> childMeta
                                      // blow parent
            , String parentSql
            , List<List<ParamWrapper>> parentParamGroupList) {

        return new ChildBatchSQLWrapperImpl(sql, paramGroupList, domainWrapperList, childMeta
                , parentSql, parentParamGroupList);
    }

}
