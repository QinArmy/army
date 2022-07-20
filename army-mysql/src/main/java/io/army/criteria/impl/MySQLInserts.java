package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.dialect._DialectUtils;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
abstract class MySQLInserts extends InsertSupport {

    private MySQLInserts() {
        throw new UnsupportedOperationException();
    }


    static <C> MySQLInsert._DomainOptionSpec<C> domainInsert(@Nullable C criteria) {
        return new DomainInsertOptionClause<>(criteria);
    }

    static <C> MySQLInsert._ValueOptionSpec<C> valueInsert(@Nullable C criteria) {
        return new ValueInsertOptionClause<>(criteria);
    }

    static <C> MySQLInsert._AssignmentOptionSpec<C> assignmentInsert(@Nullable C criteria) {
        return new AssignmentInsertOptionClause<>(criteria);
    }

    static <C> MySQLInsert._QueryInsertIntoSpec<C> queryInsert(@Nullable C criteria) {
        return new QueryInsertIntoClause<>(criteria);
    }


    interface ClauseBeforeRowAlias<RR> extends ColumnListClause {

        /**
         * @param aliasToField a unmodified map,non-empty
         */
        RR rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField);

        /**
         * @param pairList an unmodified list,empty is allowed.
         */
        Insert endInsert(List<_Pair<Object, _Expression>> pairList);

    }


    interface NonParentClauseBeforeRowAlias<C, T extends IDomain>
            extends ClauseBeforeRowAlias<MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T>> {

    }

    interface ParentClauseBeforeRowAlias<C, T extends IDomain, CT>
            extends ClauseBeforeRowAlias<MySQLInsert._ParentOnDuplicateKeyUpdateAliasSpec<C, T, CT>> {

        CT parentStmtEnd(List<_Pair<Object, _Expression>> pairList);
    }


    @SuppressWarnings("unchecked")
    private static abstract class MySQLInsertClause<C, MR, NR, PR, IR> extends NonQueryInsertOptionsImpl<MR, NR, PR>
            implements MySQLInsert._InsertClause<C, IR> {

        private List<Hint> hintList;

        private List<MySQLWords> modifierList;

        private MySQLInsertClause(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }


        @Override
        public final IR insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::insertModifier);
            return (IR) this;
        }

        @Override
        public final IR insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteriaContext.criteria())
                    , MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::insertModifier);
            return (IR) this;
        }


        final List<Hint> hintList() {
            List<Hint> list = this.hintList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        final List<MySQLWords> modifierList() {
            List<MySQLWords> list = this.modifierList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }


    }//InsertClause


    @SuppressWarnings("unchecked")
    private static abstract class AsRowAliasSpec<C, T extends IDomain, RR, UR, DR>
            implements Insert._CommaFieldValuePairClause<C, T, UR>
            , MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, UR>
            , MySQLInsert._StaticOnDuplicateKeyFieldClause<C, T, UR>
            , MySQLInsert._DynamicOnDuplicateKeyUpdateClause<C, PairConsumer<FieldMeta<T>>, DR>
            , PairConsumer<FieldMeta<T>>
            , MySQLInsert._AsRowAliasClause<C, T, RR> {

        final CriteriaContext criteriaContext;

        final C criteria;

        final ClauseBeforeRowAlias<RR> clause;

        private boolean optionalOnDuplicateKey = true;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        private List<_Pair<Object, _Expression>> duplicatePairList;

        private AsRowAliasSpec(ClauseBeforeRowAlias<RR> clause) {
            this.criteriaContext = clause.getCriteriaContext();
            this.criteria = this.criteriaContext.criteria();
            this.clause = clause;
        }

        private AsRowAliasSpec(DomainParentPartitionClause<C, T> clause) {
            this.criteriaContext = clause.criteriaContext;
            this.criteria = clause.criteria;
            this.clause = (ClauseBeforeRowAlias<RR>) this;
        }

        @Override
        public final MySQLInsert._RowColumnAliasListClause<C, T, RR> as(final @Nullable String alias) {
            if (alias == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            return new RowAliasClause<>(alias, this.clause);
        }

        @Override
        public final UR update(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return (UR) this;
        }

        @Override
        public final UR updateLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return (UR) this;
        }

        @Override
        public final UR updateExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return (UR) this;
        }

        @Override
        public final UR updateExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            this.addValuePair(field, function.apply(this.criteria));
            return (UR) this;
        }

        @Override
        public final UR comma(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return (UR) this;
        }

        @Override
        public final UR commaLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return (UR) this;
        }

        @Override
        public final UR commaExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return (UR) this;
        }

        @Override
        public final UR commaExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            this.addValuePair(field, function.apply(this.criteria));
            return (UR) this;
        }

        @Override
        public final MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, UR> onDuplicateKey() {
            this.optionalOnDuplicateKey = false;
            return this;
        }

        @Override
        public final DR onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKey = false;
            consumer.accept(this);
            return (DR) this;
        }

        @Override
        public final DR onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKey = false;
            consumer.accept(this.criteria, this);
            return (DR) this;
        }

        @Override
        public final DR ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKey = true;
            consumer.accept(this);
            return (DR) this;
        }

        @Override
        public final DR ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKey = true;
            consumer.accept(this.criteria, this);
            return (DR) this;
        }

        @Override
        public final PairConsumer<FieldMeta<T>> accept(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public final PairConsumer<FieldMeta<T>> acceptLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public final PairConsumer<FieldMeta<T>> acceptExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return this;
        }


        final List<_Pair<Object, _Expression>> endDuplicateKeyClause() {
            List<_Pair<Object, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                if (!this.optionalOnDuplicateKey) {
                    String m = "You don't add any field and value pair";
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
                }
                pairList = Collections.emptyList();
            } else if (pairList instanceof ArrayList) {
                pairList = Collections.unmodifiableList(pairList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return pairList;
        }

        private void addValuePair(final FieldMeta<T> field, final @Nullable Expression value) {

            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }

            this.clause.validateField(field, (ArmyExpression) value);

            Map<FieldMeta<?>, Boolean> filedMap = this.fieldMap;
            if (filedMap == null) {
                filedMap = new HashMap<>();
                this.fieldMap = filedMap;
            }
            if (filedMap.putIfAbsent(field, Boolean.TRUE) != null) {
                throw duplicationValuePair(this.criteriaContext, field);
            }

            List<_Pair<Object, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                pairList = new ArrayList<>();
                this.duplicatePairList = pairList;
            }
            pairList.add(_Pair.create(field, (ArmyExpression) value));
        }


    }//AsRowAliasSpec


    /**
     * <p>
     * This class is a implementation of {@link MySQLInsert._AsRowAliasSpec}
     * </p>
     */
    private static final class NonParentAsRowAliasSpec<C, T extends IDomain>
            extends AsRowAliasSpec<
            C,
            T,
            MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T>,
            MySQLInsert._StaticAssignmentCommaFieldSpec<C, T>,
            Insert._InsertSpec>
            implements MySQLInsert._AsRowAliasSpec<C, T>
            , MySQLInsert._StaticAssignmentCommaFieldSpec<C, T> {


        private NonParentAsRowAliasSpec(NonParentClauseBeforeRowAlias<C, T> clause) {
            super(clause);
        }


        @Override
        public Insert asInsert() {
            return this.clause.endInsert(this.endDuplicateKeyClause());
        }


    }//NonParentAsRowAliasSpec


    private static final class ParentAsRowAliasSpec<C, P extends IDomain, CT>
            extends AsRowAliasSpec<
            C,
            P,
            MySQLInsert._ParentOnDuplicateKeyUpdateAliasSpec<C, P, CT>,
            MySQLInsert._ParentStaticAssignmentCommaFieldSpec<C, P, CT>,
            MySQLInsert._MySQLChildSpec<CT>>
            implements MySQLInsert._ParentAsRowAliasSpec<C, P, CT>
            , MySQLInsert._ParentStaticAssignmentCommaFieldSpec<C, P, CT> {

        private ParentAsRowAliasSpec(ParentClauseBeforeRowAlias<C, P, CT> clause) {
            super(clause);
        }

        @Override
        public CT child() {
            return ((ParentClauseBeforeRowAlias<C, P, CT>) this.clause).parentStmtEnd(this.endDuplicateKeyClause());
        }

        @Override
        public Insert asInsert() {
            return this.clause.endInsert(this.endDuplicateKeyClause());
        }

    }//ParentAsRowAliasSpec


    private static final class DomainSingleParentAsRowAliasSpec<C, T extends IDomain>
            extends AsRowAliasSpec<
            C,
            T,
            MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T>,
            MySQLInsert._StaticAssignmentCommaFieldSpec<C, T>,
            Insert._InsertSpec>
            implements MySQLInsert._AsRowAliasSpec<C, T>
            , MySQLInsert._StaticAssignmentCommaFieldSpec<C, T>
            , NonParentClauseBeforeRowAlias<C, T> {


        private final DomainParentPartitionClause<C, T> domainClause;

        private DomainSingleParentAsRowAliasSpec(DomainParentPartitionClause<C, T> clause) {
            super(clause);
            this.domainClause = clause;
        }

        @Override
        public Insert asInsert() {
            return this.domainClause.endInsert(this.endDuplicateKeyClause());
        }

        @Override
        public CriteriaContext getCriteriaContext() {
            return this.domainClause.criteriaContext;
        }

        @Override
        public void validateField(FieldMeta<?> field, @Nullable ArmyExpression value) {
            this.domainClause.validateField(field, value);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T> rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField) {
            return this.domainClause.parentRowAliasEnd(rowAlias, aliasToField);
        }

        @Override
        public Insert endInsert(List<_Pair<Object, _Expression>> pairList) {
            return this.domainClause.endInsert(pairList);
        }


    }//DomainSingleParentAsRowAliasSpec


    private static final class DomainParentAsRowAliasSpec<C, P extends IDomain>
            extends AsRowAliasSpec<
            C,
            P,
            MySQLInsert._DomainParentOnDuplicateKeyUpdateAliasSpec<C, P>,
            MySQLInsert._DomainParentStaticAssignmentCommaFieldSpec<C, P>,
            MySQLInsert._DomainChildClause<C, P>>
            implements MySQLInsert._DomainParentAsRowAliasSpec<C, P>
            , MySQLInsert._DomainParentStaticAssignmentCommaFieldSpec<C, P> {

        private DomainParentAsRowAliasSpec(DomainParentPartitionClause<C, P> clause) {
            super(clause);
        }

        @Override
        public MySQLInsert._DomainChildInsertIntoSpec<C, P> child() {
            return ((DomainParentPartitionClause<C, P>) this.clause).parentStmtEnd(this.endDuplicateKeyClause());
        }


    }//DomainParentAsRowAliasSpec


    private static final class RowAliasClause<C, T extends IDomain, RR>
            implements MySQLInsert._RowColumnAliasListClause<C, T, RR>
            , Statement._RightParenClause<RR>
            , MySQLInsert._ColumnAliasClause<T, RR> {

        private final CriteriaContext criteriaContext;

        private final String rowAlias;

        private final ClauseBeforeRowAlias<RR> clause;

        private Map<FieldMeta<?>, Boolean> fieldMap = new HashMap<>();

        private Map<String, FieldMeta<?>> aliasToField = new HashMap<>();

        private RowAliasClause(String rowAlias, ClauseBeforeRowAlias<RR> clause) {
            this.criteriaContext = clause.getCriteriaContext();
            this.rowAlias = rowAlias;
            this.clause = clause;
        }


        @Override
        public Statement._RightParenClause<RR> leftParen(Consumer<BiConsumer<FieldMeta<T>, String>> consumer) {
            consumer.accept(this::addFieldAlias);
            return this;
        }

        @Override
        public Statement._RightParenClause<RR> leftParen(BiConsumer<C, BiConsumer<FieldMeta<T>, String>> consumer) {
            consumer.accept(this.criteriaContext.criteria(), this::addFieldAlias);
            return this;
        }

        @Override
        public MySQLInsert._ColumnAliasClause<T, RR> leftParen(FieldMeta<T> field, String columnAlias) {
            this.addFieldAlias(field, columnAlias);
            return this;
        }

        @Override
        public MySQLInsert._ColumnAliasClause<T, RR> comma(FieldMeta<T> field, String columnAlias) {
            this.addFieldAlias(field, columnAlias);
            return this;
        }

        @Override
        public RR rightParen() {
            Map<String, FieldMeta<?>> aliasToField = this.aliasToField;
            if (aliasToField.size() == 0) {
                String m = "You use row alias clause but don't add any column alias.";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (aliasToField instanceof HashMap) {
                aliasToField = Collections.unmodifiableMap(aliasToField);
                this.aliasToField = aliasToField;
                this.fieldMap = null;
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this.clause.rowAliasEnd(this.rowAlias, aliasToField);
        }

        private void addFieldAlias(final FieldMeta<T> field, final @Nullable String columnAlias) {
            this.clause.validateField(field, null);

            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (fieldMap.putIfAbsent(field, Boolean.TRUE) != null) {
                String m = String.format("duplication column alias[%s] for %s", columnAlias, field);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            if (columnAlias == null) {
                String m = String.format("%s columnAlis is null", field);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }

            if (this.aliasToField.putIfAbsent(columnAlias, field) != null) {
                String m = String.format("column alis[%s] duplication", columnAlias);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }


        }


    }//RowAliasClause

    @SuppressWarnings("unchecked")
    private static abstract class DuplicateKeyUpdateAliasSpec<C, T extends IDomain, UR, DR>
            implements MySQLInsert._StaticOnDuplicateKeyAliasClause<C, T, UR>
            , MySQLInsert._StaticOnDuplicateKeyAliasUpdateClause<C, T, UR>
            , MySQLInsert._DynamicOnDuplicateKeyUpdateClause<C, AliasColumnConsumer<FieldMeta<T>>, DR>
            , Insert._CommaFieldValuePairClause<C, T, UR>
            , MySQLInsert._CommaAliasValuePairClause<C, UR>
            , AliasColumnConsumer<FieldMeta<T>> {

        private final CriteriaContext criteriaContext;

        private final C criteria;

        final ClauseBeforeRowAlias<?> clause;

        private final Map<String, FieldMeta<?>> aliasToField;

        private List<_Pair<Object, _Expression>> duplicatePairList;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        private boolean optionalOnDuplicateKeyClause;

        private DuplicateKeyUpdateAliasSpec(ClauseBeforeRowAlias<?> clause, Map<String, FieldMeta<?>> aliasToField) {
            this.criteriaContext = clause.getCriteriaContext();
            this.criteria = this.criteriaContext.criteria();
            this.clause = clause;
            this.aliasToField = aliasToField;
        }


        @Override
        public final MySQLInsert._StaticOnDuplicateKeyAliasUpdateClause<C, T, UR> onDuplicateKey() {
            this.optionalOnDuplicateKeyClause = false;
            return this;
        }

        @Override
        public final DR onDuplicateKeyUpdate(Consumer<AliasColumnConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKeyClause = false;
            consumer.accept(this);
            return (DR) this;
        }

        @Override
        public final DR onDuplicateKeyUpdate(BiConsumer<C, AliasColumnConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKeyClause = false;
            consumer.accept(this.criteria, this);
            return (DR) this;
        }

        @Override
        public final DR ifOnDuplicateKeyUpdate(Consumer<AliasColumnConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKeyClause = true;
            consumer.accept(this);
            return (DR) this;
        }

        @Override
        public final DR ifOnDuplicateKeyUpdate(BiConsumer<C, AliasColumnConsumer<FieldMeta<T>>> consumer) {
            this.optionalOnDuplicateKeyClause = true;
            consumer.accept(this.criteria, this);
            return (DR) this;
        }

        @Override
        public final UR update(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return (UR) this;
        }

        @Override
        public final UR updateLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return (UR) this;
        }

        @Override
        public final UR updateExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return (UR) this;
        }

        @Override
        public final UR updateExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            this.addValuePair(field, function.apply(this.criteria));
            return (UR) this;
        }

        @Override
        public final UR update(String columnAlias, @Nullable Object value) {
            this.addValuePair(columnAlias, SQLs._nullableParam(this.mapToFiled(columnAlias), value));
            return (UR) this;
        }

        @Override
        public final UR updateLiteral(String columnAlias, @Nullable Object value) {
            this.addValuePair(columnAlias, SQLs._nullableLiteral(this.mapToFiled(columnAlias), value));
            return (UR) this;
        }

        @Override
        public final UR updateExp(String columnAlias, Supplier<? extends Expression> supplier) {
            this.addValuePair(columnAlias, supplier.get());
            return (UR) this;
        }

        @Override
        public final UR updateExp(String columnAlias, Function<C, ? extends Expression> function) {
            this.addValuePair(columnAlias, function.apply(this.criteria));
            return (UR) this;
        }

        @Override
        public final UR comma(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return (UR) this;
        }

        @Override
        public final UR commaLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nonNullLiteral(field, value));
            return (UR) this;
        }

        @Override
        public final UR commaExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return (UR) this;
        }

        @Override
        public final UR commaExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            this.addValuePair(field, function.apply(this.criteria));
            return (UR) this;
        }

        @Override
        public final UR comma(String columnAlias, @Nullable Object value) {
            this.addValuePair(columnAlias, SQLs._nullableParam(this.mapToFiled(columnAlias), value));
            return (UR) this;
        }

        @Override
        public final UR commaLiteral(String columnAlias, @Nullable Object value) {
            this.addValuePair(columnAlias, SQLs._nullableLiteral(this.mapToFiled(columnAlias), value));
            return (UR) this;
        }

        @Override
        public final UR commaExp(String columnAlias, Supplier<? extends Expression> supplier) {
            this.addValuePair(columnAlias, supplier.get());
            return (UR) this;
        }

        @Override
        public final UR commaExp(String columnAlias, Function<C, ? extends Expression> function) {
            this.addValuePair(columnAlias, function.apply(this.criteria));
            return (UR) this;
        }

        @Override
        public final AliasColumnConsumer<FieldMeta<T>> accept(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public final AliasColumnConsumer<FieldMeta<T>> acceptLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nonNullLiteral(field, value));
            return this;
        }

        @Override
        public final AliasColumnConsumer<FieldMeta<T>> acceptExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return this;
        }

        @Override
        public final AliasColumnConsumer<FieldMeta<T>> accept(String columnAlias, @Nullable Object value) {
            this.addValuePair(columnAlias, SQLs._nullableParam(this.mapToFiled(columnAlias), value));
            return this;
        }

        @Override
        public final AliasColumnConsumer<FieldMeta<T>> acceptLiteral(String columnAlias, @Nullable Object value) {
            this.addValuePair(columnAlias, SQLs._nullableLiteral(this.mapToFiled(columnAlias), value));
            return this;
        }

        @Override
        public final AliasColumnConsumer<FieldMeta<T>> acceptExp(String columnAlias, Supplier<? extends Expression> supplier) {
            this.addValuePair(columnAlias, supplier.get());
            return this;
        }


        final List<_Pair<Object, _Expression>> endDuplicateKeyClause() {
            List<_Pair<Object, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                if (!this.optionalOnDuplicateKeyClause) {
                    String m = "You use non-ifOnDuplicateKeyUpdate  but don't add any filed value pair";
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
                }
                pairList = Collections.emptyList();
            } else if (pairList instanceof ArrayList) {
                pairList = _CollectionUtils.unmodifiableList(pairList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return pairList;
        }


        private FieldMeta<?> mapToFiled(String columnAlias) {
            final FieldMeta<?> field;
            field = this.aliasToField.get(columnAlias);
            if (field == null) {
                throw this.unknownColumnAlias(columnAlias);
            }
            return field;
        }


        private void addValuePair(final Object fieldOrAlias, final @Nullable Expression value) {
            //1. validate value
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }

            //2. get fieldMap
            Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                fieldMap = new HashMap<>();
                this.fieldMap = fieldMap;
            }
            //3. validate field
            final FieldMeta<?> field;
            if (fieldOrAlias instanceof FieldMeta) {
                field = (FieldMeta<?>) fieldOrAlias;
                this.clause.validateField(field, (ArmyExpression) value);
                if (fieldMap.putIfAbsent(field, Boolean.TRUE) != null) {
                    throw duplicationValuePair(this.criteriaContext, field);
                }
            } else if (fieldOrAlias instanceof String) {
                field = this.aliasToField.get((String) fieldOrAlias);
                if (field == null) {
                    throw this.unknownColumnAlias((String) fieldOrAlias);
                }
                if (fieldMap.putIfAbsent(field, Boolean.TRUE) != null) {
                    String m = String.format("duplication value pair for column alias[%s]", fieldOrAlias);
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
                }
            } else {
                //no bug,never here
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "unknown key type");
            }

            //4.get duplicatePairList
            List<_Pair<Object, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                pairList = new ArrayList<>();
                this.duplicatePairList = pairList;
            }
            //5. add pair
            pairList.add(_Pair.create(fieldOrAlias, (ArmyExpression) value));

        }

        private CriteriaException unknownColumnAlias(@Nullable String columnAlias) {
            String m = String.format("unknown column alias[%s]", columnAlias);
            return CriteriaContextStack.criteriaError(this.criteriaContext, m);
        }


    }//DuplicateKeyUpdateAliasSpec


    private static final class NonParentDuplicateKeyUpdateAliasSpec<C, T extends IDomain>
            extends DuplicateKeyUpdateAliasSpec<C, T, MySQLInsert._StaticCommaAliasValuePairSpec<C, T>, Insert._InsertSpec>
            implements MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T>
            , MySQLInsert._StaticCommaAliasValuePairSpec<C, T> {


        private NonParentDuplicateKeyUpdateAliasSpec(ClauseBeforeRowAlias<?> clause
                , Map<String, FieldMeta<?>> aliasToField) {
            super(clause, aliasToField);
        }

        @Override
        public Insert asInsert() {
            return this.clause.endInsert(this.endDuplicateKeyClause());
        }


    }//NonParentDuplicateKeyUpdateAliasSpec

    private static final class ParentDuplicateKeyUpdateAliasSpec<C, P extends IDomain, CT>
            extends DuplicateKeyUpdateAliasSpec<C, P, MySQLInsert._ParentStaticCommaAliasValuePairSpec<C, P, CT>, MySQLInsert._MySQLChildSpec<CT>>
            implements MySQLInsert._ParentStaticCommaAliasValuePairSpec<C, P, CT>
            , MySQLInsert._ParentOnDuplicateKeyUpdateAliasSpec<C, P, CT> {

        private ParentDuplicateKeyUpdateAliasSpec(ParentClauseBeforeRowAlias<C, P, CT> clause
                , Map<String, FieldMeta<?>> aliasToField) {
            super(clause, aliasToField);
        }


        @Override
        public Insert asInsert() {
            return this.clause.endInsert(this.endDuplicateKeyClause());
        }

        @SuppressWarnings("unchecked")
        @Override
        public CT child() {
            return ((ParentClauseBeforeRowAlias<C, P, CT>) this.clause).parentStmtEnd(this.endDuplicateKeyClause());
        }


    }//ParentDuplicateKeyUpdateAliasSpec


    private static final class DomainParentDuplicateKeyUpdateAliasSpec<C, P extends IDomain>
            extends DuplicateKeyUpdateAliasSpec<C, P, MySQLInsert._DomainParentStaticCommaAliasValuePairSpec<C, P>, MySQLInsert._DomainChildClause<C, P>>
            implements MySQLInsert._DomainParentStaticCommaAliasValuePairSpec<C, P>
            , MySQLInsert._DomainParentOnDuplicateKeyUpdateAliasSpec<C, P> {

        private DomainParentDuplicateKeyUpdateAliasSpec(DomainParentPartitionClause<C, P> clause
                , Map<String, FieldMeta<?>> aliasToField) {
            super(clause, aliasToField);
        }

        @SuppressWarnings("unchecked")
        @Override
        public MySQLInsert._DomainChildInsertIntoSpec<C, P> child() {
            return ((DomainParentPartitionClause<C, P>) this.clause).parentStmtEnd(this.endDuplicateKeyClause());
        }


    }//DomainParentDuplicateKeyUpdateAliasSpec





    /*-------------------below domain insert syntax classes-------------------*/

    private static final class DomainInsertOptionClause<C>
            extends MySQLInsertClause<
            C,
            MySQLInsert._DomainNullOptionSpec<C>,
            MySQLInsert._DomainPreferLiteralSpec<C>,
            MySQLInsert._DomainInsertIntoSpec<C>,
            MySQLInsert._DomainIntoClause<C>>
            implements MySQLInsert._DomainOptionSpec<C>, MySQLInsert._DomainIntoClause<C> {


        private DomainInsertOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new DomainPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new DomainParentPartitionClause<>(this, table);
        }


        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new DomainPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new DomainParentPartitionClause<>(this, table);
        }


    }//DomainOptionClause


    private static final class DomainPartitionClause<C, T extends IDomain>
            extends InsertSupport.DomainValueClause<
            C,
            T,
            MySQLInsert._DomainDefaultSpec<C, T>,
            MySQLInsert._AsRowAliasSpec<C, T>>
            implements MySQLInsert._DomainPartitionSpec<C, T>
            , NonParentClauseBeforeRowAlias<C, T> {

        private final _Insert._DomainInsert parentStmt;

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private DomainPartitionClause(DomainInsertOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentStmt = null;

        }

        private DomainPartitionClause(DomainParentPartitionClause<C, ?> parentClause, ChildTableMeta<T> table) {
            super(parentClause, table);
            this.hintList = _CollectionUtils.safeList(parentClause.childHintList);
            this.modifierList = _CollectionUtils.safeList(parentClause.childModifierList);
            this.parentStmt = parentClause.createParentStmt(); //couldn't invoke asInsert method
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._DomainColumnListSpec<C, T>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }


        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T> rowAliasEnd(final String rowAlias
                , final Map<String, FieldMeta<?>> aliasToField) {
            if (this.rowAlias != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowAlias = rowAlias;
            this.aliasToField = aliasToField;
            return new NonParentDuplicateKeyUpdateAliasSpec<>(this, aliasToField);
        }

        @Override
        public Insert endInsert(final List<_Pair<Object, _Expression>> pairList) {
            final Insert._InsertSpec spec;
            if (pairList.size() == 0) {
                if (this.parentStmt == null) {
                    spec = new DomainInsertStatement(this);
                } else {
                    spec = new DomainChildInsertStatement(this);
                }
            } else if (this.rowAlias == null) {
                if (this.parentStmt == null) {
                    spec = new DomainInsertWithDuplicateKey(this, pairList);
                } else {
                    spec = new DomainChildInsertWithDuplicateKey(this, pairList);
                }
            } else if (this.parentStmt == null) {
                spec = new DomainInsertWIthRowAlias(this, pairList);
            } else {
                spec = new DomainChildInsertWIthRowAlias(this, pairList);
            }
            return spec.asInsert();
        }


        @Override
        MySQLInsert._DomainDefaultSpec<C, T> columnListEnd() {
            return this;
        }

        @Override
        MySQLInsert._AsRowAliasSpec<C, T> valuesEnd() {
            return new NonParentAsRowAliasSpec<>(this);
        }


        private MySQLInsert._DomainColumnListSpec<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

    }//DomainPartitionClause


    private static final class DomainParentPartitionClause<C, P extends IDomain>
            extends InsertSupport.DomainValueClause<
            C,
            P,
            MySQLInsert._DomainParentDefaultSpec<C, P>,
            MySQLInsert._AsRowAliasSpec<C, P>>
            implements MySQLInsert._DomainParentPartitionSpec<C, P>
            , ClauseBeforeRowAlias<MySQLInsert._DomainParentOnDuplicateKeyUpdateAliasSpec<C, P>>
            , MySQLInsert._DomainChildClause<C, P>
            , MySQLInsert._DomainChildInsertIntoSpec<C, P>
            , MySQLInsert._DomainChildIntoClause<C, P>
            , InsertOptions {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private List<_Pair<Object, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLWords> childModifierList;

        private DomainParentPartitionClause(DomainInsertOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public MySQLInsert._RowColumnAliasListClause<C, P, MySQLInsert._DomainParentOnDuplicateKeyUpdateAliasSpec<C, P>> as(final @Nullable String alias) {
            if (alias == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            return new RowAliasClause<>(alias, this);
        }


        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, P, MySQLInsert._DomainParentStaticAssignmentCommaFieldSpec<C, P>> onDuplicateKey() {
            return new DomainParentAsRowAliasSpec<>(this)
                    .onDuplicateKey();
        }

        @Override
        public MySQLInsert._DomainChildClause<C, P> onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<P>>> consumer) {
            return new DomainParentAsRowAliasSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._DomainChildClause<C, P> onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<P>>> consumer) {
            return new DomainParentAsRowAliasSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._DomainChildClause<C, P> ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<P>>> consumer) {
            return new DomainParentAsRowAliasSpec<>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._DomainChildClause<C, P> ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<P>>> consumer) {
            return new DomainParentAsRowAliasSpec<>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._DomainParentColumnsSpec<C, P>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._DomainChildInsertIntoSpec<C, P> child() {
            return this.parentStmtEnd(Collections.emptyList());
        }

        @Override
        public MySQLInsert._DomainChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public MySQLInsert._DomainChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final DomainPartitionClause<C, T> childClause;
            childClause = new DomainPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        MySQLInsert._DomainParentDefaultSpec<C, P> columnListEnd() {
            return this;
        }

        @Override
        MySQLInsert._AsRowAliasSpec<C, P> valuesEnd() {
            return new DomainSingleParentAsRowAliasSpec<>(this);
        }

        @Override
        public MySQLInsert._DomainParentOnDuplicateKeyUpdateAliasSpec<C, P> rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField) {
            if (this.rowAlias != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowAlias = rowAlias;
            this.aliasToField = aliasToField;
            return new DomainParentDuplicateKeyUpdateAliasSpec<>(this, aliasToField);
        }

        @Override
        public Insert endInsert(final List<_Pair<Object, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this.createParentStmt().asInsert();
        }

        private MySQLInsert._DomainChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<Object, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this;
        }

        private MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, P> parentRowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField) {
            if (this.rowAlias != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowAlias = rowAlias;
            this.aliasToField = aliasToField;
            return new NonParentDuplicateKeyUpdateAliasSpec<>(this, aliasToField);
        }

        private MySQLInsert._DomainParentColumnsSpec<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


        private DomainInsertStatement createParentStmt() {
            final List<_Pair<Object, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final DomainInsertStatement statement;
            if (pairList.size() == 0) {
                statement = new DomainInsertStatement(this);
            } else if (this.rowAlias == null) {
                statement = new DomainInsertWithDuplicateKey(this, pairList);
            } else {
                statement = new DomainInsertWIthRowAlias(this, pairList);
            }
            return statement;
        }


    }//DomainParentPartitionClause


    static abstract class MySQLValueSyntaxStatement extends InsertSupport.ValueSyntaxStatement<Insert>
            implements MySQLInsert, _MySQLInsert, Insert._InsertSpec {


        private MySQLValueSyntaxStatement(_ValuesSyntaxInsert clause) {
            super(clause);

        }

        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//MySQLValueSyntaxStatement


    private static class DomainInsertStatement extends MySQLValueSyntaxStatement
            implements _MySQLInsert._MySQLDomainInsert {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;
        private final List<IDomain> domainList;

        private DomainInsertStatement(DomainPartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.domainList = clause.domainList();
        }

        private DomainInsertStatement(DomainParentPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.domainList = clause.domainList();
        }

        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLWords> modifierList() {
            return this.modifierList;
        }


        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<IDomain> domainList() {
            return this.domainList;
        }

    }//MySQLDomainInsertStatement


    private static class DomainInsertWithDuplicateKey extends DomainInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {


        private final List<_Pair<Object, _Expression>> pairList;

        private DomainInsertWithDuplicateKey(DomainPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        private DomainInsertWithDuplicateKey(DomainParentPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        @Override
        public final List<_Pair<Object, _Expression>> duplicatePairList() {
            return this.pairList;
        }


    }//MySQLDomainInsertWithDuplicateKey


    private static class DomainInsertWIthRowAlias extends DomainInsertWithDuplicateKey
            implements _MySQLInsert._InsertWithRowAlias {

        private final String rowAlias;

        private final Map<String, FieldMeta<?>> aliasToField;

        private DomainInsertWIthRowAlias(DomainPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause, pairList);
            this.rowAlias = clause.rowAlias;
            this.aliasToField = clause.aliasToField;
            assert this.rowAlias != null && this.aliasToField != null;
        }

        private DomainInsertWIthRowAlias(DomainParentPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause, pairList);
            this.rowAlias = clause.rowAlias;
            this.aliasToField = clause.aliasToField;
        }

        @Override
        public String rowAlias() {
            return this.rowAlias;
        }

        @Override
        public Map<String, FieldMeta<?>> aliasToField() {
            return this.aliasToField;
        }


    }//MySQLDomainInsertWIthRowAlias


    private static class DomainChildInsertStatement extends DomainInsertStatement
            implements _Insert._ChildDomainInsert {

        private final _Insert._DomainInsert parentStmt;

        private DomainChildInsertStatement(DomainPartitionClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public final _DomainInsert parentStmt() {
            return this.parentStmt;
        }

    }//MySQLDomainChildInsertStatement


    private static class DomainChildInsertWithDuplicateKey extends DomainChildInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<Object, _Expression>> pairList;

        private DomainChildInsertWithDuplicateKey(DomainPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        @Override
        public final List<_Pair<Object, _Expression>> duplicatePairList() {
            return this.pairList;
        }

    }//MySQLDomainChildInsertWithDuplicateKey


    private static final class DomainChildInsertWIthRowAlias extends DomainChildInsertWithDuplicateKey
            implements _MySQLInsert._InsertWithRowAlias {

        private final String rowAlias;

        private final Map<String, FieldMeta<?>> aliasToField;

        private DomainChildInsertWIthRowAlias(DomainPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause, pairList);
            this.rowAlias = clause.rowAlias;
            this.aliasToField = clause.aliasToField;
            assert this.rowAlias != null;
        }

        @Override
        public String rowAlias() {
            return this.rowAlias;
        }

        @Override
        public Map<String, FieldMeta<?>> aliasToField() {
            return this.aliasToField;
        }

    }//MySQLDomainChildInsertWIthRowAlias



    /*-------------------below value insert syntax classes  -------------------*/


    private static final class ValueInsertOptionClause<C>
            extends MySQLInsertClause<
            C,
            MySQLInsert._ValueNullOptionSpec<C>,
            MySQLInsert._ValuePreferLiteralSpec<C>,
            MySQLInsert._ValueInsertIntoSpec<C>,
            MySQLInsert._ValueIntoClause<C>>
            implements MySQLInsert._ValueOptionSpec<C>, MySQLInsert._ValueIntoClause<C>, InsertOptions {

        private ValueInsertOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValuePartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new ValuePartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValueParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new ValueParentPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValuePartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new ValuePartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValueParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new ValueParentPartitionClause<>(this, table);
        }

    }//ValueOptionClause


    private static final class StaticValuesLeftParenClause<C, T extends IDomain>
            extends InsertSupport.StaticColumnValuePairClause<C, T, MySQLInsert._ValueStaticValuesLeftParenSpec<C, T>>
            implements MySQLInsert._ValueStaticValuesLeftParenSpec<C, T> {

        private final ValuePartitionClause<C, T> clause;

        private StaticValuesLeftParenClause(ValuePartitionClause<C, T> clause) {
            super(clause.getCriteriaContext(), clause::validateField);
            this.clause = clause;
        }

        @Override
        public MySQLInsert._ValueStaticValuesLeftParenSpec<C, T> rightParen() {
            this.endCurrentRow();
            return this;
        }


        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, MySQLInsert._StaticAssignmentCommaFieldSpec<C, T>> onDuplicateKey() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKey();
        }


        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._RowColumnAliasListClause<C, T, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T>> as(String alias) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .as(alias);
        }

        @Override
        public Insert asInsert() {
            return this.clause.valueClauseEndBeforeAs(this.endValuesClause());
        }

    }//StaticValuesLeftParenClause

    private static final class ParentStaticValuesLeftParenClause<C, T extends IDomain>
            extends InsertSupport.StaticColumnValuePairClause<C, T, MySQLInsert._ValueParentStaticValueLeftParenSpec<C, T>>
            implements MySQLInsert._ValueParentStaticValueLeftParenSpec<C, T> {

        private final ValueParentPartitionClause<C, T> clause;

        private ParentStaticValuesLeftParenClause(ValueParentPartitionClause<C, T> clause) {
            super(clause.getCriteriaContext(), clause::validateField);
            this.clause = clause;
        }

        @Override
        public MySQLInsert._ValueParentStaticValueLeftParenSpec<C, T> rightParen() {
            this.endCurrentRow();
            return this;
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, MySQLInsert._ParentStaticAssignmentCommaFieldSpec<C, T, MySQLInsert._ValueChildInsertIntoSpec<C, T>>> onDuplicateKey() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKey();
        }


        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._ValueChildInsertIntoSpec<C, T>> ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._RowColumnAliasListClause<C, T, MySQLInsert._ParentOnDuplicateKeyUpdateAliasSpec<C, T, MySQLInsert._ValueChildInsertIntoSpec<C, T>>> as(String alias) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .as(alias);
        }


        @Override
        public Insert asInsert() {
            return this.clause.valueClauseEndBeforeAs(this.endValuesClause());
        }

        @Override
        public MySQLInsert._ValueChildInsertIntoSpec<C, T> child() {
            return this.clause.child();
        }


    }//ParentStaticValuesLeftParenClause


    private static final class ValuePartitionClause<C, T extends IDomain>
            extends DynamicValueInsertValueClause<
            C,
            T,
            MySQLInsert._ValueDefaultSpec<C, T>,
            MySQLInsert._AsRowAliasSpec<C, T>>
            implements MySQLInsert._ValuePartitionSpec<C, T>
            , NonParentClauseBeforeRowAlias<C, T> {

        private final _Insert._ValuesInsert parentStmt;

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private ValuePartitionClause(ValueInsertOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentStmt = null;
        }

        private ValuePartitionClause(ValueParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentStmt = clause.createParentStmt();//couldn't invoke asInsert method
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._ValueColumnListSpec<C, T>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._ValueStaticValuesLeftParenClause<C, T> values() {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new StaticValuesLeftParenClause<>(this);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T> rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField) {
            if (this.rowValuesList == null || this.rowAlias != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowAlias = rowAlias;
            this.aliasToField = aliasToField;
            return new NonParentDuplicateKeyUpdateAliasSpec<>(this, aliasToField);
        }

        @Override
        MySQLInsert._ValueDefaultSpec<C, T> columnListEnd() {
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<Object, _Expression>> pairList) {
            if (this.rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final Insert._InsertSpec spec;
            if (pairList.size() == 0) {
                if (this.parentStmt == null) {
                    spec = new MySQLValueInsertStatement(this);
                } else {
                    spec = new MySQLValueChildInsertStatement(this);
                }
            } else if (this.rowAlias == null) {
                if (this.parentStmt == null) {
                    spec = new MySQLValueInsertWithDuplicateKey(this, pairList);
                } else {
                    spec = new MySQLValueChildInsertWithDuplicateKey(this, pairList);
                }
            } else if (this.parentStmt == null) {
                spec = new MySQLValueInsertWithRowAlias(this, pairList);
            } else {
                spec = new MySQLValueChildInsertWithRowAlias(this, pairList);
            }
            return spec.asInsert();
        }


        @Override
        MySQLInsert._AsRowAliasSpec<C, T> valueClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return new NonParentAsRowAliasSpec<>(this);
        }

        Insert valueClauseEndBeforeAs(final List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return this.endInsert(Collections.emptyList());
        }

        private MySQLInsert._ValueColumnListSpec<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


    }//ValuePartitionClause


    private static final class ValueParentPartitionClause<C, P extends IDomain>
            extends DynamicValueInsertValueClause<
            C,
            P,
            MySQLInsert._ValueParentDefaultSpec<C, P>,
            MySQLInsert._ParentAsRowAliasSpec<C, P, MySQLInsert._ValueChildInsertIntoSpec<C, P>>>
            implements MySQLInsert._ValueParentPartitionSpec<C, P>
            , ParentClauseBeforeRowAlias<C, P, MySQLInsert._ValueChildInsertIntoSpec<C, P>>
            , MySQLInsert._ValueChildInsertIntoSpec<C, P>
            , Insert._ChildPartClause<MySQLInsert._ValueChildInsertIntoSpec<C, P>>
            , MySQLInsert._ValueChildIntoClause<C, P>
            , InsertOptions {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private List<_Pair<Object, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLWords> childModifierList;

        private ValueParentPartitionClause(ValueInsertOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._ValueParentColumnsSpec<C, P>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._ValueParentStaticValueLeftParenClause<C, P> values() {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new ParentStaticValuesLeftParenClause<>(this);
        }

        @Override
        public MySQLInsert._ValueChildInsertIntoSpec<C, P> child() {
            if (this.rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }

        @Override
        public MySQLInsert._ValueChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public MySQLInsert._ValueChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValuePartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValuePartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final ValuePartitionClause<C, T> childClause;
            childClause = new ValuePartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }


        @Override
        public MySQLInsert._ParentOnDuplicateKeyUpdateAliasSpec<C, P, MySQLInsert._ValueChildInsertIntoSpec<C, P>> rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField) {
            if (this.rowValuesList == null || this.rowAlias != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowAlias = rowAlias;
            this.aliasToField = aliasToField;
            return new ParentDuplicateKeyUpdateAliasSpec<>(this, aliasToField);
        }

        @Override
        public MySQLInsert._ValueChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<Object, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<Object, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this.createParentStmt().asInsert();
        }


        MySQLInsert._ParentAsRowAliasSpec<C, P, MySQLInsert._ValueChildInsertIntoSpec<C, P>> valueClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return new ParentAsRowAliasSpec<>(this);
        }

        private Insert valueClauseEndBeforeAs(final List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return this.endInsert(Collections.emptyList());
        }


        @Override
        MySQLInsert._ValueParentDefaultSpec<C, P> columnListEnd() {
            return this;
        }

        private MySQLInsert._ValueParentColumnsSpec<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }


        private MySQLValueInsertStatement createParentStmt() {
            if (this.rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final List<_Pair<Object, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final MySQLValueInsertStatement statement;
            if (pairList.size() == 0) {
                statement = new MySQLValueInsertStatement(this);
            } else if (this.rowAlias == null) {
                statement = new MySQLValueInsertWithDuplicateKey(this, pairList);
            } else {
                statement = new MySQLValueInsertWithRowAlias(this, pairList);
            }
            return statement;
        }


    }//ValueParentPartitionClause


    private static class MySQLValueInsertStatement extends MySQLValueSyntaxStatement
            implements _MySQLInsert._MySQLValueInsert {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;

        private final List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private MySQLValueInsertStatement(ValuePartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowValuesList = clause.rowValuesList;

            assert this.rowValuesList != null;
        }

        private MySQLValueInsertStatement(ValueParentPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowValuesList = clause.rowValuesList;

            assert this.rowValuesList != null;
        }

        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLWords> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowValuesList() {
            return this.rowValuesList;
        }


    }//MySQLValueInsertStatement

    private static class MySQLValueInsertWithDuplicateKey extends MySQLValueInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<Object, _Expression>> pairList;

        private MySQLValueInsertWithDuplicateKey(ValuePartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        private MySQLValueInsertWithDuplicateKey(ValueParentPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        @Override
        public final List<_Pair<Object, _Expression>> duplicatePairList() {
            return this.pairList;
        }

    }//MySQLValueInsertWithDuplicateKey

    private static final class MySQLValueInsertWithRowAlias extends MySQLValueInsertWithDuplicateKey
            implements _MySQLInsert._InsertWithRowAlias {

        private final String rowAlias;

        private final Map<String, FieldMeta<?>> aliasToField;

        private MySQLValueInsertWithRowAlias(ValuePartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause, pairList);
            this.rowAlias = clause.rowAlias;
            this.aliasToField = clause.aliasToField;
            assert this.rowAlias != null && this.aliasToField != null;
        }

        private MySQLValueInsertWithRowAlias(ValueParentPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause, pairList);
            this.rowAlias = clause.rowAlias;
            this.aliasToField = clause.aliasToField;
            assert this.rowAlias != null && this.aliasToField != null;
        }

        @Override
        public String rowAlias() {
            return this.rowAlias;
        }

        @Override
        public Map<String, FieldMeta<?>> aliasToField() {
            return this.aliasToField;
        }


    }//MySQLValueInsertWithRowAlias


    private static class MySQLValueChildInsertStatement extends MySQLValueSyntaxStatement
            implements _MySQLInsert._MySQLValueInsert, _Insert._ChildValuesInsert {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;

        private final List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private final _Insert._ValuesInsert parentStmt;

        private MySQLValueChildInsertStatement(ValuePartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowValuesList = clause.rowValuesList;

            assert this.rowValuesList != null;

            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public final _ValuesInsert parentStmt() {
            return this.parentStmt;
        }

        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLWords> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowValuesList() {
            return this.rowValuesList;
        }


    }//MySQLValueChildInsertStatement

    private static class MySQLValueChildInsertWithDuplicateKey extends MySQLValueInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<Object, _Expression>> pairList;

        private MySQLValueChildInsertWithDuplicateKey(ValuePartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        @Override
        public final List<_Pair<Object, _Expression>> duplicatePairList() {
            return this.pairList;
        }

    }//MySQLValueChildInsertWithDuplicateKey

    private static final class MySQLValueChildInsertWithRowAlias extends MySQLValueInsertWithDuplicateKey
            implements _MySQLInsert._InsertWithRowAlias {

        private final String rowAlias;

        private final Map<String, FieldMeta<?>> aliasToField;

        private MySQLValueChildInsertWithRowAlias(ValuePartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause, pairList);
            this.rowAlias = clause.rowAlias;
            this.aliasToField = clause.aliasToField;
            assert this.rowAlias != null && this.aliasToField != null;
        }


        @Override
        public String rowAlias() {
            return this.rowAlias;
        }

        @Override
        public Map<String, FieldMeta<?>> aliasToField() {
            return this.aliasToField;
        }


    }//MySQLValueChildInsertWithRowAlias



    /*-------------------below assignment insert syntax classes-------------------*/

    private static final class AssignmentInsertOptionClause<C>
            extends MySQLInsertClause<
            C,
            MySQLInsert._AssignmentNullOptionSpec<C>,
            MySQLInsert._AssignmentPreferLiteralSpec<C>,
            MySQLInsert._AssignmentInsertIntoSpec<C>,
            MySQLInsert._AssignmentIntoClause<C>>
            implements MySQLInsert._AssignmentOptionSpec<C>, MySQLInsert._AssignmentIntoClause<C> {


        private AssignmentInsertOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new AssignmentPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new AssignmentParentPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new AssignmentPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new AssignmentParentPartitionClause<>(this, table);
        }

    }//AssignmentInsertOptionClause


    private static final class AssignmentPartitionClause<C, T extends IDomain>
            extends InsertSupport.AssignmentInsertClause<C, T, MySQLInsert._MySQLAssignmentSetSpec<C, T>>
            implements MySQLInsert._AssignmentPartitionSpec<C, T>
            , MySQLInsert._MySQLAssignmentSetSpec<C, T>
            , NonParentClauseBeforeRowAlias<C, T> {


        private final _Insert._AssignmentInsert parentStmt;
        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private AssignmentPartitionClause(AssignmentInsertOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, false, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentStmt = null;
        }

        private AssignmentPartitionClause(AssignmentParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause, false, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentStmt = clause.createParentStmt(); //couldn't invoke asInsert method
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._MySQLAssignmentSetClause<C, T>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }


        @Override
        public MySQLInsert._RowColumnAliasListClause<C, T, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T>> as(final @Nullable String alias) {
            if (alias == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.endAssignmentSetClause();
            return new RowAliasClause<>(alias, this);
        }


        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, MySQLInsert._StaticAssignmentCommaFieldSpec<C, T>> onDuplicateKey() {
            this.endAssignmentSetClause();
            return new NonParentAsRowAliasSpec<>(this)
                    .onDuplicateKey();
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentAsRowAliasSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentAsRowAliasSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<T>>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentAsRowAliasSpec<>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<T>>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentAsRowAliasSpec<>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert asInsert() {
            this.endAssignmentSetClause();
            return this.endInsert(Collections.emptyList());
        }


        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T> rowAliasEnd(final String rowAlias
                , final Map<String, FieldMeta<?>> aliasToField) {
            if (this.rowAlias != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowAlias = rowAlias;
            this.aliasToField = aliasToField;
            return new NonParentDuplicateKeyUpdateAliasSpec<>(this, aliasToField);
        }

        @Override
        public Insert endInsert(final List<_Pair<Object, _Expression>> pairList) {
            final Insert._InsertSpec spec;
            if (pairList.size() == 0) {
                if (this.parentStmt == null) {
                    spec = new MySQLAssignmentInsertStatement(this);
                } else {
                    spec = new AssignmentChildInsertStatement(this);
                }
            } else if (this.rowAlias == null) {
                if (this.parentStmt == null) {
                    spec = new AssignmentInsertWithDuplicateKey(this, pairList);
                } else {
                    spec = new AssignmentChildInsertWithDuplicateKey(this, pairList);
                }
            } else if (this.parentStmt == null) {
                spec = new AssignmentInsertWithRowAlias(this, pairList);
            } else {
                spec = new AssignmentChildInsertWithRowAlias(this, pairList);
            }
            return spec.asInsert();
        }

        private MySQLInsert._MySQLAssignmentSetClause<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        @Override
        public void validateField(final FieldMeta<?> field, final @Nullable ArmyExpression value) {
            if (!this.migration) {
                _DialectUtils.checkInsertField(this.table, field, this::forbidField);
                if (value != null && !field.nullable() && value.isNullValue()) {
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonNullField, field);
                }
            }

        }

        private CriteriaException forbidField(FieldMeta<?> field, Function<FieldMeta<?>, CriteriaException> function) {
            return CriteriaContextStack.criteriaError(this.criteriaContext, function, field);
        }


    }//AssignmentPartitionClause


    private static final class AssignmentParentPartitionClause<C, P extends IDomain>
            extends InsertSupport.AssignmentInsertClause<C, P, MySQLInsert._AssignmentParentSetSpec<C, P>>
            implements MySQLInsert._AssignmentParentPartitionSpec<C, P>
            , MySQLInsert._AssignmentParentSetSpec<C, P>
            , ParentClauseBeforeRowAlias<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>
            , MySQLInsert._AssignmentChildInsertIntoSpec<C, P>
            , MySQLInsert._AssignmentChildIntoClause<C, P>
            , NonQueryInsertOptions {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private List<_Pair<Object, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLWords> childModifierList;

        private AssignmentParentPartitionClause(AssignmentInsertOptionClause<C> clause, ParentTableMeta<P> table) {
            super(clause, false, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._AssignmentParentSetClause<C, P>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, P, MySQLInsert._ParentStaticAssignmentCommaFieldSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>> onDuplicateKey() {
            this.endAssignmentSetClause();
            return new ParentAsRowAliasSpec<>(this)
                    .onDuplicateKey();
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> onDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<P>>> consumer) {
            this.endAssignmentSetClause();
            return new ParentAsRowAliasSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<P>>> consumer) {
            this.endAssignmentSetClause();
            return new ParentAsRowAliasSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> ifOnDuplicateKeyUpdate(Consumer<PairConsumer<FieldMeta<P>>> consumer) {
            this.endAssignmentSetClause();
            return new ParentAsRowAliasSpec<>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._MySQLChildSpec<MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<FieldMeta<P>>> consumer) {
            this.endAssignmentSetClause();
            return new ParentAsRowAliasSpec<>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._RowColumnAliasListClause<C, P, MySQLInsert._ParentOnDuplicateKeyUpdateAliasSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>>> as(String alias) {
            this.endAssignmentSetClause();
            return new ParentAsRowAliasSpec<>(this)
                    .as(alias);
        }

        @Override
        public Insert asInsert() {
            this.endAssignmentSetClause();
            return this.endInsert(Collections.emptyList());
        }

        @Override
        public MySQLInsert._AssignmentChildInsertIntoSpec<C, P> child() {
            this.endAssignmentSetClause();
            return this;
        }

        @Override
        public MySQLInsert._AssignmentChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public MySQLInsert._AssignmentChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final AssignmentPartitionClause<C, T> childClause;
            childClause = new AssignmentPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        public MySQLInsert._ParentOnDuplicateKeyUpdateAliasSpec<C, P, MySQLInsert._AssignmentChildInsertIntoSpec<C, P>> rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField) {
            if (this.rowAlias != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowAlias = rowAlias;
            this.aliasToField = aliasToField;
            return new ParentDuplicateKeyUpdateAliasSpec<>(this, aliasToField);
        }

        @Override
        public MySQLInsert._AssignmentChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<Object, _Expression>> pairList) {
            if (this.duplicatePairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<Object, _Expression>> pairList) {
            if (this.duplicatePairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this.createParentStmt().asInsert();
        }


        @Override
        public void validateField(final FieldMeta<?> field, final @Nullable ArmyExpression value) {
            if (!this.migration) {
                _DialectUtils.checkInsertField(this.table, field, this::forbidField);
                if (value != null && !field.nullable() && value.isNullValue()) {
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonNullField, field);
                }
            }

        }

        private CriteriaException forbidField(FieldMeta<?> field, Function<FieldMeta<?>, CriteriaException> function) {
            return CriteriaContextStack.criteriaError(this.criteriaContext, function, field);
        }

        private MySQLInsert._AssignmentParentSetClause<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLAssignmentInsertStatement createParentStmt() {
            final List<_Pair<Object, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final MySQLAssignmentInsertStatement statement;
            if (pairList.size() == 0) {
                statement = new MySQLAssignmentInsertStatement(this);
            } else if (this.rowAlias == null) {
                statement = new AssignmentInsertWithDuplicateKey(this, pairList);
            } else {
                statement = new AssignmentInsertWithRowAlias(this, pairList);
            }
            return statement;
        }


    }//AssignmentParentPartitionClause


    static class MySQLAssignmentInsertStatement extends InsertSupport.AssignmentInsertStatement<Insert>
            implements MySQLInsert, _MySQLInsert._MySQLAssignmentInsert, Insert._InsertSpec {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;

        private MySQLAssignmentInsertStatement(AssignmentPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
        }

        private MySQLAssignmentInsertStatement(AssignmentParentPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
        }

        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLWords> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

    }//MySQLAssignmentInsertStatement

    private static class AssignmentInsertWithDuplicateKey extends MySQLAssignmentInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<Object, _Expression>> pairList;

        private AssignmentInsertWithDuplicateKey(AssignmentPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        private AssignmentInsertWithDuplicateKey(AssignmentParentPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }


        @Override
        public final List<_Pair<Object, _Expression>> duplicatePairList() {
            return this.pairList;
        }


    }//AssignmentInsertWithDuplicateKey

    private static class AssignmentInsertWithRowAlias extends AssignmentInsertWithDuplicateKey
            implements _MySQLInsert._InsertWithRowAlias {

        private final String rowAlias;

        private final Map<String, FieldMeta<?>> aliasToField;

        private AssignmentInsertWithRowAlias(AssignmentPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause, pairList);
            this.rowAlias = clause.rowAlias;
            this.aliasToField = clause.aliasToField;
            assert this.rowAlias != null && this.aliasToField != null;
        }

        private AssignmentInsertWithRowAlias(AssignmentParentPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause, pairList);
            this.rowAlias = clause.rowAlias;
            this.aliasToField = clause.aliasToField;
        }

        @Override
        public String rowAlias() {
            return this.rowAlias;
        }

        @Override
        public Map<String, FieldMeta<?>> aliasToField() {
            return this.aliasToField;
        }


    }//AssignmentInsertWIthRowAlias


    private static class AssignmentChildInsertStatement extends MySQLAssignmentInsertStatement
            implements _Insert._ChildAssignmentInsert {

        private final _AssignmentInsert parentStmt;

        private AssignmentChildInsertStatement(AssignmentPartitionClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public final _AssignmentInsert parentStmt() {
            return this.parentStmt;
        }

    }//AssignmentChildInsertStatement

    private static class AssignmentChildInsertWithDuplicateKey extends AssignmentChildInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<Object, _Expression>> pairList;

        private AssignmentChildInsertWithDuplicateKey(AssignmentPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        @Override
        public final List<_Pair<Object, _Expression>> duplicatePairList() {
            return this.pairList;
        }


    }//AssignmentChildInsertWithDuplicateKey

    private static final class AssignmentChildInsertWithRowAlias extends AssignmentChildInsertWithDuplicateKey
            implements _MySQLInsert._InsertWithRowAlias {

        private final String rowAlias;

        private final Map<String, FieldMeta<?>> aliasToField;

        private AssignmentChildInsertWithRowAlias(AssignmentPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause, pairList);
            this.rowAlias = clause.rowAlias;
            this.aliasToField = clause.aliasToField;
            assert this.rowAlias != null && this.aliasToField != null;
        }

        @Override
        public String rowAlias() {
            return this.rowAlias;
        }

        @Override
        public Map<String, FieldMeta<?>> aliasToField() {
            return this.aliasToField;
        }


    }//AssignmentChildInsertWIthRowAlias


    private static final class QueryInsertIntoClause<C> implements MySQLInsert._QueryInsertIntoSpec<C>
            , MySQLInsert._QueryIntoClause<C> {

        private final CriteriaContext criteriaContext;

        private List<Hint> hintList;

        private List<MySQLWords> modifierList;

        private QueryInsertIntoClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public MySQLInsert._QueryIntoClause<C> insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public MySQLInsert._QueryIntoClause<C> insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.hintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteriaContext.criteria())
                    , MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::insertModifier);
            return this;
        }


        @Override
        public <T extends IDomain> MySQLInsert._QueryPartitionSpec<C, T> into(SimpleTableMeta<T> table) {
            return new QueryPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._QueryParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return new QueryParentPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._QueryPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new QueryPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._QueryParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new QueryParentPartitionClause<>(this, table);
        }


    }//QueryInsertIntoClause


    /**
     * <p>
     * This class is a the implementation of MySQL RowSet insert syntax after INTO clause,for {@link  SingleTableMeta}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT Statement</a>
     */
    private static class QueryPartitionClause<C, T extends IDomain>
            extends InsertSupport.QueryInsertSpaceClause<
            C,
            T,
            MySQLInsert._QuerySpaceSubQueryClause<C, T>,
            MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T>>
            implements MySQLInsert._QueryPartitionSpec<C, T>
            , MySQLInsert._QuerySpaceSubQueryClause<C, T>
            , NonParentClauseBeforeRowAlias<C, T> {

        private final _Insert._QueryInsert parentStmt;

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private QueryPartitionClause(QueryInsertIntoClause<C> clause, SimpleTableMeta<T> table) {
            super(clause.criteriaContext, table);
            this.hintList = _CollectionUtils.safeList(clause.hintList);
            this.modifierList = _CollectionUtils.safeList(clause.modifierList);
            this.parentStmt = null;
        }

        private QueryPartitionClause(QueryParentPartitionClause<C, ?> clause, ChildTableMeta<T> table) {
            super(clause.criteriaContext, table);
            this.hintList = _CollectionUtils.safeList(clause.childHintList);
            this.modifierList = _CollectionUtils.safeList(clause.childModifierList);
            this.parentStmt = clause.createParentStmt(); //couldn't invoke asInsert method
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._QueryColumnListClause<C, T>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }


        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T> rowAliasEnd(String rowAlias
                , Map<String, FieldMeta<?>> aliasToField) {
            // query insert no AS row_alias clause
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

        @Override
        public Insert endInsert(final List<_Pair<Object, _Expression>> pairList) {
            final Insert._InsertSpec spec;
            if (pairList.size() == 0) {
                if (this.parentStmt == null) {
                    spec = new MySQLQueryInsertStatement(this);
                } else {
                    spec = new QueryChildInsertStatement(this);
                }
            } else if (this.parentStmt == null) {
                spec = new QueryInsertWithDuplicate(this, pairList);
            } else {
                spec = new QueryChildInsertWithDuplicate(this, pairList);
            }
            return spec.asInsert();
        }


        @Override
        MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, T> spaceEnd() {
            return new NonParentAsRowAliasSpec<>(this);
        }

        @Override
        MySQLInsert._QuerySpaceSubQueryClause<C, T> columnListEnd() {
            return this;
        }

        private MySQLInsert._QueryColumnListClause<C, T> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

    }//QueryPartitionClause


    private static final class QueryParentPartitionClause<C, P extends IDomain>
            extends InsertSupport.QueryInsertSpaceClause<
            C,
            P,
            MySQLInsert._QueryParentQueryClause<C, P>,
            MySQLInsert._ParentOnDuplicateKeyUpdateFieldSpec<C, P, MySQLInsert._QueryChildInsertIntoSpec<C, P>>>
            implements MySQLInsert._QueryParentPartitionSpec<C, P>
            , MySQLInsert._QueryParentQueryClause<C, P>
            , Insert._ChildPartClause<MySQLInsert._QueryChildInsertIntoSpec<C, P>>
            , ParentClauseBeforeRowAlias<C, P, MySQLInsert._QueryChildInsertIntoSpec<C, P>>
            , MySQLInsert._QueryChildInsertIntoSpec<C, P>
            , MySQLInsert._QueryChildIntoClause<C, P> {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<_Pair<Object, _Expression>> duplicatePairList;

        private List<Hint> childHintList;

        private List<MySQLWords> childModifierList;

        private QueryParentPartitionClause(QueryInsertIntoClause<C> clause, ParentTableMeta<P> table) {
            super(clause.criteriaContext, table);
            this.hintList = _CollectionUtils.safeList(clause.hintList);
            this.modifierList = _CollectionUtils.safeList(clause.modifierList);

        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._QueryParentColumnListClause<C, P>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._ParentOnDuplicateKeyUpdateAliasSpec<C, P, MySQLInsert._QueryChildInsertIntoSpec<C, P>> rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField) {
            // query insert no AS row_alias clause
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

        @Override
        public MySQLInsert._QueryChildInsertIntoSpec<C, P> child() {
            return this;
        }

        @Override
        public MySQLInsert._QueryChildIntoClause<C, P> insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, supplier.get(), MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public MySQLInsert._QueryChildIntoClause<C, P> insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            this.childHintList = MySQLUtils.asHintList(this.criteriaContext, function.apply(this.criteria)
                    , MySQLHints::castHint);
            this.childModifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers
                    , MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T extends IDomain> MySQLInsert._QueryPartitionSpec<C, T> into(ComplexTableMeta<P, T> table) {
            return this.insertInto(table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._QueryPartitionSpec<C, T> insertInto(ComplexTableMeta<P, T> table) {
            final QueryPartitionClause<C, T> childClause;
            childClause = new QueryPartitionClause<>(this, table);
            this.childHintList = null;
            this.childModifierList = null;
            return childClause;
        }

        @Override
        public MySQLInsert._QueryChildInsertIntoSpec<C, P> parentStmtEnd(final List<_Pair<Object, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this;
        }

        @Override
        public Insert endInsert(final List<_Pair<Object, _Expression>> pairList) {
            if (this.duplicatePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.duplicatePairList = pairList;
            return this.createParentStmt().asInsert();
        }

        @Override
        MySQLInsert._QueryParentQueryClause<C, P> columnListEnd() {
            return this;
        }

        @Override
        MySQLInsert._ParentOnDuplicateKeyUpdateFieldSpec<C, P, MySQLInsert._QueryChildInsertIntoSpec<C, P>> spaceEnd() {
            return new ParentAsRowAliasSpec<>(this);
        }


        private MySQLInsert._QueryParentColumnListClause<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLQueryInsertStatement createParentStmt() {
            final List<_Pair<Object, _Expression>> pairList = this.duplicatePairList;
            if (pairList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final MySQLQueryInsertStatement statement;
            if (pairList.size() == 0) {
                statement = new MySQLQueryInsertStatement(this);
            } else {
                statement = new QueryInsertWithDuplicate(this, pairList);
            }
            return statement;
        }


    }//QueryParentPartitionClause


    static class MySQLQueryInsertStatement extends QueryInsertStatement<Insert>
            implements MySQLInsert, Insert._InsertSpec, _MySQLInsert._MySQQueryInsert {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;


        private MySQLQueryInsertStatement(QueryPartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
        }

        private MySQLQueryInsertStatement(QueryParentPartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
        }

        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLWords> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//MySQLQueryInsertStatement

    private static final class QueryInsertWithDuplicate extends MySQLQueryInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<Object, _Expression>> pairList;

        private QueryInsertWithDuplicate(QueryPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        private QueryInsertWithDuplicate(QueryParentPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }


        @Override
        public List<_Pair<Object, _Expression>> duplicatePairList() {
            return this.pairList;
        }

    }//QueryInsertWithDuplicate


    private static class QueryChildInsertStatement extends MySQLQueryInsertStatement
            implements _Insert._ChildQueryInsert {

        private final _QueryInsert parentStmt;

        private QueryChildInsertStatement(QueryPartitionClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public _QueryInsert parentStmt() {
            return this.parentStmt;
        }


    }//QueryChildInsertStatement

    private static final class QueryChildInsertWithDuplicate extends QueryChildInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final List<_Pair<Object, _Expression>> pairList;

        private QueryChildInsertWithDuplicate(QueryPartitionClause<?, ?> clause
                , List<_Pair<Object, _Expression>> pairList) {
            super(clause);
            this.pairList = pairList;
        }

        @Override
        public List<_Pair<Object, _Expression>> duplicatePairList() {
            return this.pairList;
        }


    }//QueryChildInsertWithDuplicate


}
