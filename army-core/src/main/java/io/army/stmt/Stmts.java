package io.army.stmt;

import java.util.List;

public abstract class Stmts {

    protected Stmts() {
        throw new UnsupportedOperationException();
    }


    public static GroupStmt group(List<Stmt> stmtList) {
        return null;
    }

    public static SimpleStmt simple(String sql, List<ParamValue> paramList) {
        return null;
    }

    public static PairStmt pair(SimpleStmt parent, SimpleStmt child) {
        return null;
    }


}
