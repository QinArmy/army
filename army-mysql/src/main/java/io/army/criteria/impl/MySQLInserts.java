package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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


    interface ClauseBeforeRowAlias<C, T extends IDomain, RR> extends ColumnListClause {

        /**
         * @param aliasToField a unmodified map,non-empty
         */
        RR rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField);

        /**
         * @param valuePairMap a unmodified map,empty is allowed.
         */
        Insert endInsert(Map<?, _Expression> valuePairMap);

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

        final ClauseBeforeRowAlias<C, T, RR> clause;

        private boolean optionalOnDuplicateKey = true;

        private Map<FieldMeta<?>, _Expression> valuePairMap;

        private AsRowAliasSpec(ClauseBeforeRowAlias<C, T, RR> clause) {
            this.criteriaContext = clause.getCriteriaContext();
            this.criteria = this.criteriaContext.criteria();
            this.clause = clause;
        }

        private AsRowAliasSpec(DomainParentPartitionClause<C, T> clause) {
            this.criteriaContext = clause.criteriaContext;
            this.criteria = clause.criteria;
            this.clause = (ClauseBeforeRowAlias<C, T, RR>) this;
        }

        @Override
        public final MySQLInsert._RowColumnAliasListClause<C, T, RR> as(final @Nullable String alias) {
            if (alias == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            return new RowAliasClause<>(alias, this.clause);
        }

        @Override
        public final MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, T, UR> onDuplicateKey() {
            this.optionalOnDuplicateKey = false;
            return this;
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


        final Map<FieldMeta<?>, _Expression> endDuplicateKeyClause() {
            Map<FieldMeta<?>, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap == null) {
                if (!this.optionalOnDuplicateKey) {
                    String m = "You don't add any field and value pair";
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
                }
                valuePairMap = Collections.emptyMap();
            } else if (valuePairMap instanceof HashMap) {
                valuePairMap = Collections.unmodifiableMap(valuePairMap);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.valuePairMap = valuePairMap;
            return valuePairMap;
        }

        private void addValuePair(final FieldMeta<T> field, final @Nullable Expression value) {
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            this.clause.validateField(field);
            if (!field.nullable() && ((ArmyExpression) value).isNullValue()) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonNullField, field);
            }

            Map<FieldMeta<?>, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap == null) {
                valuePairMap = new HashMap<>();
                this.valuePairMap = valuePairMap;
            }
            if (valuePairMap.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.criteriaContext, field);
            }

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


        private NonParentAsRowAliasSpec(ClauseBeforeRowAlias<C, T, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T>> clause) {
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

        private ParentAsRowAliasSpec(ClauseBeforeRowAlias<C, P, MySQLInsert._ParentOnDuplicateKeyUpdateAliasSpec<C, P, CT>> clause) {
            super(clause);
        }


        @SuppressWarnings("unchecked")
        @Override
        public CT child() {
            return (CT) this.clause;
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
            , ClauseBeforeRowAlias<C, T, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T>> {


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
        public void validateField(FieldMeta<?> field) {
            this.domainClause.validateField(field);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T> rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField) {
            return new NonParentDuplicateKeyUpdateAliasSpec<>(this.domainClause, aliasToField);
        }

        @Override
        public Insert endInsert(Map<?, _Expression> valuePairMap) {
            return this.domainClause.endInsert(valuePairMap);
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
            return ((DomainParentPartitionClause<C, P>) this.clause).endParentStmt(this.endDuplicateKeyClause());
        }


    }//DomainParentAsRowAliasSpec


    private static final class RowAliasClause<C, T extends IDomain, RR>
            implements MySQLInsert._RowColumnAliasListClause<C, T, RR>
            , Statement._RightParenClause<RR>
            , MySQLInsert._ColumnAliasClause<T, RR> {

        private final CriteriaContext criteriaContext;

        private final String rowAlias;

        private final ClauseBeforeRowAlias<C, T, RR> clause;

        private Map<FieldMeta<?>, Boolean> fieldMap = new HashMap<>();

        private Map<String, FieldMeta<?>> aliasToField = new HashMap<>();

        private RowAliasClause(String rowAlias, ClauseBeforeRowAlias<C, T, RR> clause) {
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
            this.clause.validateField(field);

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

        final ClauseBeforeRowAlias<C, T, ?> clause;

        private final Map<String, FieldMeta<?>> aliasToField;

        private Map<Object, _Expression> valuePairMap;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        private boolean optionalOnDuplicateKeyClause;

        private DuplicateKeyUpdateAliasSpec(ClauseBeforeRowAlias<C, T, ?> clause, Map<String, FieldMeta<?>> aliasToField) {
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


        final Map<Object, _Expression> endDuplicateKeyClause() {
            Map<Object, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap == null) {
                if (!this.optionalOnDuplicateKeyClause) {
                    String m = "You use non-ifOnDuplicateKeyUpdate  but don't add any filed value pair";
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
                }
                valuePairMap = Collections.emptyMap();
            } else if (valuePairMap instanceof HashMap) {
                valuePairMap = Collections.unmodifiableMap(valuePairMap);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.valuePairMap = valuePairMap;
            return valuePairMap;
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

            //1. get fieldMap
            Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                fieldMap = new HashMap<>();
                this.fieldMap = fieldMap;
            }
            //2. validate field
            final FieldMeta<?> field;
            if (fieldOrAlias instanceof FieldMeta) {
                field = (FieldMeta<?>) fieldOrAlias;
                this.clause.validateField(field);
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

            //3. validate value
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            if (!field.nullable() && ((ArmyExpression) value).isNullValue()) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonNullField, field);
            }

            //4.get valuePairMap
            Map<Object, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap == null) {
                valuePairMap = new HashMap<>();
                this.valuePairMap = valuePairMap;
            }
            //5. put pair
            if (valuePairMap.putIfAbsent(fieldOrAlias, (ArmyExpression) value) != null) {
                String m = String.format("duplication value pair for fieldOrAlias[%s]", fieldOrAlias);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }

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


        private NonParentDuplicateKeyUpdateAliasSpec(ClauseBeforeRowAlias<C, T, ?> clause
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

        private ParentDuplicateKeyUpdateAliasSpec(ClauseBeforeRowAlias<C, P, ?> clause
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
            return (CT) this.clause;
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
            return ((DomainParentPartitionClause<C, P>) this.clause).endParentStmt(this.endDuplicateKeyClause());
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
            return new DomainInsertPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T> into(ParentTableMeta<T> table) {
            return null;
        }


        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> insertInto(SimpleTableMeta<T> table) {
            return new DomainInsertPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T> insertInto(ParentTableMeta<T> table) {
            return new DomainInsertPartitionClause<>(this, table);
        }


    }//DomainOptionClause


    private static final class DomainInsertPartitionClause<C, T extends IDomain>
            extends InsertSupport.DomainValueClause<
            C,
            T,
            MySQLInsert._DomainDefaultSpec<C, T>,
            MySQLInsert._AsRowAliasSpec<C, T>>
            implements MySQLInsert._DomainPartitionSpec<C, T>
            , ClauseBeforeRowAlias<C, T, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, T>> {

        private final _Insert._ValuesSyntaxInsert parentStmt;

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private DomainInsertPartitionClause(DomainInsertOptionClause<C> clause, SimpleTableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.parentStmt = null;

        }

        private DomainInsertPartitionClause(DomainParentPartitionClause<C, ?> parentClause, ChildTableMeta<T> table) {
            super(parentClause, table);
            this.hintList = parentClause.childHintList();
            this.modifierList = parentClause.childModifierList();
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
        public Insert endInsert(final Map<?, _Expression> valuePairMap) {
            final Insert._InsertSpec spec;
            if (valuePairMap.size() == 0) {
                if (this.parentStmt == null) {
                    spec = new MySQLDomainInsertStatement(this);
                } else {
                    spec = new MySQLDomainChildInsertStatement(this);
                }
            } else if (this.rowAlias == null) {
                if (this.parentStmt == null) {
                    spec = new MySQLDomainInsertWithDuplicateKey(this, valuePairMap);
                } else {
                    spec = new MySQLDomainChildInsertWithDuplicateKey(this, valuePairMap);
                }
            } else if (this.parentStmt == null) {
                spec = new MySQLDomainInsertWIthRowAlias(this, valuePairMap);
            } else {
                spec = new MySQLDomainChildInsertWIthRowAlias(this, valuePairMap);
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

        private List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

    }//DomainInsertPartitionClause


    private static final class DomainParentPartitionClause<C, P extends IDomain>
            extends InsertSupport.DomainValueClause<
            C,
            P,
            MySQLInsert._DomainParentDefaultSpec<C, P>,
            MySQLInsert._AsRowAliasSpec<C, P>>
            implements MySQLInsert._DomainParentPartitionSpec<C, P>
            , ClauseBeforeRowAlias<C, P, MySQLInsert._DomainParentOnDuplicateKeyUpdateAliasSpec<C, P>>
            , MySQLInsert._DomainChildClause<C, P>
            , MySQLInsert._DomainChildInsertIntoSpec<C, P>
            , MySQLInsert._DomainChildIntoClause<C, P>
            , InsertOptions {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private Map<?, _Expression> valuePairMap;

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
            return this;
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
            final DomainInsertPartitionClause<C, T> childClause;
            childClause = new DomainInsertPartitionClause<>(this, table);
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
        public Insert endInsert(final Map<?, _Expression> valuePairMap) {
            if (this.valuePairMap != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.valuePairMap = valuePairMap;
            return this.createParentStmt().asInsert();
        }

        private MySQLInsert._DomainChildInsertIntoSpec<C, P> endParentStmt(final Map<?, _Expression> valuePairMap) {
            if (this.valuePairMap != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.valuePairMap = valuePairMap;
            return this;
        }

        private MySQLInsert._DomainParentColumnsSpec<C, P> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        private List<Hint> childHintList() {
            List<Hint> list = this.childHintList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        private List<MySQLWords> childModifierList() {
            List<MySQLWords> list = this.childModifierList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        private MySQLDomainInsertStatement createParentStmt() {
            final Map<?, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final MySQLDomainInsertStatement statement;
            if (valuePairMap.size() == 0) {
                statement = new MySQLDomainInsertStatement(this);
            } else if (this.rowAlias == null) {
                statement = new MySQLDomainInsertWithDuplicateKey(this, valuePairMap);
            } else {
                statement = new MySQLDomainInsertWIthRowAlias(this, valuePairMap);
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


    private static class MySQLDomainInsertStatement extends MySQLValueSyntaxStatement
            implements _MySQLInsert._MySQLDomainInsert {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;
        private final List<IDomain> domainList;

        private MySQLDomainInsertStatement(DomainInsertPartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = clause.partitionList();
            this.domainList = clause.domainList();
        }

        private MySQLDomainInsertStatement(DomainParentPartitionClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = clause.partitionList();
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


    private static class MySQLDomainInsertWithDuplicateKey extends MySQLDomainInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {


        private final Map<?, _Expression> valuePairMap;

        private MySQLDomainInsertWithDuplicateKey(DomainInsertPartitionClause<?, ?> clause
                , Map<?, _Expression> valuePairMap) {
            super(clause);
            this.valuePairMap = valuePairMap;
        }

        private MySQLDomainInsertWithDuplicateKey(DomainParentPartitionClause<?, ?> clause
                , Map<?, _Expression> valuePairMap) {
            super(clause);
            this.valuePairMap = valuePairMap;
        }

        @Override
        public final Map<?, _Expression> valuePairsForDuplicate() {
            return this.valuePairMap;
        }


    }//MySQLDomainInsertWithDuplicateKey


    private static class MySQLDomainInsertWIthRowAlias extends MySQLDomainInsertWithDuplicateKey
            implements _MySQLInsert._InsertWithRowAlias {

        private final String rowAlias;

        private final Map<String, FieldMeta<?>> aliasToField;

        private MySQLDomainInsertWIthRowAlias(DomainInsertPartitionClause<?, ?> clause
                , Map<?, _Expression> valuePairMap) {
            super(clause, valuePairMap);
            this.rowAlias = clause.rowAlias;
            this.aliasToField = clause.aliasToField;
            assert this.rowAlias != null && this.aliasToField != null;
        }

        private MySQLDomainInsertWIthRowAlias(DomainParentPartitionClause<?, ?> clause
                , Map<?, _Expression> valuePairMap) {
            super(clause, valuePairMap);
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


    private static class MySQLDomainChildInsertStatement extends MySQLDomainInsertStatement
            implements _Insert._ChildDomainInsert {

        private final _Insert._ValuesSyntaxInsert parentStmt;

        private MySQLDomainChildInsertStatement(DomainInsertPartitionClause<?, ?> clause) {
            super(clause);
            this.parentStmt = clause.parentStmt;
            assert this.parentStmt != null;
        }

        @Override
        public final _ValuesSyntaxInsert parentStmt() {
            return this.parentStmt;
        }

    }//MySQLDomainChildInsertStatement


    private static class MySQLDomainChildInsertWithDuplicateKey extends MySQLDomainChildInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final Map<?, _Expression> valuePairMap;

        private MySQLDomainChildInsertWithDuplicateKey(DomainInsertPartitionClause<?, ?> clause
                , Map<?, _Expression> valuePairMap) {
            super(clause);
            this.valuePairMap = valuePairMap;
        }

        @Override
        public final Map<?, _Expression> valuePairsForDuplicate() {
            return this.valuePairMap;
        }

    }//MySQLDomainChildInsertWithDuplicateKey


    private static final class MySQLDomainChildInsertWIthRowAlias extends MySQLDomainChildInsertWithDuplicateKey
            implements _MySQLInsert._InsertWithRowAlias {

        private final String rowAlias;

        private final Map<String, FieldMeta<?>> aliasToField;

        private MySQLDomainChildInsertWIthRowAlias(DomainInsertPartitionClause<?, ?> clause
                , Map<?, _Expression> valuePairMap) {
            super(clause, valuePairMap);
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

    private interface ClauseForValueInsert<C, F extends TableField> extends ClauseBeforeRowAlias<C, F> {

        Insert valueClauseEndBeforeAs(List<Map<FieldMeta<?>, _Expression>> rowValuesList);

        MySQLInsert._AsRowAliasSpec<C, F> valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowValuesList);

    }


    private static final class ValueInsertOptionClause<C>
            extends MySQLInsertClause<
            C,
            MySQLInsert._ValueNullOptionSpec<C>,
            MySQLInsert._ValueInsertIntoSpec<C>,
            MySQLInsert._ValueIntoClause<C>>
            implements MySQLInsert._ValueOptionSpec<C>, InsertOptions {

        private ValueInsertOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }


        @Override
        public <T extends IDomain> MySQLInsert._ValuePartitionSpec<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table) {
            return new MySQLValueInsertStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValueParentPartitionSpec<C, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table) {
            return new MySQLValueInsertStatement<>(this, table);
        }


    }//ValueOptionClause


    private static final class StaticValueLeftParenClause<C, F extends TableField>
            extends InsertSupport.StaticColumnValuePairClause<C, F, MySQLInsert._AsRowAliasSpec<C, F>> {

        private final ClauseForValueInsert<C, F> clause;

        private StaticValueLeftParenClause(ClauseForValueInsert<C, F> clause) {
            super(clause.getCriteriaContext(), clause::contain);
            this.clause = clause;
        }

        @Override
        public MySQLInsert._AsRowAliasSpec<C, F> rightParen() {
            return this.clause.valueClauseEnd(this.endValuesClause());
        }


    }//StaticValueLeftParenClause


    private static final class StaticValuesLeftParenClause<C, F extends TableField>
            extends InsertSupport.StaticColumnValuePairClause<C, F, MySQLInsert._ValueStaticValuesLeftParenSpec<C, F>>
            implements MySQLInsert._ValueStaticValuesLeftParenSpec<C, F> {

        private final ClauseForValueInsert<C, F> clause;

        private StaticValuesLeftParenClause(ClauseForValueInsert<C, F> clause) {
            super(clause.getCriteriaContext(), clause::contain);
            this.clause = clause;
        }


        @Override
        public MySQLInsert._OnDuplicateKeyRowAliasClause<C, F> as(String alias) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .as(alias);
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, F, MySQLInsert._StaticAssignmentCommaFieldSpec<C, F>> onDuplicateKey() {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKey();
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<PairConsumer<F>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<F>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<PairConsumer<F>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<F>> consumer) {
            return this.clause.valueClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert asInsert() {
            return this.clause.valueClauseEndBeforeAs(this.endValuesClause());
        }


        @Override
        public MySQLInsert._ValueStaticValuesLeftParenSpec<C, F> rightParen() {
            this.endCurrentRow();
            return this;
        }


    }//StaticValuesLeftParenClause


    static final class MySQLValueInsertStatement<C, F extends TableField>
            extends DynamicValueInsertValueClause<
            C,
            F,
            MySQLInsert._ValueDefaultSpec<C, F>,
            MySQLInsert._AsRowAliasSpec<C, F>>
            implements MySQLInsert._ValuePartitionSpec<C, F>, MySQLInsert._ValueParentPartitionSpec<C, F>
            , ClauseForValueInsert<C, F>, MySQLInsert, _MySQLInsert._MySQLValueInsert
            , _MySQLInsert._InsertWithRowAlias {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<String> childPartitionList;

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private Map<?, _Expression> valuePairMap;

        private Boolean prepared;

        private MySQLValueInsertStatement(ValueInsertOptionClause<C> clause, TableMeta<?> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }


        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._ValueColumnListSpec<C, F>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._ValueChildPartitionSpec<C, F>> parentPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::parentPartitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._ValueColumnListSpec<C, F>> childPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::childPartitionEnd);
        }


        @Override
        public _StaticValueLeftParenClause<C, F, MySQLInsert._AsRowAliasSpec<C, F>> value() {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new StaticValueLeftParenClause<>(this);
        }

        @Override
        public MySQLInsert._ValueStaticValuesLeftParenClause<C, F> values() {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new StaticValuesLeftParenClause<>(this);
        }


        @Override
        public void prepared() {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

        @Override
        public boolean isPrepared() {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F> rowAliasEnd(final String rowAlias
                , final Map<String, FieldMeta<?>> aliasToField) {
            if (this.rowValuesList == null || this.rowAlias != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowAlias = rowAlias;
            this.aliasToField = aliasToField;
            return new NonParentDuplicateKeyUpdateAliasSpec<>(aliasToField, this);
        }

        @Override
        public Insert endInsert(Map<?, _Expression> valuePairMap) {
            _Assert.nonPrepared(this.prepared);
            if (this.rowValuesList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            CriteriaContextStack.clearContextStack(this.criteriaContext);
            this.valuePairMap = valuePairMap;
            this.prepared = Boolean.TRUE;
            return this;
        }

        @Override
        _ValueDefaultSpec<C, F> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }

        @Override
        public MySQLInsert._AsRowAliasSpec<C, F> valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return new NonParentAsRowAliasSpec<>(this);
        }

        @Override
        public Insert valueClauseEndBeforeAs(List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return this.endInsert(Collections.emptyMap());
        }

        @Override
        public List<Map<FieldMeta<?>, _Expression>> rowValuesList() {
            _Assert.prepared(this.prepared);
            return this.rowValuesList;
        }

        @Override
        public List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public List<MySQLWords> modifierList() {
            return this.modifierList;
        }

        @Override
        public List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        @Override
        public List<String> childPartitionList() {
            List<String> list = this.childPartitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        @Override
        public Map<?, _Expression> valuePairsForDuplicate() {
            Map<?, _Expression> map = this.valuePairMap;
            if (map == null) {
                map = Collections.emptyMap();
            }
            return map;
        }

        @Override
        public String rowAlias() {
            return this.rowAlias;
        }

        @Override
        public Map<String, FieldMeta<?>> aliasToField() {
            Map<String, FieldMeta<?>> map = this.aliasToField;
            if (map == null) {
                map = Collections.emptyMap();
            }
            return map;
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

        private MySQLInsert._ValueColumnListSpec<C, F> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLInsert._ValueChildPartitionSpec<C, F> parentPartitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLInsert._ValueColumnListSpec<C, F> childPartitionEnd(List<String> partitionList) {
            this.childPartitionList = partitionList;
            return this;
        }


    }//ValueInsertPartitionClause

    /*-------------------below assignment insert syntax classes-------------------*/

    private static final class AssignmentInsertOptionClause<C>
            extends MySQLInsertClause<
            C,
            MySQLInsert._AssignmentNullOptionSpec<C>,
            MySQLInsert._AssignmentInsertIntoSpec<C>,
            MySQLInsert._AssignmentIntoClause<C>>
            implements MySQLInsert._AssignmentOptionSpec<C>, MySQLInsert._AssignmentIntoClause<C>, InsertOptions {


        private AssignmentInsertOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentPartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table) {
            return new MySQLAssignmentInsertStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentParentPartitionSpec<C, FieldMeta<? super T>> into(ChildTableMeta<T> table) {
            return new MySQLAssignmentInsertStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentPartitionSpec<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table) {
            return new MySQLAssignmentInsertStatement<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._AssignmentParentPartitionSpec<C, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table) {
            return new MySQLAssignmentInsertStatement<>(this, table);
        }


    }//AssignmentInsertOptionClause


    static final class MySQLAssignmentInsertStatement<C, F extends TableField>
            extends InsertSupport.AssignmentInsertClause<C, F, MySQLInsert._MySQLAssignmentSetSpec<C, F>>
            implements MySQLInsert._AssignmentParentPartitionSpec<C, F>, MySQLInsert._AssignmentPartitionSpec<C, F>
            , MySQLInsert._MySQLAssignmentSetSpec<C, F>, ClauseBeforeRowAlias<C, F>, MySQLInsert
            , _MySQLInsert._MySQLAssignmentInsert, _MySQLInsert._InsertWithRowAlias {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<String> childPartitionList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private Map<?, _Expression> valuePairsForDuplicate;

        private Boolean prepared;

        private MySQLAssignmentInsertStatement(AssignmentInsertOptionClause<C> clause, TableMeta<?> table) {
            super(clause, false, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._MySQLAssignmentSetClause<C, F>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._AssignmentChildPartitionSpec<C, F>> parentPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::parentPartitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._MySQLAssignmentSetClause<C, F>> childPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::childPartitionEnd);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyRowAliasClause<C, F> as(String alias) {
            this.endAssignmentSetClause();
            return new NonParentRowAliasClause<>(alias, this);
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, F, MySQLInsert._StaticAssignmentCommaFieldSpec<C, F>> onDuplicateKey() {
            this.endAssignmentSetClause();
            return new NonParentAsRowAliasSpec<>(this)
                    .onDuplicateKey();
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<PairConsumer<F>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentAsRowAliasSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<F>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentAsRowAliasSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<PairConsumer<F>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentAsRowAliasSpec<>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<F>> consumer) {
            this.endAssignmentSetClause();
            return new NonParentAsRowAliasSpec<>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert asInsert() {
            return this.endInsert(Collections.emptyMap());
        }

        @Override
        public void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
            super.clear();
            this.partitionList = null;

            this.childPartitionList = null;
            this.rowAlias = null;
            this.aliasToField = null;
            this.valuePairsForDuplicate = null;
        }

        @Override
        public List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public List<MySQLWords> modifierList() {
            return this.modifierList;
        }

        @Override
        public List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        @Override
        public List<String> childPartitionList() {
            List<String> list = this.childPartitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        @Override
        public Map<?, _Expression> valuePairsForDuplicate() {
            Map<?, _Expression> map = this.valuePairsForDuplicate;
            if (map == null) {
                map = Collections.emptyMap();
            }
            return map;
        }

        @Override
        public String rowAlias() {
            return this.rowAlias;
        }

        @Override
        public Map<String, FieldMeta<?>> aliasToField() {
            Map<String, FieldMeta<?>> map = this.aliasToField;
            if (map == null) {
                map = Collections.emptyMap();
            }
            return map;
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F> rowAliasEnd(final String rowAlias
                , final Map<String, FieldMeta<?>> aliasToField) {
            this.rowAlias = rowAlias;
            this.aliasToField = aliasToField;
            return new NonParentDuplicateKeyUpdateAliasSpec<>(aliasToField, this);
        }

        @Override
        public Insert endInsert(final Map<?, _Expression> valuePairMap) {
            _Assert.nonPrepared(this.prepared);
            this.endAssignmentSetClause();
            CriteriaContextStack.clearContextStack(this.criteriaContext);
            this.valuePairsForDuplicate = valuePairMap;

            this.prepared = Boolean.TRUE;
            return this;
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

        private MySQLInsert._MySQLAssignmentSetClause<C, F> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLInsert._AssignmentChildPartitionSpec<C, F> parentPartitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLInsert._AssignmentParentPartitionSpec<C, F> childPartitionEnd(List<String> partitionList) {
            this.childPartitionList = partitionList;
            return this;
        }


    }//AssignmentInsertPartitionClause


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
        public <T extends IDomain> MySQLInsert._QueryPartitionSpec<C, FieldMeta<T>> into(SingleTableMeta<T> table) {
            return new QueryInsertSpaceClause<>(this, table);
        }

        @Override
        public <P extends IDomain, T extends IDomain> MySQLInsert._QueryParentPartitionSpec<C, P, T> into(ComplexTableMeta<P, T> table) {
            return new QueryInsertParentSpaceClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._QueryPartitionSpec<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table) {
            return new QueryInsertSpaceClause<>(this, table);
        }

        @Override
        public <P extends IDomain, T extends IDomain> MySQLInsert._QueryParentPartitionSpec<C, P, T> insertInto(ComplexTableMeta<P, T> table) {
            return new QueryInsertParentSpaceClause<>(this, table);
        }

        private List<Hint> hintList() {
            List<Hint> list = this.hintList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        private List<MySQLWords> modifierList() {
            List<MySQLWords> list = this.modifierList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }


    }//RowSetInsertIntoClause


    /**
     * <p>
     * This class is a the implementation of MySQL RowSet insert syntax after INTO clause,for {@link  SingleTableMeta}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT Statement</a>
     */
    private static class QueryInsertSpaceClause<C, F extends TableField>
            extends InsertSupport.ColumnsClause<C, F, MySQLInsert._QuerySpaceSubQueryClause<C, F>>
            implements MySQLInsert._QueryPartitionSpec<C, F>, MySQLInsert._QuerySpaceSubQueryClause<C, F>
            , ClauseBeforeRowAlias<C, F> {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private SubQuery subQuery;

        private QueryInsertSpaceClause(QueryInsertIntoClause<C> clause, SingleTableMeta<?> table) {
            super(clause.criteriaContext, true, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();

        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._QueryColumnListClause<C, F>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, F> space(Supplier<? extends SubQuery> supplier) {
            final SubQuery subQuery;
            subQuery = supplier.get();
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new NonParentAsRowAliasSpec<>(this);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, F> space(Function<C, ? extends SubQuery> function) {
            final SubQuery subQuery;
            subQuery = function.apply(this.criteria);
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new NonParentAsRowAliasSpec<>(this);
        }


        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F> rowAliasEnd(String rowAlias
                , Map<String, FieldMeta<?>> aliasToField) {
            // query insert no AS row_alias clause
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

        @Override
        public Insert endInsert(final Map<?, _Expression> valuePairMap) {
            if (this.subQuery == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final Insert._InsertSpec insertSpec;
            if (valuePairMap.size() == 0) {
                insertSpec = new MySQLQueryInsertStatement(this);
            } else {
                insertSpec = new QueryInsertStatementWithDuplicate(this, valuePairMap);
            }
            return insertSpec.asInsert();
        }

        @Override
        MySQLInsert._QuerySpaceSubQueryClause<C, F> columnListEnd(int fieldSize, int childFieldSize) {
            if (fieldSize == 0) {
                throw noColumnList(this.criteriaContext, this.table);
            }
            if (childFieldSize > 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }

        List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        private MySQLInsert._QueryColumnListClause<C, F> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

    }//QueryInsertSpaceClause


    private static final class QueryInsertParentSpaceClause<C, P extends IDomain, T extends IDomain>
            extends InsertSupport.ColumnsClause<C, FieldMeta<P>, MySQLInsert._QueryParentQuerySpec<C, FieldMeta<T>>>
            implements MySQLInsert._QueryParentPartitionSpec<C, P, T>
            , MySQLInsert._QueryParentQuerySpec<C, FieldMeta<T>> {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final ChildTableMeta<T> childTable;

        private List<String> parentPartitionList;

        private SubQuery subQuery;

        private QueryInsertParentSpaceClause(QueryInsertIntoClause<C> clause, ComplexTableMeta<P, T> table) {
            super(clause.criteriaContext, true, table.parentMeta());
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
            this.childTable = table;

        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._QueryParentColumnListClause<C, FieldMeta<P>, FieldMeta<T>>> parentPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::parentPartitionEnd);
        }

        @Override
        public MySQLInsert._QueryChildSubQueryPartitionSpec<C, FieldMeta<T>> space(Supplier<? extends SubQuery> supplier) {
            final SubQuery subQuery;
            subQuery = supplier.get();
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new QueryInsertChildSpaceClause<>(this);
        }

        @Override
        public MySQLInsert._QueryChildSubQueryPartitionSpec<C, FieldMeta<T>> space(Function<C, ? extends SubQuery> function) {
            final SubQuery subQuery;
            subQuery = function.apply(this.criteria);
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new QueryInsertChildSpaceClause<>(this);
        }


        @Override
        MySQLInsert._QueryParentQuerySpec<C, FieldMeta<T>> columnListEnd(int fieldSize, int childFieldSize) {
            if (fieldSize == 0) {
                throw noColumnList(this.criteriaContext, this.table);
            }
            if (childFieldSize > 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }

        List<String> partitionList() {
            List<String> list = this.parentPartitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        private MySQLInsert._QueryParentColumnListClause<C, FieldMeta<P>, FieldMeta<T>> parentPartitionEnd(List<String> partitionList) {
            this.parentPartitionList = partitionList;
            return this;
        }


    }//QueryInsertParentSpaceClause


    private static final class QueryInsertChildSpaceClause<C, F extends TableField>
            extends InsertSupport.ColumnsClause<C, F, MySQLInsert._QuerySpaceSubQueryClause<C, F>>
            implements MySQLInsert._QueryChildSubQueryPartitionSpec<C, F>
            , MySQLInsert._QuerySpaceSubQueryClause<C, F>
            , ClauseBeforeRowAlias<C, F> {

        private final QueryInsertParentSpaceClause<?, ?, ?> parentClause;

        private List<String> childPartitionList;

        private SubQuery subQuery;

        private QueryInsertChildSpaceClause(QueryInsertParentSpaceClause<C, ?, ?> clause) {
            super(clause.criteriaContext, clause.migration, clause.childTable);
            this.parentClause = clause;
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._QueryColumnListClause<C, F>> childPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::childPartitionEnd);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, F> space(Supplier<? extends SubQuery> supplier) {
            final SubQuery subQuery;
            subQuery = supplier.get();
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new NonParentAsRowAliasSpec<>(this);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, F> space(Function<C, ? extends SubQuery> function) {
            final SubQuery subQuery;
            subQuery = function.apply(this.criteria);
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new NonParentAsRowAliasSpec<>(this);
        }


        @Override
        MySQLInsert._QuerySpaceSubQueryClause<C, F> columnListEnd(int fieldSize, int childFieldSize) {
            if (fieldSize > 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (childFieldSize == 0) {
                throw noColumnList(this.criteriaContext, this.table);
            }
            return this;
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F> rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField) {
            // query insert no AS row_alias clause
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

        @Override
        public Insert endInsert(final Map<?, _Expression> valuePairMap) {
            if (this.subQuery == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final Insert._InsertSpec spec;
            if (valuePairMap.size() == 0) {
                spec = new MySQLQueryInsertStatement(this);
            } else {
                spec = new QueryInsertStatementWithDuplicate(this, valuePairMap);
            }
            return spec.asInsert();
        }

        List<String> partitionList() {
            List<String> list = this.childPartitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        private MySQLInsert._QueryColumnListClause<C, F> childPartitionEnd(List<String> partitionList) {
            this.childPartitionList = partitionList;
            return this;
        }

    }//QueryInsertChildSpaceClause


    static class MySQLQueryInsertStatement extends QueryInsertStatement<Insert>
            implements MySQLInsert, Insert._InsertSpec, _MySQLInsert._MySQQueryInsert {


        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private final List<String> partitionList;

        private final List<String> childPartitionList;

        private MySQLQueryInsertStatement(QueryInsertSpaceClause<?, ?> clause) {
            super(clause, clause.subQuery);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = clause.partitionList();
            this.childPartitionList = Collections.emptyList();

        }

        private MySQLQueryInsertStatement(QueryInsertChildSpaceClause<?, ?> clause) {
            super(clause.parentClause, clause.parentClause.subQuery, clause, clause.subQuery);

            this.hintList = clause.parentClause.hintList;
            this.modifierList = clause.parentClause.modifierList;
            this.partitionList = clause.parentClause.partitionList();
            this.childPartitionList = clause.partitionList();
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
        public final List<String> childPartitionList() {
            return this.childPartitionList;
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


    }//RowSetInsertStatement

    private static final class QueryInsertStatementWithDuplicate extends MySQLQueryInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {

        private final Map<?, _Expression> valuePairMap;

        private QueryInsertStatementWithDuplicate(QueryInsertSpaceClause<?, ?> clause
                , Map<?, _Expression> valuePairMap) {
            super(clause);
            this.valuePairMap = valuePairMap;
        }

        private QueryInsertStatementWithDuplicate(QueryInsertChildSpaceClause<?, ?> clause
                , Map<?, _Expression> valuePairMap) {
            super(clause);
            this.valuePairMap = valuePairMap;
        }

        @Override
        public Map<?, _Expression> valuePairsForDuplicate() {
            return this.valuePairMap;
        }


    }//SubQueryInsertStatementWithDuplicate


}
