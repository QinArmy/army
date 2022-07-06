package io.army.criteria.impl;

import io.army.criteria.ColumnConsumer;
import io.army.criteria.Expression;
import io.army.criteria.RowConstructor;
import io.army.criteria.impl.inner._Expression;
import io.army.lang.Nullable;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

abstract class CriteriaSupports {

    private CriteriaSupports() {
        throw new UnsupportedOperationException();
    }


    static final class RowConstructorImpl implements RowConstructor {

        final CriteriaContext criteriaContext;

        private List<List<_Expression>> rowList;

        private List<_Expression> columnList;

        RowConstructorImpl(CriteriaContext criteriaContext) {
            this.criteriaContext = criteriaContext;
        }

        @Override
        public ColumnConsumer accept(final Object value) {
            return this.addColumn(CriteriaUtils.safeParam(this.criteriaContext, value));
        }

        @Override
        public ColumnConsumer acceptLiteral(final Object value) {
            return this.addColumn(CriteriaUtils.safeLiteral(this.criteriaContext, value));
        }

        @Override
        public ColumnConsumer acceptExp(Supplier<? extends Expression> supplier) {
            return this.addColumn(supplier.get());
        }

        @Override
        public ColumnConsumer row() {
            final List<_Expression> columnList = this.columnList;
            if (columnList != null) {
                List<List<_Expression>> rowList = this.rowList;
                if (rowList == null) {
                    rowList = new ArrayList<>();
                    this.rowList = rowList;
                } else if (!(rowList instanceof ArrayList)) {
                    throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
                } else if (columnList.size() != rowList.get(0).size()) {
                    final int firstColumnSize = rowList.get(0).size();
                    throw _Exceptions.valuesColumnSizeNotMatch(firstColumnSize, rowList.size(), columnList.size());
                }
                rowList.add(_CollectionUtils.unmodifiableList(columnList));
            }
            this.columnList = new ArrayList<>();
            return this;
        }

        List<List<_Expression>> endConstructor() {
            final List<_Expression> columnList = this.columnList;
            if (!(columnList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            List<List<_Expression>> rowList = this.rowList;
            if (rowList == null) {
                rowList = Collections.singletonList(_CollectionUtils.unmodifiableList(columnList));
                this.rowList = rowList;
            } else if (rowList instanceof ArrayList) {
                rowList.add(_CollectionUtils.unmodifiableList(columnList));
                rowList = _CollectionUtils.unmodifiableList(rowList);
                this.rowList = rowList;
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.columnList = null;
            return rowList;
        }

        private ColumnConsumer addColumn(final @Nullable Expression value) {
            final List<_Expression> columnList = this.columnList;
            if (columnList == null) {
                String m = "Not found any row,please use row() method create new row.";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            columnList.add((ArmyExpression) value);
            return this;
        }

    }//RowConstructorImpl


}
