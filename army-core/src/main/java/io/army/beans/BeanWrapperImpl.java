package io.army.beans;

import io.army.ErrorCode;
import org.springframework.beans.PropertyAccessorFactory;

class BeanWrapperImpl implements BeanWrapper {

    private final org.springframework.beans.BeanWrapper actualWrapper;

    private ReadonlyWrapper readonlyWrapper;

    BeanWrapperImpl(Object target) {
        this.actualWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
    }


    @Override
    public boolean isWritableProperty(String propertyName) {
        return actualWrapper.isWritableProperty(propertyName);
    }

    @Override
    public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
        actualWrapper.setAutoGrowCollectionLimit(autoGrowCollectionLimit);
    }

    @Override
    public int getAutoGrowCollectionLimit() {
        return actualWrapper.getAutoGrowCollectionLimit();
    }

    @Override
    public void setPropertyValue(String propertyName, Object value) throws BeansException {
        try {
            actualWrapper.setPropertyValue(propertyName, value);
        } catch (org.springframework.beans.BeansException e) {
            throw new BeansException(ErrorCode.BEAN_ACCESS_ERROR, e, e.getMessage());
        }
    }

    @Override
    public boolean isReadableProperty(String propertyName) {
        return actualWrapper.isReadableProperty(propertyName);
    }

    @Override
    public Class<?> getPropertyType(String propertyName) throws BeansException {
        try {
            return actualWrapper.getPropertyType(propertyName);
        } catch (org.springframework.beans.BeansException e) {
            throw new BeansException(ErrorCode.BEAN_ACCESS_ERROR, e, e.getMessage());
        }
    }

    @Override
    public Object getPropertyValue(String propertyName) throws BeansException {
        try {
            return actualWrapper.getPropertyValue(propertyName);
        } catch (org.springframework.beans.BeansException e) {
            throw new BeansException(ErrorCode.BEAN_ACCESS_ERROR, e, e.getMessage());
        }
    }

    @Override
    public Object getWrappedInstance() {
        return actualWrapper.getWrappedInstance();
    }

    @Override
    public Class<?> getWrappedClass() {
        return actualWrapper.getWrappedClass();
    }

    @Override
    public ReadonlyWrapper getReadonlyWrapper() {
        if (this.readonlyWrapper == null) {
            this.readonlyWrapper = new ReadonlyWrapperImpl(this);
        }
        return readonlyWrapper;
    }
}
