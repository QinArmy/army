package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.StandardStatement;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;

import java.util.List;
import java.util.Map;
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
            return new DomainColumnsClause<>(this, table);
        }

        @Override
        public <T extends IDomain> Insert._StandardParentDomainColumnsSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new DomainParentColumnsClause<>(this, table);
        }

    }//StandardDomainOptionClause


    private static final class DomainColumnsClause<C, T extends IDomain>
            extends DomainValueClause<C, T, Insert._StandardDomainDefaultSpec<C, T>, Insert._InsertSpec>
            implements Insert._StandardDomainColumnsSpec<C, T> {

        private final DomainParentColumnsClause<C, ?> parentStmt;


        private DomainColumnsClause(InsertOptions options, SimpleTableMeta<T> table) {
            super(options, table);
            this.parentStmt = null;
        }

        private DomainColumnsClause(DomainParentColumnsClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, table);
            this.parentStmt = clause;
        }

        @Override
        Insert._StandardDomainDefaultSpec<C, T> columnListEnd() {
            return this;
        }

        @Override
        Insert._InsertSpec valuesEnd() {
            final Insert._InsertSpec spec;
            if (this.parentStmt == null) {
                spec = new DomainsInsertStatement(this);
            } else {
                spec = new StandardDomainChildInsertStatement(this);
            }
            return spec;
        }

    }//StandardDomainColumnsClause


    private static final class DomainParentColumnsClause<C, P extends IDomain>
            extends InsertSupport.DomainValueClause<
            C,
            P,
            Insert._StandardParentDomainDefaultSpec<C, P>,
            Insert._InsertSpec>
            implements Insert._StandardParentDomainColumnsSpec<C, P>
            , Insert._StandardChildInsertIntoClause<C, P>
            , InsertOptions {

        private DomainParentColumnsClause(InsertOptions options, ParentTableMeta<P> table) {
            super(options, table);
        }

        @Override
        public Insert._StandardChildInsertIntoClause<C, P> child() {
            return this;
        }

        @Override
        public <T extends IDomain> Insert._StandardDomainColumnsSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            return new DomainColumnsClause<>(this, table);
        }

        @Override
        Insert._StandardParentDomainDefaultSpec<C, P> columnListEnd() {
            return this;
        }

        @Override
        Insert._InsertSpec valuesEnd() {
            return this.createParentStmt(this::domainList);
        }

        private DomainsInsertStatement createParentStmt(Supplier<List<IDomain>> supplier) {
            return new DomainsInsertStatement(this, supplier);
        }

    }//StandardDomainParentColumnsClause


    private static abstract class StandardValuesSyntaxStatement extends InsertSupport.ValueSyntaxStatement<Insert>
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

    static class DomainsInsertStatement extends StandardValuesSyntaxStatement
            implements _Insert._DomainInsert {

        private final List<IDomain> domainList;

        private DomainsInsertStatement(DomainColumnsClause<?, ?> clause) {
            super(clause);
            this.domainList = clause.domainList();
        }

        private DomainsInsertStatement(DomainParentColumnsClause<?, ?> clause, Supplier<List<IDomain>> supplier) {
            super(clause);
            this.domainList = supplier.get();
        }

        @Override
        public final List<IDomain> domainList() {
            return this.domainList;
        }

    }//StandardDomainInsertStatement

    private static final class StandardDomainChildInsertStatement extends DomainsInsertStatement
            implements _Insert._ChildDomainInsert {

        private final _DomainInsert parentStmt;

        private StandardDomainChildInsertStatement(DomainColumnsClause<?, ?> clause) {
            super(clause);
            assert clause.parentStmt != null;
            this.parentStmt = clause.parentStmt.createParentStmt(clause::domainList);
        }

        @Override
        public _DomainInsert parentStmt() {
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
            return new StandardValueColumnsClause<>(this, table);
        }

        @Override
        public <T extends IDomain> Insert._StandardParentValueColumnsSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new StandardValueParentColumnsClause<>(this, table);
        }


    }//StandardValueInsertOptionClause


    private static final class StandardStaticValuesPairClause<C, T extends IDomain>
            extends StaticColumnValuePairClause<C, T, Insert._StandardValueStaticLeftParenSpec<C, T>>
            implements Insert._StandardValueStaticLeftParenSpec<C, T> {

        private final StandardValueColumnsClause<?, ?> clause;

        private StandardStaticValuesPairClause(StandardValueColumnsClause<C, T> clause) {
            super(clause.criteriaContext, clause::validateField);
            this.clause = clause;
        }


        @Override
        public Insert asInsert() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .asInsert();
        }

        @Override
        public Insert._StandardValueStaticLeftParenSpec<C, T> rightParen() {
            this.endCurrentRow();
            return this;
        }

    }//StandardStaticValuesPairClause


    private static final class StandardParentStaticValuesPairClause<C, P extends IDomain>
            extends StaticColumnValuePairClause<C, P, Insert._StandardParentStaticValuesSpec<C, P>>
            implements Insert._StandardParentStaticValuesSpec<C, P> {

        private final StandardValueParentColumnsClause<C, P> clause;

        private StandardParentStaticValuesPairClause(StandardValueParentColumnsClause<C, P> clause) {
            super(clause.criteriaContext, clause::validateField);
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
            this.endCurrentRow();
            return this;
        }

    }//StandardParentStaticValuesPairClause

    private static final class StandardValueColumnsClause<C, T extends IDomain>
            extends DynamicValueInsertValueClause<
            C,
            T,
            Insert._StandardValueDefaultSpec<C, T>,
            Insert._InsertSpec>
            implements Insert._StandardValueColumnsSpec<C, T> {

        private final _ValuesInsert parentStmt;

        private StandardValueColumnsClause(InsertOptions options, SimpleTableMeta<T> table) {
            super(options, table);
            this.parentStmt = null;
        }

        private StandardValueColumnsClause(StandardValueParentColumnsClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, table);
            this.parentStmt = clause.createParentStmt();//couldn't invoke asInsert method
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
        Insert._InsertSpec valueClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            this.endColumnDefaultClause();
            final Insert._InsertSpec spec;
            if (this.parentStmt == null) {
                spec = new ValuesInsertStatement(this, rowValuesList);
            } else if (rowValuesList.size() == this.parentStmt.rowValuesList().size()) {
                spec = new StandardValueChildInsertStatement(this, rowValuesList);
            } else {
                throw childAndParentRowsNotMatch(this.criteriaContext, (ChildTableMeta<?>) this.table
                        , this.parentStmt.rowValuesList().size(), rowValuesList.size());
            }
            return spec;
        }


    }//StandardValueValuesClause

    private static final class StandardValueParentColumnsClause<C, P extends IDomain>
            extends InsertSupport.DynamicValueInsertValueClause<
            C,
            P,
            Insert._StandardValueParentDefaultSpec<C, P>,
            Insert._StandardValueChildSpec<C, P>>
            implements Insert._StandardParentValueColumnsSpec<C, P>
            , Insert._StandardValueChildSpec<C, P>
            , Insert._StandardValueChildInsertIntoClause<C, P>
            , InsertOptions {

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private StandardValueParentColumnsClause(InsertOptions options, ParentTableMeta<P> table) {
            super(options, table);
        }

        @Override
        public Insert._StandardParentStaticValuesClause<C, P> values() {
            return new StandardParentStaticValuesPairClause<>(this);
        }

        @Override
        public Insert asInsert() {
            return this.createParentStmt()
                    .asInsert();
        }

        @Override
        public Insert._StandardValueChildInsertIntoClause<C, P> child() {
            return this;
        }

        @Override
        public <T extends IDomain> Insert._StandardValueColumnsSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            return new StandardValueColumnsClause<>(this, table);
        }

        @Override
        Insert._StandardValueParentDefaultSpec<C, P> columnListEnd() {
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

        private ValuesInsertStatement createParentStmt() {
            final List<Map<FieldMeta<?>, _Expression>> rowValuesList = this.rowValuesList;
            if (rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new ValuesInsertStatement(this, rowValuesList);
        }


    }//StandardValueParentColumnsClause


    static class ValuesInsertStatement extends StandardValuesSyntaxStatement
            implements _Insert._ValuesInsert {

        private final List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private ValuesInsertStatement(StandardValueColumnsClause<?, ?> clause
                , List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            super(clause);
            this.rowValuesList = rowValuesList;
        }

        private ValuesInsertStatement(StandardValueParentColumnsClause<?, ?> clause
                , List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            super(clause);
            this.rowValuesList = rowValuesList;
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowValuesList() {
            return this.rowValuesList;
        }


    }//StandardValueInsertStatement


    private static final class StandardValueChildInsertStatement extends ValuesInsertStatement
            implements _Insert._ChildValuesInsert {

        private final _ValuesInsert parentStmt;

        private StandardValueChildInsertStatement(StandardValueColumnsClause<?, ?> clause
                , List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            super(clause, rowValuesList);
            this.parentStmt = clause.parentStmt;
            assert parentStmt != null;
        }

        @Override
        public _ValuesInsert parentStmt() {
            return this.parentStmt;
        }


    }//StandardValueChildInsertStatement


    /*-----------------------below query insert classes -----------------------*/


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
            return new QueryColumnsClause<>(this.criteriaContext, table);
        }

        @Override
        public <T extends IDomain> Insert._StandardParentColumnsClause<C, T> insertInto(ParentTableMeta<T> table) {
            return new QueryParentColumnsClause<>(this.criteriaContext, table);
        }


    }//StandardSubQueryInsertIntoClause


    private static final class QueryColumnsClause<C, T extends IDomain>
            extends InsertSupport.QueryInsertSpaceClause<
            C,
            T,
            Insert._SpaceSubQueryClause<C, Insert._InsertSpec>,
            Insert._InsertSpec>
            implements Insert._StandardSingleColumnsClause<C, T>
            , Insert._SpaceSubQueryClause<C, Insert._InsertSpec> {

        private final _Insert._QueryInsert parentStmt;

        private QueryColumnsClause(CriteriaContext criteriaContext, SimpleTableMeta<T> table) {
            super(criteriaContext, table);
            this.parentStmt = null;
        }

        private QueryColumnsClause(QueryParentColumnsClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause.criteriaContext, table);
            this.parentStmt = clause.createParentStmt();//couldn't invoke asInsert
        }

        @Override
        Insert._SpaceSubQueryClause<C, Insert._InsertSpec> columnListEnd() {
            return this;
        }

        @Override
        Insert._InsertSpec spaceEnd() {
            final Insert._InsertSpec spec;
            if (this.parentStmt == null) {
                spec = new StandardQueryInsertStatement(this);
            } else {
                spec = new StandardQueryChildInsertStatement(this);
            }
            return spec;
        }


    }//StandardQueryColumnsClause


    private static final class QueryParentColumnsClause<C, P extends IDomain>
            extends InsertSupport.QueryInsertSpaceClause<
            C,
            P,
            Insert._StandardParentSubQueryClause<C, P>,
            Insert._StandardQueryChildPartSpec<C, P>>
            implements Insert._StandardParentColumnsClause<C, P>
            , Insert._StandardParentSubQueryClause<C, P>
            , Insert._StandardQueryChildPartSpec<C, P>
            , Insert._StandardQueryChildInsertIntoClause<C, P> {

        private QueryParentColumnsClause(CriteriaContext criteriaContext, ParentTableMeta<P> table) {
            super(criteriaContext, table);
        }

        @Override
        public Insert asInsert() {
            return this.createParentStmt()
                    .asInsert();
        }

        @Override
        public Insert._StandardQueryChildInsertIntoClause<C, P> child() {
            return this;
        }

        @Override
        public <T extends IDomain> Insert._StandardSingleColumnsClause<C, T> insertInto(ComplexTableMeta<P, T> table) {
            return new QueryColumnsClause<>(this, table);
        }

        @Override
        Insert._StandardParentSubQueryClause<C, P> columnListEnd() {
            return this;
        }

        @Override
        Insert._StandardQueryChildPartSpec<C, P> spaceEnd() {
            return this;
        }

        private StandardQueryInsertStatement createParentStmt() {
            return new StandardQueryInsertStatement(this);
        }


    }//QueryParentColumnsClause


    static class StandardQueryInsertStatement extends QueryInsertStatement<Insert>
            implements StandardStatement, Insert, Insert._InsertSpec {

        private StandardQueryInsertStatement(QueryColumnsClause<?, ?> clause) {
            super(clause);
        }

        private StandardQueryInsertStatement(QueryParentColumnsClause<?, ?> clause) {
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


    }// StandardQueryInsertStatement

    private static final class StandardQueryChildInsertStatement extends StandardQueryInsertStatement
            implements _Insert._ChildQueryInsert {

        private final _Insert._QueryInsert parentStmt;

        private StandardQueryChildInsertStatement(QueryColumnsClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public _QueryInsert parentStmt() {
            return this.parentStmt;
        }


    }//StandardQueryChildInsertStatement


}
