package io.army.wrapper;

import io.army.codec.StatementType;
import io.army.util.Assert;

import java.util.Collections;
import java.util.List;

final class BatchSimpleSQLWrapperImpl implements BatchSimpleSQLWrapper {

    private final String sql;

    private final List<List<ParamWrapper>> paramGroupList;

    private final StatementType statementType;

    private final boolean hasVersion;


    BatchSimpleSQLWrapperImpl(String sql, List<List<ParamWrapper>> paramGroupList, StatementType statementType
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
    public final List<List<ParamWrapper>> paramGroupList() {
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

    static final class Builder implements BatchSimpleSQLWrapper.Builder {

        private String sql;

        private List<List<ParamWrapper>> paramGroupList;

        private StatementType statementType;

        private boolean hasVersion;

        @Override
        public BatchSimpleSQLWrapper.Builder sql(String sql) {
            this.sql = sql;
            return this;
        }

        @Override
        public BatchSimpleSQLWrapper.Builder paramGroupList(List<List<ParamWrapper>> paramGroupList) {
            this.paramGroupList = paramGroupList;
            return this;
        }

        @Override
        public BatchSimpleSQLWrapper.Builder statementType(StatementType statementType) {
            this.statementType = statementType;
            return this;
        }

        @Override
        public BatchSimpleSQLWrapper.Builder hasVersion(boolean hasVersion) {
            this.hasVersion = hasVersion;
            return this;
        }

        @Override
        public BatchSimpleSQLWrapper build() {
            Assert.hasText(this.sql, "sql required");
            Assert.notNull(this.paramGroupList, "paramGroupList required");
            Assert.notNull(this.statementType, "statementType required");
            return new BatchSimpleSQLWrapperImpl(this.sql, this.paramGroupList, this.statementType, this.hasVersion);
        }
    }
}
