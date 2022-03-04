package io.army.bean;

import java.util.Map;

public interface MapWrapper extends ObjectWrapper {

    Map<String, Object> getUnmodifiableMap();

}
