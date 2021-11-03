package io.army.stmt;

import io.army.codec.StatementType;
import io.army.util.Assert;

import java.util.Collections;
import java.util.List;

final class BatchSimpleStmtImpl implements BatchSimpleStmt {

    private final String sql;

    private final List<List<ParamValue>> paramGroupList;

    private final StatementType statementType;

    private final boolean hasVersion;


    BatchSimpleStmtImpl(String sql, List<List<ParamValue>> paramGroupList, StatementType statementType
            , boolean hasVersion) {
        this.sql = sql;
        this.paramGroupList = Collections.unmodifiableList(paramGroupList);
        this.statementType = statementType;
        this.hasVersion = hasVersion;
    }

    @Override
    public final String sql() {
        return this.sql;
    }

    @Override
    public final List<List<ParamValue>> groupList() {
        return this.paramGroupList;
    }

    @Override
    public final boolean hasVersion() {
        return this.hasVersion;
    }

    @Override
    public final StatementType statementType() {
        return this.statementType;
    }

    static final class Builder implements BatchSimpleStmt.Builder {

        private String sql;

        private List<List<ParamValue>> paramGroupList;

        private StatementType statementType;

        private boolean hasVersion;

        @Override
        public BatchSimpleStmt.Builder sql(String sql) {
            this.sql = sql;
            return this;
        }

        @Override
        public BatchSimpleStmt.Builder paramGroupList(List<List<ParamValue>> paramGroupList) {
            this.paramGroupList = paramGroupList;
            return this;
        }

        @Override
        public BatchSimpleStmt.Builder statementType(StatementType statementType) {
            this.statementType = statementType;
            return this;
        }

        @Override
        public BatchSimpleStmt.Builder hasVersion(boolean hasVersion) {
            this.hasVersion = hasVersion;
            return this;
        }

        @Override
        public BatchSimpleStmt build() {
            Assert.hasText(this.sql, "sql required");
            Assert.notNull(this.paramGroupList, "paramGroupList required");
            Assert.notNull(this.statementType, "statementType required");
            return new BatchSimpleStmtImpl(this.sql, this.paramGroupList, this.statementType, this.hasVersion);
        }
    }
}
