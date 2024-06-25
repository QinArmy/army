/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.schema;

import io.army.meta.FieldMeta;

final class FieldResultImpl implements FieldResult {

    static FieldResultBuilder builder() {
        return new FieldResultBuilder();
    }

    private final FieldMeta<?> field;

    private final boolean sqlType;

    private final boolean defaultExp;

    private final boolean notNull;

    private final boolean comment;

    private FieldResultImpl(FieldResultBuilder builder) {
        this.field = builder.field;
        this.sqlType = builder.sqlType;
        this.defaultExp = builder.defaultExp;
        this.notNull = builder.notNull;

        this.comment = builder.comment;
    }

    @Override
    public FieldMeta<?> field() {
        return this.field;
    }

    @Override
    public boolean containSqlType() {
        return this.sqlType;
    }

    @Override
    public boolean containDefault() {
        return this.defaultExp;
    }

    @Override
    public boolean containNotNull() {
        return this.notNull;
    }

    @Override
    public boolean containComment() {
        return this.comment;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FieldResultImpl{");
        sb.append("field=").append(field);
        sb.append(", sqlType=").append(sqlType);
        sb.append(", defaultExp=").append(defaultExp);
        sb.append(", nullable=").append(notNull);
        sb.append(", comment=").append(comment);
        sb.append('}');
        return sb.toString();
    }

    private static final class FieldResultBuilder implements FieldResult.Builder {

        private FieldMeta<?> field;

        private boolean sqlType;

        private boolean defaultExp;

        private boolean notNull;

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
        public Builder notNull(boolean notNull) {
            this.notNull = notNull;
            return this;
        }

        @Override
        public Builder comment(boolean comment) {
            this.comment = comment;
            return this;
        }

        @Override
        public boolean hasDifference() {
            return this.sqlType || this.defaultExp || this.notNull || this.comment;
        }

        @Override
        public void clear() {
            this.field = null;
            this.sqlType = this.defaultExp = this.notNull = this.comment = false;
        }

        @Override
        public FieldResult build() {
            return new FieldResultImpl(this);
        }


        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FieldResultBuilder{");
            sb.append("field=").append(field);
            sb.append(", sqlType=").append(sqlType);
            sb.append(", defaultExp=").append(defaultExp);
            sb.append(", nullable=").append(notNull);
            sb.append(", comment=").append(comment);
            sb.append('}');
            return sb.toString();
        }
    }//FieldResultBuilder


}
