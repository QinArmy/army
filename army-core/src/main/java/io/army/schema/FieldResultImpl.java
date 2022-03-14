package io.army.schema;

import io.army.meta.FieldMeta;

final class FieldResultImpl implements _FieldResult {

    static FieldResultBuilder builder() {
        return new FieldResultBuilder();
    }

    private final FieldMeta<?> field;

    private final boolean sqlType;

    private final boolean defaultExp;

    private final Boolean nullable;

    private final boolean comment;

    private FieldResultImpl(FieldResultBuilder builder) {
        this.field = builder.field;
        this.sqlType = builder.sqlType;
        this.defaultExp = builder.defaultExp;
        this.nullable = builder.nullable;

        this.comment = builder.comment;
    }

    @Override
    public FieldMeta<?> field() {
        return this.field;
    }

    @Override
    public boolean sqlType() {
        return this.sqlType;
    }

    @Override
    public boolean defaultValue() {
        return this.defaultExp;
    }

    @Override
    public boolean nullable() {
        return this.nullable;
    }

    @Override
    public boolean comment() {
        return this.comment;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FieldResultImpl{");
        sb.append("field=").append(field);
        sb.append(", sqlType=").append(sqlType);
        sb.append(", defaultExp=").append(defaultExp);
        sb.append(", nullable=").append(nullable);
        sb.append(", comment=").append(comment);
        sb.append('}');
        return sb.toString();
    }

    private static final class FieldResultBuilder implements _FieldResult.Builder {

        private FieldMeta<?> field;

        private boolean sqlType;

        private boolean defaultExp;

        private Boolean nullable;

        private boolean comment;


        @Override
        public Builder field(FieldMeta<?> field) {
            this.field = field;
            return this;
        }

        @Override
        public Builder sqlType(boolean sqlType) {
            this.sqlType = sqlType;
            return this;
        }

        @Override
        public Builder defaultExp(boolean defaultExp) {
            this.defaultExp = defaultExp;
            return this;
        }

        @Override
        public Builder nullable(boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        @Override
        public void comment(boolean comment) {
            this.comment = comment;
        }

        @Override
        public boolean hasDifference() {
            return this.sqlType || this.defaultExp || this.nullable || this.comment;
        }

        @Override
        public void clear() {
            this.field = null;
            this.sqlType = this.defaultExp = this.nullable = this.comment = false;
        }

        @Override
        public _FieldResult build() {
            return new FieldResultImpl(this);
        }


        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FieldResultBuilder{");
            sb.append("field=").append(field);
            sb.append(", sqlType=").append(sqlType);
            sb.append(", defaultExp=").append(defaultExp);
            sb.append(", nullable=").append(nullable);
            sb.append(", comment=").append(comment);
            sb.append('}');
            return sb.toString();
        }
    }//FieldResultBuilder


}
