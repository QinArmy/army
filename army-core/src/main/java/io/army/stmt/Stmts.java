package io.army.stmt;

import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadAccessor;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.ParamMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collection;
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

    /**
     * <p>
     * Post insert for returning id
     * </p>
     */
    public static GeneratedKeyStmt returnId(final StmtParams params) {
        return null;
    }

    /**
     * <p>
     * Post insert for generated key
     * </p>
     */
    public static GeneratedKeyStmt post(final StmtParams params) {
        return null;
    }

    public static SimpleStmt simple(final StmtParams params) {
        return null;
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

    public static BatchStmt batchDml(SimpleStmt stmt, List<?> paramWrapperList) {
        final List<ParamValue> paramGroup = stmt.paramGroup();
        final int paramSize = paramGroup.size();
        final List<List<ParamValue>> groupList = new ArrayList<>(paramWrapperList.size());
        final ReadAccessor accessor;
        accessor = ObjectAccessorFactory.readOnlyForInstance(paramWrapperList.get(0));

        NamedParam namedParam = null;
        List<ParamValue> group;
        Object value;
        for (Object paramObject : paramWrapperList) {
            group = new ArrayList<>(paramSize);
            ParamValue param;
            for (int i = 0; i < paramSize; i++) {
                param = paramGroup.get(i);
                if (param instanceof StrictParamValue) {
                    group.add(param);
                } else if (param instanceof NamedElementParam) {
                    namedParam = ((NamedParam) param);
                    value = accessor.get(paramObject, namedParam.name());
                    if (!(value instanceof Collection)) {
                        throw _Exceptions.namedCollectionParamNotMatch((NamedElementParam) namedParam, value);
                    }
                    final int size = ((NamedElementParam) namedParam).size();
                    final Collection<?> collection = (Collection<?>) value;
                    if (collection.size() != size) {
                        throw _Exceptions.namedCollectionParamSizeError((NamedElementParam) namedParam
                                , collection.size());
                    }
                    final ParamMeta paramMeta = namedParam.paramMeta();
                    int index = i;
                    for (Object element : collection) {
                        if (paramGroup.get(index) != namedParam) {
                            //here expression bug
                            throw new CriteriaException("NamedElementParam not match");
                        }
                        group.add(ParamValue.build(paramMeta, element));
                        index++;
                    }
                    if (index > paramSize || i + size != index) {
                        throw _Exceptions.namedCollectionParamSizeError((NamedElementParam) namedParam, index - i);
                    }
                    i = index - 1;
                } else if (param instanceof NamedParam) {
                    namedParam = ((NamedParam) param);
                    value = accessor.get(paramObject, namedParam.name());
                    if (value == null && param instanceof NonNullNamedParam) {
                        throw _Exceptions.nonNullNamedParam((NonNullNamedParam) param);
                    }
                    group.add(ParamValue.build(param.paramMeta(), value));
                } else {
                    throw _Exceptions.unknownParamValue(param);
                }
            }

            if (group.size() != paramSize) {
                //here bug
                throw new IllegalStateException("create parameter group error.");
            }
            groupList.add(_CollectionUtils.unmodifiableList(group));
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
        public PrimaryFieldMeta<?> idField() {
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
