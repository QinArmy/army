package io.army.bean;

import org.springframework.beans.PropertyAccessorFactory;

class BeanWrapperImpl implements ObjectWrapper {

    final org.springframework.beans.BeanWrapper actualWrapper;

    ReadWrapper readonlyWrapper;

    BeanWrapperImpl(Object target) {
        this.actualWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
    }


    @Override
    public final boolean isWritableProperty(String propertyName) {
        return actualWrapper.isWritableProperty(propertyName);
    }

    @Override
    public final void set(String propertyName, Object value) throws BeansException {
        try {
            actualWrapper.setPropertyValue(propertyName, value);
        } catch (org.springframework.beans.BeansException e) {
            throw ReadonlyWrapperImpl.convertException(e);
        }
    }

    @Override
    public final boolean isReadable(String propertyName) {
        return actualWrapper.isReadableProperty(propertyName);
    }

    @Override
    public final Class<?> getType(String propertyName) throws BeansException {
        try {
            return actualWrapper.getPropertyType(propertyName);
        } catch (org.springframework.beans.BeansException e) {
            throw ReadonlyWrapperImpl.convertException(e);
        }
    }

    @Override
    public final Object get(String propertyName) throws BeansException {
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
    public ReadWrapper getReadonlyWrapper() {
        if (this.readonlyWrapper == null) {
            this.readonlyWrapper = new ReadonlyWrapperImpl(this.actualWrapper);
        }
        return readonlyWrapper;
    }

}
