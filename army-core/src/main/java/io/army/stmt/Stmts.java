package io.army.stmt;

import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadAccessor;
import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.PrimaryFieldMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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


    public static SimpleStmt dml(_StmtParams params) {
        return new SimpleDmlStmt(params);
    }


    public static SimpleStmt queryStmt(_StmtParams params) {
        return new QueryStmt(params);
    }

    public static PairStmt pair(SimpleStmt first, SimpleStmt second) {
        return new PairStmtImpl(first, second);
    }

    public static PairBatchStmt pairBatch(BatchStmt first, BatchStmt second) {
        return new PairBatchStmtImpl(first, second);
    }

    public static BatchStmt batchDml(final _StmtParams params, final List<?> paramWrapperList) {
        final List<SQLParam> paramGroup = params.paramList();
        final int paramSize = paramGroup.size();
        final List<List<SQLParam>> groupList = new ArrayList<>(paramWrapperList.size());
        final ReadAccessor accessor;
        accessor = ObjectAccessorFactory.readOnlyFromInstance(paramWrapperList.get(0));

        NamedParam namedParam = null;
        List<SQLParam> group;
        Object value;
        for (Object paramObject : paramWrapperList) {
            group = _Collections.arrayList(paramSize);
            for (SQLParam sqlParam : paramGroup) {
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
                    group.add(SingleParam.build(sqlParam.typeMeta(), value));
                }
            }

            if (group.size() != paramSize) {
                //no bug, never here
                throw new IllegalStateException("create parameter group error.");
            }
            groupList.add(_Collections.unmodifiableList(group));
        }
        if (namedParam == null) {
            throw new CriteriaException("Not found any named parameter in batch statement.");
        }
        return new MinBatchDmlStmt(params, groupList);
    }

    public static MultiStmtBatchStmt multiStmtBatchStmt(final _StmtParams params, final int batchSize) {
        final List<SQLParam> paramGroup;
        paramGroup = params.paramList();
        if (paramGroup.size() > 0 || batchSize < 1) {
            //no bug, never here
            throw new IllegalArgumentException();
        }
        final List<List<SQLParam>> groupList = _Collections.arrayList(batchSize);
        for (int i = 0; i < batchSize; i++) {
            groupList.add(paramGroup);
        }
        return new MultiStmtBatchStmtImpl(params, groupList);
    }

    public static MultiStmt.QueryStmt queryStmtItem(_StmtParams params) {
        return new QueryStmtItem(params.hasOptimistic(), params.selectionList());
    }


    private static final class MinSimpleStmt implements io.army.stmt.SimpleStmt {

        private final String sql;

        private final List<SQLParam> paramGroup;

        private MinSimpleStmt(_StmtParams params) {
            this.sql = params.sql();
            this.paramGroup = params.paramList();
        }

        @Override
        public String sqlText() {
            return this.sql;
        }

        @Override
        public boolean hasOptimistic() {
            return false;
        }

        @Override
        public List<SQLParam> paramGroup() {
            return this.paramGroup;
        }

        @Override
        public List<? extends Selection> selectionList() {
            return Collections.emptyList();
        }

        @Override
        public String printSql(UnaryOperator<String> function) {
            return function.apply(this.sql);
        }

        @Override
        public String toString() {
            return this.sql;
        }

    }// MinSimpleStmt


    private static final class PairStmtImpl implements PairStmt {

        private final SimpleStmt first;

        private final SimpleStmt second;

        private PairStmtImpl(SimpleStmt first, SimpleStmt second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean hasOptimistic() {
            return this.first.hasOptimistic();
        }

        @Override
        public SimpleStmt firstStmt() {
            return this.first;
        }

        @Override
        public SimpleStmt secondStmt() {
            return this.second;
        }

        @Override
        public String printSql(final UnaryOperator<String> function) {
            return _StringUtils.builder()
                    .append(function.apply(this.first.sqlText()))
                    .append('\n')
                    .append(function.apply(this.second.sqlText()))
                    .toString();
        }

        @Override
        public String toString() {
            return String.format("first sql:\n%s\n%s", this.first.sqlText(), this.second.sqlText());
        }
    }


    private static final class PairBatchStmtImpl implements PairBatchStmt {

        private final BatchStmt first;

        private final BatchStmt second;

        private PairBatchStmtImpl(BatchStmt first, BatchStmt second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean hasOptimistic() {
            return this.first.hasOptimistic();
        }

        @Override
        public BatchStmt firstStmt() {
            return this.first;
        }

        @Override
        public BatchStmt secondStmt() {
            return this.second;
        }

        @Override
        public String printSql(UnaryOperator<String> function) {
            return _StringUtils.builder()
                    .append("batch pair first:\n")
                    .append(function.apply(this.first.sqlText()))
                    .append("\nbatch pair second:\n")
                    .append(function.apply(this.second.sqlText()))
                    .toString();
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append("batch pair first:\n")
                    .append(this.first.sqlText())
                    .append("\nbatch pair second:\n")
                    .append(this.second.sqlText())
                    .toString();
        }


    }//PairBatchStmtImpl


    private static final class SimpleDmlStmt implements SimpleStmt {

        private final String sql;

        private final List<SQLParam> paramGroup;

        private final List<? extends Selection> selectionList;

        private final boolean hasOptimistic;

        private SimpleDmlStmt(_StmtParams params) {
            this.sql = params.sql();
            this.paramGroup = params.paramList();
            this.selectionList = params.selectionList();
            this.hasOptimistic = params.hasOptimistic();
        }

        @Override
        public String sqlText() {
            return this.sql;
        }

        @Override
        public boolean hasOptimistic() {
            return this.hasOptimistic;
        }

        @Override
        public List<SQLParam> paramGroup() {
            return this.paramGroup;
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.selectionList;
        }

        @Override
        public String printSql(UnaryOperator<String> function) {
            return function.apply(this.sql);
        }

        @Override
        public String toString() {
            return this.sql;
        }
    }//MinDml

    private static class MinBatchDmlStmt implements BatchStmt {

        private final String sql;

        private final List<List<SQLParam>> paramGroupList;

        private final boolean hasOptimistic;

        private MinBatchDmlStmt(_StmtParams params, List<List<SQLParam>> paramGroupList) {
            this.sql = params.sql();
            this.paramGroupList = Collections.unmodifiableList(paramGroupList);
            this.hasOptimistic = params.hasOptimistic();
        }

        @Override
        public final List<? extends Selection> selectionList() {
            return Collections.emptyList();
        }

        @Override
        public final List<List<SQLParam>> groupList() {
            return this.paramGroupList;
        }


        public final String sqlText() {
            return this.sql;
        }

        @Override
        public final boolean hasOptimistic() {
            return this.hasOptimistic;
        }

        @Override
        public final String printSql(UnaryOperator<String> function) {
            return function.apply(this.sql);
        }

        @Override
        public final String toString() {
            return this.sql;
        }

    }//MinBatchDmlStmt

    private static final class MultiStmtBatchStmtImpl implements MultiStmtBatchStmt {

        private final String sql;

        private final List<? extends Selection> selectionList;

        private final List<List<SQLParam>> paramGroupList;

        private final boolean optimistic;

        private MultiStmtBatchStmtImpl(_StmtParams params, List<List<SQLParam>> paramGroupList) {
            this.sql = params.sql();
            this.selectionList = params.selectionList();
            this.paramGroupList = _Collections.unmodifiableList(paramGroupList);
            this.optimistic = params.hasOptimistic();
        }

        @Override
        public String sqlText() {
            return this.sql;
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.selectionList;
        }

        @Override
        public List<List<SQLParam>> groupList() {
            return this.paramGroupList;
        }

        @Override
        public boolean hasOptimistic() {
            return this.optimistic;
        }

        @Override
        public String printSql(UnaryOperator<String> function) {
            return function.apply(this.sql);
        }

        @Override
        public String toString() {
            return this.sql;
        }

    }//MultiStmtBatchStm


    private static final class QueryStmt implements io.army.stmt.SimpleStmt {

        private final String sql;

        private final List<SQLParam> paramGroup;

        private final List<? extends Selection> selectionList;

        private QueryStmt(_StmtParams params) {
            this.sql = params.sql();
            this.paramGroup = params.paramList();
            this.selectionList = params.selectionList();
        }

        @Override
        public String sqlText() {
            return this.sql;
        }

        @Override
        public boolean hasOptimistic() {
            return false;
        }

        @Override
        public List<SQLParam> paramGroup() {
            return this.paramGroup;
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.selectionList;
        }

        @Override
        public String printSql(UnaryOperator<String> function) {
            return function.apply(this.sql);
        }

        @Override
        public String toString() {
            return this.sql;
        }


    }//SelectStmt

    private static abstract class PostStmt implements GeneratedKeyStmt {

        private final String sql;

        private final List<SQLParam> paramList;

        private final List<? extends Selection> selectionList;

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
        public final String sqlText() {
            return this.sql;
        }

        @Override
        public final boolean hasOptimistic() {
            return false;
        }

        @Override
        public final List<SQLParam> paramGroup() {
            return this.paramList;
        }

        @Override
        public final List<? extends Selection> selectionList() {
            return this.selectionList;
        }

        @Override
        public final String printSql(UnaryOperator<String> function) {
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


    private static final class QueryStmtItem implements MultiStmt.QueryStmt {

        private final boolean optimistic;

        private final List<? extends Selection> selectionList;

        /**
         * @param selectionList unmodified list
         */
        private QueryStmtItem(boolean optimistic, List<? extends Selection> selectionList) {
            this.optimistic = optimistic;
            this.selectionList = selectionList;
        }

        @Override
        public boolean hasOptimistic() {
            return this.optimistic;
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.selectionList;
        }


    }//QueryStmtItem


    private static IllegalStateException duplicateId(PrimaryFieldMeta<?> field) {
        String m = String.format("%s value duplication.", field);
        throw new IllegalStateException(m);
    }


}
