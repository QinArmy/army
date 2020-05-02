package io.army.wrapper;

import io.army.beans.BeanWrapper;
import io.army.meta.ChildTableMeta;

import java.util.List;

final class ChildBatchSQLWrapperImpl extends DomainBatchSQLWrapperImpl implements ChildBatchSQLWrapper {

    private final String parentSql;

    private final List<List<ParamWrapper>> parentParamGroupList;

    ChildBatchSQLWrapperImpl(String sql
            , List<List<ParamWrapper>> paramGroupList
            , List<BeanWrapper> domainWrapperList
            , ChildTableMeta<?> childMeta
                             // blow parent
            , String parentSql
            , List<List<ParamWrapper>> parentParamGroupList) {
        super(sql, paramGroupList, domainWrapperList, childMeta);

        if (parentParamGroupList.size() != domainWrapperList.size()) {
            throw new IllegalArgumentException("parentParamGroupList size and domainWrapperList size not match.");
        }

        this.parentSql = parentSql;
        this.parentParamGroupList = parentParamGroupList;
    }

    @Override
    public final String parentSql() {
        return this.parentSql;
    }

    @Override
    public final ChildTableMeta<?> tableMeta() {
        return (ChildTableMeta<?>) this.tableMeta;
    }

    @Override
    public final List<List<ParamWrapper>> parentParamGroupList() {
        return this.parentParamGroupList;
    }
}
