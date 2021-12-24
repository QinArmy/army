package io.army.stmt;

import io.army.codec.StatementType;
import io.army.util._Assert;

import java.util.Collections;
import java.util.List;

final class BatchStmtImpl implements BatchStmt {

    private final String sql;

    private final List<List<ParamValue>> paramGroupList;

    private final StatementType statementType;

    private final boolean hasVersion;


    BatchStmtImpl(String sql, List<List<ParamValue>> paramGroupList, StatementType statementType
            , boolean hasVersion) {
        this.sql = sql;
        this.paramGroupList = Collections.unmodifiableList(paramGroupList);
        this.statementType = statementType;
        this.hasVersion = hasVersion;
    }

    @Override
    public int getTimeout() {
        return 0;
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

    static final class Builder implements BatchStmt.Builder {

        private String sql;

        private List<List<ParamValue>> paramGroupList;

        private StatementType statementType;

        private boolean hasVersion;

        @Override
        public BatchStmt.Builder sql(String sql) {
            this.sql = sql;
            return this;
        }

        @Override
        public BatchStmt.Builder paramGroupList(List<List<ParamValue>> paramGroupList) {
            this.paramGroupList = paramGroupList;
            return this;
        }

        @Override
        public BatchStmt.Builder statementType(StatementType statementType) {
            this.statementType = statementType;
            return this;
        }

        @Override
        public BatchStmt.Builder hasVersion(boolean hasVersion) {
            this.hasVersion = hasVersion;
            return this;
        }

        @Override
        public BatchStmt build() {
            _Assert.hasText(this.sql, "sql required");
            _Assert.notNull(this.paramGroupList, "paramGroupList required");
            _Assert.notNull(this.statementType, "statementType required");
            return new BatchStmtImpl(this.sql, this.paramGroupList, this.statementType, this.hasVersion);
        }
    }
}
