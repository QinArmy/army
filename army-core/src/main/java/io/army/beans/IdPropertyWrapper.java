package io.army.beans;

import io.army.lang.Nullable;
import io.army.meta.TableMeta;

final class IdPropertyWrapper implements ObjectWrapper {

    private final Class<?> idClass;

    private Object id;

    IdPropertyWrapper(Class<?> idClass) {
        this.idClass = idClass;
    }

    @Override
    public boolean isWritableProperty(String propertyName) {
        return TableMeta.ID.equals(propertyName);
    }

    @Override
    public void setPropertyValue(String propertyName, @Nullable Object value) throws BeansException {
        if (TableMeta.ID.equals(propertyName)) {
            if (value == null || idClass.isInstance(value)) {
                this.id = value;
            } else {
                throw new PropertyAccessException("value isn't %s type.", idClass.getName());
            }
        } else {
            throw new InvalidPropertyException(propertyName, Object.class
                    , "%s no [%s] property name.", getClass().getName(), propertyName);
        }
    }

    @Override
    public Object getWrappedInstance() {
        return this.id;
    }

    @Override
    public ReadonlyWrapper getReadonlyWrapper() {
        return this;
    }

    @Override
    public boolean isReadableProperty(String propertyName) {
        return TableMeta.ID.equals(propertyName);
    }

    @Override
    public Class<?> getPropertyType(String propertyName) throws BeansException {
        if (TableMeta.ID.equals(propertyName)) {
            return this.idClass;
        }
        throw new InvalidPropertyException(propertyName, Object.class
                , "%s no [%s] property name.", getClass().getName(), propertyName);
    }

    @Override
    public Object getPropertyValue(String propertyName) throws BeansException {
        if (TableMeta.ID.equals(propertyName)) {
            return this.id;
        }
        throw new InvalidPropertyException(propertyName, Object.class
                , "%s no [%s] property name.", getClass().getName(), propertyName);
    }

    @Override
    public Class<?> getWrappedClass() {
        return Object.class;
    }
}