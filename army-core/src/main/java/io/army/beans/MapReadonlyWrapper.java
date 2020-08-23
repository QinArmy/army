package io.army.beans;

import io.army.ErrorCode;
import io.army.util.Assert;
import io.army.util.BeanUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

class MapReadonlyWrapper implements ReadonlyWrapper {

    final Map<String, Object> map;

    MapReadonlyWrapper(Map<String, Object> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    MapReadonlyWrapper(Class<?> mapClass) {
        Assert.isAssignable(Map.class, mapClass);

        if (mapClass.isInterface()) {
            if (SortedMap.class.isAssignableFrom(mapClass)) {
                this.map = new TreeMap<>();
            } else {
                this.map = new HashMap<>();
            }
        } else {
            this.map = (Map<String, Object>) BeanUtils.instantiateClass(mapClass);
        }

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
