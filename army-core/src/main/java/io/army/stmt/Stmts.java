package io.army.stmt;

import io.army.criteria.Selection;

import java.util.List;

public abstract class Stmts {

    protected Stmts() {
        throw new UnsupportedOperationException();
    }


    public static GroupStmt group(List<Stmt> stmtList) {
        return null;
    }

    public static GroupStmt group(Stmt stmt1, Stmt stmt2) {
        return null;
    }

    public static SimpleStmt simple(String sql, List<ParamValue> paramList) {
        return null;
    }

    public static SimpleStmt selectStmt(String sql, List<ParamValue> paramList, List<Selection> selectionList) {


        return null;
    }

    public static SimpleStmt simple(String sql, List<ParamValue> paramList, Selection selection) {
        return null;
    }

    public static PairStmt pair(SimpleStmt parent, SimpleStmt child) {
        return null;
    }

    public static BatchStmt batch(SimpleStmt simpleStmt, List<List<ParamValue>> groupList) {
        return null;
    }


}
