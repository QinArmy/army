package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Values;
import io.army.lang.Nullable;
import io.army.util._Assert;
import io.army.util._CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


abstract class SimpleValues<I extends Item, RR, OR, LR, LO, LF, SP> extends LimitRowOrderByClause<OR, LR, LO, LF>
        implements Values, _Values,
        Values._StaticValueLeftParenClause<RR>,
        Values._StaticValueRowCommaDualSpec<RR>,
        Values._StaticValueRowCommaQuadraSpec<RR>,
        Statement._AsValuesClause<I>,
        Query._QueryUnionClause<SP>,
        Query._QueryExceptClause<SP>,
        Query._QueryIntersectClause<SP>,
        Query._QueryMinusClause<SP>,
        CriteriaSupports.ArmyDerivedSpec {

    private List<_Expression> columnList;

    private List<List<_Expression>> rowList = new ArrayList<>();

    private List<Selection> selectionList;

    private Map<String, Selection> selectionMap;

    private Boolean prepared;

    SimpleValues(CriteriaContext context) {
        super(context);
        ContextStack.push(this.context);
    }

    @Override
    public final Statement._RightParenClause<RR> leftParen(Expression exp) {
        this.onAddColumn(exp);
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaDualSpec<RR> leftParen(Expression exp1, Expression exp2) {
        this.onAddColumn(exp1)
                .add((ArmyExpression) exp2);
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaDualSpec<RR> leftParen(Expression exp1, Expression exp2, Expression exp3) {
        final List<_Expression> columnList;
        columnList = this.onAddColumn(exp1);
        columnList.add((ArmyExpression) exp2);
        columnList.add((ArmyExpression) exp3);
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaQuadraSpec<RR> leftParen(Expression exp1, Expression exp2
            , Expression exp3, Expression exp4) {
        final List<_Expression> columnList;
        columnList = this.onAddColumn(exp1);
        columnList.add((ArmyExpression) exp2);
        columnList.add((ArmyExpression) exp3);
        columnList.add((ArmyExpression) exp4);
        return this;
    }

    @Override
    public final Statement._RightParenClause<RR> leftParen(Function<Object, Expression> valueOperator, Object value) {
        this.onAddColumn(valueOperator.apply(value));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaDualSpec<RR> leftParen(Function<Object, Expression> valueOperator
            , Object value1, Object value2) {
        this.onAddColumn(valueOperator.apply(value1))
                .add((ArmyExpression) valueOperator.apply(value2));
        return this;
    }

    @Override
    public final _RightParenClause<RR> leftParen(Function<Object, Expression> valueOperator, Object value1
            , Object value2, Object value3) {
        final List<_Expression> columnList;
        columnList = this.onAddColumn(valueOperator.apply(value1));
        columnList.add((ArmyExpression) valueOperator.apply(value2));
        columnList.add((ArmyExpression) valueOperator.apply(value3));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaQuadraSpec<RR> leftParen(Function<Object, Expression> valueOperator
            , Object value1, Object value2, Object value3, Object value4) {
        final List<_Expression> columnList;
        columnList = this.onAddColumn(valueOperator.apply(value1));
        columnList.add((ArmyExpression) valueOperator.apply(value2));
        columnList.add((ArmyExpression) valueOperator.apply(value3));
        columnList.add((ArmyExpression) valueOperator.apply(value4));
        return this;
    }

    @Override
    public final Statement._RightParenClause<RR> comma(Expression exp) {
        this.onAddColumn(exp);
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaDualSpec<RR> comma(Expression exp1, Expression exp2) {
        this.onAddColumn(exp1)
                .add((ArmyExpression) exp2);
        return this;
    }

    @Override
    public final Statement._RightParenClause<RR> comma(Expression exp1, Expression exp2, Expression exp3) {
        final List<_Expression> columnList;
        columnList = this.onAddColumn(exp1);
        columnList.add((ArmyExpression) exp2);
        columnList.add((ArmyExpression) exp3);
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaQuadraSpec<RR> comma(Expression exp1, Expression exp2, Expression exp3
            , Expression exp4) {
        final List<_Expression> columnList;
        columnList = this.onAddColumn(exp1);
        columnList.add((ArmyExpression) exp2);
        columnList.add((ArmyExpression) exp3);
        columnList.add((ArmyExpression) exp4);
        return this;
    }

    @Override
    public final Statement._RightParenClause<RR> comma(Function<Object, Expression> valueOperator, Object value) {
        this.onAddColumn(valueOperator.apply(value));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaDualSpec<RR> comma(Function<Object, Expression> valueOperator
            , Object value1, Object value2) {
        this.onAddColumn(valueOperator.apply(value1))
                .add((ArmyExpression) valueOperator.apply(value2));
        return this;
    }

    @Override
    public final Statement._RightParenClause<RR> comma(Function<Object, Expression> valueOperator, Object value1
            , Object value2, Object value3) {
        final List<_Expression> columnList;
        columnList = this.onAddColumn(valueOperator.apply(value1));
        columnList.add((ArmyExpression) valueOperator.apply(value2));
        columnList.add((ArmyExpression) valueOperator.apply(value3));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaQuadraSpec<RR> comma(Function<Object, Expression> valueOperator
            , Object value1, Object value2, Object value3, Object value4) {
        final List<_Expression> columnList;
        columnList = this.onAddColumn(valueOperator.apply(value1));
        columnList.add((ArmyExpression) valueOperator.apply(value2));
        columnList.add((ArmyExpression) valueOperator.apply(value3));
        columnList.add((ArmyExpression) valueOperator.apply(value4));
        return this;
    }


    @SuppressWarnings("unchecked")
    @Override
    public final RR rightParen() {
        List<_Expression> columnList = this.columnList;
        if (!(columnList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final List<List<_Expression>> rowList = this.rowList;

        if (!(rowList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final int columnSize, rowSize;
        columnSize = columnList.size();
        rowSize = rowList.size();
        if (rowSize > 0) {
            if (columnSize != rowList.get(0).size()) {
                String m = String.format("Row[%s] column count[%s] and first row column count[%s] not match."
                        , rowList.size(), columnSize, rowList.get(0).size());
                throw ContextStack.criteriaError(this.context, m);
            }
        } else if (columnSize == 1) {
            this.selectionList = Collections.singletonList(ArmySelections.forExp(columnList.get(0), this.columnAlias(0)));
        } else {
            final List<Selection> selectionList = new ArrayList<>(columnSize);
            for (int i = 0; i < columnSize; i++) {
                selectionList.add(ArmySelections.forExp(columnList.get(i), this.columnAlias(i)));
            }
            this.selectionList = selectionList;
        }

        if (columnSize == 1) {
            rowList.add(Collections.singletonList(columnList.get(0)));
        } else {
            rowList.add(Collections.unmodifiableList(columnList));
        }
        this.columnList = null;
        return (RR) this;
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
    public final List<List<_Expression>> rowList() {
        final List<List<_Expression>> list = this.rowList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final int selectionSize() {
        final List<Selection> list = this.selectionList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list.size();
    }

    @Override
    public final Selection selection(String derivedAlias) {
        Map<String, Selection> selectionMap = this.selectionMap;
        if (selectionMap == null) {
            final List<Selection> list = this.selectionList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final int selectionSize = list.size();
            if (selectionSize == 1) {
                final Selection selection;
                selection = list.get(0);
                selectionMap = Collections.singletonMap(selection.selectionName(), selection);
            } else {
                selectionMap = new HashMap<>((int) (selectionSize / 0.75F));
                for (Selection selection : list) {
                    selectionMap.put(selection.selectionName(), selection);
                }
                selectionMap = Collections.unmodifiableMap(selectionMap);
            }
            assert selectionMap.size() == list.size();
            this.selectionMap = selectionMap;
        }

        return selectionMap.get(derivedAlias);
    }

    @Override
    public final List<Selection> selectionList() {
        final List<Selection> list = this.selectionList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<String> columnAliasList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void setColumnAliasList(final List<String> aliasList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final I asValues() {
        this.endValuesStatement(false);
        return this.onAsValues();
    }

    @Override
    public final SP union() {
        return this.onUnion(UnionType.UNION);
    }

    @Override
    public final SP unionAll() {
        return this.onUnion(UnionType.UNION_ALL);
    }

    @Override
    public final SP unionDistinct() {
        return this.onUnion(UnionType.UNION_DISTINCT);
    }

    @Override
    public final SP except() {
        return this.onUnion(UnionType.EXCEPT);
    }

    @Override
    public final SP exceptAll() {
        return this.onUnion(UnionType.EXCEPT_ALL);
    }

    @Override
    public final SP exceptDistinct() {
        return this.onUnion(UnionType.EXCEPT_DISTINCT);
    }

    @Override
    public final SP intersect() {
        return this.onUnion(UnionType.INTERSECT);
    }

    @Override
    public final SP intersectAll() {
        return this.onUnion(UnionType.INTERSECT_ALL);
    }

    @Override
    public final SP intersectDistinct() {
        return this.onUnion(UnionType.INTERSECT_DISTINCT);
    }

    @Override
    public final SP minus() {
        return this.onUnion(UnionType.MINUS);
    }

    @Override
    public final SP minusAll() {
        return this.onUnion(UnionType.MINUS_ALL);
    }

    @Override
    public final SP minusDistinct() {
        return this.onUnion(UnionType.MINUS_DISTINCT);
    }

    abstract String columnAlias(int columnIndex);

    abstract I onAsValues();

    abstract SP createUnionValues(UnionType unionType);


    final void endStmtBeforeCommand() {
        this.endValuesStatement(true);
    }


    private SP onUnion(UnionType unionType) {
        this.endValuesStatement(false);
        return this.createUnionValues(unionType);
    }


    private List<_Expression> onAddColumn(final @Nullable Expression expression) {
        List<_Expression> list = this.columnList;
        if (list == null) {
            list = new ArrayList<>();
            this.columnList = list;
        } else if (!(list instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (expression == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (!(expression instanceof ArmyExpression)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        list.add((ArmyExpression) expression);
        return list;
    }

    private void endValuesStatement(final boolean beforeWordValues) {
        _Assert.nonPrepared(this.prepared);

        if (beforeWordValues) {
            this.context.endContextBeforeCommand();
        } else {
            if (this.columnList != null) {
                //here,dynamic values
                this.rightParen();
            }
            final List<List<_Expression>> rowList = this.rowList;
            if (this.selectionList == null
                    || !(rowList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.rowList = _CollectionUtils.unmodifiableList(rowList);
            this.endOrderByClause();
            this.prepared = Boolean.TRUE;
            this.context.endContext();
        }
        ContextStack.pop(this.context);
    }


    static abstract class WithSimpleValues<I extends Item, B extends CteBuilderSpec, WE, RR, OR, LR, LO, LF, SP>
            extends SimpleValues<I, RR, OR, LR, LO, LF, SP>
            implements DialectStatement._DynamicWithClause<B, WE>
            , ArmyStmtSpec {

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
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endWithClause(builder, true);
        }

        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endWithClause(builder, true);
        }

        @Override
        public final WE ifWith(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endWithClause(builder, false);
        }

        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endWithClause(builder, false);
        }


        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            final List<_Cte> list = this.cteList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }


        abstract B createCteBuilder(boolean recursive);


        final WE endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, true);
            return (WE) this;
        }

        private WE endWithClause(final B builder, final boolean required) {
            final boolean recursive;
            recursive = builder.isRecursive();
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, required);
            return (WE) this;
        }


    }//WithSimpleValues


    static final class RowConstructorImpl implements RowConstructor {

        private final SimpleValues<?, ?, ?, ?, ?, ?, ?> clause;

        RowConstructorImpl(SimpleValues<?, ?, ?, ?, ?, ?, ?> clause) {
            this.clause = clause;
        }

        @Override
        public RowConstructor column(Expression exp) {
            this.clause.comma(exp);
            return this;
        }

        @Override
        public RowConstructor column(Expression exp1, Expression exp2) {
            this.clause.comma(exp1, exp2);
            return this;
        }

        @Override
        public RowConstructor column(Expression exp1, Expression exp2, Expression exp3) {
            this.clause.comma(exp1, exp2, exp3);
            return this;
        }

        @Override
        public RowConstructor column(Expression exp1, Expression exp2, Expression exp3, Expression exp4) {
            this.clause.comma(exp1, exp2, exp3, exp4);
            return this;
        }

        @Override
        public RowConstructor column(Function<Object, Expression> valueOperator, Object value) {
            this.clause.comma(valueOperator.apply(value));
            return this;
        }

        @Override
        public RowConstructor column(Function<Object, Expression> valueOperator, Object value1, Object value2) {
            this.clause.comma(valueOperator, value1, value2);
            return this;
        }

        @Override
        public RowConstructor column(Function<Object, Expression> valueOperator, Object value1
                , Object value2, Object value3) {
            this.clause.comma(valueOperator, value1, value2, value3);
            return this;
        }

        @Override
        public RowConstructor column(Function<Object, Expression> valueOperator, Object value1
                , Object value2, Object value3, Object value4) {
            this.clause.comma(valueOperator, value1, value2, value3, value4);
            return this;
        }

        @Override
        public RowConstructor row() {
            this.clause.rightParen();
            return this;
        }


    }//RowConstructorImpl

    static final class UnionSubValues extends UnionSubRowSet implements SubValues {

        UnionSubValues(RowSet left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }


    }//UnionSubValues

    static final class UnionValues extends UnionRowSet implements Values {

        UnionValues(RowSet left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }


    }//UnionSelect

}
