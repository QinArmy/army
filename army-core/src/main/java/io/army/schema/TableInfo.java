package io.army.schema;

import javax.annotation.Nullable;
import java.util.Map;

public interface TableInfo {

    /**
     * @return lower case table name
     */
    String name();


    @Nullable
    String comment();

    /**
     * @return map, key : column name (lower case),value: {@link ColumnInfo}.
     */
    Map<String, ColumnInfo> columnMap();

    Map<String, IndexInfo> indexMap();


    static Builder builder(String tableName) {
        return TableInfoImpl.createBuilder(tableName);
    }


    interface Builder {

        String name();

        Builder type(TableType type);

        Builder comment(@Nullable String comment);

        Builder appendColumn(ColumnInfo column);

        Builder appendIndex(IndexInfo indexInfo);

        TableInfo build();

    }

}
