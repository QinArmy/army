package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.InsertStatement;
import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.standard.StandardInsert;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.*;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

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
abstract class StandardInserts extends InsertSupports {


    private StandardInserts() {
        throw new UnsupportedOperationException();
    }


    static StandardInsert._PrimaryOptionSpec singleInsert() {
        return new PrimaryInsertIntoClause();
    }


    /*-------------------below private method -------------------*/

    private static <P> InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<Insert, P>> parentInsertEnd(
            final StandardComplexValuesClause<?, ?> clause) {
        final Statement._DmlInsertClause<InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<Insert, P>>> spec;

        final InsertMode mode;
        mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new PrimaryParentDomainInsertStatement<>(clause, SQLs::_identity);
                break;
            case VALUES:
                spec = new PrimaryParentValueInsertStatement<>(clause, SQLs::_identity);
                break;
            case QUERY:
                spec = new PrimaryParentQueryInsertStatement<>(clause, SQLs::_identity);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }

        return spec.asInsert();
    }

    private static Insert singleInsertEnd(final StandardComplexValuesClause<?, ?> clause) {
        final Statement._DmlInsertClause<Insert> spec;
        final InsertMode mode;
        mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new PrimarySingleDomainInsertStatement(clause);
                break;
            case VALUES:
                spec = new PrimarySingleValueInsertStatement(clause);
                break;
            case QUERY:
                spec = new PrimarySingleQueryInsertStatement(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }

        return spec.asInsert();
    }


    /*-------------------below standard domain insert syntax class-------------------*/
    private static final class PrimaryInsertIntoClause
            extends NonQueryInsertOptionsImpl<
            StandardInsert._PrimaryNullOptionSpec,
            StandardInsert._PrimaryPreferLiteralSpec,
            StandardInsert._PrimaryInsertIntoClause>
            implements StandardInsert._PrimaryOptionSpec {

        private PrimaryInsertIntoClause() {
            super(CriteriaContexts.primaryInsertContext(null));
            ContextStack.push(this.context);
        }

        @Override
        public <T> StandardInsert._ColumnListSpec<T, Insert> insertInto(SimpleTableMeta<T> table) {
            return new StandardComplexValuesClause<>(this, table, StandardInserts::singleInsertEnd);
        }


        @Override
        public <P> StandardInsert._ColumnListSpec<P, InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<Insert, P>>> insertInto(ParentTableMeta<P> table) {
            return new StandardComplexValuesClause<>(this, table, StandardInserts::parentInsertEnd);
        }


    }//PrimaryInsertIntoClause


    private static final class ChildInsertIntoClause<I extends Item, P>
            extends SimpleValuesSyntaxOptions
            implements StandardInsert._ChildInsertIntoClause<I, P>,
            ValueSyntaxOptions {

        private final Function<StandardComplexValuesClause<?, ?>, I> dmlFunction;

        private ChildInsertIntoClause(ValueSyntaxOptions options,
                                      Function<StandardComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, CriteriaContexts.primaryInsertContext(null));
            ContextStack.push(this.context);
            this.dmlFunction = dmlFunction;
        }


        @Override
        public <T> StandardInsert._ColumnListSpec<T, I> insertInto(ComplexTableMeta<P, T> table) {
            return new StandardComplexValuesClause<>(this, table, this.dmlFunction);
        }


    }//ChildInsertIntoClause


    private static final class StandardStaticValuesClause<T, I extends Item>
            extends InsertSupports.StaticColumnValuePairClause<
            T,
            StandardInsert._ValueStaticLeftParenSpec<T, I>>
            implements StandardInsert._ValueStaticLeftParenSpec<T, I> {

        private final StandardComplexValuesClause<T, I> claus;

        private StandardStaticValuesClause(StandardComplexValuesClause<T, I> clause) {
            super(clause.context, clause::validateField);
            this.claus = clause;
        }

        @Override
        public I asInsert() {
            return this.claus.staticValuesClauseEnd(this.endValuesClause())
                    .asInsert();
        }


    }//StandardStaticValuesClause


    private static final class StandardComplexValuesClause<T, I extends Item>
            extends InsertSupports.ComplexInsertValuesClause<
            T,
            StandardInsert._ComplexColumnDefaultSpec<T, I>,
            StandardInsert._ValuesColumnDefaultSpec<T, I>,
            Statement._DmlInsertClause<I>>
            implements StandardInsert._ColumnListSpec<T, I>,
            StandardInsert._ComplexColumnDefaultSpec<T, I>,
            Statement._DmlInsertClause<I> {

        private final Function<StandardComplexValuesClause<?, ?>, I> dmlFunction;

        private StandardComplexValuesClause(PrimaryInsertIntoClause options, SingleTableMeta<T> table,
                                            Function<StandardComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.dmlFunction = dmlFunction;
        }

        private StandardComplexValuesClause(ChildInsertIntoClause<?, ?> options, ChildTableMeta<T> table,
                                            Function<StandardComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.dmlFunction = dmlFunction;
        }


        @Override
        public StandardInsert._StandardValueStaticLeftParenClause<T, I> values() {
            return new StandardStaticValuesClause<>(this);
        }


        @Override
        public StandardQuery._SelectSpec<Statement._DmlInsertClause<I>> space() {
            return StandardQueries.subQuery(this.context, this::spaceQueryEnd);
        }

        @Override
        public Statement._DmlInsertClause<I> space(Supplier<SubQuery> supplier) {
            return this.spaceQueryEnd(supplier.get());
        }

        @Override
        public Statement._DmlInsertClause<I> space(Function<StandardQuery._SelectSpec<Statement._DmlInsertClause<I>>, Statement._DmlInsertClause<I>> function) {
            return function.apply(StandardQueries.subQuery(this.context, this::spaceQueryEnd));
        }

        @Override
        public I asInsert() {
            return this.dmlFunction.apply(this);
        }


    }//StandardComplexValuesClause


    private static abstract class StandardValuesSyntaxStatement<I extends Statement>
            extends ValueSyntaxInsertStatement<I>
            implements StandardInsert, Insert {

        private StandardValuesSyntaxStatement(StandardComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }


    }//StandardValuesSyntaxStatement

    static abstract class DomainsInsertStatement<I extends Statement>
            extends StandardValuesSyntaxStatement<I>
            implements _Insert._DomainInsert {

        private DomainsInsertStatement(StandardComplexValuesClause<?, ?> clause) {
            super(clause);
        }


    }//DomainsInsertStatement

    private static final class PrimarySingleDomainInsertStatement extends DomainsInsertStatement<Insert> {

        private final List<?> domainList;

        private PrimarySingleDomainInsertStatement(StandardComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SingleTableMeta; //standard don't support cte insert and returning clause
            this.domainList = clause.domainListForSingle();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }


    }//PrimarySingleDomainInsertStatement

    private static final class PrimaryChildDomainInsertStatement extends DomainsInsertStatement<Insert>
            implements _Insert._ChildDomainInsert {

        private final List<?> domainList;

        private final PrimaryParentDomainInsertStatement<?, ?> parentStatement;

        private PrimaryChildDomainInsertStatement(PrimaryParentDomainInsertStatement<?, ?> parentStatement
                , StandardComplexValuesClause<?, ?> childClause) {
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


    private static final class PrimaryParentDomainInsertStatement<I extends Item, P>
            extends DomainsInsertStatement<InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<I, P>>>
            implements InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<I, P>> {

        private final Function<Insert, I> function;

        private final List<?> originalDomainList;

        private final List<?> domainList;

        /**
         * @see PrimaryInsertIntoClause#parentInsertEnd(StandardComplexValuesClause)
         */
        private PrimaryParentDomainInsertStatement(StandardComplexValuesClause<?, ?> clause,
                                                   Function<Insert, I> function) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.function = function;
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _CollectionUtils.asUnmodifiableList(this.originalDomainList);
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _ChildInsertIntoClause<I, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private I childInsertEnd(final StandardComplexValuesClause<?, ?> childClause) {
            childClause.domainListForChild(this.originalDomainList);
            final Insert insert;
            insert = new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
            return this.function.apply(insert);
        }


    }//PrimaryParentDomainInsertStatement

    static abstract class ValueInsertStatement<I extends Statement> extends StandardValuesSyntaxStatement<I>
            implements _Insert._ValuesInsert {

        final List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private ValueInsertStatement(StandardComplexValuesClause<?, ?> clause) {
            super(clause);
            this.rowPairList = clause.rowPairList();
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.rowPairList;
        }


    }//ValueInsertStatement


    private static final class PrimarySingleValueInsertStatement extends ValueInsertStatement<Insert> {

        private PrimarySingleValueInsertStatement(StandardComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }


    }//PrimarySimpleValueInsertStatement

    private static final class PrimaryChildValueInsertStatement extends ValueInsertStatement<Insert>
            implements _Insert._ChildValuesInsert {

        private final PrimaryParentValueInsertStatement<?, ?> parentStatement;

        private PrimaryChildValueInsertStatement(PrimaryParentValueInsertStatement<?, ?> parentStatement
                , StandardComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _ValuesInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildValueInsertStatement

    private static final class PrimaryParentValueInsertStatement<I extends Item, P>
            extends ValueInsertStatement<InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<I, P>>>
            implements InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<I, P>> {

        private final Function<Insert, I> function;

        private PrimaryParentValueInsertStatement(StandardComplexValuesClause<?, ?> clause,
                                                  Function<Insert, I> function) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.function = function;
        }

        @Override
        public _ChildInsertIntoClause<I, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }


        private I childInsertEnd(final StandardComplexValuesClause<?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            final Insert insert;
            insert = new PrimaryChildValueInsertStatement(this, childClause)
                    .asInsert();
            return this.function.apply(insert);
        }


    }//PrimaryParentValueInsertStatement


    static abstract class QueryInsertStatement<I extends Statement>
            extends InsertSupports.QuerySyntaxInsertStatement<I>
            implements Insert, StandardInsert {

        private QueryInsertStatement(final StandardComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }


    }//QueryInsertStatement


    private static final class PrimarySingleQueryInsertStatement extends QueryInsertStatement<Insert> {

        private PrimarySingleQueryInsertStatement(StandardComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SingleTableMeta;
        }

    }//PrimarySimpleQueryInsertStatement


    private static final class PrimaryChildQueryInsertStatement extends QueryInsertStatement<Insert>
            implements _Insert._ChildQueryInsert {

        private final PrimaryParentQueryInsertStatement<?, ?> parentStatement;

        private PrimaryChildQueryInsertStatement(PrimaryParentQueryInsertStatement<?, ?> parentStatement,
                                                 StandardComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _QueryInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildQueryInsertStatement

    private static final class PrimaryParentQueryInsertStatement<I extends Item, P>
            extends QueryInsertStatement<InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<I, P>>>
            implements InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<I, P>> {

        private final Function<Insert, I> function;

        private PrimaryParentQueryInsertStatement(StandardComplexValuesClause<?, ?> clause,
                                                  Function<Insert, I> function) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.function = function;
        }

        @Override
        public StandardInsert._ChildInsertIntoClause<I, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private I childInsertEnd(final StandardComplexValuesClause<?, ?> childClause) {
            final Insert insert;
            insert = new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
            return this.function.apply(insert);
        }


    }//PrimaryParentQueryInsertStatement


}
