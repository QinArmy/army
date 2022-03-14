package io.army.bean;

import io.army.ErrorCode;
import io.qinarmy.util.Pair;


final class SimpleTypeWrapper implements ObjectWrapper {

    private final Class<?> simpleType;

    private final String propName;

    private Object value;

    SimpleTypeWrapper(Class<?> simpleType, String propName) {
        this.simpleType = simpleType;
        this.propName = propName;
    }

    @Override
    public boolean isWritableProperty(String propertyName) {
        return this.propName.equals(propertyName);
    }

    @Override
    public void set(String propertyName, Object value) throws BeansException {
        if (this.propName.equals(propertyName)) {
            if (value == null || simpleType.isInstance(value)) {
                this.value = value;
            } else {
                throw new PropertyAccessException(ErrorCode.BEAN_ACCESS_ERROR
                        , "value[%s] isn't %s type.", simpleType.getName());
            }
        } else {
            throw new InvalidPropertyException(ErrorCode.BEAN_ACCESS_ERROR, propertyName
                    , Pair.class, "not found property[%s]", propertyName);
        }
    }

    @Override
    public Object getWrappedInstance() {
        return this.value;
    }

    @Override
    public ReadWrapper getReadonlyWrapper() {
        return this;
    }

    @Override
    public boolean isReadable(String propertyName) {
        return this.propName.equals(propertyName);
    }

    @Override
    public Class<?> getType(String propertyName) throws BeansException {
        if (this.propName.equals(propertyName)) {
            return this.simpleType;
        }
        throw new PropertyAccessException(ErrorCode.BEAN_ACCESS_ERROR, "can't access property name[%s]", this.propName);
    }

    @Override
    public Object get(String propertyName) throws BeansException {
        if (this.propName.equals(propertyName)) {
            return this.value;
        } else {
            throw new InvalidPropertyException(ErrorCode.BEAN_ACCESS_ERROR, propertyName
                    , Pair.class, "not found property[%s]", propertyName);
        }
    }

    @Override
    public Class<?> getWrappedClass() {
        return this.simpleType;
    }
}
