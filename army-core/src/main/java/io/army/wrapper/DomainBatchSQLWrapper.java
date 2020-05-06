package io.army.wrapper;

import io.army.beans.ObjectWrapper;
import io.army.meta.TableMeta;

import java.util.List;

public interface DomainBatchSQLWrapper extends SimpleBatchSQLWrapper {

    List<ObjectWrapper> beanWrapperList();


    static DomainBatchSQLWrapper build(String sql
            , List<List<ParamWrapper>> paramGroupList
            , TableMeta<?> tableMeta
            , List<ObjectWrapper> domainWrapperList) {

        return new DomainBatchSQLWrapperImpl(sql, paramGroupList, tableMeta, domainWrapperList);
    }


}
