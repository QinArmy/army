package io.army.env;

import org.springframework.core.env.MapPropertySource;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public class StandardEnvironment extends AbstractEnvironment {

    private final org.springframework.core.env.ConfigurableEnvironment env;


    public StandardEnvironment(ZoneId zoneId, List<String> packageToScan) {
        super(zoneId, packageToScan);
        this.env = createEnvironmentImplementation();
    }

    @Override
    public final boolean containsProperty(String key) {
        return env.containsProperty(key);
    }

    @Override
    public final String getProperty(String key) {
        return env.getProperty(key);
    }

    @Override
    public final String getProperty(String key, String defaultValue) {
        return env.getProperty(key, defaultValue);
    }

    @Override
    public final <T> T getProperty(String key, Class<T> targetType) {
        return env.getProperty(key, targetType);
    }

    @Override
    public final <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return env.getProperty(key, targetType, defaultValue);
    }

    @Override
    public final String getRequiredProperty(String key) throws IllegalStateException {
        return env.getRequiredProperty(key);
    }

    @Override
    public final <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        return env.getRequiredProperty(key, targetType);
    }

    @Override
    public <T> T getBean(String name, Class<T> beanClass) {
        return env.getProperty(name,beanClass);
    }

    @Override
    public <T> T getRequiredBean(String name, Class<T> beanClass) throws IllegalStateException {
        return env.getRequiredProperty(name,beanClass);
    }

    /*################################## blow io.army.env.ConfigurableEnvironment method ##################################*/

    @Override
    public final void addFirst(String name, Map<String, Object> propertyMap) {
        env.getPropertySources().addFirst(new MapPropertySource(name, propertyMap));
    }

    @Override
    public final void addLast(String name, Map<String, Object> propertyMap) {
        env.getPropertySources().addLast(new MapPropertySource(name, propertyMap));
    }

    @Override
    public final void addBefore(String name, Map<String, Object> propertyMap, String relativePropertyMapName) {
        env.getPropertySources().addBefore(relativePropertyMapName, new MapPropertySource(name, propertyMap));
    }

    @Override
    public final void addAfter(String name, Map<String, Object> propertyMap, String relativePropertyMapName) {
        env.getPropertySources().addAfter(relativePropertyMapName, new MapPropertySource(name, propertyMap));
    }

    org.springframework.core.env.ConfigurableEnvironment createEnvironmentImplementation() {
        return new org.springframework.core.env.StandardEnvironment();
    }

}
