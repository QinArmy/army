package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard value insert statement.
 * </p>
 *
 * @since 1.0
 */
abstract class StandardInserts extends InsertSupport {


    private StandardInserts() {
        throw new UnsupportedOperationException();
    }

    static <C> Insert._StandardDomainOptionSpec<C> domainInsert(@Nullable C criteria) {
        return new StandardDomainOptionClause<>(criteria);
    }

    static <C> Insert._StandardValueOptionSpec<C> valueInsert(@Nullable C criteria) {
        return new StandardValueInsertOptionClause<>(criteria);
    }

    static <C> Insert._StandardSubQueryInsertClause<C> rowSetInsert(@Nullable C criteria) {
        return new StandardSubQueryInsertIntoClause<>(criteria);
    }


    /*-------------------below standard domain insert syntax class-------------------*/
    private static final class StandardDomainOptionClause<C>
            extends InsertSupport.DomainInsertOptionsImpl<
            Insert._StandardDomainNullOptionSpec<C>,
            Insert._StandardDomainPreferLiteralSpec<C>,
            Insert._StandardDomainInsertIntoClause<C>>
            implements Insert._StandardDomainOptionSpec<C> {

        private StandardDomainOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> Insert._StandardDomainColumnsSpec<C, T, FieldMeta<T>> insertInto(SingleTableMeta<T> table) {
            return new StandardDomainInsertStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> Insert._StandardDomainColumnsSpec<C, T, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table) {
            return new StandardDomainInsertStatement<>(this, table);
        }


    }//StandardDomainOptionClause


    static final class StandardDomainInsertStatement<C, T extends IDomain, F extends TableField>
            extends DomainValueClause<C, T, F, Insert._StandardDomainCommonExpSpec<C, T, F>, Insert._InsertSpec>
            implements Insert._StandardDomainColumnsSpec<C, T, F>, StandardStatement, Insert, Insert._InsertSpec {


        private Boolean prepared;

        private StandardDomainInsertStatement(InsertOptions options, TableMeta<T> table) {
            super(options, table);
        }

        @Override
        public Insert asInsert() {
            _Assert.nonPrepared(this.prepared);
            CriteriaContextStack.clearContextStack(this.criteriaContext);
            this.prepared = Boolean.TRUE;
            return this;
        }

        @Override
        public void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }


        @Override
        _StandardDomainCommonExpSpec<C, T, F> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }

        @Override
        public void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
            super.clear();
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

        @Override
        _InsertSpec valuesEnd() {
            return this;
        }


    }//StandardDomainInsertStatement


    /*-------------------below standard value insert syntax class-------------------*/


    private static final class StandardStaticValueClause<C, F extends TableField>
            extends InsertSupport.StaticColumnValuePairClause<C, F, Insert._InsertSpec>
            implements Insert._StandardStaticValueLeftParenClause<C, F> {

        final StandardValueInsertStatement<?, ?> clause;

        private StandardStaticValueClause(StandardValueInsertStatement<C, F> clause) {
            super(clause.criteriaContext, clause::containField);
            this.clause = clause;
        }


        @Override
        public Insert._InsertSpec rightParen() {
            return this.clause.valueClauseEnd(this.endValuesClause());
        }


    }//StandardStaticValueClause


    private static final class StandardStaticValuesPairClause<C, F extends TableField>
            extends StaticColumnValuePairClause<C, F, Insert._StandardStaticValuesLeftParenSpec<C, F>>
            implements Insert._StandardStaticValuesLeftParenSpec<C, F> {

        final StandardValueInsertStatement<?, ?> clause;

        private StandardStaticValuesPairClause(StandardValueInsertStatement<C, F> clause) {
            super(clause.criteriaContext, clause::containField);
            this.clause = clause;
        }

        @Override
        public Insert asInsert() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .asInsert();
        }

        @Override
        public Insert._StandardStaticValuesLeftParenSpec<C, F> rightParen() {
            this.endCurrentRow();
            return this;
        }


    }//StandardStaticValuesClause

    private static final class StandardValueInsertOptionClause<C>
            extends InsertSupport.InsertOptionsImpl<
            Insert._StandardValueNullOptionSpec<C>,
            Insert._StandardValueInsertIntoClause<C>>
            implements Insert._StandardValueOptionSpec<C> {

        public StandardValueInsertOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> Insert._StandardValueColumnsSpec<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table) {
            return new StandardValueInsertStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> Insert._StandardValueColumnsSpec<C, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table) {
            return new StandardValueInsertStatement<>(this, table);
        }

    }//StandardValueInsertOptionClause

    static final class StandardValueInsertStatement<C, F extends TableField>
            extends DynamicValueInsertValueClause<
            C,
            F,
            Insert._StandardValueCommonExpSpec<C, F>,
            Insert._InsertSpec>
            implements Insert._StandardValueColumnsSpec<C, F>, StandardStatement
            , Insert, Insert._InsertSpec, _Insert._ValueInsert {

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private Boolean prepared;

        private StandardValueInsertStatement(InsertOptions options, TableMeta<?> table) {
            super(options, table);
        }

        @Override
        public Insert._StandardStaticValueLeftParenClause<C, F> value() {
            return new StandardStaticValueClause<>(this);
        }

        @Override
        public Insert._StandardStaticValuesLeftParenClause<C, F> values() {
            return new StandardStaticValuesPairClause<>(this);
        }

        @Override
        public Insert asInsert() {
            _Assert.nonPrepared(this.prepared);
            if (this.rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            CriteriaContextStack.clearContextStack(this.criteriaContext);
            this.prepared = Boolean.TRUE;
            return this;
        }

        @Override
        public void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
            super.clear();
            this.rowValuesList = null;
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

        @Override
        public List<Map<FieldMeta<?>, _Expression>> rowValuesList() {
            _Assert.prepared(this.prepared);
            return this.rowValuesList;
        }

        @Override
        _StandardValueCommonExpSpec<C, F> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }

        @Override
        Insert._InsertSpec valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return this;
        }


    }//StandardValueInsert


    /**
     * @see #rowSetInsert(Object)
     */
    private static final class StandardSubQueryInsertIntoClause<C> implements Insert._StandardSubQueryInsertClause<C> {

        private final CriteriaContext criteriaContext;

        private StandardSubQueryInsertIntoClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> Insert._StandardSingleColumnsClause<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table) {
            return new StandardSingleColumnsClause<>(this.criteriaContext, table);
        }

        @Override
        public <P extends IDomain, T extends IDomain> Insert._StandardParentColumnsClause<C, P, T> insertInto(ComplexTableMeta<P, T> table) {
            return new StandardParentColumnClause<>(this.criteriaContext, table);
        }


    }//StandardSubQueryInsertIntoClause

    /**
     * @see StandardSubQueryInsertIntoClause#insertInto(SingleTableMeta)
     */
    private static final class StandardSingleColumnsClause<C, F extends TableField>
            extends ColumnsClause<C, F, Insert._StandardSpaceSubQueryClause<C>>
            implements Insert._StandardSingleColumnsClause<C, F>, Insert._StandardSpaceSubQueryClause<C> {

        private StandardSingleColumnsClause(CriteriaContext criteriaContext, SingleTableMeta<?> table) {
            super(criteriaContext, true, table);
        }


        @Override
        public Insert._InsertSpec space(Supplier<? extends SubQuery> supplier) {
            return new StandardRowSetInsertStatement(this, supplier.get());
        }

        @Override
        public Insert._InsertSpec space(Function<C, ? extends SubQuery> function) {
            final SubQuery subQuery;
            subQuery = function.apply(this.criteriaContext.criteria());
            return new StandardRowSetInsertStatement(this, subQuery);
        }

        @Override
        Insert._StandardSpaceSubQueryClause<C> columnListEnd(int fieldSize, int childFieldSize) {
            if (fieldSize == 0 || childFieldSize > 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }


    }//StandardSingleColumnsClause


    private static final class StandardParentColumnClause<C, P extends IDomain, T extends IDomain>
            extends ColumnsClause<C, FieldMeta<P>, Insert._StandardParentSubQueryClause<C, FieldMeta<T>>>
            implements Insert._StandardParentColumnsClause<C, P, T>
            , Insert._StandardParentSubQueryClause<C, FieldMeta<T>> {

        private final ChildTableMeta<?> childTable;

        private StandardParentColumnClause(CriteriaContext criteriaContext, ChildTableMeta<?> table) {
            super(criteriaContext, true, table.parentMeta());
            this.childTable = table;
        }

        @Override
        public Insert._StandardSingleColumnsClause<C, FieldMeta<T>> space(Supplier<? extends SubQuery> supplier) {
            return new StandardChildColumnClause<>(this, supplier.get());
        }

        @Override
        public Insert._StandardSingleColumnsClause<C, FieldMeta<T>> space(Function<C, ? extends SubQuery> function) {
            return new StandardChildColumnClause<>(this, function.apply(this.criteria));
        }


        @Override
        Insert._StandardParentSubQueryClause<C, FieldMeta<T>> columnListEnd(int fieldSize, int childFieldSize) {
            if (fieldSize == 0 || childFieldSize > 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }
    }//StandardParentColumnClause


    private static final class StandardChildColumnClause<C, F extends TableField>
            extends ColumnsClause<C, F, Insert._StandardSpaceSubQueryClause<C>>
            implements Insert._StandardSpaceSubQueryClause<C>, Insert._StandardSingleColumnsClause<C, F> {
        private final _Insert parentClause;

        private final SubQuery parentRowSet;

        private StandardChildColumnClause(StandardParentColumnClause<?, ?, ?> clause, SubQuery parentRowSet) {
            super(clause.criteriaContext, true, clause.childTable);
            this.parentClause = clause;
            this.parentRowSet = parentRowSet;
        }

        @Override
        public Insert._InsertSpec space(Supplier<? extends SubQuery> supplier) {
            return new StandardRowSetInsertStatement(this, supplier.get());
        }

        @Override
        public Insert._InsertSpec space(Function<C, ? extends SubQuery> function) {
            return new StandardRowSetInsertStatement(this, function.apply(this.criteria));
        }

        @Override
        Insert._StandardSpaceSubQueryClause<C> columnListEnd(int fieldSize, int childFieldSize) {
            if (fieldSize > 0 || childFieldSize == 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }

    }//StandardChildColumnClause


    static final class StandardRowSetInsertStatement extends QueryInsertStatement<Insert>
            implements StandardStatement, Insert, Insert._InsertSpec {

        private StandardRowSetInsertStatement(StandardSingleColumnsClause<?, ?> clause, SubQuery rowSet) {
            super(clause, rowSet);
        }

        private StandardRowSetInsertStatement(StandardChildColumnClause<?, ?> clause, SubQuery childRowSet) {
            super(clause.parentClause, clause.parentRowSet, clause, childRowSet);
        }


        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }// StandardRowSetInsertStatement


}
