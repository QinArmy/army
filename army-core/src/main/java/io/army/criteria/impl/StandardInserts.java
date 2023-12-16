package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.standard.StandardCtes;
import io.army.criteria.standard.StandardInsert;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;

import javax.annotation.Nullable;

import io.army.meta.*;
import io.army.struct.CodeEnum;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard value insert statement.
 *
 * @since 0.6.0
 */
abstract class StandardInserts extends InsertSupports {


    private StandardInserts() {
        throw new UnsupportedOperationException();
    }


    static StandardInsert._PrimaryOptionSpec<Insert> singleInsert() {
        return new PrimaryInsertInto10Clause<>(null, SQLs::identity);
    }

    static StandardInsert._PrimaryOption20Spec<Insert> singleInsert20() {
        return new PrimaryInsertInto20Clause<>(null, SQLs::identity);
    }


    /*-------------------below private method -------------------*/


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

    private static abstract class PrimaryInsertIntoClause<I extends Item, N, WE extends Item>
            extends NonQueryWithCteOption<N, StandardCtes, WE>
            implements StandardInsert._PrimaryInsertIntoClause<I> {

        private final Function<? super Insert, I> function;

        private PrimaryInsertIntoClause(StandardDialect dialect, @Nullable ArmyStmtSpec spec,
                                        Function<? super Insert, I> function) {
            super(CriteriaContexts.primaryInsertContext(dialect, spec));
            this.function = function;
            ContextStack.push(this.context);
        }

        @Override
        public final <T> StandardInsert._ColumnListSpec<T, I> insertInto(SimpleTableMeta<T> table) {
            return new StandardComplexValuesClause<>(this, table, this.function.compose(StandardInserts::singleInsertEnd));
        }


        @Override
        public final <P> StandardInsert._ColumnListSpec<P, InsertStatement._ParentInsert20<I, StandardInsert._ChildInsertIntoClause<I, P>>> insertInto(ParentTableMeta<P> table) {
            return new StandardComplexValuesClause<>(this, table, this::parentInsertEnd);
        }

        @Override
        final StandardCtes createCteBuilder(boolean recursive) {
            return StandardQueries.cteBuilder(recursive, this.context);
        }

        private <P> InsertStatement._ParentInsert20<I, StandardInsert._ChildInsertIntoClause<I, P>> parentInsertEnd(StandardComplexValuesClause<?, ?> clause) {
            final Statement._DmlInsertClause<InsertStatement._ParentInsert20<I, StandardInsert._ChildInsertIntoClause<I, P>>> spec;

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


    }//PrimaryInsertIntoClause


    private static final class PrimaryInsertInto10Clause<I extends Item> extends PrimaryInsertIntoClause<
            I,
            StandardInsert._PrimaryNullOptionSpec<I>,
            Item> implements StandardInsert._PrimaryOptionSpec<I> {

        private PrimaryInsertInto10Clause(@Nullable ArmyStmtSpec spec, Function<? super Insert, I> function) {
            super(StandardDialect.STANDARD10, spec, function);
        }

    }// PrimaryInsertInto10Clause


    private static final class PrimaryInsertInto20Clause<I extends Item>
            extends PrimaryInsertIntoClause<
            I,
            StandardInsert._PrimaryNullOption20Spec<I>,
            StandardInsert._PrimaryInsertIntoClause<I>>
            implements StandardInsert._PrimaryOption20Spec<I> {

        private PrimaryInsertInto20Clause(@Nullable ArmyStmtSpec spec, Function<? super Insert, I> function) {
            super(StandardDialect.STANDARD20, spec, function);
        }

        @Override
        public StandardQuery._StaticCteParensSpec<StandardInsert._PrimaryInsertIntoClause<I>> with(String name) {
            return StandardQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public StandardQuery._StaticCteParensSpec<StandardInsert._PrimaryInsertIntoClause<I>> withRecursive(String name) {
            return StandardQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

    }//PrimaryInsertInto20Clause


    private static final class ChildInsertIntoClause<I extends Item, P>
            extends SimpleValuesSyntaxOptions
            implements StandardInsert._ChildInsertIntoClause<I, P>,
            ValueSyntaxOptions {

        private final Function<StandardComplexValuesClause<?, ?>, I> dmlFunction;

        private ChildInsertIntoClause(ValueSyntaxOptions options,
                                      Function<StandardComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, CriteriaContexts.primaryInsertContext(options.getContext().dialect(), null));
            ContextStack.push(this.context);
            this.dmlFunction = dmlFunction;
        }


        @Override
        public <T> StandardInsert._ColumnListSpec<T, I> insertInto(ComplexTableMeta<P, T> table) {
            return new StandardComplexValuesClause<>(this, table, this.dmlFunction);
        }


    }//ChildInsertIntoClause


    private static final class StandardStaticValuesClause<T, I extends Item>
            extends ValuesParensClauseImpl<
            T,
            StandardInsert._ValuesParensCommaSpec<T, I>>
            implements StandardInsert._ValuesParensCommaSpec<T, I>,
            StandardInsert._StandardValuesParensClause<T, I> {

        private final StandardComplexValuesClause<T, I> claus;

        private StandardStaticValuesClause(StandardComplexValuesClause<T, I> clause) {
            super(clause.context, clause.migration, clause::validateField);
            this.claus = clause;
        }

        @Override
        public StandardInsert._StandardValuesParensClause<T, I> comma() {
            return this;
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

        private StandardComplexValuesClause(PrimaryInsertIntoClause<?, ?, ?> options, SingleTableMeta<T> table,
                                            Function<StandardComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table, true);
            this.dmlFunction = dmlFunction;
        }

        private StandardComplexValuesClause(ChildInsertIntoClause<?, ?> options, ChildTableMeta<T> table,
                                            Function<StandardComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table, true);
            this.dmlFunction = dmlFunction;
        }


        @Override
        public StandardInsert._StandardValuesParensClause<T, I> values() {
            return new StandardStaticValuesClause<>(this);
        }


        @Override
        public StandardQuery._SelectSpec<Statement._DmlInsertClause<I>> space() {
            return StandardQueries.subQuery(this.context.dialect(StandardDialect.class),
                    this.context, this::spaceQueryEnd
            );
        }

        @Override
        public Statement._DmlInsertClause<I> space(Supplier<SubQuery> supplier) {
            return this.spaceQueryEnd(supplier.get());
        }

        @Override
        public Statement._DmlInsertClause<I> space(Function<StandardQuery._SelectSpec<Statement._DmlInsertClause<I>>, Statement._DmlInsertClause<I>> function) {
            return function.apply(StandardQueries.subQuery(this.context.dialect(StandardDialect.class),
                    this.context, this::spaceQueryEnd)
            );
        }

        @Override
        public I asInsert() {
            return this.dmlFunction.apply(this);
        }

        @Override
        public String tableAlias() {
            //null,standard api don't support table alias
            return null;
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
            extends DomainsInsertStatement<InsertStatement._ParentInsert20<I, StandardInsert._ChildInsertIntoClause<I, P>>>
            implements InsertStatement._ParentInsert20<I, StandardInsert._ChildInsertIntoClause<I, P>> {

        private final Function<? super Insert, I> function;

        private final List<?> originalDomainList;

        private final List<?> domainList;

        /**
         * @see PrimaryInsertIntoClause#parentInsertEnd(StandardComplexValuesClause)
         */
        private PrimaryParentDomainInsertStatement(StandardComplexValuesClause<?, ?> clause,
                                                   Function<? super Insert, I> function) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.function = function;
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _Collections.asUnmodifiableList(this.originalDomainList);
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

        @Override
        public I space() {
            return this.function.apply(this);
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
            extends ValueInsertStatement<InsertStatement._ParentInsert20<I, StandardInsert._ChildInsertIntoClause<I, P>>>
            implements InsertStatement._ParentInsert20<I, StandardInsert._ChildInsertIntoClause<I, P>> {

        private final Function<? super Insert, I> function;

        private PrimaryParentValueInsertStatement(StandardComplexValuesClause<?, ?> clause,
                                                  Function<? super Insert, I> function) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.function = function;
        }

        @Override
        public _ChildInsertIntoClause<I, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        @Override
        public I space() {
            return this.function.apply(this);
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
        public ParentQueryInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildQueryInsertStatement

    private static final class PrimaryParentQueryInsertStatement<I extends Item, P>
            extends QueryInsertStatement<InsertStatement._ParentInsert20<I, StandardInsert._ChildInsertIntoClause<I, P>>>
            implements InsertStatement._ParentInsert20<I, StandardInsert._ChildInsertIntoClause<I, P>>,
            ParentQueryInsert {

        private final Function<? super Insert, I> function;

        private CodeEnum discriminatorValue;

        private PrimaryParentQueryInsertStatement(StandardComplexValuesClause<?, ?> clause,
                                                  Function<? super Insert, I> function) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.function = function;
        }

        @Override
        public CodeEnum discriminatorEnum() {
            final CodeEnum value = this.discriminatorValue;
            assert value != null;
            return value;
        }

        @Override
        public void onValidateEnd(final CodeEnum discriminatorValue) {
            assert this.discriminatorValue == null;
            this.discriminatorValue = discriminatorValue;
        }

        @Override
        public StandardInsert._ChildInsertIntoClause<I, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        @Override
        public I space() {
            return this.function.apply(this);
        }

        private I childInsertEnd(final StandardComplexValuesClause<?, ?> childClause) {
            final Insert insert;
            insert = new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
            return this.function.apply(insert);
        }


    }//PrimaryParentQueryInsertStatement


}
