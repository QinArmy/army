package io.army.schema;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

final class _SchemaInfoImpl implements SchemaInfo {

    static _SchemaInfoImpl create(@Nullable String catalog, @Nullable String schema
            , Map<String, TableInfo.Builder> builderMap) {

        final Map<String, TableInfo> tableMap = new HashMap<>(builderMap.size());
        for (TableInfo.Builder builder : builderMap.values()) {
            //table name must lower case
            if (tableMap.putIfAbsent(builder.name().toLowerCase(Locale.ROOT), builder.build()) != null) {
                throw new IllegalArgumentException("builderMap error.");
            }
        }
        return new _SchemaInfoImpl(catalog, schema, tableMap);
    }


    private final String catalog;

    private final String schema;

    private final Map<String, TableInfo> builderMap;


    private _SchemaInfoImpl(@Nullable String catalog, @Nullable String schema, Map<String, TableInfo> tableMap) {
        this.catalog = catalog;
        this.schema = schema;
        this.builderMap = Collections.unmodifiableMap(tableMap);
    }


    @Override
    public String catalog() {
        return this.catalog;
    }

    @Override
    public String schema() {
        return this.schema;
    }

    @Override
    public Map<String, TableInfo> tableMap() {
        return this.builderMap;
    }


}
