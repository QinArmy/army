package io.army.bean;

import io.army.ErrorCode;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

class ReadonlyWrapperImpl implements ReadWrapper {

    private final PropertyAccessor beanWrapper;

    private final Object bean;

    ReadonlyWrapperImpl(Object bean, PropertyAccessor beanWrapper) {
        this.bean = bean;
        this.beanWrapper = beanWrapper;
    }

    ReadonlyWrapperImpl(Object bean) {
        this.bean = bean;
        if (bean instanceof FieldAccessBean) {
            this.beanWrapper = PropertyAccessorFactory.forDirectFieldAccess(bean);
        } else {
            this.beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        }

    }

    @Override
    public final boolean isReadable(String propertyName) {
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
    public final Object get(String propertyName) throws BeansException {
        try {
            return beanWrapper.getPropertyValue(propertyName);
        } catch (org.springframework.beans.BeansException e) {
            throw convertException(e);
        }
    }


    @Override
    public final Class<?> getWrappedClass() {
        return this.bean.getClass();
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
