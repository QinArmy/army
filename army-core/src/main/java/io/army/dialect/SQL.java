package io.army.dialect;

import io.army.dialect.func.SQLFunc;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SQL {

    String quoteIfNeed(String text);

    boolean isKeyWord(String text);

    Map<String, List<String>> standardFunc();

    ZoneId zoneId();

}
