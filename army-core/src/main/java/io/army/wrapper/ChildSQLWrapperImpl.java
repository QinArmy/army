package io.army.wrapper;

import io.army.beans.DomainWrapper;

import java.util.List;

final class ChildSQLWrapperImpl extends DomainSQLWrapperImpl implements ChildSQLWrapper {

    private final String parentSQL;

    private final List<ParamWrapper> parentParamList;

    ChildSQLWrapperImpl(String sql, List<ParamWrapper> paramList, DomainWrapper domainWrapper
            , String parentSQL, List<ParamWrapper> parentParamList) {
        super(sql, paramList, domainWrapper);
        this.parentSQL = parentSQL;
        this.parentParamList = parentParamList;
    }

    @Override
    public final String parentSql() {
        return this.parentSQL;
    }

    @Override
    public final List<ParamWrapper> parentParamList() {
        return this.parentParamList;
    }
}
