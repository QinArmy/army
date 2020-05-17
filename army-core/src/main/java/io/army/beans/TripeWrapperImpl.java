package io.army.beans;

import io.army.ErrorCode;
import io.army.util.BeanUtils;
import io.army.util.Pair;

import java.lang.reflect.Constructor;

final class TripeWrapperImpl implements TripeWrapper {

    private static final String FIRST = PairBeanWrapperImpl.FIRST;

    private static final String SECOND = PairBeanWrapperImpl.SECOND;

    private static final String THIRD = "third";

    private final Class<?> tripeClass;

    private Object first;

    private Object second;

    private Object third;

    public TripeWrapperImpl(Class<?> tripeClass) {
        this.tripeClass = tripeClass;
    }

    @Override
    public Object getWrappedInstance() throws BeansException {
        try {
            Constructor<?> constructor = tripeClass.getConstructor(Object.class, Object.class, Object.class);
            constructor.newInstance(this.first, this.second, this.third);

            return BeanUtils.instantiateClass(constructor, this.first, this.second, this.third);
        } catch (Exception e) {
            throw new BeansException(ErrorCode.BEAN_ACCESS_ERROR, e, "can't create instance of %s", tripeClass.getName());
        }
    }

    @Override
    public boolean isWritableProperty(String propertyName) {
        return FIRST.equals(propertyName) || SECOND.equals(propertyName) || THIRD.equals(propertyName);
    }

    @Override
    public void setPropertyValue(String propertyName, Object value) throws BeansException {
        if (FIRST.equals(propertyName)) {
            this.first = value;
        } else if (SECOND.equals(propertyName)) {
            this.second = value;
        } else if (THIRD.equals(propertyName)) {
            this.third = value;
        } else {
            throw new InvalidPropertyException(ErrorCode.BEAN_ACCESS_ERROR, propertyName
                    , Pair.class, "not found property[%s]", propertyName);
        }
    }

    @Override
    public ReadonlyWrapper getReadonlyWrapper() {
        return this;
    }

    @Override
    public boolean isReadableProperty(String propertyName) {
        return FIRST.equals(propertyName) || SECOND.equals(propertyName) || THIRD.equals(propertyName);
    }

    @Override
    public Class<?> getPropertyType(String propertyName) throws BeansException {
        Class<?> clazz = Object.class;
        if (FIRST.equals(propertyName)) {
            if (this.first != null) {
                clazz = this.first.getClass();
            }
        } else if (SECOND.equals(propertyName)) {
            if (this.second != null) {
                clazz = this.second.getClass();
            }
        } else if (THIRD.equals(propertyName)) {
            if (this.third != null) {
                clazz = this.third.getClass();
            }
        } else {
            throw new InvalidPropertyException(ErrorCode.BEAN_ACCESS_ERROR, propertyName
                    , Pair.class, "not found property[%s]", propertyName);
        }
        return clazz;
    }

    @Override
    public Object getPropertyValue(String propertyName) throws BeansException {
        Object value;
        if (FIRST.equals(propertyName)) {
            value = this.first;
        } else if (SECOND.equals(propertyName)) {
            value = this.second;
        } else if (THIRD.equals(propertyName)) {
            value = this.third;
        } else {
            throw new InvalidPropertyException(ErrorCode.BEAN_ACCESS_ERROR, propertyName
                    , Pair.class, "not found property[%s]", propertyName);
        }
        return value;
    }

    @Override
    public Class<?> getWrappedClass() {
        return tripeClass;
    }
}
