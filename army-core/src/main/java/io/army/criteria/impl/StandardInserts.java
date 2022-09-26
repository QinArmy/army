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
        public <P> Insert._StandardParentColumnListSpec<C, P, Insert._StandardChildInsertIntoClause<C, P>> insertInto(ParentTableMeta<P> table) {
            return new SimpleParentComplexValuesClause<>(this, table);
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

        private final ParentComplexValuesClause<C, ?, ?> parentClause;


        private NonParentComplexValuesClause(InsertOptions options, SimpleTableMeta<T> table) {
            super(options, table);
            this.parentClause = null;
        }

        private NonParentComplexValuesClause(ParentComplexValuesClause<C, ?, ?> clause, ChildTableMeta<T> table) {
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
            final ParentComplexValuesClause<C, ?, ?> parentClause = this.parentClause;
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
                case QUERY: {
                    if (parentClause == null) {
                        spec = new QueryInsertStatement(this);
                    } else {
                        spec = new ChildQueryInsertStatement(parentClause, this);
                    }
                }
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


    private static final class ParentValuesLeftParenClause<C, P, CT> extends InsertSupport.StaticColumnValuePairClause<
            C,
            P,
            Insert._StandardParentValueStaticLeftParenSpec<C, P, CT>>
            implements Insert._StandardParentValueStaticLeftParenSpec<C, P, CT> {

        private final ParentComplexValuesClause<C, P, CT> clause;

        private ParentValuesLeftParenClause(ParentComplexValuesClause<C, P, CT> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public Insert asInsert() {
            final ParentComplexValuesClause<C, P, CT> clause = this.clause;
            clause.staticValuesClauseEnd(this.endValuesClause());
            return clause.asInsert();
        }

        @Override
        public CT child() {
            final ParentComplexValuesClause<C, P, CT> clause = this.clause;
            clause.staticValuesClauseEnd(this.endValuesClause());
            return clause.child();
        }


    }//ParentValuesLeftParenClause


    private static abstract class ParentComplexValuesClause<C, P, CT>
            extends InsertSupport.ComplexInsertValuesClause<
            C,
            P,
            Insert._StandardParentComplexColumnDefaultSpec<C, P, CT>,
            Insert._StandardParentValuesColumnDefaultSpec<C, P, CT>,
            Insert._StandardChildSpec<CT>>
            implements Insert._StandardParentColumnListSpec<C, P, CT>
            , Insert._StandardParentComplexColumnDefaultSpec<C, P, CT>
            , Insert._StandardChildSpec<CT>
            , InsertOptions {

        private ParentComplexValuesClause(InsertOptions options, ParentTableMeta<P> table) {
            super(options, table);
        }

        @Override
        public final Insert._StandardParentValueStaticLeftParenClause<C, P, CT> values() {
            return new ParentValuesLeftParenClause<>(this);
        }


        @Override
        public final StandardQuery._StandardSelectClause<C, Insert._StandardParentInsertQuery<CT>> space() {
            return StandardSimpleQuery.parentInsertQuery(this.criteria, this::staticInsertSubQueryEnd);
        }


        @Override
        public final Insert asInsert() {
            final InsertMode mode;
            mode = this.getInsertMode();
            final Insert._InsertSpec spec;
            switch (mode) {
                case DOMAIN:
                    spec = new DomainsInsertStatement(this, this.domainListForSingle());
                    break;
                case VALUES:
                    spec = new ValuesInsertStatement(this);
                    break;
                case QUERY:
                    spec = new QueryInsertStatement(this);
                    break;
                default:
                    //no bug,never here
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return spec.asInsert();
        }

        private Insert._StandardChildSpec<CT> staticInsertSubQueryEnd(final SubQuery subQuery) {
            this.staticSpaceSubQueryClauseEnd(subQuery);
            return this;
        }


    }//ParentComplexValuesClause

    private static final class SimpleParentComplexValuesClause<C, P> extends ParentComplexValuesClause<
            C,
            P,
            Insert._StandardChildInsertIntoClause<C, P>>
            implements Insert._StandardChildInsertIntoClause<C, P> {

        private SimpleParentComplexValuesClause(InsertOptions options, ParentTableMeta<P> table) {
            super(options, table);
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


    }//SimpleParentComplexValuesClause


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

        private DomainsInsertStatement(ParentComplexValuesClause<?, ?, ?> parentClause, NonParentComplexValuesClause<?, ?> clause) {
            super(clause);
            this.domainList = clause.domainListForChild(parentClause);
        }

        private DomainsInsertStatement(ParentComplexValuesClause<?, ?, ?> clause, List<?> domainList) {
            super(clause);
            this.domainList = domainList;
        }

        @Override
        public final List<?> domainList() {
            return this.domainList;
        }

    }//DomainsInsertStatement

    private static final class ChildDomainInsertStatement extends DomainsInsertStatement
            implements _Insert._ChildDomainInsert {

        private final DomainsInsertStatement parentStmt;

        private ChildDomainInsertStatement(ParentComplexValuesClause<?, ?, ?> parentClause, NonParentComplexValuesClause<?, ?> clause) {
            super(parentClause, clause);
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

        private ValuesInsertStatement(ParentComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.rowPairList = clause.rowPairList();
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.rowPairList;
        }


    }//ValuesInsertStatement

    private static final class ChildValuesInsertStatement extends ValuesInsertStatement
            implements _Insert._ChildValuesInsert {

        private final ValuesInsertStatement parentStmt;

        private ChildValuesInsertStatement(ParentComplexValuesClause<?, ?, ?> parentClause, NonParentComplexValuesClause<?, ?> clause) {
            super(clause);
            this.parentStmt = new ValuesInsertStatement(parentClause);
        }

        @Override
        public _ValuesInsert parentStmt() {
            return this.parentStmt;
        }


    }//ChildValuesInsertStatement

    static class QueryInsertStatement extends InsertSupport.QuerySyntaxInsertStatement<Insert>
            implements Insert, StandardStatement, Insert._InsertSpec {


        private QueryInsertStatement(NonParentComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private QueryInsertStatement(ParentComplexValuesClause<?, ?, ?> clause) {
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

    private static final class ChildQueryInsertStatement extends QueryInsertStatement
            implements _Insert._ChildQueryInsert {

        private final QueryInsertStatement parentStmt;

        private ChildQueryInsertStatement(ParentComplexValuesClause<?, ?, ?> parentClause, NonParentComplexValuesClause<?, ?> clause) {
            super(clause);
            this.parentStmt = new QueryInsertStatement(parentClause);
        }

        @Override
        public _QueryInsert parentStmt() {
            return this.parentStmt;
        }

    }//StandardChildQueryInsertStatement


}
