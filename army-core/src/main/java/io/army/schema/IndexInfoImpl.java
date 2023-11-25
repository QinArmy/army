package io.army.schema;

import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

final class IndexInfoImpl implements IndexInfo {

    static IndexInfoBuilder createBuilder() {
        return new IndexInfoBuilder();
    }

    private final String indexName;

    private final String indexType;

    private final boolean unique;

    private final List<Boolean> ascList;

    private final List<String> columnList;

    private IndexInfoImpl(IndexInfoBuilder builder) {
        this.indexName = builder.indexName.toLowerCase(Locale.ROOT);
        this.indexType = builder.indexType;
        this.unique = builder.unique;
        this.ascList = Collections.unmodifiableList(builder.ascList);

        this.columnList = Collections.unmodifiableList(builder.columnList);
    }

    @Override
    public String indexName() {
        return this.indexName;
    }

    @Override
    public String indexType() {
        return this.indexType;
    }

    @Override
    public boolean unique() {
        return this.unique;
    }

    @Override
    public List<Boolean> ascList() {
        return this.ascList;
    }

    @Override
    public List<String> columnList() {
        return this.columnList;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IndexInfoImpl{");
        sb.append("indexName='").append(indexName).append('\'');
        sb.append(", indexType='").append(indexType).append('\'');
        sb.append(", unique=").append(unique);
        sb.append(", ascList=").append(ascList);
        sb.append(", columnList=").append(columnList);
        sb.append('}');
        return sb.toString();
    }

    private static final class IndexInfoBuilder implements IndexInfo.Builder {

        @Override
        public String name() {
            return this.indexName;
        }

        private String indexName;

        private String indexType;

        private boolean unique;

        private List<Boolean> ascList;

        private List<String> columnList;

        @Override
        public Builder name(String name) {
            this.indexName = name;
            return this;
        }

        @Override
        public Builder type(String type) {
            this.indexType = type;
            return this;
        }

        @Override
        public Builder unique(boolean unique) {
            this.unique = unique;
            return this;
        }

        @Override
        public Builder appendColumn(String columnName, @Nullable Boolean asc) {
            List<String> columnList = this.columnList;
            if (columnList == null) {
                this.columnList = columnList = _Collections.arrayList();
            }
            List<Boolean> ascList = this.ascList;
            if (ascList == null) {
                this.ascList = ascList = _Collections.arrayList();
            }

            columnList.add(columnName);
            ascList.add(asc);
            return this;
        }

        @Override
        public IndexInfo buildAndClear() {
            final IndexInfo indexInfo;
            indexInfo = new IndexInfoImpl(this);

            this.indexName = null;
            this.indexType = null;
            this.unique = false;
            this.ascList = null;

            this.columnList = null;

            return indexInfo;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("IndexInfoBuilder{");
            sb.append("indexName='").append(indexName).append('\'');
            sb.append(", indexType='").append(indexType).append('\'');
            sb.append(", unique=").append(unique);
            sb.append(", ascList=").append(ascList);
            sb.append(", columnList=").append(columnList);
            sb.append('}');
            return sb.toString();
        }
    }//IndexInfoBuilder


}
