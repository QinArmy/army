package io.army.beans;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;

final class MapWrapperImpl extends MapReadonlyWrapper implements MapWrapper {

    MapWrapperImpl(Map<String, Object> map) {
        super(map);
    }

    MapWrapperImpl(Class<?> mapClass) {
        super(mapClass);
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
    public Map<String, Object> getUnmodifiableMap() {
        return this.map instanceof SortedMap
                ? Collections.unmodifiableSortedMap((SortedMap<String, Object>) this.map)
                : Collections.unmodifiableMap(this.map);
    }

    @Override
    public ReadonlyWrapper getReadonlyWrapper() {
        return new MapReadonlyWrapper(this.map);
    }
}
