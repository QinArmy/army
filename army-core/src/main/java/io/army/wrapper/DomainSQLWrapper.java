package io.army.wrapper;

import io.army.beans.DomainWrapper;

import java.util.List;

public interface DomainSQLWrapper extends SQLWrapper {

    DomainWrapper domainWrapper();

    static DomainSQLWrapper build(String sql, List<ParamWrapper> paramWrapperList, DomainWrapper domainWrapper) {
        return new DomainSQLWrapperImpl(sql, paramWrapperList, domainWrapper);
    }

}
