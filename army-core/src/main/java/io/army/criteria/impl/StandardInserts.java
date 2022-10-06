package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

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


    static <C> StandardInsert._PrimaryOptionSpec<C> primaryInsert(@Nullable C criteria) {
        return new PrimaryInsertIntoClause<>(criteria);
    }


    /*-------------------below standard domain insert syntax class-------------------*/
    private static final class PrimaryInsertIntoClause<C>
            extends NonQueryInsertOptionsImpl<
            StandardInsert._PrimaryNullOptionSpec<C>,
            StandardInsert._PrimaryPreferLiteralSpec<C>,
            StandardInsert._PrimaryInsertIntoClause<C>>
            implements StandardInsert._PrimaryOptionSpec<C> {

        private PrimaryInsertIntoClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            ContextStack.setContextStack(this.context);
        }

        @Override
        public <T> StandardInsert._ColumnListSpec<C, T, Insert> insertInto(SimpleTableMeta<T> table) {
            return new StandardComplexValuesClause<>(this, table, this::simpleInsertEnd);
        }


        @Override
        public <P> StandardInsert._ColumnListSpec<C, P, Insert._ParentInsert<StandardInsert._ChildInsertIntoClause<C, P>>> insertInto(ParentTableMeta<P> table) {
            return new StandardComplexValuesClause<>(this, table, this::parentInsertEnd);
        }

        private <P> Insert._ParentInsert<StandardInsert._ChildInsertIntoClause<C, P>> parentInsertEnd(final StandardComplexValuesClause<?, ?, ?> clause) {
            final Statement._DmlInsertSpec<Insert._ParentInsert<StandardInsert._ChildInsertIntoClause<C, P>>> spec;

            final InsertMode mode;
            mode = clause.getInsertMode();
            switch (mode) {
                case DOMAIN:
                    spec = new PrimaryParentDomainInsertStatement<>(clause);
                    break;
                case VALUES:
                    spec = new PrimaryParentValueInsertStatement<>(clause);
                    break;
                case QUERY:
                    spec = new PrimaryParentQueryInsertStatement<>(clause);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }

            return spec.asInsert();
        }

        private Insert simpleInsertEnd(final StandardComplexValuesClause<?, ?, ?> clause) {
            final Statement._DmlInsertSpec<Insert> spec;
            final InsertMode mode;
            mode = clause.getInsertMode();
            switch (mode) {
                case DOMAIN:
                    spec = new PrimarySimpleDomainInsertStatement(clause);
                    break;
                case VALUES:
                    spec = new PrimarySimpleValueInsertStatement(clause);
                    break;
                case QUERY:
                    spec = new PrimarySimpleQueryInsertStatement(clause);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }

            return spec.asInsert();
        }


    }//PrimaryInsertIntoClause


    private static final class ChildInsertIntoClause<C, P>
            extends SimpleValuesSyntaxOptions
            implements StandardInsert._ChildInsertIntoClause<C, P>
            , ValueSyntaxOptions {

        private final Function<StandardComplexValuesClause<?, ?, ?>, Insert> dmlFunction;

        private ChildInsertIntoClause(@Nullable C criteria, ValueSyntaxOptions options, Function<StandardComplexValuesClause<?, ?, ?>, Insert> dmlFunction) {
            super(options, CriteriaContexts.primaryInsertContext(criteria));
            this.dmlFunction = dmlFunction;
            ContextStack.setContextStack(this.context);
        }


        @Override
        public <T> StandardInsert._ColumnListSpec<C, T, Insert> insertInto(ComplexTableMeta<P, T> table) {
            return new StandardComplexValuesClause<>(this, table, this.dmlFunction);
        }


    }//ChildInsertIntoClause


    private static final class StandardStaticValuesClause<C, T, I extends DmlInsert>
            extends InsertSupport.StaticColumnValuePairClause<
            C,
            T,
            StandardInsert._ValueStaticLeftParenSpec<C, T, I>>
            implements StandardInsert._ValueStaticLeftParenSpec<C, T, I> {

        private final StandardComplexValuesClause<C, T, I> claus;

        private StandardStaticValuesClause(StandardComplexValuesClause<C, T, I> clause) {
            super(clause.context, clause::validateField);
            this.claus = clause;
        }

        @Override
        public I asInsert() {
            this.claus.staticValuesClauseEnd(this.endValuesClause());
            return this.claus.asInsert();
        }


    }//StandardStaticValuesClause


    private static final class StandardComplexValuesClause<C, T, I extends DmlInsert>
            extends InsertSupport.ComplexInsertValuesClause<
            C,
            T,
            StandardInsert._ComplexColumnDefaultSpec<C, T, I>,
            StandardInsert._ValuesColumnDefaultSpec<C, T, I>,
            Statement._DmlInsertSpec<I>>
            implements StandardInsert._ColumnListSpec<C, T, I>
            , StandardInsert._ComplexColumnDefaultSpec<C, T, I>
            , Statement._DmlInsertSpec<I> {

        private final Function<StandardComplexValuesClause<?, ?, ?>, I> dmlFunction;

        private StandardComplexValuesClause(ValueSyntaxOptions options, TableMeta<T> table
                , Function<StandardComplexValuesClause<?, ?, ?>, I> dmlFunction) {
            super(options, table);
            this.dmlFunction = dmlFunction;
        }


        @Override
        public StandardInsert._StandardValueStaticLeftParenClause<C, T, I> values() {
            return new StandardStaticValuesClause<>(this);
        }


        @Override
        public StandardQuery._SelectSpec<C, Statement._DmlInsertSpec<I>> space() {
            return StandardQueries.subQuery(this.criteria, this.context, this::staticInsertQueryEnd);
        }

        @Override
        public I asInsert() {
            return this.dmlFunction.apply(this);
        }

        private Statement._DmlInsertSpec<I> staticInsertQueryEnd(final SubQuery subQuery) {
            this.staticSpaceSubQueryClauseEnd(subQuery);
            return this;
        }


    }//StandardComplexValuesClause


    private static abstract class StandardValuesSyntaxStatement<I extends DmlInsert>
            extends ValueSyntaxInsertStatement<I>
            implements StandardInsert {

        private StandardValuesSyntaxStatement(StandardComplexValuesClause<?, ?, ?> clause) {
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

    static abstract class DomainsInsertStatement<I extends DmlInsert> extends StandardValuesSyntaxStatement<I>
            implements _Insert._DomainInsert {

        private DomainsInsertStatement(StandardComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//DomainsInsertStatement

    private static final class PrimarySimpleDomainInsertStatement extends DomainsInsertStatement<Insert>
            implements Insert {

        private final List<?> domainList;

        private PrimarySimpleDomainInsertStatement(StandardComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta; //standard don't support cte insert and returning clause
            this.domainList = clause.domainListForNonParent();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }


    }//PrimaryDomainInsertStatement

    private static final class PrimaryChildDomainInsertStatement extends DomainsInsertStatement<Insert>
            implements Insert, _Insert._ChildDomainInsert {

        private final List<?> domainList;

        private final PrimaryParentDomainInsertStatement<?, ?> parentStatement;

        private PrimaryChildDomainInsertStatement(PrimaryParentDomainInsertStatement<?, ?> parentStatement
                , StandardComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.domainList = parentStatement.domainList;
            this.parentStatement = parentStatement;
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _DomainInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildDomainInsertStatement


    private static final class PrimaryParentDomainInsertStatement<C, P>
            extends DomainsInsertStatement<Insert._ParentInsert<StandardInsert._ChildInsertIntoClause<C, P>>>
            implements Insert._ParentInsert<StandardInsert._ChildInsertIntoClause<C, P>> {

        private final List<?> originalDomainList;

        private final List<?> domainList;

        private PrimaryParentDomainInsertStatement(StandardComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _CollectionUtils.asUnmodifiableList(this.originalDomainList);
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _ChildInsertIntoClause<C, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this.context.criteria(), this, this::childInsertEnd);
        }

        private Insert childInsertEnd(final StandardComplexValuesClause<?, ?, ?> childClause) {
            assertDomainList(this.originalDomainList, childClause);
            return new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentDomainInsertStatement

    static abstract class ValueInsertStatement<I extends DmlInsert> extends StandardValuesSyntaxStatement<I>
            implements _Insert._ValuesInsert {

        final List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private ValueInsertStatement(StandardComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.rowPairList = clause.rowPairList();
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.rowPairList;
        }


    }//ValueInsertStatement


    private static final class PrimarySimpleValueInsertStatement extends ValueInsertStatement<Insert>
            implements Insert {

        private PrimarySimpleValueInsertStatement(StandardComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }


    }//PrimarySimpleValueInsertStatement

    private static final class PrimaryChildValueInsertStatement extends ValueInsertStatement<Insert>
            implements Insert, _Insert._ChildValuesInsert {

        private final PrimaryParentValueInsertStatement<?, ?> parentStatement;

        private PrimaryChildValueInsertStatement(PrimaryParentValueInsertStatement<?, ?> parentStatement
                , StandardComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _ValuesInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildValueInsertStatement

    private static final class PrimaryParentValueInsertStatement<C, P>
            extends ValueInsertStatement<Insert._ParentInsert<StandardInsert._ChildInsertIntoClause<C, P>>>
            implements Insert._ParentInsert<StandardInsert._ChildInsertIntoClause<C, P>> {

        private PrimaryParentValueInsertStatement(StandardComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public _ChildInsertIntoClause<C, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this.context.criteria(), this, this::childInsertEnd);
        }


        private Insert childInsertEnd(final StandardComplexValuesClause<?, ?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueInsertStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentValueInsertStatement


    static abstract class QueryInsertStatement<I extends DmlInsert>
            extends InsertSupport.QuerySyntaxInsertStatement<I> {

        private QueryInsertStatement(final StandardComplexValuesClause<?, ?, ?> clause) {
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


    }//QueryInsertStatement


    private static final class PrimarySimpleQueryInsertStatement extends QueryInsertStatement<Insert>
            implements Insert {

        private PrimarySimpleQueryInsertStatement(StandardComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }

    }//PrimarySimpleQueryInsertStatement


    private static final class PrimaryChildQueryInsertStatement extends QueryInsertStatement<Insert>
            implements Insert, _Insert._ChildQueryInsert {

        private final PrimaryParentQueryInsertStatement<?, ?> parentStatement;

        private PrimaryChildQueryInsertStatement(PrimaryParentQueryInsertStatement<?, ?> parentStatement
                , StandardComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _QueryInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildQueryInsertStatement

    private static final class PrimaryParentQueryInsertStatement<C, P>
            extends QueryInsertStatement<Insert._ParentInsert<StandardInsert._ChildInsertIntoClause<C, P>>>
            implements Insert._ParentInsert<StandardInsert._ChildInsertIntoClause<C, P>> {

        private PrimaryParentQueryInsertStatement(StandardComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public StandardInsert._ChildInsertIntoClause<C, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this.context.criteria(), this, this::childInsertEnd);
        }

        private Insert childInsertEnd(StandardComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentQueryInsertStatement


}
