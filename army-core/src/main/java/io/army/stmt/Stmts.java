/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.stmt;

import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadAccessor;
import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.dialect._DialectUtils;
import io.army.meta.PrimaryFieldMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

public abstract class Stmts {

    private Stmts() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * Post insert for generated key
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


    public static SimpleStmt dmlStmt(StmtParams params) {
        return new SimpleDmlStmt(params);
    }


    public static SimpleStmt queryStmt(final StmtParams params) {
        final SimpleStmt stmt;
        final int idSelectionIndex;
        if (params instanceof DmlStmtParams
                && (idSelectionIndex = ((DmlStmtParams) params).idSelectionIndex()) > -1) {
            stmt = new TwoStmtModeQueryStmtIml(params, idSelectionIndex, ((DmlStmtParams) params).maxColumnSize());
        } else {
            stmt = new QueryStmt(params);
        }
        return stmt;
    }

    public static DeclareCursorStmt declareCursorStmt(CursorStmtParams params) {
        return new ArmyDeclareCursorStmt(params);
    }

    public static BatchStmt batchQueryStmt(final StmtParams params, final List<?> paramList) {
        // TODO add code for firebird database
        return new BatchQueryStmt(params, createParamGroupList(params, paramList));
    }


    public static PairStmt pair(SimpleStmt first, SimpleStmt second) {
        return new PairStmtImpl(first, second);
    }

    public static PairBatchStmt pairBatch(BatchStmt first, BatchStmt second) {
        return new PairBatchStmtImpl(first, second);
    }


    public static BatchStmt batchDmlStmt(final StmtParams params, final List<?> namedParamList) {
        return new MinBatchDmlStmt(params, createParamGroupList(params, namedParamList));
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


    /**
     * @return a modified list
     * @see #batchDmlStmt(StmtParams, List)
     * @see #batchQueryStmt(StmtParams, List)
     */
    private static List<List<SQLParam>> createParamGroupList(final StmtParams params, final List<?> namedParamList) {
        final List<SQLParam> paramGroup = params.paramList();
        final int paramSize = paramGroup.size();
        final int batchSize = namedParamList.size();

        final List<List<SQLParam>> groupList = _Collections.arrayList(batchSize);
        final ReadAccessor accessor;
        accessor = ObjectAccessorFactory.readOnlyFromInstance(namedParamList.get(0));

        NamedParam namedParam = null;
        List<SQLParam> group;
        Object value, paramObject;
        for (int batchIndex = 0; batchIndex < batchSize; batchIndex++) {
            paramObject = namedParamList.get(batchIndex);

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
        } // for loop

        if (namedParam == null) {
            throw new CriteriaException("Not found any named parameter in batch statement.");
        }
        return groupList;
    }

    private static abstract class ArmySingleSqlStmt implements SingleSqlStmt {

        private final String sql;

        private final StmtType stmtType;

        private ArmySingleSqlStmt(StmtParams params) {
            this.sql = params.sql();
            this.stmtType = params.stmtType();
        }

        @Override
        public final String sqlText() {
            return this.sql;
        }

        @Override
        public final StmtType stmtType() {
            return this.stmtType;
        }

        @Override
        public final void printSql(BiConsumer<String, Consumer<String>> beautifyFunc, Consumer<String> appender) {
            beautifyFunc.accept(this.sql, appender);
        }

        @Override
        public final String toString() {
            return this.sql;
        }

    }//SingleSqlStmt

    private static final class MinSimpleStmt extends ArmySingleSqlStmt implements SimpleStmt {

        private final List<SQLParam> paramGroup;

        private MinSimpleStmt(StmtParams params) {
            super(params);
            this.paramGroup = params.paramList();
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
        public StmtType stmtType() {
            return this.first.stmtType();
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
        public void printSql(BiConsumer<String, Consumer<String>> beautifyFunc, Consumer<String> appender) {
            appender.accept("pair first:\n");
            beautifyFunc.accept(this.first.sqlText(), appender);
            appender.accept("\npair second:\n");
            beautifyFunc.accept(this.second.sqlText(), appender);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder(128);
            this.printSql(_DialectUtils.NON_BEAUTIFY_SQL_FUNC, builder::append);
            return builder.toString();
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
        public StmtType stmtType() {
            return this.first.stmtType();
        }

        @Override
        public void printSql(BiConsumer<String, Consumer<String>> beautifyFunc, Consumer<String> appender) {
            appender.accept("batch pair first:\n");
            beautifyFunc.accept(this.first.sqlText(), appender);
            appender.accept("\nbatch pair second:\n");
            beautifyFunc.accept(this.second.sqlText(), appender);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder(128);
            this.printSql(_DialectUtils.NON_BEAUTIFY_SQL_FUNC, builder::append);
            return builder.toString();
        }


    }//PairBatchStmtImpl


    private static final class SimpleDmlStmt extends ArmySingleSqlStmt implements SimpleStmt {


        private final List<SQLParam> paramGroup;

        private final List<? extends Selection> selectionList;

        private final boolean hasOptimistic;

        private SimpleDmlStmt(StmtParams params) {
            super(params);
            this.paramGroup = params.paramList();
            this.selectionList = params.selectionList();
            this.hasOptimistic = params.hasOptimistic();
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


    }//MinDml


    private static class MinBatchDmlStmt extends ArmySingleSqlStmt implements BatchStmt {


        private final List<List<SQLParam>> paramGroupList;

        private final boolean hasOptimistic;

        private MinBatchDmlStmt(StmtParams params, List<List<SQLParam>> paramGroupList) {
            super(params);
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


        @Override
        public final boolean hasOptimistic() {
            return this.hasOptimistic;
        }


    } // MinBatchDmlStmt


    private static final class BatchQueryStmt extends ArmySingleSqlStmt implements BatchStmt {

        private final List<? extends Selection> selectionList;

        private final List<List<SQLParam>> paramGroupList;

        private final boolean hasOptimistic;

        private BatchQueryStmt(StmtParams params, List<List<SQLParam>> paramGroupList) {
            super(params);
            this.selectionList = params.selectionList();
            this.paramGroupList = Collections.unmodifiableList(paramGroupList);
            this.hasOptimistic = params.hasOptimistic();
        }

        @Override
        public List<List<SQLParam>> groupList() {
            return this.paramGroupList;
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.selectionList;
        }

        @Override
        public boolean hasOptimistic() {
            return this.hasOptimistic;
        }


    } // BatchQueryStmt

    private static final class MultiStmtBatchStmtImpl extends ArmySingleSqlStmt implements MultiStmtBatchStmt {


        private final List<? extends Selection> selectionList;

        private final List<List<SQLParam>> paramGroupList;

        private final boolean optimistic;

        private MultiStmtBatchStmtImpl(StmtParams params, List<List<SQLParam>> paramGroupList) {
            super(params);
            this.selectionList = params.selectionList();
            this.paramGroupList = _Collections.unmodifiableList(paramGroupList);
            this.optimistic = params.hasOptimistic();
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


    }//MultiStmtBatchStm


    private static class QueryStmt extends ArmySingleSqlStmt implements SimpleStmt {

        private final List<SQLParam> paramGroup;

        final List<? extends Selection> selectionList;

        private final boolean optimistic;

        private QueryStmt(StmtParams params) {
            super(params);
            this.paramGroup = params.paramList();
            this.selectionList = params.selectionList();
            this.optimistic = params.hasOptimistic();
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


    } // QueryStmt


    private static final class ArmyDeclareCursorStmt extends QueryStmt implements DeclareCursorStmt {

        private final String name;

        private final String safeName;

        private ArmyDeclareCursorStmt(CursorStmtParams params) {
            super(params);
            this.name = params.cursorName();
            this.safeName = params.safeCursorName();
        }

        @Override
        public String cursorName() {
            return this.name;
        }

        @Override
        public String safeCursorName() {
            return this.safeName;
        }


    } // ArmyDeclareCursorStmt

    private static final class TwoStmtModeQueryStmtIml extends QueryStmt implements TwoStmtQueryStmt {

        private final int idSelectionIndex;

        private final int maxColumnSize;

        private TwoStmtModeQueryStmtIml(StmtParams params, int idSelectionIndex, int maxColumnSize) {
            super(params);
            final int selectionSize = this.selectionList.size();
            assert idSelectionIndex > -1 && idSelectionIndex < selectionSize;
            assert maxColumnSize >= selectionSize;
            this.idSelectionIndex = idSelectionIndex;
            this.maxColumnSize = maxColumnSize;
        }

        @Override
        public int idSelectionIndex() {
            return this.idSelectionIndex;
        }

        @Override
        public int maxColumnSize() {
            return this.maxColumnSize;
        }

    }//TwoStmtModeQueryStmtIml

    private static class PostStmt extends ArmySingleSqlStmt implements GeneratedKeyStmt {


        private final List<SQLParam> paramList;

        final List<? extends Selection> selectionList;

        private final int rowSize;

        private final PrimaryFieldMeta<?> field;

        private final int idSelectionIndex;

        private final ObjIntConsumer<Object> consumer;

        private final boolean hasConflictClause;

        private final boolean armyAppendReturning;

        private PostStmt(InsertStmtParams params) {
            super(params);
            this.paramList = params.paramList();
            this.selectionList = params.selectionList();
            this.rowSize = params.rowSize();

            this.field = params.idField();
            this.idSelectionIndex = params.idSelectionIndex();
            this.consumer = params.idConsumer();
            this.hasConflictClause = params.hasConflictClause();
            this.armyAppendReturning = params.isArmyAppendReturning();
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
        public final boolean hasConflictClause() {
            return this.hasConflictClause;
        }

        @Override
        public final boolean isArmyAppendReturning() {
            return this.armyAppendReturning;
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


    }//PostStmt


    private static final class TwoStmtQueryPostStmt extends PostStmt implements TwoStmtQueryStmt {

        private final int maxColumnSize;

        private TwoStmtQueryPostStmt(InsertStmtParams params) {
            super(params);
            this.maxColumnSize = params.maxColumnSize();
            assert this.maxColumnSize >= this.selectionList.size();
        }

        @Override
        public int maxColumnSize() {
            return this.maxColumnSize;
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


}
