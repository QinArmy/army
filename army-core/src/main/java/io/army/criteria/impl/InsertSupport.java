package io.army.criteria.impl;

import io.army.annotation.GeneratorType;
import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.Stmt;
import io.army.struct.CodeEnum;
import io.army.util._Assert;
import io.army.util._ClassUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.*;

/**
 * <p>
 * This class hold the base class(interface) of the implementation of all insert syntax interfaces.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
abstract class InsertSupport {

    InsertSupport() {
        throw new UnsupportedOperationException();
    }

    static void assertDomainList(List<?> parentOriginalDomainList, ComplexInsertValuesClause<?, ?, ?, ?> childClause) {
        final List<?> childOriginalList;
        childOriginalList = childClause.originalDomainList();
        if (childOriginalList != parentOriginalDomainList
                && childOriginalList.get(0) != parentOriginalDomainList.get(0)) {
            final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childClause.insertTable;
            throw CriteriaUtils.childParentDomainListNotMatch(childClause.context, childTable);
        }
    }


    interface InsertOptions extends CriteriaContextSpec, _Insert._InsertOption {


    }

    interface ValueSyntaxOptions extends InsertOptions {

        @Nullable
        NullHandleMode nullHandle();

    }

    interface WithClauseOptions extends CriteriaContextSpec {

        boolean isRecursive();

        List<_Cte> cteList();
    }

    interface WithValueSyntaxOptions extends ValueSyntaxOptions, WithClauseOptions {


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

        private LiteralMode literalMode;

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
        public final PR literalMode(LiteralMode mode) {
            this.literalMode = mode;
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
        public final LiteralMode literalMode() {
            final LiteralMode mode = this.literalMode;
            return mode == null ? LiteralMode.DEFAULT : mode;
        }


    }//InsertOptionsImpl

    static abstract class NonQueryInsertOptionsImpl<MR, NR, PR> extends InsertOptionsImpl<MR, PR>
            implements ValueSyntaxOptions, Insert._NullOptionClause<NR> {


        private NullHandleMode nullHandleMode;

        NonQueryInsertOptionsImpl(CriteriaContext context) {
            super(context);
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
    static abstract class NonQueryWithCteOption<MR, NR, PR, B extends CteBuilderSpec, WE>
            extends NonQueryInsertOptionsImpl<MR, NR, PR>
            implements Query._DynamicWithCteClause<B, WE>
            , WithValueSyntaxOptions {

        private boolean recursive;

        private List<_Cte> cteList;

        NonQueryWithCteOption(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }

        @Override
        public final WE with(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endWithClause(builder, true);
        }


        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endWithClause(builder, true);
        }


        @Override
        public final WE ifWith(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endWithClause(builder, false);
        }


        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endWithClause(builder, false);
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            List<_Cte> cteList = this.cteList;
            if (cteList == null) {
                cteList = Collections.emptyList();
            }
            return cteList;
        }

        /**
         * @param cteList unmodified list
         */
        @Deprecated
        final void withClauseEnd(final boolean recursive, final List<_Cte> cteList) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = cteList;
        }

        final void endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(true);
        }


        abstract B createCteBuilder(boolean recursive);


        private WE endWithClause(final B builder, final boolean required) {
            ((CriteriaSupports.CteBuilderSpec) builder).endWithClause(required);
            assert this.cteList != null;
            return (WE) this;
        }


    }//NonQueryWithCteOption

    static abstract class ChildDynamicWithClause<B extends CteBuilderSpec, WE>
            implements Query._DynamicWithCteClause<B, WE>
            , WithValueSyntaxOptions {

        final CriteriaContext context;

        private final boolean migration;

        private final NullHandleMode nullHandleMode;

        private final LiteralMode literalMode;

        private boolean recursive;

        private List<_Cte> cteList;

        ChildDynamicWithClause(ValueSyntaxOptions options, CriteriaContext childContext) {
            this.context = childContext;
            this.migration = options.isMigration();
            this.nullHandleMode = options.nullHandle();
            this.literalMode = options.literalMode();
        }

        @Override
        public final WE with(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }


        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }


        @Override
        public final WE ifWith(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, false);
        }


        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, false);
        }


        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final NullHandleMode nullHandle() {
            return this.nullHandleMode;
        }

        @Override
        public final boolean isMigration() {
            return this.migration;
        }

        @Override
        public final LiteralMode literalMode() {
            return this.literalMode;
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            List<_Cte> cteList = this.cteList;
            if (cteList == null) {
                cteList = Collections.emptyList();
                this.cteList = cteList;
            }
            return cteList;
        }


        final void endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(true);//static with syntax is required
        }


        abstract B createCteBuilder(boolean recursive);


        @SuppressWarnings("unchecked")
        private WE endDynamicWithClause(final B builder, final boolean required) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = builder.isRecursive();
            this.cteList = this.context.endWithClause(required);
            return (WE) this;
        }


    }//DynamicWithClause

    static abstract class SimpleValuesSyntaxOptions implements ValueSyntaxOptions {

        final CriteriaContext context;

        private final NullHandleMode nullHandleMode;

        private final boolean migration;

        private final LiteralMode literalMode;

        SimpleValuesSyntaxOptions(ValueSyntaxOptions options, CriteriaContext context) {
            this.context = context;
            this.nullHandleMode = options.nullHandle();
            this.literalMode = options.literalMode();
            this.migration = options.isMigration();
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final NullHandleMode nullHandle() {
            return this.nullHandleMode;
        }

        @Override
        public final boolean isMigration() {
            return this.migration;
        }

        @Override
        public final LiteralMode literalMode() {
            return this.literalMode;
        }


    }//SimpleValuesSyntaxOptions


    static abstract class ColumnsClause<T, RR>
            implements Insert._ColumnListClause<T, RR>, Insert._StaticColumnDualClause<T, RR>
            , _Insert._ColumnListInsert, ColumnListClause {

        final CriteriaContext context;

        final boolean migration;

        final TableMeta<T> insertTable;

        private List<FieldMeta<?>> fieldList;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        ColumnsClause(CriteriaContext context, boolean migration, @Nullable TableMeta<T> table) {
            if (table == null) {
                //validate for insertInto method
                throw ContextStack.nullPointer(context);
            }
            this.context = context;
            this.migration = migration;
            this.insertTable = table;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(Consumer<Consumer<FieldMeta<T>>> consumer) {
            consumer.accept(this::comma);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(FieldMeta<T> field) {
            return this.comma(field);
        }

        @Override
        public final Insert._StaticColumnDualClause<T, RR> leftParen(FieldMeta<T> field1, FieldMeta<T> field2) {
            this.comma(field1);
            this.comma(field2);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> comma(FieldMeta<T> field) {
            checkField(this.context, this.insertTable, this.migration, field);
            if (!this.migration && _MetaBridge.VISIBLE.equals(field.fieldName())) {
                String m = String.format("%s is managed by army for column list clause,in non-migration mode."
                        , _MetaBridge.VISIBLE);
                throw ContextStack.criteriaError(this.context, m);
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
                throw ContextStack.criteriaError(this.context, m);
            }
            fieldList.add(field);
            return this;
        }

        @Override
        public final Insert._StaticColumnDualClause<T, RR> comma(FieldMeta<T> field1, FieldMeta<T> field2) {
            this.comma(field1);
            this.comma(field2);
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final RR rightParen() {
            final List<FieldMeta<?>> fieldList = this.fieldList;
            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;

            if (fieldList == null) {
                this.fieldList = Collections.emptyList();
                assert fieldMap == null;
            } else if (!(fieldList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
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
                throw ContextStack.castCriteriaApi(this.context);
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
                throw ContextStack.castCriteriaApi(this.context);
            }
            return fieldList;
        }

        final List<? extends TableField> effectiveFieldList() {
            final List<FieldMeta<?>> fieldList = this.fieldList;

            final List<? extends TableField> effectiveList;
            if (fieldList == null) {
                effectiveList = this.insertTable.fieldList();
            } else if (fieldList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            } else {
                effectiveList = fieldList;
            }
            return effectiveList;
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            Map<FieldMeta<?>, Boolean> map = this.fieldMap;
            if (map == null) {
                map = Collections.emptyMap();
            } else if (map instanceof HashMap) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return map;
        }

        @Override
        public void clear() {
            this.fieldList = null;
            this.fieldMap = null;
        }

        @Deprecated
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
                throw ContextStack.criteriaError(this.context, _Exceptions::nonNullField, field);
            }

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
    static abstract class ColumnDefaultClause<T, LR, DR> extends ColumnsClause<T, LR>
            implements Insert._ColumnDefaultClause<T, DR>, _Insert._ValuesSyntaxInsert {


        final LiteralMode literalMode;

        final NullHandleMode nullHandleMode;

        private Map<FieldMeta<?>, _Expression> commonExpMap;

        ColumnDefaultClause(InsertOptions options, TableMeta<T> table) {
            super(options.getContext(), options.isMigration(), table);
            if (options instanceof ValueSyntaxOptions) {
                this.nullHandleMode = ((ValueSyntaxOptions) options).nullHandle();
            } else {
                this.nullHandleMode = null;
            }
            this.literalMode = options.literalMode();
        }

        @Override
        public final DR defaultValue(final FieldMeta<T> field, final @Nullable Expression value) {
            if (this.migration) {
                String m = "migration mode not support default value clause";
                throw ContextStack.criteriaError(this.context, m);
            }

            if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            }
            final ArmyExpression valueExp;
            valueExp = (ArmyExpression) value;
            this.validateField(field, valueExp);

            Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
            if (commonExpMap == null) {
                commonExpMap = new HashMap<>();
                this.commonExpMap = commonExpMap;
            } else if (!(commonExpMap instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (commonExpMap.putIfAbsent(field, valueExp) != null) {
                String m = String.format("duplication default for %s.", field);
                throw ContextStack.criteriaError(this.context, m);
            }
            return (DR) this;
        }

        @Override
        public final DR defaultValue(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            return this.defaultValue(field, supplier.get());
        }

        @Override
        public final DR defaultValue(FieldMeta<T> field, Function<? super FieldMeta<T>, ? extends Expression> function) {
            return this.defaultValue(field, function.apply(field));
        }
        @Override
        public final <E> DR defaultValue(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, @Nullable E value) {
            return this.defaultValue(field, operator.apply(field, value));
        }

        @Override
        public final <E> DR defaultValue(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, Supplier<E> supplier) {
            return this.defaultValue(field, operator.apply(field, supplier.get()));
        }

        @Override
        public final DR defaultValue(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
            return this.defaultValue(field, operator.apply(field, function.apply(keyName)));
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
        public final LiteralMode literalMode() {
            return this.literalMode;
        }

        final void endColumnDefaultClause() {
            final Map<FieldMeta<?>, _Expression> map = this.commonExpMap;
            if (map == null) {
                this.commonExpMap = Collections.emptyMap();
            } else if (map instanceof HashMap) {
                this.commonExpMap = Collections.unmodifiableMap(map);
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
        }


    }//CommonExpClause


    @Deprecated
    static abstract class DomainValueClause<P, CR, DR, VR>
            extends ColumnDefaultClause<P, CR, DR> implements Insert._DomainValueClause<P, VR>
            , _Insert._DomainInsert {


        DomainValueClause(InsertOptions options, TableMeta<P> table) {
            super(options, table);
        }

        @Override
        public final <T extends P> VR value(@Nullable T domain) {
            if (domain == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.endColumnDefaultClause();
            return this.domainValuesEnd(Collections.singletonList(domain));
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
            this.endColumnDefaultClause();
            return this.domainValuesEnd(domainList);
        }

        @Override
        public final <T extends P> VR values(Supplier<List<T>> supplier) {
            return this.values(supplier.get());
        }


        @Override
        public final List<?> domainList() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            super.clear();
        }

        VR valuesEnd() {
            throw new UnsupportedOperationException();
        }

        VR domainValuesEnd(List<?> domainList) {
            throw new UnsupportedOperationException();
        }

        private CriteriaException domainListIsEmpty() {
            return ContextStack.criteriaError(this.context, "domainList must non-empty");
        }


    }//DomainValueClause

    static abstract class DomainValueShortClause<C, P, DR, VR> extends DomainValueClause<P, DR, DR, VR> {

        DomainValueShortClause(InsertOptions options, TableMeta<P> table) {
            super(options, table);
        }

    }//DomainValueShortClause


    @SuppressWarnings("unchecked")
    static abstract class ComplexInsertValuesClause<T, CR, DR, VR> extends ColumnDefaultClause<T, CR, DR>
            implements Insert._DomainValueClause<T, VR>
            , Insert._DynamicValuesClause<T, VR>
            , _Insert._ValuesInsert
            , _Insert._QueryInsert {

        private InsertMode insertMode;

        private List<?> domainList;

        private List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private SubQuery subQuery;

        ComplexInsertValuesClause(InsertOptions options, TableMeta<T> table) {
            super(options, table);
        }


        @Override
        public final <TS extends T> VR value(@Nullable TS domain) {
            if (domain == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (this.insertMode != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();
            this.domainList = Collections.singletonList(domain);
            this.insertMode = InsertMode.DOMAIN;
            return (VR) this;
        }

        @Override
        public final <TS extends T> VR value(Supplier<TS> supplier) {
            return this.value(supplier.get());
        }


        /**
         * @see #domainListForNonParent()
         * @see #domainListForChild(ComplexInsertValuesClause)
         */
        @Override
        public final <TS extends T> VR values(final @Nullable List<TS> domainList) {
            if (domainList == null || domainList.size() == 0) {
                throw ContextStack.criteriaError(this.context, "domainList must non-empty");
            } else if (this.insertMode != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();
            this.domainList = domainList;//just store
            this.insertMode = InsertMode.DOMAIN;
            return (VR) this;
        }

        @Override
        public final <TS extends T> VR values(Supplier<List<TS>> supplier) {
            return this.values(supplier.get());
        }


        @Override
        public final VR values(final Consumer<PairsConstructor<T>> consumer) {
            if (this.insertMode != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();

            final DynamicPairsConstructor<T> constructor;
            constructor = new DynamicPairsConstructor<>(this.context, this::validateField);
            consumer.accept(constructor);
            this.rowPairList = constructor.endPairConsumer();
            this.insertMode = InsertMode.VALUES;
            return (VR) this;
        }


        /**
         * @param rowPairList a unmodified list,empty is allowed.
         */
        final void staticValuesClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowPairList) {
            if (this.insertMode != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();

            this.rowPairList = rowPairList;
            this.insertMode = InsertMode.VALUES;
        }

        final void staticSpaceSubQueryClauseEnd(final SubQuery subQuery) {
            if (this.insertMode != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();

            this.subQuery = subQuery;
            this.insertMode = InsertMode.QUERY;
        }


        final InsertMode getInsertMode() {
            final InsertMode mode = this.insertMode;
            if (mode == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return mode;
        }

        final InsertMode assertInsertMode(final @Nullable ComplexInsertValuesClause<?, ?, ?, ?> parent) {
            final InsertMode mode = this.insertMode;
            assert mode != null;
            if (parent != null && mode == parent.insertMode) {
                String m = String.format("%s and %s insert syntax not match", this.insertTable, parent.insertTable);
                throw ContextStack.criteriaError(this.context, m);
            }
            return mode;
        }

        /**
         * @return a unmodified list,new instance each time.
         */
        final List<?> domainListForNonParent() {
            assert !(this.insertTable instanceof ParentTableMeta);
            final List<?> domainList = this.domainList;
            if (this.insertMode != InsertMode.DOMAIN || domainList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return _CollectionUtils.asUnmodifiableList(domainList);
        }

        /**
         * @return a original list
         */
        final List<?> originalDomainList() {
            assert !(this.insertTable instanceof SimpleTableMeta);
            final List<?> domainList = this.domainList;
            if (this.insertMode != InsertMode.DOMAIN || domainList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return domainList;
        }

        /**
         * @return a unmodified list,new instance each time.
         */
        final List<?> domainListForChild(final ComplexInsertValuesClause<?, ?, ?, ?> parent) {
            assert this.insertTable instanceof ChildTableMeta;
            assert parent.insertTable instanceof ParentTableMeta;

            final List<?> domainList = this.domainList, parentDomainList = parent.domainList;
            if (this.insertMode != InsertMode.DOMAIN || domainList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (domainList != parentDomainList
                    && domainList.get(0) != parentDomainList.get(0)) {
                String m = String.format("%s and %s domain list not match.", this.insertTable, parent.insertTable);
                throw ContextStack.criteriaError(this.context, m);
            }
            return _CollectionUtils.asUnmodifiableList(domainList);
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            final List<Map<FieldMeta<?>, _Expression>> list = this.rowPairList;
            if (this.insertMode != InsertMode.VALUES || list == null || list instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        public final SubQuery subQuery() {
            final SubQuery query = this.subQuery;
            if (this.insertMode != InsertMode.QUERY || query == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return query;
        }


    }//ComplexInsertValuesClause


    static abstract class StaticColumnValuePairClause<T, RR>
            implements Insert._StaticValueLeftParenClause<T, RR>, Insert._StaticColumnValueClause<T, RR>
            , CriteriaContextSpec {

        final CriteriaContext context;


        final BiConsumer<FieldMeta<?>, ArmyExpression> validator;

        private List<Map<FieldMeta<?>, _Expression>> rowList;

        private Map<FieldMeta<?>, _Expression> rowValuesMap;

        StaticColumnValuePairClause(CriteriaContext criteriaContext
                , BiConsumer<FieldMeta<?>, ArmyExpression> validator) {
            this.context = criteriaContext;
            this.validator = validator;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }


        @Override
        public final Insert._StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, Expression value) {
            return this.comma(field, value);
        }

        @Override
        public final Insert._StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            return this.comma(field, supplier.get());
        }

        @Override
        public final Insert._StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, Function<? super FieldMeta<T>, ? extends Expression> function) {
            return this.comma(field, function.apply(field));
        }

        @Override
        public final <E> Insert._StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, @Nullable E value) {
            return this.comma(field, operator.apply(field, value));
        }

        @Override
        public final <E> Insert._StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, Supplier<E> supplier) {
            return this.comma(field, operator.apply(field, supplier.get()));
        }

        @Override
        public final Insert._StaticColumnValueClause<T, RR> leftParen(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
            return this.comma(field, operator.apply(field, function.apply(keyName)));
        }

        @Override
        public final Insert._StaticColumnValueClause<T, RR> comma(final FieldMeta<T> field, final @Nullable Expression value) {
            if (value instanceof DataField) {
                throw ContextStack.criteriaError(this.context, "column value must be non-field.");
            } else if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
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
        public final Insert._StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            return this.comma(field, supplier.get());
        }

        @Override
        public final Insert._StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, Function<? super FieldMeta<T>, ? extends Expression> function) {
            return this.comma(field, function.apply(field));
        }

        @Override
        public final <E> Insert._StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, @Nullable E value) {
            return this.comma(field, operator.apply(field, value));
        }

        @Override
        public final <E> Insert._StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, Supplier<E> supplier) {
            return this.comma(field, operator.apply(field, supplier.get()));
        }

        @Override
        public final Insert._StaticColumnValueClause<T, RR> comma(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
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
                throw ContextStack.castCriteriaApi(this.context);
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
                throw ContextStack.castCriteriaApi(this.context);
            }
            List<Map<FieldMeta<?>, _Expression>> rowValueList = this.rowList;
            if (!(rowValueList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
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

    @Deprecated
    static abstract class DynamicValueInsertValueClauseShort<T, RR, VR>
            extends ColumnDefaultClause<T, RR, RR>
            implements Insert._DynamicValuesClause<T, VR>
            , PairsConstructor<T>, ValueSyntaxOptions {

        public DynamicValueInsertValueClauseShort(ValueSyntaxOptions options, TableMeta<T> table) {
            super(options, table);
        }

        @Override
        public VR values(Consumer<PairsConstructor<T>> consumer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, Expression value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            return null;
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, Function<? super FieldMeta<T>, ? extends Expression> function) {
            return null;
        }

        @Override
        public <E> PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, E value) {
            return null;
        }

        @Override
        public <E> PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, Supplier<E> supplier) {
            return null;
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public PairConsumer<T> row() {
            throw new UnsupportedOperationException();
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
                    throw ContextStack.castCriteriaApi(this.context);
                }
                valuePairList.add(Collections.unmodifiableMap(currentPairMap));
            } else if (currentPairMap != null || this.rowPairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
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
                throw ContextStack.criteriaError(this.context, m);
            } else if (!(currentRowMap instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            }
            this.validator.accept(field, (ArmyExpression) value);

            if (currentRowMap.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.context, field);
            }
            return this;
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            return this.accept(field, supplier.get());
        }

        @Override
        public PairConsumer<T> accept(FieldMeta<T> field, Function<? super FieldMeta<T>, ? extends Expression> function) {
            return this.accept(field, function.apply(field));
        }

        @Override
        public <E> PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, @Nullable E value) {
            return this.accept(field, operator.apply(field, value));
        }

        @Override
        public <E> PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, Supplier<E> supplier) {
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
                throw ContextStack.criteriaError(this.context, m);
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
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.rowMap = null;
            this.rowPairList = null;
            return valuePairList;
        }


    }//DynamicPairsConstructor

    @SuppressWarnings("unchecked")
    static abstract class AssignmentSetClause<T, SR>
            implements Insert._AssignmentSetClause<T, SR>, ColumnListClause, _Insert._AssignmentStatementSpec
            , PairConsumer<T> {
        final CriteriaContext criteriaContext;

        final TableMeta<T> insertTable;

        private Map<FieldMeta<?>, _Expression> fieldPairMap;
        private List<_Pair<FieldMeta<?>, _Expression>> itemPairList;

        AssignmentSetClause(CriteriaContext criteriaContext, TableMeta<T> insertTable) {
            this.criteriaContext = criteriaContext;
            this.insertTable = insertTable;
        }


        @Override
        public final SR setPair(Consumer<PairConsumer<T>> consumer) {
            consumer.accept(this);
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
        public final SR ifSet(FieldMeta<T> field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
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
        public final SR ifSetLiteral(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.addFieldPair(field, SQLs._nullableLiteral(field, value));
            }
            return (SR) this;
        }


        @Override
        public final PairConsumer<T> accept(FieldMeta<T> field, Expression value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final PairConsumer<T> accept(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final PairConsumer<T> accept(FieldMeta<T> field, Function<? super FieldMeta<T>, ? extends Expression> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final <E> PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, E value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final <E> PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, E, ? extends Expression> operator, Supplier<E> supplier) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final PairConsumer<T> accept(FieldMeta<T> field, BiFunction<? super FieldMeta<T>, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void validateField(FieldMeta<?> field, @Nullable ArmyExpression value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final CriteriaContext getContext() {
            return this.criteriaContext;
        }

        @Override
        public final List<_Pair<FieldMeta<?>, _Expression>> pairList() {
            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.itemPairList;
            if (pairList == null || pairList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            return pairList;
        }

        @Override
        public final Map<FieldMeta<?>, _Expression> pairMap() {
            final Map<FieldMeta<?>, _Expression> fieldMap = this.fieldPairMap;
            if (fieldMap == null || fieldMap instanceof HashMap) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            return fieldMap;
        }


        final void endAssignmentSetClause() {
            List<_Pair<FieldMeta<?>, _Expression>> itemPairList = this.itemPairList;
            Map<FieldMeta<?>, _Expression> fieldMap = this.fieldPairMap;

            if (itemPairList == null) {
                itemPairList = Collections.emptyList();

            } else if (!(itemPairList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            } else {
                if (this instanceof AssignmentInsertClause && ((AssignmentInsertClause<T, SR>) this).migration) {
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
                throw ContextStack.criteriaError(this.criteriaContext, m);
            } else if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.criteriaContext);
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


    static abstract class AssignmentInsertClause<T, SR>
            extends AssignmentSetClause<T, SR>
            implements Insert._AssignmentSetClause<T, SR>, _Insert._AssignmentInsert {

        final boolean migration;

        final LiteralMode literalMode;

        AssignmentInsertClause(InsertOptions options, TableMeta<T> table) {
            super(options.getContext(), table);

            this.migration = options.isMigration();
            this.literalMode = options.literalMode();
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
        public final LiteralMode literalMode() {
            return this.literalMode;
        }


        @Override
        public void clear() {
            //no-op
        }


    }//AssignmentInsertClause


    @Deprecated
    static abstract class QueryInsertSpaceClause<T, RR, SR> extends ColumnsClause<T, RR>
            implements _Insert._QueryInsert {

        private SubQuery subQuery;

        QueryInsertSpaceClause(CriteriaContext criteriaContext, TableMeta<T> table) {
            super(criteriaContext, true, table);
        }


        @Override
        public final SubQuery subQuery() {
            final SubQuery query = this.subQuery;
            if (query == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return query;
        }

        abstract SR spaceEnd();

        private SR innerSpace(final @Nullable SubQuery query) {
            if (query == null) {
                throw ContextStack.nullPointer(this.context);
            }
            if (this.subQuery != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.subQuery = query;
            return this.spaceEnd();
        }

    }//QueryInsertSpaceClause


    @SuppressWarnings("unchecked")
    static abstract class ConflictUpdateSetClause<T, SR> implements Update._StaticSetClause<FieldMeta<T>, SR>
            , CriteriaContextSpec
            , _Insert._ConflictActionClauseSpec {

        final CriteriaContext context;

        final TableMeta<T> insertTable;

        private final boolean supportRow;

        private List<ItemPair> itemPairList;


        ConflictUpdateSetClause(CriteriaContext context, TableMeta<T> insertTable, boolean supportRow) {
            this.context = context;
            this.insertTable = insertTable;
            this.supportRow = supportRow;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final SR setPairs(Consumer<Consumer<ItemPair>> consumer) {
            consumer.accept(this::addItemPair);
            return (SR) this;
        }



        @Override
        public final SR set(FieldMeta<T> field, Expression value) {
            return this.addFieldValuePair(field, value);
        }

        @Override
        public final SR set(FieldMeta<T> field, Supplier<Expression> supplier) {
            return this.addFieldValuePair(field, supplier.get());
        }

        @Override
        public final <E> SR set(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, @Nullable E value) {
            return this.addFieldValuePair(field, valueOperator.apply(field, value));
        }

        @Override
        public final <E> SR set(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator
                , Supplier<E> supplier) {
            return this.addFieldValuePair(field, valueOperator.apply(field, supplier.get()));
        }

        @Override
        public final SR set(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            return this.addFieldValuePair(field, valueOperator.apply(field, function.apply(keyName)));
        }

        @Override
        public final <E> SR set(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator
                , BiFunction<FieldMeta<T>, E, Expression> valueOperator, @Nullable E value) {
            return this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
        }

        @Override
        public final <E> SR set(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator
                , BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier) {
            return this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, supplier.get())));
        }

        @Override
        public final SR set(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator
                , BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            return this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, function.apply(keyName))));
        }

        @Override
        public final <E> SR ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator
                , @Nullable E value) {
            if (value != null) {
                this.addFieldValuePair(field, valueOperator.apply(field, value));
            }
            return (SR) this;
        }

        @Override
        public final <E> SR ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator
                , Supplier<E> supplier) {
            final E value;
            value = supplier.get();
            if (value != null) {
                this.addFieldValuePair(field, valueOperator.apply(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.addFieldValuePair(field, valueOperator.apply(field, value));
            }
            return (SR) this;
        }

        @Override
        public final <E> SR ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator
                , BiFunction<FieldMeta<T>, E, Expression> valueOperator, @Nullable E value) {
            if (value != null) {
                this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
            }
            return (SR) this;
        }

        @Override
        public final <E> SR ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator
                , BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier) {
            final E value;
            value = supplier.get();
            if (value != null) {
                this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator
                , BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
            }
            return (SR) this;
        }

        @Override
        public final List<ItemPair> updateSetClauseList() {
            final List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null || itemPairList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return itemPairList;
        }

        /**
         * @return a unmodified list
         */
        final List<ItemPair> endUpdateSetClause() {
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                itemPairList = Collections.emptyList();
                this.itemPairList = itemPairList;
            } else if (itemPairList instanceof ArrayList) {
                itemPairList = _CollectionUtils.unmodifiableList(itemPairList);
                this.itemPairList = itemPairList;
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return itemPairList;
        }

        abstract Dialect syntaxDialect();


        final void addSafeRowPair(final SQLs.RowItemPair pair) {
            assert this.supportRow;
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                itemPairList = new ArrayList<>();
                this.itemPairList = itemPairList;
            } else if (!(itemPairList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            itemPairList.add(pair);
        }

        private SR addFieldValuePair(final FieldMeta<T> field, final @Nullable Expression value) {
            if (field.updateMode() == UpdateMode.IMMUTABLE) {
                throw ContextStack.criteriaError(this.context, _Exceptions::immutableField, field);
            } else if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            } else if (!field.nullable() && ((ArmyExpression) value).isNullValue()) {
                throw ContextStack.criteriaError(this.context, _Exceptions::nonNullField, field);
            }
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                itemPairList = new ArrayList<>();
                this.itemPairList = itemPairList;
            }
            itemPairList.add(SQLs._itemPair(field, null, value));
            return (SR) this;
        }

        private SR addItemPair(final ItemPair pair) {
            final SQLs.FieldItemPair fieldPair;
            final FieldMeta<?> field;
            if (pair instanceof SQLs.RowItemPair) {
                if (!this.supportRow) {
                    throw ContextStack.criteriaError(this.context, _Exceptions::dontSupportRowLeftItem
                            , this.syntaxDialect());
                }
                this.validateRowPair((SQLs.RowItemPair) pair);
            } else if (!(pair instanceof SQLs.FieldItemPair)) {
                throw ContextStack.criteriaError(this.context, "Illegal ItemPair");
            } else if (!((fieldPair = (SQLs.FieldItemPair) pair).field instanceof FieldMeta)) {
                throw ContextStack.criteriaError(this.context, _Exceptions::unsupportedFieldType
                        , fieldPair.field);
            } else if ((field = (FieldMeta<?>) fieldPair.field).updateMode() == UpdateMode.IMMUTABLE) {
                throw ContextStack.criteriaError(this.context, _Exceptions::immutableField, field);
            } else if (!field.nullable() && ((ArmyExpression) fieldPair.right).isNullValue()) {
                throw ContextStack.criteriaError(this.context, _Exceptions::nonNullField, field);
            }
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                this.itemPairList = itemPairList = new ArrayList<>();
            }
            itemPairList.add(pair);
            return (SR) this;
        }

        private void validateRowPair(final SQLs.RowItemPair pair) {
            final List<DataField> fieldList = pair.fieldList;
            final SubQuery query = (SubQuery) pair.right;
            if (((_Query) query).selectionSize() != fieldList.size()) {
                final Supplier<CriteriaException> supplier;
                supplier = () -> _Exceptions.rowSetSelectionAndFieldSizeNotMatch(((_Query) query).selectionSize()
                        , fieldList.size(), this.insertTable);
                throw ContextStack.criteriaError(this.context, supplier);
            }
            FieldMeta<?> field;
            final TableMeta<?> insertTable = this.insertTable;
            for (DataField dataField : fieldList) {
                if (!(dataField instanceof FieldMeta)) {
                    throw ContextStack.criteriaError(this.context, _Exceptions::unsupportedFieldType
                            , dataField);
                }
                field = (FieldMeta<?>) dataField;
                if (field.tableMeta() != insertTable) {
                    throw ContextStack.criteriaError(this.context, _Exceptions::unknownColumn, field);
                } else if (field.updateMode() == UpdateMode.IMMUTABLE) {
                    throw ContextStack.criteriaError(this.context, _Exceptions::immutableField, field);
                }
            }

        }

    }//ConflictUpdateSetClause

    @SuppressWarnings("unchecked")
    static abstract class ConflictUpdateWhereClause<T, SR, WR, WA> extends ConflictUpdateSetClause<T, SR>
            implements Statement._MinQueryWhereClause<WR, WA>
            , Statement._MinWhereAndClause<WA>
            , _Insert._ConflictActionPredicateClauseSpec {

        private List<_Predicate> predicateList;

        ConflictUpdateWhereClause(CriteriaContext context, TableMeta<T> table) {
            super(context, table, true);
        }

        @Override
        public final WR where(Consumer<Consumer<IPredicate>> consumer) {
            consumer.accept(this::and);
            if (this.predicateList == null) {
                throw ContextStack.criteriaError(this.context, _Exceptions::predicateListIsEmpty);
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
        public final WA whereIf(Supplier<IPredicate> supplier) {
            return this.ifAnd(supplier);
        }

        @Override
        public final WR ifWhere(Consumer<Consumer<IPredicate>> consumer) {
            consumer.accept(this::and);
            return (WR) this;
        }


        @Override
        public final WA and(final @Nullable IPredicate predicate) {
            if (predicate == null) {
                throw ContextStack.nullPointer(this.context);
            }
            List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                predicateList = new ArrayList<>();
                this.predicateList = predicateList;
            } else if (!(predicateList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            predicateList.add((OperationPredicate) predicate);
            return (WA) this;
        }

        @Override
        public final WA and(Supplier<IPredicate> supplier) {
            return this.and(supplier.get());
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
        public final List<_Predicate> updateSetPredicateList() {
            final List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null || predicateList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return predicateList;
        }

        final List<_Predicate> endWhereClause() {

            List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                this.predicateList = predicateList = Collections.emptyList();
            } else if (predicateList instanceof ArrayList) {
                this.predicateList = predicateList = _CollectionUtils.unmodifiableList(predicateList);
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return predicateList;
        }


    }//ConflictUpdateWhereClause


    @SuppressWarnings("unchecked")
    static class MinWhereClause<WR, WA> implements Statement._MinQueryWhereClause<WR, WA>
            , Statement._MinWhereAndClause<WA>
            , CriteriaContextSpec {

        final CriteriaContext context;

        private List<_Predicate> predicateList;

        MinWhereClause(CriteriaContext context) {
            this.context = context;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final WR where(Consumer<Consumer<IPredicate>> consumer) {
            consumer.accept(this::and);
            if (this.predicateList == null) {
                throw ContextStack.criteriaError(this.context, _Exceptions::predicateListIsEmpty);
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
        public final WA whereIf(Supplier<IPredicate> supplier) {
            return this.ifAnd(supplier);
        }

        @Override
        public final WA and(final @Nullable IPredicate predicate) {
            if (predicate == null) {
                throw ContextStack.nullPointer(this.context);
            }
            List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                this.predicateList = predicateList = new ArrayList<>();
            } else if (!(predicateList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            predicateList.add((OperationPredicate) predicate);
            return (WA) this;
        }

        @Override
        public final WA and(Supplier<IPredicate> supplier) {
            return this.and(supplier.get());
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

        final List<_Predicate> endWhereClause() {
            List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null) {
                this.predicateList = predicateList = Collections.emptyList();
            } else if (predicateList instanceof ArrayList) {
                this.predicateList = predicateList = _CollectionUtils.unmodifiableList(predicateList);
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return predicateList;
        }

        final List<_Predicate> predicateList() {
            final List<_Predicate> predicateList = this.predicateList;
            if (predicateList == null || predicateList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return predicateList;
        }


    }//MinWhereClause

    private static abstract class AbstractInsertStatement<I extends DmlInsert, Q extends DqlInsert>
            implements _Insert
            , Statement.StatementMockSpec
            , Statement
            , CriteriaContextSpec
            , Statement._DmlInsertSpec<I>, DqlInsert._DqlInsertSpec<Q> {

        final CriteriaContext context;

        final TableMeta<?> insertTable;

        private Boolean prepared;

        AbstractInsertStatement(_Insert clause) {
            this.context = ((CriteriaContextSpec) clause).getContext();
            this.insertTable = clause.table();
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
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
            return this.insertTable;
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
            if (this instanceof _Insert._ChildInsert) {
                ((_ChildInsert) this).parentStmt().clear();
            }
            this.prepared = Boolean.FALSE;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final I asInsert() {
            if (!(this instanceof DmlInsert)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.asInsertStatement();
            return (I) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final Q asReturningInsert() {
            if (!(this instanceof DqlInsert)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.asInsertStatement();
            return (Q) this;
        }

        private void asInsertStatement() {
            _Assert.nonPrepared(this.prepared);

            if (this instanceof _Insert._ChildInsert) {
                ((AbstractInsertStatement<?, ?>) ((_ChildInsert) this).parentStmt())
                        .prepareParentStatement();
            }
            insertStatementGuard(this);

            //finally clear context
            if (this instanceof SubStatement) {
                ContextStack.pop(this.context);
            } else {
                ContextStack.clearContextStack(this.context);
            }
            this.prepared = Boolean.TRUE;
        }

        private Stmt mockStmt(DialectParser parser, Visible visible) {
            final Stmt stmt;
            if (this instanceof Insert) {
                stmt = parser.insert((Insert) this, visible);
            } else if (this instanceof ReplaceInsert
                    || this instanceof MergeInsert
                    || this instanceof ReturningInsert) {
                stmt = parser.dialectStmt((DialectStatement) this, visible);
            } else {
                //non-primary insert
                throw ContextStack.castCriteriaApi(this.context);
            }
            return stmt;
        }


        private void prepareParentStatement() {
            _Assert.nonPrepared(this.prepared);
            assert this.insertTable instanceof ParentTableMeta;
            assert !(this instanceof _Insert._ChildInsert);
            this.prepared = Boolean.TRUE;
        }

    }//AbstractInsertStatement


    static abstract class AbstractValueSyntaxStatement<I extends DmlInsert, Q extends DqlInsert>
            extends AbstractInsertStatement<I, Q>
            implements _Insert._ValuesSyntaxInsert {

        private final boolean migration;

        private final NullHandleMode nullHandleMode;

        private final LiteralMode literalMode;

        private final List<FieldMeta<?>> fieldList;

        private final Map<FieldMeta<?>, Boolean> fieldMap;

        private final Map<FieldMeta<?>, _Expression> defaultExpMap;

        AbstractValueSyntaxStatement(_ValuesSyntaxInsert clause) {
            super(clause);

            this.migration = clause.isMigration();
            this.nullHandleMode = clause.nullHandle();
            this.literalMode = clause.literalMode();
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
        public final LiteralMode literalMode() {
            return this.literalMode;
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


    }//AbstractValueSyntaxStatement


    static abstract class ValueSyntaxInsertStatement<I extends DmlInsert>
            extends AbstractValueSyntaxStatement<I, DqlInsert>
            implements ValueSyntaxOptions {

        ValueSyntaxInsertStatement(_ValuesSyntaxInsert clause) {
            super(clause);

        }


    }//ValueInsertStatement


    private static abstract class AbstractAssignmentInsertStatement<I extends DmlInsert, Q extends DqlInsert>
            extends AbstractInsertStatement<I, Q> implements _Insert._AssignmentInsert, ValueSyntaxOptions {

        private final boolean migration;

        private final LiteralMode preferLiteral;

        private final List<_Pair<FieldMeta<?>, _Expression>> rowPairList;

        private final Map<FieldMeta<?>, _Expression> fieldMap;

        AbstractAssignmentInsertStatement(_AssignmentInsert clause) {
            super(clause);
            this.migration = clause.isMigration();
            this.preferLiteral = clause.literalMode();
            this.rowPairList = clause.pairList();

            this.fieldMap = clause.pairMap();
        }

        @Override
        public final boolean isMigration() {
            return this.migration;
        }

        @Override
        public final LiteralMode literalMode() {
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

    }//AbstractAssignmentInsertStatement

    static abstract class AssignmentInsertStatement<I extends DmlInsert>
            extends AbstractAssignmentInsertStatement<I, DqlInsert> {

        AssignmentInsertStatement(_AssignmentInsert clause) {
            super(clause);
        }

    }//AssignmentInsertStatement


    static abstract class AbstractQuerySyntaxInsertStatement<I extends DmlInsert, Q extends DqlInsert>
            extends AbstractInsertStatement<I, Q>
            implements _Insert._QueryInsert, ValueSyntaxOptions {

        private final List<FieldMeta<?>> fieldList;

        private final Map<FieldMeta<?>, Boolean> fieldMap;

        private final SubQuery query;

        AbstractQuerySyntaxInsertStatement(_QueryInsert clause) {
            super(clause);
            this.fieldList = clause.fieldList();
            this.fieldMap = clause.fieldMap();
            this.query = clause.subQuery();
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
            return this.query;
        }

        @Override
        public final NullHandleMode nullHandle() {
            //always null,query insert don't support this
            return null;
        }

        @Override
        public final boolean isMigration() {
            //always true,query insert do this
            return true;
        }

        @Override
        public final LiteralMode literalMode() {
            //always DEFAULT,query insert don't support this
            return LiteralMode.DEFAULT;
        }


    }//AbstractQuerySyntaxInsertStatement


    static abstract class QuerySyntaxInsertStatement<I extends DmlInsert>
            extends AbstractQuerySyntaxInsertStatement<I, DqlInsert> {


        QuerySyntaxInsertStatement(_QueryInsert clause) {
            super(clause);

        }


    }//QueryInsertStatement


    static abstract class QuerySyntaxReturningInsertStatement<Q extends DqlInsert>
            extends AbstractQuerySyntaxInsertStatement<DmlInsert, Q>
            implements DqlInsert {

        QuerySyntaxReturningInsertStatement(_QueryInsert clause) {
            super(clause);
        }

    }//QuerySyntaxReturningInsertStatement


    static void checkField(final CriteriaContext context, final TableMeta<?> table
            , final boolean migration, final FieldMeta<?> field) {

        if (field.tableMeta() != table) {
            //don't contain parent field
            throw ContextStack.criteriaError(context, _Exceptions::unknownColumn, field);
        } else if (migration) {
            if (field instanceof PrimaryFieldMeta && field.tableMeta() instanceof ChildTableMeta) {
                throw childIdIsManaged(context, (ChildTableMeta<?>) field.tableMeta());
            }
        } else if (!field.insertable()) {
            throw ContextStack.criteriaError(context, _Exceptions::nonInsertableField, field);
        } else if (isArmyManageField(table, field)) {
            throw ContextStack.criteriaError(context, _Exceptions::armyManageField, field);
        } else if (field instanceof PrimaryFieldMeta && field.tableMeta() instanceof ChildTableMeta) {
            throw childIdIsManaged(context, (ChildTableMeta<?>) field.tableMeta());
        }

    }


    static CriteriaException notContainField(CriteriaContext criteriaContext, FieldMeta<?> field) {
        String m = String.format("insert field list don't contain %s", field);
        return ContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException duplicationValuePair(CriteriaContext criteriaContext, FieldMeta<?> field) {
        String m = String.format("duplication value of %s at same row.", field);
        return ContextStack.criteriaError(criteriaContext, m);
    }


    private static CriteriaException nonDomainInstance(CriteriaContext criteriaContext, @Nullable Object domain
            , TableMeta<?> table) {
        String m;
        m = String.format("%s isn't %s instance.", _ClassUtils.safeClassName(domain), table.javaType().getName());
        return ContextStack.criteriaError(criteriaContext, m);
    }

    private static CriteriaException childIdIsManaged(CriteriaContext criteriaContext, ChildTableMeta<?> table) {
        return ContextStack.criteriaError(criteriaContext, _Exceptions::childIdIsManagedByArmy, table);
    }

    static CriteriaException childAndParentRowsNotMatch(CriteriaContext criteriaContext
            , ChildTableMeta<?> table, int parent, int child) {
        Supplier<CriteriaException> supplier;
        supplier = () -> _Exceptions.childAndParentRowsNotMatch(table, parent, child);
        return ContextStack.criteriaError(criteriaContext, supplier);
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
                    throw ContextStack.criteriaError(context, _Exceptions::migrationManageGeneratorField, field);
                }
            }
            field = insertTable.discriminator();
            if (field != null && fieldMap.get(field) == null) {
                throw ContextStack.criteriaError(context, _Exceptions::migrationManageGeneratorField, field);
            }
        }

        for (FieldMeta<?> field : insertTable.fieldChain()) {
            if (!field.nullable() && fieldMap.get(field) == null) {
                throw ContextStack.criteriaError(context, _Exceptions::migrationModeGeneratorField, field);
            }
        }

    }


    /**
     * <p>
     * Check insert statement for safety.
     * </p>
     */
    private static void insertStatementGuard(final _Insert statement) {
        if (!(statement instanceof _Insert._ChildInsert)) {
            if (statement instanceof _Insert._SupportWithClauseInsert) {
                if (statement instanceof PrimaryStatement) {
                    validateSupportWithClauseInsert((_Insert._SupportWithClauseInsert) statement);
                }
            } else if (statement instanceof _Insert._QueryInsert) {
                validateQueryInsert((_Insert._QueryInsert) statement);
            }
        } else if (isForbidChildSyntax((_Insert._ChildInsert) statement)) {
            final ParentTableMeta<?> parentTable;
            parentTable = ((ChildTableMeta<?>) statement.table()).parentMeta();
            String m = String.format("%s id %s is %s ,so you couldn't use duplicate key clause(on conflict)"
                    , parentTable, GeneratorType.class.getName()
                    , parentTable.id().generatorType());
            throw ContextStack.criteriaError(((CriteriaContextSpec) statement).getContext(), m);
        } else if (statement instanceof _Insert._QueryInsert) {
            validateQueryInsert((_Insert._QueryInsert) statement);
        } else if (statement instanceof _Insert._ChildDomainInsert) {
            validateChildDomainInsert((_Insert._ChildDomainInsert) statement);
        } else if (statement instanceof _Insert._ChildValuesInsert) {
            validateChildValueInsert((_Insert._ChildValuesInsert) statement);
        } else if (!(statement instanceof _Insert._ChildAssignmentInsert)) {
            //no bug,never here
            throw new IllegalStateException();
        }

    }

    /**
     * @see #insertStatementGuard(_Insert)
     */
    private static void validateSupportWithClauseInsert(final _Insert._SupportWithClauseInsert statement) {
        assert statement instanceof PrimaryStatement;
        //TODO 完成校验
//
//        final List<Cte> cteList;
//         cteList = statement.cteList();
//           final int cteSize ;
//            cteSize = cteList.size();
//           SQLs.CteImpl cte;
//           TableMeta<?> insertTable;
//        _Insert._SupportWithClauseInsert subInsert;
//        for (int i = cteSize - 1; i > -1 ; i--) {
//             cte = (SQLs.CteImpl)cteList.get(i);
//             if(!(cte.subStatement instanceof SubReturningInsert)){
//                  continue;
//             }
//            subInsert = (_Insert._SupportWithClauseInsert)cte.subStatement;
//            insertTable = subInsert.table();
//            if(insertTable instanceof SimpleTableMeta){
//                continue;
//            }
//
//
//        }
        // throw new UnsupportedOperationException();
    }

    /**
     * @return true : representing insert {@link ChildTableMeta} and syntax error
     * , statement executor couldn't get the auto increment primary key of {@link ParentTableMeta}
     * @see #insertStatementGuard(_Insert)
     */
    private static boolean isForbidChildSyntax(final _Insert._ChildInsert child) {
        final _Insert parentStmt;
        parentStmt = child.parentStmt();
        return parentStmt instanceof _Insert._SupportConflictClauseSpec
                && !(parentStmt instanceof _Insert._SupportReturningClauseSpec)
                && ((_Insert._SupportConflictClauseSpec) parentStmt).hasConflictAction()
                && parentStmt.table().id().generatorType() == GeneratorType.POST;
    }

    /**
     * @see #insertStatementGuard(_Insert)
     */
    private static void validateQueryInsert(final _Insert._QueryInsert statement) {
        SubQuery query;
        _Insert._QueryInsert currentStatement = statement;
        //1. validate column size and sub query selection size
        for (int i = 0, selectionSize, columnSize; i < 2; i++) {

            query = currentStatement.subQuery();
            selectionSize = ((_Query) query).selectionSize();
            columnSize = currentStatement.fieldList().size();
            if (columnSize == 0) {
                throw ContextStack.castCriteriaApi(((CriteriaContextSpec) statement).getContext());
            }
            if (selectionSize != columnSize) {
                String m = String.format("%s insert statement selection size[%s] and column size[%s] not match"
                        , currentStatement.table(), selectionSize, columnSize);
                throw ContextStack.criteriaError(((CriteriaContextSpec) statement).getContext(), m);
            }
            if (!(currentStatement instanceof _Insert._ChildQueryInsert)) {
                break;
            }
            currentStatement = ((_Insert._ChildQueryInsert) currentStatement).parentStmt();
        }

        final TableMeta<?> insertTable;
        insertTable = statement.table();
        if (insertTable instanceof SimpleTableMeta) {
            return;
        }
        //2. validate parent statement discriminator
        final List<FieldMeta<?>> fieldList;
        final List<? extends SelectItem> selectItemList;
        if (statement instanceof _Insert._ChildQueryInsert) {
            final _Insert._QueryInsert parentStmt;
            parentStmt = ((_Insert._ChildQueryInsert) statement).parentStmt();
            fieldList = parentStmt.fieldList();
            selectItemList = parentStmt.subQuery().selectItemList();
        } else {
            fieldList = statement.fieldList();
            selectItemList = statement.subQuery().selectItemList();
        }
        //2.1 find discriminatorSelection
        final int fieldSize = fieldList.size();
        final FieldMeta<?> discriminatorField = insertTable.discriminator();
        assert discriminatorField != null;
        Selection discriminatorSelection = null;
        outerFor:
        for (int i = 0, selectionIndex; i < fieldSize; i++) {
            if (fieldList.get(i) != discriminatorField) {
                continue;
            }
            selectionIndex = 0;
            for (SelectItem selectItem : selectItemList) {
                if (selectItem instanceof Selection) {
                    if (selectionIndex == i) {
                        discriminatorSelection = (Selection) selectItem;
                        break outerFor;
                    }
                    selectionIndex++;
                    continue;
                }
                if (!(selectItem instanceof SelectionGroup)) {
                    //no bug,never here
                    throw _Exceptions.unknownSelectItem(selectItem);
                }
                for (Selection selection : ((SelectionGroup) selectItem).selectionList()) {
                    if (selectionIndex == i) {
                        discriminatorSelection = selection;
                        break outerFor;
                    }
                    selectionIndex++;
                }

            }

            break;
        }//outerFor

        if (discriminatorSelection == null) {
            //army syntax api forbid this
            throw ContextStack.castCriteriaApi(((CriteriaContextSpec) statement).getContext());
        }

        //2.2 validate discriminatorExp
        final Expression discriminatorExp;
        discriminatorExp = ((_Selection) discriminatorSelection).selectionExp();

        if (!(discriminatorExp instanceof LiteralExpression.SingleLiteral)) {
            String m = String.format("The appropriate %s[%s] of discriminator %s must be literal."
                    , Selection.class.getSimpleName(), discriminatorSelection.alias()
                    , discriminatorField);
            throw ContextStack.criteriaError(((CriteriaContextSpec) statement).getContext(), m);
        }

        final Object value;
        value = ((LiteralExpression.SingleLiteral) discriminatorExp).value();
        final Class<?> discriminatorJavaType;
        discriminatorJavaType = discriminatorField.javaType();

        if (!discriminatorJavaType.isInstance(value)) {
            String m = String.format("The appropriate %s[%s] of discriminator %s must be instance of %s."
                    , Selection.class.getSimpleName(), discriminatorSelection.alias()
                    , discriminatorField, discriminatorJavaType.getName());
            throw ContextStack.criteriaError(((CriteriaContextSpec) statement).getContext(), m);
        }

        final CodeEnum discriminatorEnum;
        discriminatorEnum = insertTable.discriminatorValue();
        assert discriminatorEnum != null;
        if (value != discriminatorEnum) {
            String m = String.format("The appropriate %s[%s] of discriminator %s must be %s.%s ."
                    , Selection.class.getSimpleName(), discriminatorSelection.alias()
                    , discriminatorField, discriminatorJavaType.getName()
                    , discriminatorEnum.name());
            throw ContextStack.criteriaError(((CriteriaContextSpec) statement).getContext(), m);
        }

    }

    /**
     * @see #insertStatementGuard(_Insert)
     */
    private static void validateChildDomainInsert(final _Insert._ChildDomainInsert childStmt) {
        final List<?> childDomainList, parentDomainList;
        childDomainList = childStmt.domainList();
        parentDomainList = childStmt.parentStmt().domainList();
        if (childDomainList != parentDomainList) {
            final CriteriaContext context;
            context = ((CriteriaContextSpec) childStmt).getContext();
            throw CriteriaUtils.childParentDomainListNotMatch(context, (ChildTableMeta<?>) childStmt.table());
        }
    }

    /**
     * @see #insertStatementGuard(_Insert)
     */
    private static void validateChildValueInsert(final _Insert._ChildValuesInsert childStmt) {
        final List<?> childPairList, parentPairList;
        childPairList = childStmt.rowPairList();
        parentPairList = childStmt.parentStmt().rowPairList();
        if (childPairList.size() != parentPairList.size()) {
            String m = String.format("%s insert row number[%s] and parent insert row number[%s] not match"
                    , childStmt.table(), childPairList.size()
                    , parentPairList.size());
            throw ContextStack.criteriaError(((CriteriaContextSpec) childStmt).getContext(), m);
        }
    }


}

