package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
import io.army.criteria.impl.inner.postgre._PostgreInsert;
import io.army.criteria.postgre.PostgreInsert;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.struct.CodeEnum;
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
            CriteriaContextStack.setContextStack(this.context);
        }


        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table) {
            return new PrimaryComplexValuesClause<>(this, table);
        }

        @Override
        public <P> PostgreInsert._ParentTableAliasSpec<C, P> insertInto(ParentTableMeta<P> table) {
            return null;
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<C, T, Insert, ReturningInsert> insertInto(ChildTableMeta<T> table) {
            return new PrimaryComplexValuesClause<>(this, table);
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
            final List<? extends SelectItem> effectiveReturningList;
            if (selectItemList == null) {
                effectiveReturningList = Collections.emptyList();
            } else if (selectItemList.size() == 0) {
                effectiveReturningList = this.effectiveFieldList();
            } else {
                effectiveReturningList = _CollectionUtils.unmodifiableList(selectItemList);
            }
            return effectiveReturningList;
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
        public DqlStatement._DqlInsertSpec<Q> returning() {
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
            return null;
        }


    }//PrimaryNonParentComplexValuesClause


    private static final class SubNonParentComplexValuesClause<C, T>
            extends NonParentComplexValuesClause<C, T, SubInsert, SubReturningInsert> {

        private final CodeEnum discriminatorValue;

        private SubNonParentComplexValuesClause(WithValueSyntaxOptions options, SimpleTableMeta<T> table) {
            super(options, table);
            this.discriminatorValue = null;
        }

        private SubNonParentComplexValuesClause(WithValueSyntaxOptions options, ChildTableMeta<T> table) {
            super(options, table);
            this.discriminatorValue = table.discriminatorValue();
        }

        private SubNonParentComplexValuesClause(WithValueSyntaxOptions options, ParentTableMeta<T> table, CodeEnum discriminatorValue) {
            super(options, table);
            this.discriminatorValue = discriminatorValue;
        }

        @Override
        public SubInsert asInsert() {
            return null;
        }

        @Override
        public SubReturningInsert asReturningInsert() {
            return null;
        }


    }//SubNonParentComplexValuesClause


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
        public PostgreInsert._PostgreChildReturnSpec<PostgreInsert._ChildWithCteSpec<C, P>, ReturningInsert> returning() {
            this.clause.staticValuesClauseEnd(this.endValuesClause());
            return this.clause.returning();
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


        private ParentComplexValuesClause(WithValueSyntaxOptions options, ParentTableMeta<P> table) {
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
            final InsertMode mode;
            mode = this.getInsertMode();
            final Insert._InsertSpec spec;
            switch (mode) {
                case DOMAIN:
                    spec = new PrimaryDomainInsertStatement(this, this.domainListForSingle());
                    break;
                case VALUES:
                case QUERY:
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return spec.asInsert();
        }

        @Override
        public ReturningInsert asReturningInsert() {
            return null;
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


    private static abstract class PostgreValueSyntaxInsertStatement<I extends DmlStatement.DmlInsert>
            extends InsertSupport.ValueSyntaxStatement<I>
            implements PostgreInsert, _PostgreInsert {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final String tableAlias;

        private final OverridingMode overridingMode;

        private final _ConflictActionClauseResult conflictAction;


        private PostgreValueSyntaxInsertStatement(final ComplexValuesClause<?, ?, ?, ?, ?, ?, ?, ?, ?> clause) {
            super(clause);

            this.recursive = clause.recursive;
            this.cteList = clause.cteList;
            this.tableAlias = clause.tableAlias;
            this.overridingMode = clause.overridingMode;

            this.conflictAction = clause.conflictAction;
            ;
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
            return Collections.emptyList();
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


    static abstract class DomainInsertStatement<I extends DmlStatement.DmlInsert> extends PostgreValueSyntaxInsertStatement<I>
            implements _PostgreInsert._PostgreDomainInsert {

        final List<?> domainList;

        private DomainInsertStatement(final NonParentComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
            this.domainList = clause.domainListForSingle();
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

    private static final class PrimaryDomainInsertStatement extends DomainInsertStatement<Insert>
            implements Insert, Insert._InsertSpec {

        private PrimaryDomainInsertStatement(PrimaryComplexValuesClause<?, ?> clause) {
            super(clause);
        }

        private PrimaryDomainInsertStatement(ParentComplexValuesClause<?, ?> clause, List<?> domainList) {
            super(clause, domainList);
        }

    }//PrimaryDomainInsertStatement

    private static final class SubDomainInsertStatement extends DomainInsertStatement<SubInsert>
            implements SubInsert {

        private SubDomainInsertStatement(NonParentComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

        private SubDomainInsertStatement(ParentComplexValuesClause<?, ?> clause, List<?> domainList) {
            super(clause, domainList);
        }

    }//SubDomainInsertStatement


    private static final class ChildDomainInsertStatement
            extends DomainInsertStatement<Insert>
            implements _PostgreInsert._PostgreChildDomainInsert
            , Insert, Insert._InsertSpec {

        private final PrimaryDomainInsertStatement parentStmt;

        private ChildDomainInsertStatement(ParentComplexValuesClause<?, ?> parentClause, final NonParentComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
            this.parentStmt = new PrimaryDomainInsertStatement(parentClause, this.domainList);
        }

        @Override
        public _DomainInsert parentStmt() {
            return this.parentStmt;
        }

    }//ChildDomainInsertStatement


    static abstract class ValueInsertStatement<I extends DmlStatement.DmlInsert>
            extends PostgreValueSyntaxInsertStatement<I>
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


    private static class PrimaryValueInsertStatement extends ValueInsertStatement<Insert>
            implements Insert, Insert._InsertSpec {

        private PrimaryValueInsertStatement(ComplexValuesClause<?, ?, ?, ?, ?, ?, ?, ?, ?> clause) {
            super(clause);
        }

    }//PrimaryValueInsertStatement


    private static final class ChildValueInsertStatement extends PrimaryValueInsertStatement
            implements _PostgreInsert._PostgreChildValueInsert {

        private final PrimaryValueInsertStatement parentStmt;

        private ChildValueInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , final NonParentComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
            this.parentStmt = new PrimaryValueInsertStatement(parentClause);
        }

        @Override
        public _PostgreValueInsert parentStmt() {
            return this.parentStmt;
        }


    }//ChildValueInsertStatement


    private static final class SubValueInsertStatement extends ValueInsertStatement<SubInsert>
            implements SubInsert, SubInsert._SubInsertSpec {

        private SubValueInsertStatement(NonParentComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }


    }//SubValueInsertStatement


    static abstract class QueryInsertStatement<I extends DmlStatement.DmlInsert>
            extends InsertSupport.QuerySyntaxInsertStatement<I>
            implements _PostgreInsert._PostgreQueryInsert {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final String tableAlias;

        private final OverridingMode overridingMode;

        private final _ConflictActionClauseResult conflictAction;

        private QueryInsertStatement(ComplexValuesClause<?, ?, ?, ?, ?, ?, ?, ?, ?> clause) {
            super(clause);
            this.recursive = clause.recursive;
            this.cteList = clause.cteList;
            this.tableAlias = clause.tableAlias;
            this.overridingMode = clause.overridingMode;

            this.conflictAction = clause.conflictAction;
            ;
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
            return Collections.emptyList();
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


    private static class PrimaryQueryInsertStatement extends QueryInsertStatement<Insert>
            implements Insert, Insert._InsertSpec {

        private PrimaryQueryInsertStatement(ComplexValuesClause<?, ?, ?, ?, ?, ?, ?, ?, ?> clause) {
            super(clause);
        }

    }//PrimaryQueryInsertStatement


    private static final class ChildQueryInsertStatement extends PrimaryQueryInsertStatement
            implements _PostgreInsert._PostgreChildQueryInsert {

        private final PrimaryQueryInsertStatement parentStmt;


        private ChildQueryInsertStatement(ParentComplexValuesClause<?, ?> parentClause
                , final NonParentComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
            this.parentStmt = new PrimaryQueryInsertStatement(parentClause);
        }

        @Override
        public _PostgreQueryInsert parentStmt() {
            return this.parentStmt;
        }

    } //ChildQueryInsertStatement

    private static final class SubQueryInsertStatement extends QueryInsertStatement<SubInsert>
            implements SubInsert {

        private SubQueryInsertStatement(NonParentComplexValuesClause<?, ?, ?, ?> clause) {
            super(clause);
        }

    }//SubQueryInsertStatement


}
