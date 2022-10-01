package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
import io.army.criteria.impl.inner.postgre._PostgreInsert;
import io.army.criteria.postgre.PostgreCteBuilder;
import io.army.criteria.postgre.PostgreInsert;
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

abstract class PostgreInserts extends InsertSupport {

    private PostgreInserts() {
    }


    static <C> PostgreInsert._PrimaryOptionSpec<C> primaryInsert(@Nullable C criteria) {
        return new PrimaryInsertIntoClause<>(criteria);
    }

    static <C> PostgreInsert._DynamicSubInsert<C> dynamicSubInsert(final String name, CriteriaContext outContext, @Nullable C criteria) {
        return new DynamicSubInsertIntoClause<>(name, outContext, criteria);
    }

    static <C, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert> PostgreInsert._StaticSubOptionSpec<C, I, Q> staticSubInsert(CriteriaContext outContext
            , @Nullable C criteria, Function<SubInsert, I> dmlFunc, Function<SubReturningInsert, Q> dqlFunc) {
        return new StaticSubInsertIntoClause<>(outContext, criteria, dmlFunc, dqlFunc);
    }


    @SuppressWarnings("unchecked")
    private static abstract class ConflictTargetItem<T, CR, PR>
            implements PostgreInsert._ConflictCollateClause<CR>
            , PostgreInsert._ConflictOpClassClause<PR>
            , CriteriaContextSpec
            , _ConflictTargetItem {

        private final IndexFieldMeta<T> indexColumn;

        private String collationName;

        private boolean opclass;

        ConflictTargetItem(IndexFieldMeta<T> indexColumn) {
            this.indexColumn = indexColumn;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            context.appendField(this.indexColumn);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            final String collationName = this.collationName;
            if (collationName != null) {
                sqlBuilder.append(" COLLATE ");
                context.parser().identifier(collationName, sqlBuilder);
            }
            if (this.opclass) {
                sqlBuilder.append(" opclass");
            }
        }

        @Override
        public final CR collation(final @Nullable String collationName) {
            if (collationName == null) {
                throw CriteriaContextStack.nullPointer(this.getContext());
            }
            this.collationName = collationName;
            return (CR) this;
        }

        @Override
        public final CR collation(Supplier<String> supplier) {
            this.collationName = supplier.get();
            return this.collation(supplier.get());
        }

        @Override
        public final CR ifCollation(Supplier<String> supplier) {
            this.collationName = supplier.get();
            return (CR) this;
        }

        @Override
        public final PR opClass() {
            this.opclass = true;
            return (PR) this;
        }

        @Override
        public final PR ifOpClass(BooleanSupplier supplier) {
            this.opclass = supplier.getAsBoolean();
            return (PR) this;
        }

    }//ConflictTargetItem

    /**
     * <p>
     * This interface is base interface of below:
     *     <ul>
     *         <li>{@link NonParentTargetWhereClauseSpec}</li>
     *         <li>{@link ParentTargetWhereClauseSpec}</li>
     *     </ul>
     * </p>
     */
    private interface TargetWhereClauseSpec extends CriteriaContextSpec {

        void addConflictTargetItem(_ConflictTargetItem item);

    }

    private interface NonParentTargetWhereClauseSpec<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends TargetWhereClauseSpec {

        PostgreInsert._ReturningSpec<C, I, Q> _doNothing(List<_Predicate> predicateList);

        PostgreInsert._DoUpdateSetClause<C, T, I, Q> _doUpdate(List<_Predicate> predicateList);

    }


    private static final class NonParentConflictAction<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends InsertSupport.MinWhereClause<
            C,
            PostgreInsert._NonParentConflictActionClause<C, T, I, Q>,
            PostgreInsert._ConflictTargetWhereAndSpec<C, T, I, Q>
            > implements PostgreInsert._ConflictTargetWhereSpec<C, T, I, Q>
            , PostgreInsert._ConflictTargetWhereAndSpec<C, T, I, Q> {

        private final NonParentTargetWhereClauseSpec<C, T, I, Q> clause;


        private NonParentConflictAction(NonParentTargetWhereClauseSpec<C, T, I, Q> clause) {
            super(clause.getContext());
            this.clause = clause;
        }

        @Override
        public PostgreInsert._ReturningSpec<C, I, Q> doNothing() {
            return this.clause._doNothing(this.endWhereClause());
        }

        @Override
        public PostgreInsert._DoUpdateSetClause<C, T, I, Q> doUpdate() {
            return this.clause._doUpdate(this.endWhereClause());
        }


    }//NonParentConflictAction


    private static final class NonParentConflictTargetItem<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends ConflictTargetItem<
            T,
            PostgreInsert._ConflictOpClassSpec<C, T, I, Q>,
            PostgreInsert._ConflictTargetCommaSpec<C, T, I, Q>>
            implements PostgreInsert._ConflictCollateSpec<C, T, I, Q> {

        private final NonParentTargetWhereClauseSpec<C, T, I, Q> clause;

        private NonParentConflictTargetItem(NonParentTargetWhereClauseSpec<C, T, I, Q> clause, IndexFieldMeta<T> indexColumn) {
            super(indexColumn);
            this.clause = clause;
        }

        @Override
        public PostgreInsert._ConflictCollateSpec<C, T, I, Q> comma(IndexFieldMeta<T> indexColumn) {
            final NonParentConflictTargetItem<C, T, I, Q> item;
            item = new NonParentConflictTargetItem<>(this.clause, indexColumn);
            this.clause.addConflictTargetItem(item);
            return item;
        }

        @Override
        public PostgreInsert._ConflictTargetWhereSpec<C, T, I, Q> rightParen() {
            return new NonParentConflictAction<>(this.clause);
        }

        @Override
        public CriteriaContext getContext() {
            return this.clause.getContext();
        }


    }//NonParentConflictTargetItem


    private interface ParentTargetWhereClauseSpec<C, P> extends TargetWhereClauseSpec {

        PostgreInsert._ParentReturningSpec<C, P> _doNothing(List<_Predicate> predicateList);


        PostgreInsert._ParentDoUpdateSetClause<C, P> _doUpdate(List<_Predicate> predicateList);


    }//ParentTargetWhereClauseSpec

    private static final class ParentConflictAction<C, P> extends InsertSupport.MinWhereClause<
            C,
            PostgreInsert._ParentConflictActionClause<C, P>,
            PostgreInsert._ParentConflictTargetWhereAndSpec<C, P>
            > implements PostgreInsert._ParentConflictTargetWhereSpec<C, P> {

        private final ParentTargetWhereClauseSpec<C, P> clause;

        private ParentConflictAction(ParentTargetWhereClauseSpec<C, P> clause) {
            super(clause.getContext());
            this.clause = clause;
        }

        @Override
        public PostgreInsert._ParentReturningSpec<C, P> doNothing() {
            return this.clause._doNothing(this.endWhereClause());
        }

        @Override
        public PostgreInsert._ParentDoUpdateSetClause<C, P> doUpdate() {
            return this.clause._doUpdate(this.endWhereClause());
        }


    }//ParentConflictAction

    private static final class ParentConflictTargetItem<C, P>
            extends ConflictTargetItem<
            P,
            PostgreInsert._ParentConflictOpClassSpec<C, P>,
            PostgreInsert._ParentConflictTargetCommaSpec<C, P>>
            implements PostgreInsert._ParentConflictCollateSpec<C, P> {

        private final ParentTargetWhereClauseSpec<C, P> clause;

        private ParentConflictTargetItem(ParentTargetWhereClauseSpec<C, P> clause, IndexFieldMeta<P> indexColumn) {
            super(indexColumn);
            this.clause = clause;
        }

        @Override
        public PostgreInsert._ParentConflictCollateSpec<C, P> comma(IndexFieldMeta<P> indexColumn) {
            final ParentConflictTargetItem<C, P> item = new ParentConflictTargetItem<>(this.clause, indexColumn);
            this.clause.addConflictTargetItem(item);
            return item;
        }

        @Override
        public PostgreInsert._ParentConflictTargetWhereSpec<C, P> rightParen() {
            return new ParentConflictAction<>(this.clause);
        }

        @Override
        public CriteriaContext getContext() {
            return this.clause.getContext();
        }


    }//ParentConflictTargetItem


    /**
     * @see AbstractOnConflictClause
     */
    private interface PostgreInsertValuesClauseSpec<C, CT, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends CriteriaContextSpec {

        PostgreInsert._ParentReturningClause<C, CT, I, Q> onConflictClauseEnd(_PostgreInsert._ConflictActionClauseResult result);

    }


    @SuppressWarnings("unchecked")
    private static abstract class AbstractOnConflictClause<C, T, CT, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert, LR, OC, UR, SR, WR, WA>
            extends InsertSupport.MinWhereClause<C, WR, WA>
            implements PostgreInsert._ConflictActionClause<WR, UR>
            , PostgreInsert._ConflictItemClause<T, LR, OC>
            , Update._SetClause<C, FieldMeta<T>, SR>
            , PostgreInsert._ParentReturningClause<C, CT, I, Q>
            , TargetWhereClauseSpec
            , _PostgreInsert._ConflictActionClauseResult {

        final PostgreInsertValuesClauseSpec<C, CT, I, Q> clause;

        private List<_ConflictTargetItem> targetItemList;
        private String constraintName;

        private boolean doNothing;

        private List<ItemPair> itemPairList;

        private List<_Predicate> actionPredicateList;

        private AbstractOnConflictClause(PostgreInsertValuesClauseSpec<C, CT, I, Q> clause) {
            super(clause.getContext());
            this.clause = clause;
        }

        @Override
        public final OC onConstraint(final @Nullable String constraintName) {
            if (constraintName == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.constraintName = constraintName;
            return (OC) this;
        }


        @Override
        public final WR doNothing() {
            this.doNothing = true;
            this.endDoUpdateSetClause();
            return (WR) this;
        }

        @Override
        public final UR doUpdate() {
            return (UR) this;
        }


        @Override
        public final SR setPairs(Consumer<Consumer<ItemPair>> consumer) {
            consumer.accept(this::addItemPair);
            return (SR) this;
        }

        @Override
        public final SR setPairs(BiConsumer<C, Consumer<ItemPair>> consumer) {
            consumer.accept(this.criteria, this::addItemPair);
            return (SR) this;
        }

        @Override
        public final SR setExp(FieldMeta<T> field, Expression value) {
            return this.addFieldValuePair(field, value);
        }

        @Override
        public final SR setExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            return this.addFieldValuePair(field, supplier.get());
        }

        @Override
        public final SR setExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            return this.addFieldValuePair(field, function.apply(this.criteria));
        }

        @Override
        public final SR ifSetExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            final Expression value;
            value = supplier.get();
            if (value != null) {
                this.addFieldValuePair(field, value);
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            final Expression value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.addFieldValuePair(field, value);
            }
            return (SR) this;
        }

        @Override
        public final SR setDefault(FieldMeta<T> field) {
            return this.addFieldValuePair(field, SQLs.defaultWord());
        }

        @Override
        public final SR setNull(FieldMeta<T> field) {
            return this.addFieldValuePair(field, SQLs.nullWord());
        }

        @Override
        public final I asInsert() {
            this.endDoUpdateSetClause();
            return this.clause.onConflictClauseEnd(this)
                    .asInsert();
        }

        @Override
        public final PostgreInsert._PostgreChildReturnSpec<CT, Q> returningAll() {
            this.endDoUpdateSetClause();
            return this.clause.onConflictClauseEnd(this)
                    .returningAll();
        }

        @Override
        public final PostgreInsert._ParentReturningCommaUnaryClause<CT, Q> returning(SelectItem selectItem) {
            this.endDoUpdateSetClause();
            return this.clause.onConflictClauseEnd(this)
                    .returning(selectItem);
        }

        @Override
        public final PostgreInsert._ParentReturningCommaDualClause<CT, Q> returning(SelectItem selectItem1, SelectItem selectItem2) {
            this.endDoUpdateSetClause();
            return this.clause.onConflictClauseEnd(this)
                    .returning(selectItem1, selectItem2);
        }

        @Override
        public final PostgreInsert._PostgreChildReturnSpec<CT, Q> returning(Consumer<Consumer<SelectItem>> consumer) {
            this.endDoUpdateSetClause();
            return this.clause.onConflictClauseEnd(this)
                    .returning(consumer);
        }

        @Override
        public final PostgreInsert._PostgreChildReturnSpec<CT, Q> returning(BiConsumer<C, Consumer<SelectItem>> consumer) {
            this.endDoUpdateSetClause();
            return this.clause.onConflictClauseEnd(this)
                    .returning(consumer);
        }


        @Override
        public final void addConflictTargetItem(final _ConflictTargetItem item) {
            List<_ConflictTargetItem> targetItemList = this.targetItemList;
            if (targetItemList == null) {
                this.targetItemList = targetItemList = new ArrayList<>();
            } else if (!(targetItemList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            targetItemList.add(item);
        }

        @Override
        public final String constraintName() {
            return this.constraintName;
        }

        @Override
        public final List<_ConflictTargetItem> conflictTargetItemList() {
            List<_ConflictTargetItem> targetItemList = this.targetItemList;
            if (targetItemList == null) {
                this.targetItemList = targetItemList = Collections.emptyList();
            }
            return targetItemList;
        }


        @Override
        public final boolean isDoNothing() {
            return this.doNothing;
        }

        @Override
        public final List<ItemPair> updateSetClauseList() {
            final List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null || itemPairList instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return itemPairList;
        }

        @Override
        public final List<_Predicate> updateSetPredicateList() {
            final List<_Predicate> actionPredicateList = this.actionPredicateList;
            if (actionPredicateList == null || actionPredicateList instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return actionPredicateList;
        }

        private SR addFieldValuePair(FieldMeta<T> field, Expression value) {
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                this.itemPairList = itemPairList = new ArrayList<>();
            } else if (!(itemPairList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            itemPairList.add(SQLs._itemPair(field, null, value));
            return (SR) this;
        }

        private void addItemPair(final ItemPair pair) {
            if (!(pair instanceof SQLs.ArmyItemPair)) {
                throw CriteriaContextStack.criteriaError(this.context, "Illegal ItemPair");
            }
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                this.itemPairList = itemPairList = new ArrayList<>();
            } else if (!(itemPairList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            itemPairList.add(pair);
        }

        private void endDoUpdateSetClause() {
            final List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                this.itemPairList = Collections.emptyList();
            } else if (itemPairList instanceof ArrayList) {
                this.itemPairList = _CollectionUtils.unmodifiableList(itemPairList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.actionPredicateList = this.endWhereClause();
        }


    }//AbstractOnConflictClause

    private static final class OnConflictClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends AbstractOnConflictClause<
            C,
            T,
            Void,
            I,
            Q,
            PostgreInsert._ConflictCollateSpec<C, T, I, Q>,
            PostgreInsert._NonParentConflictActionClause<C, T, I, Q>,
            PostgreInsert._DoUpdateSetClause<C, T, I, Q>,
            PostgreInsert._DoUpdateWhereSpec<C, T, I, Q>,
            PostgreInsert._ReturningSpec<C, I, Q>,
            PostgreInsert._DoUpdateWhereAndSpec<C, T, I, Q>>

            implements PostgreInsert._NonParentConflictItemClause<C, T, I, Q>
            , PostgreInsert._NonParentConflictActionClause<C, T, I, Q>
            , PostgreInsert._DoUpdateWhereSpec<C, T, I, Q>
            , PostgreInsert._DoUpdateWhereAndSpec<C, T, I, Q>
            , NonParentTargetWhereClauseSpec<C, T, I, Q> {

        private List<_Predicate> indexPredicateList;

        private OnConflictClause(PostgreInsertValuesClauseSpec<C, Void, I, Q> clause) {
            super(clause);
        }

        @Override
        public PostgreInsert._ConflictCollateSpec<C, T, I, Q> leftParen(IndexFieldMeta<T> indexColumn) {
            final NonParentConflictTargetItem<C, T, I, Q> item;
            item = new NonParentConflictTargetItem<>(this, indexColumn);
            this.addConflictTargetItem(item);
            return item;
        }

        @Override
        public Void child() {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }

        @Override
        public List<_Predicate> indexPredicateList() {
            return _CollectionUtils.safeList(this.indexPredicateList);
        }

        @Override
        public PostgreInsert._ReturningSpec<C, I, Q> _doNothing(List<_Predicate> predicateList) {
            if (this.indexPredicateList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.indexPredicateList = predicateList;
            return this;
        }

        @Override
        public PostgreInsert._DoUpdateSetClause<C, T, I, Q> _doUpdate(List<_Predicate> predicateList) {
            if (this.indexPredicateList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.indexPredicateList = predicateList;
            return this;
        }


    }//OnConflictClause


    private static final class ParentOnConflictClause<C, P> extends AbstractOnConflictClause<
            C,
            P,
            PostgreInsert._ChildWithCteSpec<C, P>,
            Insert,
            ReturningInsert,
            PostgreInsert._ParentConflictCollateSpec<C, P>,
            PostgreInsert._ParentConflictActionClause<C, P>,
            PostgreInsert._ParentDoUpdateSetClause<C, P>,
            PostgreInsert._ParentDoUpdateWhereSpec<C, P>,
            PostgreInsert._ParentReturningSpec<C, P>,
            PostgreInsert._ParentDoUpdateWhereAndSpec<C, P>>
            implements PostgreInsert._ParentConflictItemClause<C, P>
            , PostgreInsert._ParentConflictActionClause<C, P>
            , PostgreInsert._ParentDoUpdateWhereSpec<C, P>
            , PostgreInsert._ParentDoUpdateWhereAndSpec<C, P>
            , ParentTargetWhereClauseSpec<C, P> {

        private List<_Predicate> indexPredicateList;

        private ParentOnConflictClause(PostgreInsertValuesClauseSpec<C, PostgreInsert._ChildWithCteSpec<C, P>, Insert, ReturningInsert> clause) {
            super(clause);
        }


        @Override
        public PostgreInsert._ParentConflictCollateSpec<C, P> leftParen(IndexFieldMeta<P> indexColumn) {
            final ParentConflictTargetItem<C, P> item;
            item = new ParentConflictTargetItem<>(this, indexColumn);
            this.addConflictTargetItem(item);
            return item;
        }

        @Override
        public PostgreInsert._ChildWithCteSpec<C, P> child() {
            return this.clause.onConflictClauseEnd(this)
                    .child();
        }


        @Override
        public PostgreInsert._ParentReturningSpec<C, P> _doNothing(List<_Predicate> predicateList) {
            if (this.indexPredicateList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.indexPredicateList = predicateList;
            return this;
        }

        @Override
        public PostgreInsert._ParentDoUpdateSetClause<C, P> _doUpdate(List<_Predicate> predicateList) {
            if (this.indexPredicateList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.indexPredicateList = predicateList;
            return this;
        }

        @Override
        public List<_Predicate> indexPredicateList() {
            return _CollectionUtils.safeList(this.indexPredicateList);
        }


    }//ParentOnConflictClause



    /*-------------------below insert after values syntax class-------------------*/


    private static final class StaticCteInsertCommaClause<C> implements PostgreInsert._CteInsert<C>
            , PostgreInsert._CteReturningInsert<C> {

        private final boolean recursive;

        private final StaticCteComplexCommandClause<C> primaryComplexClause;

        private StaticCteInsertCommaClause(boolean recursive, StaticCteComplexCommandClause<C> staticPrimaryClause) {
            this.recursive = recursive;
            this.primaryComplexClause = staticPrimaryClause;
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C> comma(final @Nullable String name) {
            final StaticCteComplexCommandClause<C> staticPrimaryClause = this.primaryComplexClause;
            if (name == null) {
                throw CriteriaContextStack.nullPointer(staticPrimaryClause.context);
            }
            if (staticPrimaryClause.name != null) {
                throw CriteriaContextStack.nullPointer(staticPrimaryClause.context);
            }
            staticPrimaryClause.context.onStartCte(name);
            staticPrimaryClause.name = name;
            staticPrimaryClause.columnAliasList = null;
            return staticPrimaryClause;
        }

        @Override
        public PostgreInsert._PrimaryInsertIntoClause<C> space() {
            final PrimaryInsertIntoClause<C> primaryClause = this.primaryComplexClause.primaryClause;
            primaryClause.doWithCte(this.recursive, primaryClause.context.endWithClause(true)); //static with clause no ifWith method
            return primaryClause;
        }

    }//StaticCteInsertCommaClause

    private static final class StaticCteComplexCommandClause<C>
            extends CriteriaSupports.ParenStringConsumerClause<C, PostgreInsert._StaticCteAsClause<C>>
            implements PostgreInsert._StaticCteLeftParenSpec<C>
            , PostgreInsert._StaticCteComplexCommandSpec<C> {

        private final PrimaryInsertIntoClause<C> primaryClause;
        private final StaticCteInsertCommaClause<C> commaClause;

        private String name;

        private List<String> columnAliasList;


        private StaticCteComplexCommandClause(boolean recursive, final String name, PrimaryInsertIntoClause<C> primaryClause) {
            super(primaryClause.context);
            this.primaryClause = primaryClause;
            this.commaClause = new StaticCteInsertCommaClause<>(recursive, this);
            this.name = name;

        }

        @Override
        public PostgreInsert._StaticCteComplexCommandSpec<C> as() {
            return this;
        }

        @Override
        public PostgreInsert._CteInsertIntoClause<C, PostgreInsert._CteInsert<C>, PostgreInsert._CteReturningInsert<C>> literalMode(LiteralMode mode) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::insertEnd, this::returningInsertEnd)
                    .literalMode(mode);
        }

        @Override
        public PostgreInsert._StaticSubNullOptionSpec<C, PostgreInsert._CteInsert<C>, PostgreInsert._CteReturningInsert<C>> migration(boolean migration) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::insertEnd, this::returningInsertEnd)
                    .migration(migration);
        }

        @Override
        public PostgreInsert._StaticSubPreferLiteralSpec<C, PostgreInsert._CteInsert<C>, PostgreInsert._CteReturningInsert<C>> nullHandle(NullHandleMode mode) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::insertEnd, this::returningInsertEnd)
                    .nullHandle(mode);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, PostgreInsert._CteInsert<C>, PostgreInsert._CteReturningInsert<C>> insertInto(TableMeta<T> table) {
            return PostgreInserts.staticSubInsert(this.context, this.criteria, this::insertEnd, this::returningInsertEnd)
                    .insertInto(table);
        }


        @Override
        PostgreInsert._StaticCteAsClause<C> stringConsumerEnd(final List<String> stringList) {
            if (this.columnAliasList != null || stringList.size() == 0) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = stringList;
            this.context.onCteColumnAlias(this.name, stringList);
            return this;
        }


        private PostgreInsert._CteInsert<C> insertEnd(final SubInsert insert) {
            CriteriaUtils.createAndAddCte(this.context, this.name, this.columnAliasList, insert);
            //clear for next cte
            this.name = null;
            this.columnAliasList = null;
            return this.commaClause;
        }

        private PostgreInsert._CteReturningInsert<C> returningInsertEnd(final SubReturningInsert insert) {
            CriteriaUtils.createAndAddCte(this.context, this.name, this.columnAliasList, insert);
            //clear for next cte
            this.name = null;
            this.columnAliasList = null;
            return this.commaClause;
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
            CriteriaContextStack.setContextStack(this.context);
        }


        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C> with(final @Nullable String name) {
            final CriteriaContext context = this.context;
            if (name == null) {
                throw CriteriaContextStack.nullPointer(context);
            }
            context.onBeforeWithClause(false);
            context.onStartCte(name);
            return new StaticCteComplexCommandClause<>(false, name, this);
        }

        @Override
        public PostgreInsert._StaticCteLeftParenSpec<C> withRecursive(final @Nullable String name) {
            final CriteriaContext context = this.context;
            if (name == null) {
                throw CriteriaContextStack.nullPointer(context);
            }
            context.onBeforeWithClause(true);
            context.onStartCte(name);
            return new StaticCteComplexCommandClause<>(true, name, this);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table) {
            return new PrimaryComplexValuesClause<>(this, table);
        }

        @Override
        public <P> PostgreInsert._ParentTableAliasSpec<C, P> insertInto(ParentTableMeta<P> table) {
            return new ParentComplexValuesClause<>(this, table);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ChildTableMeta<T> table) {
            return new PrimaryComplexValuesClause<>(this, table);
        }

        @Override
        PostgreCteBuilder createCteBuilder(final boolean recursive) {
            return PostgreSupports.cteBuilder(recursive, this.context, this::doWithCte);
        }

    }//PrimaryInsertIntoClause


    private static final class StaticSubInsertIntoClause<C, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends NonQueryInsertOptionsImpl<
            PostgreInsert._StaticSubNullOptionSpec<C, I, Q>,
            PostgreInsert._StaticSubPreferLiteralSpec<C, I, Q>,
            PostgreInsert._CteInsertIntoClause<C, I, Q>>
            implements PostgreInsert._StaticSubOptionSpec<C, I, Q>
            , WithValueSyntaxOptions {

        private final Function<SubInsert, I> dmlFunction;

        private final Function<SubReturningInsert, Q> dqlFunction;

        private StaticSubInsertIntoClause(CriteriaContext outerContext, @Nullable C criteria, Function<SubInsert, I> dmlFunction
                , Function<SubReturningInsert, Q> dqlFunction) {
            super(CriteriaContexts.cteInsertContext(outerContext, criteria));
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
            //just push cte,here don't need to start cte
            CriteriaContextStack.push(this.context);

        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, I, Q> insertInto(TableMeta<T> table) {
            return new SubComplexValuesClause<>(this, table, dmlFunction, dqlFunction);
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


    private static final class DynamicSubInsertIntoClause<C>
            extends NonQueryWithCteOption<
            C,
            PostgreInsert._DynamicSubNullOptionSpec<C, SubInsert, SubReturningInsert>,
            PostgreInsert._DynamicSubPreferLiteralSpec<C, SubInsert, SubReturningInsert>,
            PostgreInsert._DynamicSubWithCteSpec<C, SubInsert, SubReturningInsert>,
            PostgreCteBuilder,
            PostgreInsert._CteInsertIntoClause<C, SubInsert, SubReturningInsert>>
            implements PostgreInsert._DynamicSubInsert<C>
            , Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, SubInsert, SubReturningInsert>> {

        private final C criteria;

        final String name;

        private List<String> columnAliasList;

        private DynamicSubInsertIntoClause(final String name, final CriteriaContext outContext, final @Nullable C criteria) {
            super(CriteriaContexts.cteInsertContext(outContext, criteria));
            this.criteria = criteria;
            this.name = name;
            //firstly,onStartCte
            outContext.onStartCte(name);
            //secondly,push context
            CriteriaContextStack.push(this.context);

        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, SubInsert, SubReturningInsert>> leftParen(String string) {
            if (this.columnAliasList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = Collections.singletonList(string);
            return this;
        }

        @Override
        public Statement._CommaStringDualSpec<PostgreInsert._DynamicSubOptionSpec<C, SubInsert, SubReturningInsert>> leftParen(String string1, String string2) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(string1, string2);
        }

        @Override
        public Statement._CommaStringQuadraSpec<PostgreInsert._DynamicSubOptionSpec<C, SubInsert, SubReturningInsert>> leftParen(String string1, String string2, String string3, String string4) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(string1, string2, string3, string4);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, SubInsert, SubReturningInsert>> leftParen(Consumer<Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(consumer);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, SubInsert, SubReturningInsert>> leftParen(BiConsumer<C, Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParen(consumer);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, SubInsert, SubReturningInsert>> leftParenIf(Consumer<Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParenIf(consumer);
        }

        @Override
        public Statement._RightParenClause<PostgreInsert._DynamicSubOptionSpec<C, SubInsert, SubReturningInsert>> leftParenIf(BiConsumer<C, Consumer<String>> consumer) {
            return CriteriaSupports.stringQuadra(this.context, this.criteria, this::columnAliasClauseEnd)
                    .leftParenIf(consumer);
        }

        @Override
        public PostgreInsert._DynamicSubOptionSpec<C, SubInsert, SubReturningInsert> rightParen() {
            return this;
        }


        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, SubInsert, SubReturningInsert> insertInto(TableMeta<T> table) {
            return new SubComplexValuesClause<>(this, table, this::sameInsert, this::sameReturningInsert);
        }


        @Override
        PostgreCteBuilder createCteBuilder(final boolean recursive) {
            return PostgreSupports.cteBuilder(recursive, this.context, this::doWithCte);
        }


        private PostgreInsert._DynamicSubOptionSpec<C, SubInsert, SubReturningInsert> columnAliasClauseEnd(final List<String> aliasList) {
            if (this.columnAliasList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final CriteriaContext outerContext;
            outerContext = this.context.getOuterContext();
            assert outerContext != null;
            outerContext.onCteColumnAlias(this.name, aliasList);
            this.columnAliasList = aliasList;
            return this;
        }

        private SubInsert sameInsert(final SubInsert insert) {
            final CriteriaContext outerContext = this.context.getOuterContext();
            assert outerContext != null;
            CriteriaUtils.createAndAddCte(outerContext, this.name, this.columnAliasList, insert);
            return insert;
        }

        private SubReturningInsert sameReturningInsert(final SubReturningInsert insert) {
            final CriteriaContext outerContext = this.context.getOuterContext();
            assert outerContext != null;
            CriteriaUtils.createAndAddCte(outerContext, this.name, this.columnAliasList, insert);
            return insert;
        }


    }//DynamicSubInsertIntoClause


    private enum OverridingMode implements SQLWords {

        OVERRIDING_SYSTEM_VALUE,
        OVERRIDING_USER_VALUE;


        @Override
        public final String render() {
            final String words;
            switch (this) {
                case OVERRIDING_USER_VALUE:
                    words = "OVERRIDING USER VALUE";
                    break;
                case OVERRIDING_SYSTEM_VALUE:
                    words = "OVERRIDING SYSTEM VALUE";
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(this);
            }
            return words;
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


    private static abstract class ComplexValuesClause<C, T, CT, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert, AR, CR, DR, VR>
            extends ComplexInsertValuesClause<C, T, CR, DR, VR>
            implements Statement._AsClause<AR>
            , PostgreInsert._ParentReturningClause<C, CT, I, Q>
            , PostgreInsert._ParentReturningCommaUnaryClause<CT, Q>
            , PostgreInsert._ParentReturningCommaDualClause<CT, Q>
            , WithValueSyntaxOptions {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private String tableAlias;

        private List<SelectItem> selectItemList;

        OverridingMode overridingMode;

        _PostgreInsert._ConflictActionClauseResult conflictAction;

        private ComplexValuesClause(WithValueSyntaxOptions options, TableMeta<T> table) {
            super(options, table);
            this.cteList = options.cteList();
            this.recursive = options.isRecursive();
        }

        @SuppressWarnings("unchecked")
        @Override
        public final AR as(final @Nullable String alias) {
            if (alias == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.tableAlias = alias;
            return (AR) this;
        }

        @Override
        public final PostgreInsert._PostgreChildReturnSpec<CT, Q> returningAll() {
            if (this.selectItemList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.selectItemList = Collections.emptyList();
            return this;
        }

        @Override
        public final PostgreInsert._ParentReturningCommaUnaryClause<CT, Q> returning(SelectItem selectItem1) {
            return this.comma(selectItem1);
        }

        @Override
        public final PostgreInsert._ParentReturningCommaDualClause<CT, Q> returning(SelectItem selectItem1, SelectItem selectItem2) {
            return this.comma(selectItem1, selectItem2);
        }

        @Override
        public final PostgreInsert._PostgreChildReturnSpec<CT, Q> returning(Consumer<Consumer<SelectItem>> consumer) {
            consumer.accept(this::comma);
            return this;
        }

        @Override
        public final PostgreInsert._PostgreChildReturnSpec<CT, Q> returning(BiConsumer<C, Consumer<SelectItem>> consumer) {
            consumer.accept(this.criteria, this::comma);
            return this;
        }

        @Override
        public final PostgreInsert._ParentReturningCommaUnaryClause<CT, Q> comma(final SelectItem selectItem) {
            List<SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null) {
                this.selectItemList = selectItemList = new ArrayList<>();
            } else if (!(selectItemList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            selectItemList.add(selectItem);
            return this;
        }

        @Override
        public final PostgreInsert._ParentReturningCommaDualClause<CT, Q> comma(final SelectItem selectItem1, final SelectItem selectItem2) {
            List<SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null) {
                selectItemList = new ArrayList<>();
                this.selectItemList = selectItemList;
            } else if (!(selectItemList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            selectItemList.add(selectItem1);
            selectItemList.add(selectItem2);
            return this;
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            return this.cteList;
        }

        private List<? extends SelectItem> returningList() {
            final List<SelectItem> selectItemList = this.selectItemList;
            assert selectItemList != null;
            final List<? extends SelectItem> effectiveReturningList;
            if (selectItemList.size() == 0) {
                effectiveReturningList = this.effectiveFieldList();
            } else {
                effectiveReturningList = _CollectionUtils.unmodifiableList(selectItemList);
            }
            return effectiveReturningList;
        }

        final boolean hasReturningClause() {
            return this.selectItemList != null;
        }

    }//ComplexValuesClause


    private static final class NonParentStaticValuesLeftParenClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends InsertSupport.StaticColumnValuePairClause<
            C,
            T,
            PostgreInsert._ValuesLeftParenSpec<C, T, I, Q>>
            implements PostgreInsert._ValuesLeftParenSpec<C, T, I, Q> {

        private final NonParentComplexValuesClause<C, T, I, Q> clause;

        private NonParentStaticValuesLeftParenClause(NonParentComplexValuesClause<C, T, I, Q> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public PostgreInsert._NonParentConflictItemClause<C, T, I, Q> onConflict() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.onConflict();
        }

        @Override
        public DqlStatement._DqlInsertSpec<Q> returningAll() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returningAll();
        }

        @Override
        public PostgreInsert._StaticReturningCommaUnaryClause<Q> returning(SelectItem selectItem) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(selectItem);
        }

        @Override
        public PostgreInsert._StaticReturningCommaDualClause<Q> returning(SelectItem selectItem1, SelectItem selectItem2) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(selectItem1, selectItem2);
        }

        @Override
        public DqlStatement._DqlInsertSpec<Q> returning(Consumer<Consumer<SelectItem>> consumer) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(consumer);
        }

        @Override
        public DqlStatement._DqlInsertSpec<Q> returning(BiConsumer<C, Consumer<SelectItem>> consumer) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(consumer);
        }

        @Override
        public I asInsert() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.asInsert();
        }


    }//NonParentStaticValuesLeftParenClause


    private static abstract class NonParentComplexValuesClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends ComplexValuesClause<
            C,
            T,
            Void,
            I,
            Q,
            PostgreInsert._ColumnListSpec<C, T, I, Q>,
            PostgreInsert._ComplexOverridingValueSpec<C, T, I, Q>,
            PostgreInsert._ValuesDefaultSpec<C, T, I, Q>,
            PostgreInsert._OnConflictSpec<C, T, I, Q>>
            implements PostgreInsert._TableAliasSpec<C, T, I, Q>
            , PostgreInsert._ComplexOverridingValueSpec<C, T, I, Q>
            , PostgreInsert._ComplexColumnDefaultSpec<C, T, I, Q>
            , PostgreInsert._OnConflictSpec<C, T, I, Q>
            , PostgreInsertValuesClauseSpec<C, Void, I, Q> {


        private NonParentComplexValuesClause(WithValueSyntaxOptions options, TableMeta<T> table) {
            super(options, table);
        }


        @Override
        public final PostgreInsert._ComplexColumnDefaultSpec<C, T, I, Q> overridingSystemValue() {
            this.overridingMode = OverridingMode.OVERRIDING_SYSTEM_VALUE;
            return this;
        }

        @Override
        public final PostgreInsert._ComplexColumnDefaultSpec<C, T, I, Q> overridingUserValue() {
            this.overridingMode = OverridingMode.OVERRIDING_USER_VALUE;
            return this;
        }

        @Override
        public final PostgreInsert._ValuesDefaultSpec<C, T, I, Q> ifOverridingSystemValue(final BooleanSupplier supplier) {
            if (supplier.getAsBoolean()) {
                this.overridingMode = OverridingMode.OVERRIDING_SYSTEM_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }

        @Override
        public final PostgreInsert._ValuesDefaultSpec<C, T, I, Q> ifOverridingUserValue(final BooleanSupplier supplier) {
            if (supplier.getAsBoolean()) {
                this.overridingMode = OverridingMode.OVERRIDING_USER_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }


        @Override
        public final PostgreInsert._ValuesLeftParenClause<C, T, I, Q> values() {
            return new NonParentStaticValuesLeftParenClause<>(this);
        }


        @Override
        public final PostgreInsert._NonParentConflictItemClause<C, T, I, Q> onConflict() {
            return new OnConflictClause<>(this);
        }

        @Override
        public final Void child() {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }

        @Override
        public final PostgreInsert._ParentReturningClause<C, Void, I, Q> onConflictClauseEnd(final _PostgreInsert._ConflictActionClauseResult result) {
            if (this.conflictAction != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.conflictAction = result;
            return this;
        }


    }//NonParentComplexValuesClause

    private static final class PrimaryComplexValuesClause<C, T>
            extends NonParentComplexValuesClause<C, T, Insert, ReturningInsert> {

        private final ParentComplexValuesClause<C, ?> parentClause;

        private PrimaryComplexValuesClause(PrimaryInsertIntoClause<C> options, SimpleTableMeta<T> table) {
            super(options, table);
            this.parentClause = null;
        }

        private PrimaryComplexValuesClause(PrimaryInsertIntoClause<C> options, ChildTableMeta<T> table) {
            super(options, table);
            this.parentClause = null;
        }

        private PrimaryComplexValuesClause(ParentComplexValuesClause<C, ?> parentClause, ChildTableMeta<T> table) {
            super(parentClause, table);
            this.parentClause = parentClause;
        }

        @Override
        public Insert asInsert() {
            if (this.hasReturningClause()) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final ParentComplexValuesClause<C, ?> parentClause = this.parentClause;
            final InsertMode mode;
            mode = this.assertInsertMode(parentClause);
            final Insert._InsertSpec spec;
            switch (mode) {
                case DOMAIN: {
                    if (parentClause == null) {
                        spec = new PrimaryDomainInsertStatement(this);
                    } else {
                        spec = new ChildDomainInsertStatement(parentClause, this);
                    }
                }
                break;
                case VALUES: {
                    if (parentClause == null) {
                        spec = new PrimaryValueInsertStatement(this);
                    } else {
                        spec = new ChildValueInsertStatement(parentClause, this);
                    }
                }
                break;
                case QUERY: {
                    if (parentClause == null) {
                        spec = new PrimaryQueryInsertStatement(this);
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

        @Override
        public ReturningInsert asReturningInsert() {
            if (!this.hasReturningClause()) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final ParentComplexValuesClause<C, ?> parentClause = this.parentClause;
            final InsertMode mode;
            mode = this.assertInsertMode(parentClause);
            final ReturningInsert._ReturningInsertSpec spec;
            switch (mode) {
                case DOMAIN: {
                    if (parentClause == null) {
                        spec = new PrimaryDomainReturningInsertStatement(this);
                    } else {
                        spec = new ChildDomainReturningInsertStatement(parentClause, this);
                    }
                }
                break;
                case VALUES: {
                    if (parentClause == null) {
                        spec = new PrimaryValueReturningInsertStatement(this);
                    } else {
                        spec = new ChildValueReturningInsertStatement(parentClause, this);
                    }
                }
                break;
                case QUERY: {
                    if (parentClause == null) {
                        spec = new PrimaryQueryReturningInsertStatement(this);
                    } else {
                        spec = new ChildQueryReturningInsertStatement(parentClause, this);
                    }
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return spec.asReturningInsert();
        }


    }//PrimaryNonParentComplexValuesClause


    private static final class SubComplexValuesClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends NonParentComplexValuesClause<C, T, I, Q>
            implements _Insert._SupportWithClauseInsert {

        private final Function<SubInsert, I> dmlFunction;

        private final Function<SubReturningInsert, Q> dqlFunction;


        private SubComplexValuesClause(DynamicSubInsertIntoClause<C> options, TableMeta<T> table
                , Function<SubInsert, I> dmlFunction, Function<SubReturningInsert, Q> dqlFunction) {
            super(options, table);
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        private SubComplexValuesClause(StaticSubInsertIntoClause<C, I, Q> options, TableMeta<T> table
                , Function<SubInsert, I> dmlFunction, Function<SubReturningInsert, Q> dqlFunction) {
            super(options, table);
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }


        @Override
        public I asInsert() {
            if (this.hasReturningClause()) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final InsertMode mode;
            mode = this.getInsertMode();
            final SubInsert._SubInsertSpec spec;
            switch (mode) {
                case DOMAIN:
                    spec = new SubDomainInsertStatement(this);
                    break;
                case VALUES:
                    spec = new SubValueInsertStatement(this);
                    break;
                case QUERY:
                    spec = new SubQueryInsertStatement(this);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return this.dmlFunction.apply(spec.asInsert());
        }

        @Override
        public Q asReturningInsert() {
            if (!this.hasReturningClause()) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final InsertMode mode;
            mode = this.getInsertMode();
            final SubReturningInsert._SubReturningInsertSpec spec;
            switch (mode) {
                case DOMAIN:
                    spec = new SubDomainReturningInsertStatement(this);
                    break;
                case VALUES:
                    spec = new SubValueReturningInsertStatement(this);
                    break;
                case QUERY:
                    spec = new SubQueryReturningInsertStatement(this);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return this.dqlFunction.apply(spec.asReturningInsert());
        }


    }//SubComplexValuesClause


    private static final class ParentStaticValuesLeftParenClause<C, P>
            extends InsertSupport.StaticColumnValuePairClause<
            C,
            P,
            PostgreInsert._ParentValuesLeftParenSpec<C, P>>
            implements PostgreInsert._ParentValuesLeftParenSpec<C, P> {

        private final ParentComplexValuesClause<C, P> clause;

        private ParentStaticValuesLeftParenClause(ParentComplexValuesClause<C, P> clause) {
            super(clause.context, clause::validateField);
            this.clause = clause;
        }

        @Override
        public PostgreInsert._ParentConflictItemClause<C, P> onConflict() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.onConflict();
        }

        @Override
        public PostgreInsert._PostgreChildReturnSpec<PostgreInsert._ChildWithCteSpec<C, P>, ReturningInsert> returningAll() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returningAll();
        }

        @Override
        public PostgreInsert._ParentReturningCommaUnaryClause<PostgreInsert._ChildWithCteSpec<C, P>, ReturningInsert> returning(SelectItem selectItem) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(selectItem);
        }

        @Override
        public PostgreInsert._ParentReturningCommaDualClause<PostgreInsert._ChildWithCteSpec<C, P>, ReturningInsert> returning(SelectItem selectItem1, SelectItem selectItem2) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(selectItem1, selectItem2);
        }

        @Override
        public PostgreInsert._PostgreChildReturnSpec<PostgreInsert._ChildWithCteSpec<C, P>, ReturningInsert> returning(Consumer<Consumer<SelectItem>> consumer) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(consumer);
        }

        @Override
        public PostgreInsert._PostgreChildReturnSpec<PostgreInsert._ChildWithCteSpec<C, P>, ReturningInsert> returning(BiConsumer<C, Consumer<SelectItem>> consumer) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(consumer);
        }

        @Override
        public PostgreInsert._ChildWithCteSpec<C, P> child() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.child();
        }

        @Override
        public Insert asInsert() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.asInsert();
        }


    }//NonParentStaticValuesLeftParenClause

    private static final class ParentComplexValuesClause<C, P> extends ComplexValuesClause<
            C,
            P,
            PostgreInsert._ChildWithCteSpec<C, P>,
            Insert,
            ReturningInsert,
            PostgreInsert._ParentColumnListSpec<C, P>,
            PostgreInsert._ParentComplexOverridingValueSpec<C, P>,
            PostgreInsert._ParentValuesDefaultSpec<C, P>,
            PostgreInsert._ParentOnConflictSpec<C, P>>
            implements PostgreInsert._ParentTableAliasSpec<C, P>
            , PostgreInsert._ParentComplexOverridingValueSpec<C, P>
            , PostgreInsert._ParentComplexColumnDefaultSpec<C, P>
            , PostgreInsert._ParentOnConflictSpec<C, P>
            , PostgreInsertValuesClauseSpec<C, PostgreInsert._ChildWithCteSpec<C, P>, Insert, ReturningInsert> {


        private ParentComplexValuesClause(PrimaryInsertIntoClause<C> options, ParentTableMeta<P> table) {
            super(options, table);
        }

        @Override
        public PostgreInsert._ParentComplexColumnDefaultSpec<C, P> overridingSystemValue() {
            this.overridingMode = OverridingMode.OVERRIDING_SYSTEM_VALUE;
            return this;
        }

        @Override
        public PostgreInsert._ParentComplexColumnDefaultSpec<C, P> overridingUserValue() {
            this.overridingMode = OverridingMode.OVERRIDING_USER_VALUE;
            return this;
        }

        @Override
        public PostgreInsert._ParentValuesDefaultSpec<C, P> ifOverridingSystemValue(final BooleanSupplier supplier) {
            if (supplier.getAsBoolean()) {
                this.overridingMode = OverridingMode.OVERRIDING_SYSTEM_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }

        @Override
        public PostgreInsert._ParentValuesDefaultSpec<C, P> ifOverridingUserValue(final BooleanSupplier supplier) {
            if (supplier.getAsBoolean()) {
                this.overridingMode = OverridingMode.OVERRIDING_USER_VALUE;
            } else {
                this.overridingMode = null;
            }
            return this;
        }

        @Override
        public PostgreInsert._ParentValuesLeftParenClause<C, P> values() {
            return new ParentStaticValuesLeftParenClause<>(this);
        }


        @Override
        public PostgreInsert._ParentConflictItemClause<C, P> onConflict() {
            return new ParentOnConflictClause<>(this);
        }

        @Override
        public Insert asInsert() {
            if (this.hasReturningClause()) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final InsertMode mode;
            mode = this.getInsertMode();
            final Insert._InsertSpec spec;
            switch (mode) {
                case DOMAIN:
                    spec = new PrimaryDomainInsertStatement(this, this.domainListForSingle());
                    break;
                case VALUES:
                    spec = new PrimaryValueInsertStatement(this);
                    break;
                case QUERY:
                    spec = new PrimaryQueryInsertStatement(this);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return spec.asInsert();
        }

        @Override
        public ReturningInsert asReturningInsert() {
            if (!this.hasReturningClause()) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final InsertMode mode;
            mode = this.getInsertMode();
            final ReturningInsert._ReturningInsertSpec spec;
            switch (mode) {
                case DOMAIN:
                    spec = new PrimaryDomainReturningInsertStatement(this, this.domainListForSingle());
                    break;
                case VALUES:
                    spec = new PrimaryValueReturningInsertStatement(this);
                    break;
                case QUERY:
                    spec = new PrimaryQueryReturningInsertStatement(this);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return spec.asReturningInsert();
        }


        @Override
        public PostgreInsert._ChildWithCteSpec<C, P> child() {
            return null;
        }

        @Override
        public PostgreInsert._ParentReturningClause<C, PostgreInsert._ChildWithCteSpec<C, P>, Insert, ReturningInsert> onConflictClauseEnd(final _PostgreInsert._ConflictActionClauseResult result) {
            if (this.conflictAction != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.conflictAction = result;
            return this;
        }


    }//ParentComplexValuesClause


    private static abstract class PostgreValueSyntaxInsertStatement<I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends AbstractValueSyntaxStatement<I, Q>
            implements PostgreInsert, _PostgreInsert {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final String tableAlias;

        private final OverridingMode overridingMode;

        private final _ConflictActionClauseResult conflictAction;

        private final List<? extends SelectItem> returningList;


        private PostgreValueSyntaxInsertStatement(final ComplexValuesClause<?, ?, ?, ?, ?, ?, ?, ?, ?> clause) {
            super(clause);

            this.recursive = clause.recursive;
            this.cteList = clause.cteList;
            this.tableAlias = clause.tableAlias;
            this.overridingMode = clause.overridingMode;

            this.conflictAction = clause.conflictAction;
            if (this instanceof DqlStatement.DqlInsert) {
                this.returningList = clause.returningList();
                assert this.returningList.size() > 0;
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


    static abstract class DomainInsertStatement<I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends PostgreValueSyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreDomainInsert {

        final List<?> domainList;

        private DomainInsertStatement(final NonParentComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
            if (this.insertTable instanceof ChildTableMeta) {
                this.domainList = clause.domainListForWithInsert();
            } else {
                this.domainList = clause.domainListForSingle();
            }
        }

        private DomainInsertStatement(final ParentComplexValuesClause<?, ?> clause, List<?> domainList) {
            super(clause);
            this.domainList = domainList;
        }

        private DomainInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , final NonParentComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
            this.domainList = clause.domainListForChild(parentClause);
        }

        @Override
        public final List<?> domainList() {
            return this.domainList;
        }


    }//DomainInsertStatement

    private static final class PrimaryDomainInsertStatement extends DomainInsertStatement<Insert, ReturningInsert>
            implements Insert, Insert._InsertSpec {

        private PrimaryDomainInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryDomainInsertStatement(ParentComplexValuesClause<?, ?> clause, List<?> domainList) {
            super(clause, domainList);
        }

    }//PrimaryDomainInsertStatement

    private static final class PrimaryDomainReturningInsertStatement
            extends DomainInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private PrimaryDomainReturningInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryDomainReturningInsertStatement(ParentComplexValuesClause<?, ?> clause, List<?> domainList) {
            super(clause, domainList);
        }

    }//PrimaryDomainReturningInsertStatement


    private static abstract class ChildSyntaxDomainInsertStatement
            extends DomainInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildDomainInsert {

        private final DomainInsertStatement<Insert, ReturningInsert> parentStmt;

        private ChildSyntaxDomainInsertStatement(final ParentComplexValuesClause<?, ?> parentClause
                , final PrimaryComplexValuesClause<?, ?> clause) {
            super(parentClause, clause);
            if (parentClause.hasReturningClause()) {
                this.parentStmt = new PrimaryDomainReturningInsertStatement(parentClause, this.domainList);
            } else {
                this.parentStmt = new PrimaryDomainInsertStatement(parentClause, this.domainList);
            }

        }

        @Override
        public final _PostgreDomainInsert parentStmt() {
            return this.parentStmt;
        }

    }//ChildSyntaxDomainInsertStatement


    private static final class ChildDomainInsertStatement extends ChildSyntaxDomainInsertStatement
            implements Insert, Insert._InsertSpec {

        private ChildDomainInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , PrimaryComplexValuesClause<?, ?> clause) {
            super(parentClause, clause);
        }


    }//ChildDomainInsertStatement

    private static final class ChildDomainReturningInsertStatement extends ChildSyntaxDomainInsertStatement
            implements ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private ChildDomainReturningInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , PrimaryComplexValuesClause<?, ?> clause) {
            super(parentClause, clause);
        }

    }//ChildDomainReturningInsertStatement


    private static final class SubDomainInsertStatement extends DomainInsertStatement<SubInsert, SubReturningInsert>
            implements SubInsert
            , SubInsert._SubInsertSpec {


        private SubDomainInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

    }//SubDomainInsertStatement


    private static final class SubDomainReturningInsertStatement
            extends DomainInsertStatement<SubInsert, SubReturningInsert>
            implements SubReturningInsert
            , SubReturningInsert._SubReturningInsertSpec {


        private SubDomainReturningInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

    }//SubDomainReturningInsertStatement


    static abstract class ValueInsertStatement<I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends PostgreValueSyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreValueInsert {

        private final List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private ValueInsertStatement(ComplexValuesClause<?, ?, ?, ?, ?, ?, ?, ?, ?> clause) {
            super(clause);
            this.rowPairList = clause.rowPairList();
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.rowPairList;
        }


    }//ValueInsertStatement


    private static final class PrimaryValueInsertStatement extends ValueInsertStatement<Insert, ReturningInsert>
            implements Insert, Insert._InsertSpec {

        private PrimaryValueInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryValueInsertStatement(ParentComplexValuesClause<?, ?> clause) {
            super(clause);
        }

    }//PrimaryValueInsertStatement

    private static final class PrimaryValueReturningInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private PrimaryValueReturningInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryValueReturningInsertStatement(ParentComplexValuesClause<?, ?> clause) {
            super(clause);
        }

    }//PrimaryValueReturningInsertStatement


    private static abstract class ChildSyntaxValueInsertStatement
            extends ValueInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildValueInsert {

        private final ValueInsertStatement<Insert, ReturningInsert> parentStmt;

        private ChildSyntaxValueInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , final PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
            if (parentClause.hasReturningClause()) {
                this.parentStmt = new PrimaryValueReturningInsertStatement(parentClause);
            } else {
                this.parentStmt = new PrimaryValueInsertStatement(parentClause);
            }

        }

        @Override
        public final _PostgreValueInsert parentStmt() {
            return this.parentStmt;
        }


    }//ChildSyntaxValueInsertStatement

    private static final class ChildValueInsertStatement extends ChildSyntaxValueInsertStatement
            implements Insert, Insert._InsertSpec {

        private ChildValueInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , PrimaryComplexValuesClause<?, ?> clause) {
            super(parentClause, clause);
        }
    }//ChildValueInsertStatement


    private static final class ChildValueReturningInsertStatement extends ChildSyntaxValueInsertStatement
            implements ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private ChildValueReturningInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , PrimaryComplexValuesClause<?, ?> clause) {
            super(parentClause, clause);
        }

    }//ChildValueReturningInsertStatement


    private static final class SubValueInsertStatement
            extends ValueInsertStatement<SubInsert, SubReturningInsert>
            implements SubInsert
            , SubInsert._SubInsertSpec {

        private SubValueInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//SubValueInsertStatement


    private static final class SubValueReturningInsertStatement
            extends ValueInsertStatement<SubInsert, SubReturningInsert>
            implements SubReturningInsert
            , SubReturningInsert._SubReturningInsertSpec {

        private SubValueReturningInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

    }//SubValueReturningInsertStatement


    static abstract class QueryInsertStatement<I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends InsertSupport.AbstractQuerySyntaxInsertStatement<I, Q>
            implements _PostgreInsert._PostgreQueryInsert, PostgreInsert {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final String tableAlias;

        private final OverridingMode overridingMode;

        private final _ConflictActionClauseResult conflictAction;

        private final List<? extends SelectItem> returningList;

        private QueryInsertStatement(ComplexValuesClause<?, ?, ?, ?, ?, ?, ?, ?, ?> clause) {
            super(clause);
            this.recursive = clause.recursive;
            this.cteList = clause.cteList;
            this.tableAlias = clause.tableAlias;
            this.overridingMode = clause.overridingMode;

            this.conflictAction = clause.conflictAction;
            if (this instanceof DqlStatement.DqlInsert) {
                this.returningList = clause.returningList();
                assert this.returningList.size() > 0;
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
            implements Insert, Insert._InsertSpec {

        private PrimaryQueryInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryQueryInsertStatement(ParentComplexValuesClause<?, ?> clause) {
            super(clause);
        }

    }//PrimaryQueryInsertStatement

    private static final class PrimaryQueryReturningInsertStatement
            extends QueryInsertStatement<Insert, ReturningInsert>
            implements ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private PrimaryQueryReturningInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryQueryReturningInsertStatement(ParentComplexValuesClause<?, ?> clause) {
            super(clause);
        }


    }//PrimaryQueryReturningInsertStatement


    private static final class ChildQueryInsertStatement extends QueryInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildQueryInsert, Insert, Insert._InsertSpec {

        private final QueryInsertStatement<Insert, ReturningInsert> parentStmt;


        private ChildQueryInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , final PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
            if (parentClause.hasReturningClause()) {
                this.parentStmt = new PrimaryQueryReturningInsertStatement(parentClause);
            } else {
                this.parentStmt = new PrimaryQueryInsertStatement(parentClause);
            }
        }

        @Override
        public _PostgreQueryInsert parentStmt() {
            return this.parentStmt;
        }

    } //ChildQueryInsertStatement

    private static final class ChildQueryReturningInsertStatement extends QueryInsertStatement<Insert, ReturningInsert>
            implements _PostgreInsert._PostgreChildQueryInsert
            , ReturningInsert, ReturningInsert._ReturningInsertSpec {

        private final QueryInsertStatement<Insert, ReturningInsert> parentStmt;


        private ChildQueryReturningInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , final PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
            if (parentClause.hasReturningClause()) {
                this.parentStmt = new PrimaryQueryReturningInsertStatement(parentClause);
            } else {
                this.parentStmt = new PrimaryQueryInsertStatement(parentClause);
            }
        }

        @Override
        public _PostgreQueryInsert parentStmt() {
            return this.parentStmt;
        }

    } //ChildQueryReturningInsertStatement

    private static final class SubQueryInsertStatement extends QueryInsertStatement<SubInsert, SubReturningInsert>
            implements SubInsert
            , SubInsert._SubInsertSpec {

        private SubQueryInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//SubQueryInsertStatement

    private static final class SubQueryReturningInsertStatement extends QueryInsertStatement<SubInsert, SubReturningInsert>
            implements SubReturningInsert
            , SubReturningInsert._SubReturningInsertSpec {

        private SubQueryReturningInsertStatement(SubComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//SubQueryInsertStatement


}
