package io.army.stmt;

import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadAccessor;
import io.army.criteria.CriteriaException;
import io.army.criteria.NamedParam;
import io.army.criteria.NonNullNamedParam;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.meta.PrimaryFieldMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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

    public static GeneratedKeyStmt post(String sql, List<ParamValue> paramList
            , List<IDomain> domainList, ObjectAccessor domainAccessor, PrimaryFieldMeta<?> field) {
        return new PostStmt(sql, paramList, domainList, domainAccessor, field);
    }

    public static SimpleStmt dml(String sql, List<ParamValue> paramList, boolean hasOptimistic) {
        return new MinDmlStmt(sql, paramList, hasOptimistic);
    }


    public static SimpleStmt selectStmt(String sql, List<ParamValue> paramList, List<Selection> selectionList) {
        return new SelectStmt(sql, paramList, selectionList);
    }

    public static SimpleStmt simple(String sql, List<ParamValue> paramList, Selection selection) {
        return null;
    }

    public static PairStmt pair(SimpleStmt parent, SimpleStmt child) {
        return new PairStmtImpl(parent, child);
    }

    public static BatchStmt batchDml(SimpleStmt stmt, List<?> paramList) {
        final List<ParamValue> paramGroup = stmt.paramGroup();
        final int paramSize = paramGroup.size();
        final List<List<ParamValue>> groupList = new ArrayList<>(paramList.size());
        final ReadAccessor accessor;
        accessor = ObjectAccessorFactory.readOnlyForInstance(paramList.get(0));

        NamedParam namedParam = null;
        List<ParamValue> group;
        Object value;
        for (Object paramObject : paramList) {
            group = new ArrayList<>(paramSize);

            for (ParamValue param : paramGroup) {
                if (!(param instanceof NamedParam)) {
                    group.add(param);
                    continue;
                }
                namedParam = ((NamedParam) param);
                value = accessor.get(paramObject, namedParam.name());
                if (value == null && param instanceof NonNullNamedParam) {
                    throw _Exceptions.nonNullNamedParam((NonNullNamedParam) param);
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
            this.paramGroup = _CollectionUtils.unmodifiableList(paramGroup);
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

        @Override
        public String printSql(Function<String, String> function) {
            return function.apply(this.sql);
        }

        @Override
        public String toString() {
            return this.sql;
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

        @Override
        public String printSql(final Function<String, String> function) {
            return String.format("parent sql:\n%s\n%s"
                    , function.apply(this.parent.sql()), function.apply(this.child.sql()));
        }

        @Override
        public String toString() {
            return String.format("parent sql:\n%s\n%s", this.parent.sql(), this.child.sql());
        }
    }


    private static final class MinDmlStmt implements SimpleStmt {

        private final String sql;

        private final List<ParamValue> paramGroup;

        private final boolean hasOptimistic;

        private MinDmlStmt(String sql, List<ParamValue> paramGroup, boolean hasOptimistic) {
            this.sql = sql;
            this.paramGroup = _CollectionUtils.unmodifiableList(paramGroup);
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

        @Override
        public String printSql(Function<String, String> function) {
            return function.apply(this.sql);
        }

        @Override
        public String toString() {
            return this.sql;
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

        @Override
        public String printSql(Function<String, String> function) {
            return function.apply(this.sql);
        }

        @Override
        public String toString() {
            return this.sql;
        }
    }//MinBatchDmlStmt

    private static final class SelectStmt implements SimpleStmt {

        private final String sql;

        private final List<ParamValue> paramGroup;

        private final List<Selection> selectionList;

        private SelectStmt(String sql, List<ParamValue> paramGroup, List<Selection> selectionList) {
            this.sql = sql;
            this.paramGroup = _CollectionUtils.unmodifiableList(paramGroup);
            this.selectionList = _CollectionUtils.unmodifiableList(selectionList);
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
            return this.selectionList;
        }

        @Override
        public String printSql(Function<String, String> function) {
            return function.apply(this.sql);
        }

        @Override
        public String toString() {
            return this.sql;
        }
    }//SelectStmt

    private static final class PostStmt implements GeneratedKeyStmt {

        private final String sql;

        private final List<ParamValue> paramGroup;

        private final List<IDomain> domainList;

        private final ObjectAccessor domainAccessor;

        private final PrimaryFieldMeta<?> field;

        private PostStmt(String sql, List<ParamValue> paramGroup, List<IDomain> domainList
                , ObjectAccessor domainAccessor, PrimaryFieldMeta<?> field) {
            this.sql = sql;
            this.paramGroup = Collections.unmodifiableList(paramGroup);
            this.domainList = domainList;
            this.domainAccessor = domainAccessor;
            this.field = field;
        }

        @Override
        public String primaryKeyName() {
            return this.field.fieldName();
        }

        @Override
        public ObjectAccessor domainAccessor() {
            return this.domainAccessor;
        }

        @Override
        public List<IDomain> domainList() {
            return this.domainList;
        }

        @Override
        public PrimaryFieldMeta<?> idMeta() {
            return this.field;
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


        @Override
        public String printSql(Function<String, String> function) {
            return function.apply(this.sql);
        }

        @Override
        public String toString() {
            return this.sql;
        }

    }//PostStmt


}
