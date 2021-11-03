package io.army.beans;

import io.army.ErrorCode;

class ReadonlyWrapperImpl implements ReadonlyWrapper {

    private final org.springframework.beans.BeanWrapper beanWrapper;

    ReadonlyWrapperImpl(org.springframework.beans.BeanWrapper beanWrapper) {
        this.beanWrapper = beanWrapper;
    }

    ReadonlyWrapperImpl(Object target) {
        beanWrapper = org.springframework.beans.PropertyAccessorFactory.forBeanPropertyAccess(target);
    }

    @Override
    public final boolean isReadableProperty(String propertyName) {
        return beanWrapper.isReadableProperty(propertyName);
    }

    @Override
    public final Class<?> getType(String propertyName) throws BeansException {
        try {
            return beanWrapper.getPropertyType(propertyName);
        } catch (org.springframework.beans.BeansException e) {
            throw convertException(e);
        }
    }

    @Override
    public final Object getPropertyValue(String propertyName) throws BeansException {
        try {
            return beanWrapper.getPropertyValue(propertyName);
        } catch (org.springframework.beans.BeansException e) {
            throw convertException(e);
        }
    }


    @Override
    public final Class<?> getWrappedClass() {
        return beanWrapper.getWrappedClass();
    }


    static BeansException convertException(org.springframework.beans.BeansException e) {
        BeansException be;
        if (e instanceof org.springframework.beans.InvalidPropertyException) {
            org.springframework.beans.InvalidPropertyException pe =
                    (org.springframework.beans.InvalidPropertyException) e;

            be = new InvalidPropertyException(ErrorCode.BEAN_ACCESS_ERROR, e, pe.getPropertyName(), pe.getBeanClass()
                    , e.getMessage());
        } else if (e instanceof org.springframework.beans.PropertyAccessException) {
            be = new PropertyAccessException(ErrorCode.BEAN_ACCESS_ERROR, e, e.getMessage());
        } else {
            be = new BeansException(ErrorCode.BEAN_ACCESS_ERROR, e, e.getMessage());
        }
        return be;
    }

}
