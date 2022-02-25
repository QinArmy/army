package io.army.schema;

import io.army.lang.Nullable;

public interface _ColumnInfo {

    /**
     * @return upper case type name.
     */
    String typeName();

    boolean nullable();

    @Nullable
    String comment();

    static Builder builder() {
        throw new UnsupportedOperationException();
    }


    interface Builder {

        Builder name(String fieldName);

        Builder type(String sqlType);

        Builder precision(int precision);

        Builder scale(int scale);

        Builder defaultExp(String defaultExp);

        Builder nullable(boolean nullable);

        Builder comment(String comment);

        Builder autoincrement(boolean autoincrement);

        _ColumnInfo buildAndClear();

    }


}
