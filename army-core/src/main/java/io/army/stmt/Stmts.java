package io.army.stmt;

import io.army.criteria.Selection;
import io.army.util.CollectionUtils;

import java.util.Collections;
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
        return new MinSimpleStmt(sql, paramList);
    }

    public static SimpleStmt selectStmt(String sql, List<ParamValue> paramList, List<Selection> selectionList) {


        return null;
    }

    public static SimpleStmt simple(String sql, List<ParamValue> paramList, Selection selection) {
        return null;
    }

    public static PairStmt pair(SimpleStmt parent, SimpleStmt child) {
        return new PairStmtImpl(parent, child);
    }

    public static BatchStmt batch(SimpleStmt simpleStmt, List<List<ParamValue>> groupList) {
        return null;
    }


    private static final class MinSimpleStmt implements SimpleStmt {

        private final String sql;

        private final List<ParamValue> paramGroup;

        private MinSimpleStmt(String sql, List<ParamValue> paramGroup) {
            this.sql = sql;
            this.paramGroup = CollectionUtils.unmodifiableList(paramGroup);
        }

        @Override
        public String sql() {
            return this.sql;
        }

        @Override
        public boolean hasVersion() {
            return false;
        }

        @Override
        public List<ParamValue> paramGroup() {
            return this.paramGroup;
        }

        @Override
        public List<Selection> selectionList() {
            return Collections.emptyList();
        }


    }// MinSimpleStmt


    private static final class PairStmtImpl implements PairStmt {

        private final SimpleStmt parent;

        private final SimpleStmt child;

        private PairStmtImpl(SimpleStmt parent, SimpleStmt child) {
            this.parent = parent;
            this.child = child;
        }

        @Override
        public SimpleStmt parentStmt() {
            return this.parent;
        }

        @Override
        public SimpleStmt childStmt() {
            return this.child;
        }

    }


}
