package io.army.stmt;


import io.army.codec.StatementType;
import io.army.criteria.Selection;
import io.army.util._Assert;

import java.util.Collections;
import java.util.List;

final class SimpleStmtImpl implements SimpleStmt {

    private final String sql;

    private final List<ParamValue> paramList;

    private final boolean hasVersion;

    private final List<Selection> selectionList;

    private final StatementType statementType;

    public SimpleStmtImpl(String sql, List<ParamValue> paramList, boolean hasVersion
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
    public int getTimeout() {
        return 0;
    }

    @Override
    public final String sql() {
        return sql;
    }

    @Override
    public final List<ParamValue> paramGroup() {
        return paramList;
    }

    @Override
    public final boolean hasOptimistic() {
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


    static final class Builder implements SimpleStmt.Builder {

        private String sql;

        private List<ParamValue> paramList;

        private boolean hasVersion;

        private List<Selection> selectionList;

        private StatementType statementType;

        @Override
        public SimpleStmt.Builder sql(String sql) {
            this.sql = sql;
            return this;
        }

        @Override
        public SimpleStmt.Builder paramList(List<ParamValue> paramList) {
            this.paramList = paramList;
            return this;
        }

        @Override
        public SimpleStmt.Builder statementType(StatementType statementType) {
            this.statementType = statementType;
            return this;
        }

        @Override
        public SimpleStmt.Builder selectionList(List<Selection> selectionList) {
            this.selectionList = selectionList;
            return this;
        }

        @Override
        public SimpleStmt.Builder hasVersion(boolean hasVersion) {
            this.hasVersion = hasVersion;
            return this;
        }

        @Override
        public SimpleStmt build() {
            _Assert.hasText(this.sql, "sql required");
            _Assert.notNull(this.statementType, "statementType required");

            List<ParamValue> paramList = this.paramList;
            if (paramList == null) {
                paramList = Collections.emptyList();
            }
            List<Selection> selectionList = this.selectionList;
            if (selectionList == null) {
                selectionList = Collections.emptyList();
            }
            return new SimpleStmtImpl(this.sql, paramList, this.hasVersion, selectionList, this.statementType);
        }
    }
}
