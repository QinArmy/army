package io.army.schema;

import io.army.lang.Nullable;

import java.util.Map;

public interface _TableInfo {

    /**
     * @return lower case table name
     */
    String name();


    @Nullable
    String comment();

    /**
     * @return map, key : column name (lower case),value: {@link _ColumnInfo}.
     */
    Map<String, _ColumnInfo> columnMap();

    Map<String, _IndexInfo> indexMap();


    static Builder builder(String tableName) {
        return TableInfoImpl.createBuilder(tableName);
    }


    interface Builder {

        String name();

        Builder type(_TableType type);

        Builder comment(@Nullable String comment);

        Builder appendColumn(_ColumnInfo column);

        Builder appendIndex(_IndexInfo indexInfo);

        _TableInfo build();

    }

}
