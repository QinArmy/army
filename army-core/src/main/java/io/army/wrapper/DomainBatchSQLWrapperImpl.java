package io.army.wrapper;

import io.army.beans.BeanWrapper;
import io.army.meta.TableMeta;

import java.util.List;

class DomainBatchSQLWrapperImpl implements DomainBatchSQLWrapper {

    private final String sql;

    private final List<List<ParamWrapper>> paramGroupList;

    private final List<BeanWrapper> domainWrapperList;

    final TableMeta<?> tableMeta;

    DomainBatchSQLWrapperImpl(String sql, List<List<ParamWrapper>> paramGroupList
            , List<BeanWrapper> domainWrapperList, TableMeta<?> tableMeta) {
        this.sql = sql;
        if (paramGroupList.size() != domainWrapperList.size()) {
            throw new IllegalArgumentException("paramGroupList size and domainWrapperList size not match.");
        }

        this.paramGroupList = paramGroupList;
        this.domainWrapperList = domainWrapperList;
        this.tableMeta = tableMeta;
    }

    @Override
    public final String sql() {
        return this.sql;
    }

    @Override
    public final List<List<ParamWrapper>> paramGroupList() {
        return this.paramGroupList;
    }

    @Override
    public final List<BeanWrapper> domainWrapperList() {
        return this.domainWrapperList;
    }

    @Override
    public TableMeta<?> tableMeta() {
        return this.tableMeta;
    }
}
