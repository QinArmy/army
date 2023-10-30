package io.army.schema;

import javax.annotation.Nullable;

import java.util.Locale;

final class ColumnInfoImpl implements _ColumnInfo {

    static ColumnInfoBuilder createBuilder() {
        return new ColumnInfoBuilder();
    }

    private final String columnName;

    private final String typeName;

    private final int precision;

    private final int scale;

    private final String defaultExp;

    private final Boolean nullable;

    private final String comment;

    private final boolean autoincrement;

    private ColumnInfoImpl(ColumnInfoBuilder builder) {
        this.columnName = builder.columnName;
        this.typeName = builder.typeName.toUpperCase(Locale.ROOT);
        this.precision = builder.precision;
        this.scale = builder.scale;

        this.defaultExp = builder.defaultExp;
        this.nullable = builder.nullable;
        this.comment = builder.comment;
        this.autoincrement = builder.autoincrement;
    }


    @Override
    public String columnName() {
        return this.columnName;
    }

    @Override
    public String typeName() {
        return this.typeName;
    }

    @Override
    public Boolean nullable() {
        return this.nullable;
    }

    @Override
    public String comment() {
        return this.comment;
    }

    @Override
    public String defaultExp() {
        return this.defaultExp;
    }

    @Override
    public int precision() {
        return this.precision;
    }

    @Override
    public int scale() {
        return this.scale;
    }

    @Override
    public boolean autoincrement() {
        return this.autoincrement;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ColumnInfoImpl{");
        sb.append("columnName='").append(columnName).append('\'');
        sb.append(", typeName='").append(typeName).append('\'');
        sb.append(", precision=").append(precision);
        sb.append(", scale=").append(scale);
        sb.append(", defaultExp='").append(defaultExp).append('\'');
        sb.append(", nullable=").append(nullable);
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", autoincrement=").append(autoincrement);
        sb.append('}');
        return sb.toString();
    }

    private static final class ColumnInfoBuilder implements _ColumnInfo.Builder {

        private String columnName;

        private String typeName;

        private int precision;

        private int scale;

        private String defaultExp;

        private Boolean nullable;

        private String comment;

        private boolean autoincrement;


        @Override
        public Builder name(String columnName) {
            this.columnName = columnName;
            return this;
        }

        @Override
        public Builder type(String sqlType) {
            this.typeName = sqlType;
            return this;
        }

        @Override
        public Builder precision(int precision) {
            this.precision = precision;
            return this;
        }

        @Override
        public Builder scale(int scale) {
            this.scale = scale;
            return this;
        }

        @Override
        public Builder defaultExp(String defaultExp) {
            this.defaultExp = defaultExp;
            return this;
        }

        @Override
        public Builder nullable(@Nullable Boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        @Override
        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        @Override
        public Builder autoincrement(boolean autoincrement) {
            this.autoincrement = autoincrement;
            return this;
        }

        @Override
        public _ColumnInfo buildAndClear() {
            final _ColumnInfo columnInfo;
            columnInfo = new ColumnInfoImpl(this);

            this.columnName = null;
            this.typeName = null;
            this.precision = -1;
            this.scale = -1;

            this.defaultExp = null;
            this.nullable = true;
            this.comment = null;
            this.autoincrement = false;

            return columnInfo;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ColumnInfoBuilder{");
            sb.append("columnName='").append(columnName).append('\'');
            sb.append(", typeName='").append(typeName).append('\'');
            sb.append(", precision=").append(precision);
            sb.append(", scale=").append(scale);
            sb.append(", defaultExp='").append(defaultExp).append('\'');
            sb.append(", nullable=").append(nullable);
            sb.append(", comment='").append(comment).append('\'');
            sb.append(", autoincrement=").append(autoincrement);
            sb.append('}');
            return sb.toString();
        }

    }//ColumnInfoBuilder


}
