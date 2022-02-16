package io.army.schema;

import java.util.Map;

public interface _SchemaInfo {

    String catalog();

    String schema();

    Map<String, _TableInfo> tableMap();
}
