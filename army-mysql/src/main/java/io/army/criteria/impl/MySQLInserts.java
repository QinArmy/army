package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._Assert;

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


    interface ClauseBeforeRowAlias<C, F extends TableField> extends ColumnListClause {

        /**
         * @param aliasToField a unmodified map,non-empty
         */
        MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F> rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField);

        /**
         * @param valuePairMap a unmodified map,empty is allowed.
         */
        Insert endInsert(Map<?, _Expression> valuePairMap);

    }


    @SuppressWarnings("unchecked")
    private static abstract class MySQLInsertClause<C, MR, NR, IR> extends InsertOptionsImpl<MR, NR>
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


    /**
     * <p>
     * This class is a implementation of {@link MySQLInsert._AsRowAliasSpec}
     * </p>
     */
    private static final class AsRowAliasSpec<C, F extends TableField>
            implements MySQLInsert._AsRowAliasSpec<C, F>
            , MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, F, MySQLInsert._StaticAssignmentCommaFieldSpec<C, F>>
            , MySQLInsert._StaticAssignmentCommaFieldSpec<C, F>, PairConsumer<F> {


        final CriteriaContext criteriaContext;

        final C criteria;

        private final ClauseBeforeRowAlias<C, F> clause;

        private boolean optionalOnDuplicateKey = true;

        private Map<FieldMeta<?>, _Expression> valuePairMap;


        private AsRowAliasSpec(ClauseBeforeRowAlias<C, F> clause) {
            this.criteriaContext = clause.getCriteriaContext();
            this.criteria = this.criteriaContext.criteria();
            this.clause = clause;
        }


        @Override
        public MySQLInsert._OnDuplicateKeyRowAliasClause<C, F> as(String alias) {
            if (this.valuePairMap != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            CriteriaContextStack.assertNonNull(this.criteriaContext, alias, "row alias must be non-null");
            this.valuePairMap = Collections.emptyMap();
            return new RowAliasClause<>(alias, this.clause);
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, F, MySQLInsert._StaticAssignmentCommaFieldSpec<C, F>> onDuplicateKey() {
            this.optionalOnDuplicateKey = false;
            return this;
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<PairConsumer<F>> consumer) {
            this.optionalOnDuplicateKey = false;
            consumer.accept(this);
            return this;
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<F>> consumer) {
            this.optionalOnDuplicateKey = false;
            consumer.accept(this.criteria, this);
            return this;
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<PairConsumer<F>> consumer) {
            this.optionalOnDuplicateKey = true;
            consumer.accept(this);
            return this;
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<F>> consumer) {
            this.optionalOnDuplicateKey = true;
            consumer.accept(this.criteria, this);
            return this;
        }

        @Override
        public MySQLInsert._StaticAssignmentCommaFieldSpec<C, F> update(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public MySQLInsert._StaticAssignmentCommaFieldSpec<C, F> updateLiteral(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public MySQLInsert._StaticAssignmentCommaFieldSpec<C, F> updateExp(F field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return this;
        }

        @Override
        public MySQLInsert._StaticAssignmentCommaFieldSpec<C, F> updateExp(F field, Function<C, ? extends Expression> function) {
            this.addValuePair(field, function.apply(this.criteria));
            return this;
        }

        @Override
        public MySQLInsert._StaticAssignmentCommaFieldSpec<C, F> comma(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public MySQLInsert._StaticAssignmentCommaFieldSpec<C, F> commaLiteral(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public MySQLInsert._StaticAssignmentCommaFieldSpec<C, F> commaExp(F field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return this;
        }

        @Override
        public MySQLInsert._StaticAssignmentCommaFieldSpec<C, F> commaExp(F field, Function<C, ? extends Expression> function) {
            this.addValuePair(field, function.apply(this.criteria));
            return this;
        }

        @Override
        public PairConsumer<F> accept(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public PairConsumer<F> acceptLiteral(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public PairConsumer<F> acceptExp(F field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return this;
        }


        @Override
        public Insert asInsert() {
            Map<FieldMeta<?>, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap instanceof HashMap) {
                valuePairMap = Collections.unmodifiableMap(valuePairMap);
                this.valuePairMap = valuePairMap;
            } else if (valuePairMap != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            } else if (this.optionalOnDuplicateKey) {
                valuePairMap = Collections.emptyMap();
                this.valuePairMap = valuePairMap;
            } else {
                String m = "You use non-if onDuplicateKey clause,but don't add any field and value pair.";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            return this.clause.endInsert(valuePairMap);
        }


        private void addValuePair(final F field, final @Nullable Expression value) {
            if (!this.clause.contain((FieldMeta<?>) field)) {
                throw notContainField(this.criteriaContext, (FieldMeta<?>) field);
            }
            CriteriaContextStack.assertFunctionExp(this.criteriaContext, value);

            Map<FieldMeta<?>, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap == null) {
                valuePairMap = new HashMap<>();
                this.valuePairMap = valuePairMap;
            }
            if (valuePairMap.putIfAbsent((FieldMeta<?>) field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.criteriaContext, (FieldMeta<?>) field);
            }

        }


    }//DuplicateKeyUpdateClause


    /**
     * @see AsRowAliasSpec#as(String)
     */
    private static final class RowAliasClause<C, F extends TableField>
            implements MySQLInsert._OnDuplicateKeyRowAliasClause<C, F>
            , Statement._RightParenClause<MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>>
            , MySQLInsert._ColumnAliasClause<F, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>> {


        private final CriteriaContext criteriaContext;

        private final String rowAlias;

        private final ClauseBeforeRowAlias<C, F> clause;

        private Map<FieldMeta<?>, Boolean> fieldMap = new HashMap<>();

        private Map<String, FieldMeta<?>> aliasToField = new HashMap<>();

        private RowAliasClause(String rowAlias, ClauseBeforeRowAlias<C, F> clause) {
            this.criteriaContext = clause.getCriteriaContext();
            this.rowAlias = rowAlias;
            this.clause = clause;
        }

        @Override
        public Statement._RightParenClause<MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>> leftParen(Consumer<BiConsumer<F, String>> consumer) {
            consumer.accept(this::addFieldAlias);
            return this;
        }

        @Override
        public Statement._RightParenClause<MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>> leftParen(BiConsumer<C, BiConsumer<F, String>> consumer) {
            consumer.accept(this.criteriaContext.criteria(), this::addFieldAlias);
            return this;
        }

        @Override
        public MySQLInsert._ColumnAliasClause<F, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>> leftParen(F field, String columnAlias) {
            this.addFieldAlias(field, columnAlias);
            return this;
        }

        @Override
        public MySQLInsert._ColumnAliasClause<F, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>> comma(F field, String columnAlias) {
            this.addFieldAlias(field, columnAlias);
            return this;
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F> rightParen() {
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

        private void addFieldAlias(final F field, final @Nullable String columnAlias) {
            if (!this.clause.contain((FieldMeta<?>) field)) {
                throw notContainField(this.criteriaContext, (FieldMeta<?>) field);
            }
            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (fieldMap.putIfAbsent((FieldMeta<?>) field, Boolean.TRUE) != null) {
                String m = String.format("duplication column alias[%s] for %s", columnAlias, field);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            if (columnAlias == null) {
                String m = String.format("%s columnAlis is null", field);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }

            if (this.aliasToField.putIfAbsent(columnAlias, (FieldMeta<?>) field) != null) {
                String m = String.format("column alis[%s] duplication", columnAlias);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }


        }


    }//OnDuplicateKeyRowAliasListClause


    private static final class OnDuplicateKeyUpdateAliasSpec<C, F extends TableField>
            implements MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>
            , MySQLInsert._StaticOnDuplicateKeyAliasUpdateClause<C, F, MySQLInsert._StaticCommaAliasValuePairSpec<C, F>>
            , MySQLInsert._StaticCommaAliasValuePairSpec<C, F>
            , AliasColumnConsumer<F> {

        private final CriteriaContext criteriaContext;

        private final C criteria;

        private final ClauseBeforeRowAlias<C, F> clause;

        private final Map<String, FieldMeta<?>> aliasToField;

        private Map<Object, _Expression> valuePairMap;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        private boolean optionalOnDuplicateKeyClause = true;

        private OnDuplicateKeyUpdateAliasSpec(Map<String, FieldMeta<?>> aliasToField
                , ClauseBeforeRowAlias<C, F> clause) {
            this.criteriaContext = clause.getCriteriaContext();
            this.criteria = this.criteriaContext.criteria();
            this.aliasToField = aliasToField;
            this.clause = clause;

        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyAliasUpdateClause<C, F, MySQLInsert._StaticCommaAliasValuePairSpec<C, F>> onDuplicateKey() {
            this.optionalOnDuplicateKeyClause = false;
            return this;
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<AliasColumnConsumer<F>> consumer) {
            this.optionalOnDuplicateKeyClause = false;
            consumer.accept(this);
            return this;
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, AliasColumnConsumer<F>> consumer) {
            this.optionalOnDuplicateKeyClause = false;
            consumer.accept(this.criteria, this);
            return this;
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<AliasColumnConsumer<F>> consumer) {
            this.optionalOnDuplicateKeyClause = true;
            consumer.accept(this);
            return this;
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, AliasColumnConsumer<F>> consumer) {
            this.optionalOnDuplicateKeyClause = true;
            consumer.accept(this.criteria, this);
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> update(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> updateLiteral(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> updateExp(F field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> updateExp(F field, Function<C, ? extends Expression> function) {
            this.addValuePair(field, function.apply(this.criteria));
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> update(String columnAlias, @Nullable Object value) {
            this.accept(columnAlias, value);
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> updateLiteral(String columnAlias, @Nullable Object value) {
            this.acceptLiteral(columnAlias, value);
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> updateExp(String columnAlias, Supplier<? extends Expression> supplier) {
            this.addValuePair(columnAlias, supplier.get());
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> updateExp(String columnAlias, Function<C, ? extends Expression> function) {
            this.addValuePair(columnAlias, function.apply(this.criteria));
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> comma(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> commaLiteral(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> commaExp(F field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> commaExp(F field, Function<C, ? extends Expression> function) {
            this.addValuePair(field, function.apply(this.criteria));
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> comma(String columnAlias, @Nullable Object value) {
            this.accept(columnAlias, value);
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> commaLiteral(String columnAlias, @Nullable Object value) {
            this.acceptLiteral(columnAlias, value);
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> commaExp(String columnAlias, Supplier<? extends Expression> supplier) {
            this.addValuePair(columnAlias, supplier.get());
            return this;
        }

        @Override
        public MySQLInsert._StaticCommaAliasValuePairSpec<C, F> commaExp(String columnAlias, Function<C, ? extends Expression> function) {
            this.addValuePair(columnAlias, function.apply(this.criteria));
            return this;
        }

        @Override
        public AliasColumnConsumer<F> accept(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public AliasColumnConsumer<F> acceptLiteral(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public AliasColumnConsumer<F> acceptExp(F field, Supplier<? extends Expression> supplier) {
            this.addValuePair(field, supplier.get());
            return this;
        }

        @Override
        public AliasColumnConsumer<F> accept(String columnAlias, @Nullable Object value) {
            final FieldMeta<?> field;
            field = this.aliasToField.get(columnAlias);
            if (field == null) {
                throw this.unknownColumnAlias(columnAlias);
            }
            this.addValuePair(columnAlias, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public AliasColumnConsumer<F> acceptLiteral(String columnAlias, @Nullable Object value) {
            final FieldMeta<?> field;
            field = this.aliasToField.get(columnAlias);
            if (field == null) {
                throw this.unknownColumnAlias(columnAlias);
            }
            this.addValuePair(columnAlias, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public AliasColumnConsumer<F> acceptExp(String columnAlias, Supplier<? extends Expression> supplier) {
            this.addValuePair(columnAlias, supplier.get());
            return this;
        }

        @Override
        public Insert asInsert() {
            Map<Object, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap instanceof HashMap) {
                valuePairMap = Collections.unmodifiableMap(valuePairMap);
                this.valuePairMap = valuePairMap;
                this.fieldMap = null;
            } else if (valuePairMap != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            } else if (this.optionalOnDuplicateKeyClause) {
                valuePairMap = Collections.emptyMap();
                this.valuePairMap = valuePairMap;
                this.fieldMap = null;
            } else {
                String m = "Your use non-if onDuplicateKey clause but don't add any value pair.";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            return this.clause.endInsert(valuePairMap);
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
                if (!this.clause.contain(field)) {
                    throw notContainField(this.criteriaContext, field);
                }
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
            CriteriaContextStack.assertFunctionExp(this.criteriaContext, value);

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


    }//OnDuplicateKeyUpdateAliasSpec


    /*-------------------below domain insert syntax classes-------------------*/

    private static final class DomainInsertOptionClause<C>
            extends MySQLInsertClause<
            C,
            MySQLInsert._DomainNullOptionSpec<C>,
            MySQLInsert._DomainPreferLiteralSpec<C>,
            MySQLInsert._DomainIntoClause<C>>
            implements MySQLInsert._DomainOptionSpec<C>, MySQLInsert._DomainIntoClause<C>, NonQueryInsertOptions {

        private boolean preferLiteral;


        private DomainInsertOptionClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public MySQLInsert._DomainInsertIntoSpec<C> preferLiteral(boolean prefer) {
            this.preferLiteral = prefer;
            return this;
        }


        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T, FieldMeta<T>> into(SingleTableMeta<T> table) {
            return new DomainInsertPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T, FieldMeta<? super T>> into(ChildTableMeta<T> table) {
            return new DomainInsertPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T, FieldMeta<T>> insertInto(SingleTableMeta<T> table) {
            return new DomainInsertPartitionClause<>(this, table);
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table) {
            return new DomainInsertPartitionClause<>(this, table);
        }

        @Override
        public boolean isPreferLiteral() {
            return this.preferLiteral;
        }


    }//DomainOptionClause


    private static final class DomainInsertPartitionClause<C, T extends IDomain, F extends TableField>
            extends InsertSupport.DomainValueClause<
            C,
            T,
            F,
            MySQLInsert._DomainCommonExpSpec<C, T, F>,
            MySQLInsert._AsRowAliasSpec<C, F>>
            implements MySQLInsert._DomainPartitionSpec<C, T, F>
            , MySQLInsert._DomainParentPartitionSpec<C, T, F>
            , ClauseBeforeRowAlias<C, F> {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private List<String> partitionList;

        private List<String> childPartitionList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private DomainInsertPartitionClause(DomainInsertOptionClause<C> clause, TableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();

        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._DomainColumnListSpec<C, T, F>> partition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::partitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._DomainChildPartitionSpec<C, T, F>> parentPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::parentPartitionEnd);
        }

        @Override
        public MySQLQuery._PartitionLeftParenClause<C, MySQLInsert._DomainColumnListSpec<C, T, F>> childPartition() {
            return new MySQLPartitionClause<>(this.criteriaContext, this::childPartitionEnd);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F> rowAliasEnd(final String rowAlias
                , final Map<String, FieldMeta<?>> aliasToField) {
            this.rowAlias = rowAlias;
            this.aliasToField = aliasToField;
            return new OnDuplicateKeyUpdateAliasSpec<>(aliasToField, this);
        }

        @Override
        public Insert endInsert(Map<?, _Expression> valuePairMap) {
            final Insert insert;
            if (valuePairMap.size() == 0) {
                insert = new MySQLDomainInsertStatement(this)
                        .asInsert();
            } else if (this.rowAlias == null) {
                insert = new MySQLDomainInsertWithDuplicateKey(valuePairMap, this)
                        .asInsert();
            } else {
                insert = new MySQLDomainInsertWIthRowAlias(valuePairMap, this)
                        .asInsert();
            }
            return insert;
        }


        @Override
        MySQLInsert._DomainCommonExpSpec<C, T, F> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }


        @Override
        MySQLInsert._AsRowAliasSpec<C, F> valuesEnd() {
            return new AsRowAliasSpec<>(this);
        }


        private MySQLInsert._DomainColumnListSpec<C, T, F> partitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLInsert._DomainChildPartitionSpec<C, T, F> parentPartitionEnd(List<String> partitionList) {
            this.partitionList = partitionList;
            return this;
        }

        private MySQLInsert._DomainColumnListSpec<C, T, F> childPartitionEnd(List<String> partitionList) {
            this.childPartitionList = partitionList;
            return this;
        }


    }//DomainInsertPartitionClause


    static class MySQLDomainInsertStatement extends ValueSyntaxStatement<Insert>
            implements MySQLInsert, _MySQLInsert._MySQLDomainInsert, Insert._InsertSpec {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;
        private final boolean preferLiteral;

        private final List<String> partitionList;

        private final List<String> childPartitionList;

        private final List<IDomain> domainList;

        private MySQLDomainInsertStatement(DomainInsertPartitionClause<?, ?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.preferLiteral = clause.preferLiteral;
            final List<String> partitionList = clause.partitionList;
            if (partitionList == null) {
                this.partitionList = Collections.emptyList();
            } else {
                this.partitionList = partitionList;
            }
            final List<String> childPartitionList = clause.childPartitionList;
            if (childPartitionList == null) {
                this.childPartitionList = Collections.emptyList();
            } else {
                this.childPartitionList = childPartitionList;
            }
            this.domainList = clause.domainList();
        }

        @Override
        public final boolean isPreferLiteral() {
            return this.preferLiteral;
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
        public final List<IDomain> domainList() {
            return this.domainList;
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


    private static class MySQLDomainInsertWithDuplicateKey extends MySQLDomainInsertStatement
            implements _MySQLInsert._InsertWithDuplicateKey {


        private final Map<?, _Expression> valuePairMap;

        private MySQLDomainInsertWithDuplicateKey(Map<?, _Expression> valuePairMap
                , DomainInsertPartitionClause<?, ?, ?> clause) {
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

        private MySQLDomainInsertWIthRowAlias(Map<?, _Expression> valuePairMap
                , DomainInsertPartitionClause<?, ?, ?> clause) {
            super(valuePairMap, clause);
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
            MySQLInsert._ValueCommonExpSpec<C, F>,
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
            return new OnDuplicateKeyUpdateAliasSpec<>(aliasToField, this);
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
        MySQLInsert._ValueCommonExpSpec<C, F> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }

        @Override
        public MySQLInsert._AsRowAliasSpec<C, F> valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowValuesList) {
            if (this.rowValuesList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValuesList;
            return new AsRowAliasSpec<>(this);
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
            return new RowAliasClause<>(alias, this);
        }

        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, F, MySQLInsert._StaticAssignmentCommaFieldSpec<C, F>> onDuplicateKey() {
            this.endAssignmentSetClause();
            return new AsRowAliasSpec<>(this)
                    .onDuplicateKey();
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<PairConsumer<F>> consumer) {
            this.endAssignmentSetClause();
            return new AsRowAliasSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, PairConsumer<F>> consumer) {
            this.endAssignmentSetClause();
            return new AsRowAliasSpec<>(this)
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<PairConsumer<F>> consumer) {
            this.endAssignmentSetClause();
            return new AsRowAliasSpec<>(this)
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, PairConsumer<F>> consumer) {
            this.endAssignmentSetClause();
            return new AsRowAliasSpec<>(this)
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
            return new OnDuplicateKeyUpdateAliasSpec<>(aliasToField, this);
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
            return new AsRowAliasSpec<>(this);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, F> space(Function<C, ? extends SubQuery> function) {
            final SubQuery subQuery;
            subQuery = function.apply(this.criteria);
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new AsRowAliasSpec<>(this);
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
            return new AsRowAliasSpec<>(this);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateFieldSpec<C, F> space(Function<C, ? extends SubQuery> function) {
            final SubQuery subQuery;
            subQuery = function.apply(this.criteria);
            if (subQuery == null) {
                throw subQueryIsNull(this.criteriaContext);
            }
            this.subQuery = subQuery;
            return new AsRowAliasSpec<>(this);
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
