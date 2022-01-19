package io.army.stmt;

import io.army.beans.ReadWrapper;
import io.army.criteria.CriteriaException;
import io.army.criteria.NamedParam;
import io.army.criteria.NonNullNamedParam;
import io.army.criteria.Selection;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
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

    public static SimpleStmt dml(String sql, List<ParamValue> paramList, boolean hasOptimistic) {
        return new MinDmlStmt(sql, paramList, hasOptimistic);
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

    public static BatchStmt batchDml(SimpleStmt stmt, List<ReadWrapper> wrapperList) {
        final List<ParamValue> paramGroup = stmt.paramGroup();
        final int paramSize = paramGroup.size();
        final List<List<ParamValue>> groupList = new ArrayList<>(wrapperList.size());

        NamedParam<?> namedParam = null;
        for (ReadWrapper wrapper : wrapperList) {
            final List<ParamValue> group = new ArrayList<>(paramSize);

            for (ParamValue param : paramGroup) {
                if (!(param instanceof NamedParam)) {
                    group.add(param);
                    continue;
                }
                namedParam = ((NamedParam<?>) param);
                final Object value = wrapper.get(namedParam.name());
                if (value == null && param instanceof NonNullNamedParam) {
                    throw _Exceptions.nonNullNamedParam((NonNullNamedParam<?>) param);
                }
                group.add(ParamValue.build(param.paramMeta(), value));
            }

            groupList.add(group);

        }
        if (namedParam == null) {
            throw new CriteriaException("Not found any named parameter in batch statement.");
        }
        return new MinBatchDmlStmt(stmt.sql(), groupList, stmt.hasOptimistic());
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
        public boolean hasOptimistic() {
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


    private static final class MinDmlStmt implements SimpleStmt {

        private final String sql;

        private final List<ParamValue> paramGroup;

        private final boolean hasOptimistic;

        private MinDmlStmt(String sql, List<ParamValue> paramGroup, boolean hasOptimistic) {
            this.sql = sql;
            this.paramGroup = CollectionUtils.unmodifiableList(paramGroup);
            this.hasOptimistic = hasOptimistic;
        }

        @Override
        public String sql() {
            return this.sql;
        }

        @Override
        public boolean hasOptimistic() {
            return this.hasOptimistic;
        }

        @Override
        public List<ParamValue> paramGroup() {
            return this.paramGroup;
        }

        @Override
        public List<Selection> selectionList() {
            return Collections.emptyList();
        }

    }//MinDml

    private static final class MinBatchDmlStmt implements BatchStmt {

        private final String sql;

        private final List<List<ParamValue>> paramGroupList;

        private final boolean hasOptimistic;

        private MinBatchDmlStmt(String sql, List<List<ParamValue>> paramGroupList, boolean hasOptimistic) {
            this.sql = sql;
            this.paramGroupList = Collections.unmodifiableList(paramGroupList);
            this.hasOptimistic = hasOptimistic;
        }

        @Override
        public List<List<ParamValue>> groupList() {
            return this.paramGroupList;
        }

        @Override
        public String sql() {
            return this.sql;
        }

        @Override
        public boolean hasOptimistic() {
            return this.hasOptimistic;
        }

    }//MinBatchDmlStmt


}
