package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.StandardStatement;
import io.army.criteria.SubQuery;
import io.army.criteria.Visible;
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

    static <C> Insert._StandardQueryInsertClause<C> rowSetInsert(@Nullable C criteria) {
        return new StandardQueryInsertIntoClause<>(criteria);
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
            return new StandardValueParentValuesClause<>(this, table);
        }


    }//StandardValueInsertOptionClause


    private static final class StandardStaticValuesPairClause<C, T extends IDomain>
            extends StaticColumnValuePairClause<C, T, Insert._StandardValueStaticLeftParenSpec<C, T>>
            implements Insert._StandardValueStaticLeftParenSpec<C, T> {

        final StandardValueValuesClause<?, ?> clause;

        private StandardStaticValuesPairClause(StandardValueValuesClause<C, T> clause) {
            super(clause.criteriaContext, clause.table, clause::validateFieldPair);
            this.clause = clause;
        }


        @Override
        public Insert asInsert() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .asInsert();
        }

        @Override
        public Insert._StandardValueStaticLeftParenSpec<C, T> rightParen() {
            return this;
        }

    }//StandardStaticValuesPairClause


    private static final class StandardParentStaticValuesPairClause<C, P extends IDomain>
            extends StaticColumnValuePairClause<C, P, Insert._StandardParentStaticValuesSpec<C, P>>
            implements Insert._StandardParentStaticValuesSpec<C, P> {

        private final StandardValueParentValuesClause<C, P> clause;

        private StandardParentStaticValuesPairClause(StandardValueParentValuesClause<C, P> clause) {
            super(clause.criteriaContext, clause.table, clause::validateFieldPair);
            this.clause = clause;
        }

        @Override
        public Insert asInsert() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .asInsert();
        }

        @Override
        public Insert._StandardValueChildInsertIntoClause<C, P> child() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .child();
        }

        @Override
        public Insert._StandardParentStaticValuesSpec<C, P> rightParen() {
            return this;
        }

    }//StandardParentStaticValuesPairClause

    private static final class StandardValueValuesClause<C, T extends IDomain>
            extends DynamicValueInsertValueClause<
            C,
            T,
            Insert._StandardValueDefaultSpec<C, T>,
            Insert._InsertSpec>
            implements Insert._StandardValueColumnsSpec<C, T> {

        private final _ValuesInsert parentStmt;

        private StandardValueValuesClause(InsertOptions options, SimpleTableMeta<T> table) {
            super(options, table);
            this.parentStmt = null;
        }

        private StandardValueValuesClause(StandardValueParentValuesClause<C, ?> parentStmt, ChildTableMeta<T> table) {
            super(parentStmt, table);
            this.parentStmt = parentStmt;
        }


        @Override
        public Insert._StandardValueStaticLeftParenClause<C, T> values() {
            return new StandardStaticValuesPairClause<>(this);
        }

        @Override
        Insert._StandardValueDefaultSpec<C, T> columnListEnd() {
            return this;
        }

        @Override
        Insert._InsertSpec valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            this.endColumnDefaultClause();
            final Insert._InsertSpec spec;
            if (this.parentStmt == null) {
                spec = new StandardValueInsertStatement(this, rowValuesList);
            } else {
                spec = new StandardParentValueInsertStatement(this, rowValuesList);
            }
            return spec;
        }


    }//StandardValueValuesClause

    private static final class StandardValueParentValuesClause<C, P extends IDomain>
            extends DynamicValueInsertValueClause<
            C,
            P,
            Insert._StandardParentValueDefaultSpec<C, P>,
            Insert._StandardValueChildSpec<C, P>>
            implements Insert._StandardParentValueColumnsSpec<C, P>
            , Insert._StandardValueChildSpec<C, P>
            , Insert._StandardValueChildInsertIntoClause<C, P>
            , InsertOptions
            , _Insert._ValuesInsert {

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private StandardValueParentValuesClause(InsertOptions options, ParentTableMeta<P> table) {
            super(options, table);
        }

        @Override
        public Insert._StandardParentStaticValuesClause<C, P> values() {
            return new StandardParentStaticValuesPairClause<>(this);
        }

        @Override
        public Insert asInsert() {
            final List<Map<FieldMeta<?>, _Expression>> rowValuesList = this.rowValuesList;
            if (rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new StandardValueInsertStatement(this, rowValuesList)
                    .asInsert();
        }

        @Override
        public Insert._StandardValueChildInsertIntoClause<C, P> child() {
            return this;
        }

        @Override
        public <T extends IDomain> Insert._StandardValueColumnsSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final List<Map<FieldMeta<?>, _Expression>> rowValuesList = this.rowValuesList;
            if (rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new StandardValueValuesClause<>(this, table);
        }

        @Override
        public List<Map<FieldMeta<?>, _Expression>> rowValuesList() {
            final List<Map<FieldMeta<?>, _Expression>> rowValuesList = this.rowValuesList;
            if (rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return rowValuesList;
        }

        @Override
        Insert._StandardParentValueDefaultSpec<C, P> columnListEnd() {
            return this;
        }

        @Override
        Insert._StandardValueChildSpec<C, P> valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.endColumnDefaultClause();
            this.rowValuesList = rowValuesList;
            return this;
        }

    }//StandardValueParentValuesClause


    private static class StandardValueInsertStatement extends StandardValuesSyntaxStatement
            implements _Insert._ValuesInsert {

        private final List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private StandardValueInsertStatement(StandardValueValuesClause<?, ?> clause
                , List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            super(clause);
            this.rowValuesList = rowValuesList;
        }

        private StandardValueInsertStatement(StandardValueParentValuesClause<?, ?> clause
                , List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            super(clause);
            this.rowValuesList = rowValuesList;
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowValuesList() {
            return this.rowValuesList;
        }


    }//StandardValueInsertStatement


    private static final class StandardParentValueInsertStatement extends StandardValueInsertStatement
            implements _Insert._ChildValuesInsert {

        private final _ValuesInsert parentStmt;

        private StandardParentValueInsertStatement(StandardValueValuesClause<?, ?> clause
                , List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            super(clause, rowValuesList);
            this.parentStmt = clause.parentStmt;
            assert parentStmt != null;
        }

        @Override
        public _ValuesInsert parentStmt() {
            return this.parentStmt;
        }


    }//StandardParentValueInsertStatement


    /*-----------------------below query insert class -----------------------*/


    /**
     * @see #rowSetInsert(Object)
     */
    private static final class StandardQueryInsertIntoClause<C> implements Insert._StandardQueryInsertClause<C> {

        private final CriteriaContext criteriaContext;

        private StandardQueryInsertIntoClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> Insert._StandardSingleColumnsClause<C, T> insertInto(SimpleTableMeta<T> table) {
            return new StandardQuerySpaceClause<>(this.criteriaContext, table);
        }

        @Override
        public <T extends IDomain> Insert._StandardParentColumnsClause<C, T> insertInto(ParentTableMeta<T> table) {
            return new StandardQueryParentSpaceClause<>(this.criteriaContext, table);
        }


    }//StandardSubQueryInsertIntoClause


    private static final class StandardQuerySpaceClause<C, T extends IDomain>
            extends ColumnsClause<C, T, Insert._SpaceSubQueryClause<C, Insert._InsertSpec>>
            implements Insert._StandardSingleColumnsClause<C, T>
            , Insert._SpaceSubQueryClause<C, Insert._InsertSpec>
            , Insert._InsertSpec {

        private final _Insert._QueryInsert parentStmt;

        private SubQuery subQuery;

        private StandardQuerySpaceClause(CriteriaContext criteriaContext, SimpleTableMeta<T> table) {
            super(criteriaContext, true, table);
            this.parentStmt = null;
        }

        private StandardQuerySpaceClause(StandardQueryParentSpaceClause<C, ?> parentStmt, ChildTableMeta<T> table) {
            super(parentStmt.criteriaContext, true, table);
            this.parentStmt = parentStmt;
        }


        @Override
        public Insert._InsertSpec space(Supplier<? extends SubQuery> supplier) {
            return this.acceptQuery(supplier.get());
        }

        @Override
        public Insert._InsertSpec space(Function<C, ? extends SubQuery> function) {
            return this.acceptQuery(function.apply(this.criteria));
        }

        @Override
        public Insert asInsert() {
            final Insert._InsertSpec spec;
            final SubQuery subQuery = this.subQuery;
            if (subQuery == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (this.parentStmt == null) {
                spec = new StandardQueryInsertStatement(this, subQuery);
            } else {
                spec = new StandardQueryChildInsertStatement(this, subQuery);
            }
            return spec.asInsert();
        }

        @Override
        Insert._SpaceSubQueryClause<C, Insert._InsertSpec> columnListEnd() {
            return this;
        }

        private Insert._InsertSpec acceptQuery(final @Nullable SubQuery subQuery) {
            if (this.subQuery != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (subQuery == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return this;
        }

    }//StandardQuerySpaceClause


    private static final class StandardQueryParentSpaceClause<C, P extends IDomain>
            extends InsertSupport.ColumnsClause<C, P, Insert._StandardParentSubQueryClause<C, P>>
            implements Insert._StandardParentColumnsClause<C, P>
            , Insert._StandardQueryChildPartSpec<C, P>
            , Insert._StandardQueryChildInsertIntoClause<C, P>
            , Insert._StandardParentSubQueryClause<C, P>
            , _Insert._QueryInsert {

        private SubQuery subQuery;

        private StandardQueryParentSpaceClause(CriteriaContext criteriaContext, ParentTableMeta<P> table) {
            super(criteriaContext, true, table);
        }

        @Override
        public Insert._StandardQueryChildPartSpec<C, P> space(Supplier<? extends SubQuery> supplier) {
            return this.acceptQuery(supplier.get());
        }

        @Override
        public Insert._StandardQueryChildPartSpec<C, P> space(Function<C, ? extends SubQuery> function) {
            return this.acceptQuery(function.apply(this.criteria));
        }

        @Override
        public Insert asInsert() {
            final SubQuery subQuery = this.subQuery;
            if (subQuery == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new StandardQueryInsertStatement(this, subQuery)
                    .asInsert();
        }

        @Override
        public Insert._StandardQueryChildInsertIntoClause<C, P> child() {
            return this;
        }

        @Override
        public <T extends IDomain> Insert._StandardSingleColumnsClause<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final SubQuery subQuery = this.subQuery;
            if (subQuery == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new StandardQuerySpaceClause<>(this, table);
        }


        @Override
        public SubQuery subQuery() {
            final SubQuery subQuery = this.subQuery;
            if (subQuery == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return subQuery;
        }

        @Override
        Insert._StandardParentSubQueryClause<C, P> columnListEnd() {
            return this;
        }

        private Insert._StandardQueryChildPartSpec<C, P> acceptQuery(final @Nullable SubQuery subQuery) {
            if (this.subQuery != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (subQuery == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return this;
        }


    }//StandardQueryParentSpaceClause


    static class StandardQueryInsertStatement extends QueryInsertStatement<Insert>
            implements StandardStatement, Insert, Insert._InsertSpec {

        private StandardQueryInsertStatement(StandardQuerySpaceClause<?, ?> clause, SubQuery subQuery) {
            super(clause, subQuery);
        }

        private StandardQueryInsertStatement(StandardQueryParentSpaceClause<?, ?> clause, SubQuery subQuery) {
            super(clause, subQuery);
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


    }// StandardQueryInsertStatement

    private static final class StandardQueryChildInsertStatement extends StandardQueryInsertStatement
            implements _Insert._ChildQueryInsert {

        private final _Insert._QueryInsert parentStmt;

        private StandardQueryChildInsertStatement(StandardQuerySpaceClause<?, ?> clause, SubQuery subQuery) {
            super(clause, subQuery);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public _QueryInsert parentStmt() {
            return this.parentStmt;
        }


    }//StandardQueryChildInsertStatement


}
