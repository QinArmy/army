package io.army.schema;

import io.army.lang.Nullable;

import java.util.Map;

public interface SchemaInfo {

    @Nullable
    String catalog();

    @Nullable
    String schema();

    Map<String, _TableInfo> tableMap();

    static SchemaInfo create(@Nullable String catalog, @Nullable String schema, Map<String
            , _TableInfo.Builder> builderMap) {
        return _SchemaInfoImpl.create(catalog, schema, builderMap);
    }

}
