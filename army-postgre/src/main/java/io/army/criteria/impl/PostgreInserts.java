package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.ReturningInsert;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
import io.army.criteria.impl.inner.postgre._PostgreInsert;
import io.army.criteria.postgre.PostgreCtes;
import io.army.criteria.postgre.PostgreInsert;
import io.army.criteria.postgre.PostgreQuery;
import io.army.dialect.Dialect;
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


    static PostgreInsert._PrimaryOptionSpec singleInsert() {
        return new PrimaryInsertIntoClause();
    }

    static <I extends Item> PostgreInsert._ComplexOptionSpec<I> complexInsert(
            @Nullable _Statement._WithClauseSpec withSpec, Function<PrimaryStatement, I> function) {
        return new ComplexInsertIntoClause<>(withSpec, function);
    }

    static <I extends Item> PostgreInsert._DynamicSubOptionSpec<I> dynamicSubInsert(
            CriteriaContext outContext, Function<SubStatement, I> function) {
        return new DynamicSubInsertIntoClause<>(outContext, function);
    }

    static <I extends Item> PostgreInsert._StaticSubOptionSpec<I> staticSubInsert(CriteriaContext outContext,
                                                                                  Function<SubStatement, I> function) {
        return new StaticSubInsertIntoClause<>(outContext, function);
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


    private static SubStatement subInsertEnd(final PostgreComplexValuesClause<?, ?, ?> clause) {
        final Statement._DmlInsertClause<SubStatement> spec;
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


    /*-------------------below insert after values syntax class-------------------*/


    private static final class PrimaryInsertIntoClause extends NonQueryWithCteOption<
            PostgreInsert._PrimaryNullOptionSpec,
            PostgreInsert._PrimaryPreferLiteralSpec,
            PostgreInsert._PrimaryWithCteSpec,
            PostgreCtes,
            PostgreInsert._PrimaryInsertIntoClause>
            implements PostgreInsert._PrimaryOptionSpec {

        private PrimaryInsertIntoClause() {
            super(CriteriaContexts.primaryInsertContext(null));
            ContextStack.push(this.context);
        }


        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._PrimaryInsertIntoClause> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._PrimaryInsertIntoClause> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
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
        PostgreCtes createCteBuilder(final boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    }//PrimaryInsertIntoClause


    private static final class ChildInsertIntoClause<P> extends ChildDynamicWithClause<
            PostgreCtes,
            PostgreInsert._ChildInsertIntoClause<P>>
            implements PostgreInsert._ChildWithCteSpec<P> {

        private final Function<PostgreComplexValuesClause<?, ?, ?>, Insert> dmlFunction;

        private final Function<PostgreComplexValuesClause<?, ?, ?>, ReturningInsert> dqlFunction;

        private ChildInsertIntoClause(ValueSyntaxOptions parentOption
                , Function<PostgreComplexValuesClause<?, ?, ?>, Insert> dmlFunction
                , Function<PostgreComplexValuesClause<?, ?, ?>, ReturningInsert> dqlFunction) {
            super(parentOption, CriteriaContexts.primaryInsertContext(null));
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
            ContextStack.push(this.context);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._ChildInsertIntoClause<P>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._ChildInsertIntoClause<P>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table) {
            return new PostgreComplexValuesClause<>(this, table, this.dmlFunction, this.dqlFunction);
        }

        @Override
        PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    }//ChildInsertIntoClause

    private static final class ComplexInsertIntoClause<I extends Item> extends NonQueryWithCteOption<
            PostgreInsert._ComplexNullOptionSpec<I>,
            PostgreInsert._ComplexPreferLiteralSpec<I>,
            PostgreInsert._ComplexWithCteSpec<I>,
            PostgreCtes,
            PostgreInsert._ComplexInsertIntoClause<I>>
            implements PostgreInsert._ComplexOptionSpec<I> {

        private final Function<PrimaryStatement, I> function;

        private ComplexInsertIntoClause(@Nullable _Statement._WithClauseSpec withSpec,
                                        Function<PrimaryStatement, I> function) {
            super(CriteriaContexts.primaryInsertContext(withSpec));
            this.function = function;
            ContextStack.push(this.context)
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._ComplexInsertIntoClause<I>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._ComplexInsertIntoClause<I>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, I, I> insertInto(TableMeta<T> table) {
            return new PostgreComplexValuesClause<>(this, table, this.function.compose(PostgreInserts::insertEnd),
                    this.function.compose(PostgreInserts::returningInsertEnd));
        }

        @Override
        PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }

    }//ComplexInsertIntoClause


    private static final class DynamicSubInsertIntoClause<I extends Item>
            extends NonQueryWithCteOption<
            PostgreInsert._DynamicSubNullOptionSpec<I>,
            PostgreInsert._DynamicSubPreferLiteralSpec<I>,
            PostgreInsert._DynamicSubWithSpec<I>,
            PostgreCtes,
            PostgreInsert._CteInsertIntoClause<I>>
            implements PostgreInsert._DynamicSubOptionSpec<I> {

        private final Function<PostgreComplexValuesClause<?, ?, ?>, I> function;

        private DynamicSubInsertIntoClause(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(CriteriaContexts.cteInsertContext(outerContext));
            this.function = function.compose(PostgreInserts::subInsertEnd);
            //just push sub context,here don't need to start cte
            ContextStack.push(this.context);

        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._CteInsertIntoClause<I>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<PostgreInsert._CteInsertIntoClause<I>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, I, I> insertInto(TableMeta<T> table) {
            return new PostgreComplexValuesClause<>(this, table, this.function, this.function);
        }


        @Override
        PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }

    }//DynamicSubInsertIntoClause

    private static final class StaticSubInsertIntoClause<I extends Item> extends NonQueryWithCteOption<
            PostgreInsert._StaticSubNullOptionSpec<I>,
            PostgreInsert._StaticSubPreferLiteralSpec<I>,
            PostgreInsert._CteInsertIntoClause<I>,
            PostgreCtes,
            PostgreInsert._CteInsertIntoClause<I>>
            implements PostgreInsert._StaticSubOptionSpec<I> {

        private final Function<PostgreComplexValuesClause<?, ?, ?>, I> function;

        private StaticSubInsertIntoClause(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(CriteriaContexts.cteInsertContext(outerContext));
            this.function = function.compose(PostgreInserts::subInsertEnd);
            //just push sub context,here don't need to start cte
            ContextStack.push(this.context);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, I, I> insertInto(TableMeta<T> table) {
            return new PostgreComplexValuesClause<>(this, table, this.function, this.function);
        }

        @Override
        PostgreCtes createCteBuilder(boolean recursive) {
            throw ContextStack.castCriteriaApi(this.context);
        }


    }//StaticSubInsertIntoClause


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
            extends SetWhereClause.SetWhereClauseClause<
            FieldMeta<T>,
            PostgreInsert._DoUpdateWhereSpec<T, I, Q>,
            PostgreInsert._ReturningSpec<I, Q>,
            PostgreInsert._DoUpdateWhereAndSpec<I, Q>>
            implements PostgreInsert._DoUpdateWhereSpec<T, I, Q>
            , PostgreInsert._DoUpdateWhereAndSpec<I, Q> {

        private final OnConflictClause<T, I, Q> onConflictClause;

        private ConflictDoUpdateActionClause(OnConflictClause<T, I, Q> clause) {
            super(clause.valuesClause.context, clause.valuesClause.insertTable, clause.safeTableAlias);
            this.onConflictClause = clause;
        }

        @Override
        public PostgreInsert._DoUpdateWhereClause<I, Q> sets(Consumer<RowPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.rowPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Selection selection) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(selection);
        }

        @Override
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Expression expression, SQLsSyntax.WordAs wordAs
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
        public Statement._DqlInsertClause<Q> returning(Consumer<Returnings> consumer) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(consumer);
        }

        @Override
        public I asInsert() {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .asInsert();
        }


    }//ConflictDoUpdateActionClause


    private static final class OnConflictClause<T, I extends Item, Q extends Item>
            extends WhereClause.WhereClauseClause<
            PostgreInsert._ConflictActionClause<T, I, Q>,
            PostgreInsert._ConflictTargetWhereAndSpec<T, I, Q>>
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

        private final String rowAlias;
        private final List<_ConflictTargetItem> targetItemList;

        private final List<_Predicate> indexPredicateList;

        private final String constraintName;

        private final boolean doNothing;

        private final List<_ItemPair> itemPairList;

        private final List<_Predicate> updatePredicateList;

        private ConflictActionClauseResult(OnConflictClause<?, ?, ?> clause) {
            this.rowAlias = clause.valuesClause.tableAlias;
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
            this.rowAlias = clause.valuesClause.tableAlias;
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
        public boolean hasConflictAction() {
            return true;
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

        @Override
        public String rowAlias() {
            return this.rowAlias;
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
        private PostgreComplexValuesClause(WithValueSyntaxOptions options, TableMeta<T> table,
                                           Function<PostgreComplexValuesClause<?, ?, ?>, I> dmlFunction,
                                           Function<PostgreComplexValuesClause<?, ?, ?>, Q> dqlFunction) {
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
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Expression expression, SQLsSyntax.WordAs wordAs
                , String alias) {
            this.onAddSelection(ArmySelections.forExp((ArmyExpression) expression, alias));
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
        public PostgreInsert._StaticReturningCommaSpec<Q> comma(Expression expression, SQLsSyntax.WordAs wordAs
                , String alias) {
            return this.comma(ArmySelections.forExp((ArmyExpression) expression, alias));
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
        public Statement._DqlInsertClause<Q> returning(Consumer<Returnings> consumer) {
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
        public PostgreInsert._StaticReturningCommaSpec<Q> returning(Expression expression, SQLsSyntax.WordAs wordAs
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
        public Statement._DqlInsertClause<Q> returning(Consumer<Returnings> consumer) {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .returning(consumer);
        }

        @Override
        public I asInsert() {
            return this.clause.staticValuesClauseEnd(this.endValuesClause())
                    .asInsert();
        }


    }//StaticValuesLeftParenClause


    private static abstract class PostgreValueSyntaxInsertStatement<I extends Statement, Q extends Statement>
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
        final Dialect statementDialect() {
            return PostgreDialect.POSTGRE15;
        }


    }//PrimaryValueSyntaxInsertStatement


    static abstract class DomainInsertStatement<I extends Statement, Q extends Statement>
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
            this.domainList = clause.domainListForSingle();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

    }//PrimaryDomainInsertStatement

    private static final class PrimaryChildDomainInsertStatement
            extends DomainInsertStatement<InsertStatement, ReturningInsert>
            implements InsertStatement, _PostgreInsert._PostgreChildDomainInsert {

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

        private InsertStatement childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
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
            extends DomainInsertStatement<InsertStatement, ReturningInsert>
            implements ReturningInsert {

        private final List<?> domainList;

        private PrimaryDomainReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            this.domainList = clause.domainListForSingle();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }


    }//PrimaryDomainReturningInsertStatement


    private static final class PrimaryChildDomainReturningInsertStatement
            extends DomainInsertStatement<InsertStatement, ReturningInsert>
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
            extends DomainInsertStatement<InsertStatement, PostgreInsert._ParentReturnInsert<P>>
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

        private InsertStatement childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
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


    private static final class SubDomainInsertStatement extends DomainInsertStatement<SubStatement, SubStatement>
            implements SubStatement {

        private final List<?> domainList;

        private SubDomainInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            if (clause.insertTable instanceof ParentTableMeta) {
                this.domainList = _CollectionUtils.asUnmodifiableList(clause.originalDomainList());
            } else {
                this.domainList = clause.domainListForSingle();
            }
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

    }//SubDomainInsertStatement

    private static final class SubDomainReturningInsertStatement
            extends DomainInsertStatement<SubStatement, SubStatement>
            implements SubStatement {

        private final List<?> domainList;

        private SubDomainReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
            if (clause.insertTable instanceof ParentTableMeta) {
                this.domainList = _CollectionUtils.asUnmodifiableList(clause.originalDomainList());
            } else {
                this.domainList = clause.domainListForSingle();
            }
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

    }//SubDomainReturningInsertStatement


    static abstract class ValueInsertStatement<I extends Statement, Q extends Statement>
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
            implements InsertStatement {

        private PrimaryValueInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

    }//PrimaryValueInsertStatement


    private static final class PrimaryChildValueInsertStatement
            extends ValueInsertStatement<InsertStatement, ReturningInsert>
            implements _PostgreInsert._PostgreChildValueInsert
            , InsertStatement {

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

        private InsertStatement childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
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
            extends ValueInsertStatement<InsertStatement, ReturningInsert>
            implements ReturningInsert {

        private PrimaryValueReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//PrimaryValueReturningInsertStatement

    private static final class PrimaryChildValueReturningInsertStatement
            extends ValueInsertStatement<InsertStatement, ReturningInsert>
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
            extends ValueInsertStatement<InsertStatement, PostgreInsert._ParentReturnInsert<P>>
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

        private InsertStatement childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
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
            extends ValueInsertStatement<SubStatement, SubStatement>
            implements SubStatement {

        private SubValueInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//SubValueInsertStatement


    private static final class SubValueReturningInsertStatement
            extends ValueInsertStatement<SubStatement, SubStatement>
            implements SubStatement {

        private SubValueReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }

    }//SubValueReturningInsertStatement


    static abstract class QueryInsertStatement<I extends Statement, Q extends Statement>
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
        final Dialect statementDialect() {
            return PostgreDialect.POSTGRE15;
        }


    }//QueryInsertStatement


    private static final class PrimaryQueryInsertStatement extends QueryInsertStatement<Insert, ReturningInsert>
            implements InsertStatement {

        private PrimaryQueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//PrimaryQueryInsertStatement

    private static final class PrimaryChildQueryInsertStatement extends QueryInsertStatement<InsertStatement, ReturningInsert>
            implements InsertStatement, _PostgreInsert._PostgreChildQueryInsert {

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

        private InsertStatement childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentQueryInsertStatement

    private static final class PrimaryQueryReturningInsertStatement
            extends QueryInsertStatement<InsertStatement, ReturningInsert>
            implements ReturningInsert {

        private PrimaryQueryReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//PrimaryQueryReturningInsertStatement

    private static final class PrimaryChildQueryReturningInsertStatement
            extends QueryInsertStatement<InsertStatement, ReturningInsert>
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
            extends QueryInsertStatement<InsertStatement, PostgreInsert._ParentReturnInsert<P>>
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

        private InsertStatement childInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexValuesClause<?, ?, ?> childClause) {
            return new PrimaryChildQueryReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryQueryReturningInsertStatement


    private static final class SubQueryInsertStatement extends QueryInsertStatement<SubStatement, SubStatement>
            implements SubStatement {

        private SubQueryInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//SubQueryInsertStatement

    private static final class SubQueryReturningInsertStatement
            extends QueryInsertStatement<SubStatement, SubStatement>
            implements SubStatement {

        private SubQueryReturningInsertStatement(PostgreComplexValuesClause<?, ?, ?> clause) {
            super(clause);
        }


    }//SubQueryInsertStatement


}
