package io.army.schema;

import io.army.lang.Nullable;

import java.util.Map;

public interface _TableInfo {


    @Nullable
    String comment();

    Map<String, _ColumnInfo> columnMap();


    static Builder builder(String tableName) {
        throw new UnsupportedOperationException();
    }


    interface Builder {

        String name();

        Builder type(_TableType type);

        Builder comment(@Nullable String comment);

        Builder appendColumn(_ColumnInfo fieldInfo);

        Builder appendIndex(_IndexInfo indexInfo);

        _TableInfo build();

    }

}
