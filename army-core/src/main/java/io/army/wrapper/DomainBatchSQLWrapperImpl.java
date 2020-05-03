package io.army.wrapper;

import io.army.beans.BeanWrapper;
import io.army.meta.TableMeta;

import java.util.List;

final class DomainBatchSQLWrapperImpl extends BatchSQLWrapperImpl implements DomainBatchSQLWrapper {

    private final List<BeanWrapper> domainWrapperList;


    DomainBatchSQLWrapperImpl(String sql, List<List<ParamWrapper>> paramGroupList
            , TableMeta<?> tableMet, List<BeanWrapper> domainWrapperList) {
        super(sql, paramGroupList, tableMet);
        if (paramGroupList.size() != domainWrapperList.size()) {
            throw new IllegalArgumentException("paramGroupList size and beanWrapperList size not match.");
        }
        this.domainWrapperList = domainWrapperList;
    }

    @Override
    public final List<BeanWrapper> beanWrapperList() {
        return this.domainWrapperList;
    }

}
