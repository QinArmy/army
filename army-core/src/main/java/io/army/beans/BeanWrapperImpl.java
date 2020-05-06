package io.army.beans;

import org.springframework.beans.PropertyAccessorFactory;

class BeanWrapperImpl implements ObjectWrapper {

    final org.springframework.beans.BeanWrapper actualWrapper;

    ReadonlyWrapper readonlyWrapper;

    BeanWrapperImpl(Object target) {
        this.actualWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
    }


    @Override
    public final boolean isWritableProperty(String propertyName) {
        return actualWrapper.isWritableProperty(propertyName);
    }

    @Override
    public final void setPropertyValue(String propertyName, Object value) throws BeansException {
        try {
            actualWrapper.setPropertyValue(propertyName, value);
        } catch (org.springframework.beans.BeansException e) {
            throw ReadonlyWrapperImpl.convertException(e);
        }
    }

    @Override
    public final boolean isReadableProperty(String propertyName) {
        return actualWrapper.isReadableProperty(propertyName);
    }

    @Override
    public final Class<?> getPropertyType(String propertyName) throws BeansException {
        try {
            return actualWrapper.getPropertyType(propertyName);
        } catch (org.springframework.beans.BeansException e) {
            throw ReadonlyWrapperImpl.convertException(e);
        }
    }

    @Override
    public final Object getPropertyValue(String propertyName) throws BeansException {
        try {
            return actualWrapper.getPropertyValue(propertyName);
        } catch (org.springframework.beans.BeansException e) {
            throw ReadonlyWrapperImpl.convertException(e);
        }
    }

    @Override
    public final Object getWrappedInstance() {
        return actualWrapper.getWrappedInstance();
    }

    @Override
    public final Class<?> getWrappedClass() {
        return actualWrapper.getWrappedClass();
    }

    @Override
    public ReadonlyWrapper getReadonlyWrapper() {
        if (this.readonlyWrapper == null) {
            this.readonlyWrapper = new ReadonlyWrapperImpl(this.actualWrapper);
        }
        return readonlyWrapper;
    }

}
