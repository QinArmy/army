package io.army.dialect;

import io.army.beans.BeanWrapper;

import java.util.List;

final class BeanSQLWrapperImpl extends SQLWrapperImpl implements BeanSQLWrapper {

    private final BeanWrapper beanWrapper;

    public BeanSQLWrapperImpl(String sql, List<ParamWrapper> paramList, BeanWrapper beanWrapper) {
        super(sql, paramList);
        this.beanWrapper = beanWrapper;
    }

    @Override
    public final BeanWrapper beanWrapper() {
        return this.beanWrapper;
    }
}
