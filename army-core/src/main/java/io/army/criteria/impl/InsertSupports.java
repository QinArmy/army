package io.army.criteria.impl;

import io.army.annotation.GeneratorType;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.dialect._DialectUtils;
import io.army.lang.Nullable;
import io.army.mapping.CodeEnumType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util.ArmyCriteria;
import io.army.util._Assert;
import io.army.util._Collections;
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
abstract class InsertSupports {

    InsertSupports() {
        throw new UnsupportedOperationException();
    }


    interface InsertOptions extends CriteriaContextSpec, _Insert._InsertOption {


    }

    interface ValueSyntaxOptions extends InsertOptions {

        boolean isIgnoreReturnIds();


        NullMode nullHandle();

    }

    interface WithClauseOptions extends CriteriaContextSpec {

        boolean isRecursive();

        List<_Cte> cteList();
    }

    interface WithValueSyntaxOptions extends ValueSyntaxOptions, WithClauseOptions {


    }

    interface ParentQueryInsert extends _Insert._ParentQueryInsert {

        void onValidateEnd(CodeEnum discriminatorValue);
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

    static abstract class InsertOptionsImpl<R> implements InsertOptions {

        final CriteriaContext context;

        private boolean migration;

        private LiteralMode literalMode = LiteralMode.DEFAULT;

        InsertOptionsImpl(CriteriaContext criteriaContext) {
            this.context = criteriaContext;
        }

        @SuppressWarnings("unchecked")
        public final R migration() {
            this.migration = true;
            return (R) this;
        }

        @SuppressWarnings("unchecked")
        public final R literalMode(final @Nullable LiteralMode mode) {
            if (mode == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.literalMode = mode;
            return (R) this;
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
            assert mode != null;
            return mode;
        }


    }//InsertOptionsImpl

    static abstract class NonQueryInsertOptionsImpl<R> extends InsertOptionsImpl<R>
            implements ValueSyntaxOptions {


        private NullMode nullMode = NullMode.INSERT_DEFAULT;

        private boolean ignoreReturnIds;

        NonQueryInsertOptionsImpl(CriteriaContext context) {
            super(context);
        }

        @SuppressWarnings("unchecked")
        public final R nullMode(final @Nullable NullMode mode) {
            if (mode == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.nullMode = mode;
            return (R) this;
        }

        @SuppressWarnings("unchecked")
        public final R ignoreReturnIds() {
            this.ignoreReturnIds = true;
            return (R) this;
        }

        @Override
        public final NullMode nullHandle() {
            final NullMode mode = this.nullMode;
            assert mode != null;
            return mode;
        }

        @Override
        public final boolean isIgnoreReturnIds() {
            return this.ignoreReturnIds;
        }


    }//NonQueryInsertOptionsImpl


    @SuppressWarnings("unchecked")
    static abstract class NonQueryWithCteOption<R, B extends CteBuilderSpec, WE extends Item>
            extends NonQueryInsertOptionsImpl<R>
            implements DialectStatement._DynamicWithClause<B, WE>,
            WithValueSyntaxOptions {

        private boolean recursive;

        private List<_Cte> cteList;

        NonQueryWithCteOption(CriteriaContext context) {
            super(context);
        }

        NonQueryWithCteOption(@Nullable ArmyStmtSpec spec, CriteriaContext context) {
            super(context);
            if (spec != null) {
                this.recursive = spec.isRecursive();
                this.cteList = spec.cteList();
            }
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
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            List<_Cte> cteList = this.cteList;
            if (cteList == null) {
                cteList = _Collections.emptyList();
                this.cteList = cteList;
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

        final WE endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, true);
            return (WE) this;
        }


        abstract B createCteBuilder(boolean recursive);


        private WE endDynamicWithClause(final B builder, final boolean required) {
            ((CriteriaSupports.CteBuilder) builder).endLastCte();
            final boolean recursive;
            recursive = builder.isRecursive();
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, required);
            return (WE) this;
        }


    }//NonQueryWithCteOption


    static abstract class ChildOptionClause implements ValueSyntaxOptions {

        final CriteriaContext context;

        private final boolean migration;

        private final NullMode nullHandleMode;

        private final LiteralMode literalMode;

        private final boolean ignoreReturnIds;

        ChildOptionClause(ValueSyntaxOptions options, CriteriaContext context) {
            this.context = context;
            this.migration = options.isMigration();
            this.nullHandleMode = options.nullHandle();
            this.literalMode = options.literalMode();

            this.ignoreReturnIds = options.isIgnoreReturnIds();
        }


        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final NullMode nullHandle() {
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
        public final boolean isIgnoreReturnIds() {
            return this.ignoreReturnIds;
        }


    }//ChildOptionClause

    static abstract class ChildDynamicWithClause<B extends CteBuilderSpec, WE extends Item>
            extends ChildOptionClause
            implements DialectStatement._DynamicWithClause<B, WE>, WithValueSyntaxOptions {
        private boolean recursive;

        private List<_Cte> cteList;

        ChildDynamicWithClause(ValueSyntaxOptions options, CriteriaContext childContext) {
            super(options, childContext);
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


        @SuppressWarnings("unchecked")
        final WE endStaticWithClause(final boolean recursive) {
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, true);//static with syntax is required
            return (WE) this;
        }


        abstract B createCteBuilder(boolean recursive);


        @SuppressWarnings("unchecked")
        private WE endDynamicWithClause(final B builder, final boolean required) {
            ((CriteriaSupports.CteBuilder) builder).endLastCte();

            final boolean recursive;
            recursive = builder.isRecursive();
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, required);
            return (WE) this;
        }


    }//DynamicWithClause

    static abstract class SimpleValuesSyntaxOptions implements ValueSyntaxOptions {

        final CriteriaContext context;

        private final NullMode nullHandleMode;

        private final boolean migration;

        private final LiteralMode literalMode;

        private final boolean ignoreReturnIds;

        SimpleValuesSyntaxOptions(ValueSyntaxOptions options, CriteriaContext context) {
            this.context = context;
            this.nullHandleMode = options.nullHandle();
            this.literalMode = options.literalMode();
            this.migration = options.isMigration();
            this.ignoreReturnIds = options.isIgnoreReturnIds();
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final NullMode nullHandle() {
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
        public final boolean isIgnoreReturnIds() {
            return this.ignoreReturnIds;
        }


    }//SimpleValuesSyntaxOptions


    static abstract class ColumnsClause<T, R>
            implements InsertStatement._ColumnListParensClause<T, R>,
            InsertStatement._StaticColumnSpaceClause<T>,
            InsertStatement._StaticColumnCommaQuadraClause<T>,
            _Insert._ColumnListInsert,
            ColumnListClause {

        final CriteriaContext context;

        final boolean migration;

        final boolean twoStmtMode;

        final TableMeta<T> insertTable;

        private List<FieldMeta<?>> fieldList;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        private ColumnsClause(CriteriaContext context, boolean migration, TableMeta<T> table, boolean twoStmtMode) {
            this.context = context;
            this.migration = migration;
            this.twoStmtMode = twoStmtMode && table instanceof ChildTableMeta;
            this.insertTable = table;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }


        @Override
        public final R parens(Consumer<InsertStatement._StaticColumnSpaceClause<T>> consumer) {
            consumer.accept(this);
            return this.endColumnListClause(true);
        }


        @Override
        public final R parens(SQLs.SymbolSpace space, Consumer<Consumer<FieldMeta<T>>> consumer) {
            if (space != SQLs.SPACE) {
                throw CriteriaUtils.errorSymbol(space);
            }
            consumer.accept(this::comma);
            return this.endColumnListClause(false);
        }

        @Override
        public final InsertStatement._StaticColumnUnaryClause<T> space(FieldMeta<T> field) {
            return this.comma(field);
        }

        @Override
        public final InsertStatement._StaticColumnDualClause<T> space(FieldMeta<T> field1, FieldMeta<T> field2) {
            return this.comma(field1)
                    .comma(field2);
        }

        @Override
        public final InsertStatement._StaticColumnCommaQuadraClause<T> space(FieldMeta<T> field1, FieldMeta<T> field2,
                                                                             FieldMeta<T> field3, FieldMeta<T> field4) {
            return this.comma(field1)
                    .comma(field2)
                    .comma(field3)
                    .comma(field4);
        }

        @Override
        public final InsertStatement._StaticColumnCommaQuadraClause<T> comma(final FieldMeta<T> field) {
            Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            List<FieldMeta<?>> fieldList = this.fieldList;
            if (!this.migration && !field.insertable()) {
                String m = String.format("%s is non-insertable , it can be specified only in migration mode.", field);
                throw ContextStack.criteriaError(this.context, m);
            } else if (fieldMap == null) {
                this.fieldMap = fieldMap = this.createFieldMap();
                fieldList = this.fieldList;
            } else if (!(fieldMap instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            assert fieldList instanceof ArrayList;
            if (fieldMap.putIfAbsent(field, Boolean.TRUE) != null) {
                String m = String.format("%s duplication or is managed by army.", field);
                throw ContextStack.criteriaError(this.context, m);
            }
            fieldList.add(field);
            return this;
        }

        @Override
        public final InsertStatement._StaticColumnCommaQuadraClause<T> comma(FieldMeta<T> field1, FieldMeta<T> field2) {
            return this.comma(field1)
                    .comma(field2);
        }

        @Override
        public final InsertStatement._StaticColumnCommaQuadraClause<T> comma(FieldMeta<T> field1, FieldMeta<T> field2,
                                                                             FieldMeta<T> field3) {
            return this.comma(field1)
                    .comma(field2)
                    .comma(field3);
        }

        @Override
        public final InsertStatement._StaticColumnCommaQuadraClause<T> comma(FieldMeta<T> field1, FieldMeta<T> field2,
                                                                             FieldMeta<T> field3, FieldMeta<T> field4) {
            return this.comma(field1)
                    .comma(field2)
                    .comma(field3)
                    .comma(field4);
        }


        @Override
        public final TableMeta<?> table() {
            return this.insertTable;
        }


        @Override
        public final List<FieldMeta<?>> fieldList() {
            List<FieldMeta<?>> fieldList = this.fieldList;
            if (fieldList == null) {
                this.fieldList = fieldList = ArmyCriteria.fieldListOf(this.insertTable);
            } else if (fieldList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return fieldList;
        }


        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            Map<FieldMeta<?>, Boolean> map = this.fieldMap;
            if (map == null) {
                map = this.useDefaultFieldMap();
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

        @Override
        public final void validateField(final FieldMeta<?> field, final @Nullable ArmyExpression value) {
            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            final TableMeta<?> tableOfField = field.tableMeta();
            if (fieldMap != null) {
                if (!fieldMap.containsKey(field)) {
                    throw notContainField(this.context, field);
                }
            } else if (tableOfField != this.insertTable) {
                //don't contain parent field
                throw ContextStack.criteriaError(this.context, _Exceptions::unknownColumn, field);
            }

            if (this.migration) {
                if (field instanceof PrimaryFieldMeta && tableOfField instanceof ChildTableMeta) {
                    throw childIdIsManaged(this.context, (ChildTableMeta<?>) tableOfField);
                }
            } else if (!field.insertable()) {
                throw ContextStack.criteriaError(this.context, _Exceptions::nonInsertableField, field);
            } else if (isArmyManageField(this.insertTable, field)) {
                throw ContextStack.criteriaError(this.context, _Exceptions::armyManageField, field);
            } else if (field instanceof PrimaryFieldMeta && tableOfField instanceof ChildTableMeta) {
                if (this.twoStmtMode || tableOfField.nonChildId().generatorType() != GeneratorType.POST) {
                    throw childIdIsManaged(this.context, (ChildTableMeta<?>) tableOfField);
                }
            }

            if (value != null && !field.nullable() && value.isNullValue()) {
                throw ContextStack.criteriaError(this.context, _Exceptions::nonNullField, field);
            }

        }


        /**
         * For RETURNING clause
         */
        final List<? extends TableField> effictiveFieldList() {
            return this.fieldList();
        }


        private Map<FieldMeta<?>, Boolean> createFieldMap() {
            final TableMeta<?> insertTable = this.insertTable;
            final Map<FieldMeta<?>, Boolean> fieldMap = _Collections.hashMap();
            final List<FieldMeta<?>> fieldList = _Collections.arrayList();

            FieldMeta<?> field;

            if (!this.migration && insertTable instanceof SingleTableMeta) {
                for (String reservedField : _MetaBridge.RESERVED_FIELDS) {
                    field = insertTable.tryGetField(reservedField);
                    if (field != null && fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                        fieldList.add(field);
                    }
                }//for

                if (insertTable instanceof ParentTableMeta) {
                    field = insertTable.discriminator();
                    if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                        fieldList.add(field);
                    }

                }

            }


            if (!this.migration && insertTable instanceof ChildTableMeta) {
                //child id always be managed by army
                field = insertTable.id();
                if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                    fieldList.add(field);
                }
            }


            if (!this.migration) {
                for (FieldMeta<?> f : insertTable.fieldChain()) {
                    if (fieldMap.putIfAbsent(f, Boolean.TRUE) == null) {
                        fieldList.add(f);
                    }
                }
            }

            assert fieldMap.size() == fieldList.size();
            this.fieldList = fieldList;
            return fieldMap;
        }

        @SuppressWarnings("unchecked")
        private R endColumnListClause(final boolean required) {
            final List<FieldMeta<?>> fieldList = this.fieldList;
            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;

            if (fieldList == null && required) {
                throw CriteriaUtils.dontAddAnyItem();
            }

            if (fieldList != null && !(fieldList instanceof ArrayList && fieldMap instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.fieldList = _Collections.safeUnmodifiableList(fieldList);
            this.fieldMap = _Collections.safeUnmodifiableMap(fieldMap);
            return (R) this;
        }


        private Map<FieldMeta<?>, Boolean> useDefaultFieldMap() {
            assert this.fieldMap == null;

            List<FieldMeta<?>> fieldList = this.fieldList;
            if (fieldList == null) {
                this.fieldList = fieldList = ArmyCriteria.fieldListOf(this.insertTable);
            } else if (fieldList != ArmyCriteria.fieldListOf(this.insertTable)) {
                throw ContextStack.castCriteriaApi(this.context);
            }

            final int fieldSize = fieldList.size();

            Map<FieldMeta<?>, Boolean> map = _Collections.hashMap((int) (fieldSize / 0.75f));
            for (FieldMeta<?> field : fieldList) {
                map.put(field, Boolean.TRUE);
            }
            assert map.size() == fieldSize;
            this.fieldMap = map = _Collections.unmodifiableMap(map);
            return map;
        }


    }//ColumnsClause


    @SuppressWarnings("unchecked")
    private static abstract class ColumnDefaultClause<T, PR, DR extends InsertStatement._ColumnDefaultClause<T>>
            extends ColumnsClause<T, PR>
            implements InsertStatement._FullColumnDefaultClause<T, DR>,
            _Insert._ValuesSyntaxInsert {

        final LiteralMode literalMode;

        final NullMode nullHandleMode;

        final boolean ignoreReturnIds;

        private Map<FieldMeta<?>, _Expression> commonExpMap;

        private ColumnDefaultClause(InsertOptions options, TableMeta<T> table, boolean twoStmtMode) {
            super(options.getContext(), options.isMigration(), table, twoStmtMode);
            if (options instanceof ValueSyntaxOptions) {
                this.nullHandleMode = ((ValueSyntaxOptions) options).nullHandle();
                this.ignoreReturnIds = ((ValueSyntaxOptions) options).isIgnoreReturnIds();
            } else {
                this.ignoreReturnIds = true;
                this.nullHandleMode = NullMode.INSERT_DEFAULT;
            }
            this.literalMode = options.literalMode();
        }


        @Override
        public final DR defaults(Consumer<InsertStatement._ColumnDefaultClause<T>> consumer) {
            Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
            final int oldSize;
            if (commonExpMap == null) {
                oldSize = 0;
            } else {
                oldSize = commonExpMap.size();
            }

            consumer.accept(this);

            commonExpMap = this.commonExpMap;
            if (commonExpMap == null || commonExpMap.size() == oldSize) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            return (DR) this;
        }

        @Override
        public final DR ifDefaults(Consumer<InsertStatement._ColumnDefaultClause<T>> consumer) {
            consumer.accept(this);
            return (DR) this;
        }

        @Override
        public final DR defaultValue(final FieldMeta<T> field, final @Nullable Expression value) {
            if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            }
            final ArmyExpression valueExp;
            valueExp = (ArmyExpression) value;
            this.validateField(field, valueExp);

            Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
            if (commonExpMap == null) {
                this.commonExpMap = commonExpMap = _Collections.hashMap();
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
        public final DR defaultValue(FieldMeta<T> field, Supplier<Expression> supplier) {
            return this.defaultValue(field, supplier.get());
        }

        @Override
        public final DR defaultValue(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function) {
            return this.defaultValue(field, function.apply(field));
        }

        @Override
        public final <E> DR defaultValue(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> operator,
                                         @Nullable E value) {
            return this.defaultValue(field, operator.apply(field, value));
        }

        @Override
        public final <E> DR defaultValue(FieldMeta<T> field, SQLs.SymbolSpace space,
                                         BiFunction<FieldMeta<T>, E, Expression> operator, Supplier<E> supplier) {
            return this.defaultValue(field, operator.apply(field, supplier.get()));
        }

        @Override
        public final <K, V> DR defaultValue(FieldMeta<T> field, BiFunction<FieldMeta<T>, V, Expression> operator,
                                            Function<K, V> function, K key) {
            return this.defaultValue(field, operator.apply(field, function.apply(key)));
        }

        @Override
        public final DR ifDefault(FieldMeta<T> field, Supplier<Expression> supplier) {
            final Expression expression;
            if ((expression = supplier.get()) != null) {
                this.defaultValue(field, expression);
            }
            return (DR) this;
        }

        @Override
        public final DR ifDefault(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function) {
            final Expression expression;
            if ((expression = function.apply(field)) != null) {
                this.defaultValue(field, expression);
            }
            return (DR) this;
        }

        @Override
        public final <E> DR ifDefault(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> operator,
                                      Supplier<E> supplier) {
            final E value;
            if ((value = supplier.get()) != null) {
                this.defaultValue(field, operator.apply(field, value));
            }
            return (DR) this;
        }

        @Override
        public final <K, V> DR ifDefault(FieldMeta<T> field, BiFunction<FieldMeta<T>, V, Expression> operator,
                                         Function<K, V> function, K key) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.defaultValue(field, operator.apply(field, value));
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
        public final NullMode nullHandle() {
            return this.nullHandleMode;
        }

        @Override
        public final LiteralMode literalMode() {
            return this.literalMode;
        }

        @Override
        public final boolean isIgnoreReturnIds() {
            return this.ignoreReturnIds;
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

            this.context.insertColumnList(this.fieldList());
        }


    }//CommonExpClause


    @SuppressWarnings("unchecked")
    static abstract class ComplexInsertValuesClause<T, CR, DR extends InsertStatement._ColumnDefaultClause<T>, VR>
            extends ColumnDefaultClause<T, CR, DR>
            implements InsertStatement._DomainValueClause<T, VR>,
            InsertStatement._DynamicValuesClause<T, VR>,
            _Insert._ValuesInsert,
            _Insert._QueryInsert {


        private InsertMode insertMode;

        private List<?> domainList;

        private List<Map<FieldMeta<?>, _Expression>> rowPairList;

        private SubQuery subQuery;

        ComplexInsertValuesClause(InsertOptions options, TableMeta<T> table, boolean twoStmtMode) {
            super(options, table, twoStmtMode);
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
            assert this.insertMode == null;
            this.insertMode = InsertMode.DOMAIN;
            return (VR) this;
        }

        @Override
        public final <TS extends T> VR value(Supplier<TS> supplier) {
            return this.value(supplier.get());
        }


        /**
         * @see #domainListForSingle()
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
            assert this.insertMode == null;
            this.insertMode = InsertMode.DOMAIN;
            return (VR) this;
        }

        @Override
        public final <TS extends T> VR values(Supplier<List<TS>> supplier) {
            return this.values(supplier.get());
        }


        @Override
        public VR values(Consumer<ValuesConstructor<T>> consumer) {
            if (this.insertMode != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();

            final ValuesConstructorImpl<T> clause;
            clause = new ValuesConstructorImpl<>(this.context, this.migration, this::validateField);

            consumer.accept(clause);

            this.rowPairList = clause.endValuesClause();
            assert this.insertMode == null;
            this.insertMode = InsertMode.VALUES;
            return (VR) this;
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            final List<Map<FieldMeta<?>, _Expression>> list = this.rowPairList;
            if (this.insertMode != InsertMode.VALUES) {
                throw insertModeNotMatch();
            } else if (list == null || list instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        public final SubQuery subQuery() {
            final SubQuery query = this.subQuery;
            if (this.insertMode != InsertMode.QUERY) {
                throw insertModeNotMatch();
            } else if (query == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return query;
        }


        @Override
        public final int insertRowCount() {
            //no bug,never here
            throw new UnsupportedOperationException();
        }

        /**
         * @param rowPairList a unmodified list,empty is allowed.
         */
        final VR staticValuesClauseEnd(final List<Map<FieldMeta<?>, _Expression>> rowPairList) {
            if (this.insertMode != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endColumnDefaultClause();

            this.rowPairList = rowPairList;
            assert this.insertMode == null;
            this.insertMode = InsertMode.VALUES;
            return (VR) this;
        }

        final VR spaceQueryEnd(final SubQuery subQuery) {
            final CriteriaContext subContext;
            if (this.insertMode != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (!this.migration) {
                throw this.queryInsetSupportOnlyMigration();
            } else if (((_RowSet) subQuery).selectionSize() != this.fieldList().size()) {
                throw columnCountAndSelectionCountNotMatch(subQuery);
            } else if ((subContext = ((CriteriaContextSpec) subQuery).getContext()).getOuterContext() != this.context) {
                throw ContextStack.criteriaError(this.context, "sub query context and current context not match");
            }

            this.context.validateDialect(subContext);

            this.endColumnDefaultClause();

            this.subQuery = subQuery;
            assert this.insertMode == null;
            this.insertMode = InsertMode.QUERY;
            return (VR) this;
        }


        final InsertMode getInsertMode() {
            final InsertMode mode = this.insertMode;
            if (mode == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return mode;
        }

        /**
         * @return a unmodified list,new instance each time.
         */
        final List<?> domainListForSingle() {
            //  assert this.insertTable instanceof SingleTableMeta;
            final List<?> domainList = this.domainList;
            if (this.insertMode != InsertMode.DOMAIN) {
                throw insertModeNotMatch();
            } else if (domainList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return _Collections.asUnmodifiableList(domainList);
        }

        /**
         * @return a original list
         */
        final List<?> originalDomainList() {
            final List<?> domainList = this.domainList;
            if (this.insertMode != InsertMode.DOMAIN) {
                throw insertModeNotMatch();
            } else if (domainList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return domainList;
        }


        final void domainListForChild(final List<?> originalList) {
            assert this.insertTable instanceof ChildTableMeta;

            final List<?> domainList = this.domainList;
            if (this.insertMode != InsertMode.DOMAIN) {
                throw insertModeNotMatch();
            } else if (domainList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (domainList != originalList
                    && !(domainList.size() == originalList.size()
                    && domainList.size() == 1
                    && domainList.get(0) == originalList.get(0))) {
                String m = String.format("%s and %s domain list not match.", this.insertTable
                        , ((ChildTableMeta<T>) this.insertTable).parentMeta());
                throw ContextStack.criteriaError(this.context, m);
            }
        }


        final CriteriaException queryInsetSupportOnlyMigration() {
            return ContextStack.criteriaError(this.context, "query insert support only migration mode.");
        }

        final CriteriaException insertModeNotMatch() {
            final CriteriaException e;
            if (this.insertTable instanceof ChildTableMeta) {
                String m = String.format("%s insert mode[%s] and %s not match.",
                        this.insertTable, this.insertMode, ((ChildTableMeta<T>) this.insertTable).parentMeta());
                e = ContextStack.criteriaError(this.context, m);
            } else {
                e = ContextStack.castCriteriaApi(this.context);
            }
            return e;
        }

        private CriteriaException columnCountAndSelectionCountNotMatch(final SubQuery subQuery) {
            String m = String.format("SubQuery %s size[%s] and field list size[%s] of %s not match.",
                    Selection.class.getSimpleName(),
                    ((_RowSet) subQuery).selectionSize(),
                    this.fieldList().size(),
                    this.insertTable);
            return ContextStack.criteriaError(this.context, m);
        }


    }//ComplexInsertValuesClause


    @SuppressWarnings("unchecked")
    private static abstract class DynamicAssignmentSetClause<T, R>
            implements InsertStatement._StaticAssignmentSetClause<T, R> {

        final CriteriaContext context;

        private final BiConsumer<FieldMeta<T>, Expression> consumer;

        private DynamicAssignmentSetClause(CriteriaContext context,
                                           BiConsumer<FieldMeta<T>, Expression> consumer) {
            this.context = context;
            this.consumer = consumer;
        }

        private DynamicAssignmentSetClause(CriteriaContext context) {
            this.context = context;
            this.consumer = this::onAddPair;
        }

        @Override
        public final R set(final FieldMeta<T> field, final @Nullable Expression value) {
            this.consumer.accept(field, value);
            return (R) this;
        }

        @Override
        public final R set(FieldMeta<T> field, Supplier<Expression> supplier) {
            this.consumer.accept(field, supplier.get());
            return (R) this;
        }

        @Override
        public final R set(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function) {
            this.consumer.accept(field, function.apply(field));
            return (R) this;
        }

        @Override
        public final <E> R set(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator,
                               @Nullable E value) {
            this.consumer.accept(field, valueOperator.apply(field, value));
            return (R) this;
        }

        @Override
        public final <E> R set(FieldMeta<T> field, SQLs.SymbolEqual equal,
                               BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier) {
            this.consumer.accept(field, valueOperator.apply(field, supplier.get()));
            return (R) this;
        }

        @Override
        public final <K, V> R set(FieldMeta<T> field, BiFunction<FieldMeta<T>, V, Expression> valueOperator,
                                  Function<K, V> function, K key) {
            this.consumer.accept(field, valueOperator.apply(field, function.apply(key)));
            return (R) this;
        }

        @Override
        public final R ifSet(FieldMeta<T> field, Supplier<Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression != null) {
                this.consumer.accept(field, expression);
            }
            return (R) this;
        }

        @Override
        public final R ifSet(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function) {
            final Expression expression;
            expression = function.apply(field);
            if (expression != null) {
                this.consumer.accept(field, expression);
            }
            return (R) this;
        }


        @Override
        public final <E> R ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator
                , Supplier<E> supplier) {
            final E value;
            value = supplier.get();
            if (value != null) {
                this.consumer.accept(field, valueOperator.apply(field, value));
            }
            return (R) this;
        }

        @Override
        public final <K, V> R ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, V, Expression> valueOperator
                , Function<K, V> function, K key) {
            final V value;
            value = function.apply(key);
            if (value != null) {
                this.consumer.accept(field, valueOperator.apply(field, value));
            }
            return (R) this;
        }

        void onAddPair(FieldMeta<T> field, @Nullable Expression value) {
            throw new UnsupportedOperationException();
        }


    }//DynamicAssignmentSetClause

    private static final class AssignmentsImpl<T> extends DynamicAssignmentSetClause<T, Assignments<T>>
            implements Assignments<T> {

        /**
         * @see ValuesParensClauseImpl#parens(SQLs.SymbolSpace, Consumer)
         * @see ComplexInsertValuesAssignmentClause#ifSets(Consumer)
         */
        private AssignmentsImpl(CriteriaContext context, BiConsumer<FieldMeta<T>, Expression> consumer) {
            super(context, consumer);
        }

    }//AssignmentsImpl


    @SuppressWarnings("unchecked")
    static abstract class ComplexInsertValuesAssignmentClause<T, CR, DR extends InsertStatement._ColumnDefaultClause<T>, VR, SR>
            extends ComplexInsertValuesClause<T, CR, DR, VR>
            implements InsertStatement._StaticAssignmentSetClause<T, SR>
            , InsertStatement._DynamicAssignmentSetClause<T, VR>
            , _Insert._AssignmentInsert {

        private List<_Pair<FieldMeta<?>, _Expression>> assignmentPairList;

        private Map<FieldMeta<?>, _Expression> assignmentMap;

        ComplexInsertValuesAssignmentClause(InsertOptions options, TableMeta<T> table, boolean twoStmtMode) {
            super(options, table, twoStmtMode);
        }

        @Override
        public final SR set(final FieldMeta<T> field, final @Nullable Expression value) {

            List<_Pair<FieldMeta<?>, _Expression>> pairList = this.assignmentPairList;
            Map<FieldMeta<?>, _Expression> assignmentMap = this.assignmentMap;
            if (pairList == null) {
                if (((ComplexInsertValuesClause<?, ?, ?, ?>) this).insertMode != null) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
                assert ((ComplexInsertValuesClause<?, ?, ?, ?>) this).insertMode == null;
                pairList = _Collections.arrayList();
                this.assignmentPairList = pairList;
                assignmentMap = _Collections.hashMap();
                this.assignmentMap = assignmentMap;
            } else if (!(pairList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (value == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            } else {
                this.validateField(field, (ArmyExpression) value);
            }
            assert assignmentMap != null;
            if (assignmentMap.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.context, field);
            }
            pairList.add(_Pair.create(field, (ArmyExpression) value));
            return (SR) this;
        }


        @Override
        public final SR set(FieldMeta<T> field, Supplier<Expression> supplier) {
            return this.set(field, supplier.get());
        }

        @Override
        public final SR set(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function) {
            return this.set(field, function.apply(field));
        }

        @Override
        public final <E> SR set(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, @Nullable E value) {
            return this.set(field, valueOperator.apply(field, value));
        }

        @Override
        public final <E> SR set(FieldMeta<T> field, SQLs.SymbolEqual equal,
                                BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier) {
            return this.set(field, valueOperator.apply(field, supplier.get()));
        }

        @Override
        public final <K, V> SR set(FieldMeta<T> field, BiFunction<FieldMeta<T>, V, Expression> valueOperator,
                                   Function<K, V> function, K key) {
            return this.set(field, valueOperator.apply(field, function.apply(key)));
        }

        @Override
        public final SR ifSet(FieldMeta<T> field, Supplier<Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression != null) {
                this.set(field, expression);
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function) {
            final Expression expression;
            expression = function.apply(field);
            if (expression != null) {
                this.set(field, expression);
            }
            return (SR) this;
        }


        @Override
        public final <E> SR ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator
                , Supplier<E> supplier) {
            final E value;
            value = supplier.get();
            if (value != null) {
                this.set(field, valueOperator.apply(field, value));
            }
            return (SR) this;
        }

        @Override
        public final <K, V> SR ifSet(FieldMeta<T> field, BiFunction<FieldMeta<T>, V, Expression> valueOperator,
                                     Function<K, V> function, K key) {
            final V value;
            value = function.apply(key);
            if (value != null) {
                this.set(field, valueOperator.apply(field, value));
            }
            return (SR) this;
        }

        @Override
        public final VR sets(Consumer<Assignments<T>> consumer) {
            this.ifSets(consumer);
            final List<_Pair<FieldMeta<?>, _Expression>> list = this.assignmentPairList;
            if (list == null || list.size() == 0) {
                throw ContextStack.criteriaError(this.context, "You don't assignment any value.");
            }
            return (VR) this;
        }

        @Override
        public final VR ifSets(Consumer<Assignments<T>> consumer) {
            if (this.assignmentPairList != null || ((ComplexInsertValuesClause<?, ?, ?, ?>) this).insertMode != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            consumer.accept(new AssignmentsImpl<>(this.context, this::set));
            this.endStaticAssignmentClauseIfNeed();
            return (VR) this;
        }

        @Override
        public final List<_Pair<FieldMeta<?>, _Expression>> assignmentPairList() {
            this.assertAssignmentMode();
            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.assignmentPairList;
            if (pairList == null || pairList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return pairList;
        }

        @Override
        public final Map<FieldMeta<?>, _Expression> assignmentMap() {
            this.assertAssignmentMode();
            final Map<FieldMeta<?>, _Expression> fieldMap = this.assignmentMap;
            if (fieldMap == null || fieldMap instanceof HashMap) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return fieldMap;
        }

        final void endStaticAssignmentClauseIfNeed() {

            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.assignmentPairList;
            final Map<FieldMeta<?>, _Expression> fieldMap = this.assignmentMap;
            if (pairList == null) {
                this.assignmentPairList = Collections.emptyList();
                this.assignmentMap = Collections.emptyMap();
                if (((ComplexInsertValuesClause<?, ?, ?, ?>) this).insertMode == null) {
                    ((ComplexInsertValuesClause<?, ?, ?, ?>) this).insertMode = InsertMode.ASSIGNMENT;
                }
            } else if (pairList instanceof ArrayList) {
                this.assignmentPairList = _Collections.unmodifiableList(pairList);
                this.assignmentMap = Collections.unmodifiableMap(fieldMap);
                assert ((ComplexInsertValuesClause<?, ?, ?, ?>) this).insertMode == null;
                ((ComplexInsertValuesClause<?, ?, ?, ?>) this).insertMode = InsertMode.ASSIGNMENT;
            }

        }

        private void assertAssignmentMode() {
            if (((ComplexInsertValuesClause<?, ?, ?, ?>) this).insertMode != InsertMode.ASSIGNMENT) {
                throw insertModeNotMatch();
            }
        }

    }//ComplexInsertValuesAssignmentClause


    static abstract class ValuesParensClauseImpl<T, R extends Item>
            implements InsertStatement._StaticValueSpaceClause<T>,
            InsertStatement._StaticColumnValueClause<T>,
            InsertStatement._ValuesParensClause<T, R>,
            CriteriaContextSpec {

        final CriteriaContext context;

        final boolean migration;

        final BiConsumer<FieldMeta<?>, ArmyExpression> validator;

        private List<Map<FieldMeta<?>, _Expression>> rowList;

        private Map<FieldMeta<?>, _Expression> rowValuesMap;

        private Assignments<T> assignments;

        ValuesParensClauseImpl(CriteriaContext context, boolean migration,
                               BiConsumer<FieldMeta<?>, ArmyExpression> validator) {
            this.context = context;
            this.migration = migration;
            this.validator = validator;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }


        @Override
        public final R parens(Consumer<InsertStatement._StaticValueSpaceClause<T>> consumer) {
            consumer.accept(this);
            return this.endParensClause();
        }

        @Override
        public final R parens(SQLs.SymbolSpace space, Consumer<Assignments<T>> consumer) {
            if (space != SQLs.SPACE) {
                throw CriteriaUtils.errorSymbol(space);
            }
            Assignments<T> assignments = this.assignments;
            if (assignments == null) {
                this.assignments = assignments = new AssignmentsImpl<>(this.context, this::comma);
            }
            consumer.accept(assignments);
            return this.endParensClause();
        }

        @Override
        public final InsertStatement._StaticColumnValueClause<T> space(FieldMeta<T> field, Expression value) {
            return this.comma(field, value);
        }

        @Override
        public final InsertStatement._StaticColumnValueClause<T> space(FieldMeta<T> field,
                                                                       Supplier<Expression> supplier) {
            return this.comma(field, supplier.get());
        }

        @Override
        public final InsertStatement._StaticColumnValueClause<T> space(FieldMeta<T> field, Function<FieldMeta<T>,
                Expression> function) {
            return this.comma(field, function.apply(field));
        }

        @Override
        public final <E> InsertStatement._StaticColumnValueClause<T> space(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> funcRef, @Nullable E value) {
            return this.comma(field, funcRef.apply(field, value));
        }

        @Override
        public final <E> InsertStatement._StaticColumnValueClause<T> space(FieldMeta<T> field, SQLs.SymbolSpace space,
                                                                           BiFunction<FieldMeta<T>, E, Expression> funcRef,
                                                                           Supplier<E> supplier) {
            return this.comma(field, funcRef.apply(field, supplier.get()));
        }

        @Override
        public final <K, V> InsertStatement._StaticColumnValueClause<T> space(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, V, Expression> funcRef, Function<K, V> function, K key) {
            return this.comma(field, funcRef.apply(field, function.apply(key)));
        }


        @Override
        public final InsertStatement._StaticColumnValueClause<T> spaceIf(
                FieldMeta<T> field, Supplier<Expression> supplier) {
            return this.ifComma(field, supplier);
        }

        @Override
        public final InsertStatement._StaticColumnValueClause<T> spaceIf(
                FieldMeta<T> field, Function<FieldMeta<T>, Expression> function) {
            return this.ifComma(field, function);
        }

        @Override
        public final <E> InsertStatement._StaticColumnValueClause<T> spaceIf(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> funcRef, Supplier<E> supplier) {
            return this.ifComma(field, funcRef, supplier);
        }

        @Override
        public final <K, V> InsertStatement._StaticColumnValueClause<T> spaceIf(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, V, Expression> funcRef, Function<K, V> function, K key) {
            return this.ifComma(field, funcRef, function, key);
        }

        @Override
        public final InsertStatement._StaticColumnValueClause<T> comma(final FieldMeta<T> field,
                                                                       final @Nullable Expression value) {
            if (value instanceof SQLField) {
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
        public final InsertStatement._StaticColumnValueClause<T> comma(FieldMeta<T> field, Supplier<Expression> supplier) {
            return this.comma(field, supplier.get());
        }

        @Override
        public final InsertStatement._StaticColumnValueClause<T> comma(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function) {
            return this.comma(field, function.apply(field));
        }

        @Override
        public final <E> InsertStatement._StaticColumnValueClause<T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> funcRef, @Nullable E value) {
            return this.comma(field, funcRef.apply(field, value));
        }

        @Override
        public final <E> InsertStatement._StaticColumnValueClause<T> comma(FieldMeta<T> field, SQLs.SymbolSpace space,
                                                                           BiFunction<FieldMeta<T>, E, Expression> funcRef,
                                                                           Supplier<E> supplier) {
            return this.comma(field, funcRef.apply(field, supplier.get()));
        }

        @Override
        public final <K, V> InsertStatement._StaticColumnValueClause<T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, V, Expression> funcRef, Function<K, V> function, K key) {
            return this.comma(field, funcRef.apply(field, function.apply(key)));
        }

        @Override
        public final InsertStatement._StaticColumnValueClause<T> ifComma(
                FieldMeta<T> field, Supplier<Expression> supplier) {
            final Expression expression;
            if ((expression = supplier.get()) != null) {
                this.comma(field, expression);
            }
            return this;
        }

        @Override
        public final InsertStatement._StaticColumnValueClause<T> ifComma(
                FieldMeta<T> field, Function<FieldMeta<T>, Expression> function) {
            final Expression expression;
            if ((expression = function.apply(field)) != null) {
                this.comma(field, expression);
            }
            return this;
        }

        @Override
        public final <E> InsertStatement._StaticColumnValueClause<T> ifComma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> funcRef, Supplier<E> supplier) {
            final E value;
            if ((value = supplier.get()) != null) {
                this.comma(field, funcRef.apply(field, value));
            }
            return this;
        }

        @Override
        public final <K, V> InsertStatement._StaticColumnValueClause<T> ifComma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, V, Expression> funcRef, Function<K, V> function, K key) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.comma(field, funcRef.apply(field, value));
            }
            return this;
        }


        final List<Map<FieldMeta<?>, _Expression>> endValuesClause() {
            if (this.rowValuesMap != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            List<Map<FieldMeta<?>, _Expression>> rowValueList = this.rowList;
            if (!(rowValueList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.rowList = rowValueList = _Collections.unmodifiableList(rowValueList);
            this.assignments = null; // clear
            return rowValueList;
        }


        private Map<FieldMeta<?>, _Expression> newMap() {
            final List<Map<FieldMeta<?>, _Expression>> rowList = this.rowList;
            final Map<FieldMeta<?>, _Expression> map;
            if (rowList == null) {
                map = _Collections.hashMap();
            } else {
                map = _Collections.hashMap((int) (rowList.get(0).size() / 0.75F));
            }
            return map;
        }

        @SuppressWarnings("unchecked")
        private R endParensClause() {
            List<Map<FieldMeta<?>, _Expression>> rowValueList = this.rowList;
            if (rowValueList == null) {
                rowValueList = _Collections.arrayList();
                this.rowList = rowValueList;
            } else if (!(rowValueList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final Map<FieldMeta<?>, _Expression> currentRow = this.rowValuesMap;
            if (currentRow != null) {
                rowValueList.add(_Collections.unmodifiableMap(currentRow));
            } else {
                rowValueList.add(_Collections.emptyMap());
            }

            this.rowValuesMap = null;// clear for next row
            return (R) this;
        }


    }//ValuesParensClauseImpl


    private static final class ValuesConstructorImpl<T>
            extends ValuesParensClauseImpl<T, Statement._CommaClause<ValuesConstructor<T>>>
            implements Statement._CommaClause<ValuesConstructor<T>>,
            ValuesConstructor<T> {

        /**
         * @see ComplexInsertValuesClause#values(Consumer)
         */
        private ValuesConstructorImpl(CriteriaContext context, boolean migration,
                                      BiConsumer<FieldMeta<?>, ArmyExpression> validator) {
            super(context, migration, validator);
        }

        @Override
        public ValuesConstructor<T> comma() {
            return this;
        }

    }//ValuesConstructorImpl


    static abstract class AssignmentSetClause<T, SR> extends DynamicAssignmentSetClause<T, SR>
            implements ColumnListClause, _Insert._AssignmentStatementSpec {

        final TableMeta<T> insertTable;

        private Map<FieldMeta<?>, _Expression> fieldPairMap;
        private List<_Pair<FieldMeta<?>, _Expression>> itemPairList;

        AssignmentSetClause(CriteriaContext context, TableMeta<T> insertTable) {
            super(context);
            this.insertTable = insertTable;
        }

        @Override
        public final void validateField(FieldMeta<?> field, @Nullable ArmyExpression value) {
            if (field.tableMeta() != this.insertTable) {
                throw ContextStack.criteriaError(this.context, _Exceptions::unknownColumn, field);
            } else if (!field.nullable() && (value == null || value.isNullValue())) {
                throw ContextStack.criteriaError(this.context, _Exceptions::nonNullField, field);
            }
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final List<_Pair<FieldMeta<?>, _Expression>> assignmentPairList() {
            final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.itemPairList;
            if (pairList == null || pairList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return pairList;
        }

        @Override
        public final Map<FieldMeta<?>, _Expression> assignmentMap() {
            final Map<FieldMeta<?>, _Expression> fieldMap = this.fieldPairMap;
            if (fieldMap == null || fieldMap instanceof HashMap) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return fieldMap;
        }


        final void onAddPair(final FieldMeta<T> field, final @Nullable Expression value) {
            if (value == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            }
            this.validateField(field, (ArmyExpression) value);
            Map<FieldMeta<?>, _Expression> fieldPairMap = this.fieldPairMap;
            List<_Pair<FieldMeta<?>, _Expression>> itemPairList = this.itemPairList;
            if (fieldPairMap == null) {
                this.fieldPairMap = fieldPairMap = _Collections.hashMap();
                this.itemPairList = itemPairList = _Collections.arrayList();
            } else if (!(fieldPairMap instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (fieldPairMap.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.context, field);
            }
            assert itemPairList != null;
            itemPairList.add(_Pair.create(field, (ArmyExpression) value));
        }


        final void endAssignmentSetClause() {
            List<_Pair<FieldMeta<?>, _Expression>> itemPairList = this.itemPairList;
            Map<FieldMeta<?>, _Expression> fieldMap = this.fieldPairMap;

            if (itemPairList == null) {
                itemPairList = Collections.emptyList();
            } else if (!(itemPairList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (itemPairList.size() == 1) {
                itemPairList = Collections.singletonList(itemPairList.get(0));
            } else {
                itemPairList = Collections.unmodifiableList(itemPairList);
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


    }//AssignmentSetClause


    private static abstract class ArmyInsertStatement<I extends Statement, Q extends Statement>
            extends CriteriaSupports.StatementMockSupport
            implements _Insert,
            Statement.StatementMockSpec,
            Statement,
            CriteriaContextSpec,
            Statement._DmlInsertClause<I>,
            Statement._DqlInsertClause<Q> {

        final TableMeta<?> insertTable;


        final String tableAlias;

        private Boolean prepared;

        ArmyInsertStatement(_Insert clause) {
            super(((CriteriaContextSpec) clause).getContext());
            this.insertTable = clause.table();
            this.tableAlias = clause.tableAlias();
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }


        @Override
        public final TableMeta<?> table() {
            return this.insertTable;
        }

        @Override
        public final String tableAlias() {
            return this.tableAlias;
        }


        @Override
        public final int insertRowCount() {
            final int rowCount;
            if (this instanceof _Insert._DomainInsert) {
                rowCount = ((_DomainInsert) this).domainList().size();
            } else if (this instanceof _Insert._ValuesInsert) {
                rowCount = ((_ValuesInsert) this).rowPairList().size();
            } else if (this instanceof _Insert._QueryInsert) {
                rowCount = -1;
            } else if (this instanceof _Insert._AssignmentInsert) {
                rowCount = 1;
            } else {
                // no bug,never here
                throw new IllegalStateException();
            }
            assert rowCount != 0;
            return rowCount;
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
            if (this instanceof _ReturningDml) {
                throw new UnsupportedOperationException();
            }
            this.asInsertStatement();
            return (I) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final Q asReturningInsert() {
            if (!(this instanceof _ReturningDml)) {
                throw new UnsupportedOperationException();
            }
            this.asInsertStatement();
            return (Q) this;
        }


        private void asInsertStatement() {
            _Assert.nonPrepared(this.prepared);
            insertStatementGuard(this);

            //finally clear context
            final CriteriaContext context = this.context;
            context.endContext();
            ContextStack.pop(context);
            this.prepared = Boolean.TRUE;
        }

    }//AbstractInsertStatement


    static abstract class ArmyValueSyntaxStatement<I extends Statement, Q extends Statement>
            extends ArmyInsertStatement<I, Q>
            implements _Insert._ValuesSyntaxInsert {

        private final boolean migration;

        private final NullMode nullHandleMode;

        private final LiteralMode literalMode;

        private final boolean ignoreReturnIds;

        private final List<FieldMeta<?>> fieldList;

        private final Map<FieldMeta<?>, Boolean> fieldMap;

        private final Map<FieldMeta<?>, _Expression> defaultExpMap;

        ArmyValueSyntaxStatement(_ValuesSyntaxInsert clause) {
            super(clause);

            this.migration = clause.isMigration();
            this.nullHandleMode = clause.nullHandle();
            this.literalMode = clause.literalMode();
            this.ignoreReturnIds = clause.isIgnoreReturnIds();

            this.fieldList = clause.fieldList();
            this.fieldMap = clause.fieldMap();
            this.defaultExpMap = clause.defaultValueMap();
        }


        @Override
        public final boolean isMigration() {
            return this.migration;
        }

        @Override
        public final NullMode nullHandle() {
            return this.nullHandleMode;
        }

        @Override
        public final LiteralMode literalMode() {
            return this.literalMode;
        }


        @Override
        public final boolean isIgnoreReturnIds() {
            return this.ignoreReturnIds;
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


    static abstract class ValueSyntaxInsertStatement<I extends Statement>
            extends ArmyValueSyntaxStatement<I, Statement>
            implements ValueSyntaxOptions {

        ValueSyntaxInsertStatement(_ValuesSyntaxInsert clause) {
            super(clause);

        }


    }//ValueInsertStatement


    private static abstract class AssignmentSyntaxInsertStatement<I extends Statement, Q extends Statement>
            extends ArmyInsertStatement<I, Q> implements _Insert._AssignmentInsert, ValueSyntaxOptions {

        private final boolean migration;

        private final LiteralMode preferLiteral;

        private final List<_Pair<FieldMeta<?>, _Expression>> assignmentPariList;

        private final Map<FieldMeta<?>, _Expression> fieldMap;

        AssignmentSyntaxInsertStatement(_AssignmentInsert clause) {
            super(clause);
            this.migration = clause.isMigration();
            this.preferLiteral = clause.literalMode();
            this.assignmentPariList = clause.assignmentPairList();

            this.fieldMap = clause.assignmentMap();
        }

        @Override
        public final boolean isMigration() {
            return this.migration;
        }

        @Override
        public final NullMode nullHandle() {
            //assignment don't support this
            return NullMode.INSERT_DEFAULT;
        }

        @Override
        public final boolean isIgnoreReturnIds() {
            //always true,assignment don't need this.
            return true;
        }

        @Override
        public final LiteralMode literalMode() {
            return this.preferLiteral;
        }

        @Override
        public final Map<FieldMeta<?>, _Expression> assignmentMap() {
            return this.fieldMap;
        }

        @Override
        public final List<_Pair<FieldMeta<?>, _Expression>> assignmentPairList() {
            return this.assignmentPariList;
        }

    }//AbstractAssignmentInsertStatement

    static abstract class AssignmentInsertStatement<I extends Statement>
            extends AssignmentSyntaxInsertStatement<I, Statement>
            implements InsertStatement {

        AssignmentInsertStatement(_AssignmentInsert clause) {
            super(clause);
        }

    }//AssignmentInsertStatement


    static abstract class ArmyQuerySyntaxInsertStatement<I extends Statement, Q extends Statement>
            extends ArmyInsertStatement<I, Q>
            implements _Insert._QueryInsert, ValueSyntaxOptions {

        private final List<FieldMeta<?>> fieldList;

        private final Map<FieldMeta<?>, Boolean> fieldMap;

        private final SubQuery query;

        ArmyQuerySyntaxInsertStatement(_QueryInsert clause) {
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
        public final NullMode nullHandle() {
            //always INSERT_DEFAULT,query insert don't support this
            return NullMode.INSERT_DEFAULT;
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

        @Override
        public final boolean isIgnoreReturnIds() {
            // always true,query insert don't need return id.
            return true;
        }


    }//AbstractQuerySyntaxInsertStatement


    static abstract class QuerySyntaxInsertStatement<I extends Statement>
            extends ArmyQuerySyntaxInsertStatement<I, Statement>
            implements InsertStatement {


        QuerySyntaxInsertStatement(_QueryInsert clause) {
            super(clause);

        }


    }//QueryInsertStatement


    static CriteriaException notContainField(CriteriaContext criteriaContext, FieldMeta<?> field) {
        String m = String.format("insert field list don't contain %s", field);
        return ContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException duplicationValuePair(CriteriaContext criteriaContext, FieldMeta<?> field) {
        String m = String.format("duplication value of %s at same row.", field);
        return ContextStack.criteriaError(criteriaContext, m);
    }

    private static CriteriaException childIdIsManaged(CriteriaContext criteriaContext, ChildTableMeta<?> table) {
        return ContextStack.criteriaError(criteriaContext, _Exceptions::childIdIsManagedByArmy, table);
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
     *
     * @see ArmyInsertStatement#asInsertStatement()
     */
    private static void insertStatementGuard(final _Insert statement) {
        if (!(statement instanceof _Insert._ChildInsert)) {
            if (statement instanceof _Insert._SupportWithClauseInsert) { // for example,postgre insert
                if (statement instanceof PrimaryStatement) {
                    validateSupportWithClauseInsert((_Insert._SupportWithClauseInsert) statement);
                }// sub statement ignore
            } else if (statement instanceof _Insert._DomainInsert && _DialectUtils.isCannotReturnId((_Insert._DomainInsert) statement)) {
                throw _Exceptions.cannotReturnPostId(statement);
            }
        } else if (_DialectUtils.isIllegalTwoStmtMode((_Insert._ChildInsert) statement)) {
            throw _Exceptions.illegalTwoStmtMode();
        } else if (_DialectUtils.isIllegalChildPostInsert((_Insert._ChildInsert) statement)) {
            throw _Exceptions.cannotReturnPostId(statement);
        } else if (statement instanceof _Insert._ChildDomainInsert) {
            validateChildDomainInsert((_Insert._ChildDomainInsert) statement);
        } else if (statement instanceof _Insert._ChildValuesInsert) {
            validateChildValueInsert((_Insert._ChildValuesInsert) statement);
        }

    }


    /**
     * @param statement {@link PrimaryStatement} and not {@link _Insert._ChildInsert}
     * @see #insertStatementGuard(_Insert)
     */
    private static void validateSupportWithClauseInsert(final _Insert._SupportWithClauseInsert statement) {

        //TODO
    }


    private static CodeEnum getDiscriminatorValue(final CriteriaContext context,
                                                  final Selection discriminatorSelection,
                                                  final ParentTableMeta<?> insertTable) {
        final FieldMeta<?> discriminatorField;
        discriminatorField = insertTable.discriminator();

        //2.2 validate discriminatorExp
        final Expression discriminatorExp;
        discriminatorExp = ((_Selection) discriminatorSelection).underlyingExp();

        if (!(discriminatorExp instanceof ArmyLiteralExpression
                && discriminatorExp instanceof SqlValueParam.SingleAnonymousValue
                && discriminatorExp.typeMeta().mappingType() instanceof CodeEnumType)) {
            String m = String.format("The appropriate %s[%s] of discriminator %s must be literal."
                    , Selection.class.getSimpleName(), discriminatorSelection.alias()
                    , discriminatorField);
            throw ContextStack.criteriaError(context, m);
        }


        final Object value;
        value = ((SqlValueParam.SingleAnonymousValue) discriminatorExp).value();
        final Class<?> discriminatorJavaType;
        discriminatorJavaType = discriminatorField.javaType();

        if (!discriminatorJavaType.isInstance(value)) {
            String m = String.format("The appropriate %s[%s] of discriminator %s must be instance of %s."
                    , Selection.class.getSimpleName(), discriminatorSelection.alias()
                    , discriminatorField, discriminatorJavaType.getName());
            throw ContextStack.criteriaError(context, m);
        }

        return (CodeEnum) value;
    }


    @Nullable
    private static Selection validateSelectionSize(final CriteriaContext context, final _Insert._QueryInsert stmt) {
        final TableMeta<?> insertTable;
        insertTable = stmt.table();

        final List<FieldMeta<?>> fieldList;
        fieldList = stmt.fieldList();
        final int fieldSize;
        fieldSize = fieldList.size();

        final List<? extends Selection> selectionList;
        selectionList = ((_DerivedTable) stmt.subQuery()).refAllSelection();

        if (selectionList.size() != fieldSize) {
            String m = String.format("SubQuery %s size[%s] and field list size[%s] of %s not match.",
                    Selection.class.getSimpleName(),
                    selectionList.size(),
                    fieldSize,
                    stmt.table());
            throw ContextStack.criteriaError(context, m);
        }

        final Selection discriminatorSelection;
        if (insertTable instanceof ParentTableMeta) {
            final FieldMeta<?> discriminatorField;
            discriminatorField = insertTable.discriminator();
            int index = -1;
            for (int i = 0; i < fieldSize; i++) {
                if (fieldList.get(i) == discriminatorField) {
                    index = i;
                    break;
                }
            }
            assert index > -1;
            discriminatorSelection = selectionList.get(index);
        } else {
            discriminatorSelection = null;
        }
        return discriminatorSelection;
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

    private static CriteriaException discriminatorNotMatch(CriteriaContext context, TableMeta<?> domainTable,
                                                           CodeEnum codeEnum) {
        String m = String.format("The appropriate %s of discriminator %s must be %s.%s .",
                Selection.class.getSimpleName(),
                domainTable,
                codeEnum.getClass().getName(),
                codeEnum.name());
        return ContextStack.criteriaError(context, m);
    }


}

