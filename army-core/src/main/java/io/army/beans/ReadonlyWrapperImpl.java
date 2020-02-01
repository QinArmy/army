package io.army.beans;

import io.army.ErrorCode;
import org.springframework.beans.BeanWrapper;

final class ReadonlyWrapperImpl implements ReadonlyWrapper {

    private final org.springframework.beans.BeanWrapper beanWrapper;

    ReadonlyWrapperImpl(BeanWrapper beanWrapper) {
        this.beanWrapper = beanWrapper;
    }

    ReadonlyWrapperImpl(Object target) {
        beanWrapper = org.springframework.beans.PropertyAccessorFactory.forBeanPropertyAccess(target);
    }

    @Override
    public boolean isReadableProperty(String propertyName) {
        return beanWrapper.isReadableProperty(propertyName);
    }

    @Override
    public Class<?> getPropertyType(String propertyName) throws BeansException {
        try {
            return beanWrapper.getPropertyType(propertyName);
        } catch (org.springframework.beans.BeansException e) {
            throw new BeansException(ErrorCode.BEAN_ACCESS_ERROR, e, e.getMessage());
        }
    }

    @Override
    public Object getPropertyValue(String propertyName) throws BeansException {
        try {
            return beanWrapper.getPropertyValue(propertyName);
        } catch (org.springframework.beans.BeansException e) {
            throw new BeansException(ErrorCode.BEAN_ACCESS_ERROR, e, e.getMessage());
        }
    }

    @Override
    public Object getWrappedInstance() {
        return beanWrapper.getWrappedInstance();
    }

    @Override
    public Class<?> getWrappedClass() {
        return beanWrapper.getWrappedClass();
    }
}
