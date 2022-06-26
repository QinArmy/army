package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.StandardStatement;
import io.army.criteria.SubQuery;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SubQueryInsert;
import io.army.dialect.Dialect;
import io.army.dialect._Dialect;
import io.army.dialect._MockDialects;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.stmt.Stmt;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;

final class StandardSubQueryInsert<C> implements Insert._StandardSubQueryInsertClause<C> {


    static <C> Insert._StandardSubQueryInsertClause<C> create(@Nullable C criteria) {
        return new StandardSubQueryInsert<>(criteria);
    }

    private final CriteriaContext criteriaContext;

    private StandardSubQueryInsert(@Nullable C criteria) {
        this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    public <T extends IDomain> Insert._StandardSingleColumnsSpec<C, T> insertInto(SingleTableMeta<T> table) {
        CriteriaContextStack.assertNonNull(table, "table");
        return new SingleTableColumns<>(this.criteriaContext, table);
    }

    @Override
    public <P extends IDomain, T extends IDomain> Insert._StandardParentColumnsSpec<C, P, T> insertInto(ComplexTableMeta<P, T> table) {
        CriteriaContextStack.assertNonNull(table, "table");
        return new ParentTableColumns<>(this.criteriaContext, table);
    }

    private static final class SingleTableColumns<C, T extends IDomain>
            extends SubQueryInsertSupport.SubQueryColumn<C, T, Insert._StandardSubQuerySpec<C>>
            implements Insert._StandardSingleColumnsSpec<C, T> {

        private SingleTableColumns(CriteriaContext criteriaContext, SingleTableMeta<T> table) {
            super(criteriaContext, table);

        }

        @Override
        public Insert._StandardSubQuerySpec<C> rightParen() {
            return new SingleTableSubQueryInsert<>(
                    this.criteriaContext
                    , (SingleTableMeta<?>) this.table
                    , this.fieldList
            );
        }


    }//SingleTableColumns

    private static final class SingleTableSubQueryInsert<C>
            extends SubQueryInsertSupport.SubQueryClause<C, Insert._InsertSpec>
            implements Insert._InsertSpec, Insert, StandardStatement, _SubQueryInsert, Insert._StandardSubQuerySpec<C> {

        private final SingleTableMeta<?> table;

        private SingleTableSubQueryInsert(CriteriaContext criteriaContext, SingleTableMeta<?> table
                , List<FieldMeta<?>> fieldList) {
            super(criteriaContext, fieldList);
            this.table = table;
        }

        @Override
        public Insert._InsertSpec rightParen() {
            return this;
        }

        @Override
        public Insert asInsert() {
            if (this.subQuery == null) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            return this;
        }

        @Override
        public void prepared() {
            _Assert.prepared(this.subQuery != null);
        }

        @Override
        public boolean isPrepared() {
            return this.subQuery != null;
        }

        @Override
        public String mockAsString(Dialect dialect, Visible visible, boolean none) {
            final _Dialect _dialect;
            _dialect = _MockDialects.from(dialect);
            return _dialect.printStmt(_dialect.insert(this, visible), none);
        }

        @Override
        public Stmt mockAsStmt(Dialect dialect, Visible visible) {
            return _MockDialects.from(dialect).insert(this, visible);
        }

        @Override
        public String toString() {
            final String s;
            if (isPrepared()) {
                s = this.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

        @Override
        public TableMeta<?> table() {
            return this.table;
        }

        @Override
        public List<FieldMeta<?>> fieldList() {
            return this.fieldList;
        }

        @Override
        public List<FieldMeta<?>> childFieldList() {
            return Collections.emptyList();
        }

        @Override
        public SubQuery subQuery() {
            final SubQuery subQuery = this.subQuery;
            assert subQuery != null;
            return subQuery;
        }

        @Override
        public SubQuery childSubQuery() {
            //always null
            return null;
        }

        @Override
        public void clear() {
            //no-op
        }


    }//SingleTableSubQuery


    private static final class ParentTableColumns<C, P extends IDomain, T extends IDomain>
            extends SubQueryInsertSupport.SubQueryColumn<C, P, Insert._StandardParentSubQueryClause<C, T>>
            implements Insert._StandardParentColumnsSpec<C, P, T> {


        private final ComplexTableMeta<P, T> childTable;

        private ParentTableColumns(CriteriaContext criteriaContext, ComplexTableMeta<P, T> table) {
            super(criteriaContext, table.parentMeta());
            this.childTable = table;
        }

        @Override
        public Insert._StandardParentSubQueryClause<C, T> rightParen() {
            final List<FieldMeta<?>> fieldList = this.fieldList;
            if (fieldList.size() == 0) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            return new ParenTableSubQueryClause<>(this.criteriaContext, this.childTable, fieldList);
        }


    }//ParentTableColumns


    private static final class ParenTableSubQueryClause<C, T extends IDomain>
            extends SubQueryInsertSupport.SubQueryClause<C, Insert._StandardSingleColumnsSpec<C, T>>
            implements Insert._StandardParentSubQueryClause<C, T> {

        private final ChildTableMeta<T> table;

        private final List<FieldMeta<?>> fieldList;


        private ParenTableSubQueryClause(CriteriaContext criteriaContext, ChildTableMeta<T> table
                , List<FieldMeta<?>> fieldList) {
            super(criteriaContext, fieldList);
            this.table = table;
            this.fieldList = Collections.unmodifiableList(fieldList);
        }

        @Override
        public Insert._StandardSingleColumnsSpec<C, T> rightParen() {
            final SubQuery subQuery = this.subQuery;
            if (subQuery == null) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            return new ChildTableColumns<>(this.criteriaContext, this.table, this.fieldList, subQuery);
        }


    }//ParenTableSubQueryInsert


    private static final class ChildTableColumns<C, T extends IDomain>
            extends SubQueryInsertSupport.SubQueryColumn<C, T, Insert._StandardSubQuerySpec<C>>
            implements Insert._StandardSingleColumnsSpec<C, T> {

        private final List<FieldMeta<?>> parentFieldList;

        private final SubQuery parentSubQuery;

        private ChildTableColumns(CriteriaContext criteriaContext, ChildTableMeta<T> table, List<FieldMeta<?>> fieldList
                , SubQuery subQuery) {
            super(criteriaContext, table);
            this.parentFieldList = fieldList;
            this.parentSubQuery = subQuery;
        }

        @Override
        public Insert._StandardSubQuerySpec<C> rightParen() {
            if (this.fieldList.size() == 0) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            return new ChildSubQueryInsert<>(this);
        }


    }//ChildTableColumns


    private static final class ChildSubQueryInsert<C>
            extends SubQueryInsertSupport.SubQueryClause<C, Insert._InsertSpec>
            implements Insert._InsertSpec, Insert, StandardStatement, _SubQueryInsert, Insert._StandardSubQuerySpec<C> {

        private final ChildTableMeta<?> table;

        private final List<FieldMeta<?>> parentFieldList;

        private final SubQuery parentSubQuery;

        private ChildSubQueryInsert(ChildTableColumns<C, ?> columnsClause) {
            super(columnsClause.criteriaContext, columnsClause.fieldList);
            this.table = (ChildTableMeta<?>) columnsClause.table;
            this.parentFieldList = columnsClause.parentFieldList;
            this.parentSubQuery = columnsClause.parentSubQuery;
        }

        @Override
        public _InsertSpec rightParen() {
            return this;
        }

        @Override
        public Insert asInsert() {
            if (this.subQuery == null) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            return this;
        }

        @Override
        public void prepared() {
            _Assert.prepared(this.subQuery != null);
        }

        @Override
        public boolean isPrepared() {
            return this.subQuery != null;
        }

        @Override
        public String mockAsString(Dialect dialect, Visible visible, boolean none) {
            final _Dialect _dialect;
            _dialect = _MockDialects.from(dialect);
            return _dialect.printStmt(_dialect.insert(this, visible), none);
        }

        @Override
        public Stmt mockAsStmt(Dialect dialect, Visible visible) {
            return _MockDialects.from(dialect).insert(this, visible);
        }

        @Override
        public String toString() {
            final String s;
            if (isPrepared()) {
                s = this.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


        @Override
        public TableMeta<?> table() {
            return this.table;
        }

        @Override
        public List<FieldMeta<?>> fieldList() {
            return this.parentFieldList;
        }

        @Override
        public List<FieldMeta<?>> childFieldList() {
            return this.fieldList;
        }

        @Override
        public void clear() {
            //no-op
        }

        @Override
        public SubQuery subQuery() {
            return this.parentSubQuery;
        }

        @Override
        public SubQuery childSubQuery() {
            final SubQuery subQuery = this.subQuery;
            assert subQuery != null;
            return subQuery;
        }

    }// ChildSubQueryInsert


}
