package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.mysql.MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLWords;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.*;

abstract class MySQLInserts extends InsertSupport {

    private MySQLInserts() {
        throw new UnsupportedOperationException();
    }


    static <C> MySQLInsert._DomainOptionSpec<C> domainInsert(@Nullable C criteria) {
        return new DomainInsertOptionClause<>(criteria);
    }


    /*-------------------below domain insert syntax classes  -------------------*/


    @SuppressWarnings("unchecked")
    private static abstract class InsertClause<C, IR> implements MySQLInsert._InsertClause<C, IR> {


        final C criteria;

        private List<Hint> hintList;

        private List<MySQLWords> modifierList;

        private InsertClause(@Nullable C criteria) {
            this.criteria = criteria;
        }


        @Override
        public final IR insert(Supplier<List<Hint>> supplier, List<MySQLWords> modifiers) {
            final List<Hint> hintList;
            hintList = supplier.get();
            if (hintList == null) {
                this.hintList = Collections.emptyList();
            } else {
                this.hintList = _CollectionUtils.asUnmodifiableList(hintList);
            }
            this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::insertModifier);
            return (IR) this;
        }
        @Override
        public final IR insert(Function<C, List<Hint>> function, List<MySQLWords> modifiers) {
            final List<Hint> hintList;
            hintList = function.apply(this.criteria);
            if (hintList == null) {
                this.hintList = Collections.emptyList();
            } else {
                this.hintList = _CollectionUtils.asUnmodifiableList(hintList);
            }
            this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::insertModifier);
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

    private static final class DomainInsertOptionClause<C> extends InsertClause<C, MySQLInsert._DomainIntoClause<C>>
            implements MySQLInsert._DomainOptionSpec<C>, MySQLInsert._DomainIntoClause<C>, InsertOptions {

        private final CriteriaContext criteriaContext;

        private boolean preferLiteral;

        private boolean migration;

        private NullHandleMode nullHandleMode;

        private DomainInsertOptionClause(@Nullable C criteria) {
            super(criteria);
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public MySQLInsert._DomainInsertIntoSpec<C> preferLiteral(boolean prefer) {
            this.preferLiteral = prefer;
            return this;
        }
        @Override
        public MySQLInsert._DomainNullOptionSpec<C> migration(boolean migration) {
            this.migration = migration;
            return this;
        }
        @Override
        public MySQLInsert._DomainPreferLiteralSpec<C> nullHandle(NullHandleMode mode) {
            this.nullHandleMode = mode;
            return this;
        }

        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> into(SingleTableMeta<T> table) {
            return new DomainPartitionClause<>();
        }
        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T> into(ChildTableMeta<T> table) {
            return null;
        }
        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> insertInto(SingleTableMeta<T> table) {
            return null;
        }
        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T> insertInto(ChildTableMeta<T> table) {
            return null;
        }


        @Override
        public boolean isMigration() {
            return this.migration;
        }
        @Override
        public NullHandleMode nullHandle() {
            return this.nullHandleMode;
        }
        @Override
        public boolean isPreferLiteral() {
            return this.preferLiteral;
        }

        @Override
        public CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }


    }//DomainOptionClause


    @SuppressWarnings("unchecked")
    static abstract class DomainPartitionClause<C, T extends IDomain, PR, CR, VR>
            extends InsertSupport.DomainValueClause<C, T, FieldMeta<T>, CR, VR> implements MySQLQuery._PartitionClause<C, PR>
            , InsertClauseBeforeAsRowAlias<C, FieldMeta<T>> {

        private List<String> partitionList;

        private DomainPartitionClause(InsertOptions clause, TableMeta<T> table) {
            super(clause, table);
        }


        @Override
        public final PR partition(String partitionName) {
            this.partitionList = Collections.singletonList(partitionName);
            return (PR) this;
        }
        @Override
        public final PR partition(String partitionName1, String partitionNam2) {
            this.partitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2);
            return (PR) this;
        }
        @Override
        public final PR partition(String partitionName1, String partitionNam2, String partitionNam3) {
            this.partitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2, partitionNam3);
            return (PR) this;
        }
        @Override
        public final PR partition(Supplier<List<String>> supplier) {
            this.partitionList = MySQLUtils.asStringList(supplier.get(), MySQLUtils::partitionListIsEmpty);
            return (PR) this;
        }
        @Override
        public final PR partition(Function<C, List<String>> function) {
            this.partitionList = MySQLUtils.asStringList(function.apply(this.criteria), MySQLUtils::partitionListIsEmpty);
            return (PR) this;
        }
        @Override
        public final PR partition(Consumer<List<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(partitionList);
            if (partitionList.size() == 0) {
                throw CriteriaContextStack.criteriaError(MySQLUtils::partitionListIsEmpty);
            }
            this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
            return (PR) this;
        }
        @Override
        public final PR ifPartition(Supplier<List<String>> supplier) {
            final List<String> list;
            list = supplier.get();
            if (list != null && list.size() > 0) {
                this.partitionList = MySQLUtils.asStringList(list, MySQLUtils::partitionListIsEmpty);
            }
            return (PR) this;
        }
        @Override
        public final PR ifPartition(Function<C, List<String>> function) {
            final List<String> list;
            list = function.apply(this.criteria);
            if (list != null && list.size() > 0) {
                this.partitionList = MySQLUtils.asStringList(list, MySQLUtils::partitionListIsEmpty);
            }
            return (PR) this;
        }


    }//DomainPartitionClause


    private static final class DomainInsertPartitionClause<C, T extends IDomain> extends DomainPartitionClause<
            C,
            T,
            MySQLInsert._DomainColumnListSpec<C, T, FieldMeta<T>>,
            MySQLInsert._DomainCommonExpSpec<C, T, FieldMeta<T>>,
            MySQLInsert._AsRowAliasSpec<C, FieldMeta<T>>> {

        private DomainInsertPartitionClause(DomainInsertOptionClause<C> clause, TableMeta<T> table) {
            super(clause, table);
        }

        @Override
        public void prepared() {

        }
        @Override
        public boolean isPrepared() {
            return false;
        }
        @Override
        public CriteriaContext getCriteriaContext() {
            return null;
        }
        @Override
        MySQLInsert._DomainCommonExpSpec<C, T, FieldMeta<T>> columnListEnd(int fieldSize, int childFieldSize) {
            return null;
        }
        @Override
        public Predicate<FieldMeta<?>> containField() {
            return this::containField;
        }
        @Override
        public Function<Map<?, _Expression>, Insert> endFunction() {
            return null;
        }
        @Override
        public Function<Map<String, FieldMeta<?>>, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, FieldMeta<T>>> function() {
            return null;
        }


        @Override
        MySQLInsert._AsRowAliasSpec<C, FieldMeta<T>> valuesEnd() {
            return new AsRowAliasSpec<>(this);
        }


    }//DomainInsertPartitionClause


    private interface InsertClauseBeforeAsRowAlias<C, F extends TableField> extends CriteriaContextSpec {

        Predicate<FieldMeta<?>> containField();

        Function<Map<?, _Expression>, Insert> endFunction();

        Function<Map<String, FieldMeta<?>>, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>> function();
    }


    private static final class AsRowAliasSpec<C, F extends TableField>
            implements MySQLInsert._AsRowAliasSpec<C, F>
            , MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, F, MySQLInsert._StaticAssignmentCommaFieldSpec<C, F>>
            , MySQLInsert._StaticAssignmentCommaFieldSpec<C, F>, ColumnConsumer<F> {


        final CriteriaContext criteriaContext;

        final C criteria;

        private final Predicate<FieldMeta<?>> containField;

        private final Function<Map<?, _Expression>, Insert> endFunction;

        private final Function<Map<String, FieldMeta<?>>, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>> function;

        private boolean optionalOnDuplicateKey;

        private Map<FieldMeta<?>, _Expression> valuePairMap;


        private AsRowAliasSpec(InsertClauseBeforeAsRowAlias<C, F> clause) {
            this.criteriaContext = clause.getCriteriaContext();
            this.containField = clause.containField();
            this.endFunction = clause.endFunction();
            this.function = clause.function();

            this.criteria = this.criteriaContext.criteria();
        }


        @Override
        public final MySQLInsert._OnDuplicateKeyRowAliasListClause<C, F> as(String alias) {
            if (this.valuePairMap != null) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
            }
            CriteriaContextStack.assertNonNull(this.criteriaContext, alias, "row alias must be non-null");
            this.valuePairMap = Collections.emptyMap();
            return new OnDuplicateKeyRowAliasListClause<>(this);
        }
        @Override
        public MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, F, MySQLInsert._StaticAssignmentCommaFieldSpec<C, F>> onDuplicateKey() {
            return this;
        }
        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<ColumnConsumer<F>> consumer) {
            consumer.accept(this);
            return this;
        }
        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, ColumnConsumer<F>> consumer) {
            consumer.accept(this.criteria, this);
            return this;
        }
        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(Consumer<ColumnConsumer<F>> consumer) {
            this.optionalOnDuplicateKey = true;
            consumer.accept(this);
            return this;
        }
        @Override
        public Insert._InsertSpec ifOnDuplicateKeyUpdate(BiConsumer<C, ColumnConsumer<F>> consumer) {
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
        public ColumnConsumer<F> accept(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }
        @Override
        public ColumnConsumer<F> acceptLiteral(F field, @Nullable Object value) {
            this.addValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }
        @Override
        public ColumnConsumer<F> acceptExp(F field, Supplier<? extends Expression> supplier) {
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
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
            } else if (!this.optionalOnDuplicateKey) {
                String m = "You use non-if onDuplicateKey clause,but don't add any field and value pair.";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else {
                valuePairMap = Collections.emptyMap();
                this.valuePairMap = valuePairMap;
            }
            return this.endFunction.apply(valuePairMap);
        }


        private void addValuePair(final F field, final @Nullable Expression value) {
            if (!this.containField.test((FieldMeta<?>) field)) {
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


    private static final class OnDuplicateKeyRowAliasListClause<C, F extends TableField>
            implements MySQLInsert._OnDuplicateKeyRowAliasListClause<C, F>
            , Statement._RightParenClause<MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>>
            , MySQLInsert._ColumnAliasClause<F, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>> {


        private final CriteriaContext criteriaContext;

        private final Predicate<FieldMeta<?>> containField;

        private final Function<Map<String, FieldMeta<?>>, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>> function;

        private Map<FieldMeta<?>, Boolean> fieldMap = new HashMap<>();

        private Map<String, FieldMeta<?>> aliasToField = new HashMap<>();

        private OnDuplicateKeyRowAliasListClause(AsRowAliasSpec<C, F> clause) {
            this.criteriaContext = clause.criteriaContext;
            this.containField = clause.containField;
            this.function = clause.function;
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
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
            }
            return this.function.apply(aliasToField);
        }

        private void addFieldAlias(final F field, final @Nullable String columnAlias) {
            if (!this.containField.test((FieldMeta<?>) field)) {
                throw notContainField(this.criteriaContext, (FieldMeta<?>) field);
            }
            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
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


    private interface InsertClauseBeforeDuplicateKeyAliasUpdate extends CriteriaContextSpec {

        Predicate<FieldMeta<?>> containField();

        Function<Map<?, _Expression>, Insert> endFunction();

        Map<String, FieldMeta<?>> aliasToField();

    }

    private static final class OnDuplicateKeyUpdateAliasSpec<C, F extends TableField>
            implements MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>
            , MySQLInsert._StaticOnDuplicateKeyAliasUpdateClause<C, F, MySQLInsert._StaticCommaAliasValuePairSpec<C, F>>
            , MySQLInsert._StaticCommaAliasValuePairSpec<C, F>
            , AliasColumnConsumer<F> {

        private final CriteriaContext criteriaContext;

        private final C criteria;

        private final Predicate<FieldMeta<?>> containField;

        private final Function<Map<?, _Expression>, Insert> endFunction;

        private final Map<String, FieldMeta<?>> aliasToField;

        private Map<Object, _Expression> valuePairMap;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        private boolean optionalOnDuplicateKeyClause;

        private OnDuplicateKeyUpdateAliasSpec(InsertClauseBeforeDuplicateKeyAliasUpdate clause) {
            this.criteriaContext = clause.getCriteriaContext();
            this.containField = clause.containField();
            this.endFunction = clause.endFunction();
            this.aliasToField = clause.aliasToField();

            this.criteria = this.criteriaContext.criteria();

        }
        @Override
        public MySQLInsert._StaticOnDuplicateKeyAliasUpdateClause<C, F, MySQLInsert._StaticCommaAliasValuePairSpec<C, F>> onDuplicateKey() {
            return this;
        }
        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<AliasColumnConsumer<F>> consumer) {
            consumer.accept(this);
            return this;
        }
        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, AliasColumnConsumer<F>> consumer) {
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
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
            } else if (this.optionalOnDuplicateKeyClause) {
                valuePairMap = Collections.emptyMap();
                this.valuePairMap = valuePairMap;
                this.fieldMap = null;
            } else {
                String m = "Your use non-if onDuplicateKey clause but don't add any value pair.";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            return this.endFunction.apply(valuePairMap);
        }

        private void addValuePair(final Object fieldOrAlias, final @Nullable Expression value) {
            Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                fieldMap = new HashMap<>();
                this.fieldMap = fieldMap;
            }
            final FieldMeta<?> field;
            if (fieldOrAlias instanceof FieldMeta) {
                field = (FieldMeta<?>) fieldOrAlias;
                if (!this.containField.test(field)) {
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

            CriteriaContextStack.assertFunctionExp(this.criteriaContext, value);

            Map<Object, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap == null) {
                valuePairMap = new HashMap<>();
                this.valuePairMap = valuePairMap;
            }
            valuePairMap.put(fieldOrAlias, (ArmyExpression) value);
        }

        private CriteriaException unknownColumnAlias(@Nullable String columnAlias) {
            String m = String.format("unknown column alias[%s]", columnAlias);
            return CriteriaContextStack.criteriaError(this.criteriaContext, m);
        }


    }//OnDuplicateKeyUpdateAliasSpec


    static final class DomainAsRowAliasSpec<C, F extends TableField> extends AsRowAliasSpec<C, F> {

        private DomainAsRowAliasSpec(DomainValueClause<?, ?> clause) {
            super(clause.criteriaContext, clause::containField);
        }

        @Override
        Insert internalAsInsert(Map<FieldMeta<?>, _Expression> valuePairMap) {
            return null;
        }
        @Override
        Function<Map<FieldMeta<?>, String>, MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, F>> function() {
            return null;
        }


    }//DomainAsRowAliasSpec


    static class DomainInsertStatement extends ValueSyntaxStatement
            implements MySQLInsert {

        private DomainInsertStatement(DomainValueClause<?, ?> clause) {
            super(clause);
        }


    }//DomainInsertStatement


    private static final class DomainInsertStatementWithDuplicateKey extends DomainInsertStatement {

        private DomainInsertStatementWithDuplicateKey(DomainAsRowAliasSpec<?, ?> clause) {
            super(clause.clause);
        }


    }//DomainInsertStatementWithDuplicateKey


}
