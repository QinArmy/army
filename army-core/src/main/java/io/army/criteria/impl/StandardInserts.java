package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;

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
        return new StandardValueOptionClause<>(criteria);
    }

    static <C> Insert._StandardSubQueryInsertClause<C> rowSetInsert(@Nullable C criteria) {
        return new StandardSubQueryInsertIntoClause<>(criteria);
    }


    /*-------------------below standard domain insert syntax class-------------------*/
    private static final class StandardDomainOptionClause<C>
            extends NonQueryInsertOptionsImpl<
            Insert._StandardDomainNullOptionSpec<C>,
            Insert._StandardDomainPreferLiteralSpec<C>,
            Insert._StandardDomainInsertIntoClause<C>>
            implements Insert._StandardDomainOptionSpec<C> {

        private StandardDomainOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }


        @Override
        public <T extends IDomain> Insert._StandardDomainColumnsSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new StandardDomainValuesClause<>(this, table);
        }

        @Override
        public <T extends IDomain> Insert._StandardParentDomainColumnsSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new StandardDomainParentValuesClause<>(this, table);
        }

    }//StandardDomainOptionClause


    private static final class StandardDomainValuesClause<C, T extends IDomain>
            extends DomainValueClause<C, T, Insert._StandardDomainDefaultSpec<C, T>, Insert._InsertSpec>
            implements Insert._StandardDomainColumnsSpec<C, T>, Insert._InsertSpec {

        private final _ValuesSyntaxInsert parentStmt;


        private StandardDomainValuesClause(InsertOptions options, SimpleTableMeta<T> table) {
            super(options, table);
            this.parentStmt = null;
        }

        private StandardDomainValuesClause(StandardDomainParentValuesClause<C, ?> parentClause, ChildTableMeta<T> table) {
            super(parentClause, table);
            this.parentStmt = parentClause;
        }

        @Override
        public Insert asInsert() {
            final Insert._InsertSpec spec;
            if (this.parentStmt == null) {
                spec = new StandardDomainInsertStatement(this);
            } else {
                spec = new StandardDomainChildInsertStatement(this);
            }
            return spec.asInsert();
        }

        @Override
        Insert._StandardDomainDefaultSpec<C, T> columnListEnd() {
            return this;
        }

        @Override
        Insert._InsertSpec valuesEnd() {
            this.endColumnDefaultClause();
            return this;
        }

    }//StandardDomainInsertStatement


    private static final class StandardDomainParentValuesClause<C, P extends IDomain>
            extends InsertSupport.DomainValueClause<
            C,
            P,
            Insert._StandardParentDomainDefaultSpec<C, P>,
            Insert._InsertSpec>
            implements Insert._StandardParentDomainColumnsSpec<C, P>
            , Insert._StandardChildInsertIntoClause<C, P>
            , InsertOptions
            , Insert._InsertSpec {

        private StandardDomainParentValuesClause(InsertOptions options, ParentTableMeta<P> table) {
            super(options, table);
        }

        @Override
        public Insert._StandardChildInsertIntoClause<C, P> child() {
            this.endColumnDefaultClause();
            return this;
        }

        @Override
        public <T extends IDomain> Insert._StandardDomainColumnsSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            return new StandardDomainValuesClause<>(this, table);
        }

        @Override
        public Insert asInsert() {
            return new StandardDomainInsertStatement(this)
                    .asInsert();
        }

        @Override
        Insert._StandardParentDomainDefaultSpec<C, P> columnListEnd() {
            return this;
        }

        @Override
        Insert._InsertSpec valuesEnd() {
            this.endColumnDefaultClause();
            return this;
        }


    }//DomainParentInsertStatement


    static abstract class StandardValuesSyntaxStatement extends InsertSupport.ValueSyntaxStatement<Insert>
            implements StandardStatement, Insert, Insert._InsertSpec {

        private StandardValuesSyntaxStatement(_ValuesSyntaxInsert clause) {
            super(clause);
        }

        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

    }//StandardValuesSyntaxStatement

    private static class StandardDomainInsertStatement extends StandardValuesSyntaxStatement
            implements _Insert._DomainInsert {

        private final List<IDomain> domainList;

        private StandardDomainInsertStatement(StandardDomainValuesClause<?, ?> clause) {
            super(clause);
            this.domainList = clause.domainList();
        }

        private StandardDomainInsertStatement(StandardDomainParentValuesClause<?, ?> clause) {
            super(clause);
            this.domainList = clause.domainList();
        }

        @Override
        public final List<IDomain> domainList() {
            return this.domainList;
        }

    }//StandardDomainInsertStatement

    private static final class StandardDomainChildInsertStatement extends StandardDomainInsertStatement
            implements _Insert._ChildDomainInsert {

        private final _ValuesSyntaxInsert parentStmt;

        private StandardDomainChildInsertStatement(StandardDomainValuesClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public _ValuesSyntaxInsert parentStmt() {
            return this.parentStmt;
        }

    }//StandardDomainChildInsertStatement


    /*-------------------below standard value insert syntax class-------------------*/


    private static final class StandardStaticValuesPairClause<C, F extends TableField>
            extends StaticColumnValuePairClause<C, F, Insert._StandardStaticValuesLeftParenSpec<C, F>>
            implements Insert._StandardStaticValuesLeftParenSpec<C, F> {

        final StandardValueValuesClause<?, ?> clause;

        private StandardStaticValuesPairClause(StandardValueValuesClause<C, F> clause) {
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

    private static final class StandardValueOptionClause<C>
            extends InsertSupport.NonQueryInsertOptionsImpl<
            Insert._StandardValueNullOptionSpec<C>,
            Insert._StandardValuePreferLiteralSpec<C>,
            Insert._StandardValueInsertIntoClause<C>>
            implements Insert._StandardValueOptionSpec<C> {

        public StandardValueOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }


        @Override
        public <T extends IDomain> Insert._StandardValueColumnsSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new StandardValueValuesClause<>(this, table);
        }

        @Override
        public <T extends IDomain> Insert._StandardParentValueColumnsSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return null;
        }


    }//StandardValueInsertOptionClause

    private static final class StandardValueValuesClause<C, T extends IDomain>
            extends DynamicValueInsertValueClause<
            C,
            T,
            Insert._StandardValueDefaultSpec<C, T>,
            Insert._InsertSpec>
            implements Insert._StandardValueColumnsSpec<C, T>
            , Insert._InsertSpec {

        private final _ValuesInsert parentStmt;

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private StandardValueValuesClause(InsertOptions options, SimpleTableMeta<T> table) {
            super(options, table);
            this.parentStmt = null;
        }

        @Override
        public Insert._StaticValueLeftParenClause<C, FieldMeta<T>, Insert._InsertSpec> values() {
            return null;
        }

        @Override
        public Insert asInsert() {
            final Insert._InsertSpec spec;
            if (this.parentStmt == null) {
                spec = new StandardValueInsertStatement(this);
            } else {
                spec = new StandardParentValueInsertStatement(this);
            }
            return spec.asInsert();
        }


        @Override
        Insert._StandardValueDefaultSpec<C, T> columnListEnd() {
            return this;
        }

        @Override
        Insert._InsertSpec valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.endColumnDefaultClause();
            this.rowValuesList = rowValuesList;
            return this;
        }


    }//StandardValueValuesClause


    private static class StandardValueInsertStatement extends StandardValuesSyntaxStatement
            implements _Insert._ValuesInsert {

        private final List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private StandardValueInsertStatement(StandardValueValuesClause<?, ?> clause) {
            super(clause);
            this.rowValuesList = clause.rowValuesList;
            assert this.rowValuesList != null;
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowValuesList() {
            return this.rowValuesList;
        }


    }//StandardValueInsertStatement


    private static final class StandardParentValueInsertStatement extends StandardValueInsertStatement
            implements _Insert._ChildValuesInsert {

        private final _ValuesInsert parentStmt;

        private StandardParentValueInsertStatement(StandardValueValuesClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert parentStmt != null;
        }

        @Override
        public _ValuesInsert parentStmt() {
            return this.parentStmt;
        }


    }//StandardParentValueInsertStatement


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
