package io.army.schema;

import io.army.lang.Nullable;

public interface _TableInfo {


    static Builder builder(String tableName) {
        throw new UnsupportedOperationException();
    }


    interface Builder {

        String name();

        Builder type(_TableType type);

        Builder comment(@Nullable String comment);

        Builder appendField(_FieldInfo fieldInfo);


    }

}
