package io.army.wrapper;

import io.army.beans.BeanWrapper;
import io.army.meta.TableMeta;

import java.util.List;

public interface DomainBatchSQLWrapper {

    String sql();

    List<List<ParamWrapper>> paramGroupList();

    List<BeanWrapper> domainWrapperList();

    TableMeta<?> tableMeta();


    static DomainBatchSQLWrapper build(String sql
            , List<List<ParamWrapper>> paramGroupList
            , List<BeanWrapper> domainWrapperList
            , TableMeta<?> tableMeta) {

        return new DomainBatchSQLWrapperImpl(sql, paramGroupList, domainWrapperList, tableMeta);
    }


}
