package io.army.beans;


import io.army.ErrorCode;
import io.army.util.BeanUtils;
import io.qinarmy.util.Pair;

import java.lang.reflect.Constructor;

final class PairBeanWrapperImpl implements PairWrapper {

    static final String FIRST = "first";

    static final String SECOND = "second";

    private final Class<?> pairClass;

    private Object first;

    private Object second;

    PairBeanWrapperImpl(Class<?> pairClass) {
        this.pairClass = pairClass;
    }

    @Override
    public boolean isWritableProperty(String propertyName) {
        return FIRST.equals(propertyName) || SECOND.equals(propertyName);
    }

    @Override
    public void set(String propertyName, Object value) throws BeansException {
        if (FIRST.equals(propertyName)) {
            this.first = value;
        } else if (SECOND.equals(propertyName)) {
            this.second = value;
        } else {
            throw new InvalidPropertyException(ErrorCode.BEAN_ACCESS_ERROR, propertyName
                    , Pair.class, "not found property[%s]", propertyName);
        }
    }

    @Override
    public Object getWrappedInstance() throws BeansException {
        try {
            Constructor<?> constructor = pairClass.getConstructor(Object.class, Object.class);
            constructor.newInstance(this.first, this.second);

            return BeanUtils.instantiateClass(constructor, this.first, this.second);
        } catch (Exception e) {
            throw new BeansException(ErrorCode.BEAN_ACCESS_ERROR, e, "can't create instance of %s", pairClass.getName());
        }
    }

    @Override
    public ReadWrapper getReadonlyWrapper() {
        return this;
    }

    @Override
    public boolean isReadable(String propertyName) {
        return FIRST.equals(propertyName) || SECOND.equals(propertyName);
    }

    @Override
    public Class<?> getType(String propertyName) throws BeansException {
        Class<?> clazz = Object.class;
        if (FIRST.equals(propertyName)) {
            if (this.first != null) {
                clazz = this.first.getClass();
            }
        } else if (SECOND.equals(propertyName)) {
            if (this.second != null) {
                clazz = this.second.getClass();
            }
        } else {
            throw new InvalidPropertyException(ErrorCode.BEAN_ACCESS_ERROR, propertyName
                    , Pair.class, "not found property[%s]", propertyName);
        }
        return clazz;
    }

    @Override
    public Object get(String propertyName) throws BeansException {
        Object value;
        if (FIRST.equals(propertyName)) {
            value = this.first;
        } else if (SECOND.equals(propertyName)) {
            value = this.second;
        } else {
            throw new InvalidPropertyException(ErrorCode.BEAN_ACCESS_ERROR, propertyName
                    , Pair.class, "not found property[%s]", propertyName);
        }
        return value;
    }

    @Override
    public Class<?> getWrappedClass() {
        return pairClass;
    }
}
