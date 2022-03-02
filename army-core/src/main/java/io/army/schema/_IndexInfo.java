package io.army.schema;

import io.army.lang.Nullable;

import java.util.List;

public interface _IndexInfo {

    String indexName();

    @Nullable
    String indexType();

    boolean unique();

    List<Boolean> ascList();

    List<String> columnList();

    static Builder builder() {
        return IndexInfoImpl.createBuilder();
    }

    interface Builder {

        Builder name(String name);

        Builder type(String type);

        Builder unique(boolean unique);

        Builder appendColumn(String columnName, @Nullable Boolean asc);

        _IndexInfo buildAndClear();

    }


}
