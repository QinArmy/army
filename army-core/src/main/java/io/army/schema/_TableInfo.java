package io.army.schema;

import io.army.lang.Nullable;

import java.util.Map;

public interface _TableInfo {


    @Nullable
    String comment();

    /**
     * @return map, key : column name (lower case),value: {@link _ColumnInfo}.
     */
    Map<String, _ColumnInfo> columnMap();

    Map<String, _IndexInfo> indexMap();


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
