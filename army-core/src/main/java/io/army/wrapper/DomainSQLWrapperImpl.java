package io.army.wrapper;

import io.army.beans.DomainWrapper;

import java.util.List;

class DomainSQLWrapperImpl extends SQLWrapperImpl implements DomainSQLWrapper {

    private final DomainWrapper domainWrapper;

    DomainSQLWrapperImpl(String sql, List<ParamWrapper> paramList, DomainWrapper domainWrapper) {
        super(sql, paramList);
        this.domainWrapper = domainWrapper;
    }


    @Override
    public final DomainWrapper domainWrapper() {
        return this.domainWrapper;
    }
}
