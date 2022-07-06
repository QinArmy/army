package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.mysql.MySQLValues;
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
        MySQLValues._UnionOrderBySpec<C, U>,
        MySQLValues._LimitSpec<C, U>,
        MySQLValues._UnionSpec<C, U>,
        Void>
        implements RowSet.DqlValues, MySQLValues._ValuesStmtValuesClause<C, U>
        , MySQLValues._StaticValueRowSpec<C, U>
        , MySQLValues._ValueRowCommaSpec<C, U> {

    private List<List<SelectItem>> rowList;

    private List<SelectItem> selectItemList;

    private List<_Expression> columnList;

    private int columnSize = -1;


    private MySQLSimpleValues(CriteriaContext criteriaContext) {
        super(criteriaContext, JoinableClause.voidClauseSuppler());
    }

    @Override
    public final MySQLValues._StaticValueRowClause<C, U> values() {
        return this;
    }

    @Override
    public final MySQLValues._OrderBySpec<C, U> values(Consumer<RowConstructor> consumer) {
        return this;
    }

    @Override
    public final MySQLValues._OrderBySpec<C, U> values(BiConsumer<C, RowConstructor> consumer) {
        return this;
    }

    @Override
    public final MySQLValues._ValueRowCommaSpec<C, U> row(Object value) {
        return this.createNewRow(SQLs.param(value));
    }

    @Override
    public final MySQLValues._ValueRowCommaSpec<C, U> rowLiteral(Object value) {
        return this.createNewRow(SQLs.literal(value));
    }

    @Override
    public final MySQLValues._ValueRowCommaSpec<C, U> rowExp(Supplier<? extends Expression> supplier) {
        return this.createNewRow(supplier.get());
    }

    @Override
    public final MySQLValues._ValueRowCommaSpec<C, U> rowExp(Function<C, ? extends Expression> function) {
        return this.createNewRow(function.apply(this.criteria));
    }


    @Override
    public final MySQLValues._ValueRowCommaSpec<C, U> comma(@Nullable Object value) {
        if (value == null) {
            throw CriteriaContextStack.nullPointer(this.criteriaContext);
        }
        return this.addExpression(SQLs.param(value));
    }

    @Override
    public final MySQLValues._ValueRowCommaSpec<C, U> commaLiteral(@Nullable Object value) {
        if (value == null) {
            throw CriteriaContextStack.nullPointer(this.criteriaContext);
        }
        return this.addExpression(SQLs.literal(value));
    }

    @Override
    public final MySQLValues._ValueRowCommaSpec<C, U> commaExp(Supplier<? extends Expression> supplier) {
        return this.addExpression(supplier.get());
    }

    @Override
    public final MySQLValues._ValueRowCommaSpec<C, U> commaExp(Function<C, ? extends Expression> function) {
        return this.addExpression(function.apply(this.criteria));
    }


    @Override
    public final MySQLValues._StaticValueRowSpec<C, U> rightParen() {
        final List<SelectItem> columnList = this.selectItemList;
        final int currentColumnSize;
        if (columnList == null || (currentColumnSize = columnList.size()) == 0) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

        final int firstRowColumnSize = this.columnSize;
        if (firstRowColumnSize < 0) {
            this.columnSize = currentColumnSize;
        } else if (currentColumnSize != firstRowColumnSize) {
            throw _Exceptions.valuesColumnSizeNotMatch(firstRowColumnSize, rowList.size(), currentColumnSize);
        }

        List<List<SelectItem>> rowList = this.rowList;
        if (rowList == null) {
            rowList = new ArrayList<>();
            this.rowList = rowList;
        } else if (!(rowList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        rowList.add(_CollectionUtils.unmodifiableList(columnList));
        this.selectItemList = null;
        return this;
    }

    @Override
    public final U asValues() {
        return this.asQuery();
    }


    @Override
    final void crossJoinEvent(boolean success) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }

    @Override
    final U internalAsRowSet(final boolean fromAsQueryMethod) {
        final List<List<SelectItem>> rowList = this.rowList;
        if (!fromAsQueryMethod || this.selectItemList != null || !(rowList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.rowList = Collections.unmodifiableList(rowList);
        return (U) this;
    }

    @Override
    final MySQLValues._UnionOrderBySpec<C, U> createBracketQuery(RowSet rowSet) {
        return null;
    }

    @Override
    final MySQLValues._UnionOrderBySpec<C, U> getNoActionUnionRowSet(RowSet rowSet) {
        return null;
    }

    @Override
    final void internalClear() {
        this.rowList = null;
    }

    @Override
    final MySQLValues._UnionOrderBySpec<C, U> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return null;
    }

    @Override
    final Void asUnionAndRowSet(UnionType unionType) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }

    @Override
    public final List<SelectItem> selectItemList() {
        prepared();
        return this.rowList.get(0);
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        if (!(this instanceof SubValues)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        context.dialect().rowSet(this, context);
    }


    private MySQLValues._ValueRowCommaSpec<C, U> createNewRow(final @Nullable Expression value) {
        if (!(value instanceof ArmyExpression)) {
            throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
        }
        if (this.columnList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        final List<_Expression> columnList = new ArrayList<>();
        columnList.add((ArmyExpression) value);
        this.columnList = columnList;
        return this;
    }

    private MySQLValues._ValueRowCommaSpec<C, U> addExpression(final @Nullable Expression value) {
        final List<SelectItem> columnList = this.selectItemList;
        if (columnList == null) {
            throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
        }
        if (!(value instanceof ArmyExpression)) {
            throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
        }
        columnList.add(value.as("column_" + columnList.size()));
        return this;
    }


}
