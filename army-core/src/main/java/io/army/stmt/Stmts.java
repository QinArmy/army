package io.army.stmt;

import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadAccessor;
import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.PrimaryFieldMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
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
    public static GeneratedKeyStmt domainPost(final _InsertStmtParams._DomainParams params) {
        return new DomainPostStmt(params);
    }

    public static GeneratedKeyStmt valuePost(final _InsertStmtParams._ValueParams params) {
        return new ValuePostStmt(params);
    }

    public static SimpleStmt minSimple(final _StmtParams params) {
        return new MinSimpleStmt(params);
    }

    public static GeneratedKeyStmt assignmentPost(final _InsertStmtParams._AssignmentParams params) {
        return new AssignmentPostStmt(params);
    }


    public static SimpleStmt dml(DmlStmtParams params) {
        return new SimpleDmlStmt(params);
    }


    public static io.army.stmt.SimpleStmt queryStmt(_StmtParams params) {
        return new QueryStmt(params);
    }

    public static PairStmt pair(io.army.stmt.SimpleStmt parent, io.army.stmt.SimpleStmt child) {
        return new PairStmtImpl(parent, child);
    }

    public static BatchStmt batchDml(final DmlStmtParams params, final List<?> paramWrapperList) {
        final List<SqlParam> paramGroup = params.paramList();
        final int paramSize = paramGroup.size();
        final List<List<SqlParam>> groupList = new ArrayList<>(paramWrapperList.size());
        final ReadAccessor accessor;
        accessor = ObjectAccessorFactory.readOnlyFromInstance(paramWrapperList.get(0));

        NamedParam namedParam = null;
        List<SqlParam> group;
        Object value;
        for (Object paramObject : paramWrapperList) {
            group = new ArrayList<>(paramSize);
            for (SqlParam sqlParam : paramGroup) {
                if (!(sqlParam instanceof NamedParam)) {
                    group.add(sqlParam);
                } else if (sqlParam instanceof NamedParam.NamedMulti) {
                    namedParam = ((NamedParam) sqlParam);
                    value = accessor.get(paramObject, namedParam.name());
                    if (!(value instanceof Collection)) {
                        throw _Exceptions.namedParamNotMatch((NamedParam.NamedMulti) namedParam, value);
                    }
                    group.add(MultiParam.build((NamedParam.NamedMulti) namedParam, (Collection<?>) value));
                } else {
                    namedParam = ((NamedParam) sqlParam);
                    value = accessor.get(paramObject, namedParam.name());
                    if (value == null && sqlParam instanceof SqlValueParam.NonNullValue) {
                        throw _Exceptions.nonNullNamedParam((NamedParam) sqlParam);
                    }
                    group.add(SingleParam.build(sqlParam.paramMeta(), value));
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

        private final List<SqlParam> paramGroup;

        private MinSimpleStmt(_StmtParams params) {
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
        public List<SqlParam> paramGroup() {
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
            return String.format("%s\n%s", function.apply(this.parent.sql()), function.apply(this.child.sql()));
        }

        @Override
        public String toString() {
            return String.format("parent sql:\n%s\n%s", this.parent.sql(), this.child.sql());
        }
    }


    private static final class SimpleDmlStmt implements SimpleStmt {

        private final String sql;

        private final List<SqlParam> paramGroup;

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
        public List<SqlParam> paramGroup() {
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

        private final List<List<SqlParam>> paramGroupList;

        private final boolean hasOptimistic;

        private MinBatchDmlStmt(DmlStmtParams params, List<List<SqlParam>> paramGroupList) {
            this.sql = params.sql();
            this.paramGroupList = Collections.unmodifiableList(paramGroupList);
            this.hasOptimistic = params.hasVersion();
        }

        @Override
        public List<List<SqlParam>> groupList() {
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

        private final List<SqlParam> paramGroup;

        private final List<Selection> selectionList;

        private QueryStmt(_StmtParams params) {
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
        public List<SqlParam> paramGroup() {
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

    private static abstract class PostStmt implements GeneratedKeyStmt {

        private final String sql;

        private final List<SqlParam> paramList;

        private final List<Selection> selectionList;

        final PrimaryFieldMeta<?> field;

        private final String idReturnAlias;

        private PostStmt(_InsertStmtParams params) {
            this.sql = params.sql();
            this.paramList = params.paramList();
            this.selectionList = params.selectionList();
            this.field = params.idField();

            this.idReturnAlias = params.idReturnAlias();
        }

        @Override
        public final PrimaryFieldMeta<?> idField() {
            return this.field;
        }

        @Override
        public final String idReturnAlias() {
            return this.idReturnAlias;
        }

        @Override
        public final String sql() {
            return this.sql;
        }

        @Override
        public final boolean hasOptimistic() {
            return false;
        }

        @Override
        public final List<SqlParam> paramGroup() {
            return this.paramList;
        }

        @Override
        public final List<Selection> selectionList() {
            return this.selectionList;
        }

        @Override
        public final String printSql(Function<String, String> function) {
            return function.apply(this.sql);
        }

        @Override
        public final String toString() {
            return this.sql;
        }


    }//PostStmt

    private static final class DomainPostStmt extends PostStmt {

        private final List<?> domainList;

        private final int rowSize;

        private final ObjectAccessor domainAccessor;

        private DomainPostStmt(_InsertStmtParams._DomainParams params) {
            super(params);
            this.domainList = params.domainList();
            this.rowSize = this.domainList.size();
            this.domainAccessor = params.domainAccessor();
        }

        @Override
        public int rowSize() {
            return this.rowSize;
        }

        @Override
        public void setGeneratedIdValue(final int indexBasedZero, final @Nullable Object idValue) {
            if (indexBasedZero < 0 || indexBasedZero >= this.rowSize) {
                String m = String.format("indexBasedZero[%s] not in[0,%s]", indexBasedZero, this.rowSize);
                throw new IllegalArgumentException(m);
            }
            if (idValue == null) {
                throw new NullPointerException("idValue");
            }
            final Object domain;
            domain = this.domainList.get(indexBasedZero);
            final String fieldName = this.field.fieldName();

            if (this.domainAccessor.get(domain, fieldName) != null) {
                throw duplicateId(this.field);
            }
            this.domainAccessor.set(domain, fieldName, idValue);
        }


    }//DomainPostStmt


    private static final class ValuePostStmt extends PostStmt {

        private final BiFunction<Integer, Object, Object> function;

        private final int rowSize;

        private ValuePostStmt(_InsertStmtParams._ValueParams params) {
            super(params);
            this.function = params.function();
            this.rowSize = this.rowSize();
        }

        @Override
        public int rowSize() {
            return this.rowSize;
        }

        @Override
        public void setGeneratedIdValue(final int indexBasedZero, final @Nullable Object idValue) {
            if (indexBasedZero < 0 || indexBasedZero >= this.rowSize) {
                String m = String.format("indexBasedZero[%s] not in[0,%s]", indexBasedZero, this.rowSize);
                throw new IllegalArgumentException(m);
            }
            if (idValue == null) {
                throw new NullPointerException("idValue");
            }
            if (this.function.apply(indexBasedZero, idValue) != null) {
                throw duplicateId(this.field);
            }

        }
    }//ValuePostStmt

    private static final class AssignmentPostStmt extends PostStmt {

        private final Function<Object, Object> function;

        private AssignmentPostStmt(_InsertStmtParams._AssignmentParams params) {
            super(params);
            this.function = params.function();
        }

        @Override
        public int rowSize() {
            return 1;
        }

        @Override
        public void setGeneratedIdValue(final int indexBasedZero, final @Nullable Object idValue) {
            if (indexBasedZero != 0) {
                String m = String.format("indexBasedZero[%s] not 0", indexBasedZero);
                throw new IllegalArgumentException(m);
            }
            if (idValue == null) {
                throw new NullPointerException("idValue");
            }

            if (this.function.apply(idValue) != null) {
                throw duplicateId(this.field);
            }

        }


    }//AssignmentPostStmt


    private static IllegalStateException duplicateId(PrimaryFieldMeta<?> field) {
        String m = String.format("%s value duplication.", field);
        throw new IllegalStateException(m);
    }


}
