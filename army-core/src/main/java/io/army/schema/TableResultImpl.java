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
import io.army.meta.TableMeta;
import io.army.util._Collections;

import java.util.Collections;
import java.util.List;

final class TableResultImpl implements _TableResult {

    static TableResultBuilder createBuilder() {
        return new TableResultBuilder();
    }

    private final TableMeta<?> table;

    private final List<FieldMeta<?>> newFieldList;

    private final boolean comment;

    private final List<_FieldResult> fieldResultList;

    private final List<String> newIndexList;

    private final List<String> changeIndexList;

    private TableResultImpl(TableResultBuilder builder) {
        this.table = builder.table;
        final List<FieldMeta<?>> newFieldList = builder.newFieldList;
        if (newFieldList == null) {
            this.newFieldList = Collections.emptyList();
        } else {
            this.newFieldList = _Collections.unmodifiableList(newFieldList);
        }
        this.comment = builder.comment;

        final List<_FieldResult> fieldResultList = builder.fieldResultList;
        if (fieldResultList == null) {
            this.fieldResultList = Collections.emptyList();
        } else {
            this.fieldResultList = _Collections.unmodifiableList(fieldResultList);
        }

        final List<String> newIndexList = builder.newIndexList;
        if (newIndexList == null) {
            this.newIndexList = Collections.emptyList();
        } else {
            this.newIndexList = _Collections.unmodifiableList(newIndexList);
        }

        final List<String> changeIndexList = builder.changeIndexList;
        if (changeIndexList == null) {
            this.changeIndexList = Collections.emptyList();
        } else {
            this.changeIndexList = _Collections.unmodifiableList(changeIndexList);
        }

    }

    @Override
    public TableMeta<?> table() {
        return this.table;
    }

    @Override
    public boolean comment() {
        return this.comment;
    }

    @Override
    public List<FieldMeta<?>> newFieldList() {
        return this.newFieldList;
    }

    @Override
    public List<_FieldResult> changeFieldList() {
        return this.fieldResultList;
    }

    @Override
    public List<String> newIndexList() {
        return this.newIndexList;
    }

    @Override
    public List<String> changeIndexList() {
        return this.changeIndexList;
    }


    private static final class TableResultBuilder implements _TableResult.Builder {

        private TableMeta<?> table;

        private List<FieldMeta<?>> newFieldList;

        private boolean comment;

        private List<_FieldResult> fieldResultList;

        private List<String> newIndexList;

        private List<String> changeIndexList;

        @Override
        public void table(TableMeta<?> table) {
            this.table = table;
        }

        @Override
        public void appendNewColumn(FieldMeta<?> field) {
            List<FieldMeta<?>> fieldList = this.newFieldList;
            if (fieldList == null) {
                fieldList = _Collections.arrayList();
                this.newFieldList = fieldList;
            }
            fieldList.add(field);
        }

        @Override
        public void comment(boolean comment) {
            this.comment = comment;
        }

        @Override
        public void appendFieldResult(_FieldResult fieldResult) {
            List<_FieldResult> fieldResultList = this.fieldResultList;
            if (fieldResultList == null) {
                fieldResultList = _Collections.arrayList();
                this.fieldResultList = fieldResultList;
            }
            fieldResultList.add(fieldResult);
        }

        @Override
        public void appendNewIndex(String indexName) {
            List<String> newIndexList = this.newIndexList;
            if (newIndexList == null) {
                newIndexList = _Collections.arrayList();
                this.newIndexList = newIndexList;
            }
            newIndexList.add(indexName);
        }

        @Override
        public void appendChangeIndex(String indexName) {
            List<String> changeIndexList = this.changeIndexList;
            if (changeIndexList == null) {
                changeIndexList = _Collections.arrayList();
                this.changeIndexList = changeIndexList;
            }
            changeIndexList.add(indexName);
        }

        @Override
        public _TableResult buildAndClear() {
            final _TableResult tableResult;
            tableResult = new TableResultImpl(this);

            this.table = null;
            this.newFieldList = null;
            this.fieldResultList = null;
            this.comment = false;

            this.newIndexList = null;
            this.changeIndexList = null;

            return tableResult;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TableResultBuilder{");
            sb.append("newFieldList=").append(newFieldList);
            sb.append(", comment=").append(comment);
            sb.append(", fieldResultList=").append(fieldResultList);
            sb.append(", newIndexList=").append(newIndexList);
            sb.append(", changeIndexList=").append(changeIndexList);
            sb.append('}');
            return sb.toString();
        }

    }//TableResultBuilder


}
