package io.army.schema;

import javax.annotation.Nullable;
import java.util.Map;

public interface SchemaInfo {

    @Nullable
    String catalog();

    @Nullable
    String schema();

    Map<String, TableInfo> tableMap();

    static SchemaInfo create(@Nullable String catalog, @Nullable String schema,
                             Map<String, TableInfo.Builder> builderMap) {
        return _SchemaInfoImpl.create(catalog, schema, builderMap);
    }

}
