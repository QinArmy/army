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
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
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
        return new ValueOptionClause<>(criteria);
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


    private static final class AsRowAliasSpec<C, F extends TableField>
            implements MySQLInsert._AsRowAliasSpec<C, F>
            , MySQLInsert._StaticOnDuplicateKeyFieldUpdateClause<C, F, MySQLInsert._StaticAssignmentCommaFieldSpec<C, F>>
            , MySQLInsert._StaticAssignmentCommaFieldSpec<C, F>, ColumnConsumer<F> {


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
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
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
        public Insert._InsertSpec onDuplicateKeyUpdate(Consumer<ColumnConsumer<F>> consumer) {
            this.optionalOnDuplicateKey = false;
            consumer.accept(this);
            return this;
        }
        @Override
        public Insert._InsertSpec onDuplicateKeyUpdate(BiConsumer<C, ColumnConsumer<F>> consumer) {
            this.optionalOnDuplicateKey = false;
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
            if (!this.clause.containField((FieldMeta<?>) field)) {
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
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
            }
            return this.clause.rowAliasEnd(this.rowAlias, aliasToField);
        }

        private void addFieldAlias(final F field, final @Nullable String columnAlias) {
            if (!this.clause.containField((FieldMeta<?>) field)) {
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
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
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
                if (!this.clause.containField(field)) {
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


    /*-------------------below domain insert syntax classes  -------------------*/

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
            return new DomainInsertPartitionClause<>(this, table);
        }
        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T> into(ChildTableMeta<T> table) {
            return new ChildDomainInsertPartitionClause<>(this, table);
        }
        @Override
        public <T extends IDomain> MySQLInsert._DomainPartitionSpec<C, T> insertInto(SingleTableMeta<T> table) {
            return new DomainInsertPartitionClause<>(this, table);
        }
        @Override
        public <T extends IDomain> MySQLInsert._DomainParentPartitionSpec<C, T> insertInto(ChildTableMeta<T> table) {
            return new ChildDomainInsertPartitionClause<>(this, table);
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
            extends InsertSupport.DomainValueClause<C, T, FieldMeta<T>, CR, VR>
            implements MySQLQuery._PartitionClause<C, PR> {

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
        public final PR partition(Consumer<Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(partitionList::add);
            if (partitionList.size() == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, MySQLUtils::partitionListIsEmpty);
            }
            this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
            return (PR) this;
        }

        @Override
        public final PR partition(BiConsumer<C, Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(this.criteria, partitionList::add);
            if (partitionList.size() == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, MySQLUtils::partitionListIsEmpty);
            }
            this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
            return (PR) this;
        }

        @Override
        public final PR ifPartition(Consumer<Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(partitionList::add);
            if (partitionList.size() > 0) {
                this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
            }
            return (PR) this;
        }

        @Override
        public final PR ifPartition(BiConsumer<C, Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(this.criteria, partitionList::add);
            if (partitionList.size() > 0) {
                this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
            }
            return (PR) this;
        }

        final List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }


    }//DomainPartitionClause

    @SuppressWarnings("unchecked")
    static abstract class ChildDomainPartitionClause<C, T extends IDomain, PR, CC, CR, VR>
            extends InsertSupport.DomainValueClause<
            C,
            T,
            FieldMeta<? super T>,
            CR,
            VR>
            implements MySQLInsert._ParentPartitionClause<C, PR>
            , MySQLInsert, MySQLInsert._ChildPartitionClause<C, CC> {

        private List<String> parentPartitionList;

        private List<String> childPartitionList;
        private ChildDomainPartitionClause(InsertOptions clause, TableMeta<T> table) {
            super(clause, table);
        }

        @Override
        public final PR parentPartition(String partitionName) {
            this.parentPartitionList = Collections.singletonList(partitionName);
            return (PR) this;
        }
        @Override
        public final PR parentPartition(String partitionName1, String partitionNam2) {
            this.parentPartitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2);
            return (PR) this;
        }
        @Override
        public final PR parentPartition(String partitionName1, String partitionNam2, String partitionNam3) {
            this.parentPartitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2, partitionNam3);
            return (PR) this;
        }

        @Override
        public final PR parentPartition(Consumer<Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(partitionList::add);
            if (partitionList.size() == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, MySQLUtils::partitionListIsEmpty);
            }
            this.parentPartitionList = _CollectionUtils.unmodifiableList(partitionList);
            return (PR) this;
        }

        @Override
        public final PR parentPartition(BiConsumer<C, Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(this.criteria, partitionList::add);
            if (partitionList.size() == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, MySQLUtils::partitionListIsEmpty);
            }
            this.parentPartitionList = _CollectionUtils.unmodifiableList(partitionList);
            return (PR) this;
        }

        @Override
        public final PR ifParentPartition(Consumer<Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(partitionList::add);
            if (partitionList.size() > 0) {
                this.parentPartitionList = _CollectionUtils.unmodifiableList(partitionList);
            }
            return (PR) this;
        }

        @Override
        public final PR ifParentPartition(BiConsumer<C, Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(this.criteria, partitionList::add);
            if (partitionList.size() > 0) {
                this.parentPartitionList = _CollectionUtils.unmodifiableList(partitionList);
            }
            return (PR) this;
        }

        @Override
        public final CC childPartition(String partitionName) {
            this.childPartitionList = Collections.singletonList(partitionName);
            return (CC) this;
        }
        @Override
        public final CC childPartition(String partitionName1, String partitionNam2) {
            this.childPartitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2);
            return (CC) this;
        }
        @Override
        public final CC childPartition(String partitionName1, String partitionNam2, String partitionNam3) {
            this.childPartitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2, partitionNam3);
            return (CC) this;
        }

        @Override
        public final CC childPartition(Consumer<Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(partitionList::add);
            if (partitionList.size() == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, MySQLUtils::partitionListIsEmpty);
            }
            this.childPartitionList = _CollectionUtils.unmodifiableList(partitionList);
            return (CC) this;
        }

        @Override
        public final CC childPartition(BiConsumer<C, Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(this.criteria, partitionList::add);
            if (partitionList.size() == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, MySQLUtils::partitionListIsEmpty);
            }
            this.childPartitionList = _CollectionUtils.unmodifiableList(partitionList);
            return (CC) this;
        }

        @Override
        public final CC ifChildPartition(Consumer<Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(partitionList::add);
            if (partitionList.size() > 0) {
                this.childPartitionList = _CollectionUtils.unmodifiableList(partitionList);
            }
            return (CC) this;
        }
        @Override
        public final CC ifChildPartition(BiConsumer<C, Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(this.criteria, partitionList::add);
            if (partitionList.size() > 0) {
                this.childPartitionList = _CollectionUtils.unmodifiableList(partitionList);
            }
            return (CC) this;
        }

        final List<String> parentPartitionList() {
            List<String> list = this.parentPartitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

        final List<String> childPartitionList() {
            List<String> list = this.childPartitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }


    }//ChildDomainPartitionClause


    private static final class DomainInsertPartitionClause<C, T extends IDomain> extends DomainPartitionClause<
            C,
            T,
            MySQLInsert._DomainColumnListSpec<C, T, FieldMeta<T>>,
            MySQLInsert._DomainCommonExpSpec<C, T, FieldMeta<T>>,
            MySQLInsert._AsRowAliasSpec<C, FieldMeta<T>>>
            implements MySQLInsert._DomainPartitionSpec<C, T>
            , ClauseBeforeRowAlias<C, FieldMeta<T>> {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private DomainInsertPartitionClause(DomainInsertOptionClause<C> clause, TableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();

        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateAliasSpec<C, FieldMeta<T>> rowAliasEnd(final String rowAlias
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
        public void prepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean isPrepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }

        @Override
        MySQLInsert._DomainCommonExpSpec<C, T, FieldMeta<T>> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }


        @Override
        MySQLInsert._AsRowAliasSpec<C, FieldMeta<T>> valuesEnd() {
            return new AsRowAliasSpec<>(this);
        }


    }//DomainInsertPartitionClause


    private static final class ChildDomainInsertPartitionClause<C, T extends IDomain>
            extends ChildDomainPartitionClause<
            C,
            T,
            MySQLInsert._DomainChildPartitionSpec<C, T>,
            MySQLInsert._DomainColumnListSpec<C, T, FieldMeta<? super T>>,
            MySQLInsert._DomainCommonExpSpec<C, T, FieldMeta<? super T>>,
            MySQLInsert._AsRowAliasSpec<C, FieldMeta<? super T>>>
            implements MySQLInsert._DomainParentPartitionSpec<C, T>
            , ClauseBeforeRowAlias<C, FieldMeta<? super T>> {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private ChildDomainInsertPartitionClause(DomainInsertOptionClause<C> clause, ChildTableMeta<T> table) {
            super(clause, table);
            this.hintList = clause.hintList();
            this.modifierList = clause.modifierList();
        }

        @Override
        public void prepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean isPrepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }

        @Override
        public _OnDuplicateKeyUpdateAliasSpec<C, FieldMeta<? super T>> rowAliasEnd(String rowAlias, Map<String, FieldMeta<?>> aliasToField) {
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
        _DomainCommonExpSpec<C, T, FieldMeta<? super T>> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }
        @Override
        _AsRowAliasSpec<C, FieldMeta<? super T>> valuesEnd() {
            return new AsRowAliasSpec<>(this);
        }


    }//ChildDomainInsertPartitionClause


    static class MySQLDomainInsertStatement extends ValueSyntaxStatement
            implements MySQLInsert, _MySQLInsert._MySQLDomainInsert {

        private final List<Hint> hintList;

        private final List<MySQLWords> modifierList;
        private final boolean preferLiteral;

        private final List<String> partitionList;

        private final List<String> childPartitionList;

        private final List<IDomain> domainList;

        private MySQLDomainInsertStatement(DomainInsertPartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.preferLiteral = clause.preferLiteral;
            this.partitionList = clause.partitionList();

            this.childPartitionList = Collections.emptyList();
            this.domainList = clause.domainList();
        }

        private MySQLDomainInsertStatement(ChildDomainInsertPartitionClause<?, ?> clause) {
            super(clause);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.preferLiteral = clause.preferLiteral;
            this.partitionList = clause.parentPartitionList();

            this.childPartitionList = clause.childPartitionList();
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
                , DomainInsertPartitionClause<?, ?> clause) {
            super(clause);
            this.valuePairMap = valuePairMap;
        }

        private MySQLDomainInsertWithDuplicateKey(Map<?, _Expression> valuePairMap
                , ChildDomainInsertPartitionClause<?, ?> clause) {
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
                , DomainInsertPartitionClause<?, ?> clause) {
            super(valuePairMap, clause);
            this.rowAlias = clause.rowAlias;
            this.aliasToField = clause.aliasToField;
        }

        private MySQLDomainInsertWIthRowAlias(Map<?, _Expression> valuePairMap
                , ChildDomainInsertPartitionClause<?, ?> clause) {
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

    private interface ClauseForValueInsert<C, F extends TableField> extends ColumnListClause {

        MySQLInsert._AsRowAliasSpec<C, F> valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> valuePairList);

    }


    private static final class ValueOptionClause<C> extends InsertClause<C, MySQLInsert._ValueIntoClause<C>>
            implements MySQLInsert._ValueOptionSpec<C>, InsertOptions {


        private final CriteriaContext criteriaContext;

        private boolean migration;

        private NullHandleMode nullHandleMode;

        private ValueOptionClause(@Nullable C criteria) {
            super(criteria);
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }


        @Override
        public MySQLInsert._ValueNullOptionSpec<C> migration(boolean migration) {
            this.migration = migration;
            return this;
        }
        @Override
        public MySQLInsert._ValueInsertIntoSpec<C> nullHandle(NullHandleMode mode) {
            this.nullHandleMode = mode;
            return this;
        }

        @Override
        public <T extends IDomain> MySQLInsert._ValuePartitionSpec<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table) {
            return null;
        }
        @Override
        public <T extends IDomain> MySQLInsert._ValueParentPartitionSpec<C, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table) {
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
            // always false
            return false;
        }

        @Override
        public CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }


    }//ValueOptionClause


    @SuppressWarnings("unchecked")
    static abstract class ValuePartitionClause<C, F extends TableField, PR, RR, VR>
            extends InsertSupport.ValueInsertValueClause<C, F, RR, VR>
            implements MySQLQuery._PartitionClause<C, PR> {

        private List<String> partitionList;


        private ValuePartitionClause(InsertOptions options, TableMeta<?> table) {
            super(options, table);
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
        public final PR partition(Consumer<Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(partitionList::add);
            if (partitionList.size() == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, MySQLUtils::partitionListIsEmpty);
            }
            this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
            return (PR) this;
        }

        @Override
        public final PR partition(BiConsumer<C, Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(this.criteria, partitionList::add);
            if (partitionList.size() == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, MySQLUtils::partitionListIsEmpty);
            }
            this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
            return (PR) this;
        }

        @Override
        public final PR ifPartition(Consumer<Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(partitionList::add);
            if (partitionList.size() > 0) {
                this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
            }
            return (PR) this;
        }

        @Override
        public final PR ifPartition(BiConsumer<C, Consumer<String>> consumer) {
            final List<String> partitionList = new ArrayList<>();
            consumer.accept(this.criteria, partitionList::add);
            if (partitionList.size() > 0) {
                this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
            }
            return (PR) this;
        }

        final List<String> partitionList() {
            List<String> list = this.partitionList;
            if (list == null) {
                list = Collections.emptyList();
            }
            return list;
        }

    }//ValuePartitionClause


    private static final class StaticValueLeftParenClause<C, F extends TableField>
            extends InsertSupport.StaticColumnValuePairClause<C, F, MySQLInsert._AsRowAliasSpec<C, F>> {

        private final ClauseForValueInsert<C, F> clause;

        private Map<FieldMeta<?>, _Expression> valuePairMap = new HashMap<>();

        private StaticValueLeftParenClause(ClauseForValueInsert<C, F> clause) {
            super(clause.getCriteriaContext());
            this.clause = clause;
        }

        @Override
        public MySQLInsert._AsRowAliasSpec<C, F> rightParen() {
            Map<FieldMeta<?>, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap instanceof HashMap && valuePairMap.size() > 0) {
                valuePairMap = Collections.unmodifiableMap(valuePairMap);
                this.valuePairMap = valuePairMap;
            } else {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
            }
            return this.clause.valueClauseEnd(Collections.singletonList(valuePairMap));
        }
        @Override
        void addValuePair(final FieldMeta<?> field, final _Expression value) {
            if (!this.clause.containField(field)) {
                throw notContainField(this.criteriaContext, field);
            }
            final Map<FieldMeta<?>, _Expression> valuePairMap = this.valuePairMap;
            if (!(valuePairMap instanceof HashMap)) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
            }
            if (valuePairMap.putIfAbsent(field, value) != null) {
                throw duplicationValuePair(this.criteriaContext, field);
            }
        }


    }//StaticValueLeftParenClause


    private static final class StaticValuesLeftParenClause<C, F extends TableField>
            extends InsertSupport.StaticColumnValuePairClause<C, F, MySQLInsert._ValueStaticValuesLeftParenSpec<C, F>> {

        private final ClauseForValueInsert<C, F> clause;

        private List<Map<FieldMeta<?>, _Expression>> valuePairList;

        private Map<FieldMeta<?>, _Expression> currentValuePairMap;

        private StaticValuesLeftParenClause(ClauseForValueInsert<C, F> clause) {
            super(clause.getCriteriaContext());
            this.clause = clause;
        }
        @Override
        public MySQLInsert._ValueStaticValuesLeftParenSpec<C, F> rightParen() {
            return null;
        }
        @Override
        void addValuePair(FieldMeta<?> field, _Expression value) {

        }
    }//StaticValuesLeftParenClause


    private static final class ValueInsertPartitionClause<C, F extends TableField>
            extends ValuePartitionClause<
            C,
            F,
            MySQLInsert._ValueColumnListSpec<C, F>,
            MySQLInsert._ValueCommonExpSpec<C, F>,
            MySQLInsert._AsRowAliasSpec<C, F>>
            implements MySQLInsert._ValuePartitionSpec<C, F>
            , ClauseBeforeRowAlias<C, F>
            , ClauseForValueInsert<C, F> {

        private String rowAlias;

        private Map<String, FieldMeta<?>> aliasToField;

        private ValueInsertPartitionClause(ValueOptionClause<C> clause, TableMeta<?> table) {
            super(clause, table);
        }

        @Override
        public _StaticValueLeftParenClause<C, F, MySQLInsert._AsRowAliasSpec<C, F>> value() {
            return new StaticValueLeftParenClause<>(this);
        }
        @Override
        public MySQLInsert._ValueStaticValuesLeftParenClause<C, F> values() {
            return null;
        }


        @Override
        public void prepared() {
            throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
        }
        @Override
        public boolean isPrepared() {
            throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
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
            return null;
        }
        @Override
        MySQLInsert._ValueCommonExpSpec<C, F> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }
        @Override
        public MySQLInsert._AsRowAliasSpec<C, F> valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> valuePairList) {
            return new AsRowAliasSpec<>(this);
        }


    }//ValueInsertPartitionClause


}
