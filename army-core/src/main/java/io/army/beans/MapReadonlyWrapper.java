package io.army.beans;

import io.army.ErrorCode;
import io.army.util.Assert;
import io.army.util.BeanUtils;

import java.util.Map;

final class MapReadonlyWrapper implements ReadonlyWrapper {

    private final Map<String, Object> map;

    MapReadonlyWrapper(Map<String, Object> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    MapReadonlyWrapper(Class<?> mapClass) {
        Assert.isAssignable(Map.class, mapClass);
        this.map = (Map<String, Object>) BeanUtils.instantiateClass(mapClass);
    }

    @Override
    public boolean isReadableProperty(String propertyName) {
        return map.containsKey(propertyName);
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
