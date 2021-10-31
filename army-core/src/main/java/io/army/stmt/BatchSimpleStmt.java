package io.army.stmt;

import io.army.codec.StatementType;

import java.util.List;

public interface BatchSimpleStmt extends GenericSimpleStmt {


    List<List<ParamValue>> paramGroupList();

    static Builder builder() {
        return new BatchSimpleStmtImpl.Builder();
    }

    interface Builder {

        Builder sql(String sql);

        Builder paramGroupList(List<List<ParamValue>> paramGroupList);

        Builder statementType(StatementType statementType);

        Builder hasVersion(boolean hasVersion);

        BatchSimpleStmt build();
    }

}
