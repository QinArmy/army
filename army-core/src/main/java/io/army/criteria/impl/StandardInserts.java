package io.army.criteria.impl;

import io.army.criteria.InsertStatement;
import io.army.criteria.Item;
import io.army.criteria.Statement;
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


    static <I extends Item> StandardInsert._PrimaryOptionSpec<I> primaryInsert(Function<InsertStatement, I> function) {
        return new PrimaryInsertIntoClause<>(function);
    }


    /*-------------------below standard domain insert syntax class-------------------*/
    private static final class PrimaryInsertIntoClause<I extends Item>
            extends NonQueryInsertOptionsImpl<
            StandardInsert._PrimaryNullOptionSpec<I>,
            StandardInsert._PrimaryPreferLiteralSpec<I>,
            StandardInsert._PrimaryInsertIntoClause<I>>
            implements StandardInsert._PrimaryOptionSpec<I> {

        private final Function<InsertStatement, I> function;

        private PrimaryInsertIntoClause(Function<InsertStatement, I> function) {
            super(CriteriaContexts.primaryInsertContext(null));
            ContextStack.push(this.context);
            this.function = function;
        }

        @Override
        public <T> StandardInsert._ColumnListSpec<T, I> insertInto(SingleTableMeta<T> table) {
            return new StandardComplexValuesClause<>(this, table, this::singleInsertEnd);
        }


        @Override
        public <P> StandardInsert._ColumnListSpec<P, InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<I, P>>> insertInto(ParentTableMeta<P> table) {
            return new StandardComplexValuesClause<>(this, table, this::parentInsertEnd);
        }

        private <P> InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<I, P>> parentInsertEnd(final StandardComplexValuesClause<?, ?> clause) {
            final Statement._DmlInsertClause<InsertStatement._ParentInsert<StandardInsert._ChildInsertIntoClause<I, P>>> spec;

            final InsertMode mode;
            mode = clause.getInsertMode();
            switch (mode) {
                case DOMAIN:
                    spec = new PrimaryParentDomainInsertStatement<>(clause, this.function);
                    break;
                case VALUES:
                    spec = new PrimaryParentValueInsertStatement<>(clause, this.function);
                    break;
                case QUERY:
                    spec = new PrimaryParentQueryInsertStatement<>(clause, this.function);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }

            return spec.asInsert();
        }

        private I singleInsertEnd(final StandardComplexValuesClause<?, ?> clause) {
            final Statement._DmlInsertClause<InsertStatement> spec;
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

            return this.function.apply(spec.asInsert());
        }


    }//PrimaryInsertIntoClause


    private static final class ChildInsertIntoClause<I extends Item, P>
            extends SimpleValuesSyntaxOptions
            implements StandardInsert._ChildInsertIntoClause<I, P>
            , ValueSyntaxOptions {

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
            implements StandardInsert._ColumnListSpec<T, I>
            , StandardInsert._ComplexColumnDefaultSpec<T, I>
            , Statement._DmlInsertClause<I> {

        private final Function<StandardComplexValuesClause<?, ?>, I> dmlFunction;

        private StandardComplexValuesClause(ValueSyntaxOptions options, TableMeta<T> table
                , Function<StandardComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.dmlFunction = dmlFunction;
        }


        @Override
        public StandardInsert._StandardValueStaticLeftParenClause<T, I> values() {
            return new StandardStaticValuesClause<>(this);
        }


        @Override
        public StandardQuery._StandardSelectClause<Statement._DmlInsertClause<I>> space() {
            return StandardQueries.subQuery(this.context, this::staticSpaceQueryEnd);
        }

        @Override
        public I asInsert() {
            return this.dmlFunction.apply(this);
        }


    }//StandardComplexValuesClause


    private static abstract class StandardValuesSyntaxStatement<I extends Statement.DmlInsert>
            extends ValueSyntaxInsertStatement<I>
            implements StandardInsert, InsertStatement {

        private StandardValuesSyntaxStatement(StandardComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }


    }//StandardValuesSyntaxStatement

    static abstract class DomainsInsertStatement<I extends Statement.DmlInsert>
            extends StandardValuesSyntaxStatement<I>
            implements _Insert._DomainInsert {

        private DomainsInsertStatement(StandardComplexValuesClause<?, ?> clause) {
            super(clause);
        }


    }//DomainsInsertStatement

    private static final class PrimarySingleDomainInsertStatement extends DomainsInsertStatement<InsertStatement> {

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

    private static final class PrimaryChildDomainInsertStatement extends DomainsInsertStatement<InsertStatement>
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

        private final Function<InsertStatement, I> function;

        private final List<?> originalDomainList;

        private final List<?> domainList;

        /**
         * @see PrimaryInsertIntoClause#parentInsertEnd(StandardComplexValuesClause)
         */
        private PrimaryParentDomainInsertStatement(StandardComplexValuesClause<?, ?> clause,
                                                   Function<InsertStatement, I> function) {
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
            final InsertStatement insert;
            insert = new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
            return this.function.apply(insert);
        }


    }//PrimaryParentDomainInsertStatement

    static abstract class ValueInsertStatement<I extends Statement.DmlInsert> extends StandardValuesSyntaxStatement<I>
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


    private static final class PrimarySingleValueInsertStatement extends ValueInsertStatement<InsertStatement> {

        private PrimarySingleValueInsertStatement(StandardComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }


    }//PrimarySimpleValueInsertStatement

    private static final class PrimaryChildValueInsertStatement extends ValueInsertStatement<InsertStatement>
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

        private final Function<InsertStatement, I> function;

        private PrimaryParentValueInsertStatement(StandardComplexValuesClause<?, ?> clause,
                                                  Function<InsertStatement, I> function) {
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
            final InsertStatement insert;
            insert = new PrimaryChildValueInsertStatement(this, childClause)
                    .asInsert();
            return this.function.apply(insert);
        }


    }//PrimaryParentValueInsertStatement


    static abstract class QueryInsertStatement<I extends Statement.DmlInsert>
            extends InsertSupports.QuerySyntaxInsertStatement<I>
            implements InsertStatement, StandardInsert {

        private QueryInsertStatement(final StandardComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }


    }//QueryInsertStatement


    private static final class PrimarySingleQueryInsertStatement extends QueryInsertStatement<InsertStatement> {

        private PrimarySingleQueryInsertStatement(StandardComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SingleTableMeta;
        }

    }//PrimarySimpleQueryInsertStatement


    private static final class PrimaryChildQueryInsertStatement extends QueryInsertStatement<InsertStatement>
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

        private final Function<InsertStatement, I> function;

        private PrimaryParentQueryInsertStatement(StandardComplexValuesClause<?, ?> clause,
                                                  Function<InsertStatement, I> function) {
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
            final InsertStatement insert;
            insert = new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
            return this.function.apply(insert);
        }


    }//PrimaryParentQueryInsertStatement


}
