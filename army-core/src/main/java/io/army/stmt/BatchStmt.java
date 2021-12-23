package io.army.stmt;

import io.army.codec.StatementType;

import java.util.List;

public interface BatchStmt extends GenericSimpleStmt {


    List<List<ParamValue>> groupList();

    static Builder builder() {
        return new BatchStmtImpl.Builder();
    }

    interface Builder {

        Builder sql(String sql);

        Builder paramGroupList(List<List<ParamValue>> paramGroupList);

        Builder statementType(StatementType statementType);

        Builder hasVersion(boolean hasVersion);

        BatchStmt build();
    }

}
