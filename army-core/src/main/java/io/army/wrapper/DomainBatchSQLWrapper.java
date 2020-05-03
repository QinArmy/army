package io.army.wrapper;

import io.army.beans.BeanWrapper;
import io.army.meta.TableMeta;

import java.util.List;

public interface DomainBatchSQLWrapper extends SimpleBatchSQLWrapper {

    List<BeanWrapper> beanWrapperList();


    static DomainBatchSQLWrapper build(String sql
            , List<List<ParamWrapper>> paramGroupList
            , TableMeta<?> tableMeta
            , List<BeanWrapper> domainWrapperList) {

        return new DomainBatchSQLWrapperImpl(sql, paramGroupList, tableMeta, domainWrapperList);
    }


}
