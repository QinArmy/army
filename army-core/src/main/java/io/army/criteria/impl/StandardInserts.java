package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;

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
            return new NonParentComplexValuesClause<>(this, table);
        }

        @Override
        public <T> Insert._StandardParentColumnListSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new ParentComplexValuesClause<>(this, table);
        }

    }//StandardDomainOptionClause


    private static final class NonParentValuesLeftParenClause<C, T> extends InsertSupport.StaticColumnValuePairClause<
            C,
            T,
            Insert._StandardValueStaticLeftParenSpec<C, T>>
            implements Insert._StandardValueStaticLeftParenSpec<C, T> {

        private final NonParentComplexValuesClause<C, T> clause;

        private NonParentValuesLeftParenClause(NonParentComplexValuesClause<C, T> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public Insert asInsert() {
            final NonParentComplexValuesClause<C, T> clause = this.clause;
            clause.staticValuesClauseEnd(this.endValuesClause());
            return clause.asInsert();
        }


    }//NonParentValuesLeftParenClause


    private static final class NonParentComplexValuesClause<C, T>
            extends InsertSupport.ComplexInsertValuesClause<
            C,
            T,
            Insert._StandardComplexColumnDefaultSpec<C, T>,
            Insert._StandardValuesColumnDefaultSpec<C, T>,
            Insert._InsertSpec>
            implements Insert._StandardColumnListSpec<C, T>
            , Insert._StandardComplexColumnDefaultSpec<C, T>
            , Insert._InsertSpec {

        private final ParentComplexValuesClause<C, ?> parentClause;


        private NonParentComplexValuesClause(InsertOptions options, SimpleTableMeta<T> table) {
            super(options, table);
            this.parentClause = null;
        }

        private NonParentComplexValuesClause(ParentComplexValuesClause<C, ?> clause, ChildTableMeta<T> table) {
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
            final ParentComplexValuesClause<C, ?> parentClause = this.parentClause;
            final InsertMode mode;
            mode = this.assertInsertMode(parentClause);
            final Insert._InsertSpec spec;
            switch (mode) {
                case DOMAIN: {
                    if (parentClause == null) {
                        spec = new DomainsInsertStatement(this);
                    } else {
                        spec = new ChildDomainInsertStatement(parentClause, this);
                    }
                }
                break;
                case VALUES: {
                    if (parentClause == null) {
                        spec = new ValuesInsertStatement(this);
                    } else {
                        spec = new ChildValuesInsertStatement(parentClause, this);
                    }
                }
                break;
                case QUERY:
                    spec = new StandardQueryInsertStatement(this);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return spec.asInsert();
        }

        private Insert._InsertSpec staticInsertSubQueryEnd(final SubQuery subQuery) {
            this.staticSpaceSubQueryClauseEnd(subQuery);
            return this;
        }


    }//NonParentComplexValuesClause


    private static final class ParentValuesLeftParenClause<C, P> extends InsertSupport.StaticColumnValuePairClause<
            C,
            P,
            Insert._StandardParentValueStaticLeftParenSpec<C, P>>
            implements Insert._StandardParentValueStaticLeftParenSpec<C, P> {

        private final ParentComplexValuesClause<C, P> clause;

        private ParentValuesLeftParenClause(ParentComplexValuesClause<C, P> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public Insert asInsert() {
            final ParentComplexValuesClause<C, P> clause = this.clause;
            clause.staticValuesClauseEnd(this.endValuesClause());
            return clause.asInsert();
        }

        @Override
        public Insert._StandardChildInsertIntoClause<C, P> child() {
            final ParentComplexValuesClause<C, P> clause = this.clause;
            clause.staticValuesClauseEnd(this.endValuesClause());
            return clause.child();
        }


    }//ParentValuesLeftParenClause


    private static final class ParentComplexValuesClause<C, P>
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

        private ParentComplexValuesClause(InsertOptions options, ParentTableMeta<P> table) {
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
            return new NonParentComplexValuesClause<>(this, table);
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

        private DomainsInsertStatement(NonParentComplexValuesClause<?, ?> clause) {
            super(clause);
            this.domainList = clause.domainListForSingle();
        }

        private DomainsInsertStatement(NonParentComplexValuesClause<?, ?> clause, ParentComplexValuesClause<?, ?> parentClause) {
            super(clause);
            this.domainList = clause.domainListForChild(parentClause);
        }

        private DomainsInsertStatement(ParentComplexValuesClause<?, ?> clause, List<?> domainList) {
            super(clause);
            this.domainList = domainList;
        }

        @Override
        public final List<?> domainList() {
            return this.domainList;
        }

    }//DomainsInsertStatement

    static final class ChildDomainInsertStatement extends DomainsInsertStatement
            implements _Insert._ChildDomainInsert {

        private final DomainsInsertStatement parentStmt;

        private ChildDomainInsertStatement(ParentComplexValuesClause<?, ?> parentClause, NonParentComplexValuesClause<?, ?> clause) {
            super(clause, parentClause);
            this.parentStmt = new DomainsInsertStatement(parentClause, ((DomainsInsertStatement) this).domainList);
        }

        @Override
        public _DomainInsert parentStmt() {
            return this.parentStmt;
        }


    }//ChildDomainInsertStatement

    static class ValuesInsertStatement extends StandardValuesSyntaxStatement
            implements _Insert._ValuesInsert {

        private final List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private ValuesInsertStatement(NonParentComplexValuesClause<?, ?> clause) {
            super(clause);
            this.rowPairList = clause.rowPairList();
        }

        private ValuesInsertStatement(ParentComplexValuesClause<?, ?> clause) {
            super(clause);
            this.rowPairList = clause.rowPairList();
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.rowPairList;
        }


    }//ValuesInsertStatement

    static final class ChildValuesInsertStatement extends ValuesInsertStatement
            implements _Insert._ChildValuesInsert {

        private final ValuesInsertStatement parentStmt;

        private ChildValuesInsertStatement(ParentComplexValuesClause<?, ?> parentClause, NonParentComplexValuesClause<?, ?> clause) {
            super(clause);
            this.parentStmt = new ValuesInsertStatement(parentClause);
        }

        @Override
        public _ValuesInsert parentStmt() {
            return this.parentStmt;
        }


    }//ChildValuesInsertStatement

    static class StandardQueryInsertStatement extends InsertSupport.QueryInsertStatement<Insert>
            implements Insert, StandardStatement, Insert._InsertSpec {


        private StandardQueryInsertStatement(NonParentComplexValuesClause<?, ?> clause) {
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


    }//StandardQueryInsertStatement


}
