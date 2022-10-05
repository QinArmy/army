package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
import io.army.criteria.impl.inner.postgre._PostgreInsert;
import io.army.criteria.postgre.PostgreCteBuilder;
import io.army.criteria.postgre.PostgreInsert;
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
import java.util.function.*;

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


    static <C> PostgreInsert._PrimaryOptionSpec<C> primaryInsert(@Nullable C criteria) {
        return new PrimaryInsertIntoClause<>(criteria);
    }

    static <C, I extends Item> PostgreInsert._DynamicSubInsert<C, I> dynamicSubInsert(final String name, CriteriaContext outContext, @Nullable C criteria, Supplier<I> endSuppler) {
        return new DynamicSubInsertIntoClause<>(name, outContext, criteria, endSuppler);
    }

    static <C, I extends Item, Q extends Item> PostgreInsert._StaticSubOptionSpec<C, I, Q> staticSubInsert(CriteriaContext outContext, @Nullable C criteria
            , Function<SubInsert, I> dmlFunc, Function<SubReturningInsert, Q> dqlFunc) {
        return new StaticSubInsertIntoClause<>(outContext, criteria, dmlFunc, dqlFunc);
    }

    /*-------------------below private method -------------------*/

    private static SubInsert createSubInsert(final PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
        final Statement._DmlInsertSpec<SubInsert> spec;
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

    private static SubReturningInsert createSubReturningInsert(final PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
        final DqlInsert._DqlInsertSpec<SubReturningInsert> spec;
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


    private static final class ConflictTargetItem<C, T, I extends Item, Q extends Item>
            implements PostgreInsert._ConflictCollateSpec<C, T, I, Q>
            , _ConflictTargetItem {

        private final OnConflictClause<C, T, I, Q> clause;

        private final IndexFieldMeta<T> indexColumn;

        private String collationName;

        private Boolean opclass;

        private ConflictTargetItem(OnConflictClause<C, T, I, Q> clause, IndexFieldMeta<T> indexColumn) {
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
        public PostgreInsert._ConflictOpClassSpec<C, T, I, Q> collation(final @Nullable String collationName) {
            if (collationName == null) {
                throw ContextStack.nullPointer(this.clause.valuesClause.context);
            } else if (this.collationName != null || this.opclass != null) {
                throw ContextStack.castCriteriaApi(this.clause.valuesClause.context);
            }
            this.collationName = collationName;
            return this;
        }

        @Override
        public PostgreInsert._ConflictOpClassSpec<C, T, I, Q> collation(Supplier<String> supplier) {
            return this.collation(supplier.get());
        }

        @Override
        public PostgreInsert._ConflictOpClassSpec<C, T, I, Q> ifCollation(Supplier<String> supplier) {
            final String collation;
            collation = supplier.get();
            if (collation != null) {
                this.collation(collationName);
            }
            return this;
        }

        @Override
        public PostgreInsert._ConflictTargetCommaSpec<C, T, I, Q> opClass() {
            if (this.opclass != null) {
                throw ContextStack.castCriteriaApi(this.clause.valuesClause.context);
            }
            this.opclass = Boolean.TRUE;
            return this;
        }

        @Override
        public PostgreInsert._ConflictTargetCommaSpec<C, T, I, Q> ifOpClass(BooleanSupplier supplier) {
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
        public PostgreInsert._ConflictCollateSpec<C, T, I, Q> comma(IndexFieldMeta<T> indexColumn) {
            if (this.opclass == null) {
                this.opclass = Boolean.FALSE;
            }
            return this.clause.leftParen(indexColumn); // create and add
        }

        @Override
        public PostgreInsert._ConflictTargetWhereSpec<C, T, I, Q> rightParen() {
            if (this.opclass == null) {
                this.opclass = Boolean.FALSE;
            }
            return this.clause.targetItemClauseEnd();
        }


    }//ConflictTargetItem


    private static final class ConflictDoUpdateActionClause<C, T, I extends Item, Q extends Item>
            extends InsertSupport.ConflictUpdateWhereClause<
            C,
            T,
            PostgreInsert._DoUpdateWhereSpec<C, T, I, Q>,
            PostgreInsert._ReturningSpec<C, I, Q>,
            PostgreInsert._DoUpdateWhereAndSpec<C, I, Q>>
            implements PostgreInsert._DoUpdateWhereSpec<C, T, I, Q>
            , PostgreInsert._DoUpdateWhereAndSpec<C, I, Q> {

        private final OnConflictClause<C, T, I, Q> onConflictClause;

        private ConflictDoUpdateActionClause(OnConflictClause<C, T, I, Q> onConflictClause) {
            super(onConflictClause.valuesClause.context, onConflictClause.valuesClause.insertTable);
            this.onConflictClause = onConflictClause;
        }


        @Override
        public DialectStatement._StaticReturningCommaUnaryClause<Q> returning(Selection selectItem) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(selectItem);
        }

        @Override
        public DialectStatement._StaticReturningCommaDualClause<Q> returning(Selection selectItem1, Selection selectItem2) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(selectItem1, selectItem2);
        }

        @Override
        public DqlInsert._DqlInsertSpec<Q> returningAll() {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returningAll();
        }

        @Override
        public DqlInsert._DqlInsertSpec<Q> returning(Consumer<Consumer<Selection>> consumer) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(consumer);
        }

        @Override
        public DqlInsert._DqlInsertSpec<Q> returning(BiConsumer<C, Consumer<Selection>> consumer) {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .returning(consumer);
        }

        @Override
        public I asInsert() {
            return this.onConflictClause.updateActionClauseEnd(this.endUpdateSetClause(), this.endWhereClause())
                    .asInsert();
        }

        @Override
        Dialect syntaxDialect() {
            return PostgreDialect.POSTGRE14;
        }


    }//ConflictDoUpdateActionClause


    private static final class OnConflictClause<C, T, I extends Item, Q extends Item>
            extends InsertSupport.MinWhereClause<C, PostgreInsert._ConflictActionClause<C, T, I, Q>, PostgreInsert._ConflictTargetWhereAndSpec<C, T, I, Q>>
            implements PostgreInsert._ConflictTargetOptionSpec<C, T, I, Q>
            , PostgreInsert._ConflictTargetWhereSpec<C, T, I, Q>
            , PostgreInsert._ConflictTargetWhereAndSpec<C, T, I, Q> {

        private final PostgreComplexInsertValuesClause<C, T, I, Q> valuesClause;

        private List<_ConflictTargetItem> targetItemList;

        private String constraintName;

        private boolean doNothing;

        private OnConflictClause(PostgreComplexInsertValuesClause<C, T, I, Q> valuesClause) {
            super(valuesClause.context);
            this.valuesClause = valuesClause;
        }

        @Override
        public PostgreInsert._ConflictCollateSpec<C, T, I, Q> leftParen(IndexFieldMeta<T> indexColumn) {
            List<_ConflictTargetItem> targetItemList = this.targetItemList;
            if (targetItemList == null) {
                targetItemList = new ArrayList<>();
                this.targetItemList = targetItemList;
            } else if (!(targetItemList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.valuesClause.context);
            }
            final ConflictTargetItem<C, T, I, Q> item = new ConflictTargetItem<>(this, indexColumn);
            targetItemList.add(item);
            return item;
        }

        @Override
        public PostgreInsert._ConflictActionClause<C, T, I, Q> onConstraint(final @Nullable String constraintName) {
            if (this.constraintName != null) {
                throw ContextStack.castCriteriaApi(this.valuesClause.context);
            } else if (constraintName == null) {
                throw ContextStack.nullPointer(this.valuesClause.context);
            }
            this.constraintName = constraintName;
            return this;
        }

        @Override
        public PostgreInsert._ReturningSpec<C, I, Q> doNothing() {
            this.endWhereClause();
            this.doNothing = true;
            return this.valuesClause.conflictClauseEnd(new ConflictActionClauseResult(this));
        }

        @Override
        public PostgreInsert._DoUpdateSetClause<C, T, I, Q> doUpdate() {
            this.endWhereClause();
            return new ConflictDoUpdateActionClause<>(this);
        }

        private PostgreInsert._ConflictTargetWhereSpec<C, T, I, Q> targetItemClauseEnd() {
            final List<_ConflictTargetItem> targetItemList = this.targetItemList;
            if (targetItemList instanceof ArrayList) {
                this.targetItemList = Collections.unmodifiableList(targetItemList);
            } else {
                throw ContextStack.castCriteriaApi(this.valuesClause.context);
            }
            return this;
        }

        private PostgreInsert._ReturningSpec<C, I, Q> updateActionClauseEnd(List<ItemPair> itemPairList
                , List<_Predicate> predicateList) {
            return this.valuesClause
                    .conflictClauseEnd(new ConflictActionClauseResult(this, itemPairList, predicateList));
        }


    }//OnConflictClause


    private static final class ConflictActionClauseResult implements _PostgreInsert._ConflictActionClauseResult {

        private final List<_ConflictTargetItem> targetItemList;

        private final List<_Predicate> indexPredicateList;

        private final String constraintName;

        private final boolean doNothing;

        private final List<ItemPair> itemPairList;

        private final List<_Predicate> updatePredicateList;

        private ConflictActionClauseResult(OnConflictClause<?, ?, ?, ?> clause) {
            this.targetItemList = _CollectionUtils.safeList(clause.targetItemList);
            if (this.targetItemList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(clause.valuesClause.context);
            }
            this.indexPredicateList = clause.predicateList();
            this.constraintName = clause.constraintName;
            this.doNothing = clause.doNothing;

            this.itemPairList = Collections.emptyList();
            this.updatePredicateList = Collections.emptyList();
        }

        private ConflictActionClauseResult(OnConflictClause<?, ?, ?, ?> clause, List<ItemPair> itemPairList
                , List<_Predicate> updatePredicateList) {
            this.targetItemList = _CollectionUtils.safeList(clause.targetItemList);
            if (this.targetItemList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(clause.valuesClause.context);
            }
            this.indexPredicateList = clause.predicateList();
            this.constraintName = clause.constraintName;
            this.doNothing = clause.doNothing;

            this.itemPairList = itemPairList;
            this.updatePredicateList = updatePredicateList;
        }

        @Override
        public List<ItemPair> updateSetClauseList() {
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




    /*-------------------below insert after values syntax class-------------------*/


    private static final class ParentCteCommaClause<C> implements PostgreInsert._ParentCteComma<C> {

        private final boolean recursive;

        private final PrimaryInsertIntoClause<C> primaryClause;

        private final StaticCteComplexCommandClause<C, PostgreInsert._ParentCteComma<C>, PostgreInsert._ParentCteComma<C>> commandClause;

        private ParentCteCommaClause(boolean recursive, String name, PrimaryInsertIntoClause<C> primaryClause) {
            this.recursive = recursive;
            this.primaryClause = primaryClause;
            this.commandClause = new StaticCteComplexCommandClause<>(name, primaryClause.context, this, this);
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._ParentCteComma<C>, PostgreInsert._ParentCteComma<C>> comma(final @Nullable String name) {
            final StaticCteComplexCommandClause<C, PostgreInsert._ParentCteComma<C>, PostgreInsert._ParentCteComma<C>> commandClause;
            commandClause = this.commandClause;
            if (name == null) {
                throw ContextStack.nullPointer(commandClause.context);
            }
            if (commandClause.name != null) {
                throw ContextStack.castCriteriaApi(commandClause.context);
            }
            commandClause.context.onStartCte(name);
            commandClause.name = name;
            commandClause.columnAliasList = null;
            return commandClause;
        }

        @Override
        public PostgreInsert._PrimaryInsertIntoClause<C> space() {
            final PrimaryInsertIntoClause<C> primaryClause = this.primaryClause;
            primaryClause.endStaticWithClause(this.recursive);
            return primaryClause;
        }

    }//StaticParentCteCommaClause


    private static final class StaticChildCteCommaClause<C, P> implements PostgreInsert._ChildCteComma<C, P> {

        private final boolean recursive;
        private final ChildInsertIntoClause<C, P> primaryClause;

        private final StaticCteComplexCommandClause<C, PostgreInsert._ChildCteComma<C, P>, PostgreInsert._ChildCteComma<C, P>> commandClause;

        private StaticChildCteCommaClause(boolean recursive, String name, ChildInsertIntoClause<C, P> primaryClause) {
            this.recursive = recursive;
            this.primaryClause = primaryClause;
            this.commandClause = new StaticCteComplexCommandClause<>(name, primaryClause.context, this, this);
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._ChildCteComma<C, P>, PostgreInsert._ChildCteComma<C, P>> comma(final @Nullable String name) {
            final StaticCteComplexCommandClause<C, PostgreInsert._ChildCteComma<C, P>, PostgreInsert._ChildCteComma<C, P>> complexClause;
            complexClause = this.commandClause;
            if (complexClause.name != null) {
                //last cte don't end
                throw ContextStack.castCriteriaApi(complexClause.context);
            }
            if (name == null) {
                throw ContextStack.nullPointer(complexClause.context);
            }
            complexClause.context.onStartCte(name);
            complexClause.name = name;
            complexClause.columnAliasList = null;
            return complexClause;
        }

        @Override
        public PostgreInsert._ChildInsertIntoClause<C, P> space() {
            final ChildInsertIntoClause<C, P> primaryClause = this.primaryClause;
            primaryClause.endStaticWithClause(this.recursive);
            return primaryClause;
        }


    }//StaticParentCteCommaClause


    private static final class StaticCteComplexCommandClause<C, I extends Item, Q extends Item>
            extends CriteriaSupports.ParenStringConsumerClause<C, PostgreInsert._StaticCteAsClause<C, I, Q>>
            implements PostgreInsert._StaticCteLeftParenSpec<C, I, Q>
            , PostgreInsert._StaticSubComplexCommandSpec<C, I, Q> {

        private final I commaClause;

        private final Q commaQueryClause;

        private String name;

        private List<String> columnAliasList;


        private StaticCteComplexCommandClause(final String name, CriteriaContext primaryContext
                , I commaClause, Q commaQueryClause) {
            super(primaryContext);
            this.commaClause = commaClause;
            this.commaQueryClause = commaQueryClause;
            this.name = name;

        }

        @Override
        public PostgreInsert._StaticSubComplexCommandSpec<C, I, Q> as() {
            return this;
        }

        @Override
        public PostgreInsert._SubInsertIntoClause<C, I, Q> literalMode(LiteralMode mode) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::cteInsertEnd, this::cteReturnInsertEnd)
                    .literalMode(mode);
        }

        @Override
        public PostgreInsert._StaticSubNullOptionSpec<C, I, Q> migration(boolean migration) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::cteInsertEnd, this::cteReturnInsertEnd)
                    .migration(migration);
        }

        @Override
        public PostgreInsert._StaticSubPreferLiteralSpec<C, I, Q> nullHandle(NullHandleMode mode) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::cteInsertEnd, this::cteReturnInsertEnd)
                    .nullHandle(mode);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, I, Q> insertInto(TableMeta<T> table) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::cteInsertEnd, this::cteReturnInsertEnd)
                    .insertInto(table);
        }


        @Override
        PostgreInsert._StaticCteAsClause<C, I, Q> stringConsumerEnd(final List<String> stringList) {
            if (this.columnAliasList != null || stringList.size() == 0) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = stringList;
            this.context.onCteColumnAlias(this.name, stringList);
            return this;
        }


        private I cteInsertEnd(final SubInsert insert) {
            CriteriaUtils.createAndAddCte(this.context, this.name, this.columnAliasList, insert);
            //clear for next cte
            this.name = null;
            this.columnAliasList = null;
            return this.commaClause;
        }

        private Q cteReturnInsertEnd(final SubReturningInsert insert) {
            CriteriaUtils.createAndAddCte(this.context, this.name, this.columnAliasList, insert);
            //clear for next cte
            this.name = null;
            this.columnAliasList = null;
            return this.commaQueryClause;
        }


    }//StaticCteComplexCommandClause

    private static final class PrimaryInsertIntoClause<C> extends NonQueryWithCteOption<
            C,
            PostgreInsert._PrimaryNullOptionSpec<C>,
            PostgreInsert._PrimaryPreferLiteralSpec<C>,
            PostgreInsert._PrimaryWithCteSpec<C>,
            PostgreCteBuilder,
            PostgreInsert._PrimaryInsertIntoClause<C>>
            implements PostgreInsert._PrimaryOptionSpec<C> {

        private PrimaryInsertIntoClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            ContextStack.setContextStack(this.context);
        }


        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._ParentCteComma<C>, PostgreInsert._ParentCteComma<C>> with(final @Nullable String name) {
            final CriteriaContext context = this.context;
            if (name == null) {
                throw ContextStack.nullPointer(context);
            }
            final boolean recursive = false;
            context.onBeforeWithClause(recursive);
            context.onStartCte(name);
            return new ParentCteCommaClause<>(recursive, name, this).commandClause;
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._ParentCteComma<C>, PostgreInsert._ParentCteComma<C>> withRecursive(final @Nullable String name) {
            final CriteriaContext context = this.context;
            if (name == null) {
                throw ContextStack.nullPointer(context);
            }
            final boolean recursive = true;
            context.onBeforeWithClause(recursive);
            context.onStartCte(name);
            return new ParentCteCommaClause<>(recursive, name, this).commandClause;
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table) {
            return new PostgreComplexInsertValuesClause<>(this, table, this::insertEnd, this::returningInsertEnd);
        }

        @Override
        public <P> PostgreInsert._TableAliasSpec<C, P, PostgreInsert._ParentInsert<C, P>, PostgreInsert._ParentReturnInsert<C, P>> insertInto(ParentTableMeta<P> table) {
            return new PostgreComplexInsertValuesClause<>(this, table, this::parentInsertEnd, this::parentReturningInsertEnd);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ChildTableMeta<T> table) {
            return new PostgreComplexInsertValuesClause<>(this, table, this::insertEnd, this::returningInsertEnd);
        }

        @Override
        PostgreCteBuilder createCteBuilder(final boolean recursive) {
            return PostgreSupports.cteBuilder(recursive, this.context);
        }

        private <P> PostgreInsert._ParentInsert<C, P> parentInsertEnd(final PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            final Statement._DmlInsertSpec<PostgreInsert._ParentInsert<C, P>> spec;
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

        private <P> PostgreInsert._ParentReturnInsert<C, P> parentReturningInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            final DqlInsert._DqlInsertSpec<PostgreInsert._ParentReturnInsert<C, P>> spec;
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

        private Insert insertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            final Statement._DmlInsertSpec<Insert> spec;
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

        private ReturningInsert returningInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            final DqlInsert._DqlInsertSpec<ReturningInsert> spec;
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


    }//PrimaryInsertIntoClause

    private static final class ChildInsertIntoClause<C, P> extends ChildDynamicWithClause<
            C,
            PostgreCteBuilder,
            PostgreInsert._ChildInsertIntoClause<C, P>>
            implements PostgreInsert._ChildWithCteSpec<C, P> {

        private final Function<PostgreComplexInsertValuesClause<?, ?, ?, ?>, Insert> dmlFunction;

        private final Function<PostgreComplexInsertValuesClause<?, ?, ?, ?>, ReturningInsert> dqlFunction;

        private ChildInsertIntoClause(@Nullable C criteria, ValueSyntaxOptions parentOption
                , Function<PostgreComplexInsertValuesClause<?, ?, ?, ?>, Insert> dmlFunction
                , Function<PostgreComplexInsertValuesClause<?, ?, ?, ?>, ReturningInsert> dqlFunction) {
            super(parentOption, CriteriaContexts.primaryInsertContext(criteria));
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
            ContextStack.setContextStack(this.context);
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._ChildCteComma<C, P>, PostgreInsert._ChildCteComma<C, P>> with(final @Nullable String name) {
            final CriteriaContext context = this.context;
            if (name == null) {
                throw ContextStack.nullPointer(context);
            }
            final boolean recursive = false;
            context.onBeforeWithClause(recursive);
            context.onStartCte(name);
            return new StaticChildCteCommaClause<>(recursive, name, this).commandClause;
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C, PostgreInsert._ChildCteComma<C, P>, PostgreInsert._ChildCteComma<C, P>> withRecursive(final @Nullable String name) {
            final CriteriaContext context = this.context;
            if (name == null) {
                throw ContextStack.nullPointer(context);
            }
            final boolean recursive = true;
            context.onBeforeWithClause(recursive);
            context.onStartCte(name);
            return new StaticChildCteCommaClause<>(recursive, name, this).commandClause;
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ComplexTableMeta<P, T> table) {
            return new PostgreComplexInsertValuesClause<>(this, table, this.dmlFunction, this.dqlFunction);
        }

        @Override
        PostgreCteBuilder createCteBuilder(boolean recursive) {
            return PostgreSupports.cteBuilder(recursive, this.context);
        }


    }//ChildInsertIntoClause


    private static final class StaticSubInsertIntoClause<C, I extends Item, Q extends Item>
            extends NonQueryInsertOptionsImpl<
            PostgreInsert._StaticSubNullOptionSpec<C, I, Q>,
            PostgreInsert._StaticSubPreferLiteralSpec<C, I, Q>,
            PostgreInsert._SubInsertIntoClause<C, I, Q>>
            implements PostgreInsert._StaticSubOptionSpec<C, I, Q>
            , WithValueSyntaxOptions {


        private final Function<SubInsert, I> dmlFunction;

        private final Function<SubReturningInsert, Q> dqlFunction;

        private StaticSubInsertIntoClause(CriteriaContext outerContext, @Nullable C criteria
                , Function<SubInsert, I> dmlFunction, Function<SubReturningInsert, Q> dqlFunction) {
            super(CriteriaContexts.cteInsertContext(outerContext, criteria));
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
            //just push sub context,here don't need to start cte
            ContextStack.push(this.context);

        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, I, Q> insertInto(TableMeta<T> table) {
            return new PostgreComplexInsertValuesClause<>(this, table, this::subInsertEnd, this::subReturningInsertEnd);
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

        private I subInsertEnd(final PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            return this.dmlFunction.apply(createSubInsert(clause));
        }

        private Q subReturningInsertEnd(final PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            return this.dqlFunction.apply(createSubReturningInsert(clause));
        }

    }//StaticSubInsertIntoClause


    private static final class DynamicSubInsertIntoClause<C, I extends Item>
            extends NonQueryWithCteOption<
            C,
            PostgreInsert._DynamicSubNullOptionSpec<C, I, I>,
            PostgreInsert._DynamicSubPreferLiteralSpec<C, I, I>,
            PostgreInsert._DynamicSubWithCteSpec<C, I, I>,
            PostgreCteBuilder,
            PostgreInsert._SubInsertIntoClause<C, I, I>>
            implements PostgreInsert._DynamicSubOptionSpec<C, I, I>
            , Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, I, I>>
            , PostgreInsert._DynamicSubInsert<C, I> {


        private final C criteria;

        final String name;

        private final Supplier<I> endSuppler;

        private List<String> columnAliasList;

        private DynamicSubInsertIntoClause(final String name, final CriteriaContext outContext, final @Nullable C criteria, Supplier<I> endSuppler) {
            super(CriteriaContexts.cteInsertContext(outContext, criteria));
            this.criteria = criteria;
            this.name = name;
            //firstly,onStartCte
            outContext.onStartCte(name);
            //secondly,push context
            ContextStack.push(this.context);
            this.endSuppler = endSuppler;

        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, I, I>> leftParen(String string) {
            if (this.columnAliasList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = Collections.singletonList(string);
            return this;
        }

        @Override
        public Statement._CommaStringDualSpec<PostgreInsert._DynamicSubOptionSpec<C, I, I>> leftParen(String string1, String string2) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(string1, string2);
        }

        @Override
        public Statement._CommaStringQuadraSpec<PostgreInsert._DynamicSubOptionSpec<C, I, I>> leftParen(String string1, String string2, String string3, String string4) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(string1, string2, string3, string4);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, I, I>> leftParen(Consumer<Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(consumer);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, I, I>> leftParen(BiConsumer<C, Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(consumer);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, I, I>> leftParenIf(Consumer<Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParenIf(consumer);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, I, I>> leftParenIf(BiConsumer<C, Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParenIf(consumer);
        }

        @Override
        public PostgreInsert._DynamicSubOptionSpec<C, I, I> rightParen() {
            return this;
        }


        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, I, I> insertInto(TableMeta<T> table) {
            return new PostgreComplexInsertValuesClause<>(this, table, this::subInsertEnd, this::subReturnInsertEnd);
        }


        @Override
        PostgreCteBuilder createCteBuilder(final boolean recursive) {
            return PostgreSupports.cteBuilder(recursive, this.context);
        }


        private PostgreInsert._DynamicSubOptionSpec<C, I, I> columnAliasClauseEnd(final List<String> aliasList) {
            if (this.columnAliasList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final CriteriaContext outerContext;
            outerContext = this.context.getOuterContext();
            assert outerContext != null;
            outerContext.onCteColumnAlias(this.name, aliasList);
            this.columnAliasList = aliasList;
            return this;
        }

        private I subInsertEnd(final PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            final SubInsert subInsert;
            subInsert = createSubInsert(clause);
            final CriteriaContext outerContext = this.context.getOuterContext();
            assert outerContext != null;
            CriteriaUtils.createAndAddCte(outerContext, this.name, this.columnAliasList, subInsert);
            return this.endSuppler.get();
        }

        private I subReturnInsertEnd(final PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            final SubReturningInsert subReturningInsert;
            subReturningInsert = createSubReturningInsert(clause);

            final CriteriaContext outerContext = this.context.getOuterContext();
            assert outerContext != null;
            CriteriaUtils.createAndAddCte(outerContext, this.name, this.columnAliasList, subReturningInsert);
            return this.endSuppler.get();
        }


    }//DynamicSubInsertIntoClause


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


    private static final class PostgreComplexInsertValuesClause<C, T, I extends Item, Q extends Item>
            extends ComplexInsertValuesClause<
            C,
            T,
            PostgreInsert._ComplexOverridingValueSpec<C, T, I, Q>,
            PostgreInsert._ValuesDefaultSpec<C, T, I, Q>,
            PostgreInsert._OnConflictSpec<C, T, I, Q>>
            implements PostgreInsert._TableAliasSpec<C, T, I, Q>
            , PostgreInsert._OnConflictSpec<C, T, I, Q>
            , DialectStatement._StaticReturningCommaUnaryClause<Q>
            , DialectStatement._StaticReturningCommaDualClause<Q> {

        private static final List<SelectItem> RETURNING_ALL = Collections.emptyList();

        private final Function<PostgreComplexInsertValuesClause<?, ?, ?, ?>, I> dmlFunction;

        private final Function<PostgreComplexInsertValuesClause<?, ?, ?, ?>, Q> dqlFunction;

        private final boolean recursive;

        private final List<_Cte> cteList;

        private String tableAlias;

        private OverridingMode overridingMode;

        private _PostgreInsert._ConflictActionClauseResult conflictAction;

        private List<SelectItem> selectItemList;


        /**
         * @see PrimaryInsertIntoClause#insertInto(SimpleTableMeta)
         * @see PrimaryInsertIntoClause#insertInto(ParentTableMeta)
         * @see PrimaryInsertIntoClause#insertInto(ChildTableMeta)
         * @see ChildInsertIntoClause#insertInto(ComplexTableMeta)
         */
        private PostgreComplexInsertValuesClause(WithValueSyntaxOptions options, TableMeta<T> table
                , Function<PostgreComplexInsertValuesClause<?, ?, ?, ?>, I> dmlFunction
                , Function<PostgreComplexInsertValuesClause<?, ?, ?, ?>, Q> dqlFunction) {
            super(options, table);
            this.recursive = options.isRecursive();
            this.cteList = options.cteList();
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        @Override
        public PostgreInsert._ColumnListSpec<C, T, I, Q> as(final @Nullable String alias) {
            if (alias == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.tableAlias = alias;
            return this;
        }

        @Override
        public PostgreInsert._ValuesDefaultSpec<C, T, I, Q> overridingSystemValue() {
            this.overridingMode = OverridingMode.OVERRIDING_SYSTEM_VALUE;
            return this;
        }

        @Override
        public PostgreInsert._ValuesDefaultSpec<C, T, I, Q> overridingUserValue() {
            this.overridingMode = OverridingMode.OVERRIDING_USER_VALUE;
            return this;
        }

        @Override
        public PostgreInsert._ValuesDefaultSpec<C, T, I, Q> ifOverridingSystemValue(BooleanSupplier supplier) {
            if (supplier.getAsBoolean()) {
                this.overridingMode = OverridingMode.OVERRIDING_SYSTEM_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }

        @Override
        public PostgreInsert._ValuesDefaultSpec<C, T, I, Q> ifOverridingUserValue(BooleanSupplier supplier) {
            if (supplier.getAsBoolean()) {
                this.overridingMode = OverridingMode.OVERRIDING_USER_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }

        @Override
        public PostgreInsert._ValuesLeftParenClause<C, T, I, Q> values() {
            return new StaticValuesLeftParenClause<>(this);
        }

        @Override
        public PostgreInsert._ConflictTargetOptionSpec<C, T, I, Q> onConflict() {
            return new OnConflictClause<>(this);
        }

        @Override
        public DialectStatement._StaticReturningCommaUnaryClause<Q> returning(Selection selectItem) {
            return this.comma(selectItem);
        }

        @Override
        public DialectStatement._StaticReturningCommaDualClause<Q> returning(Selection selectItem1, Selection selectItem2) {
            return this.comma(selectItem1, selectItem2);
        }

        @Override
        public DialectStatement._StaticReturningCommaUnaryClause<Q> comma(final Selection selectItem) {
            List<SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null) {
                selectItemList = new ArrayList<>();
                this.selectItemList = selectItemList;
            } else if (!(selectItemList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            selectItemList.add(selectItem);
            return this;
        }

        @Override
        public DialectStatement._StaticReturningCommaDualClause<Q> comma(final Selection selectItem1, final Selection selectItem2) {
            List<SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null) {
                selectItemList = new ArrayList<>();
                this.selectItemList = selectItemList;
            } else if (!(selectItemList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            selectItemList.add(selectItem1);
            selectItemList.add(selectItem2);
            return this;
        }

        @Override
        public DqlInsert._DqlInsertSpec<Q> returning(Consumer<Consumer<Selection>> consumer) {
            consumer.accept(this::comma);
            if (this.selectItemList == null) {
                throw ContextStack.criteriaError(this.context, _Exceptions::returningListEmpty);
            }
            return this;
        }

        @Override
        public DqlInsert._DqlInsertSpec<Q> returning(BiConsumer<C, Consumer<Selection>> consumer) {
            consumer.accept(this.criteria, this::comma);
            if (this.selectItemList == null) {
                throw ContextStack.criteriaError(this.context, _Exceptions::returningListEmpty);
            }
            return this;
        }

        @Override
        public DqlInsert._DqlInsertSpec<Q> returningAll() {
            if (this.selectItemList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.selectItemList = RETURNING_ALL;
            return this;
        }

        @Override
        public I asInsert() {
            if (this.selectItemList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this.dmlFunction.apply(this);
        }

        @Override
        public Q asReturningInsert() {
            final List<SelectItem> selectItemList = this.selectItemList;
            if (selectItemList != RETURNING_ALL) {
                if (!(selectItemList instanceof ArrayList && selectItemList.size() > 0)) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
                this.selectItemList = _CollectionUtils.unmodifiableList(selectItemList);
            }
            return this.dqlFunction.apply(this);
        }

        private PostgreInsert._ReturningSpec<C, I, Q> conflictClauseEnd(_PostgreInsert._ConflictActionClauseResult result) {
            if (this.conflictAction != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.conflictAction = result;
            return this;
        }

        private List<? extends SelectItem> effectiveReturningList() {
            final List<SelectItem> selectItemList = this.selectItemList;
            final List<? extends SelectItem> effectiveList;
            if (selectItemList == RETURNING_ALL) {
                effectiveList = this.effectiveFieldList();
            } else if (selectItemList == null || selectItemList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            } else {
                effectiveList = selectItemList;
            }
            return effectiveList;
        }


    }//PostgreComplexInsertValuesClause


    private static final class StaticValuesLeftParenClause<C, T, I extends Item, Q extends Item>
            extends InsertSupport.StaticColumnValuePairClause<
            C,
            T,
            PostgreInsert._ValuesLeftParenSpec<C, T, I, Q>>
            implements PostgreInsert._ValuesLeftParenSpec<C, T, I, Q> {

        private final PostgreComplexInsertValuesClause<C, T, I, Q> clause;

        private StaticValuesLeftParenClause(PostgreComplexInsertValuesClause<C, T, I, Q> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public PostgreInsert._ConflictTargetOptionSpec<C, T, I, Q> onConflict() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.onConflict();
        }

        @Override
        public DqlInsert._DqlInsertSpec<Q> returningAll() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returningAll();
        }

        @Override
        public PostgreInsert._StaticReturningCommaUnaryClause<Q> returning(Selection selectItem) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(selectItem);
        }

        @Override
        public PostgreInsert._StaticReturningCommaDualClause<Q> returning(Selection selectItem1, Selection selectItem2) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(selectItem1, selectItem2);
        }

        @Override
        public DqlInsert._DqlInsertSpec<Q> returning(Consumer<Consumer<Selection>> consumer) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(consumer);
        }

        @Override
        public DqlInsert._DqlInsertSpec<Q> returning(BiConsumer<C, Consumer<Selection>> consumer) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(consumer);
        }

        @Override
        public I asInsert() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.asInsert();
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


        private PostgreValueSyntaxInsertStatement(final PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
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


        private DomainInsertStatement(final PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);

        }


    }//DomainInsertStatement

    private static final class PrimaryDomainInsertStatement extends DomainInsertStatement<Insert, ReturningInsert>
            implements Insert {

        private final List<?> domainList;

        private PrimaryDomainInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
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
                , PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            super(childClause);
            parentStatement.prepared();
            this.parentStatement = parentStatement;
            if (parentStatement instanceof PrimaryParentDomainInsertStatement) {
                this.domainList = ((PrimaryParentDomainInsertStatement<?, ?>) parentStatement).domainList;
            } else if (parentStatement instanceof PrimaryParentDomainReturningInsertStatement) {
                this.domainList = ((PrimaryParentDomainReturningInsertStatement<?, ?>) parentStatement).domainList;
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


    private static final class PrimaryParentDomainInsertStatement<C, P>
            extends DomainInsertStatement<PostgreInsert._ParentInsert<C, P>, ReturningInsert>
            implements PostgreInsert._ParentInsert<C, P>
            , ValueSyntaxOptions {

        private final List<?> originalDomainList;

        private final List<?> domainList;

        private PrimaryParentDomainInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _CollectionUtils.asUnmodifiableList(this.originalDomainList);
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _ChildWithCteSpec<C, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this.context.criteria(), this
                    , this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            assertDomainList(this.originalDomainList, childClause);
            return new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            assertDomainList(this.originalDomainList, childClause);
            return new PrimaryChildDomainReturningInsertStatement(this, childClause);
        }


    }//PrimaryParentDomainInsertStatement

    private static final class PrimaryDomainReturningInsertStatement
            extends DomainInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert {

        private final List<?> domainList;

        private PrimaryDomainReturningInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
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
                , PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            super(childClause);
            this.parentStatement = parentStatement;
            if (parentStatement instanceof PrimaryParentDomainReturningInsertStatement) {
                this.domainList = ((PrimaryParentDomainReturningInsertStatement<?, ?>) parentStatement).domainList;
            } else if (parentStatement instanceof PrimaryParentDomainInsertStatement) {
                this.domainList = ((PrimaryParentDomainInsertStatement<?, ?>) parentStatement).domainList;
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

    private static final class PrimaryParentDomainReturningInsertStatement<C, P>
            extends DomainInsertStatement<Insert, PostgreInsert._ParentReturnInsert<C, P>>
            implements PostgreInsert._ParentReturnInsert<C, P>
            , ValueSyntaxOptions {

        private final List<?> originalDomainList;
        private final List<?> domainList;

        private PrimaryParentDomainReturningInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _CollectionUtils.asUnmodifiableList(this.originalDomainList);
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _ChildWithCteSpec<C, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this.context.criteria(), this, this::childInsertEnd
                    , this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            assertDomainList(this.originalDomainList, childClause);
            return new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            assertDomainList(this.originalDomainList, childClause);
            return new PrimaryChildDomainReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentDomainReturningInsertStatement


    private static final class SubDomainInsertStatement extends DomainInsertStatement<SubInsert, SubReturningInsert>
            implements SubInsert {

        private final List<?> domainList;

        private SubDomainInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
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

        private SubDomainReturningInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
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

        private ValueInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
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

        private PrimaryValueInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

    }//PrimaryValueInsertStatement


    private static final class PrimaryChildValueInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildValueInsert
            , Insert {

        private final ValueInsertStatement<?, ?> parentStatement;

        private PrimaryChildValueInsertStatement(ValueInsertStatement<?, ?> parentStatement
                , PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
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


    private static final class PrimaryParentValueInsertStatement<C, P>
            extends ValueInsertStatement<PostgreInsert._ParentInsert<C, P>, ReturningInsert>
            implements PostgreInsert._ParentInsert<C, P>
            , ValueSyntaxOptions {

        private PrimaryParentValueInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<C, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this.context.criteria(), this, this::childInsertEnd
                    , this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
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

        private PrimaryValueReturningInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//PrimaryValueReturningInsertStatement

    private static final class PrimaryChildValueReturningInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildValueInsert, ReturningInsert {

        private final ValueInsertStatement<?, ?> parentStatement;

        private PrimaryChildValueReturningInsertStatement(ValueInsertStatement<?, ?> parentStatement
                , PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
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

    private static final class PrimaryParentValueReturningInsertStatement<C, P>
            extends ValueInsertStatement<Insert, PostgreInsert._ParentReturnInsert<C, P>>
            implements PostgreInsert._ParentReturnInsert<C, P>
            , ValueSyntaxOptions {

        private PrimaryParentValueReturningInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<C, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this.context.criteria(), this
                    , this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            if (childClause.rowPairList().size() != this.rowPairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
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

        private SubValueInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//SubValueInsertStatement


    private static final class SubValueReturningInsertStatement
            extends ValueInsertStatement<SubInsert, SubReturningInsert>
            implements SubReturningInsert {

        private SubValueReturningInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
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

        private QueryInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
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

        private PrimaryQueryInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//PrimaryQueryInsertStatement

    private static final class PrimaryChildQueryInsertStatement extends QueryInsertStatement<Insert, ReturningInsert>
            implements Insert, _PostgreInsert._PostgreChildQueryInsert {

        private final QueryInsertStatement<?, ?> parentStatement;

        private PrimaryChildQueryInsertStatement(QueryInsertStatement<?, ?> parentStatement
                , PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
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


    private static final class PrimaryParentQueryInsertStatement<C, P>
            extends QueryInsertStatement<PostgreInsert._ParentInsert<C, P>, ReturningInsert>
            implements PostgreInsert._ParentInsert<C, P> {

        private PrimaryParentQueryInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<C, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this.context.criteria(), this
                    , this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            return new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            return new PrimaryChildQueryReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryParentQueryInsertStatement

    private static final class PrimaryQueryReturningInsertStatement
            extends QueryInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert {

        private PrimaryQueryReturningInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//PrimaryQueryReturningInsertStatement

    private static final class PrimaryChildQueryReturningInsertStatement
            extends QueryInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, _PostgreInsert._PostgreChildQueryInsert {

        private final QueryInsertStatement<?, ?> parentStatement;

        private PrimaryChildQueryReturningInsertStatement(QueryInsertStatement<?, ?> parentStatement
                , PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
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


    private static final class PrimaryParentQueryReturningInsertStatement<C, P>
            extends QueryInsertStatement<Insert, PostgreInsert._ParentReturnInsert<C, P>>
            implements PostgreInsert._ParentReturnInsert<C, P> {

        private PrimaryParentQueryReturningInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

        @Override
        public _ChildWithCteSpec<C, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this.context.criteria(), this
                    , this::childInsertEnd, this::childReturningInsertEnd);
        }

        private Insert childInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            return new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
        }

        private ReturningInsert childReturningInsertEnd(PostgreComplexInsertValuesClause<?, ?, ?, ?> childClause) {
            return new PrimaryChildQueryReturningInsertStatement(this, childClause)
                    .asReturningInsert();
        }


    }//PrimaryQueryReturningInsertStatement


    private static final class SubQueryInsertStatement extends QueryInsertStatement<SubInsert, SubReturningInsert>
            implements SubInsert {

        private SubQueryInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//SubQueryInsertStatement

    private static final class SubQueryReturningInsertStatement
            extends QueryInsertStatement<SubInsert, SubReturningInsert>
            implements SubReturningInsert {

        private SubQueryReturningInsertStatement(PostgreComplexInsertValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//SubQueryInsertStatement


}
