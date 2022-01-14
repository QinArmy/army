package io.army.stmt;

import io.army.criteria.SelectPart;
import io.army.criteria.Selection;
import io.army.criteria.SelectionGroup;
import io.army.util._Exceptions;

import java.util.ArrayList;
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

    public static SimpleStmt selectStmt(String sql, List<ParamValue> paramList, List<SelectPart> selectPartList) {
        final List<Selection> selectionList = new ArrayList<>(selectPartList.size());
        for (SelectPart selectPart : selectPartList) {
            if (selectPart instanceof Selection) {
                selectionList.add((Selection) selectPart);
            } else if (selectPart instanceof SelectionGroup) {
                selectionList.addAll(((SelectionGroup) selectPart).selectionList());
            } else {
                throw _Exceptions.unknownSelectPart(selectPart);
            }
        }
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
