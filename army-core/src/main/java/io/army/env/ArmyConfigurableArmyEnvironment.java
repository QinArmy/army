package io.army.env;

import io.army.bean.ArmyBean;

import java.util.Map;

public interface ArmyConfigurableArmyEnvironment extends ArmyEnvironment {

    void addFirst(String name, Map<String, Object> propertyMap);

    void addLast(String name, Map<String, Object> propertyMap);

    void addBefore(String name, Map<String, Object> propertyMap, String relativePropertyMapName);

    void addAfter(String name, Map<String, Object> propertyMap, String relativePropertyMapName);

    void addBean(String beanName, ArmyBean bean);

    void addAllBean(Map<String, ArmyBean> beanMap);

    void addBeansIfNotExists(Map<String, ArmyBean> beanMap);

}
