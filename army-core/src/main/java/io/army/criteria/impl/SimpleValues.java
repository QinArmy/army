package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Values;
import io.army.lang.Nullable;
import io.army.util._Assert;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;


abstract class SimpleValues<I extends Item, RR, OR, LR, LO, LF> extends LimitRowOrderByClause<OR, LR, LO, LF>
        implements Values._StaticValueLeftParenClause<RR>
        , Values._StaticValueRowCommaDualSpec<RR>
        , Values._StaticValueRowCommaQuadraSpec<RR>
        , _Values, Statement._AsValuesClause<I> {

    private List<_Expression> columnList;

    private List<List<_Expression>> rowList = new ArrayList<>();

    private List<Selection> selectionList;

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
            this.selectionList = Collections.singletonList(Selections.forExp(columnList.get(0), this.columnAlias(0)));
        } else {
            final List<Selection> selectionList = new ArrayList<>(columnSize);
            for (int i = 0; i < columnSize; i++) {
                selectionList.add(Selections.forExp(columnList.get(i), this.columnAlias(i)));
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
    public final List<? extends SelectItem> selectItemList() {
        final List<Selection> list = this.selectionList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final I asValues() {
        _Assert.nonPrepared(this.prepared);
        final List<List<_Expression>> rowList = this.rowList;
        if (this.columnList != null
                || this.selectionList != null
                || !(rowList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.rowList = _CollectionUtils.unmodifiableList(rowList);
        this.endOrderByClause();
        this.prepared = Boolean.TRUE;
        return this.onAsValues();
    }

    abstract String columnAlias(int columnIndex);

    abstract I onAsValues();


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


    static final class RowConstructorImpl implements RowConstructor {

        private final SimpleValues<?, ?, ?, ?, ?, ?> clause;

        RowConstructorImpl(SimpleValues<?, ?, ?, ?, ?, ?> clause) {
            this.clause = clause;
        }

        @Override
        public RowConstructor add(Expression exp) {
            this.clause.comma(exp);
            return this;
        }

        @Override
        public RowConstructor add(Expression exp1, Expression exp2) {
            this.clause.comma(exp1, exp2);
            return this;
        }

        @Override
        public RowConstructor add(Expression exp1, Expression exp2, Expression exp3) {
            this.clause.comma(exp1, exp2, exp3);
            return this;
        }

        @Override
        public RowConstructor add(Expression exp1, Expression exp2, Expression exp3, Expression exp4) {
            this.clause.comma(exp1, exp2, exp3, exp4);
            return this;
        }

        @Override
        public RowConstructor add(Function<Object, Expression> valueOperator, Object value) {
            this.clause.comma(valueOperator.apply(value));
            return this;
        }

        @Override
        public RowConstructor add(Function<Object, Expression> valueOperator, Object value1, Object value2) {
            this.clause.comma(valueOperator, value1, value2);
            return this;
        }

        @Override
        public RowConstructor add(Function<Object, Expression> valueOperator, Object value1
                , Object value2, Object value3) {
            this.clause.comma(valueOperator, value1, value2, value3);
            return this;
        }

        @Override
        public RowConstructor add(Function<Object, Expression> valueOperator, Object value1
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

}
