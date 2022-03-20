package io.army.env;

import io.army.bean.ArmyBean;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SpringArmyEnvironment implements ArmyEnvironment {

    private final String prefix;

    private final org.springframework.core.env.Environment env;

    public SpringArmyEnvironment(String factoryName, Environment env) {
        this.prefix = "army." + factoryName + ".";
        this.env = env;
    }


    @Override
    public boolean containsProperty(String key) {
        return this.env.containsProperty(this.prefix + key);
    }

    @Override
    public boolean containsValue(String key, String targetValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOn(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOff(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends ArmyBean> T getBean(String name, Class<T> beanClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends ArmyBean> T getRequiredBean(String name, Class<T> beanClass) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ArmyBean> getAllBean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOffDuration(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOnDuration(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T get(MyKey<T> key) {
        return this.env.getProperty(this.prefix + key.name, key.javaType);
    }

    @Override
    public <T> T getOrDefault(final MyKey<T> key) {
        T value;
        value = this.env.getProperty(this.prefix + key.name, key.javaType);
        if (value == null) {
            value = key.defaultValue;
            if (value == null) {
                throw new IllegalArgumentException(String.format("%s no default value.", key.name));
            }
        }
        return value;
    }

    @Override
    public String get(String key) {
        return this.env.getProperty(this.prefix + key);
    }

    @Override
    public String get(String key, String defaultValue) {
        return this.env.getProperty(this.prefix + key, defaultValue);
    }

    @Override
    public <T> T get(String key, Class<T> targetType) {
        return this.env.getProperty(this.prefix + key, targetType);
    }

    @Override
    public <T> T getNonNull(String key, Class<T> resultClass) {
        return this.env.getRequiredProperty(this.prefix + key, resultClass);
    }

    @Override
    public <T> List<T> getPropertyList(String key, Class<T[]> targetArrayType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> getPropertyList(String key, Class<T[]> targetArrayType, List<T> defaultList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Set<T> getPropertySet(String key, Class<T[]> targetArrayType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Set<T> getPropertySet(String key, Class<T[]> targetArrayType, Set<T> defaultSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T get(String key, Class<T> resultClass, T defaultValue) {
        return this.env.getProperty(this.prefix + key, resultClass, defaultValue);
    }

    @Override
    public String getNonNull(String key) throws IllegalStateException {
        return this.env.getRequiredProperty(this.prefix + key);
    }


    @Override
    public <T> List<T> getList(String key, Class<T> elementClass) throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Set<T> getRequiredPropertySet(String key, Class<T[]> targetArrayType) throws IllegalStateException {
        throw new UnsupportedOperationException();
    }


}
