package io.army.schema;

import javax.annotation.Nullable;
import java.util.List;

public interface IndexInfo {

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

        @Nullable
        String name();

        Builder name(String name);

        Builder type(String type);

        Builder unique(boolean unique);

        Builder appendColumn(String columnName, @Nullable Boolean asc);

        IndexInfo buildAndClear();

    }


}
