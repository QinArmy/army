package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.mysql.MySQLDqlValues;
import io.army.dialect.Dialect;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see MySQLUnionValues
 */
@SuppressWarnings("unchecked")
abstract class MySQLSimpleValues<C, U extends RowSet.DqlValues>
        extends PartRowSet<
        C,
        U,
        Void,
        Void,
        Void,
        Void,
        Void,
        Void,
        MySQLDqlValues._UnionOrderBySpec<C, U>,
        MySQLDqlValues._LimitSpec<C, U>,
        MySQLDqlValues._UnionSpec<C, U>,
        Void>
        implements RowSet.DqlValues, MySQLDqlValues._ValuesStmtValuesClause<C, U>
        , MySQLDqlValues._StaticValueRowSpec<C, U>
        , MySQLDqlValues._ValueRowCommaSpec<C, U>
        , MySQLDqlValues {


    private List<List<_Expression>> rowList;

    private List<SelectItem> selectItemList;

    private List<_Expression> columnList;

    private int columnSize = -1;


    private MySQLSimpleValues(CriteriaContext criteriaContext) {
        super(criteriaContext, JoinableClause.voidClauseSuppler());
    }

    @Override
    public final MySQLDqlValues._StaticValueRowClause<C, U> values() {
        return this;
    }

    @Override
    public final MySQLDqlValues._OrderBySpec<C, U> values(Consumer<RowConstructor> consumer) {
        if (this.rowList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        final CriteriaSupports.RowConstructorImpl constructor;
        constructor = new CriteriaSupports.RowConstructorImpl(this.criteriaContext);
        consumer.accept(constructor);
        this.rowList = constructor.endConstructor();
        return this;
    }

    @Override
    public final MySQLDqlValues._OrderBySpec<C, U> values(BiConsumer<C, RowConstructor> consumer) {
        if (this.rowList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        final CriteriaSupports.RowConstructorImpl constructor;
        constructor = new CriteriaSupports.RowConstructorImpl(this.criteriaContext);
        consumer.accept(this.criteria, constructor);
        this.rowList = constructor.endConstructor();
        return this;
    }

    @Override
    public final MySQLDqlValues._ValueRowCommaSpec<C, U> row(Object value) {
        return this.createNewRow(SQLs.param(value));
    }

    @Override
    public final MySQLDqlValues._ValueRowCommaSpec<C, U> rowLiteral(Object value) {
        return this.createNewRow(SQLs.literal(value));
    }

    @Override
    public final MySQLDqlValues._ValueRowCommaSpec<C, U> rowExp(Supplier<? extends Expression> supplier) {
        return this.createNewRow(supplier.get());
    }

    @Override
    public final MySQLDqlValues._ValueRowCommaSpec<C, U> rowExp(Function<C, ? extends Expression> function) {
        return this.createNewRow(function.apply(this.criteria));
    }


    @Override
    public final MySQLDqlValues._ValueRowCommaSpec<C, U> comma(Object value) {
        return this.addExpression(CriteriaUtils.safeParam(this.criteriaContext, value));
    }

    @Override
    public final MySQLDqlValues._ValueRowCommaSpec<C, U> commaLiteral(Object value) {
        return this.addExpression(CriteriaUtils.safeLiteral(this.criteriaContext, value));
    }

    @Override
    public final MySQLDqlValues._ValueRowCommaSpec<C, U> commaExp(Supplier<? extends Expression> supplier) {
        return this.addExpression(supplier.get());
    }

    @Override
    public final MySQLDqlValues._ValueRowCommaSpec<C, U> commaExp(Function<C, ? extends Expression> function) {
        return this.addExpression(function.apply(this.criteria));
    }


    @Override
    public final MySQLDqlValues._StaticValueRowSpec<C, U> rightParen() {
        final List<_Expression> columnList = this.columnList;
        final int currentColumnSize;
        if (columnList == null || (currentColumnSize = columnList.size()) == 0) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

        List<List<_Expression>> rowList = this.rowList;
        if (rowList == null) {
            rowList = new ArrayList<>();
            this.rowList = rowList;
        } else if (!(rowList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

        final int firstRowColumnSize = this.columnSize;
        if (firstRowColumnSize < 0) {
            this.columnSize = currentColumnSize;
        } else if (currentColumnSize != firstRowColumnSize) {
            throw _Exceptions.valuesColumnSizeNotMatch(firstRowColumnSize, rowList.size(), currentColumnSize);
        }

        rowList.add(_CollectionUtils.unmodifiableList(columnList));
        this.columnList = null;
        return this;
    }

    @Override
    public final U asValues() {
        return this.asQuery();
    }

    @Override
    public final String toString() {
        final String s;
        if (this instanceof Values && this.isPrepared()) {
            s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }

    @Override
    final void onOrderBy() {
        //no-op
    }

    @Override
    final void crossJoinEvent(boolean success) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }

    @Override
    final U internalAsRowSet(final boolean fromAsQueryMethod) {
        final List<List<_Expression>> rowList = this.rowList;
        if (!fromAsQueryMethod || this.columnList != null || !(rowList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        final List<_Expression> firstRow = rowList.get(0);
        final int columnSize = firstRow.size();
        if (columnSize == 1) {
            this.selectItemList = Collections.singletonList(firstRow.get(0).as("column_0"));
        } else {
            final List<SelectItem> selectItemList = new ArrayList<>(columnSize);
            for (int i = 0; i < columnSize; i++) {
                selectItemList.add(firstRow.get(i).as("column_" + i));
            }
            this.selectItemList = Collections.unmodifiableList(selectItemList);
        }
        this.rowList = Collections.unmodifiableList(rowList);
        return (U) this;
    }

    @Override
    final MySQLDqlValues._UnionOrderBySpec<C, U> createBracketQuery(RowSet rowSet) {
        return MySQLUnionValues.bracket((DqlValues) rowSet);
    }

    @Override
    final MySQLDqlValues._UnionOrderBySpec<C, U> getNoActionUnionRowSet(RowSet rowSet) {
        return MySQLUnionValues.noActionValues((DqlValues) rowSet);
    }

    @Override
    final MySQLDqlValues._UnionOrderBySpec<C, U> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return MySQLUnionValues.union((U) left, unionType, (DqlValues) right);
    }

    @Override
    final void internalClear() {
        this.rowList = null;
        this.selectItemList = null;
    }


    @Override
    final Void asUnionAndRowSet(UnionType unionType) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }

    @Override
    public final List<SelectItem> selectItemList() {
        prepared();
        return this.selectItemList;
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        if (!(this instanceof SubValues)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        context.dialect().rowSet(this, context);
    }


    private MySQLDqlValues._ValueRowCommaSpec<C, U> createNewRow(final @Nullable Expression value) {
        if (this.columnList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        if (!(value instanceof ArmyExpression)) {
            throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
        }
        final List<_Expression> columnList = new ArrayList<>();
        columnList.add((ArmyExpression) value);
        this.columnList = columnList;
        return this;
    }

    private MySQLDqlValues._ValueRowCommaSpec<C, U> addExpression(final @Nullable Expression value) {
        final List<_Expression> columnList = this.columnList;
        if (columnList == null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        if (!(value instanceof ArmyExpression)) {
            throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
        }
        columnList.add((ArmyExpression) value);
        return this;
    }


    private static final class SimpleValues<C> extends MySQLSimpleValues<C, Values>
            implements Values {

        private SimpleValues(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }

    }//SimpleValues


}
