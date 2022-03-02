package io.army.env;

import io.army.beans.ArmyBean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StandardEnvironment extends AbstractArmyEnvironment {

    protected final Environment env;

    protected final ConcurrentMap<String, ArmyBean> beanMap = new ConcurrentHashMap<>();


    public StandardEnvironment() {
        this(new org.springframework.core.env.StandardEnvironment());
    }

    StandardEnvironment(Environment env) {
        this.env = env;
    }


    @Override
    public final boolean containsProperty(String key) {
        return env.containsProperty(key);
    }

    @Override
    public final String get(String key) {
        return env.getProperty(key);
    }

    @Override
    public final String get(String key, String defaultValue) {
        return env.getProperty(key, defaultValue);
    }

    @Override
    public final <T> T get(String key, Class<T> targetType) {
        return env.getProperty(key, targetType);
    }

    @Override
    public <T> T getNonNull(String key, Class<T> resultClass) {
        return null;
    }

    @Override
    public final <T> T get(String key, Class<T> targetType, T defaultValue) {
        return env.getProperty(key, targetType, defaultValue);
    }

    @Override
    public final String getNonNull(String key) throws IllegalStateException {
        return env.getRequiredProperty(key);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T extends ArmyBean> T getBean(String name, Class<T> beanClass) {
        ArmyBean armyBean = this.beanMap.get(name);
        return beanClass.isInstance(armyBean) ? (T) armyBean : null;
    }

    @Override
    public <T extends ArmyBean> T getRequiredBean(String name, Class<T> beanClass) throws BeansException {
        T t = getBean(name, beanClass);
        if (t == null) {
            throw new BeansException("NoSuchBean name[%s],Class[%s]", name, beanClass.getName());
        }
        return t;
    }

    @Override
    public Map<String, ArmyBean> getAllBean() {
        return Collections.unmodifiableMap(this.beanMap);
    }

    /*################################## blow io.army.env.ArmyConfigurableArmyEnvironment method ##################################*/

    @Override
    public final void addFirst(String name, Map<String, Object> propertyMap) {
        obtainConfigurableEnvironment().getPropertySources().addFirst(new MapPropertySource(name, propertyMap));
    }

    @Override
    public final void addLast(String name, Map<String, Object> propertyMap) {
        obtainConfigurableEnvironment().getPropertySources().addLast(new MapPropertySource(name, propertyMap));
    }

    @Override
    public final void addBefore(String name, Map<String, Object> propertyMap, String relativePropertyMapName) {
        obtainConfigurableEnvironment().getPropertySources()
                .addBefore(relativePropertyMapName, new MapPropertySource(name, propertyMap));
    }

    @Override
    public final void addAfter(String name, Map<String, Object> propertyMap, String relativePropertyMapName) {
        obtainConfigurableEnvironment().getPropertySources()
                .addAfter(relativePropertyMapName, new MapPropertySource(name, propertyMap));
    }

    @Override
    public void addBean(String beanName, ArmyBean bean) {
        if (this.beanMap.putIfAbsent(beanName, bean) != null) {
            throw new IllegalStateException(String.format("ArmyBean[%s] already exists.", beanName));
        }
    }

    @Override
    public void addAllBean(Map<String, ArmyBean> beanMap) {
        for (Map.Entry<String, ArmyBean> e : beanMap.entrySet()) {
            addBean(e.getKey(), e.getValue());
        }
    }

    @Override
    public void addBeansIfNotExists(Map<String, ArmyBean> beanMap) {
        for (Map.Entry<String, ArmyBean> e : beanMap.entrySet()) {
            this.beanMap.putIfAbsent(e.getKey(), e.getValue());
        }
    }

    protected ConfigurableEnvironment obtainConfigurableEnvironment() {
        return (ConfigurableEnvironment) this.env;
    }


}
