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

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Selection;
import io.army.criteria.impl.inner._SelectionMap;
import io.army.criteria.impl.inner._ValuesQuery;
import io.army.util._Assert;
import io.army.util._Collections;
import io.army.util._Exceptions;

import io.army.lang.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


abstract class SimpleValues<I extends Item, OR, OD, LR, LO, LF, SP> extends LimitRowOrderByClause<OR, OD, LR, LO, LF>
        implements _ValuesQuery,
        Values._ValuesDynamicColumnClause,
        Values._ValueStaticColumnSpaceClause,
        Values._ValueStaticColumnDualCommaClause,
        Values._ValueStaticColumnQuadraCommaClause,
        Values._ValueStaticColumnOctupleCommaClause,
        Statement._AsValuesClause<I>,
        RowSet._StaticUnionClause<SP>,
        RowSet._StaticExceptClause<SP>,
        RowSet._StaticIntersectClause<SP>,
        RowSet._StaticMinusClause<SP>,
        _SelectionMap {

    private List<Object> columnList;

    private List<List<Object>> rowList = _Collections.arrayList();

    private List<_Selection> selectionList;

    private Map<String, Selection> selectionMap;

    private Boolean prepared;

    SimpleValues(CriteriaContext context) {
        super(context);
        ContextStack.push(this.context);
    }

    @Override
    public final Values._ValuesDynamicColumnClause column(@Nullable Object exp) {
        onAddColumn(exp);
        return this;
    }

    @Override
    public final Values._ValuesDynamicColumnClause column(@Nullable Object exp1, @Nullable Object exp2) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        return this;
    }

    @Override
    public final Values._ValuesDynamicColumnClause column(@Nullable Object exp1, @Nullable Object exp2, @Nullable Object exp3) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        return this;
    }

    @Override
    public final Values._ValuesDynamicColumnClause column(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                          @Nullable Object exp4) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);
        return this;
    }

    @Override
    public final Values._ValuesDynamicColumnClause column(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                          @Nullable Object exp4, @Nullable Object exp5) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        return this;
    }

    @Override
    public final Values._ValuesDynamicColumnClause column(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                          @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        onAddColumn(exp6);
        return this;
    }

    @Override
    public final Values._ValuesDynamicColumnClause column(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                          @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6,
                                                          @Nullable Object exp7) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        onAddColumn(exp6);
        onAddColumn(exp7);
        return this;
    }

    @Override
    public final Values._ValuesDynamicColumnClause column(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                          @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6,
                                                          @Nullable Object exp7, @Nullable Object exp8) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        onAddColumn(exp6);
        onAddColumn(exp7);
        onAddColumn(exp8);
        return this;
    }

    @Override
    public final Item space(@Nullable Object exp) {
        onAddColumn(exp);
        return this;
    }

    @Override
    public final Values._ValueStaticColumnDualCommaClause space(@Nullable Object exp1, @Nullable Object exp2) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        return this;
    }

    @Override
    public final Item space(@Nullable Object exp1, @Nullable Object exp2, @Nullable Object exp3) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        return this;
    }

    @Override
    public final Values._ValueStaticColumnQuadraCommaClause space(@Nullable Object exp1, Object exp2, @Nullable Object exp3, @Nullable Object exp4) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);
        return this;
    }


    @Override
    public final Values._ValueStaticColumnOctupleCommaClause space(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                                   @Nullable Object exp4, @Nullable Object exp5) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        return this;
    }

    @Override
    public final Values._ValueStaticColumnOctupleCommaClause space(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                                   @Nullable Object exp4, @Nullable Object exp5,
                                                                   @Nullable Object exp6) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        onAddColumn(exp6);
        return this;
    }

    @Override
    public final Values._ValueStaticColumnOctupleCommaClause space(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                                   @Nullable Object exp4, @Nullable Object exp5,
                                                                   @Nullable Object exp6, @Nullable Object exp7) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        onAddColumn(exp6);
        onAddColumn(exp7);
        return this;
    }

    @Override
    public final Values._ValueStaticColumnOctupleCommaClause space(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                                   @Nullable Object exp4, @Nullable Object exp5,
                                                                   @Nullable Object exp6, @Nullable Object exp7,
                                                                   @Nullable Object exp8) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        onAddColumn(exp6);
        onAddColumn(exp7);
        onAddColumn(exp8);
        return this;
    }

    @Override
    public final Item comma(@Nullable Object exp) {
        onAddColumn(exp);
        return this;
    }

    @Override
    public final Values._ValueStaticColumnDualCommaClause comma(@Nullable Object exp1, @Nullable Object exp2) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        return this;
    }

    @Override
    public final Item comma(@Nullable Object exp1, @Nullable Object exp2, @Nullable Object exp3) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        return this;
    }

    @Override
    public final Values._ValueStaticColumnQuadraCommaClause comma(@Nullable Object exp1, Object exp2, @Nullable Object exp3, @Nullable Object exp4) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);
        return this;
    }


    @Override
    public final Values._ValueStaticColumnOctupleCommaClause comma(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                                   @Nullable Object exp4, @Nullable Object exp5) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        return this;
    }

    @Override
    public final Values._ValueStaticColumnOctupleCommaClause comma(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                                   @Nullable Object exp4, @Nullable Object exp5,
                                                                   @Nullable Object exp6) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        onAddColumn(exp6);
        return this;
    }

    @Override
    public final Values._ValueStaticColumnOctupleCommaClause comma(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                                   @Nullable Object exp4, @Nullable Object exp5,
                                                                   @Nullable Object exp6, @Nullable Object exp7) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        onAddColumn(exp6);
        onAddColumn(exp7);
        return this;
    }

    @Override
    public final Values._ValueStaticColumnOctupleCommaClause comma(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                                   @Nullable Object exp4, @Nullable Object exp5,
                                                                   @Nullable Object exp6, @Nullable Object exp7,
                                                                   @Nullable Object exp8) {
        onAddColumn(exp1);
        onAddColumn(exp2);
        onAddColumn(exp3);
        onAddColumn(exp4);

        onAddColumn(exp5);
        onAddColumn(exp6);
        onAddColumn(exp7);
        onAddColumn(exp8);
        return this;
    }


    protected final void endCurrentRow() {
        List<Object> columnList = this.columnList;
        if (columnList == null) {
            throw CriteriaUtils.dontAddAnyItem();
        } else if (!(columnList instanceof ArrayList)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        final List<List<Object>> rowList = this.rowList;

        if (!(rowList instanceof ArrayList)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }

        final int columnSize, rowSize;
        columnSize = columnList.size();
        rowSize = rowList.size();
        if (rowSize > 0) {
            if (columnSize != rowList.get(0).size()) {
                String m = String.format("Row[%s (based 1)] column count[%s] and first row column count[%s] not match.",
                        rowSize + 1, columnSize, rowList.get(0).size());
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            this.context.onValuesRowEnd();
        } else if (columnSize == 1) {
            final List<_Selection> selectionList;
            selectionList = Collections.singletonList(ArmySelections.forExp(convertToExpression(columnList.get(0)), columnAlias(0)));
            this.selectionList = selectionList;
            this.context.registerValuesSelectionList(selectionList);
        } else {
            List<_Selection> selectionList = _Collections.arrayList(columnSize);
            for (int i = 0; i < columnSize; i++) {
                selectionList.add(ArmySelections.forExp(convertToExpression(columnList.get(i)), columnAlias(i)));
            }
            this.selectionList = selectionList = Collections.unmodifiableList(selectionList);
            this.context.registerValuesSelectionList(selectionList);
        }

        if (columnSize == 1) {
            rowList.add(Collections.singletonList(columnList.get(0)));
        } else {
            rowList.add(Collections.unmodifiableList(columnList));
        }
        this.columnList = null;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = Boolean.FALSE;
        this.rowList = null;
        this.selectionList = null;
        this.clearOrderByList();
    }

    @Override
    public final List<_Selection> selectItemList() {
        final List<_Selection> list = this.selectionList;
        if (list == null) {
            throw _Exceptions.castCriteriaApi();
        }
        return list;
    }

    @Override
    public final List<List<Object>> rowList() {
        final List<List<Object>> list = this.rowList;
        if (list == null || list instanceof ArrayList) {
            throw _Exceptions.castCriteriaApi();
        }
        return list;
    }

    @Override
    public final int selectionSize() {
        final List<_Selection> list = this.selectionList;
        if (list == null) {
            throw _Exceptions.castCriteriaApi();
        }
        return list.size();
    }

    @Override
    public final Selection refSelection(final String name) {
        Map<String, Selection> selectionMap = this.selectionMap;
        if (selectionMap != null) {
            return selectionMap.get(name);
        }
        final List<_Selection> list = this.selectionList;
        if (list == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        final int selectionSize = list.size();
        if (selectionSize == 1) {
            final Selection selection;
            selection = list.get(0);
            selectionMap = Collections.singletonMap(selection.label(), selection);
        } else {
            selectionMap = _Collections.hashMap((int) (selectionSize / 0.75F));
            for (Selection selection : list) {
                selectionMap.put(selection.label(), selection);
            }
            assert selectionMap.size() == selectionSize;
            selectionMap = Collections.unmodifiableMap(selectionMap);
        }

        this.selectionMap = selectionMap;
        return selectionMap.get(name);
    }

    @Override
    public final List<? extends Selection> refAllSelection() {
        final List<_Selection> list = this.selectionList;
        if (list == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }

    @Override
    public final I asValues() {
        this.endValuesStatement(false);
        return this.onAsValues();
    }

    @Override
    public final SP union() {
        return this.onUnion(_UnionType.UNION);
    }

    @Override
    public final SP unionAll() {
        return this.onUnion(_UnionType.UNION_ALL);
    }

    @Override
    public final SP unionDistinct() {
        return this.onUnion(_UnionType.UNION_DISTINCT);
    }

    @Override
    public final SP except() {
        return this.onUnion(_UnionType.EXCEPT);
    }

    @Override
    public final SP exceptAll() {
        return this.onUnion(_UnionType.EXCEPT_ALL);
    }

    @Override
    public final SP exceptDistinct() {
        return this.onUnion(_UnionType.EXCEPT_DISTINCT);
    }

    @Override
    public final SP intersect() {
        return this.onUnion(_UnionType.INTERSECT);
    }

    @Override
    public final SP intersectAll() {
        return this.onUnion(_UnionType.INTERSECT_ALL);
    }

    @Override
    public final SP intersectDistinct() {
        return this.onUnion(_UnionType.INTERSECT_DISTINCT);
    }

    @Override
    public final SP minus() {
        return this.onUnion(_UnionType.MINUS);
    }

    @Override
    public final SP minusAll() {
        return this.onUnion(_UnionType.MINUS_ALL);
    }

    @Override
    public final SP minusDistinct() {
        return this.onUnion(_UnionType.MINUS_DISTINCT);
    }

    abstract String columnAlias(int columnIndex);

    abstract I onAsValues();

    abstract SP createUnionValues(_UnionType unionType);


    final void endStmtBeforeCommand() {
        this.endValuesStatement(true);
    }


    private SP onUnion(_UnionType unionType) {
        this.endValuesStatement(false);
        return this.createUnionValues(unionType);
    }


    private void onAddColumn(final @Nullable Object expOrLiteral) {
        List<Object> list = this.columnList;
        if (list == null) {
            this.columnList = list = _Collections.arrayList();
        } else if (!(list instanceof ArrayList)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }

        if (expOrLiteral == null) {
            list.add(SQLs.NULL);
        } else if (expOrLiteral == SQLs.DEFAULT) {
            throw ContextStack.clearStackAndCriteriaError("VALUES don't support DEFAULT key word");
        } else {
            list.add(expOrLiteral);
        }
    }

    private void endValuesStatement(final boolean beforeWordValues) {
        _Assert.nonPrepared(this.prepared);

        if (beforeWordValues) {
            this.context.endContextBeforeCommand();
        } else {
            if (this.columnList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final List<List<Object>> rowList = this.rowList;
            if (this.selectionList == null || !(rowList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.rowList = _Collections.unmodifiableList(rowList);
            this.endOrderByClauseIfNeed();
            this.prepared = Boolean.TRUE;
        }
        ContextStack.pop(this.context);
    }

    private Expression convertToExpression(final @Nullable Object column) {
        Expression expression;
        if (column == null) {
            expression = SQLs.NULL;
        } else if (column instanceof Expression) {
            expression = (Expression) column;
        } else {
            expression = SQLs.literalValue(column);
        }
        return expression;
    }

    @SuppressWarnings("unchecked")
    static abstract class WithSimpleValues<I extends Item, B extends CteBuilderSpec, WE extends Item, OR, OD, LR, LO, LF, SP>
            extends SimpleValues<I, OR, OD, LR, LO, LF, SP>
            implements DialectStatement._DynamicWithClause<B, WE>, ArmyStmtSpec {

        private boolean recursive;

        private List<_Cte> cteList;

        WithSimpleValues(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
            super(context);
            if (withSpec != null) {
                this.recursive = withSpec.isRecursive();
                this.cteList = withSpec.cteList();
            }
        }

        @Override
        public final WE with(Consumer<B> consumer) {
            return endDynamicWithClause(false, consumer, true);
        }


        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            return endDynamicWithClause(true, consumer, true);
        }


        @Override
        public final WE ifWith(Consumer<B> consumer) {
            return endDynamicWithClause(false, consumer, false);
        }


        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            return endDynamicWithClause(true, consumer, false);
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            final List<_Cte> list = this.cteList;
            if (list == null) {
                throw _Exceptions.castCriteriaApi();
            }
            return list;
        }


        abstract B createCteBuilder(boolean recursive);


        final WE endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, true);
            return (WE) this;
        }

        @SuppressWarnings("unchecked")
        private WE endDynamicWithClause(final boolean recursive, final Consumer<B> consumer, final boolean required) {
            final B builder;
            builder = createCteBuilder(recursive);

            CriteriaUtils.invokeConsumer(builder, consumer);

            ((CriteriaSupports.CteBuilder) builder).endLastCte();

            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, required);
            return (WE) this;
        }


    } // WithSimpleValues


    static final class UnionSubValues extends UnionSubRowSet implements ArmySubValues {

        UnionSubValues(RowSet left, _UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }


    }//UnionSubValues

    static final class UnionValues extends UnionRowSet implements ArmyValues {

        UnionValues(Values left, _UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }


    }//UnionSelect

}
