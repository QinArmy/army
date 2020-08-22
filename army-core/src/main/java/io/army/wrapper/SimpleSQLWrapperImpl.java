package io.army.wrapper;


import io.army.codec.StatementType;
import io.army.criteria.Selection;
import io.army.util.Assert;

import java.util.Collections;
import java.util.List;

final class SimpleSQLWrapperImpl implements SimpleSQLWrapper {

    private final String sql;

    private final List<ParamWrapper> paramList;

    private final boolean hasVersion;

    private final List<Selection> selectionList;

    private final StatementType statementType;

    public SimpleSQLWrapperImpl(String sql, List<ParamWrapper> paramList, boolean hasVersion
            , List<Selection> selectionList, StatementType statementType) {
        this.sql = sql;
        this.paramList = paramList.isEmpty()
                ? Collections.emptyList()
                : Collections.unmodifiableList(paramList);
        this.hasVersion = hasVersion;
        this.selectionList = paramList.isEmpty()
                ? Collections.emptyList()
                : Collections.unmodifiableList(selectionList);
        this.statementType = statementType;
    }

    @Override
    public final String sql() {
        return sql;
    }

    @Override
    public final List<ParamWrapper> paramList() {
        return paramList;
    }

    @Override
    public final boolean hasVersion() {
        return this.hasVersion;
    }

    @Override
    public final List<Selection> selectionList() {
        return this.selectionList;
    }

    @Override
    public final StatementType statementType() {
        return this.statementType;
    }


    static final class Builder implements SimpleSQLWrapper.Builder {

        private String sql;

        private List<ParamWrapper> paramList;

        private boolean hasVersion;

        private List<Selection> selectionList;

        private StatementType statementType;

        @Override
        public SimpleSQLWrapper.Builder sql(String sql) {
            this.sql = sql;
            return this;
        }

        @Override
        public SimpleSQLWrapper.Builder paramList(List<ParamWrapper> paramList) {
            this.paramList = paramList;
            return this;
        }

        @Override
        public SimpleSQLWrapper.Builder statementType(StatementType statementType) {
            this.statementType = statementType;
            return this;
        }

        @Override
        public SimpleSQLWrapper.Builder selectionList(List<Selection> selectionList) {
            this.selectionList = selectionList;
            return this;
        }

        @Override
        public SimpleSQLWrapper.Builder hasVersion(boolean hasVersion) {
            this.hasVersion = hasVersion;
            return this;
        }

        @Override
        public SimpleSQLWrapper build() {
            Assert.hasText(this.sql, "sql required");
            Assert.notNull(this.statementType, "statementType required");

            List<ParamWrapper> paramList = this.paramList;
            if (paramList == null) {
                paramList = Collections.emptyList();
            }
            List<Selection> selectionList = this.selectionList;
            if (selectionList == null) {
                selectionList = Collections.emptyList();
            }
            return new SimpleSQLWrapperImpl(this.sql, paramList, this.hasVersion, selectionList, this.statementType);
        }
    }
}
