package io.army.wrapper;

import io.army.beans.ObjectWrapper;
import io.army.meta.TableMeta;

import java.util.List;

final class DomainBatchSQLWrapperImpl extends BatchSQLWrapperImpl implements DomainBatchSQLWrapper {

    private final List<ObjectWrapper> domainWrapperList;


    DomainBatchSQLWrapperImpl(String sql, List<List<ParamWrapper>> paramGroupList
            , TableMeta<?> tableMet, List<ObjectWrapper> domainWrapperList) {
        super(sql, paramGroupList, tableMet);
        if (paramGroupList.size() != domainWrapperList.size()) {
            throw new IllegalArgumentException("paramGroupList size and beanWrapperList size not match.");
        }
        this.domainWrapperList = domainWrapperList;
    }

    @Override
    public final List<ObjectWrapper> beanWrapperList() {
        return this.domainWrapperList;
    }

}
