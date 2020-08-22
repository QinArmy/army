package io.army.beans;

import java.util.Map;

public interface MapWrapper extends BeanWrapper {

    Map<String, Object> getUnmodifiableMap();

}
