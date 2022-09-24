package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
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


    static <C> PostgreInsert._DomainOptionSpec<C> domainInsert(@Nullable C criteria) {
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


    private static abstract class AbstractOnConflictClause<C, T, CT, Q extends DqlStatement.DqlInsert, LR, OC, SR, WR, WA, DR, RC>
            extends InsertSupport.MinWhereClause<C, WR, WA>
            implements PostgreInsert._ConflictItemClause<T, LR, OC>
            , Update._SetClause<C, FieldMeta<T>, SR>
            , PostgreInsert._ParentReturningClause<C, DR>
            , PostgreInsert._ReturningClause<RC>
            , PostgreInsert._StaticReturningCommaDualClause<DR>
            , TargetWhereClauseSpec {


        private List<_ConflictTargetItem> targetItemList;
        private String constraintName;

        private List<_Predicate> indexPredicateList;

        private List<ItemPair> itemPairList;

        private List<SelectItem> selectItemList;

        private AbstractOnConflictClause(CriteriaContext context) {
            super(context);
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
        public final void addConflictTargetItem(final _ConflictTargetItem item) {
            List<_ConflictTargetItem> targetItemList = this.targetItemList;
            if (targetItemList == null) {
                this.targetItemList = targetItemList = new ArrayList<>();
            }
            targetItemList.add(item);
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
        public final DR returning() {
            this.endDoUpdateSetClause();

            this.selectItemList = Collections.emptyList();
            return (DR) this;
        }

        @Override
        public final DR returning(SelectItem selectItem) {
            this.endDoUpdateSetClause();

            this.selectItemList = Collections.singletonList(selectItem);
            return (DR) this;
        }

        @Override
        public final DR returning(Consumer<Consumer<SelectItem>> consumer) {
            this.endDoUpdateSetClause();

            consumer.accept(this::addSelectItem);
            return (DR) this;
        }

        @Override
        public final DR returning(BiConsumer<C, Consumer<SelectItem>> consumer) {
            this.endDoUpdateSetClause();

            consumer.accept(this.criteria, this::addSelectItem);
            return (DR) this;
        }

        @Override
        public final SR returning(SelectItem selectItem1, SelectItem selectItem2) {
            this.endDoUpdateSetClause();

            this.addSelectItem(selectItem1);
            this.addSelectItem(selectItem2);
            return (SR) this;
        }


        @Override
        public final DR comma(SelectItem selectItem) {
            this.addSelectItem(selectItem);
            return (DR) this;
        }

        @Override
        public final PostgreInsert._StaticReturningCommaDualClause<RC> comma(SelectItem selectItem1, SelectItem selectItem2) {
            this.addSelectItem(selectItem1);
            this.addSelectItem(selectItem2);
            return this;
        }


        private void addSelectItem(final SelectItem selectItem) {
            List<SelectItem> selectItemList = this.selectItemList;
            if (selectItemList == null) {
                this.selectItemList = selectItemList = new ArrayList<>();
            } else if (!(selectItemList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            selectItemList.add(selectItem);
        }

        private SR addFieldValuePair(FieldMeta<T> field, Expression value) {
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                this.itemPairList = itemPairList = new ArrayList<>();
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
            }
            itemPairList.add(pair);
        }

        private void endDoUpdateSetClause() {
            this.endWhereClause();

            final List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                this.itemPairList = Collections.emptyList();
            } else if (itemPairList instanceof ArrayList) {
                this.itemPairList = _CollectionUtils.unmodifiableList(itemPairList);
            }
        }


    }//AbstractOnConflictClause

    private static final class OnConflictClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends AbstractOnConflictClause<
            C,
            T,
            PostgreInsert._ConflictCollateSpec<C, T, I, Q>,
            PostgreInsert._NonParentConflictActionClause<C, T, I, Q>,
            PostgreInsert._DoUpdateWhereSpec<C, T, I, Q>,
            PostgreInsert._ReturningSpec<C, I, Q>,
            PostgreInsert._DoUpdateWhereAndSpec<C, T, I, Q>,
            DqlStatement.DqlInsertSpec<Q>>

            implements PostgreInsert._NonParentConflictItemClause<C, T, I, Q>
            , PostgreInsert._NonParentConflictActionClause<C, T, I, Q>
            , PostgreInsert._ReturningSpec<C, I, Q>
            , PostgreInsert._DoUpdateWhereSpec<C, T, I, Q>
            , PostgreInsert._DoUpdateWhereAndSpec<C, T, I, Q>
            , DqlStatement.DqlInsertSpec<Q> {

        private OnConflictClause(CriteriaContext context) {
            super(context);
        }

        @Override
        public PostgreInsert._ConflictCollateSpec<C, T, I, Q> leftParen(IndexFieldMeta<T> indexColumn) {
            return null;
        }

        @Override
        public PostgreInsert._ReturningSpec<C, I, Q> doNothing() {
            return null;
        }

        @Override
        public PostgreInsert._DoUpdateSetClause<C, T, I, Q> doUpdate() {
            return null;
        }

        @Override
        public I asInsert() {
            return null;
        }

        @Override
        public Q asReturningInsert() {
            return null;
        }


    }//OnConflictClause



    /*-------------------below domain insert syntax class-------------------*/


    private static final class DomainInsertIntoClause<C> extends NonQueryWithCteOption<
            C,
            PostgreInsert._DomainNullOptionSpec<C>,
            PostgreInsert._DomainPreferLiteralSpec<C>,
            PostgreInsert._DomainWithCteSpec<C>,
            SubStatement,
            PostgreInsert._DomainInsertIntoClause<C>>
            implements PostgreInsert._DomainOptionSpec<C> {

        private DomainInsertIntoClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
        }


        @Override
        public <T> PostgreInsert._DomainTableAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table) {
            return new DomainInsertIntoValuesClause<>(this, table);
        }

        @Override
        public <P> PostgreInsert._DomainParentAliasSpec<C, P> insertInto(ParentTableMeta<P> table) {
            return null;
        }


    }//DomainInsertIntoClause


    private static final class DomainInsertIntoValuesClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends DomainValueClause<
            C,
            T,
            PostgreInsert._DomainOverridingValueSpec<C, T, I, Q>,
            PostgreInsert._DomainColumnDefaultSpec<C, T, I, Q>,
            PostgreInsert._OnConflictSpec<C, T, I, Q>>
            implements PostgreInsert._DomainTableAliasSpec<C, T, I, Q> {

        private String tableAlias;

        private DomainInsertIntoValuesClause(WithValueSyntaxOptions options, SimpleTableMeta<T> table) {
            super(options, table);
        }

        @Override
        public PostgreInsert._DomainColumnListSpec<C, T, I, Q> as(final @Nullable String alias) {
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
        public PostgreInsert._DomainColumnDefaultSpec<C, T, I, Q> overridingSystemValue() {
            return null;
        }

        @Override
        public PostgreInsert._DomainColumnDefaultSpec<C, T, I, Q> overridingUserValue() {
            return null;
        }


    }//DomainInsertIntoValuesClause


}
