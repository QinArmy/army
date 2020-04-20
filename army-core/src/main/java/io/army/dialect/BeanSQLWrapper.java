package io.army.dialect;

import io.army.beans.BeanWrapper;

import java.util.List;

public interface BeanSQLWrapper extends SQLWrapper {

    BeanWrapper beanWrapper();

    static BeanSQLWrapper build(String sql, List<ParamWrapper> paramWrapperList, BeanWrapper beanWrapper) {
        return new BeanSQLWrapperImpl(sql, paramWrapperList, beanWrapper);
    }
}
