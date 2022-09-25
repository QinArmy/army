package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
import io.army.criteria.impl.inner.postgre._PostgreInsert;
import io.army.criteria.postgre.PostgreInsert;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
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



    /*-------------------below insert after values syntax class-------------------*/


    private static final class PrimaryInsertIntoClause<C> extends NonQueryWithCteOption<
            C,
            PostgreInsert._PrimaryNullOptionSpec<C>,
            PostgreInsert._PrimaryPreferLiteralSpec<C>,
            PostgreInsert._PrimaryWithCteSpec<C>,
            SubStatement,
            PostgreInsert._PrimaryInsertIntoClause<C>>
            implements PostgreInsert._PrimaryOptionSpec<C> {

        private PrimaryInsertIntoClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
        }


        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table) {
            return new NonParentComplexValuesClause<>(this, table);
        }

        @Override
        public <P> PostgreInsert._ParentTableAliasSpec<C, P> insertInto(ParentTableMeta<P> table) {
            return null;
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ChildTableMeta<T> table) {
            return new NonParentComplexValuesClause<>(this, table);
        }


    }//PrimaryInsertIntoClause


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
            extends DomainValueClause<C, T, CR, DR, VR>
            implements Statement._AsClause<AR>
            , PostgreInsert._ParentReturningClause<C, CT, I, Q>
            , PostgreInsert._ParentReturningCommaUnaryClause<CT, Q>
            , PostgreInsert._ParentReturningCommaDualClause<CT, Q> {

        private String tableAlias;

        private List<SelectItem> selectItemList;

        private ComplexValuesClause(WithValueSyntaxOptions options, SimpleTableMeta<T> table) {
            super(options, table);
        }

        private ComplexValuesClause(WithValueSyntaxOptions options, ChildTableMeta<T> table) {
            super(options, table);
        }

        @Override
        public final AR as(final @Nullable String alias) {
            if (alias == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.tableAlias = alias;
            return (AR) this;
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
        public DqlStatement.DqlInsertSpec<Q> returning() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning();
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
        public DqlStatement.DqlInsertSpec<Q> returning(Consumer<Consumer<SelectItem>> consumer) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(consumer);
        }

        @Override
        public DqlStatement.DqlInsertSpec<Q> returning(BiConsumer<C, Consumer<SelectItem>> consumer) {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning(consumer);
        }

        @Override
        public I asInsert() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.asInsert();
        }


    }//NonParentStaticValuesLeftParenClause


    private static class NonParentComplexValuesClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
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

        private InsertMode insertMode;

        private String tableAlias;

        private OverridingMode overridingMode;

        private List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private boolean defaultValues;

        private SubQuery subQuery;

        private _PostgreInsert._ConflictActionClauseResult conflictAction;

        private NonParentComplexValuesClause(WithValueSyntaxOptions options, SimpleTableMeta<T> table) {
            super(options, table);
        }

        private NonParentComplexValuesClause(WithValueSyntaxOptions options, ChildTableMeta<T> table) {
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
        public final PostgreInsert._OnConflictSpec<C, T, I, Q> defaultValues() {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.rowPairList = Collections.emptyList();
            this.defaultValues = true;
            this.insertMode = InsertMode.VALUES;
            return this;
        }

        @Override
        public final PostgreInsert._OnConflictSpec<C, T, I, Q> values(final Consumer<PairsConstructor<T>> consumer) {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final DynamicPairsConstructor<T> constructor;
            constructor = new DynamicPairsConstructor<>(this.context, this::validateField);
            consumer.accept(constructor);
            this.rowPairList = constructor.endPairConsumer();
            this.insertMode = InsertMode.VALUES;
            return this;
        }

        @Override
        public final PostgreInsert._OnConflictSpec<C, T, I, Q> values(final BiConsumer<C, PairsConstructor<T>> consumer) {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final DynamicPairsConstructor<T> constructor;
            constructor = new DynamicPairsConstructor<>(this.context, this::validateField);
            consumer.accept(this.criteria, constructor);
            this.rowPairList = constructor.endPairConsumer();
            this.insertMode = InsertMode.VALUES;
            return this;
        }

        @Override
        public final PostgreInsert._ValuesLeftParenClause<C, T, I, Q> values() {
            return new NonParentStaticValuesLeftParenClause<>(this);
        }

        @Override
        public final PostgreInsert._OnConflictSpec<C, T, I, Q> space(final Supplier<? extends SubQuery> supplier) {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final SubQuery subQuery;
            subQuery = supplier.get();
            if (subQuery == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.subQuery = subQuery;
            this.insertMode = InsertMode.QUERY;
            return this;
        }

        @Override
        public final PostgreInsert._OnConflictSpec<C, T, I, Q> space(final Function<C, ? extends SubQuery> function) {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final SubQuery subQuery;
            subQuery = function.apply(this.criteria);
            if (subQuery == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.subQuery = subQuery;
            this.insertMode = InsertMode.QUERY;
            return this;
        }

        @Override
        public final PostgreInsert._NonParentConflictItemClause<C, T, I, Q> onConflict() {
            return new OnConflictClause<>(this);
        }

        @Override
        public final I asInsert() {
            return null;
        }

        @Override
        public final Q asReturningInsert() {
            return null;
        }

        @Override
        public final Void child() {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }

        @Override
        public PostgreInsert._ParentReturningClause<C, Void, I, Q> onConflictClauseEnd(final _PostgreInsert._ConflictActionClauseResult result) {
            if (this.conflictAction != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.conflictAction = result;
            return this;
        }

        @Override
        final PostgreInsert._OnConflictSpec<C, T, I, Q> valuesEnd() {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.insertMode = InsertMode.DOMAIN;
            return this;
        }

        private void staticValuesClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowPairList) {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.rowPairList = rowPairList;
            this.insertMode = InsertMode.VALUES;
        }


    }//NonParentComplexValuesClause


}
