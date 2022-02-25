package io.army.schema;

import io.army.lang.Nullable;

import java.util.List;

public interface _IndexInfo {

    boolean unique();

    List<Boolean> ascList();

    List<String> columnList();

    static Builder builder() {
        throw new UnsupportedOperationException();
    }

    interface Builder {

        boolean isNotEmpty();

        Builder name(String name);

        Builder type(String type);

        Builder unique(boolean unique);

        Builder appendColumn(String columnName, @Nullable Boolean asc);

        _IndexInfo buildAndClear();
    }


}
