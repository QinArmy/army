package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
import io.army.criteria.impl.inner.postgre._PostgreInsert;
import io.army.criteria.postgre.PostgreCteBuilder;
import io.army.criteria.postgre.PostgreInsert;
import io.army.criteria.postgre.PostgreStatement;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the implementation of postgre insert syntax interfaces.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreInserts extends InsertSupport {

    private PostgreInserts() {
    }


    static PostgreInsert._PrimaryOptionSpec primaryInsert() {
        return new PrimaryInsertIntoClause();
    }

    static <I extends Item> PostgreInsert._DynamicSubMaterializedSpec<I> dynamicSubInsert(CriteriaContext outContext
            , Function<SubStatement, I> function) {
        return new DynamicSubInsertIntoClause<>(outContext, function);
    }

    static <I extends Item> PostgreInsert._StaticSubOptionSpec<I> staticSubInsert(CriteriaContext outContext
            , Function<SubStatement, I> function) {
        return new StaticSubInsertIntoClause<>(outContext, function);
    }

    static <I extends Item> PostgreInsert._ComplexOptionSpec<I> complexInsert(Function<PrimaryStatement, I> function) {
        return new ComplexInsertIntoClause<>(function);
    }


    /*-------------------below private method -------------------*/

    private static <P> PostgreInsert._ParentInsert<P> parentInsertEnd(final PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DmlInsertClause<PostgreInsert._ParentInsert<P>> spec;
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

    private static <P> PostgreInsert._ParentReturnInsert<P> parentReturningEnd(PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DqlInsertClause<PostgreInsert._ParentReturnInsert<P>> spec;
        final InsertMode mode;
        mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new PrimaryParentDomainReturningInsertStatement<>(clause);
                break;
            case VALUES:
                spec = new PrimaryParentValueReturningInsertStatement<>(clause);
                break;
            case QUERY:
                spec = new PrimaryParentQueryReturningInsertStatement<>(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asReturningInsert();
    }

    private static Insert insertEnd(PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DmlInsertClause<Insert> spec;
        final InsertMode mode;
        mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new PrimaryDomainInsertStatement(clause);
                break;
            case VALUES:
                spec = new PrimaryValueInsertStatement(clause);
                break;
            case QUERY:
                spec = new PrimaryQueryInsertStatement(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asInsert();
    }


    private static ReturningInsert returningInsertEnd(PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DqlInsertClause<ReturningInsert> spec;
        final InsertMode mode;
        mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new PrimaryDomainReturningInsertStatement(clause);
                break;
            case VALUES:
                spec = new PrimaryValueReturningInsertStatement(clause);
                break;
            case QUERY:
                spec = new PrimaryQueryReturningInsertStatement(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asReturningInsert();
    }


    private static SubInsert subInsertEnd(final PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DmlInsertClause<SubInsert> spec;
        final InsertMode mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new SubDomainInsertStatement(clause);
                break;
            case VALUES:
                spec = new SubValueInsertStatement(clause);
                break;
            case QUERY:
                spec = new SubQueryInsertStatement(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asInsert();
    }

    private static SubReturningInsert subReturningInsertEnd(final PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DqlInsertClause<SubReturningInsert> spec;
        final InsertMode mode = clause.getInsertMode();
        switch (mode) {
            case DOMAIN:
                spec = new SubDomainReturningInsertStatement(clause);
                break;
            case VALUES:
                spec = new SubValueReturningInsertStatement(clause);
                break;
            case QUERY:
                spec = new SubQueryReturningInsertStatement(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asReturningInsert();
    }


    /*-------------------below insert after values syntax class-------------------*/


    private static final class PrimaryCteCommaClause implements PostgreInsert._PrimaryCteComma {

        private final boolean recursive;

        private final PrimaryInsertIntoClause clause;

        private final Function<String, PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._PrimaryCteComma>> function;

        private PrimaryCteCommaClause(boolean recursive, PrimaryInsertIntoClause clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = PostgreQueries.complexCte(clause.context, this);
        }

        @Override
        public PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._PrimaryCteComma> comma(final String name) {
            return this.function.apply(name);

        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table) {
            return this.clause.endStaticWithClause(this.recursive)
                    .insertInto(table);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, Insert, ReturningInsert> insertInto(ChildTableMeta<T> table) {
            return this.clause.endStaticWithClause(this.recursive)
                    .insertInto(table);
        }

        @Override
        public <P> PostgreInsert._TableAliasSpec<P, PostgreInsert._ParentInsert<P>, PostgreInsert._ParentReturnInsert<P>> insertInto(ParentTableMeta<P> table) {
            return this.clause.endStaticWithClause(this.recursive)
                    .insertInto(table);
        }


    }//PrimaryCteCommaClause


    private static final class ChildCteComma<P> implements PostgreInsert._ChildCteComma<P> {

        private final boolean recursive;

        private final ChildInsertIntoClause<P> primaryClause;

        private final Function<String, PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._ChildCteComma<P>>> function;

        private ChildCteComma(boolean recursive, ChildInsertIntoClause<P> clause) {
            this.recursive = recursive;
            this.primaryClause = clause;
            this.function = PostgreQueries.complexCte(clause.context, this);
        }

        @Override
        public PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._ChildCteComma<P>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table) {
            return this.primaryClause.endStaticWithClause(this.recursive)
                    .insertInto(table);
        }


    }//ChildCteComma


    private static final class PrimaryInsertIntoClause extends NonQueryWithCteOption<
            PostgreInsert._PrimaryNullOptionSpec,
            PostgreInsert._PrimaryPreferLiteralSpec,
            PostgreInsert._PrimaryWithCteSpec,
            PostgreCteBuilder,
            PostgreInsert._PrimaryInsertIntoClause>
            implements PostgreInsert._PrimaryOptionSpec {

        private PrimaryInsertIntoClause() {
            super(CriteriaContexts.primaryInsertContext());
            ContextStack.push(this.context);
        }


        @Override
        public PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._PrimaryCteComma> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new PrimaryCteCommaClause(recursive, this).function.apply(name);
        }

        @Override
        public PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._PrimaryCteComma> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new PrimaryCteCommaClause(recursive, this).function.apply(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table) {
            return new PostgreComplexValuesClause<>(this, table, PostgreInserts::insertEnd, PostgreInserts::returningInsertEnd);
        }

        @Override
        public <P> PostgreInsert._TableAliasSpec<P, PostgreInsert._ParentInsert<P>, PostgreInsert._ParentReturnInsert<P>> insertInto(ParentTableMeta<P> table) {
            return new PostgreComplexValuesClause<>(this, table, PostgreInserts::parentInsertEnd, PostgreInserts::parentReturningEnd);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, Insert, ReturningInsert> insertInto(ChildTableMeta<T> table) {
            return new PostgreComplexValuesClause<>(this, table, PostgreInserts::insertEnd, PostgreInserts::returningInsertEnd);
        }

        @Override
        PostgreCteBuilder createCteBuilder(final boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    }//PrimaryInsertIntoClause

    private static final class ChildInsertIntoClause<P> extends ChildDynamicWithClause<
            PostgreCteBuilder,
            PostgreInsert._ChildInsertIntoClause<P>>
            implements PostgreInsert._ChildWithCteSpec<P> {

        private final Function<PostgreComplexValuesClause<?, ?, ?>, Insert> dmlFunction;

        private final Function<PostgreComplexValuesClause<?, ?, ?>, ReturningInsert> dqlFunction;

        private ChildInsertIntoClause(ValueSyntaxOptions parentOption
                , Function<PostgreComplexValuesClause<?, ?, ?>, Insert> dmlFunction
                , Function<PostgreComplexValuesClause<?, ?, ?>, ReturningInsert> dqlFunction) {
            super(parentOption, CriteriaContexts.primaryInsertContext());
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
            ContextStack.push(this.context);
        }

        @Override
        public PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._ChildCteComma<P>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new ChildCteComma<>(recursive, this).function.apply(name);
        }

        @Override
        public PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._ChildCteComma<P>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new ChildCteComma<>(recursive, this).function.apply(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table) {
            return new PostgreComplexValuesClause<>(this, table, this.dmlFunction, this.dqlFunction);
        }

        @Override
        PostgreCteBuilder createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    }//ChildInsertIntoClause


    private static final class StaticSubInsertIntoClause<I extends Item>
            extends NonQueryInsertOptionsImpl<
            PostgreInsert._StaticSubNullOptionSpec<I>,
            PostgreInsert._StaticSubPreferLiteralSpec<I>,
            PostgreInsert._ComplexInsertIntoClause<I, I>>
            implements PostgreInsert._StaticSubOptionSpec<I>
            , WithValueSyntaxOptions {


        private final Function<PostgreComplexValuesClause<?, ?, ?>, I> dmlFunction;

        private final Function<PostgreComplexValuesClause<?, ?, ?>, I> dqlFunction;

        private StaticSubInsertIntoClause(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(CriteriaContexts.cteInsertContext(outerContext));
            this.dmlFunction = function.compose(PostgreInserts::subInsertEnd);
            this.dqlFunction = function.compose(PostgreInserts::subReturningInsertEnd);
            //just push sub context,here don't need to start cte
            ContextStack.push(this.context);

        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, I, I> insertInto(TableMeta<T> table) {
            return new PostgreComplexValuesClause<>(this, table, this.dmlFunction, this.dqlFunction);
        }

        @Override
        public boolean isRecursive() {
            return false;
        }

        @Override
        public List<_Cte> cteList() {
            //static with cte don't support WITH clause
            return Collections.emptyList();
        }

    }//StaticSubInsertIntoClause


    private static final class DynamicSubInsertIntoClause<I extends Item>
            extends NonQueryWithCteOption<
            PostgreInsert._DynamicSubNullOptionSpec<I>,
            PostgreInsert._DynamicSubPreferLiteralSpec<I>,
            PostgreInsert._DynamicSubWithCteSpec<I>,
            PostgreCteBuilder,
            PostgreInsert._ComplexInsertIntoClause<I, I>>
            implements PostgreInsert._DynamicSubMaterializedSpec<I> {

        private final Function<SubStatement, I> function;

        private PostgreSupports.MaterializedOption materializedOption;

        private List<String> columnAliasList;

        private DynamicSubInsertIntoClause(final CriteriaContext outContext, Function<SubStatement, I> function) {
            super(CriteriaContexts.cteInsertContext(outContext));
            this.function = function;
            ContextStack.push(this.context);
        }


        @Override
        public PostgreInsert._DynamicSubOptionSpec<I> materialized() {
            this.materializedOption = PostgreSupports.MaterializedOption.MATERIALIZED;
            return this;
        }

        @Override
        public PostgreInsert._DynamicSubOptionSpec<I> notMaterialized() {
            this.materializedOption = PostgreSupports.MaterializedOption.NOT_MATERIALIZED;
            return this;
        }

        @Override
        public PostgreInsert._DynamicSubOptionSpec<I> ifMaterialized(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.materializedOption = PostgreSupports.MaterializedOption.MATERIALIZED;
            } else {
                this.materializedOption = null;
            }
            return this;
        }

        @Override
        public PostgreInsert._DynamicSubOptionSpec<I> ifNotMaterialized(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.materializedOption = PostgreSupports.MaterializedOption.NOT_MATERIALIZED;
            } else {
                this.materializedOption = null;
            }
            return this;
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, I, I> insertInto(TableMeta<T> table) {
            return new PostgreComplexValuesClause<>(this, table, this::subInsertEnd, this::subReturningInsertEnd);
        }

        @Override
        PostgreCteBuilder createCteBuilder(final boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }

        private I subInsertEnd(final PostgreComplexValuesClause<?, ?, ?> clause) {
            final SubInsert subInsert;
            subInsert = PostgreInserts.subInsertEnd(clause);
            final PostgreSupports.MaterializedOption option = this.materializedOption;
            return this.function.apply(
                    option == null ? subInsert : new PostgreSupports.PostgreSubStatement(option, subInsert)
            );
        }

        private I subReturningInsertEnd(final PostgreComplexValuesClause<?, ?, ?> clause) {
            final SubReturningInsert subInsert;
            subInsert = PostgreInserts.subReturningInsertEnd(clause);
            final PostgreSupports.MaterializedOption option = this.materializedOption;
            return this.function.apply(
                    option == null ? subInsert : new PostgreSupports.PostgreSubStatement(option, subInsert)
            );
        }


    }//DynamicSubInsertIntoClause


    private static final class ComplexComma<I extends Item> implements PostgreInsert._ComplexComma<I> {

        private final boolean recursive;

        private final ComplexInsertIntoClause<I> clause;

        private final Function<String, PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._ComplexComma<I>>> function;

        private ComplexComma(boolean recursive, ComplexInsertIntoClause<I> clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = PostgreQueries.complexCte(clause.context, this);
        }

        @Override
        public PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._ComplexComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, I, I> insertInto(TableMeta<T> table) {
            return this.clause.endStaticWithClause(this.recursive).insertInto(table);
        }

    }//ComplexComma


    private static final class ComplexInsertIntoClause<I extends Item> extends NonQueryWithCteOption<
            PostgreInsert._ComplexNullOptionSpec<I>,
            PostgreInsert._ComplexPreferLiteralSpec<I>,
            PostgreInsert._ComplexWithSpec<I>,
            PostgreCteBuilder,
            PostgreInsert._ComplexInsertIntoClause<I, I>>
            implements PostgreInsert._ComplexOptionSpec<I> {

        private final Function<PrimaryStatement, I> function;


        private ComplexInsertIntoClause(Function<PrimaryStatement, I> function) {
            super(CriteriaContexts.primaryInsertContext());
            this.function = function;
            ContextStack.push(this.context);
        }


        @Override
        public PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._ComplexComma<I>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new ComplexComma<>(recursive, this).function.apply(name);
        }

        @Override
        public PostgreStatement._StaticCteLeftParenSpec<PostgreInsert._ComplexComma<I>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new ComplexComma<>(recursive, this).function.apply(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, I, I> insertInto(TableMeta<T> table) {
            return new PostgreComplexValuesClause<>(this, table
                    , this.function.compose(PostgreInserts::insertEnd)
                    , this.function.compose(PostgreInserts::returningInsertEnd)
            );
        }

        @Override
        PostgreCteBuilder createCteBuilder(final boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    }//PrimaryInsertIntoClause


    private enum OverridingMode implements SQLWords {

        OVERRIDING_SYSTEM_VALUE(" OVERRIDING SYSTEM VALUE"),
        OVERRIDING_USER_VALUE(" OVERRIDING USER VALUE");

        private final String spaceWords;

        OverridingMode(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }


        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(OverridingMode.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }


    }//OverridingMode


    private static final class ConflictTargetItem<T, I extends Item, Q extends Item>
            implements PostgreInsert._ConflictCollateSpec<T, I, Q>
            , _ConflictTargetItem {

        private final OnConflictClause<T, I, Q> clause;

        private final IndexFieldMeta<T> indexColumn;

        private String collationName;

        private Boolean opclass;

        private ConflictTargetItem(OnConflictClause<T, I, Q> clause, IndexFieldMeta<T> indexColumn) {
            this.clause = clause;
            this.indexColumn = indexColumn;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.appendField(this.indexColumn);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            final String collationName = this.collationName;
            if (collationName != null) {
                sqlBuilder.append(" COLLATE ");
                context.parser().identifier(collationName, sqlBuilder);
            }
            final Boolean opclass = this.opclass;
            if (opclass != null && opclass) {
                sqlBuilder.append(" opclass");
            }
        }

        @Override
        public PostgreInsert._ConflictOpClassSpec<T, I, Q> collation(final @Nullable String collationName) {
            if (collationName == null) {
                throw ContextStack.nullPointer(this.clause.valuesClause.context);
            } else if (this.collationName != null || this.opclass != null) {
                throw ContextStack.castCriteriaApi(this.clause.valuesClause.context);
            }
            this.collationName = collationName;
            return this;
        }

        @Override
        public PostgreInsert._ConflictOpClassSpec<T, I, Q> collation(Supplier<String> supplier) {
            return this.collation(supplier.get());
        }

        @Override
        public PostgreInsert._ConflictOpClassSpec<T, I, Q> ifCollation(Supplier<String> supplier) {
            final String collation;
            collation = supplier.get();
            if (collation != null) {
                this.collation(collationName);
            }
            return this;
        }

        @Override
        public PostgreInsert._ConflictTargetCommaSpec<T, I, Q> opClass() {
            if (this.opclass != null) {
                throw ContextStack.castCriteriaApi(this.clause.valuesClause.context);
            }
            this.opclass = Boolean.TRUE;
            return this;
        }

        @Override
        public PostgreInsert._ConflictTargetCommaSpec<T, I, Q> ifOpClass(BooleanSupplier supplier) {
            if (this.opclass != null) {
                throw ContextStack.castCriteriaApi(this.clause.valuesClause.context);
            }
            if (supplier.getAsBoolean()) {
                this.opclass = Boolean.TRUE;
            } else {
                this.opclass = Boolean.FALSE;
            }
            return this;
        }

        @Override
        public PostgreInsert._ConflictCollateSpec<T, I, Q> comma(IndexFieldMeta<T> indexColumn) {
            if (this.opclass == null) {
                this.opclass = Boolean.FALSE;
            }
            return this.clause.leftParen(indexColumn); // create and add
        }

        @Override
        public PostgreInsert._ConflictTargetWhereSpec<T, I, Q> rightParen() {
            if (this.opclass == null) {
                this.opclass = Boolean.FALSE;
            }
            return this.clause.targetItemClauseEnd();
        }


    }//ConflictTargetItem


    private static final class ConflictDoUpdateActionClause<T, I extends Item, Q extends Item>
            extends SetWhereClause<
            FieldMeta<T>,
            RowPairs<FieldMeta<T>>,
            PostgreInsert._DoUpdateWhereSpec<T, I, Q>,
            PostgreInsert._DoUpdateWhereClause<I, Q>,
            PostgreInsert._ReturningSpec<I, Q>,
            PostgreInsert._DoUpdateWhereAndSpec<I, Q>,
            Object,
            Object,
            Object,
            Object>
            implements PostgreInsert._DoUpdateWhereSpec<T, I, Q>
            , PostgreInsert._DoUpdateWhereAndSpec<I, Q> {

        private final OnConflictClause<T, I, Q> onConflictClause;

        private ConflictDoUpdateActionClause(OnConflictClause<T, I, Q> clause) {
            super(clause.valuesClause.context, clause.valuesClause.insertTable, clause.safeTableAlias);
            this.onConflictClause = clause;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Selection selection) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(selection);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Expression expression, StandardSyntax.WordAs wordAs
                , String alias) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(expression, wordAs, alias);
        }


        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Supplier<Selection> supplier) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(supplier);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(exp1, exp2);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(exp1, exp2, exp3);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3, NamedExpression exp4) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(exp1, exp2, exp3, exp4);
        }

        @Override
        public Statement._DqlInsertClause<Q> returningAll() {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returningAll();
        }

        @Override
        public Statement._DqlInsertClause<Q> returning(Consumer<ReturningBuilder> consumer) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(consumer);
        }

        @Override
        public I asInsert() {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .asInsert();
        }

        @Override
        RowPairs<FieldMeta<T>> createItemPairBuilder(Consumer<ItemPair> consumer) {
            return CriteriaSupports.rowPairs(consumer);
        }


    }//ConflictDoUpdateActionClause


    private static final class OnConflictClause<T, I extends Item, Q extends Item>
            extends WhereClause<
            PostgreInsert._ConflictActionClause<T, I, Q>,
            PostgreInsert._ConflictTargetWhereAndSpec<T, I, Q>,
            Object,
            Object,
            Object,
            Object>
            implements PostgreInsert._ConflictTargetOptionSpec<T, I, Q>
            , PostgreInsert._ConflictTargetWhereSpec<T, I, Q>
            , PostgreInsert._ConflictTargetWhereAndSpec<T, I, Q> {

        private final PostgreComplexValuesClause<T, I, Q> valuesClause;

        private final String safeTableAlias;

        private List<_ConflictTargetItem> targetItemList;

        private String constraintName;

        private boolean doNothing;

        private OnConflictClause(PostgreComplexValuesClause<T, I, Q> valuesClause) {
            super(valuesClause.context);
            this.valuesClause = valuesClause;
            final String tableAlias = valuesClause.tableAlias;
            this.safeTableAlias = tableAlias == null ? "" : tableAlias;
        }

        @Override
        public PostgreInsert._ConflictCollateSpec<T, I, Q> leftParen(IndexFieldMeta<T> indexColumn) {
            List<_ConflictTargetItem> targetItemList = this.targetItemList;
            if (targetItemList == null) {
                targetItemList = new ArrayList<>();
                this.targetItemList = targetItemList;
            } else if (!(targetItemList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.valuesClause.context);
            }
            final ConflictTargetItem<T, I, Q> item = new ConflictTargetItem<>(this, indexColumn);
            targetItemList.add(item);
            return item;
        }

        @Override
        public PostgreInsert._ConflictActionClause<T, I, Q> onConstraint(final @Nullable String constraintName) {
            if (this.constraintName != null) {
                throw ContextStack.castCriteriaApi(this.valuesClause.context);
            } else if (constraintName == null) {
                throw ContextStack.nullPointer(this.valuesClause.context);
            }
            this.constraintName = constraintName;
            return this;
        }

        @Override
        public PostgreInsert._ReturningSpec<I, Q> doNothing() {
            this.endWhereClause();
            this.doNothing = true;
            return this.valuesClause.conflictClauseEnd(new ConflictActionClauseResult(this));
        }

        @Override
        public PostgreInsert._DoUpdateSetClause<T, I, Q> doUpdate() {
            this.endWhereClause();
            return new ConflictDoUpdateActionClause<>(this);
        }

        private PostgreInsert._ConflictTargetWhereSpec<T, I, Q> targetItemClauseEnd() {
            final List<_ConflictTargetItem> targetItemList = this.targetItemList;
            if (targetItemList instanceof ArrayList) {
                this.targetItemList = Collections.unmodifiableList(targetItemList);
            } else {
                throw ContextStack.castCriteriaApi(this.valuesClause.context);
            }
            return this;
        }

        private PostgreInsert._ReturningSpec<I, Q> updateActionClauseEnd(List<_ItemPair> itemPairList
                , List<_Predicate> predicateList) {
            return this.valuesClause
                    .conflictClauseEnd(new ConflictActionClauseResult(this, itemPairList, predicateList));
        }


    }//OnConflictClause


    private static final class ConflictActionClauseResult
            implements _PostgreInsert._ConflictActionClauseResult {

        private final List<_ConflictTargetItem> targetItemList;

        private final List<_Predicate> indexPredicateList;

        private final String constraintName;

        private final boolean doNothing;

        private final List<_ItemPair> itemPairList;

        private final List<_Predicate> updatePredicateList;

        private ConflictActionClauseResult(OnConflictClause<?, ?, ?> clause) {
            this.targetItemList = _CollectionUtils.safeList(clause.targetItemList);
            if (this.targetItemList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(clause.valuesClause.context);
            }
            this.indexPredicateList = clause.wherePredicateList();
            this.constraintName = clause.constraintName;
            this.doNothing = clause.doNothing;

            this.itemPairList = Collections.emptyList();
            this.updatePredicateList = Collections.emptyList();
        }

        private ConflictActionClauseResult(OnConflictClause<?, ?, ?> clause, List<_ItemPair> itemPairList
                , List<_Predicate> updatePredicateList) {
            this.doNothing = clause.doNothing;
            this.targetItemList = _CollectionUtils.safeList(clause.targetItemList);
            if (this.targetItemList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(clause.valuesClause.context);
            }
            this.indexPredicateList = clause.wherePredicateList();
            this.constraintName = clause.constraintName;


            this.itemPairList = itemPairList;
            this.updatePredicateList = updatePredicateList;
        }

        @Override
        public List<_ItemPair> updateSetClauseList() {
            return this.itemPairList;
        }

        @Override
        public List<_Predicate> updateSetPredicateList() {
            return this.updatePredicateList;
        }

        @Override
        public String constraintName() {
            return this.constraintName;
        }

        @Override
        public List<_ConflictTargetItem> conflictTargetItemList() {
            return this.targetItemList;
        }

        @Override
        public List<_Predicate> indexPredicateList() {
            return this.indexPredicateList;
        }

        @Override
        public boolean isDoNothing() {
            return this.doNothing;
        }


    }//ConflictActionClauseResult


    private static final class PostgreComplexValuesClause<T, I extends Item, Q extends Item>
            extends ComplexInsertValuesClause<
            T,
            PostgreInsert._ComplexOverridingValueSpec<T, I, Q>,
            PostgreInsert._ValuesDefaultSpec<T, I, Q>,
            PostgreInsert._OnConflictSpec<T, I, Q>>
            implements PostgreInsert._TableAliasSpec<T, I, Q>
            , PostgreInsert._OnConflictSpec<T, I, Q>
            , PostgreInsert._StaticReturningCommaSpec<Q>
            , Statement._DqlInsertClause<Q> {

        private final Function<PostgreComplexValuesClause<?, ?, ?>, I> dmlFunction;

        private final Function<PostgreComplexValuesClause<?, ?, ?>, Q> dqlFunction;

        private final boolean recursive;

        private final List<_Cte> cteList;

        private String tableAlias;

        private OverridingMode overridingMode;

        private _PostgreInsert._ConflictActionClauseResult conflictAction;

        private List<Selection> returningList;


        /**
         * @see PrimaryInsertIntoClause#insertInto(SimpleTableMeta)
         * @see PrimaryInsertIntoClause#insertInto(ParentTableMeta)
         * @see PrimaryInsertIntoClause#insertInto(ChildTableMeta)
         * @see ChildInsertIntoClause#insertInto(ComplexTableMeta)
         */
        private PostgreComplexValuesClause(WithValueSyntaxOptions options, TableMeta<T> table
                , Function<PostgreComplexValuesClause<?, ?, ?>, I> dmlFunction
                , Function<PostgreComplexValuesClause<?, ?, ?>, Q> dqlFunction) {
            super(options, table);
            this.recursive = options.isRecursive();
            this.cteList = options.cteList();
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        @Override
        public PostgreInsert._ColumnListSpec<T, I, Q> as(final @Nullable String alias) {
            if (alias == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.tableAlias = alias;
            return this;
        }

        @Override
        public PostgreInsert._ValuesDefaultSpec<T, I, Q> overridingSystemValue() {
            this.overridingMode = OverridingMode.OVERRIDING_SYSTEM_VALUE;
            return this;
        }

        @Override
        public PostgreInsert._ValuesDefaultSpec<T, I, Q> overridingUserValue() {
            this.overridingMode = OverridingMode.OVERRIDING_USER_VALUE;
            return this;
        }

        @Override
        public PostgreInsert._ValuesDefaultSpec<T, I, Q> ifOverridingSystemValue(BooleanSupplier supplier) {
            if (supplier.getAsBoolean()) {
                this.overridingMode = OverridingMode.OVERRIDING_SYSTEM_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }

        @Override
        public PostgreInsert._ValuesDefaultSpec<T, I, Q> ifOverridingUserValue(BooleanSupplier supplier) {
            if (supplier.getAsBoolean()) {
                this.overridingMode = OverridingMode.OVERRIDING_USER_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }

        @Override
        public PostgreInsert._ValuesLeftParenClause<T, I, Q> values() {
            return new StaticValuesLeftParenClause<>(this);
        }

        @Override
        public PostgreInsert._ConflictTargetOptionSpec<T, I, Q> onConflict() {
            return new OnConflictClause<>(this);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Selection selection) {
            this.onAddSelection(selection);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Expression expression, StandardSyntax.WordAs wordAs
                , String alias) {
            this.onAddSelection(Selections.forExp((ArmyExpression) expression, alias));
            return this;
        }


        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Supplier<Selection> supplier) {
            this.onAddSelection(supplier.get());
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2) {
            this.onAddSelection(exp1)
                    .add(exp2);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3, NamedExpression exp4) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            list.add(exp4);
            return this;
        }


        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(final Selection selection) {
            this.onAddSelection(selection);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(Expression expression, StandardSyntax.WordAs wordAs
                , String alias) {
            return this.comma(Selections.forExp((ArmyExpression) expression, alias));
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(Supplier<Selection> supplier) {
            return this.comma(supplier.get());
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2) {
            this.onAddSelection(exp1)
                    .add(exp2);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3, NamedExpression exp4) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            list.add(exp4);
            return this;
        }

        @Override
        public Statement._DqlInsertClause<Q> returningAll() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = PostgreSupports.RETURNING_ALL;
            return this;
        }

        @Override
        public Statement._DqlInsertClause<Q> returning(Consumer<ReturningBuilder> consumer) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            consumer.accept(CriteriaSupports.returningBuilder(this::onAddSelection));
            return this;
        }

        @Override
        public I asInsert() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this.dmlFunction.apply(this);
        }


        @Override
        public Q asReturningInsert() {
            final List<Selection> selectionList = this.returningList;
            if (selectionList != PostgreSupports.RETURNING_ALL) {
                if (!(selectionList instanceof ArrayList && selectionList.size() > 0)) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
                this.returningList = _CollectionUtils.unmodifiableList(selectionList);
            }
            return this.dqlFunction.apply(this);
        }

        private PostgreInsert._ReturningSpec<I, Q> conflictClauseEnd(_PostgreInsert._ConflictActionClauseResult result) {
            if (this.conflictAction != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.conflictAction = result;
            return this;
        }

        private List<? extends Selection> effectiveReturningList() {
            final List<Selection> selectionList = this.returningList;
            final List<? extends Selection> effectiveList;
            if (selectionList == PostgreSupports.RETURNING_ALL) {
                effectiveList = this.effectiveFieldList();
            } else if (selectionList == null || selectionList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            } else {
                effectiveList = selectionList;
            }
            return effectiveList;
        }


        private List<Selection> onAddSelection(final @Nullable Selection selection) {
            if (selection == null) {
                throw ContextStack.nullPointer(this.context);
            }
            List<Selection> list = this.returningList;
            if (list == null) {
                list = new ArrayList<>();
                this.returningList = list;
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            list.add(selection);
            return list;
        }


    }//PostgreComplexInsertValuesClause


    private static final class StaticValuesLeftParenClause<T, I extends Item, Q extends Item>
            extends InsertSupport.StaticColumnValuePairClause<
            T,
            PostgreInsert._ValuesLeftParenSpec<T, I, Q>>
            implements PostgreInsert._ValuesLeftParenSpec<T, I, Q> {

        private final PostgreComplexValuesClause<T, I, Q> clause;

        private StaticValuesLeftParenClause(PostgreComplexValuesClause<T, I, Q> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public PostgreInsert._ConflictTargetOptionSpec<T, I, Q> onConflict() {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .onConflict();
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Selection selection) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(selection);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Expression expression, StandardSyntax.WordAs wordAs
                , String alias) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(expression, wordAs, alias);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Supplier<Selection> supplier) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(supplier);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(exp1, exp2);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(exp1, exp2, exp3);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3, NamedExpression exp4) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(exp1, exp2, exp3, exp4);
        }

        @Override
        public Statement._DqlInsertClause<Q> returningAll() {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returningAll();
        }


        @Override
        public Statement._DqlInsertClause<Q> returning(Consumer<ReturningBuilder> consumer) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(consumer);
        }

        @Override
        public I asInsert() {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .asInsert();
        }


    }//StaticValuesLeftParenClause


    private static abstract class PostgreValueSyntaxInsertStatement<I extends DmlInsert, Q extends DqlInsert>
            extends AbstractValueSyntaxStatement<I, Q>
            implements PostgreInsert, _PostgreInsert {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final String tableAlias;

        private final OverridingMode overridingMode;

        private final _ConflictActionClauseResult conflictAction;

        private final List<? extends SelectItem> returningList;


        private PostgreValueSyntaxInsertStatement(final PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.recursive = clause.recursive;
            this.cteList = clause.cteList;
            this.tableAlias = clause.tableAlias;
            this.overridingMode = clause.overridingMode;

            this.conflictAction = clause.conflictAction;
            if (this instanceof DqlInsert) {
                this.returningList = clause.effectiveReturningList();
            } else {
                this.returningList = Collections.emptyList();
            }
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            return this.cteList;
        }

        @Override
        public final String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public final List<? extends SelectItem> returningList() {
            return this.returningList;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictAction != null;
        }

        @Override
        public final SQLWords overridingValueWords() {
            return this.overridingMode;
        }

        @Override
        public final _ConflictActionClauseResult getConflictActionResult() {
            return this.conflictAction;
        }

        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(PostgreDialect.POSTGRE14, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//PrimaryValueSyntaxInsertStatement


    static abstract class DomainInsertStatement<I extends DmlInsert, Q extends DqlInsert>
            extends PostgreValueSyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreDomainInsert {


        private DomainInsertStatement(final PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);

        }


    }//DomainInsertStatement

    private static final class PrimaryDomainInsertStatement extends DomainInsertStatement<Insert, ReturningInsert>
            implements Insert {

        private final List<?> domainList;

        private PrimaryDomainInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.domainList = clause.domainListForNonParent();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

    }//PrimaryDomainInsertStatement

    private static final class PrimaryChildDomainInsertStatement
            extends DomainInsertStatement<Insert, ReturningInsert>
            implements Insert, _PostgreInsert._PostgreChildDomainInsert {

        private final DomainInsertStatement<?, ?> parentStatement;

        private final List<?> domainList;


        private PrimaryChildDomainInsertStatement(DomainInsertStatement<?, ?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            parentStatement.prepared();
            this.parentStatement = parentStatement;
            if (parentStatement instanceof PrimaryParentDomainInsertStatement) {
                this.domainList = ((PrimaryParentDomainInsertStatement<?>) parentStatement).domainList;
            } else if (parentStatement instanceof PrimaryParentDomainReturningInsertStatement) {
                this.domainList = ((PrimaryParentDomainReturningInsertStatement<?>) parentStatement).domainList;
            } else {
                //no bug,never here
                throw new IllegalArgumentException();
            }

        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _PostgreDomainInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildDomainInsertStatement


    private static final class PrimaryParentDomainInsertStatement<P>
            extends DomainInsertStatement<PostgreInsert._ParentInsert<P>, ReturningInsert>
            implements PostgreInsert._ParentInsert<P>
            , ValueSyntaxOptions {

        private final List<?> originalDomainList;

        private final List<?> domainList;

        private PrimaryParentDomainInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _CollectionUtils.asUnmodifiableList(this.originalDomainList);
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            assertDomainList(this.originalDomainList, childClause);
            return new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            assertDomainList(this.originalDomainList, childClause);
            return new PrimaryChildDomainReturningInsertStatement(this, childClause);
        }


    }//PrimaryParentDomainInsertStatement

    private static final class PrimaryDomainReturningInsertStatement
            extends DomainInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert {

        private final List<?> domainList;

        private PrimaryDomainReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.domainList = clause.domainListForNonParent();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }


    }//PrimaryDomainReturningInsertStatement


    private static final class PrimaryChildDomainReturningInsertStatement
            extends DomainInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, _PostgreInsert._PostgreChildDomainInsert {

        private final DomainInsertStatement<?, ?> parentStatement;

        private final List<?> domainList;

        private PrimaryChildDomainReturningInsertStatement(DomainInsertStatement<?, ?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            this.parentStatement = parentStatement;
            if (parentStatement instanceof PrimaryParentDomainReturningInsertStatement) {
                this.domainList = ((PrimaryParentDomainReturningInsertStatement<?>) parentStatement).domainList;
            } else if (parentStatement instanceof PrimaryParentDomainInsertStatement) {
                this.domainList = ((PrimaryParentDomainInsertStatement<?>) parentStatement).domainList;
            } else {
                //no bug,never here
                throw new IllegalArgumentException();
            }
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _PostgreDomainInsert parentStmt() {
            return this.parentStatement;
        }

    }//PrimaryChildDomainReturningInsertStatement

    private static final class PrimaryParentDomainReturningInsertStatement<P>
            extends DomainInsertStatement<Insert, PostgreInsert._ParentReturnInsert<P>>
            implements PostgreInsert._ParentReturnInsert<P>
            , ValueSyntaxOptions {

        private final List<?> originalDomainList;
        private final List<?> domainList;

        private PrimaryParentDomainReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _CollectionUtils.asUnmodifiableList(this.originalDomainList);
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd
                    , this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            assertDomainList(this.originalDomainList, childClause);
            return new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            assertDomainList(this.originalDomainList, childClause);
            return new PrimaryChildDomainReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentDomainReturningInsertStatement


    private static final class SubDomainInsertStatement extends DomainInsertStatement<SubInsert, SubReturningInsert>
            implements SubInsert {

        private final List<?> domainList;

        private SubDomainInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            if (clause.insertTable instanceof ParentTableMeta) {
                this.domainList = _CollectionUtils.asUnmodifiableList(clause.originalDomainList());
            } else {
                this.domainList = clause.domainListForNonParent();
            }
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

    }//SubDomainInsertStatement

    private static final class SubDomainReturningInsertStatement
            extends DomainInsertStatement<SubInsert, SubReturningInsert>
            implements SubReturningInsert {

        private final List<?> domainList;

        private SubDomainReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            if (clause.insertTable instanceof ParentTableMeta) {
                this.domainList = _CollectionUtils.asUnmodifiableList(clause.originalDomainList());
            } else {
                this.domainList = clause.domainListForNonParent();
            }
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

    }//SubDomainReturningInsertStatement


    static abstract class ValueInsertStatement<I extends DmlInsert, Q extends DqlInsert>
            extends PostgreValueSyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreValueInsert {

        final List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private ValueInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.rowPairList = clause.rowPairList();
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.rowPairList;
        }


    }//ValueInsertStatement


    private static final class PrimaryValueInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements Insert {

        private PrimaryValueInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

    }//PrimaryValueInsertStatement


    private static final class PrimaryChildValueInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildValueInsert
            , Insert {

        private final ValueInsertStatement<?, ?> parentStatement;

        private PrimaryChildValueInsertStatement(ValueInsertStatement<?, ?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            assert parentStatement instanceof PrimaryParentValueInsertStatement
                    || parentStatement instanceof PrimaryParentValueReturningInsertStatement;
            this.parentStatement = parentStatement;
        }

        @Override
        public _PostgreValueInsert parentStmt() {
            return this.parentStatement;
        }

    }//PrimaryChildValueInsertStatement


    private static final class PrimaryParentValueInsertStatement<P>
            extends ValueInsertStatement<PostgreInsert._ParentInsert<P>, ReturningInsert>
            implements PostgreInsert._ParentInsert<P>
            , ValueSyntaxOptions {

        private PrimaryParentValueInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentValueInsertStatement

    private static final class PrimaryValueReturningInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert {

        private PrimaryValueReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//PrimaryValueReturningInsertStatement

    private static final class PrimaryChildValueReturningInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildValueInsert, ReturningInsert {

        private final ValueInsertStatement<?, ?> parentStatement;

        private PrimaryChildValueReturningInsertStatement(ValueInsertStatement<?, ?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            assert parentStatement instanceof PrimaryParentValueInsertStatement
                    || parentStatement instanceof PrimaryParentValueReturningInsertStatement;
            parentStatement.prepared();
            this.parentStatement = parentStatement;
        }

        @Override
        public _PostgreValueInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildValueReturningInsertStatement

    private static final class PrimaryParentValueReturningInsertStatement<P>
            extends ValueInsertStatement<Insert, PostgreInsert._ParentReturnInsert<P>>
            implements PostgreInsert._ParentReturnInsert<P>
            , ValueSyntaxOptions {

        private PrimaryParentValueReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this
                    , this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentValueReturningInsertStatement


    private static final class SubValueInsertStatement
            extends ValueInsertStatement<SubInsert, SubReturningInsert>
            implements SubInsert {

        private SubValueInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//SubValueInsertStatement


    private static final class SubValueReturningInsertStatement
            extends ValueInsertStatement<SubInsert, SubReturningInsert>
            implements SubReturningInsert {

        private SubValueReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

    }//SubValueReturningInsertStatement


    static abstract class QueryInsertStatement<I extends DmlInsert, Q extends DqlInsert>
            extends InsertSupport.AbstractQuerySyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreQueryInsert, PostgreInsert {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final String tableAlias;

        private final OverridingMode overridingMode;

        private final _ConflictActionClauseResult conflictAction;

        private final List<? extends SelectItem> returningList;

        private QueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.recursive = clause.recursive;
            this.cteList = clause.cteList;
            this.tableAlias = clause.tableAlias;
            this.overridingMode = clause.overridingMode;

            this.conflictAction = clause.conflictAction;
            if (this instanceof DqlInsert) {
                this.returningList = clause.effectiveReturningList();
            } else {
                this.returningList = Collections.emptyList();
            }
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            return this.cteList;
        }

        @Override
        public final String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public final SQLWords overridingValueWords() {
            return this.overridingMode;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictAction != null;
        }

        @Override
        public final _ConflictActionClauseResult getConflictActionResult() {
            return this.conflictAction;
        }

        @Override
        public final List<? extends SelectItem> returningList() {
            return this.returningList;
        }


        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(PostgreDialect.POSTGRE14, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//QueryInsertStatement


    private static final class PrimaryQueryInsertStatement extends QueryInsertStatement<Insert, ReturningInsert>
            implements Insert {

        private PrimaryQueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//PrimaryQueryInsertStatement

    private static final class PrimaryChildQueryInsertStatement extends QueryInsertStatement<Insert, ReturningInsert>
            implements Insert, _PostgreInsert._PostgreChildQueryInsert {

        private final QueryInsertStatement<?, ?> parentStatement;

        private PrimaryChildQueryInsertStatement(QueryInsertStatement<?, ?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            assert parentStatement instanceof PrimaryParentQueryInsertStatement
                    || parentStatement instanceof PrimaryParentQueryReturningInsertStatement;
            parentStatement.prepared();
            this.parentStatement = parentStatement;
        }

        @Override
        public _PostgreQueryInsert parentStmt() {
            return this.parentStatement;
        }

    }//PrimaryChildQueryInsertStatement


    private static final class PrimaryParentQueryInsertStatement<P>
            extends QueryInsertStatement<PostgreInsert._ParentInsert<P>, ReturningInsert>
            implements PostgreInsert._ParentInsert<P> {

        private PrimaryParentQueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this
                    , this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentQueryInsertStatement

    private static final class PrimaryQueryReturningInsertStatement
            extends QueryInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert {

        private PrimaryQueryReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//PrimaryQueryReturningInsertStatement

    private static final class PrimaryChildQueryReturningInsertStatement
            extends QueryInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, _PostgreInsert._PostgreChildQueryInsert {

        private final QueryInsertStatement<?, ?> parentStatement;

        private PrimaryChildQueryReturningInsertStatement(QueryInsertStatement<?, ?> parentStatement
                , PostgreComplexValuesClause<?, ?, ?> childClause) {
            super(childClause);
            assert parentStatement instanceof PrimaryParentQueryInsertStatement
                    || parentStatement instanceof PrimaryParentQueryReturningInsertStatement;
            parentStatement.prepared();
            this.parentStatement = parentStatement;
        }

        @Override
        public _PostgreQueryInsert parentStmt() {
            return this.parentStatement;
        }

    }//PrimaryQueryReturningInsertStatement


    private static final class PrimaryParentQueryReturningInsertStatement<P>
            extends QueryInsertStatement<Insert, PostgreInsert._ParentReturnInsert<P>>
            implements PostgreInsert._ParentReturnInsert<P> {

        private PrimaryParentQueryReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this
                    , this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryQueryReturningInsertStatement


    private static final class SubQueryInsertStatement extends QueryInsertStatement<SubInsert, SubReturningInsert>
            implements SubInsert {

        private SubQueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//SubQueryInsertStatement

    private static final class SubQueryReturningInsertStatement
            extends QueryInsertStatement<SubInsert, SubReturningInsert>
            implements SubReturningInsert {

        private SubQueryReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//SubQueryInsertStatement


}
