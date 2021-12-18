package io.army.beans;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;

final class MapWrapperImpl extends MapReadonlyWrapper implements MapWrapper {

    MapWrapperImpl(Class<?> mapClass) {
        super(mapClass);
    }

    @Override
    public boolean isWritableProperty(String propertyName) {
        return true;
    }

    @Override
    public void set(String propertyName, Object value) throws BeansException {
        this.map.put(propertyName, value);
    }

    @Override
    public Object getWrappedInstance() {
        return this.map;
    }

    @Override
    public Map<String, Object> getUnmodifiableMap() {
        Map<String, Object> resultMap;
        if (this.map instanceof NavigableMap) {
            resultMap = Collections.unmodifiableNavigableMap((NavigableMap<String, Object>) this.map);
        } else if (this.map instanceof SortedMap) {
            resultMap = Collections.unmodifiableSortedMap((SortedMap<String, Object>) this.map);
        } else {
            resultMap = Collections.unmodifiableMap(this.map);
        }
        return resultMap;
    }

    @Override
    public ReadWrapper getReadonlyWrapper() {
        return new MapReadonlyWrapper(this.map);
    }
}
