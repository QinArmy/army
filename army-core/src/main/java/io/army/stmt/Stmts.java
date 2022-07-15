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

    private Stmts() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * Post insert for generated key
     * </p>
     */
    public static GeneratedKeyStmt post(final InsertStmtParams params) {
        return new PostStmt(params);
    }

    public static io.army.stmt.SimpleStmt minSimple(final StmtParams params) {
        return new MinSimpleStmt(params);
    }


    public static SimpleStmt dml(DmlStmtParams params) {
        return new SimpleDmlStmt(params);
    }


    public static io.army.stmt.SimpleStmt queryStmt(StmtParams params) {
        return new QueryStmt(params);
    }

    public static PairStmt pair(io.army.stmt.SimpleStmt parent, io.army.stmt.SimpleStmt child) {
        return new PairStmtImpl(parent, child);
    }

    public static BatchStmt batchDml(final DmlStmtParams params, final List<?> paramWrapperList) {
        final List<ParamValue> paramGroup = params.paramList();
        final int paramSize = paramGroup.size();
        final List<List<ParamValue>> groupList = new ArrayList<>(paramWrapperList.size());
        final ReadAccessor accessor;
        accessor = ObjectAccessorFactory.readOnlyFromInstance(paramWrapperList.get(0));

        NamedParam namedParam = null;
        List<ParamValue> group;
        Object value;
        for (Object paramObject : paramWrapperList) {
            group = new ArrayList<>(paramSize);
            ParamValue param;
            for (int i = 0; i < paramSize; i++) {
                param = paramGroup.get(i);
                if (!(param instanceof NamedParam)) {
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
                            throw _Exceptions.namedElementParamNotMatch(size, index - i);
                        }
                        group.add(ParamValue.build(paramMeta, element));
                        index++;
                    }
                    if (index > paramSize || i + size != index) {
                        throw _Exceptions.namedCollectionParamSizeError((NamedElementParam) namedParam, index - i);
                    }
                    i = index - 1;
                } else {
                    namedParam = ((NamedParam) param);
                    value = accessor.get(paramObject, namedParam.name());
                    if (value == null && param instanceof NonNullNamedParam) {
                        throw _Exceptions.nonNullNamedParam((NonNullNamedParam) param);
                    }
                    group.add(ParamValue.build(param.paramMeta(), value));
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
        return new MinBatchDmlStmt(params, groupList);
    }


    private static final class MinSimpleStmt implements io.army.stmt.SimpleStmt {

        private final String sql;

        private final List<ParamValue> paramGroup;

        private MinSimpleStmt(StmtParams params) {
            this.sql = params.sql();
            this.paramGroup = params.paramList();
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

        private final io.army.stmt.SimpleStmt parent;

        private final io.army.stmt.SimpleStmt child;

        private PairStmtImpl(io.army.stmt.SimpleStmt parent, io.army.stmt.SimpleStmt child) {
            this.parent = parent;
            this.child = child;
        }

        @Override
        public io.army.stmt.SimpleStmt parentStmt() {
            return this.parent;
        }

        @Override
        public io.army.stmt.SimpleStmt childStmt() {
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


    private static final class SimpleDmlStmt implements SimpleStmt {

        private final String sql;

        private final List<ParamValue> paramGroup;

        private final List<Selection> selectionList;

        private final boolean hasOptimistic;

        private SimpleDmlStmt(DmlStmtParams params) {
            this.sql = params.sql();
            this.paramGroup = params.paramList();
            this.selectionList = params.selectionList();
            this.hasOptimistic = params.hasVersion();
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
    }//MinDml

    private static final class MinBatchDmlStmt implements BatchStmt {

        private final String sql;

        private final List<List<ParamValue>> paramGroupList;

        private final boolean hasOptimistic;

        private MinBatchDmlStmt(DmlStmtParams params, List<List<ParamValue>> paramGroupList) {
            this.sql = params.sql();
            this.paramGroupList = Collections.unmodifiableList(paramGroupList);
            this.hasOptimistic = params.hasVersion();
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

    private static final class QueryStmt implements io.army.stmt.SimpleStmt {

        private final String sql;

        private final List<ParamValue> paramGroup;

        private final List<Selection> selectionList;

        private QueryStmt(StmtParams params) {
            this.sql = params.sql();
            this.paramGroup = params.paramList();
            this.selectionList = params.selectionList();
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

        private final List<Selection> selectionList;

        private final List<IDomain> domainList;

        private final ObjectAccessor domainAccessor;

        private final PrimaryFieldMeta<?> field;

        private final String idAlias;

        private PostStmt(InsertStmtParams params) {
            this.sql = params.sql();
            this.paramGroup = params.paramList();
            this.selectionList = params.selectionList();
            this.domainList = params.domainList();

            this.domainAccessor = params.domainAccessor();
            this.field = params.returnId();
            this.idAlias = params.idReturnAlias();
        }

        @Override
        public String idReturnAlias() {
            return this.idAlias;
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

    }//PostStmt


}
