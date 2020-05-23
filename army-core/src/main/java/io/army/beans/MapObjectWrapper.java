package io.army.beans;

import io.army.ErrorCode;
import io.army.util.Assert;
import io.army.util.BeanUtils;

import java.util.Map;

final class MapObjectWrapper implements BeanWrapper {

    private final Map<String, Object> map;

    MapObjectWrapper(Map<String, Object> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    MapObjectWrapper(Class<?> mapClass) {
        Assert.isAssignable(Map.class, mapClass);
        this.map = (Map<String, Object>) BeanUtils.instantiateClass(mapClass);
    }

    @Override
    public boolean isWritableProperty(String propertyName) {
        return true;
    }

    @Override
    public void setPropertyValue(String propertyName, Object value) throws BeansException {
        this.map.put(propertyName, value);
    }

    @Override
    public Object getWrappedInstance() {
        return this.map;
    }

    @Override
    public ReadonlyWrapper getReadonlyWrapper() {
        return this;
    }

    @Override
    public boolean isReadableProperty(String propertyName) {
        return true;
    }

    @Override
    public Class<?> getPropertyType(String propertyName) throws BeansException {
        Object object = this.map.get(propertyName);
        if (object == null) {
            throw new InvalidPropertyException(ErrorCode.BEAN_ACCESS_ERROR, propertyName, this.map.getClass()
                    , "no value for property[%s]", propertyName);
        }
        return object.getClass();
    }

    @Override
    public Object getPropertyValue(String propertyName) throws BeansException {
        return this.map.get(propertyName);
    }

    @Override
    public Class<?> getWrappedClass() {
        return this.map.getClass();
    }
}
