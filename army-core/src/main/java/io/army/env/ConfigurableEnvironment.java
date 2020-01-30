package io.army.env;

import java.util.Map;

public interface ConfigurableEnvironment extends Environment{

    void addFirst(String name,Map<String,Object> propertyMap);

    void addLast(String name,Map<String,Object> propertyMap);

    void addBefore(String name,Map<String,Object> propertyMap,String relativePropertyMapName);

    void addAfter(String name,Map<String,Object> propertyMap,String relativePropertyMapName);

}
