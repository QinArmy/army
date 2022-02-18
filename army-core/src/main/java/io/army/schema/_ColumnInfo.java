package io.army.schema;

public interface _ColumnInfo {

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
