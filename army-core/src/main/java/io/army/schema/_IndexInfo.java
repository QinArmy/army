package io.army.schema;

import io.army.lang.Nullable;

public interface _IndexInfo {

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
