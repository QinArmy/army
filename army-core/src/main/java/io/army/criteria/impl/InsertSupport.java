package io.army.criteria.impl;

import io.army.annotation.GeneratorType;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._DialectUtils;
import io.army.dialect._MockDialects;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.Stmt;
import io.army.util._Assert;
import io.army.util._ClassUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class InsertSupport {

    InsertSupport() {
        throw new UnsupportedOperationException();
    }


    interface InsertOptions extends CriteriaContextSpec, _Insert._InsertOption {


    }

    interface ValueSyntaxOptions extends InsertOptions {

        @Nullable
        NullHandleMode nullHandle();

    }


    interface ColumnListClause extends CriteriaContextSpec {

        /**
         * @param value if non-null and not {@link  FieldMeta#nullable()},then validate value isn't non-null expression
         */
        void validateField(FieldMeta<?> field, @Nullable ArmyExpression value);


    }

    static abstract class InsertOptionsImpl<MR, PR> implements InsertOptions, Insert._MigrationOptionClause<MR>
            , Insert._PreferLiteralClause<PR> {

        final CriteriaContext criteriaContext;

        private boolean migration;

        private boolean preferLiteral;

        InsertOptionsImpl(CriteriaContext criteriaContext) {
            this.criteriaContext = criteriaContext;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final MR migration(boolean migration) {
            this.migration = migration;
            return (MR) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final PR preferLiteral(boolean prefer) {
            this.preferLiteral = prefer;
            return (PR) this;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.criteriaContext;
        }

        @Override
        public final boolean isMigration() {
            return this.migration;
        }

        @Override
        public final boolean isPreferLiteral() {
            return this.preferLiteral;
        }


    }//InsertOptionsImpl


    static abstract class NonQueryInsertOptionsImpl<MR, NR, PR> extends InsertOptionsImpl<MR, PR>
            implements ValueSyntaxOptions, Insert._NullOptionClause<NR> {


        private NullHandleMode nullHandleMode;

        NonQueryInsertOptionsImpl(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }

        @SuppressWarnings("unchecked")
        @Override
        public final NR nullHandle(NullHandleMode mode) {
            this.nullHandleMode = mode;
            return (NR) this;
        }

        @Override
        public final NullHandleMode nullHandle() {
            return this.nullHandleMode;
        }

    }//NonQueryInsertOptionsImpl


    static abstract class ColumnsClause<C, T, RR>
            implements Insert._ColumnListClause<C, T, RR>, Insert._StaticColumnDualClause<T, RR>
            , _Insert._ColumnListInsert, ColumnListClause {

        final CriteriaContext criteriaContext;

        final C criteria;

        final boolean migration;

        final TableMeta<T> insertTable;

        private List<FieldMeta<?>> fieldList;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        ColumnsClause(CriteriaContext criteriaContext, boolean migration, @Nullable TableMeta<T> table) {
            if (table == null) {
                //validate for insertInto method
                throw CriteriaContextStack.nullPointer(criteriaContext);
            }
            this.criteriaContext = criteriaContext;
            this.criteria = criteriaContext.criteria();
            this.migration = migration;
            this.insertTable = table;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.criteriaContext;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(Consumer<Consumer<FieldMeta<T>>> consumer) {
            consumer.accept(this::addField);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(BiConsumer<C, Consumer<FieldMeta<T>>> consumer) {
            consumer.accept(this.criteria, this::addField);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(FieldMeta<T> field) {
            this.addField(field);
            return this;
        }

        @Override
        public final Insert._StaticColumnDualClause<T, RR> leftParen(FieldMeta<T> field1, FieldMeta<T> field2) {
            this.addField(field1);
            this.addField(field2);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> comma(FieldMeta<T> field) {
            this.addField(field);
            return this;
        }

        @Override
        public final Insert._StaticColumnDualClause<T, RR> comma(FieldMeta<T> field1, FieldMeta<T> field2) {
            this.addField(field1);
            this.addField(field2);
            return this;
        }

        @Override
        public final RR rightParen() {
            final List<FieldMeta<?>> fieldList = this.fieldList;
            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;

            if (fieldList == null) {
                this.fieldList = Collections.emptyList();
                assert fieldMap == null;
            } else if (!(fieldList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            } else {
                if (this.migration) {
                    validateMigrationColumnList(this.criteriaContext, this.insertTable, fieldMap);
                    assert !(this.insertTable instanceof ChildTableMeta) || fieldMap.containsKey(this.insertTable.id());
                }
                if (fieldList.size() == 1) {
                    this.fieldList = Collections.singletonList(fieldList.get(0));
                } else {
                    this.fieldList = Collections.unmodifiableList(fieldList);
                }
            }

            if (fieldMap == null) {
                this.fieldMap = Collections.emptyMap();
            } else if (fieldMap instanceof HashMap) {
                this.fieldMap = Collections.unmodifiableMap(fieldMap);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this.columnListEnd();
        }


        @Override
        public final TableMeta<?> table() {
            return this.insertTable;
        }

        @Override
        public final List<FieldMeta<?>> fieldList() {
            List<FieldMeta<?>> fieldList = this.fieldList;
            if (fieldList == null) {
                fieldList = Collections.emptyList();
            } else if (fieldList instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return fieldList;
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            Map<FieldMeta<?>, Boolean> map = this.fieldMap;
            if (map == null) {
                map = Collections.emptyMap();
            } else if (map instanceof HashMap) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return map;
        }

        @Override
        public void clear() {
            this.fieldList = null;
            this.fieldMap = null;
        }

        abstract RR columnListEnd();


        @Override
        public final void validateField(final FieldMeta<?> field, final @Nullable ArmyExpression value) {
            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                checkField(this.criteriaContext, this.insertTable, this.migration, field);
            } else if (!fieldMap.containsKey(field)) {
                throw notContainField(this.criteriaContext, field);
            }
            if (value != null && !field.nullable() && value.isNullValue()) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonNullField, field);
            }

        }

        private void addField(final FieldMeta<T> field) {
            checkField(this.criteriaContext, this.insertTable, this.migration, field);
            if (!this.migration && _MetaBridge.VISIBLE.equals(field.fieldName())) {
                String m = String.format("%s is managed by army for column list clause,in non-migration mode."
                        , _MetaBridge.VISIBLE);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }

            Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            List<FieldMeta<?>> fieldList;
            if (fieldMap == null) {
                fieldMap = this.createAndInitializingFieldMap(); // create map and add the fields that is managed by army.
                this.fieldMap = fieldMap;
                fieldList = this.fieldList;
                assert fieldList != null && fieldList.size() == fieldMap.size();
            } else {
                fieldList = this.fieldList;
            }

            if (fieldMap.putIfAbsent(field, Boolean.TRUE) != null) {
                String m = String.format("%s duplication", field);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            fieldList.add(field);

        }

        private Map<FieldMeta<?>, Boolean> createAndInitializingFieldMap() {
            assert this.fieldMap == null && this.fieldList == null;
            final Map<FieldMeta<?>, Boolean> fieldMap = new HashMap<>();
            final List<FieldMeta<?>> fieldList = new ArrayList<>();

            final TableMeta<?> insertTable = this.insertTable;
            FieldMeta<?> reservedField;
            if (this.migration) {
                if (insertTable instanceof ChildTableMeta) {
                    reservedField = insertTable.id();
                    fieldMap.put(reservedField, Boolean.TRUE); // child id must be managed by army
                    fieldList.add(reservedField);
                }
            } else if (insertTable instanceof ChildTableMeta) {
                reservedField = insertTable.id();
                fieldMap.put(reservedField, Boolean.TRUE); // child id must be managed by army
                fieldList.add(reservedField);

                for (FieldMeta<?> field : insertTable.fieldChain()) {
                    if (fieldMap.putIfAbsent(field, Boolean.TRUE) != null) {
                        //no bug,never here
                        throw new IllegalStateException("fieldChain error");
                    }
                    fieldList.add(field);
                }
            } else {
                for (String fieldName : _MetaBridge.RESERVED_FIELDS) {
                    reservedField = insertTable.tryGetField(fieldName);
                    if (reservedField == null) {
                        continue;
                    }
                    if (reservedField instanceof PrimaryFieldMeta
                            && (!reservedField.insertable() || reservedField.generatorType() == null)) {
                        continue;
                    }
                    fieldMap.putIfAbsent(reservedField, Boolean.TRUE);
                    fieldList.add(reservedField);

                }
                reservedField = insertTable.discriminator();
                if (reservedField != null) {
                    fieldMap.putIfAbsent(reservedField, Boolean.TRUE);
                    fieldList.add(reservedField);
                }

                for (FieldMeta<?> field : insertTable.fieldChain()) {
                    if (field instanceof PrimaryFieldMeta) {
                        continue;
                    }
                    if (fieldMap.putIfAbsent(field, Boolean.TRUE) != null) {
                        //no bug,never here
                        throw new IllegalStateException("fieldChain error");
                    }
                    fieldList.add(field);
                }

            }

            this.fieldList = fieldList;
            return fieldMap;
        }

    }//ColumnsClause


    @SuppressWarnings("unchecked")
    static abstract class ColumnDefaultClause<C, T, RR> extends ColumnsClause<C, T, RR>
            implements Insert._ColumnDefaultClause<C, T, RR>, _Insert._ValuesSyntaxInsert {


        final boolean preferLiteral;

        final NullHandleMode nullHandleMode;

        private Map<FieldMeta<?>, _Expression> commonExpMap;

        ColumnDefaultClause(InsertOptions options, TableMeta<T> table) {
            super(options.getContext(), options.isMigration(), table);
            if (options instanceof ValueSyntaxOptions) {
                this.nullHandleMode = ((ValueSyntaxOptions) options).nullHandle();
            } else {
                this.nullHandleMode = null;
            }
            this.preferLiteral = options.isPreferLiteral();
        }

        @Override
        public final RR defaultValue(final FieldMeta<T> field, final @Nullable Object value) {
            if (this.migration) {
                String m = "migration mode not support default value clause";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            final ArmyExpression valueExp;
            if (value == null) {
                if (this.preferLiteral) {
                    valueExp = (ArmyExpression) SQLs.nullWord();
                } else {
                    valueExp = (ArmyExpression) SQLs.param(field, null);
                }
            } else if (value instanceof DataField) {
                String m = "column default value must be non-field";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (!(value instanceof Expression)) {
                valueExp = (ArmyExpression) SQLs.param(field, value);
            } else if (value instanceof ArmyExpression) {
                valueExp = (ArmyExpression) value;
            } else {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }

            this.validateField(field, valueExp);

            Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
            if (commonExpMap == null) {
                commonExpMap = new HashMap<>();
                this.commonExpMap = commonExpMap;
            }
            if (commonExpMap.putIfAbsent(field, valueExp) != null) {
                String m = String.format("duplication default for %s.", field);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            return (RR) this;
        }


        @Override
        public final RR defaultLiteral(FieldMeta<T> field, @Nullable Object value) {
            return this.defaultValue(field, SQLs._nullableLiteral(field, value));
        }

        @Override
        public final RR defaultExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            final Expression expression;
            expression = function.apply(this.criteria);
            if (expression == null) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            return this.defaultValue(field, expression);
        }

        @Override
        public final RR defaultExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression == null) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            return this.defaultValue(field, expression);
        }

        @Override
        public final RR defaultNull(FieldMeta<T> field) {
            return this.defaultValue(field, SQLs.nullWord());
        }


        @Override
        public final RR ifDefaultValue(FieldMeta<T> field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.defaultValue(field, value);
            }
            return (RR) this;
        }

        @Override
        public final RR ifDefaultValue(FieldMeta<T> field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.defaultValue(field, value);
            }
            return (RR) this;
        }

        @Override
        public final RR ifDefaultValue(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.defaultValue(field, value);
            }
            return (RR) this;
        }

        @Override
        public final RR ifDefaultLiteral(FieldMeta<T> field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.defaultValue(field, SQLs._nonNullLiteral(field, value));
            }
            return (RR) this;
        }

        @Override
        public final RR ifDefaultLiteral(FieldMeta<T> field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.defaultValue(field, SQLs._nonNullLiteral(field, value));
            }
            return (RR) this;
        }

        @Override
        public final RR ifDefaultLiteral(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.defaultValue(field, SQLs._nonNullLiteral(field, value));
            }
            return (RR) this;
        }

        @Override
        public final Map<FieldMeta<?>, _Expression> defaultValueMap() {
            Map<FieldMeta<?>, _Expression> map = this.commonExpMap;
            if (map == null) {
                map = Collections.emptyMap();
                this.commonExpMap = map;
            } else if (map instanceof HashMap) {
                map = Collections.unmodifiableMap(map);
                this.commonExpMap = map;
            }
            return map;
        }


        @Override
        public void clear() {
            super.clear();
            this.commonExpMap = null;
        }

        @Override
        public final boolean isMigration() {
            return this.migration;
        }


        @Override
        public final NullHandleMode nullHandle() {
            return this.nullHandleMode;
        }

        @Override
        public final boolean isPreferLiteral() {
            return this.preferLiteral;
        }

        final void endColumnDefaultClause() {
            final Map<FieldMeta<?>, _Expression> map = this.commonExpMap;
            if (map == null) {
                this.commonExpMap = Collections.emptyMap();
            } else if (map instanceof HashMap) {
                this.commonExpMap = Collections.unmodifiableMap(map);
            }
        }


    }//CommonExpClause


    @SuppressWarnings("unchecked")
    static abstract class DomainValueClause<C, T, CR, VR>
            extends ColumnDefaultClause<C, T, CR> implements Insert._DomainValueClause<C, T, VR>
            , _Insert._DomainInsert {


        private List<?> domainList;

        DomainValueClause(InsertOptions options, TableMeta<T> table) {
            super(options, table);
        }

        @Override
        public final VR value(@Nullable T domain) {
            if (domain == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.domainList = Collections.singletonList(domain);
            this.endColumnDefaultClause();
            return this.valuesEnd();
        }

        @Override
        public final VR value(Function<C, T> function) {
            return this.value(function.apply(this.criteriaContext.criteria()));
        }

        @Override
        public final VR value(Supplier<T> supplier) {
            return this.value(supplier.get());
        }

        @Override
        public final VR value(Function<String, Object> function, String keyName) {
            final Object domain;
            domain = function.apply(keyName);
            if (!this.insertTable.javaType().isInstance(domain)) {
                throw nonDomainInstance(this.criteriaContext, domain, this.insertTable);
            }
            return this.value((T) domain);
        }

        @Override
        public final VR values(final @Nullable List<T> domainList) {
            if (domainList == null || domainList.size() == 0) {
                throw domainListIsEmpty();
            }
            this.domainList = Collections.unmodifiableList(new ArrayList<>(domainList));
            this.endColumnDefaultClause();
            return this.valuesEnd();
        }

        @Override
        public final VR values(Function<C, List<T>> function) {
            return this.values(function.apply(this.criteriaContext.criteria()));
        }

        @Override
        public final VR values(Supplier<List<T>> supplier) {
            return this.values(supplier.get());
        }

        @Override
        public final VR values(Function<String, Object> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (!(value instanceof List)) {
                String m = String.format("%s return isn't %s.", Function.class.getName(), List.class.getName());
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }

            final List<?> domainList = (List<?>) value;
            if (domainList.size() == 0) {
                throw domainListIsEmpty();
            }
            if (!this.insertTable.javaType().isInstance(domainList.get(0))) {
                throw nonDomainInstance(this.criteriaContext, domainList.get(0), this.insertTable);
            }
            this.domainList = _CollectionUtils.asUnmodifiableList(domainList);
            this.endColumnDefaultClause();
            return this.valuesEnd();
        }


        @Override
        public final List<?> domainList() {
            final List<?> list = this.domainList;
            if (list == null || list instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return list;
        }

        @Override
        public void clear() {
            super.clear();
            this.domainList = null;
        }

        abstract VR valuesEnd();

        private CriteriaException domainListIsEmpty() {
            return CriteriaContextStack.criteriaError(this.criteriaContext, "domainList must non-empty");
        }


    }//DomainValueClause


    static abstract class StaticColumnValuePairClause<C, T, VR>
            implements Insert._StaticValueLeftParenClause<C, T, VR>, Insert._StaticColumnValueClause<C, T, VR>
            , CriteriaContextSpec {

        final CriteriaContext criteriaContext;

        final C criteria;

        final BiConsumer<FieldMeta<?>, ArmyExpression> validator;

        private List<Map<FieldMeta<?>, _Expression>> rowList;

        private Map<FieldMeta<?>, _Expression> rowValuesMap;

        StaticColumnValuePairClause(CriteriaContext criteriaContext
                , BiConsumer<FieldMeta<?>, ArmyExpression> validator) {
            this.criteriaContext = criteriaContext;
            this.criteria = criteriaContext.criteria();
            this.validator = validator;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.criteriaContext;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> leftParen(FieldMeta<T> field, @Nullable Object value) {
            this.innerAddValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> leftParenLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.innerAddValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> leftParenExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            final Expression exp;
            exp = supplier.get();
            this.innerAddValuePair(field, exp);
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> leftParenExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            final Expression exp;
            exp = function.apply(this.criteria);
            this.innerAddValuePair(field, exp);
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> comma(FieldMeta<T> field, @Nullable Object value) {
            this.innerAddValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> commaLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.innerAddValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> commaExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            final Expression exp;
            exp = supplier.get();
            this.innerAddValuePair(field, exp);
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> commaExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            final Expression exp;
            exp = function.apply(this.criteria);
            this.innerAddValuePair(field, exp);
            return this;
        }


        final void endCurrentRow() {
            final Map<FieldMeta<?>, _Expression> currentRow = this.rowValuesMap;
            if (!(currentRow instanceof HashMap)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            List<Map<FieldMeta<?>, _Expression>> rowValueList = this.rowList;
            if (rowValueList == null) {
                rowValueList = new ArrayList<>();
                this.rowList = rowValueList;
            } else if (!(rowValueList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            rowValueList.add(Collections.unmodifiableMap(currentRow));
            this.rowValuesMap = null;
        }

        final List<Map<FieldMeta<?>, _Expression>> endValuesClause() {
            if (this.rowValuesMap != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            List<Map<FieldMeta<?>, _Expression>> rowValueList = this.rowList;
            if (rowValueList instanceof ArrayList) {
                rowValueList = _CollectionUtils.unmodifiableList(rowValueList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowList = rowValueList;
            return rowValueList;
        }


        private void innerAddValuePair(final FieldMeta<?> field, final @Nullable Expression value) {
            if (value instanceof DataField) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "column value must be non-field.");
            } else if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            this.validator.accept(field, (ArmyExpression) value);
            Map<FieldMeta<?>, _Expression> currentRow = this.rowValuesMap;
            if (currentRow == null) {
                currentRow = this.newMap();
                this.rowValuesMap = currentRow;
            }
            if (currentRow.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.criteriaContext, field);
            }
        }

        private Map<FieldMeta<?>, _Expression> newMap() {
            final List<Map<FieldMeta<?>, _Expression>> rowList = this.rowList;
            final Map<FieldMeta<?>, _Expression> map;
            if (rowList == null) {
                map = new HashMap<>();
            } else {
                map = new HashMap<>((int) (rowList.get(0).size() / 0.75F));
            }
            return map;
        }


    }//StaticValueColumnClause


    static abstract class DynamicValueInsertValueClause<C, T, RR, VR>
            extends ColumnDefaultClause<C, T, RR> implements Insert._DynamicValuesClause<C, T, VR>
            , PairsConstructor<T>, ValueSyntaxOptions {

        private List<Map<FieldMeta<?>, _Expression>> valuePairList;

        private Map<FieldMeta<?>, _Expression> valuePairMap;

        DynamicValueInsertValueClause(ValueSyntaxOptions options, TableMeta<T> table) {
            super(options, table);
        }

        @Override
        public final VR values(Consumer<PairsConstructor<T>> consumer) {
            return this.multiRowValues(consumer);
        }

        @Override
        public final VR values(BiConsumer<C, PairsConstructor<T>> consumer) {
            return this.multiRowValues(consumer);
        }

        @Override
        public final PairConsumer<T> row() {
            final Map<FieldMeta<?>, _Expression> currentPairMap = this.valuePairMap;
            if (currentPairMap instanceof HashMap) {
                List<Map<FieldMeta<?>, _Expression>> valuePairList = this.valuePairList;
                if (valuePairList == null) {
                    valuePairList = new ArrayList<>();
                    this.valuePairList = valuePairList;
                } else if (!(valuePairList instanceof ArrayList)) {
                    throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
                }
                valuePairList.add(Collections.unmodifiableMap(currentPairMap));
            } else if (currentPairMap != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.valuePairMap = new HashMap<>();
            return this;
        }

        @Override
        public final PairConsumer<T> accept(FieldMeta<T> field, @Nullable Object value) {
            return this.addValuePair(field, SQLs._nullableParam(field, value));
        }

        @Override
        public final PairConsumer<T> acceptLiteral(FieldMeta<T> field, @Nullable Object value) {
            return this.addValuePair(field, SQLs._nullableLiteral(field, value));
        }

        @Override
        public final PairConsumer<T> acceptExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            return this.addValuePair(field, supplier.get());
        }

        @Override
        public void clear() {
            super.clear();
            this.valuePairList = null;
            this.valuePairMap = null;
        }

        /**
         * @param rowList a unmodified list
         */
        abstract VR valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowList);


        private PairConsumer<T> addValuePair(final FieldMeta<?> field, final @Nullable Expression value) {
            final Map<FieldMeta<?>, _Expression> currentPairMap = this.valuePairMap;
            if (currentPairMap == null) {
                String m = String.format("Not found any row,please use %s.row() method create row."
                        , PairsConstructor.class.getName());
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (!(currentPairMap instanceof HashMap)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }

            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            this.validateField(field, (ArmyExpression) value);

            if (currentPairMap.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.criteriaContext, field);
            }
            return this;
        }


        @SuppressWarnings("unchecked")
        private VR multiRowValues(final Object callback) {
            //1. validate
            if (this.valuePairMap != null || this.valuePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            //2. callback
            if (callback instanceof Consumer) {
                ((Consumer<PairsConstructor<T>>) callback).accept(this);
            } else if (callback instanceof BiConsumer) {
                ((BiConsumer<C, PairsConstructor<T>>) callback).accept(this.criteria, this);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }

            Map<FieldMeta<?>, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap == null) {
                String m = "Values insert must have one row at least";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            if (valuePairMap.size() == 0) {
                valuePairMap = Collections.emptyMap();
            } else {
                valuePairMap = Collections.unmodifiableMap(valuePairMap);
            }
            this.valuePairMap = valuePairMap;
            List<Map<FieldMeta<?>, _Expression>> valuePairList = this.valuePairList;
            if (valuePairList == null) {
                valuePairList = Collections.singletonList(valuePairMap);
            } else if (valuePairList instanceof ArrayList) {
                valuePairList.add(valuePairMap);
                valuePairList = Collections.unmodifiableList(valuePairList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.valuePairList = valuePairList;
            return this.valueClauseEnd(valuePairList);
        }


    }//ValueInsertValueClause

    @SuppressWarnings("unchecked")
    static abstract class AssignmentSetClause<C, T, SR>
            implements Insert._AssignmentSetClause<C, T, SR>, ColumnListClause, _Insert._AssignmentStatementSpec
            , PairConsumer<T> {
        final CriteriaContext criteriaContext;
        final C criteria;

        final TableMeta<T> insertTable;

        private Map<FieldMeta<?>, _Expression> fieldPairMap;
        private List<_Pair<FieldMeta<?>, _Expression>> itemPairList;

        AssignmentSetClause(CriteriaContext criteriaContext, TableMeta<T> insertTable) {
            this.criteriaContext = criteriaContext;
            this.criteria = criteriaContext.criteria();
            this.insertTable = insertTable;
        }


        @Override
        public final SR setPair(Consumer<PairConsumer<T>> consumer) {
            consumer.accept(this);
            return (SR) this;
        }

        @Override
        public final SR setPair(BiConsumer<C, PairConsumer<T>> consumer) {
            consumer.accept(this.criteria, this);
            return (SR) this;
        }

        @Override
        public final SR set(FieldMeta<T> field, @Nullable Object value) {
            return this.addFieldPair(field, SQLs._nullableParam(field, value));
        }

        @Override
        public final SR setLiteral(FieldMeta<T> field, @Nullable Object value) {
            return this.addFieldPair(field, SQLs._nullableLiteral(field, value));
        }

        @Override
        public final SR setExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            return this.addFieldPair(field, supplier.get());
        }

        @Override
        public final SR setExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            return this.addFieldPair(field, function.apply(this.criteria));
        }

        @Override
        public final SR ifSet(FieldMeta<T> field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.addFieldPair(field, SQLs._nullableParam(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(FieldMeta<T> field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.addFieldPair(field, SQLs._nullableParam(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.addFieldPair(field, SQLs._nullableParam(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetLiteral(FieldMeta<T> field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.addFieldPair(field, SQLs._nullableLiteral(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetLiteral(FieldMeta<T> field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.addFieldPair(field, SQLs._nullableLiteral(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetLiteral(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.addFieldPair(field, SQLs._nullableLiteral(field, value));
            }
            return (SR) this;
        }


        @Override
        public final PairConsumer<T> accept(FieldMeta<T> field, @Nullable Object value) {
            this.addFieldPair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public final PairConsumer<T> acceptLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.addFieldPair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public final PairConsumer<T> acceptExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            this.addFieldPair(field, supplier.get());
            return this;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.criteriaContext;
        }

        @Override
        public final List<_Pair<FieldMeta<?>, _Expression>> pairList() {
            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.itemPairList;
            if (pairList == null || pairList instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return pairList;
        }

        @Override
        public final Map<FieldMeta<?>, _Expression> pairMap() {
            final Map<FieldMeta<?>, _Expression> fieldMap = this.fieldPairMap;
            if (fieldMap == null || fieldMap instanceof HashMap) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return fieldMap;
        }


        final void endAssignmentSetClause() {
            List<_Pair<FieldMeta<?>, _Expression>> itemPairList = this.itemPairList;
            Map<FieldMeta<?>, _Expression> fieldMap = this.fieldPairMap;

            if (itemPairList == null) {
                itemPairList = Collections.emptyList();

            } else if (!(itemPairList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            } else {
                if (this instanceof AssignmentInsertClause && ((AssignmentInsertClause<C, T, SR>) this).migration) {
                    validateMigrationColumnList(this.criteriaContext, this.insertTable, fieldMap);
                }
                if (itemPairList.size() == 1) {
                    itemPairList = Collections.singletonList(itemPairList.get(0));
                } else {
                    itemPairList = _CollectionUtils.unmodifiableList(itemPairList);
                }
            }
            this.itemPairList = itemPairList;

            if (fieldMap == null) {
                fieldMap = Collections.emptyMap();
            } else {
                fieldMap = Collections.unmodifiableMap(fieldMap);
            }
            this.fieldPairMap = fieldMap;

            assert fieldMap.size() == itemPairList.size();

        }


        private SR addFieldPair(final FieldMeta<?> field, final @Nullable Expression value) {
            if (value instanceof DataField && this instanceof AssignmentInsertClause) {
                String m = "assignment insert value must be non-field.";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            this.validateField(field, (ArmyExpression) value);

            Map<FieldMeta<?>, _Expression> fieldPairMap = this.fieldPairMap;
            if (fieldPairMap == null) {
                fieldPairMap = new HashMap<>();
                this.fieldPairMap = fieldPairMap;
            }
            if (fieldPairMap.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.criteriaContext, field);
            }
            List<_Pair<FieldMeta<?>, _Expression>> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                itemPairList = new ArrayList<>();
                this.itemPairList = itemPairList;
            }
            itemPairList.add(_Pair.create(field, (ArmyExpression) value));
            return (SR) this;
        }


    }//AssignmentSetClause


    static abstract class AssignmentInsertClause<C, T, SR>
            extends AssignmentSetClause<C, T, SR>
            implements Insert._AssignmentSetClause<C, T, SR>, _Insert._AssignmentInsert {

        final boolean migration;

        final boolean preferLiteral;

        AssignmentInsertClause(InsertOptions options, TableMeta<T> table) {
            super(options.getContext(), table);

            this.migration = options.isMigration();
            this.preferLiteral = options.isPreferLiteral();
        }


        @Override
        public final void validateField(final FieldMeta<?> field, final @Nullable ArmyExpression value) {
            InsertSupport.checkField(this.criteriaContext, this.insertTable, true, field);
            if (value != null && !field.nullable() && value.isNullValue()) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonNullField, field);
            }

        }

        @Override
        public final TableMeta<?> table() {
            return this.insertTable;
        }

        @Override
        public final boolean isMigration() {
            return this.migration;
        }

        @Override
        public final boolean isPreferLiteral() {
            return this.preferLiteral;
        }


        @Override
        public void clear() {
            //no-op
        }


    }//AssignmentInsertClause


    static abstract class QueryInsertSpaceClause<C, T, RR, SR> extends ColumnsClause<C, T, RR>
            implements Insert._SpaceSubQueryClause<C, SR>, _Insert._QueryInsert {

        private SubQuery subQuery;

        QueryInsertSpaceClause(CriteriaContext criteriaContext, TableMeta<T> table) {
            super(criteriaContext, true, table);
        }

        @Override
        public final SR space(Supplier<? extends SubQuery> supplier) {
            return this.innerSpace(supplier.get());
        }

        @Override
        public final SR space(Function<C, ? extends SubQuery> function) {
            return this.innerSpace(function.apply(this.criteria));
        }

        @Override
        public final SubQuery subQuery() {
            final SubQuery query = this.subQuery;
            if (query == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return query;
        }

        abstract SR spaceEnd();

        private SR innerSpace(final @Nullable SubQuery query) {
            if (query == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            if (this.subQuery != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.subQuery = query;
            return this.spaceEnd();
        }

    }//QueryInsertSpaceClause


    static abstract class InsertStatement<I extends DmlStatement.DmlInsert>
            implements _Insert, Statement.StatementMockSpec, DmlStatement._DmlInsertSpec<I>
            , DmlStatement.DmlInsert {


        final CriteriaContext criteriaContext;

        final TableMeta<?> table;

        private Boolean prepared;

        InsertStatement(_Insert clause) {
            this.criteriaContext = ((CriteriaContextSpec) clause).getContext();
            this.table = clause.table();
        }


        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            final DialectParser parser;
            parser = _MockDialects.from(dialect);
            return parser.printStmt(this.mockStmt(parser, visible), none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            return this.mockStmt(_MockDialects.from(dialect), visible);
        }

        @Override
        public final TableMeta<?> table() {
            return this.table;
        }


        @SuppressWarnings("unchecked")
        @Override
        public final I asInsert() {
            _Assert.nonPrepared(this.prepared);

            if (this instanceof QueryInsertStatement) {
                ((QueryInsertStatement<I>) this).validateQueryInsertStatement();
            } else if (this instanceof _Insert._ChildInsert) {
                final _Insert parentStmt = ((_ChildInsert) this).parentStmt();
                if (parentStmt instanceof _Insert._DuplicateKeyClause
                        && parentStmt.table().id().generatorType() == GeneratorType.POST
                        && !(parentStmt instanceof _Insert._SupportReturningClause)) {
                    throw CriteriaContextStack.criteriaError(this.criteriaContext
                            , _Exceptions::duplicateKeyAndPostIdInsert, (ChildTableMeta<?>) this.table);
                }
            }

            if (this instanceof SubStatement) {
                CriteriaContextStack.pop(this.criteriaContext);
            } else {
                CriteriaContextStack.clearContextStack(this.criteriaContext);
            }

            this.prepared = Boolean.TRUE;
            return (I) this;
        }

        @Override
        public final void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public final boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public final void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
        }


        private Stmt mockStmt(DialectParser parser, Visible visible) {
            final Stmt stmt;
            if (this instanceof Insert) {
                stmt = parser.insert((Insert) this, visible);
            } else if (this instanceof ReplaceInsert || this instanceof MergeInsert) {
                stmt = parser.dialectStmt((DialectStatement) this, visible);
            } else {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return stmt;
        }


    }//InsertStatement


    static abstract class ValueSyntaxStatement<I extends DmlStatement.DmlInsert>
            extends InsertStatement<I> implements _Insert._ValuesSyntaxInsert {

        private final boolean migration;
        private final NullHandleMode nullHandleMode;

        private final boolean preferLiteral;

        private final List<FieldMeta<?>> fieldList;

        private final Map<FieldMeta<?>, Boolean> fieldMap;

        private final Map<FieldMeta<?>, _Expression> defaultExpMap;

        ValueSyntaxStatement(_ValuesSyntaxInsert clause) {
            super(clause);
            this.migration = clause.isMigration();
            this.nullHandleMode = clause.nullHandle();
            this.preferLiteral = clause.isPreferLiteral();
            this.fieldList = clause.fieldList();

            this.fieldMap = clause.fieldMap();
            this.defaultExpMap = clause.defaultValueMap();
        }


        @Override
        public final boolean isMigration() {
            return this.migration;
        }

        @Override
        public final NullHandleMode nullHandle() {
            return this.nullHandleMode;
        }

        @Override
        public final boolean isPreferLiteral() {
            return this.preferLiteral;
        }

        @Override
        public final List<FieldMeta<?>> fieldList() {
            return this.fieldList;
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            return this.fieldMap;
        }

        @Override
        public final Map<FieldMeta<?>, _Expression> defaultValueMap() {
            return this.defaultExpMap;
        }


    }//ValueInsertStatement

    static abstract class AssignmentInsertStatement<I extends DmlStatement.DmlInsert>
            extends InsertStatement<I>
            implements _Insert._AssignmentInsert {

        private final boolean migration;

        private final boolean preferLiteral;

        private final List<_Pair<FieldMeta<?>, _Expression>> rowPairList;

        private final Map<FieldMeta<?>, _Expression> fieldMap;

        AssignmentInsertStatement(_AssignmentInsert clause) {
            super(clause);
            this.migration = clause.isMigration();
            this.preferLiteral = clause.isPreferLiteral();
            this.rowPairList = clause.pairList();

            this.fieldMap = clause.pairMap();
        }

        @Override
        public final boolean isMigration() {
            return this.migration;
        }

        @Override
        public final boolean isPreferLiteral() {
            return this.preferLiteral;
        }

        @Override
        public final Map<FieldMeta<?>, _Expression> pairMap() {
            return this.fieldMap;
        }

        @Override
        public final List<_Pair<FieldMeta<?>, _Expression>> pairList() {
            return this.rowPairList;
        }


    }//AssignmentInsertStatement


    static abstract class QueryInsertStatement<I extends DmlStatement.DmlInsert>
            extends InsertStatement<I>
            implements _Insert._QueryInsert {


        private final List<FieldMeta<?>> fieldList;

        private final Map<FieldMeta<?>, Boolean> fieldMap;

        private final SubQuery subQuery;

        QueryInsertStatement(_QueryInsert clause) {
            super(clause);
            this.fieldList = clause.fieldList();
            this.fieldMap = clause.fieldMap();
            this.subQuery = clause.subQuery();
        }


        @Override
        public final List<FieldMeta<?>> fieldList() {
            return this.fieldList;
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            return this.fieldMap;
        }

        @Override
        public final SubQuery subQuery() {
            return this.subQuery;
        }

        /**
         * <p>
         * This method is invoked after {@link  CriteriaContextStack#clearContextStack(CriteriaContext)}
         * and {@link  CriteriaContextStack#pop(CriteriaContext)}.
         * </p>
         */
        private void validateQueryInsertStatement() {
            if (this instanceof _Insert._ChildQueryInsert) {
                final _Insert._QueryInsert parentStmt = ((_ChildQueryInsert) this).parentStmt();
                this.doValidateStatement(parentStmt.table(), parentStmt.fieldList(), parentStmt.subQuery());
            }
            this.doValidateStatement(this.table, this.fieldList, this.subQuery);
        }


        private void doValidateStatement(final TableMeta<?> insertTable, final List<FieldMeta<?>> fieldList
                , final @Nullable SubQuery query) {
            final int fieldSize;
            fieldSize = fieldList.size();
            if (fieldSize == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext
                        , _Exceptions::noFieldsForQueryInsert, insertTable);
            }
            if (query == null) {
                String m = String.format("SubQuery must be non-null for query insert of %s", insertTable);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            final List<Selection> selectionList;
            selectionList = _DialectUtils.flatSelectItem(query.selectItemList());

            if (selectionList.size() != fieldSize) {
                Supplier<CriteriaException> supplier;
                supplier = () -> _Exceptions.rowSetSelectionAndFieldSizeNotMatch(selectionList.size(), fieldSize, insertTable);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, supplier);
            }

        }


    }//QueryInsertStatement


    static void checkField(final CriteriaContext context, final TableMeta<?> table
            , final boolean migration, final FieldMeta<?> field) {

        if (field.tableMeta() != table) {
            //don't contain parent field
            throw CriteriaContextStack.criteriaError(context, _Exceptions::unknownColumn, field);
        } else if (migration) {
            if (field instanceof PrimaryFieldMeta && field.tableMeta() instanceof ChildTableMeta) {
                throw childIdIsManaged(context, (ChildTableMeta<?>) field.tableMeta());
            }
        } else if (!field.insertable()) {
            throw CriteriaContextStack.criteriaError(context, _Exceptions::nonInsertableField, field);
        } else if (isArmyManageField(table, field)) {
            throw CriteriaContextStack.criteriaError(context, _Exceptions::armyManageField, field);
        } else if (field instanceof PrimaryFieldMeta && field.tableMeta() instanceof ChildTableMeta) {
            throw childIdIsManaged(context, (ChildTableMeta<?>) field.tableMeta());
        }

    }


    static CriteriaException notContainField(CriteriaContext criteriaContext, FieldMeta<?> field) {
        String m = String.format("insert field list don't contain %s", field);
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException duplicationValuePair(CriteriaContext criteriaContext, FieldMeta<?> field) {
        String m = String.format("duplication value of %s at same row.", field);
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }


    private static CriteriaException nonDomainInstance(CriteriaContext criteriaContext, @Nullable Object domain
            , TableMeta<?> table) {
        String m;
        m = String.format("%s isn't %s instance.", _ClassUtils.safeClassName(domain), table.javaType().getName());
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }

    private static CriteriaException childIdIsManaged(CriteriaContext criteriaContext, ChildTableMeta<?> table) {
        return CriteriaContextStack.criteriaError(criteriaContext, _Exceptions::childIdIsManagedByArmy, table);
    }

    static CriteriaException childAndParentRowsNotMatch(CriteriaContext criteriaContext
            , ChildTableMeta<?> table, int parent, int child) {
        Supplier<CriteriaException> supplier;
        supplier = () -> _Exceptions.childAndParentRowsNotMatch(table, parent, child);
        return CriteriaContextStack.criteriaError(criteriaContext, supplier);
    }


    private static boolean isArmyManageField(final TableMeta<?> insertTable, final FieldMeta<?> field) {
        final boolean match;
        switch (field.fieldName()) {
            case _MetaBridge.CREATE_TIME:
            case _MetaBridge.UPDATE_TIME:
            case _MetaBridge.VERSION:
                // here,don't contain  id or visible field
                match = true;
                break;
            default:
                match = field == insertTable.discriminator() || field.generatorType() != null;

        }
        return match;
    }

    private static void validateMigrationColumnList(final CriteriaContext context, final TableMeta<?> insertTable
            , final Map<FieldMeta<?>, ?> fieldMap) {
        if (insertTable instanceof SingleTableMeta) {
            FieldMeta<?> field;
            for (String fieldName : _MetaBridge.RESERVED_FIELDS) {
                field = insertTable.tryGetField(fieldName);
                if (field != null && fieldMap.get(field) == null) {
                    throw CriteriaContextStack.criteriaError(context, _Exceptions::migrationManageGeneratorField, field);
                }
            }
            field = insertTable.discriminator();
            if (field != null && fieldMap.get(field) == null) {
                throw CriteriaContextStack.criteriaError(context, _Exceptions::migrationManageGeneratorField, field);
            }
        }

        for (FieldMeta<?> field : insertTable.fieldChain()) {
            if (!field.nullable() && fieldMap.get(field) == null) {
                throw CriteriaContextStack.criteriaError(context, _Exceptions::migrationModeGeneratorField, field);
            }
        }

    }


}
