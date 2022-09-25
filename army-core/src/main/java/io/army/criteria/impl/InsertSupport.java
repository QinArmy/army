package io.army.criteria.impl;

import io.army.annotation.GeneratorType;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._Predicate;
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
import java.util.function.*;

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

    interface WithValueSyntaxOptions extends ValueSyntaxOptions {

        boolean isRecursive();

        List<Cte> cteList();

    }


    enum InsertMode {
        DOMAIN,
        VALUES,
        QUERY,
        ASSIGNMENT
    }


    interface ColumnListClause extends CriteriaContextSpec {

        /**
         * @param value if non-null and not {@link  FieldMeta#nullable()},then validate value isn't non-null expression
         */
        void validateField(FieldMeta<?> field, @Nullable ArmyExpression value);


    }

    static abstract class InsertOptionsImpl<MR, PR> implements InsertOptions, Insert._MigrationOptionClause<MR>
            , Insert._PreferLiteralClause<PR> {

        final CriteriaContext context;

        private boolean migration;

        private boolean preferLiteral;

        InsertOptionsImpl(CriteriaContext criteriaContext) {
            this.context = criteriaContext;
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
            return this.context;
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


    @SuppressWarnings("unchecked")
    static abstract class NonQueryWithCteOption<C, MR, NR, PR, SS extends SubStatement, WE>
            extends NonQueryInsertOptionsImpl<MR, NR, PR>
            implements DialectStatement._WithCteClause<C, SS, WE>
            , WithValueSyntaxOptions {

        private boolean recursive;

        private List<Cte> cteList;

        NonQueryWithCteOption(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }


        @Override
        public final WE with(String cteName, Supplier<? extends SS> supplier) {
            CriteriaUtils.withClause(false, SQLs.cte(cteName, supplier.get()), this.context, this::doWithCte);
            return (WE) this;
        }

        @Override
        public final WE with(String cteName, Function<C, ? extends SS> function) {
            CriteriaUtils.withClause(false, SQLs.cte(cteName, function.apply(this.context.criteria()))
                    , this.context, this::doWithCte);
            return (WE) this;
        }

        @Override
        public final WE with(Consumer<Consumer<Cte>> consumer) {
            CriteriaUtils.withClause(false, consumer, this.context, this::doWithCte);
            return (WE) this;
        }

        @Override
        public final WE with(BiConsumer<C, Consumer<Cte>> consumer) {
            CriteriaUtils.withClause(false, consumer, this.context, this::doWithCte);
            return (WE) this;
        }

        @Override
        public final WE ifWith(Consumer<Consumer<Cte>> consumer) {
            CriteriaUtils.ifWithClause(false, consumer, this.context, this::doWithCte);
            return (WE) this;
        }

        @Override
        public final WE ifWith(BiConsumer<C, Consumer<Cte>> consumer) {
            CriteriaUtils.ifWithClause(false, consumer, this.context, this::doWithCte);
            return (WE) this;
        }

        @Override
        public final WE withRecursive(String cteName, Supplier<? extends SS> supplier) {
            CriteriaUtils.withClause(true, SQLs.cte(cteName, supplier.get()), this.context, this::doWithCte);
            return (WE) this;
        }

        @Override
        public final WE withRecursive(String cteName, Function<C, ? extends SS> function) {
            CriteriaUtils.withClause(true, SQLs.cte(cteName, function.apply(this.context.criteria()))
                    , this.context, this::doWithCte);
            return (WE) this;
        }

        @Override
        public final WE withRecursive(Consumer<Consumer<Cte>> consumer) {
            CriteriaUtils.withClause(true, consumer, this.context, this::doWithCte);
            return (WE) this;
        }

        @Override
        public final WE withRecursive(BiConsumer<C, Consumer<Cte>> consumer) {
            CriteriaUtils.withClause(true, consumer, this.context, this::doWithCte);
            return (WE) this;
        }

        @Override
        public final WE ifWithRecursive(Consumer<Consumer<Cte>> consumer) {
            CriteriaUtils.ifWithClause(true, consumer, this.context, this::doWithCte);
            return (WE) this;
        }

        @Override
        public final WE ifWithRecursive(BiConsumer<C, Consumer<Cte>> consumer) {
            CriteriaUtils.ifWithClause(true, consumer, this.context, this::doWithCte);
            return (WE) this;
        }


        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<Cte> cteList() {
            List<Cte> cteList = this.cteList;
            if (cteList == null) {
                cteList = Collections.emptyList();
            }
            return cteList;
        }

        /**
         * @param cteList unmodified list
         */
        final void doWithCte(boolean recursive, List<Cte> cteList) {
            this.recursive = recursive;
            this.cteList = cteList;
        }


    }//NonQueryWithCteOption


    static abstract class ColumnsClause<C, T, RR>
            implements Insert._ColumnListClause<C, T, RR>, Insert._StaticColumnDualClause<T, RR>
            , _Insert._ColumnListInsert, ColumnListClause {

        final CriteriaContext context;

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
            this.context = criteriaContext;
            this.criteria = criteriaContext.criteria();
            this.migration = migration;
            this.insertTable = table;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
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
                throw CriteriaContextStack.castCriteriaApi(this.context);
            } else {
                if (this.migration) {
                    validateMigrationColumnList(this.context, this.insertTable, fieldMap);
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
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return (RR) this;
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
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return fieldList;
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            Map<FieldMeta<?>, Boolean> map = this.fieldMap;
            if (map == null) {
                map = Collections.emptyMap();
            } else if (map instanceof HashMap) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return map;
        }

        @Override
        public void clear() {
            this.fieldList = null;
            this.fieldMap = null;
        }

        RR columnListEnd() {
            throw new UnsupportedOperationException();
        }


        @Override
        public final void validateField(final FieldMeta<?> field, final @Nullable ArmyExpression value) {
            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                checkField(this.context, this.insertTable, this.migration, field);
            } else if (!fieldMap.containsKey(field)) {
                throw notContainField(this.context, field);
            }
            if (value != null && !field.nullable() && value.isNullValue()) {
                throw CriteriaContextStack.criteriaError(this.context, _Exceptions::nonNullField, field);
            }

        }

        private void addField(final FieldMeta<T> field) {
            checkField(this.context, this.insertTable, this.migration, field);
            if (!this.migration && _MetaBridge.VISIBLE.equals(field.fieldName())) {
                String m = String.format("%s is managed by army for column list clause,in non-migration mode."
                        , _MetaBridge.VISIBLE);
                throw CriteriaContextStack.criteriaError(this.context, m);
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
                throw CriteriaContextStack.criteriaError(this.context, m);
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
    static abstract class ColumnDefaultClause<C, T, LR, DR> extends ColumnsClause<C, T, LR>
            implements Insert._ColumnDefaultClause<C, T, DR>, _Insert._ValuesSyntaxInsert {


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
        public final DR defaultValue(final FieldMeta<T> field, final @Nullable Object value) {
            if (this.migration) {
                String m = "migration mode not support default value clause";
                throw CriteriaContextStack.criteriaError(this.context, m);
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
                throw CriteriaContextStack.criteriaError(this.context, m);
            } else if (!(value instanceof Expression)) {
                valueExp = (ArmyExpression) SQLs.param(field, value);
            } else if (value instanceof ArmyExpression) {
                valueExp = (ArmyExpression) value;
            } else {
                throw CriteriaContextStack.nonArmyExp(this.context);
            }

            this.validateField(field, valueExp);

            Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
            if (commonExpMap == null) {
                commonExpMap = new HashMap<>();
                this.commonExpMap = commonExpMap;
            }
            if (commonExpMap.putIfAbsent(field, valueExp) != null) {
                String m = String.format("duplication default for %s.", field);
                throw CriteriaContextStack.criteriaError(this.context, m);
            }
            return (DR) this;
        }


        @Override
        public final DR defaultLiteral(FieldMeta<T> field, @Nullable Object value) {
            return this.defaultValue(field, SQLs._nullableLiteral(field, value));
        }

        @Override
        public final DR defaultExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            final Expression expression;
            expression = function.apply(this.criteria);
            if (expression == null) {
                throw CriteriaContextStack.nonArmyExp(this.context);
            }
            return this.defaultValue(field, expression);
        }

        @Override
        public final DR defaultExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression == null) {
                throw CriteriaContextStack.nonArmyExp(this.context);
            }
            return this.defaultValue(field, expression);
        }

        @Override
        public final DR defaultNull(FieldMeta<T> field) {
            return this.defaultValue(field, SQLs.nullWord());
        }


        @Override
        public final DR ifDefaultValue(FieldMeta<T> field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.defaultValue(field, value);
            }
            return (DR) this;
        }

        @Override
        public final DR ifDefaultValue(FieldMeta<T> field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.defaultValue(field, value);
            }
            return (DR) this;
        }

        @Override
        public final DR ifDefaultValue(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.defaultValue(field, value);
            }
            return (DR) this;
        }

        @Override
        public final DR ifDefaultLiteral(FieldMeta<T> field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.defaultValue(field, SQLs._nonNullLiteral(field, value));
            }
            return (DR) this;
        }

        @Override
        public final DR ifDefaultLiteral(FieldMeta<T> field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.defaultValue(field, SQLs._nonNullLiteral(field, value));
            }
            return (DR) this;
        }

        @Override
        public final DR ifDefaultLiteral(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.defaultValue(field, SQLs._nonNullLiteral(field, value));
            }
            return (DR) this;
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


    static abstract class DomainValueClause<C, P, CR, DR, VR>
            extends ColumnDefaultClause<C, P, CR, DR> implements Insert._DomainValueClause<C, P, VR>
            , _Insert._DomainInsert {


        private List<?> domainList;

        DomainValueClause(InsertOptions options, TableMeta<P> table) {
            super(options, table);
        }

        @Override
        public final <T extends P> VR value(@Nullable T domain) {
            if (domain == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.domainList = Collections.singletonList(domain);
            this.endColumnDefaultClause();
            return this.valuesEnd();
        }

        @Override
        public final <T extends P> VR value(Function<C, T> function) {
            return this.value(function.apply(this.context.criteria()));
        }

        @Override
        public final <T extends P> VR value(Supplier<T> supplier) {
            return this.value(supplier.get());
        }


        @Override
        public final <T extends P> VR values(final @Nullable List<T> domainList) {
            if (domainList == null || domainList.size() == 0) {
                throw domainListIsEmpty();
            }
            this.domainList = domainList;
            this.endColumnDefaultClause();
            return this.valuesEnd();
        }

        @Override
        public final <T extends P> VR values(Function<C, List<T>> function) {
            return this.values(function.apply(this.context.criteria()));
        }

        @Override
        public final <T extends P> VR values(Supplier<List<T>> supplier) {
            return this.values(supplier.get());
        }


        @Override
        public final List<?> domainList() {
            final List<?> list = this.domainList;
            if (list == null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
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
            return CriteriaContextStack.criteriaError(this.context, "domainList must non-empty");
        }


    }//DomainValueClause

    static abstract class DomainValueShortClause<C, P, DR, VR> extends DomainValueClause<C, P, DR, DR, VR> {

        DomainValueShortClause(InsertOptions options, TableMeta<P> table) {
            super(options, table);
        }
    }//DomainValueShortClause


    @SuppressWarnings("unchecked")
    static abstract class ComplexInsertValuesClause<C, T, CR, DR, VR> extends DomainValueClause<C, T, CR, DR, VR>
            implements Insert._DynamicValuesClause<C, T, VR>
            , Insert._SpaceSubQueryClause<C, VR> {

        private InsertMode insertMode;

        private List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private SubQuery subQuery;

        ComplexInsertValuesClause(InsertOptions options, TableMeta<T> table) {
            super(options, table);
        }

        @Override
        public final VR values(Consumer<PairsConstructor<T>> consumer) {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();

            final DynamicPairsConstructor<T> constructor;
            constructor = new DynamicPairsConstructor<>(this.context, this::validateField);
            consumer.accept(constructor);
            this.rowPairList = constructor.endPairConsumer();
            this.insertMode = InsertMode.VALUES;
            return (VR) this;
        }

        @Override
        public final VR values(BiConsumer<C, PairsConstructor<T>> consumer) {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();

            final DynamicPairsConstructor<T> constructor;
            constructor = new DynamicPairsConstructor<>(this.context, this::validateField);
            consumer.accept(this.criteria, constructor);
            this.rowPairList = constructor.endPairConsumer();
            this.insertMode = InsertMode.VALUES;
            return (VR) this;
        }

        @Override
        public final VR space(Supplier<? extends SubQuery> supplier) {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();

            final SubQuery subQuery;
            subQuery = supplier.get();
            if (subQuery == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.subQuery = subQuery;
            this.insertMode = InsertMode.QUERY;
            return (VR) this;
        }

        @Override
        public final VR space(Function<C, ? extends SubQuery> function) {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();

            final SubQuery subQuery;
            subQuery = function.apply(this.criteria);
            if (subQuery == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.subQuery = subQuery;
            this.insertMode = InsertMode.QUERY;
            return (VR) this;
        }


        @Override
        final VR valuesEnd() {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.insertMode = InsertMode.DOMAIN;
            return (VR) this;
        }

        /**
         * @param rowPairList a unmodified list,empty is allowed.
         */
        final void staticValuesClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowPairList) {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();

            this.rowPairList = rowPairList;
            this.insertMode = InsertMode.VALUES;
        }

        final void staticSpaceSubQueryClauseEnd(final SubQuery subQuery) {
            if (this.insertMode != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();

            this.subQuery = subQuery;
            this.insertMode = InsertMode.QUERY;
        }


        final InsertMode getInsertMode() {
            final InsertMode mode = this.insertMode;
            if (mode == null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return mode;
        }


    }//ComplexInsertValuesClause


    static abstract class StaticColumnValuePairClause<C, T, RR>
            implements Insert._StaticValueLeftParenClause<C, T, RR>, Insert._StaticColumnValueClause<C, T, RR>
            , CriteriaContextSpec {

        final CriteriaContext context;

        final C criteria;

        final BiConsumer<FieldMeta<?>, ArmyExpression> validator;

        private List<Map<FieldMeta<?>, _Expression>> rowList;

        private Map<FieldMeta<?>, _Expression> rowValuesMap;

        StaticColumnValuePairClause(CriteriaContext criteriaContext
                , BiConsumer<FieldMeta<?>, ArmyExpression> validator) {
            this.context = criteriaContext;
            this.criteria = criteriaContext.criteria();
            this.validator = validator;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }


        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, Expression value) {
            return this.comma(field, value);
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, Supplier<?> supplier) {
            return this.comma(field, SQLs._nullableParam(field, supplier.get()));
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, Function<C, ?> function) {
            return this.comma(field, SQLs._nullableParam(field, function.apply(this.criteria)));
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            return this.comma(field, SQLs._nullableParam(field, function.apply(keyName)));
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, @Nullable Object value) {
            return this.comma(field, operator.apply(field, SQLs._safeParam(value)));
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Supplier<?> supplier) {
            return comma(field, operator.apply(field, supplier.get()));
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
            return this.comma(field, operator.apply(field, function.apply(keyName)));
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> comma(final FieldMeta<T> field, final @Nullable Expression value) {
            if (value instanceof DataField) {
                throw CriteriaContextStack.criteriaError(this.context, "column value must be non-field.");
            } else if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.context);
            }
            this.validator.accept(field, (ArmyExpression) value);
            Map<FieldMeta<?>, _Expression> currentRow = this.rowValuesMap;
            if (currentRow == null) {
                currentRow = this.newMap();
                this.rowValuesMap = currentRow;
            }
            if (currentRow.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.context, field);
            }
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, Supplier<?> supplier) {
            return this.comma(field, SQLs._nullableParam(field, supplier.get()));
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, Function<C, ?> function) {
            return this.comma(field, SQLs._nullableParam(field, function.apply(this.criteria)));
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            return this.comma(field, SQLs._nullableParam(field, function.apply(keyName)));
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, @Nullable Object value) {
            return this.comma(field, operator.apply(field, SQLs._safeParam(value)));
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Supplier<?> supplier) {
            return comma(field, operator.apply(field, supplier.get()));
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
            return this.comma(field, operator.apply(field, function.apply(keyName)));
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR rightParen() {
            List<Map<FieldMeta<?>, _Expression>> rowValueList = this.rowList;
            if (rowValueList == null) {
                rowValueList = new ArrayList<>();
                this.rowList = rowValueList;
            } else if (!(rowValueList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final Map<FieldMeta<?>, _Expression> currentRow = this.rowValuesMap;
            if (currentRow == null) {
                rowValueList.add(Collections.emptyMap());
            } else {
                rowValueList.add(Collections.unmodifiableMap(currentRow));
            }

            this.rowValuesMap = null;// clear for next row
            return (RR) this;
        }


        final List<Map<FieldMeta<?>, _Expression>> endValuesClause() {
            if (this.rowValuesMap != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            List<Map<FieldMeta<?>, _Expression>> rowValueList = this.rowList;
            if (!(rowValueList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            rowValueList = _CollectionUtils.unmodifiableList(rowValueList);
            this.rowList = rowValueList;
            return rowValueList;
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

    static abstract class DynamicValueInsertValueClauseShort<C, T, RR, VR>
            extends ColumnDefaultClause<C, T, RR, RR>
            implements Insert._DynamicValuesClause<C, T, VR>
            , PairsConstructor<T>, ValueSyntaxOptions {

        public DynamicValueInsertValueClauseShort(ValueSyntaxOptions options, TableMeta<T> table) {
            super(options, table);
        }

        @Override
        public VR values(Consumer<PairsConstructor<T>> consumer) {
            return null;
        }

        @Override
        public VR values(BiConsumer<C, PairsConstructor<T>> consumer) {
            return null;
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, Expression value) {
            return null;
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, Supplier<?> supplier) {
            return null;
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Object value) {
            return null;
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Supplier<?> supplier) {
            return null;
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public PairConsumer<T> row() {
            return null;
        }

        abstract VR valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowList);
    }


    static final class DynamicPairsConstructor<T> implements PairsConstructor<T> {
        final CriteriaContext context;

        private final BiConsumer<FieldMeta<T>, ArmyExpression> validator;

        private List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private Map<FieldMeta<?>, _Expression> rowMap;

        DynamicPairsConstructor(CriteriaContext context, BiConsumer<FieldMeta<T>, ArmyExpression> validator) {
            this.context = context;
            this.validator = validator;
        }


        @Override
        public PairConsumer<T> row() {
            final Map<FieldMeta<?>, _Expression> currentPairMap = this.rowMap;
            if (currentPairMap instanceof HashMap) {
                List<Map<FieldMeta<?>, _Expression>> valuePairList = this.rowPairList;
                if (valuePairList == null) {
                    valuePairList = new ArrayList<>();
                    this.rowPairList = valuePairList;
                } else if (!(valuePairList instanceof ArrayList)) {
                    throw CriteriaContextStack.castCriteriaApi(this.context);
                }
                valuePairList.add(Collections.unmodifiableMap(currentPairMap));
            } else if (currentPairMap != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.rowMap = new HashMap<>();
            return this;
        }

        @Override
        public PairConsumer<T> accept(final FieldMeta<T> field, final @Nullable Expression value) {
            final Map<FieldMeta<?>, _Expression> currentRowMap = this.rowMap;
            if (currentRowMap == null) {
                String m = String.format("Not found any row,please use %s.row() method create row."
                        , PairsConstructor.class.getName());
                throw CriteriaContextStack.criteriaError(this.context, m);
            } else if (!(currentRowMap instanceof HashMap)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.context);
            }
            this.validator.accept(field, (ArmyExpression) value);

            if (currentRowMap.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.context, field);
            }
            return this;
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, Supplier<?> supplier) {
            return this.accept(field, SQLs._nullableParam(field, supplier.get()));
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            return this.accept(field, SQLs._nullableParam(field, function.apply(keyName)));
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, @Nullable Object value) {
            return this.accept(field, operator.apply(field, SQLs._safeParam(value)));
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Supplier<?> supplier) {
            return this.accept(field, operator.apply(field, supplier.get()));
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
            return this.accept(field, operator.apply(field, function.apply(keyName)));
        }


        List<Map<FieldMeta<?>, _Expression>> endPairConsumer() {
            Map<FieldMeta<?>, _Expression> valuePairMap = this.rowMap;
            if (valuePairMap == null) {
                String m = "Values insert must have one row at least";
                throw CriteriaContextStack.criteriaError(this.context, m);
            }
            if (valuePairMap.size() == 0) {
                valuePairMap = Collections.emptyMap();
            } else {
                valuePairMap = Collections.unmodifiableMap(valuePairMap);
            }
            List<Map<FieldMeta<?>, _Expression>> valuePairList = this.rowPairList;
            if (valuePairList == null) {
                valuePairList = Collections.singletonList(valuePairMap);
            } else if (valuePairList instanceof ArrayList) {
                valuePairList.add(valuePairMap);
                valuePairList = Collections.unmodifiableList(valuePairList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.rowMap = null;
            this.rowPairList = null;
            return valuePairList;
        }


    }//DynamicPairsConstructor

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
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return query;
        }

        abstract SR spaceEnd();

        private SR innerSpace(final @Nullable SubQuery query) {
            if (query == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            if (this.subQuery != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.subQuery = query;
            return this.spaceEnd();
        }

    }//QueryInsertSpaceClause


    @SuppressWarnings("unchecked")
    abstract class ConflictUpdateSetClause<C, T, SR> implements Update._SetClause<C, FieldMeta<T>, SR> {

        final CriteriaContext context;

        final C criteria;

        final TableMeta<T> table;

        private List<ItemPair> itemPairList;

        ConflictUpdateSetClause(CriteriaContext context, TableMeta<T> table) {
            this.context = context;
            this.criteria = context.criteria();
            this.table = table;
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


    }//ConflictUpdateSetClause

    @SuppressWarnings("unchecked")
    static class MinWhereClause<C, WR, WA> implements Statement._MinQueryWhereClause<C, WR, WA>
            , Statement._MinWhereAndClause<C, WA>
            , CriteriaContextSpec {

        final CriteriaContext context;

        final C criteria;

        private List<_Predicate> predicateList;

        MinWhereClause(CriteriaContext context) {
            this.context = context;
            this.criteria = context.criteria();
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final WR where(Consumer<Consumer<IPredicate>> consumer) {
            consumer.accept(this::and);
            if (this.predicateList == null) {
                throw CriteriaContextStack.criteriaError(this.context, _Exceptions::predicateListIsEmpty);
            }
            return (WR) this;
        }

        @Override
        public final WR where(BiConsumer<C, Consumer<IPredicate>> consumer) {
            consumer.accept(this.criteria, this::and);
            if (this.predicateList == null) {
                throw CriteriaContextStack.criteriaError(this.context, _Exceptions::predicateListIsEmpty);
            }
            return (WR) this;
        }

        @Override
        public final WA where(IPredicate predicate) {
            return this.and(predicate);
        }

        @Override
        public final WA where(Supplier<IPredicate> supplier) {
            return this.and(supplier.get());
        }

        @Override
        public final WR ifWhere(Consumer<Consumer<IPredicate>> consumer) {
            consumer.accept(this::and);
            return (WR) this;
        }

        @Override
        public final WR ifWhere(BiConsumer<C, Consumer<IPredicate>> consumer) {
            consumer.accept(this.criteria, this::and);
            return (WR) this;
        }


        @Override
        public final WA where(Function<C, IPredicate> function) {
            return this.and(function.apply(this.criteria));
        }

        @Override
        public final WA whereIf(Supplier<IPredicate> supplier) {
            return this.ifAnd(supplier);
        }

        @Override
        public final WA whereIf(Function<C, IPredicate> function) {
            return this.ifAnd(function);
        }

        @Override
        public final WA and(final @Nullable IPredicate predicate) {
            if (predicate == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                this.predicateList = predicateList = new ArrayList<>();
            } else if (!(predicateList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            predicateList.add((OperationPredicate) predicate);
            return (WA) this;
        }

        @Override
        public final WA and(Supplier<IPredicate> supplier) {
            return this.and(supplier.get());
        }

        @Override
        public final WA and(Function<C, IPredicate> function) {
            return this.and(function.apply(this.criteria));
        }

        @Override
        public final WA ifAnd(Supplier<IPredicate> supplier) {
            final IPredicate predicate;
            predicate = supplier.get();
            if (predicate != null) {
                this.and(predicate);
            }
            return (WA) this;
        }

        @Override
        public final WA ifAnd(Function<C, IPredicate> function) {
            final IPredicate predicate;
            predicate = function.apply(this.criteria);
            if (predicate != null) {
                this.and(predicate);
            }
            return (WA) this;
        }

        final List<_Predicate> endWhereClause() {
            List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                this.predicateList = predicateList = Collections.emptyList();
            } else if (predicateList instanceof ArrayList) {
                this.predicateList = predicateList = _CollectionUtils.unmodifiableList(predicateList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return predicateList;
        }


    }//MinWhereClause


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

