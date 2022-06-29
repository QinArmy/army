package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._DomainInsert;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._RowSetInsert;
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
import io.army.util._ClassUtils;
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


    interface InsertOptions {


        boolean isMigration();

        NullHandleMode nullHandle();

        boolean isPreferLiteral();

    }

    /**
     * @param <F> must be {@code  FieldMeta<T>} or {@code  FieldMeta<? super T>}
     */
    static abstract class ColumnsClause<C, F extends TableField, RR>
            implements Insert._ColumnListClause<C, F, RR>, Insert._StaticColumnClause<F, RR>, _Insert, Insert {


        final CriteriaContext criteriaContext;

        final C criteria;

        final boolean migration;

        final TableMeta<?> table;

        private List<FieldMeta<?>> fieldList;

        private List<FieldMeta<?>> childFieldList;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        ColumnsClause(CriteriaContext criteriaContext, boolean migration, TableMeta<?> table) {
            CriteriaContextStack.assertNonNull(table);
            this.criteriaContext = criteriaContext;
            this.criteria = criteriaContext.criteria();
            this.migration = migration;
            this.table = table;
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
                throw CriteriaContextStack.criteriaError("column list must non-empty");
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
                //here, don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
                throw _Exceptions.castCriteriaApi();
            }
            return map;
        }


        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            final _Dialect d;
            d = _MockDialects.from(dialect);
            final Stmt stmt;
            stmt = d.insert(this, visible);
            return d.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            return _MockDialects.from(dialect).insert(this, visible);
        }


        @Override
        public void clear() {
            this.fieldList = null;
            this.childFieldList = null;
            this.fieldMap = null;
        }

        abstract RR columnListEnd(int fieldSize, int childFieldSize);


        final boolean containField(final FieldMeta<?> field) {
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
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            fieldTable = field.tableMeta();
            if (!this.migration) {
                if (!field.insertable()) {
                    throw CriteriaContextStack.criteriaError(_Exceptions::nonInsertableField, field);
                }
                if (fieldTable instanceof SingleTableMeta) {
                    switch (field.fieldName()) {
                        case _MetaBridge.CREATE_TIME:
                        case _MetaBridge.UPDATE_TIME:
                        case _MetaBridge.VERSION:
                            throw CriteriaContextStack.criteriaError(_Exceptions::armyManageField, field);
                        default://no-op
                    }
                }

                if (field == this.table.discriminator() || field.generatorType() != null) {
                    throw CriteriaContextStack.criteriaError(_Exceptions::armyManageField, field);
                }

            }


            if (fieldTable instanceof ChildTableMeta) {
                if (fieldTable != table) {
                    throw CriteriaContextStack.criteriaError(_Exceptions::unknownColumn, field);
                }
                List<FieldMeta<?>> childFieldList = this.childFieldList;
                if (childFieldList == null) {
                    childFieldList = new ArrayList<>();
                    this.childFieldList = childFieldList;
                }
            } else {
                if (table instanceof ChildTableMeta) {
                    if (fieldTable != ((ChildTableMeta<?>) table).parentMeta()) {
                        throw CriteriaContextStack.criteriaError(_Exceptions::unknownColumn, field);
                    }
                } else if (fieldTable != table) {
                    throw CriteriaContextStack.criteriaError(_Exceptions::unknownColumn, field);
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
                throw CriteriaContextStack.criteriaError(m);
            }

        }

    }//ColumnsClause


    /**
     * @param <F> must be {@code  FieldMeta<T>} or {@code  FieldMeta<? super T>}
     */
    @SuppressWarnings("unchecked")
    static abstract class CommonExpClause<C, F extends TableField, RR, CR> extends ColumnsClause<C, F, RR>
            implements Insert._CommonExpClause<C, F, CR>, _Insert._CommonExpInsert {


        final boolean preferLiteral;

        final NullHandleMode nullHandleMode;

        private Map<FieldMeta<?>, _Expression> commonExpMap;

        CommonExpClause(CriteriaContext criteriaContext, InsertOptions options, TableMeta<?> table) {
            super(criteriaContext, options.isMigration(), table);
            this.preferLiteral = options.isPreferLiteral();
            this.nullHandleMode = options.nullHandle();
        }

        @Override
        public final CR common(final F field, final @Nullable Object value) {
            if (!field.insertable()) {
                throw CriteriaContextStack.criteriaError(_Exceptions::nonInsertableField, field);
            }
            final String fieldName = field.fieldName();
            if (_MetaBridge.UPDATE_TIME.equals(fieldName)
                    || _MetaBridge.VERSION.equals(fieldName)
                    || _MetaBridge.CREATE_TIME.equals(fieldName)) {
                String m = String.format("Common expression don't support %s", field);
                throw CriteriaContextStack.criteriaError(m);
            }
            if (!this.migration && field.generatorType() != null) {
                throw CriteriaContextStack.criteriaError(_Exceptions::armyManageField, field);
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
                throw CriteriaContextStack.criteriaError(_Exceptions::nonScalarSubQuery, (SubQuery) value);
            } else if (value instanceof Expression) {
                exp = (Expression) value;
            } else {
                exp = SQLs.param(field, value);
            }
            if (commonExpMap.putIfAbsent((FieldMeta<?>) field, (ArmyExpression) exp) != null) {
                String m = String.format("duplication common expression for %s.", field);
                throw CriteriaContextStack.criteriaError(m);
            }
            return (CR) this;
        }

        @Override
        public final CR commonLiteral(F field, @Nullable Object value) {
            return this.common(field, SQLs._nullableLiteral(field, value));
        }

        @Override
        public final CR commonExp(F field, Function<C, ? extends Expression> function) {
            final Expression expression;
            expression = function.apply(this.criteria);
            if (expression == null) {
                throw CriteriaContextStack.criteriaError("return null,not expression.");
            }
            return this.common(field, expression);
        }

        @Override
        public final CR commonExp(F field, Supplier<? extends Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression == null) {
                throw CriteriaContextStack.criteriaError("return null,not expression.");
            }
            return this.common(field, expression);
        }

        @Override
        public final CR commonDefault(F field) {
            return this.common(field, SQLs.defaultWord());
        }

        @Override
        public final CR commonNull(F field) {
            return this.common(field, SQLs.nullWord());
        }


        @Override
        public final CR ifCommon(F field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.common(field, value);
            }
            return (CR) this;
        }

        @Override
        public final CR ifCommon(F field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.common(field, value);
            }
            return (CR) this;
        }

        @Override
        public final CR ifCommon(F field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.common(field, value);
            }
            return (CR) this;
        }

        @Override
        public final CR ifCommonLiteral(F field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.common(field, SQLs._nonNullLiteral(field, value));
            }
            return (CR) this;
        }

        @Override
        public final CR ifCommonLiteral(F field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.common(field, SQLs._nonNullLiteral(field, value));
            }
            return (CR) this;
        }

        @Override
        public final CR ifCommonLiteral(F field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.common(field, SQLs._nonNullLiteral(field, value));
            }
            return (CR) this;
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
            extends CommonExpClause<C, F, CR, CR> implements Insert._DomainValueClause<C, T, VR>, _DomainInsert {


        private List<IDomain> domainList;

        DomainValueClause(CriteriaContext criteriaContext, InsertOptions options, TableMeta<T> table) {
            super(criteriaContext, options, table);
        }

        @Override
        public final VR value(T domain) {
            CriteriaContextStack.assertNonNull(domain, "domain must non-null");
            this.domainList = Collections.singletonList(domain);
            return (VR) this;
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
                throw nonDomainInstance(domain, this.table);
            }
            return this.value((T) domain);
        }

        @Override
        public final VR values(final List<T> domainList) {
            CriteriaContextStack.assertNonNull(domainList);
            if (domainList.size() == 0) {
                throw CriteriaContextStack.criteriaError("domainList must non-empty");
            }
            this.domainList = Collections.unmodifiableList(new ArrayList<>(domainList));
            return (VR) this;
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
                throw CriteriaContextStack.criteriaError(m);
            }

            final List<?> domainList = (List<?>) value;
            final int size = domainList.size();
            if (size == 0) {
                throw CriteriaContextStack.criteriaError("domainList must non-empty");
            }
            final TableMeta<?> table = this.table;
            if (table == null) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            final Class<?> javaType = table.javaType();
            final List<IDomain> list = new ArrayList<>(size);
            for (Object domain : domainList) {
                if (domain == null || !javaType.isAssignableFrom(domain.getClass())) {
                    throw nonDomainInstance(domain, this.table);
                }
                list.add((IDomain) domain);
            }
            this.domainList = Collections.unmodifiableList(list);
            return (VR) this;
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
    }//DomainValueClause


    static abstract class StaticColumnValuePairClause<C, F extends TableField, VR>
            implements Insert._StaticValueLeftParenClause<C, F, VR>, Insert._StaticColumnValueClause<C, F, VR> {

        final CriteriaContext criteriaContext;

        final C criteria;

        StaticColumnValuePairClause(CriteriaContext criteriaContext) {
            this.criteriaContext = criteriaContext;
            this.criteria = criteriaContext.criteria();
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
            CriteriaContextStack.assertTrue(exp instanceof ArmyExpression);
            this.addValuePair((FieldMeta<?>) field, (ArmyExpression) exp);
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, F, VR> leftParenExp(F field, Function<C, ? extends Expression> function) {
            final Expression exp;
            exp = function.apply(this.criteria);
            CriteriaContextStack.assertTrue(exp instanceof ArmyExpression);
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
            CriteriaContextStack.assertTrue(exp instanceof ArmyExpression);
            this.addValuePair((FieldMeta<?>) field, (ArmyExpression) exp);
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, F, VR> commaExp(F field, Function<C, ? extends Expression> function) {
            final Expression exp;
            exp = function.apply(this.criteria);
            CriteriaContextStack.assertTrue(exp instanceof ArmyExpression);
            this.addValuePair((FieldMeta<?>) field, (ArmyExpression) exp);
            return this;
        }

        abstract void addValuePair(FieldMeta<?> field, _Expression value);


    }//StaticValueColumnClause


    static abstract class ValueInsertValueClause<C, F extends TableField, RR, CR, VR>
            extends CommonExpClause<C, F, RR, CR> implements Insert._DynamicValueClause<C, F, VR>
            , Insert._DynamicValuesClause<C, F, VR>, RowConstructor<F> {

        private List<Map<FieldMeta<?>, _Expression>> valuePairList;

        private Map<FieldMeta<?>, _Expression> valuePairMap;


        ValueInsertValueClause(CriteriaContext criteriaContext, InsertOptions options, TableMeta<?> table) {
            super(criteriaContext, options, table);
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
                } else if (valuePairList == Collections.EMPTY_LIST) {
                    throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
                }
                valuePairList.add(Collections.unmodifiableMap(currentPairMap));
            } else if (currentPairMap != null) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
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
         * @param valuePairList a unmodified list
         */
        abstract VR valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> valuePairList);


        private ColumnConsumer<F> addValuePair(FieldMeta<?> field, @Nullable Expression value) {
            final Map<FieldMeta<?>, _Expression> currentPairMap = this.valuePairMap;
            if (currentPairMap == null) {
                String m = String.format("Not found any row,please use %s.row() method create row."
                        , RowConstructor.class.getName());
                throw CriteriaContextStack.criteriaError(m);
            } else if (!(currentPairMap instanceof HashMap)) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }

            if (!containField(field)) {
                throw notContainField(field);
            }
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.criteriaError("value not army expression.");
            }
            if (currentPairMap.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(field);
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        private VR singleRowValues(final Object callback) {
            //1. validate
            if (this.valuePairMap != null || this.valuePairList != null) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
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
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            //5. finally
            valuePairMap = Collections.unmodifiableMap(valuePairMap);
            this.valuePairMap = valuePairMap;
            return this.valueClauseEnd(Collections.singletonList(valuePairMap));
        }

        @SuppressWarnings("unchecked")
        private VR multiRowValues(final Object callback) {
            //1. validate
            if (this.valuePairMap != null || this.valuePairList != null) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
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
                throw CriteriaContextStack.criteriaError("Values insert must have one row at least");
            }
            valuePairMap = Collections.unmodifiableMap(valuePairMap);
            this.valuePairMap = valuePairMap;
            List<Map<FieldMeta<?>, _Expression>> valuePairList = this.valuePairList;
            if (valuePairList == null) {
                valuePairList = Collections.singletonList(valuePairMap);
            } else {
                valuePairList.add(valuePairMap);
                valuePairList = Collections.unmodifiableList(valuePairList);
            }
            this.valuePairList = valuePairList;
            return this.valueClauseEnd(valuePairList);
        }


    }//ValueInsertValueClause


    static abstract class ValueSyntaxStatement implements Insert, _Insert._CommonExpInsert, Insert._InsertSpec {

        private final boolean migration;
        private final NullHandleMode nullHandleMode;

        private final TableMeta<?> table;

        private final List<FieldMeta<?>> fieldList;

        private final List<FieldMeta<?>> childFieldList;

        private final Map<FieldMeta<?>, Boolean> fieldMap;

        private final Map<FieldMeta<?>, _Expression> commonExpMap;


        ValueSyntaxStatement(_Insert._CommonExpInsert clause) {
            this.migration = clause.isMigration();
            this.nullHandleMode = clause.nullHandle();
            this.table = clause.table();
            this.fieldList = clause.fieldList();

            this.childFieldList = clause.childFieldList();
            this.fieldMap = clause.fieldMap();
            this.commonExpMap = clause.commonExpMap();
        }


        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            final _Dialect d;
            d = _MockDialects.from(dialect);
            final Stmt stmt;
            stmt = d.insert(this, visible);
            return d.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            return _MockDialects.from(dialect).insert(this, visible);
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


    static abstract class RowSetInsertStatement implements Insert, _RowSetInsert, Insert._InsertSpec {

        final TableMeta<?> table;

        final List<FieldMeta<?>> fieldList;

        final List<FieldMeta<?>> childFieldList;

        final Map<FieldMeta<?>, Boolean> fieldMap;

        final RowSet rowSet;

        final RowSet childRowSet;

        RowSetInsertStatement(_Insert clause, RowSet rowSet) {
            this.table = clause.table();
            this.fieldList = clause.fieldList();
            this.fieldMap = clause.fieldMap();
            this.rowSet = rowSet;

            this.childFieldList = Collections.emptyList();
            this.childRowSet = null;
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

        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            final _Dialect d;
            d = _MockDialects.from(dialect);
            final Stmt stmt;
            stmt = d.insert(this, visible);
            return d.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            return _MockDialects.from(dialect).insert(this, visible);
        }

        /**
         * <p>
         * This method is invoked after {@link  CriteriaContextStack#clearContextStack(CriteriaContext)}
         * and {@link  CriteriaContextStack#pop(CriteriaContext)}.
         * </p>
         */
        final void validateStatement() {
            final int size;
            size = this.fieldList.size();
            if (size == 0) {

            }
            final RowSet rowSet = this.rowSet;
            if (rowSet == null) {
                throw _Exceptions.castCriteriaApi();
            }
            final int selectionSize;
            selectionSize = CriteriaUtils.selectionCount(rowSet);
            if (selectionSize != size) {

            }

            final TableMeta<?> table = this.table;
            if (table instanceof SingleTableMeta) {

            }
        }


    }//RowSetInsertStatement


    static CriteriaException notContainField(FieldMeta<?> field) {
        return CriteriaContextStack.criteriaError(String.format("insert field list don't contain %s", field));
    }

    static CriteriaException duplicationValuePair(FieldMeta<?> field) {
        return CriteriaContextStack.criteriaError(String.format("duplication value of %s at same row.", field));
    }

    private static CriteriaException nonDomainInstance(@Nullable Object domain, TableMeta<?> table) {
        String m;
        m = String.format("%s isn't %s instance.", _ClassUtils.safeClassName(domain), table.javaType().getName());
        return CriteriaContextStack.criteriaError(m);
    }


}
