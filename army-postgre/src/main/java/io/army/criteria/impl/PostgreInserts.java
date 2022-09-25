package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
import io.army.criteria.impl.inner.postgre._PostgreInsert;
import io.army.criteria.postgre.PostgreInsert;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

abstract class PostgreInserts extends InsertSupport {

    private PostgreInserts() {
    }


    static <C> PostgreInsert._PrimaryOptionSpec<C> domainInsert(@Nullable C criteria) {
        return new DomainInsertIntoClause<>(criteria);
    }


    private interface PostgreOnConflictSpec<RR> extends CriteriaContextSpec {

        void addConflictTargetItem(_ConflictTargetItem item);

        RR endConflictTarget();

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


    private interface ParentTargetWhereClauseSpec<C, P, CT> extends TargetWhereClauseSpec {

        PostgreInsert._ParentReturningSpec<C, CT> _doNothing(List<_Predicate> predicateList);


        PostgreInsert._ParentDoUpdateSetClause<C, P, CT> _doUpdate(List<_Predicate> predicateList);


    }//ParentTargetWhereClauseSpec

    private static final class ParentConflictAction<C, P, CT> extends InsertSupport.MinWhereClause<
            C,
            PostgreInsert._ParentConflictActionClause<C, P, CT>,
            PostgreInsert._ParentConflictTargetWhereAndSpec<C, P, CT>
            > implements PostgreInsert._ParentConflictTargetWhereSpec<C, P, CT> {

        private final ParentTargetWhereClauseSpec<C, P, CT> clause;

        private ParentConflictAction(ParentTargetWhereClauseSpec<C, P, CT> clause) {
            super(clause.getContext());
            this.clause = clause;
        }

        @Override
        public PostgreInsert._ParentReturningSpec<C, CT> doNothing() {
            return this.clause._doNothing(this.endWhereClause());
        }

        @Override
        public PostgreInsert._ParentDoUpdateSetClause<C, P, CT> doUpdate() {
            return this.clause._doUpdate(this.endWhereClause());
        }


    }//ParentConflictAction

    private static final class ParentConflictTargetItem<C, P, CT>
            extends ConflictTargetItem<
            P,
            PostgreInsert._ParentConflictOpClassSpec<C, P, CT>,
            PostgreInsert._ParentConflictTargetCommaSpec<C, P, CT>>
            implements PostgreInsert._ParentConflictCollateSpec<C, P, CT> {

        private final ParentTargetWhereClauseSpec<C, P, CT> clause;

        private ParentConflictTargetItem(ParentTargetWhereClauseSpec<C, P, CT> clause, IndexFieldMeta<P> indexColumn) {
            super(indexColumn);
            this.clause = clause;
        }

        @Override
        public PostgreInsert._ParentConflictCollateSpec<C, P, CT> comma(IndexFieldMeta<P> indexColumn) {
            final ParentConflictTargetItem<C, P, CT> item = new ParentConflictTargetItem<>(this.clause, indexColumn);
            this.clause.addConflictTargetItem(item);
            return item;
        }

        @Override
        public PostgreInsert._ParentConflictTargetWhereSpec<C, P, CT> rightParen() {
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
        public final PostgreInsert._PostgreChildReturnSpec<CT, Q> returning() {
            this.endDoUpdateSetClause();
            return this.clause.onConflictClauseEnd(this)
                    .returning();
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

        final boolean isOnConflictEnd() {
            final List<ItemPair> itemPairList = this.itemPairList;
            return !(itemPairList == null || itemPairList instanceof ArrayList);
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


    private static final class ParentOnConflictClause<C, P, CT> extends AbstractOnConflictClause<
            C,
            P,
            CT,
            Insert,
            ReturningInsert,
            PostgreInsert._ParentConflictCollateSpec<C, P, CT>,
            PostgreInsert._ParentConflictActionClause<C, P, CT>,
            PostgreInsert._ParentDoUpdateSetClause<C, P, CT>,
            PostgreInsert._ParentDoUpdateWhereSpec<C, P, CT>,
            PostgreInsert._ParentReturningSpec<C, CT>,
            PostgreInsert._ParentDoUpdateWhereAndSpec<C, CT>>
            implements PostgreInsert._ParentConflictItemClause<C, P, CT>
            , PostgreInsert._ParentConflictActionClause<C, P, CT>
            , PostgreInsert._ParentDoUpdateWhereSpec<C, P, CT>
            , PostgreInsert._ParentDoUpdateWhereAndSpec<C, CT>
            , ParentTargetWhereClauseSpec<C, P, CT> {

        private List<_Predicate> indexPredicateList;

        private ParentOnConflictClause(PostgreInsertValuesClauseSpec<C, CT, Insert, ReturningInsert> clause) {
            super(clause);
        }


        @Override
        public PostgreInsert._ParentConflictCollateSpec<C, P, CT> leftParen(IndexFieldMeta<P> indexColumn) {
            final ParentConflictTargetItem<C, P, CT> item;
            item = new ParentConflictTargetItem<>(this, indexColumn);
            this.addConflictTargetItem(item);
            return item;
        }

        @Override
        public CT child() {
            return this.clause.onConflictClauseEnd(this)
                    .child();
        }


        @Override
        public PostgreInsert._ParentReturningSpec<C, CT> _doNothing(List<_Predicate> predicateList) {
            if (this.indexPredicateList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.indexPredicateList = predicateList;
            return this;
        }

        @Override
        public PostgreInsert._ParentDoUpdateSetClause<C, P, CT> _doUpdate(List<_Predicate> predicateList) {
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



    /*-------------------below domain insert syntax class-------------------*/


    private static final class DomainInsertIntoClause<C> extends NonQueryWithCteOption<
            C,
            PostgreInsert._PrimaryNullOptionSpec<C>,
            PostgreInsert._PrimaryPreferLiteralSpec<C>,
            PostgreInsert._PrimaryWithCteSpec<C>,
            SubStatement,
            PostgreInsert._PrimaryInsertIntoClause<C>>
            implements PostgreInsert._PrimaryOptionSpec<C> {

        private DomainInsertIntoClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
        }


        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table) {
            return new DomainInsertIntoValuesClause<>(this, table);
        }

        @Override
        public <P> PostgreInsert._ParentTableAliasSpec<C, P> insertInto(ParentTableMeta<P> table) {
            return null;
        }


    }//DomainInsertIntoClause


    private static abstract class DomainInsertIntoValuesClause<C, T, CT, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends DomainValueClause<
            C,
            T,
            PostgreInsert._OverridingValueSpec<C, T, I, Q>,
            PostgreInsert._ComplexColumnDefaultSpec<C, T, I, Q>,
            PostgreInsert._OnConflictSpec<C, T, I, Q>>
            implements PostgreInsert._TableAliasSpec<C, T, I, Q>
            , PostgreInsert._ParentReturningClause<C, CT, I, Q>
            , PostgreInsert._ParentReturningCommaUnaryClause<CT, Q>
            , PostgreInsert._ParentReturningCommaDualClause<CT, Q> {

        private String tableAlias;

        private List<SelectItem> selectItemList;

        private DomainInsertIntoValuesClause(WithValueSyntaxOptions options, SimpleTableMeta<T> table) {
            super(options, table);
        }

        @Override
        public PostgreInsert._ColumnListSpec<C, T, I, Q> as(final @Nullable String alias) {
            if (alias == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.tableAlias = alias;
            return this;
        }

        @Override
        PostgreInsert._OnConflictSpec<C, T, I, Q> valuesEnd() {
            return null;
        }

        @Override
        public PostgreInsert._OnConflictSpec<C, T, I, Q> defaultValues() {
            return null;
        }

        @Override
        public PostgreInsert._ComplexColumnDefaultSpec<C, T, I, Q> overridingSystemValue() {
            return null;
        }

        @Override
        public PostgreInsert._ComplexColumnDefaultSpec<C, T, I, Q> overridingUserValue() {
            return null;
        }


        @Override
        public final PostgreInsert._PostgreChildReturnSpec<CT, Q> returning() {
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

    }//DomainInsertIntoValuesClause


}
