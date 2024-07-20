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

import io.army.util._Collections;

import io.army.lang.Nullable;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

final class TableInfoImpl implements TableInfo {

    static TableInfoBuilder createBuilder(boolean reactive, String name) {
        return new TableInfoBuilder(reactive, name);
    }

    private final String name;

    private final TableType tableType;

    private final String comment;

    private final Map<String, ColumnInfo> columnMap;

    private final Map<String, IndexInfo> indexMap;

    private TableInfoImpl(TableInfoBuilder builder) {
        this.name = builder.name.toLowerCase(Locale.ROOT);
        this.tableType = builder.tableType;
        this.comment = builder.comment;
        this.columnMap = Collections.unmodifiableMap(builder.columnMap);

        final Map<String, IndexInfo> indexMap = builder.indexMap;
        if (indexMap == null) {
            this.indexMap = Collections.emptyMap();
        } else {
            this.indexMap = Collections.unmodifiableMap(indexMap);
        }

    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String comment() {
        return this.comment;
    }

    @Override
    public Map<String, ColumnInfo> columnMap() {
        return this.columnMap;
    }

    @Override
    public Map<String, IndexInfo> indexMap() {
        return this.indexMap;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TableInfoImpl{");
        sb.append("name='").append(name).append('\'');
        sb.append(", tableType=").append(tableType);
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", columnMap=").append(columnMap);
        sb.append(", indexMap=").append(indexMap);
        sb.append('}');
        return sb.toString();
    }

    private static final class TableInfoBuilder implements TableInfo.Builder {

        private final String name;

        private TableType tableType;

        private String comment;

        private Map<String, ColumnInfo> columnMap;

        private Map<String, IndexInfo> indexMap;

        private TableInfoBuilder(boolean reactive, String name) {
            if (reactive) {
                this.columnMap = _Collections.concurrentHashMap();
                this.indexMap = _Collections.concurrentHashMap();
            }
            this.name = name;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Builder type(TableType type) {
            this.tableType = type;
            return this;
        }

        @Override
        public Builder comment(@Nullable String comment) {
            this.comment = comment;
            return this;
        }

        @Override
        public Builder appendColumn(ColumnInfo column) {
            Map<String, ColumnInfo> columnMap = this.columnMap;
            if (columnMap == null) {
                this.columnMap = columnMap = _Collections.hashMap();
            }
            columnMap.put(column.columnName().toLowerCase(), column);
            return this;
        }

        @Override
        public Builder appendIndex(IndexInfo indexInfo) {
            Map<String, IndexInfo> indexMap = this.indexMap;
            if (indexMap == null) {
                this.indexMap = indexMap = _Collections.hashMap();
            }
            indexMap.put(indexInfo.indexName().toLowerCase(Locale.ROOT), indexInfo);
            return this;
        }

        @Override
        public TableInfo build() {
            return new TableInfoImpl(this);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TableInfoBuilder{");
            sb.append("name='").append(name).append('\'');
            sb.append(", tableType=").append(tableType);
            sb.append(", comment='").append(comment).append('\'');
            sb.append('}');
            return sb.toString();
        }

    }//TableInfoBuilder


}
