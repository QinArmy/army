package io.army.stmt;

import io.army.codec.StatementType;
import io.army.criteria.Selection;

import java.util.List;

public interface SimpleStmt extends GenericSimpleStmt {

    /**
     * @return a unmodifiable list
     */
    List<ParamValue> paramGroup();

    /**
     * @return a unmodifiable list
     */
    List<Selection> selectionList();

    static Builder builder() {
        return new SimpleStmtImpl.Builder();
    }

    interface Builder {

        Builder sql(String sql);

        Builder paramList(List<ParamValue> paramList);

        Builder statementType(StatementType statementType);

        Builder selectionList(List<Selection> selectionList);

        Builder hasVersion(boolean hasVersion);

        SimpleStmt build();
    }

}
