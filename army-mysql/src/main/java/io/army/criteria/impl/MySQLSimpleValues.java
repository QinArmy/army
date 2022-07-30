package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.mysql.MySQLDqlValues;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @see MySQLUnionValues
 */
@SuppressWarnings("unchecked")
abstract class MySQLSimpleValues<C, V extends RowSet.DqlValues>
        extends SimpleValues<
        C,
        V,
        MySQLDqlValues._StaticRowSpec<C, V>,
        MySQLDqlValues._OrderBySpec<C, V>,
        MySQLDqlValues._UnionOrderBySpec<C, V>,
        MySQLDqlValues._LimitSpec<C, V>,
        MySQLDqlValues._UnionSpec<C, V>>
        implements MySQLDqlValues._ValuesStmtValuesClause<C, V>
        , MySQLDqlValues._StaticRowSpec<C, V>
        , MySQLDqlValues {

    static <C> MySQLDqlValues._ValuesStmtValuesClause<C, Values> primaryValues(@Nullable C criteria) {
        return new SimpleValues<>(CriteriaContexts.primaryValuesContext(criteria));
    }

    static <C> MySQLDqlValues._ValuesStmtValuesClause<C, SubValues> subValues(@Nullable C criteria) {
        return new SimpleSubValues<>(CriteriaContexts.subValuesContext(criteria));
    }


    private List<List<_Expression>> rowList;

    private List<Selection> selectionList;

    private List<_Expression> columnList;


    private MySQLSimpleValues(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }

    @Override
    public final _StaticRowClause<C, V> values() {
        return this;
    }


    @Override
    public final Values._StaticValueLeftParenClause<_StaticRowSpec<C, V>> row() {
        return this;
    }

    @Override
    public final _StaticRowSpec<C, V> rightParen() {
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
        } else if (currentColumnSize != rowList.get(0).size()) {
            throw _Exceptions.valuesColumnSizeNotMatch(rowList.get(0).size(), rowList.size(), currentColumnSize);
        }

        if (rowList.size() == 0) {
            final List<Selection> selectionList;
            if (currentColumnSize == 1) {
                selectionList = Collections.singletonList(columnList.get(0).as("column_0"));
            } else {
                final List<Selection> tempList = new ArrayList<>(currentColumnSize);
                for (int i = 0; i < currentColumnSize; i++) {
                    tempList.add(columnList.get(i).as("column_" + i));
                }
                selectionList = Collections.unmodifiableList(tempList);
            }
            this.selectionList = selectionList;
            this.criteriaContext.selectList(selectionList);//notify context
        }
        rowList.add(_CollectionUtils.unmodifiableList(columnList));
        this.columnList = null;
        return this;
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
    public final List<List<_Expression>> rowList() {
        prepared();
        return this.rowList;
    }


    @Override
    final V onAsValues() {
        final List<List<_Expression>> rowList = this.rowList;
        if (this.columnList != null || !(rowList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.rowList = _CollectionUtils.unmodifiableList(rowList);
        return (V) this;
    }

    @Override
    final MySQLDqlValues._UnionOrderBySpec<C, V> createBracketQuery(RowSet rowSet) {
        return MySQLUnionValues.bracket(rowSet);
    }

    @Override
    final MySQLDqlValues._UnionOrderBySpec<C, V> getNoActionUnionRowSet(RowSet rowSet) {
        return MySQLUnionValues.noActionValues(rowSet);
    }

    @Override
    final MySQLDqlValues._UnionOrderBySpec<C, V> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return MySQLUnionValues.union((V) left, unionType, right);
    }

    @Override
    final void internalClear() {
        this.rowList = null;
        this.selectionList = null;
    }


    @Override
    public final List<? extends SelectItem> selectItemList() {
        prepared();
        return this.selectionList;
    }

    @Override
    public final int selectionSize() {
        prepared();
        return this.selectionList.size();
    }

    @Override
    final _OrderBySpec<C, V> dynamicValuesEnd(List<List<_Expression>> rowList) {
        if (this.rowList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.rowList = rowList;
        return this;
    }

    @Override
    final List<_Expression> createNewRow() {
        final List<List<_Expression>> rowList = this.rowList;
        if (this.columnList != null || (rowList != null && !(rowList instanceof ArrayList))) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        final List<_Expression> columnList;
        if (rowList == null) {
            columnList = new ArrayList<>();
        } else {
            columnList = new ArrayList<>(rowList.get(0).size());
        }
        this.columnList = columnList;
        return columnList;
    }

    @Override
    final List<_Expression> getCurrentRow() {
        final List<_Expression> columnList = this.columnList;
        if (columnList == null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return columnList;
    }


    private static final class SimpleValues<C> extends MySQLSimpleValues<C, Values>
            implements Values {

        private SimpleValues(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }

    }//SimpleValues

    private static final class SimpleSubValues<C> extends MySQLSimpleValues<C, SubValues>
            implements SubValues {

        private Map<String, Selection> selectionMap;

        private SimpleSubValues(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }

        @Override
        public Selection selection(final String derivedFieldName) {
            prepared();
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                selectionMap = CriteriaUtils.createSelectionMap(this.selectItemList());
                this.selectionMap = selectionMap;
            }
            return selectionMap.get(derivedFieldName);
        }

    }//SimpleSubValues


}
