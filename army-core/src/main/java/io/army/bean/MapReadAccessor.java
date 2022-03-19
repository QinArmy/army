package io.army.bean;

import java.util.Map;
import java.util.Objects;

final class MapReadAccessor implements ReadAccessor {

    static final MapReadAccessor INSTANCE = new MapReadAccessor();

    private MapReadAccessor() {
    }


    @Override
    public boolean isReadable(String propertyName) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object get(final Object target, final String propertyName) throws ObjectAccessException {
        if (!(target instanceof Map)) {
            Objects.requireNonNull(target);
            String m = String.format("%s isn't %s type."
                    , target.getClass().getName(), Map.class.getName());
            throw new IllegalArgumentException(m);
        }

        return ((Map<String, Object>) target).get(propertyName);
    }

    @Override
    public Class<?> getAccessedType() {
        return Map.class;
    }


    @Override
    public String toString() {
        return String.format("%s of %s", ReadAccessor.class.getName(), Map.class.getName());
    }


}
