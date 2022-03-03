package io.army.schema;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

final class TableInfoImpl implements _TableInfo {

    static TableInfoBuilder createBuilder(String name) {
        return new TableInfoBuilder(name);
    }

    private final String name;

    private final _TableType tableType;

    private final String comment;

    private final Map<String, _ColumnInfo> columnMap;

    private final Map<String, _IndexInfo> indexMap;

    private TableInfoImpl(TableInfoBuilder builder) {
        this.name = builder.name.toLowerCase(Locale.ROOT);
        this.tableType = builder.tableType;
        this.comment = builder.comment;
        this.columnMap = Collections.unmodifiableMap(builder.columnMap);

        final Map<String, _IndexInfo> indexMap = builder.indexMap;
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
    public Map<String, _ColumnInfo> columnMap() {
        return this.columnMap;
    }

    @Override
    public Map<String, _IndexInfo> indexMap() {
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

    private static final class TableInfoBuilder implements _TableInfo.Builder {

        private final String name;

        private _TableType tableType;

        private String comment;

        private Map<String, _ColumnInfo> columnMap = new HashMap<>();

        private Map<String, _IndexInfo> indexMap;

        private TableInfoBuilder(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Builder type(_TableType type) {
            this.tableType = type;
            return this;
        }

        @Override
        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        @Override
        public Builder appendColumn(_ColumnInfo column) {
            this.columnMap.put(column.columnName().toLowerCase(), column);
            return this;
        }

        @Override
        public Builder appendIndex(_IndexInfo indexInfo) {
            Map<String, _IndexInfo> indexMap = this.indexMap;
            if (indexMap == null) {
                indexMap = new HashMap<>();
                this.indexMap = indexMap;
            }
            indexMap.put(indexInfo.indexName().toLowerCase(Locale.ROOT), indexInfo);
            return this;
        }

        @Override
        public _TableInfo build() {
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
