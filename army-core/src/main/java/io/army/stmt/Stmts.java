package io.army.stmt;

import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadAccessor;
import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.meta.PrimaryFieldMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.ObjIntConsumer;
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
    public static GeneratedKeyStmt postStmt(final InsertStmtParams params) {
        final GeneratedKeyStmt stmt;
        if (params.isTwoStmtQuery()) {
            stmt = new TwoStmtQueryPostStmt(params);
        } else {
            stmt = new PostStmt(params);
        }
        return stmt;
    }


    public static SimpleStmt minSimple(final StmtParams params) {
        return new MinSimpleStmt(params);
    }


    public static SimpleStmt dml(StmtParams params) {
        return new SimpleDmlStmt(params);
    }


    public static SimpleStmt queryStmt(final StmtParams params) {
        final SimpleStmt stmt;
        final int idSelectionIndex;
        if (params instanceof DmlStmtParams
                && (idSelectionIndex = ((DmlStmtParams) params).idSelectionIndex()) > -1) {
            stmt = new TwoStmtModeQueryStmtIml(params, idSelectionIndex);
        } else {
            stmt = new QueryStmt(params);
        }
        return stmt;
    }


    public static PairStmt pair(SimpleStmt first, SimpleStmt second) {
        return new PairStmtImpl(first, second);
    }

    public static PairBatchStmt pairBatch(BatchStmt first, BatchStmt second) {
        return new PairBatchStmtImpl(first, second);
    }

    public static BatchStmt batchDml(final StmtParams params, final List<?> paramWrapperList) {
        final List<SQLParam> paramGroup = params.paramList();
        final int paramSize = paramGroup.size();
        final int batchSize = paramWrapperList.size();

        final List<List<SQLParam>> groupList = _Collections.arrayList(batchSize);
        final ReadAccessor accessor;
        accessor = ObjectAccessorFactory.readOnlyFromInstance(paramWrapperList.get(0));

        NamedParam namedParam = null;
        List<SQLParam> group;
        Object value, paramObject;
        for (int batchIndex = 0; batchIndex < batchSize; batchIndex++) {
            paramObject = paramWrapperList.get(batchIndex);

            group = _Collections.arrayList(paramSize);
            for (SQLParam sqlParam : paramGroup) {
                if (!(sqlParam instanceof NamedParam)) {
                    group.add(sqlParam);
                } else if (sqlParam instanceof NamedParam.NamedRow) {
                    namedParam = ((NamedParam) sqlParam);
                    value = accessor.get(paramObject, namedParam.name());
                    if (!(value instanceof Collection)) {
                        throw _Exceptions.namedParamNotMatch((NamedParam.NamedRow) namedParam, value);
                    }
                    group.add(MultiParam.build((NamedParam.NamedRow) namedParam, (Collection<?>) value));
                } else if (sqlParam == SQLs.BATCH_NO_PARAM) {
                    group.add(SingleParam.build(sqlParam.typeMeta(), batchIndex + 1));
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

    public static MultiStmtBatchStmt multiStmtBatchStmt(final StmtParams params, final int batchSize) {
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

    public static MultiStmt.StmtItem queryOrUpdateItem(final StmtParams params) {
        final List<? extends Selection> selectionList;
        selectionList = params.selectionList();

        final MultiStmt.StmtItem item;
        if (selectionList.size() > 0) {
            item = new QueryStmtItem(params.hasOptimistic(), selectionList);
        } else if (params.hasOptimistic()) {
            item = MultiStmt.UpdateStmt.OPTIMISTIC;
        } else {
            item = MultiStmt.UpdateStmt.NON_OPTIMISTIC;
        }
        return item;
    }


    private static final class MinSimpleStmt implements io.army.stmt.SimpleStmt {

        private final String sql;

        private final List<SQLParam> paramGroup;

        private MinSimpleStmt(StmtParams params) {
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

        private SimpleDmlStmt(StmtParams params) {
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

        private MinBatchDmlStmt(StmtParams params, List<List<SQLParam>> paramGroupList) {
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

        private MultiStmtBatchStmtImpl(StmtParams params, List<List<SQLParam>> paramGroupList) {
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


    private static class QueryStmt implements SimpleStmt {

        private final String sql;

        private final List<SQLParam> paramGroup;

        final List<? extends Selection> selectionList;

        private final boolean optimistic;

        private QueryStmt(StmtParams params) {
            this.sql = params.sql();
            this.paramGroup = params.paramList();
            this.selectionList = params.selectionList();
            this.optimistic = params.hasOptimistic();
        }

        @Override
        public final String sqlText() {
            return this.sql;
        }

        @Override
        public final boolean hasOptimistic() {
            return this.optimistic;
        }

        @Override
        public final List<SQLParam> paramGroup() {
            return this.paramGroup;
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


    }//QueryStmt

    private static final class TwoStmtModeQueryStmtIml extends QueryStmt implements TwoStmtQueryStmt {

        private final int idSelectionIndex;

        private TwoStmtModeQueryStmtIml(StmtParams params, int idSelectionIndex) {
            super(params);
            assert idSelectionIndex > -1 && idSelectionIndex < this.selectionList.size();
            this.idSelectionIndex = idSelectionIndex;
        }

        @Override
        public int idSelectionIndex() {
            return this.idSelectionIndex;
        }


    }//TwoStmtModeQueryStmtIml

    private static class PostStmt implements GeneratedKeyStmt {

        private final String sql;

        private final List<SQLParam> paramList;

        private final List<? extends Selection> selectionList;

        private final int rowSize;

        private final PrimaryFieldMeta<?> field;

        private final int idSelectionIndex;

        private final ObjIntConsumer<Object> consumer;

        private PostStmt(InsertStmtParams params) {
            this.sql = params.sql();
            this.paramList = params.paramList();
            this.selectionList = params.selectionList();
            this.rowSize = params.rowSize();

            this.field = params.idField();
            this.idSelectionIndex = params.idSelectionIndex();
            this.consumer = params.idConsumer();
        }

        @Override
        public final int rowSize() {
            return this.rowSize;
        }

        @Override
        public final void setGeneratedIdValue(int indexBasedZero, Object idValue) {
            this.consumer.accept(idValue, indexBasedZero);
        }

        @Override
        public final PrimaryFieldMeta<?> idField() {
            return this.field;
        }


        @Override
        public final int idSelectionIndex() {
            return this.idSelectionIndex;
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


    private static final class TwoStmtQueryPostStmt extends PostStmt implements TwoStmtQueryStmt {

        private TwoStmtQueryPostStmt(InsertStmtParams params) {
            super(params);
        }

    }//TwoStmtQueryPostStmt


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
