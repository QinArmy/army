package io.army.bean;

import io.army.lang.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

final class BeanWriterAccessor extends BeanReadAccessor implements ObjectAccessor {

    private ReadAccessor readAccessor;

    BeanWriterAccessor(BeanAccessors accessors) {
        super(accessors);
    }

    @Override
    public boolean isWritable(String propertyName) {
        return this.accessors.writerMap.get(propertyName) != null;
    }

    @Override
    public void set(final Object target, final String propertyName, final @Nullable Object value)
            throws ObjectAccessException {
        if (!this.accessors.beanClass.isInstance(target)) {
            Objects.requireNonNull(target);
            String m = String.format("%s isn't %s type."
                    , target.getClass().getName(), this.accessors.beanClass.getName());
            throw new IllegalArgumentException(m);
        }
        final ValueWriteAccessor accessor;
        accessor = accessors.writerMap.get(propertyName);
        if (accessor == null) {
            throw invalidProperty(propertyName);
        }
        try {
            accessor.set(target, value);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw accessError(propertyName, e);
        }
    }

    @Override
    public ReadAccessor getReadAccessor() {
        ReadAccessor readAccessor = this.readAccessor;
        if (readAccessor == null) {
            readAccessor = new BeanReadAccessor(this.accessors);
            this.readAccessor = readAccessor;
        }
        return readAccessor;
    }

}
