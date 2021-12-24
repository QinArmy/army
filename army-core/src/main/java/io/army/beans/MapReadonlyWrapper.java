package io.army.beans;

import io.army.ErrorCode;
import io.army.util.BeanUtils;
import io.army.util._Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

class MapReadonlyWrapper implements ReadWrapper {

    final Map<String, Object> map;

    MapReadonlyWrapper(Map<String, Object> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    MapReadonlyWrapper(Class<?> mapClass) {
        _Assert.isAssignable(Map.class, mapClass);

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
    public boolean isReadable(String propertyName) {
        return map.containsKey(propertyName);
    }

    @Override
    public Class<?> getType(String propertyName) throws BeansException {
        Object object = this.map.get(propertyName);
        if (object == null) {
            throw new InvalidPropertyException(ErrorCode.BEAN_ACCESS_ERROR, propertyName, this.map.getClass()
                    , "no value for property[%s]", propertyName);
        }
        return object.getClass();
    }

    @Override
    public Object get(String propertyName) throws BeansException {
        return this.map.get(propertyName);
    }

    @Override
    public Class<?> getWrappedClass() {
        return this.map.getClass();
    }
}
