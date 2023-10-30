package io.army.schema;

import javax.annotation.Nullable;

public interface _ColumnInfo {

    String columnName();

    /**
     * @return upper case type name.
     */
    String typeName();

    @Nullable
    String defaultExp();

    @Nullable
    Boolean nullable();

    int precision();

    int scale();

    @Nullable
    String comment();

    boolean autoincrement();

    static Builder builder() {
        return ColumnInfoImpl.createBuilder();
    }


    interface Builder {

        Builder name(String columnName);

        Builder type(String sqlType);

        Builder precision(int precision);

        Builder scale(int scale);

        Builder defaultExp(String defaultExp);

        Builder nullable(@Nullable Boolean nullable);

        Builder comment(String comment);

        Builder autoincrement(boolean autoincrement);

        _ColumnInfo buildAndClear();

    }


}
