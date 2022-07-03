package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.Dialect;
import io.army.dialect._Dialect;
import io.army.dialect._MockDialects;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
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


    interface InsertOptions extends CriteriaContextSpec {


        boolean isMigration();

        @Nullable
        NullHandleMode nullHandle();


    }

    interface DomainInsertOptions extends InsertOptions {

        boolean isPreferLiteral();

    }


    interface ColumnListClause extends CriteriaContextSpec {

        boolean containField(FieldMeta<?> field);
    }

    static abstract class InsertOptionsImpl<MR, NR> implements InsertOptions, Insert._MigrationOptionClause<MR>
            , Insert._NullOptionClause<NR> {

        final CriteriaContext criteriaContext;

        private boolean migration;

        private NullHandleMode nullHandleMode;

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
        public final NR nullHandle(NullHandleMode mode) {
            this.nullHandleMode = mode;
            return (NR) this;
        }

        @Override
        public final CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public final boolean isMigration() {
            return this.migration;
        }

        @Override
        public final NullHandleMode nullHandle() {
            return this.nullHandleMode;
        }


    }//InsertOptionsImpl


    static abstract class DomainInsertOptionsImpl<MR, NR, PR> extends InsertOptionsImpl<MR, NR>
            implements DomainInsertOptions, Insert._PreferLiteralClause<PR> {


        private boolean preferLiteral;

        DomainInsertOptionsImpl(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }

        @SuppressWarnings("unchecked")
        @Override
        public final PR preferLiteral(boolean prefer) {
            this.preferLiteral = prefer;
            return (PR) this;
        }

        @Override
        public final boolean isPreferLiteral() {
            return this.preferLiteral;
        }


    }//DomainInsertOptionsImpl


    /**
     * @param <F> must be {@code  FieldMeta<T>} or {@code  FieldMeta<? super T>}
     */
    static abstract class ColumnsClause<C, F extends TableField, RR>
            implements Insert._ColumnListClause<C, F, RR>, Insert._StaticColumnClause<F, RR>
            , _Insert, ColumnListClause, Statement.StatementMockSpec {

        final CriteriaContext criteriaContext;

        final C criteria;

        final boolean migration;

        final TableMeta<?> table;

        private List<FieldMeta<?>> fieldList;

        private List<FieldMeta<?>> childFieldList;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        ColumnsClause(CriteriaContext criteriaContext, boolean migration, TableMeta<?> table) {
            this.criteriaContext = criteriaContext;
            this.criteria = criteriaContext.criteria();
            this.migration = migration;
            this.table = table;

            CriteriaContextStack.assertNonNull(this.criteriaContext, this.table, "table must non-null");
        }

        @Override
        public final CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(Consumer<Consumer<F>> consumer) {
            consumer.accept(this::addField);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(BiConsumer<C, Consumer<F>> consumer) {
            consumer.accept(this.criteria, this::addField);
            return this;
        }

        @Override
        public final Insert._StaticColumnClause<F, RR> leftParen(F field) {
            this.addField(field);
            return this;
        }

        @Override
        public final Insert._StaticColumnClause<F, RR> comma(F field) {
            this.addField(field);
            return this;
        }

        @Override
        public final RR rightParen() {
            final List<FieldMeta<?>> fieldList, childFieldList;
            fieldList = this.fieldList;
            childFieldList = this.childFieldList;

            final int size, childSize;
            if (fieldList == null) {
                this.fieldList = Collections.emptyList();
                size = 0;
            } else if ((size = fieldList.size()) == 1) {
                this.fieldList = Collections.singletonList(fieldList.get(0));
            } else {
                this.fieldList = Collections.unmodifiableList(fieldList);
            }

            if (childFieldList == null) {
                this.childFieldList = Collections.emptyList();
                childSize = 0;
            } else if ((childSize = childFieldList.size()) == 1) {
                this.childFieldList = Collections.singletonList(childFieldList.get(0));
            } else {
                this.childFieldList = Collections.unmodifiableList(childFieldList);
            }

            if (size == 0 && childSize == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "column list must non-empty");
            }
            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null || fieldMap.size() != size + childSize) {
                //no bug,never here
                throw new IllegalStateException();
            }
            this.fieldMap = Collections.unmodifiableMap(fieldMap);
            return this.columnListEnd(size, childSize);
        }


        @Override
        public final TableMeta<?> table() {
            return this.table;
        }

        @Override
        public final List<FieldMeta<?>> fieldList() {
            List<FieldMeta<?>> fieldList = this.fieldList;
            if (fieldList == null) {
                fieldList = Collections.emptyList();
                this.fieldList = fieldList;
            } else if (fieldList instanceof ArrayList) {
                //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
                throw _Exceptions.castCriteriaApi();
            }
            return fieldList;
        }

        @Override
        public final List<FieldMeta<?>> childFieldList() {
            List<FieldMeta<?>> childFieldList = this.childFieldList;
            if (childFieldList == null) {
                childFieldList = Collections.emptyList();
                this.childFieldList = childFieldList;
            } else if (childFieldList instanceof ArrayList) {
                //here, don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
                throw _Exceptions.castCriteriaApi();
            }
            return childFieldList;
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            Map<FieldMeta<?>, Boolean> map = this.fieldMap;
            if (map == null) {
                map = Collections.emptyMap();
                this.fieldMap = map;
            } else if (map instanceof HashMap) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return map;
        }


        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final _Dialect d;
            d = _MockDialects.from(dialect);
            final Stmt stmt;
            stmt = d.insert((Insert) this, visible);
            return d.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return _MockDialects.from(dialect).insert((Insert) this, visible);
        }


        @Override
        public void clear() {
            this.fieldList = null;
            this.childFieldList = null;
            this.fieldMap = null;
        }

        abstract RR columnListEnd(int fieldSize, int childFieldSize);


        @Override
        public final boolean containField(final FieldMeta<?> field) {
            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            final TableMeta<?> table, fieldTable;
            table = this.table;
            final boolean match;
            if (fieldMap != null) {
                match = fieldMap.containsKey(field);
            } else if ((fieldTable = field.tableMeta()) instanceof ChildTableMeta) {
                match = fieldTable == table;
            } else if (table instanceof ChildTableMeta) {
                match = fieldTable == ((ChildTableMeta<?>) table).parentMeta();
            } else {
                match = fieldTable == table;
            }
            return match;
        }


        private void addField(final F field) {
            final TableMeta<?> table = this.table, fieldTable;
            if (table == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            fieldTable = field.tableMeta();
            if (!this.migration) {
                if (!field.insertable()) {
                    throw CriteriaContextStack.criteriaError(this.criteriaContext
                            , _Exceptions::nonInsertableField, field);
                }
                if (fieldTable instanceof SingleTableMeta) {
                    switch (field.fieldName()) {
                        case _MetaBridge.CREATE_TIME:
                        case _MetaBridge.UPDATE_TIME:
                        case _MetaBridge.VERSION:
                            throw CriteriaContextStack.criteriaError(this.criteriaContext
                                    , _Exceptions::armyManageField, field);
                        default://no-op
                    }
                }

                if (field == this.table.discriminator() || field.generatorType() != null) {
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::armyManageField, field);
                }

            }


            if (fieldTable instanceof ChildTableMeta) {
                if (fieldTable != table) {
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::unknownColumn, field);
                }
                List<FieldMeta<?>> childFieldList = this.childFieldList;
                if (childFieldList == null) {
                    childFieldList = new ArrayList<>();
                    this.childFieldList = childFieldList;
                }
            } else {
                if (table instanceof ChildTableMeta) {
                    if (fieldTable != ((ChildTableMeta<?>) table).parentMeta()) {
                        throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::unknownColumn, field);
                    }
                } else if (fieldTable != table) {
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::unknownColumn, field);
                }

                List<FieldMeta<?>> fieldList = this.fieldList;
                if (fieldList == null) {
                    fieldList = new ArrayList<>();
                    this.fieldList = fieldList;
                }
            }

            Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                fieldMap = new HashMap<>();
                this.fieldMap = fieldMap;
            }
            if (fieldMap.putIfAbsent((FieldMeta<?>) field, Boolean.TRUE) != null) {
                String m = String.format("%s duplication", field);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }

        }

    }//ColumnsClause


    /**
     * @param <F> must be {@code  FieldMeta<T>} or {@code  FieldMeta<? super T>}
     */
    @SuppressWarnings("unchecked")
    static abstract class CommonExpClause<C, F extends TableField, RR> extends ColumnsClause<C, F, RR>
            implements Insert._CommonExpClause<C, F, RR>, _Insert._CommonExpInsert {


        final boolean preferLiteral;

        final NullHandleMode nullHandleMode;

        private Map<FieldMeta<?>, _Expression> commonExpMap;

        CommonExpClause(InsertOptions options, TableMeta<?> table) {
            super(options.getCriteriaContext(), options.isMigration(), table);
            if (options instanceof DomainInsertOptions) {
                this.preferLiteral = ((DomainInsertOptions) options).isPreferLiteral();
            } else {
                this.preferLiteral = false;
            }
            this.nullHandleMode = options.nullHandle();
        }

        @Override
        public final RR common(final F field, final @Nullable Object value) {
            if (!field.insertable()) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonInsertableField, field);
            }
            final String fieldName = field.fieldName();
            if (_MetaBridge.UPDATE_TIME.equals(fieldName)
                    || _MetaBridge.VERSION.equals(fieldName)
                    || _MetaBridge.CREATE_TIME.equals(fieldName)) {
                String m = String.format("Common expression don't support %s", field);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            if (!this.migration && field.generatorType() != null) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::armyManageField, field);
            }
            Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
            if (commonExpMap == null) {
                commonExpMap = new HashMap<>();
                this.commonExpMap = commonExpMap;
            }
            final Expression exp;
            if (value == null) {
                if (this.preferLiteral) {
                    exp = SQLs.nullWord();
                } else {
                    exp = SQLs.param(field, null);
                }
            } else if (value instanceof SubQuery && !(value instanceof ScalarExpression)) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonScalarSubQuery
                        , (SubQuery) value);
            } else if (value instanceof Expression) {
                exp = (Expression) value;
            } else {
                exp = SQLs.param(field, value);
            }
            if (commonExpMap.putIfAbsent((FieldMeta<?>) field, (ArmyExpression) exp) != null) {
                String m = String.format("duplication common expression for %s.", field);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            return (RR) this;
        }

        @Override
        public final RR commonLiteral(F field, @Nullable Object value) {
            return this.common(field, SQLs._nullableLiteral(field, value));
        }

        @Override
        public final RR commonExp(F field, Function<C, ? extends Expression> function) {
            final Expression expression;
            expression = function.apply(this.criteria);
            if (expression == null) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            return this.common(field, expression);
        }

        @Override
        public final RR commonExp(F field, Supplier<? extends Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression == null) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            return this.common(field, expression);
        }

        @Override
        public final RR commonDefault(F field) {
            return this.common(field, SQLs.defaultWord());
        }

        @Override
        public final RR commonNull(F field) {
            return this.common(field, SQLs.nullWord());
        }


        @Override
        public final RR ifCommon(F field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.common(field, value);
            }
            return (RR) this;
        }

        @Override
        public final RR ifCommon(F field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.common(field, value);
            }
            return (RR) this;
        }

        @Override
        public final RR ifCommon(F field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.common(field, value);
            }
            return (RR) this;
        }

        @Override
        public final RR ifCommonLiteral(F field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.common(field, SQLs._nonNullLiteral(field, value));
            }
            return (RR) this;
        }

        @Override
        public final RR ifCommonLiteral(F field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.common(field, SQLs._nonNullLiteral(field, value));
            }
            return (RR) this;
        }

        @Override
        public final RR ifCommonLiteral(F field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.common(field, SQLs._nonNullLiteral(field, value));
            }
            return (RR) this;
        }

        @Override
        public final Map<FieldMeta<?>, _Expression> commonExpMap() {
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

        final void unmodifiedCommonExpMap() {
            final Map<FieldMeta<?>, _Expression> map = this.commonExpMap;
            if (map == null) {
                this.commonExpMap = Collections.emptyMap();
            } else if (map instanceof HashMap) {
                this.commonExpMap = Collections.unmodifiableMap(map);
            }
        }


    }//CommonExpClause


    @SuppressWarnings("unchecked")
    static abstract class DomainValueClause<C, T extends IDomain, F extends TableField, CR, VR>
            extends CommonExpClause<C, F, CR> implements Insert._DomainValueClause<C, T, VR>
            , _Insert._DomainInsert {


        private List<IDomain> domainList;

        DomainValueClause(InsertOptions options, TableMeta<T> table) {
            super(options, table);
        }

        @Override
        public final VR value(T domain) {
            CriteriaContextStack.assertNonNull(this.criteriaContext, domain, "domain must non-null");
            this.domainList = Collections.singletonList(domain);
            this.unmodifiedCommonExpMap();
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
            if (!this.table.javaType().isInstance(domain)) {
                throw nonDomainInstance(this.criteriaContext, domain, this.table);
            }
            return this.value((T) domain);
        }

        @Override
        public final VR values(final List<T> domainList) {
            CriteriaContextStack.assertNonNull(this.criteriaContext, domainList, "domainList must non-empty");
            if (domainList.size() == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "domainList must non-empty");
            }
            this.domainList = Collections.unmodifiableList(new ArrayList<>(domainList));
            this.unmodifiedCommonExpMap();
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
            final int size = domainList.size();
            if (size == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "domainList must non-empty");
            }
            final TableMeta<?> table = this.table;
            if (table == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final Class<?> javaType = table.javaType();
            final List<IDomain> list = new ArrayList<>(size);
            for (Object domain : domainList) {
                if (domain == null || !javaType.isAssignableFrom(domain.getClass())) {
                    throw nonDomainInstance(this.criteriaContext, domain, this.table);
                }
                list.add((IDomain) domain);
            }
            this.domainList = Collections.unmodifiableList(list);
            this.unmodifiedCommonExpMap();
            return this.valuesEnd();
        }

        @Override
        public final boolean isPreferLiteral() {
            return this.preferLiteral;
        }

        @Override
        public final List<IDomain> domainList() {
            final List<IDomain> list = this.domainList;
            assert list != null;
            return list;
        }

        @Override
        public void clear() {
            super.clear();
            this.domainList = null;
        }

        abstract VR valuesEnd();

    }//DomainValueClause


    static abstract class StaticColumnValuePairClause<C, F extends TableField, VR>
            implements Insert._StaticValueLeftParenClause<C, F, VR>, Insert._StaticColumnValueClause<C, F, VR>
            , CriteriaContextSpec {

        final CriteriaContext criteriaContext;

        final C criteria;

        StaticColumnValuePairClause(CriteriaContext criteriaContext) {
            this.criteriaContext = criteriaContext;
            this.criteria = criteriaContext.criteria();
        }

        @Override
        public final CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, F, VR> leftParen(F field, @Nullable Object value) {
            this.addValuePair((FieldMeta<?>) field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, F, VR> leftParenLiteral(F field, @Nullable Object value) {
            this.addValuePair((FieldMeta<?>) field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, F, VR> leftParenExp(F field, Supplier<? extends Expression> supplier) {
            final Expression exp;
            exp = supplier.get();
            CriteriaContextStack.assertFunctionExp(this.criteriaContext, exp);
            this.addValuePair((FieldMeta<?>) field, (ArmyExpression) exp);
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, F, VR> leftParenExp(F field, Function<C, ? extends Expression> function) {
            final Expression exp;
            exp = function.apply(this.criteria);
            CriteriaContextStack.assertFunctionExp(this.criteriaContext, exp);
            this.addValuePair((FieldMeta<?>) field, (ArmyExpression) exp);
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, F, VR> comma(F field, @Nullable Object value) {
            this.addValuePair((FieldMeta<?>) field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, F, VR> commaLiteral(F field, @Nullable Object value) {
            this.addValuePair((FieldMeta<?>) field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, F, VR> commaExp(F field, Supplier<? extends Expression> supplier) {
            final Expression exp;
            exp = supplier.get();
            CriteriaContextStack.assertFunctionExp(this.criteriaContext, exp);
            this.addValuePair((FieldMeta<?>) field, (ArmyExpression) exp);
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, F, VR> commaExp(F field, Function<C, ? extends Expression> function) {
            final Expression exp;
            exp = function.apply(this.criteria);
            CriteriaContextStack.assertFunctionExp(this.criteriaContext, exp);
            this.addValuePair((FieldMeta<?>) field, (ArmyExpression) exp);
            return this;
        }

        abstract void addValuePair(FieldMeta<?> field, _Expression value);


    }//StaticValueColumnClause


    static abstract class DynamicValueInsertValueClause<C, F extends TableField, RR, VR>
            extends CommonExpClause<C, F, RR> implements Insert._DynamicValueClause<C, F, VR>
            , Insert._DynamicValuesClause<C, F, VR>, RowConstructor<F> {

        private List<Map<FieldMeta<?>, _Expression>> valuePairList;

        private Map<FieldMeta<?>, _Expression> valuePairMap;


        DynamicValueInsertValueClause(InsertOptions options, TableMeta<?> table) {
            super(options, table);
        }

        @Override
        public final VR value(Consumer<ColumnConsumer<F>> consumer) {
            return this.singleRowValues(consumer);
        }

        @Override
        public final VR value(BiConsumer<C, ColumnConsumer<F>> consumer) {
            return this.singleRowValues(consumer);
        }

        @Override
        public final VR values(Consumer<RowConstructor<F>> consumer) {
            return this.multiRowValues(consumer);
        }

        @Override
        public final VR values(BiConsumer<C, RowConstructor<F>> consumer) {
            return this.multiRowValues(consumer);
        }

        @Override
        public final ColumnConsumer<F> row() {
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
        public final ColumnConsumer<F> accept(F field, @Nullable Object value) {
            return this.addValuePair((FieldMeta<?>) field, SQLs._nullableParam(field, value));
        }

        @Override
        public final ColumnConsumer<F> acceptLiteral(F field, @Nullable Object value) {
            return this.addValuePair((FieldMeta<?>) field, SQLs._nullableLiteral(field, value));
        }

        @Override
        public final ColumnConsumer<F> acceptExp(F field, Supplier<? extends Expression> supplier) {
            return this.addValuePair((FieldMeta<?>) field, supplier.get());
        }

        @Override
        public void clear() {
            super.clear();
            this.valuePairList = null;
            this.valuePairMap = null;
        }

        /**
         * @param rowValueList a unmodified list
         */
        abstract VR valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowValueList);


        private ColumnConsumer<F> addValuePair(FieldMeta<?> field, @Nullable Expression value) {
            final Map<FieldMeta<?>, _Expression> currentPairMap = this.valuePairMap;
            if (currentPairMap == null) {
                String m = String.format("Not found any row,please use %s.row() method create row."
                        , RowConstructor.class.getName());
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (!(currentPairMap instanceof HashMap)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }

            if (!containField(field)) {
                throw notContainField(this.criteriaContext, field);
            }
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "value not army expression.");
            }
            if (currentPairMap.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.criteriaContext, field);
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        private VR singleRowValues(final Object callback) {
            //1. validate
            if (this.valuePairMap != null || this.valuePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            //2. initializing
            Map<FieldMeta<?>, _Expression> valuePairMap = new HashMap<>();
            this.valuePairMap = valuePairMap;
            final List<Map<FieldMeta<?>, _Expression>> emptyList;
            emptyList = Collections.emptyList();
            this.valuePairList = emptyList;

            //3. callback
            if (callback instanceof Consumer) {
                ((Consumer<ColumnConsumer<F>>) callback).accept(this);
            } else if (callback instanceof BiConsumer) {
                ((BiConsumer<C, ColumnConsumer<F>>) callback).accept(this.criteria, this);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }
            //4. validate
            if (this.valuePairList != emptyList || this.valuePairMap != valuePairMap) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            //5. finally
            if (valuePairMap.size() == 0) {
                valuePairMap = Collections.emptyMap();
            } else {
                valuePairMap = Collections.unmodifiableMap(valuePairMap);
            }
            this.valuePairMap = valuePairMap;
            return this.valueClauseEnd(Collections.singletonList(valuePairMap));
        }

        @SuppressWarnings("unchecked")
        private VR multiRowValues(final Object callback) {
            //1. validate
            if (this.valuePairMap != null || this.valuePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            //2. callback
            if (callback instanceof Consumer) {
                ((Consumer<RowConstructor<F>>) callback).accept(this);
            } else if (callback instanceof BiConsumer) {
                ((BiConsumer<C, RowConstructor<F>>) callback).accept(this.criteria, this);
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
    static abstract class AssignmentInsertClause<C, F extends TableField, SR>
            implements Insert._AssignmentSetClause<C, F, SR>, _Insert._AssignmentInsert, ColumnListClause
            , Statement.StatementMockSpec {

        final CriteriaContext criteriaContext;

        final C criteria;

        final boolean migration;

        final NullHandleMode nullHandleMode;

        final boolean supportRowItem;

        final TableMeta<?> table;

        private List<ItemPair> itemPairList;


        AssignmentInsertClause(InsertOptions options, boolean supportRowItem, TableMeta<?> table) {
            this.criteriaContext = options.getCriteriaContext();
            this.criteria = this.criteriaContext.criteria();
            this.migration = options.isMigration();
            this.nullHandleMode = options.nullHandle();

            this.supportRowItem = supportRowItem;
            this.table = table;
        }

        @Override
        public final SR setPair(Consumer<Consumer<ItemPair>> consumer) {
            consumer.accept(this::innerAddItemPair);
            return (SR) this;
        }

        @Override
        public final SR setPair(BiConsumer<C, Consumer<ItemPair>> consumer) {
            consumer.accept(this.criteria, this::innerAddItemPair);
            return (SR) this;
        }

        @Override
        public final SR set(F field, @Nullable Object value) {
            return this.addFieldPair((FieldMeta<?>) field, SQLs._nullableParam(field, value));
        }

        @Override
        public final SR setLiteral(F field, @Nullable Object value) {
            return this.addFieldPair((FieldMeta<?>) field, SQLs._nullableLiteral(field, value));
        }

        @Override
        public final SR setExp(F field, Supplier<? extends Expression> supplier) {
            return this.addFieldPair((FieldMeta<?>) field, supplier.get());
        }

        @Override
        public final SR setExp(F field, Function<C, ? extends Expression> function) {
            return this.addFieldPair((FieldMeta<?>) field, function.apply(this.criteria));
        }

        @Override
        public final SR ifSet(F field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableParam(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(F field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableParam(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(F field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableParam(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetLiteral(F field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableLiteral(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetLiteral(F field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableLiteral(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetLiteral(F field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableLiteral(field, value));
            }
            return (SR) this;
        }

        @Override
        public final TableMeta<?> table() {
            return this.table;
        }

        @Override
        public final List<FieldMeta<?>> fieldList() {
            return Collections.emptyList();
        }

        @Override
        public final List<FieldMeta<?>> childFieldList() {
            return Collections.emptyList();
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            return Collections.emptyMap();
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
        public final List<ItemPair> rowPairList() {
            final List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null || itemPairList instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return itemPairList;
        }

        @Override
        public void clear() {
            this.itemPairList = null;
        }

        @Override
        public final CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public final boolean containField(final FieldMeta<?> field) {
            final TableMeta<?> table, fieldTable;
            table = this.table;
            fieldTable = field.tableMeta();
            final boolean match;
            if (fieldTable instanceof ChildTableMeta) {
                match = fieldTable == table;
            } else if (table instanceof ChildTableMeta) {
                match = fieldTable == ((ChildTableMeta<?>) table).parentMeta();
            } else {
                match = fieldTable == table;
            }
            return match;
        }

        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final _Dialect d;
            d = _MockDialects.from(dialect);
            final Stmt stmt;
            stmt = d.insert((Insert) this, visible);
            return d.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return _MockDialects.from(dialect).insert((Insert) this, visible);
        }


        final void assignmentSetClauseEnd() {
            final List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                this.itemPairList = Collections.emptyList();
            } else if (itemPairList instanceof ArrayList) {
                this.itemPairList = _CollectionUtils.unmodifiableList(itemPairList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
        }


        private void innerAddItemPair(final ItemPair itemPair) {
            if (itemPair instanceof SQLs.FieldItemPair) {
                this.validateItemField(((SQLs.FieldItemPair) itemPair).field);
            } else if (!(itemPair instanceof SQLs.RowItemPair)) {
                String m = String.format("Non-Army %s", ItemPair.class.getName());
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (this.supportRowItem) {
                for (DataField field : ((SQLs.RowItemPair) itemPair).fieldList) {
                    this.validateItemField(field);
                }
            } else {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "Don't support row item");
            }
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                itemPairList = new ArrayList<>();
                this.itemPairList = itemPairList;
            }
            itemPairList.add(itemPair);
        }

        private SR addFieldPair(FieldMeta<?> field, @Nullable Expression value) {
            if (!this.containField(field)) {
                throw notContainField(this.criteriaContext, field);
            }
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                itemPairList = new ArrayList<>();
                this.itemPairList = itemPairList;
            }
            itemPairList.add(SQLs.itemPair(field, value));
            return (SR) this;
        }

        private void validateItemField(final DataField field) {
            if (!(field instanceof FieldMeta)) {
                String m = String.format("assignment insert syntax support only %s", FieldMeta.class.getName());
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            if (!this.containField((FieldMeta<?>) field)) {
                throw notContainField(this.criteriaContext, (FieldMeta<?>) field);
            }
        }


    }//AssignmentInsertClause


    static abstract class InsertStatement<I extends DmlStatement.DmlInsert>
            implements _Insert, Statement.StatementMockSpec, DmlStatement._DmlInsertSpec<I>
            , DmlStatement.DmlInsert {


        private final CriteriaContext criteriaContext;

        private final TableMeta<?> table;

        private final List<FieldMeta<?>> fieldList;

        private final List<FieldMeta<?>> childFieldList;

        private final Map<FieldMeta<?>, Boolean> fieldMap;


        private Boolean prepared;

        InsertStatement(_Insert clause) {
            this.criteriaContext = ((CriteriaContextSpec) clause).getCriteriaContext();
            this.table = clause.table();
            this.fieldList = clause.fieldList();

            this.childFieldList = clause.childFieldList();
            this.fieldMap = clause.fieldMap();
        }


        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final _Dialect d;
            d = _MockDialects.from(dialect);
            final Stmt stmt;
            stmt = d.insert((Insert) this, visible);
            return d.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return _MockDialects.from(dialect).insert((Insert) this, visible);
        }

        @Override
        public final TableMeta<?> table() {
            return this.table;
        }

        @Override
        public final List<FieldMeta<?>> fieldList() {
            return this.fieldList;
        }

        @Override
        public final List<FieldMeta<?>> childFieldList() {
            return this.childFieldList;
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            return this.fieldMap;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final I asInsert() {
            _Assert.nonPrepared(this.prepared);
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


    }//InsertStatement


    static abstract class ValueSyntaxStatement<I extends DmlStatement.DmlInsert>
            extends InsertStatement<I> implements _Insert._CommonExpInsert {

        private final boolean migration;
        private final NullHandleMode nullHandleMode;

        private final Map<FieldMeta<?>, _Expression> commonExpMap;

        ValueSyntaxStatement(_Insert._CommonExpInsert clause) {
            super(clause);
            this.migration = clause.isMigration();
            this.nullHandleMode = clause.nullHandle();
            this.commonExpMap = clause.commonExpMap();
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
        public final Map<FieldMeta<?>, _Expression> commonExpMap() {
            return this.commonExpMap;
        }


    }//ValueInsertStatement


    static abstract class RowSetInsertStatement<I extends DmlStatement.DmlInsert>
            implements _Insert._RowSetInsert, DmlStatement._DmlInsertSpec<I>
            , DmlStatement.DmlInsert, Statement.StatementMockSpec {

        final CriteriaContext criteriaContext;
        final TableMeta<?> table;

        final List<FieldMeta<?>> fieldList;

        final List<FieldMeta<?>> childFieldList;

        final Map<FieldMeta<?>, Boolean> fieldMap;

        final RowSet rowSet;

        final RowSet childRowSet;

        private Boolean prepared;

        RowSetInsertStatement(_Insert clause, RowSet rowSet) {
            this.criteriaContext = ((CriteriaContextSpec) clause).getCriteriaContext();

            this.table = clause.table();
            this.fieldList = clause.fieldList();
            this.fieldMap = clause.fieldMap();
            this.rowSet = rowSet;

            this.childFieldList = Collections.emptyList();
            this.childRowSet = null;
        }

        RowSetInsertStatement(_Insert parentClause, RowSet parentSet, _Insert childClause, RowSet childRowSet) {
            this.criteriaContext = ((CriteriaContextSpec) parentClause).getCriteriaContext();

            this.table = childClause.table();
            assert this.table instanceof ChildTableMeta;
            this.fieldList = parentClause.fieldList();
            this.rowSet = parentSet;

            this.childFieldList = childClause.childFieldList();
            this.childRowSet = childRowSet;

            final Map<FieldMeta<?>, Boolean> parentFieldMap, childFieldMap, fieldMap;

            parentFieldMap = parentClause.fieldMap();
            childFieldMap = childClause.fieldMap();
            fieldMap = new HashMap<>((int) ((parentFieldMap.size() + childFieldMap.size()) / 0.75F));

            fieldMap.putAll(parentFieldMap);
            fieldMap.putAll(childFieldMap);
            this.fieldMap = Collections.unmodifiableMap(fieldMap);
        }


        @Override
        public final TableMeta<?> table() {
            return this.table;
        }

        @Override
        public final List<FieldMeta<?>> fieldList() {
            return this.fieldList;
        }

        @Override
        public final List<FieldMeta<?>> childFieldList() {
            return this.childFieldList;
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            return this.fieldMap;
        }

        @Override
        public final RowSet rowSet() {
            return this.rowSet;
        }

        @Override
        public final RowSet childRowSet() {
            return this.childRowSet;
        }


        @SuppressWarnings("unchecked")
        @Override
        public final I asInsert() {
            _Assert.nonPrepared(this.prepared);
            if (this instanceof SubStatement) {
                CriteriaContextStack.pop(this.criteriaContext);
            } else {
                CriteriaContextStack.clearContextStack(this.criteriaContext);
            }
            this.validateStatement();
            this.criteriaContext.clear();
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

        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final _Dialect d;
            d = _MockDialects.from(dialect);
            final Stmt stmt;
            stmt = d.insert((Insert) this, visible);
            return d.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return _MockDialects.from(dialect).insert((Insert) this, visible);
        }

        /**
         * <p>
         * This method is invoked after {@link  CriteriaContextStack#clearContextStack(CriteriaContext)}
         * and {@link  CriteriaContextStack#pop(CriteriaContext)}.
         * </p>
         */
        final void validateStatement() {
            final TableMeta<?> table = this.table;
            if (table instanceof ChildTableMeta) {
                doValidateStatement(((ChildTableMeta<?>) table).parentMeta(), this.fieldList, this.rowSet);
                doValidateStatement(table, this.childFieldList, this.childRowSet);
            } else {
                doValidateStatement(table, this.fieldList, this.rowSet);
            }

        }

        private static void doValidateStatement(final TableMeta<?> table, final List<FieldMeta<?>> fieldList
                , final @Nullable RowSet rowSet) {
            final int size;
            size = fieldList.size();
            if (size == 0) {
                throw _Exceptions.noFieldsForRowSetInsert(table);
            }
            if (rowSet == null) {
                throw new CriteriaException(String.format("RowSet is null for %s", table));
            }
            final int selectionSize;
            selectionSize = CriteriaUtils.selectionCount(rowSet);
            if (selectionSize != size) {
                throw _Exceptions.rowSetSelectionAndFieldSizeNotMatch(selectionSize, size, table);
            }
        }


    }//RowSetInsertStatement


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


}
