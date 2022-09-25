package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.mysql.MySQLDialect;
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

    static <C> Insert._StandardDomainOptionSpec<C> primaryInsert(@Nullable C criteria) {
        return new StandardDomainOptionClause<>(criteria);
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
            CriteriaContextStack.setContextStack(this.context);
        }


        @Override
        public <T> Insert._StandardColumnListSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new DomainColumnsClause<>(this, table);
        }

        @Override
        public <T> Insert._StandardParentColumnListSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new DomainParentColumnsClause<>(this, table);
        }

    }//StandardDomainOptionClause


    private static final class NonParentValuesLeftParenClause<C, T> extends InsertSupport.StaticColumnValuePairClause<
            C,
            T,
            Insert._StandardValueStaticLeftParenSpec<C, T>>
            implements Insert._StandardValueStaticLeftParenSpec<C, T> {

        private final DomainColumnsClause<C, T> clause;

        private NonParentValuesLeftParenClause(DomainColumnsClause<C, T> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public Insert asInsert() {
            final DomainColumnsClause<C, T> clause = this.clause;
            clause.staticValuesClauseEnd(this.endValuesClause());
            return clause.asInsert();
        }


    }//NonParentValuesLeftParenClause


    private static final class DomainColumnsClause<C, T>
            extends InsertSupport.ComplexInsertValuesClause<
            C,
            T,
            Insert._StandardComplexColumnDefaultSpec<C, T>,
            Insert._StandardValuesColumnDefaultSpec<C, T>,
            Insert._InsertSpec>
            implements Insert._StandardColumnListSpec<C, T>
            , Insert._StandardComplexColumnDefaultSpec<C, T>
            , Insert._InsertSpec {

        private final DomainParentColumnsClause<C, ?> parentClause;


        private DomainColumnsClause(InsertOptions options, SimpleTableMeta<T> table) {
            super(options, table);
            this.parentClause = null;
        }

        private DomainColumnsClause(DomainParentColumnsClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, table);
            this.parentClause = clause;
        }

        @Override
        public Insert._StandardValueStaticLeftParenClause<C, T> values() {
            return new NonParentValuesLeftParenClause<>(this);
        }

        @Override
        public StandardQuery._StandardSelectClause<C, Insert._StandardInsertQuery> space() {
            return StandardSimpleQuery.insertQuery(this.criteria, this::staticInsertSubQueryEnd);
        }

        @Override
        public Insert asInsert() {
            return null;
        }

        private Insert._InsertSpec staticInsertSubQueryEnd(final SubQuery subQuery) {
            this.staticSpaceSubQueryClauseEnd(subQuery);
            return this;
        }


    }//StandardDomainColumnsClause


    private static final class ParentValuesLeftParenClause<C, P> extends InsertSupport.StaticColumnValuePairClause<
            C,
            P,
            Insert._StandardParentValueStaticLeftParenSpec<C, P>>
            implements Insert._StandardParentValueStaticLeftParenSpec<C, P> {

        private final DomainParentColumnsClause<C, P> clause;

        private ParentValuesLeftParenClause(DomainParentColumnsClause<C, P> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public Insert asInsert() {
            final DomainParentColumnsClause<C, P> clause = this.clause;
            clause.staticValuesClauseEnd(this.endValuesClause());
            return clause.asInsert();
        }

        @Override
        public Insert._StandardChildInsertIntoClause<C, P> child() {
            final DomainParentColumnsClause<C, P> clause = this.clause;
            clause.staticValuesClauseEnd(this.endValuesClause());
            return clause.child();
        }


    }//ParentValuesLeftParenClause


    private static final class DomainParentColumnsClause<C, P>
            extends InsertSupport.ComplexInsertValuesClause<
            C,
            P,
            Insert._StandardParentComplexColumnDefaultSpec<C, P>,
            Insert._StandardParentValuesColumnDefaultSpec<C, P>,
            Insert._StandardChildSpec<C, P>>
            implements Insert._StandardParentColumnListSpec<C, P>
            , Insert._StandardParentComplexColumnDefaultSpec<C, P>
            , Insert._StandardChildSpec<C, P>
            , Insert._StandardChildInsertIntoClause<C, P>
            , InsertOptions {

        private DomainParentColumnsClause(InsertOptions options, ParentTableMeta<P> table) {
            super(options, table);
        }

        @Override
        public Insert._StandardParentValueStaticLeftParenClause<C, P> values() {
            return new ParentValuesLeftParenClause<>(this);
        }


        @Override
        public StandardQuery._StandardSelectClause<C, Insert._StandardParentInsertQuery<C, P>> space() {
            return StandardSimpleQuery.parentInsertQuery(this.criteria, this::staticInsertSubQueryEnd);
        }

        @Override
        public Insert._StandardChildInsertIntoClause<C, P> child() {
            return this;
        }

        @Override
        public <T> Insert._StandardColumnListSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            this.getInsertMode();// assert
            return new DomainColumnsClause<>(this, table);
        }

        @Override
        public Insert asInsert() {
            return null;
        }

        private Insert._StandardChildSpec<C, P> staticInsertSubQueryEnd(final SubQuery subQuery) {
            this.staticSpaceSubQueryClauseEnd(subQuery);
            return this;
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
                s = this.mockAsString(MySQLDialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

    }//StandardValuesSyntaxStatement

    static class DomainsInsertStatement extends StandardValuesSyntaxStatement
            implements _Insert._DomainInsert {

        private final List<?> domainList;

        private DomainsInsertStatement(DomainColumnsClause<?, ?> clause) {
            super(clause);
            this.domainList = clause.domainList();
        }

        private DomainsInsertStatement(DomainParentColumnsClause<?, ?> clause, Supplier<List<?>> supplier) {
            super(clause);
            this.domainList = supplier.get();
        }

        @Override
        public final List<?> domainList() {
            return this.domainList;
        }

    }//StandardDomainInsertStatement

    private static final class StandardDomainChildInsertStatement extends DomainsInsertStatement
            implements _Insert._ChildDomainInsert {

        private final _DomainInsert parentStmt;

        private StandardDomainChildInsertStatement(DomainColumnsClause<?, ?> clause) {
            super(clause);
            assert clause.parentClause != null;
            this.parentStmt = clause.parentClause.createParentStmt(clause::domainList);
        }

        @Override
        public _DomainInsert parentStmt() {
            return this.parentStmt;
        }

    }//StandardDomainChildInsertStatement


    /*-------------------below standard value insert syntax class-------------------*/


    private static final class StandardStaticValuesPairClause<C, T>
            extends StaticColumnValuePairClause<C, T, Insert._StandardValueStaticLeftParenSpec<C, T>>
            implements Insert._StandardValueStaticLeftParenSpec<C, T> {

        private final StandardValueColumnsClause<?, ?> clause;

        private StandardStaticValuesPairClause(StandardValueColumnsClause<C, T> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public Insert asInsert() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .asInsert();
        }


    }//StandardStaticValuesPairClause


    private static final class StandardParentStaticValuesPairClause<C, P>
            extends StaticColumnValuePairClause<C, P, Insert._StandardParentStaticValuesSpec<C, P>>
            implements Insert._StandardParentStaticValuesSpec<C, P> {

        private final StandardValueParentColumnsClause<C, P> clause;

        private StandardParentStaticValuesPairClause(StandardValueParentColumnsClause<C, P> clause) {
            super(clause.context, clause::validateField);
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


    }//StandardParentStaticValuesPairClause


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
        public final List<Map<FieldMeta<?>, _Expression>> rowList() {
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
        public <T> Insert._StandardSingleColumnsClause<C, T> insertInto(SimpleTableMeta<T> table) {
            return new QueryColumnsClause<>(this.criteriaContext, table);
        }

        @Override
        public <T> Insert._StandardParentColumnsClause<C, T> insertInto(ParentTableMeta<T> table) {
            return new QueryParentColumnsClause<>(this.criteriaContext, table);
        }


    }//StandardSubQueryInsertIntoClause


    private static final class QueryColumnsClause<C, T>
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
            super(clause.context, table);
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


    private static final class QueryParentColumnsClause<C, P>
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
        public <T> Insert._StandardSingleColumnsClause<C, T> insertInto(ComplexTableMeta<P, T> table) {
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
                s = this.mockAsString(MySQLDialect.MySQL57, Visible.ONLY_VISIBLE, true);
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
