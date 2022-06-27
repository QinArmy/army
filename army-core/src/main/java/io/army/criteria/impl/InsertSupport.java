package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._DomainInsert;
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

        ColumnsClause(CriteriaContext criteriaContext, boolean migration, TableMeta<?> table) {
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

            if ((fieldList == null || fieldList.size() == 0)
                    && (childFieldList == null || childFieldList.size() == 0)) {
                throw CriteriaContextStack.criteriaError("column list must non-empty");
            }

            if (fieldList == null) {
                this.fieldList = Collections.emptyList();
            } else if (fieldList.size() == 1) {
                this.fieldList = Collections.singletonList(fieldList.get(0));
            } else {
                this.fieldList = Collections.unmodifiableList(fieldList);
            }

            if (childFieldList == null) {
                this.childFieldList = Collections.emptyList();
            } else if (childFieldList.size() == 1) {
                this.childFieldList = Collections.singletonList(childFieldList.get(0));
            } else {
                this.childFieldList = Collections.unmodifiableList(childFieldList);
            }
            return this.columnListEnd();
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
            }
            return fieldList;
        }

        @Override
        public final List<FieldMeta<?>> childFieldList() {
            List<FieldMeta<?>> childFieldList = this.childFieldList;
            if (childFieldList == null) {
                childFieldList = Collections.emptyList();
                this.childFieldList = childFieldList;
            }
            return childFieldList;
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
        }

        abstract RR columnListEnd();


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

        }

    }//ColumnsClause


    /**
     * @param <F> must be {@code  FieldMeta<T>} or {@code  FieldMeta<? super T>}
     */
    @SuppressWarnings("unchecked")
    static abstract class CommonExpClause<C, F extends TableField, RR, CR> extends ColumnsClause<C, F, RR>
            implements Insert._CommonExpClause<C, F, CR>, _Insert._CommonExpInsert {


        final boolean preferLiteral;

        private Map<FieldMeta<?>, _Expression> commonExpMap;

        CommonExpClause(CriteriaContext criteriaContext, InsertOptions options, TableMeta<?> table) {
            super(criteriaContext, options.isMigration(), table);
            this.preferLiteral = options.isPreferLiteral();
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
            final Map<FieldMeta<?>, _Expression> map = this.commonExpMap;
            assert map != null;
            return map;
        }


        @Override
        public void clear() {
            super.clear();
            this.commonExpMap = null;
        }

        final void unmodifiedCommonExpMap() {
            final Map<FieldMeta<?>, _Expression> map = this.commonExpMap;
            if (map == null) {
                this.commonExpMap = Collections.emptyMap();
            } else {
                this.commonExpMap = Collections.unmodifiableMap(map);
            }
        }


    }//CommonExpClause


    @SuppressWarnings("unchecked")
    static abstract class DomainValueClause<C, T extends IDomain, F extends TableField, CR, VR>
            extends CommonExpClause<C, F, CR, CR> implements Insert._DomainValueClause<C, T, VR>, _DomainInsert {


        final NullHandleMode nullHandleMode;
        private List<IDomain> domainList;

        DomainValueClause(CriteriaContext criteriaContext, InsertOptions options, TableMeta<T> table) {
            super(criteriaContext, options, table);
            this.nullHandleMode = options.nullHandle();
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


    static abstract class SubQueryColumn<C, T extends IDomain, IR> implements Insert._SingleColumnListClause<C, T, IR>
            , Insert._SingleColumnClause<T, IR>, Statement._RightParenClause<IR> {

        final CriteriaContext criteriaContext;

        final TableMeta<T> table;

        final List<FieldMeta<?>> fieldList = new ArrayList<>();

        SubQueryColumn(CriteriaContext criteriaContext, TableMeta<T> table) {
            this.criteriaContext = criteriaContext;
            this.table = table;
        }

        @Override
        public final Statement._RightParenClause<IR> leftParen(Consumer<Consumer<FieldMeta<T>>> consumer) {
            consumer.accept(this::addField);
            return this;
        }

        @Override
        public final Statement._RightParenClause<IR> leftParen(BiConsumer<C, Consumer<FieldMeta<T>>> consumer) {
            consumer.accept(this.criteriaContext.criteria(), this::addField);
            return this;
        }

        @Override
        public final Insert._SingleColumnClause<T, IR> leftParen(FieldMeta<T> field) {
            this.addField(field);
            return this;
        }

        @Override
        public final Insert._SingleColumnClause<T, IR> comma(FieldMeta<T> field) {
            this.addField(field);
            return this;
        }

        private void addField(final FieldMeta<? super T> field) {
            if (!field.insertable()) {
                throw CriteriaContextStack.criteriaError(_Exceptions::nonInsertableField, field);
            }
            if (field.tableMeta() != this.table) {
                throw CriteriaContextStack.criteriaError(_Exceptions::unknownColumn, field);
            }
            this.fieldList.add(field);
        }


    }// SubQueryColumn


    static abstract class SubQueryClause<C, SR> implements Insert._SubQueryClause<C, SR>, Statement._RightParenClause<SR> {

        final CriteriaContext criteriaContext;

        final List<FieldMeta<?>> fieldList;

        SubQuery subQuery;

        SubQueryClause(CriteriaContext criteriaContext, List<FieldMeta<?>> fieldList) {
            this.criteriaContext = criteriaContext;
            this.fieldList = fieldList;
        }

        @Override
        public SR space(Supplier<? extends SubQuery> supplier) {
            return this.acceptSubQuery(supplier.get());
        }

        @Override
        public SR space(Function<C, ? extends SubQuery> function) {
            return this.acceptSubQuery(function.apply(this.criteriaContext.criteria()));
        }


        abstract SR endSubQuery();


        private SR acceptSubQuery(final @Nullable SubQuery subQuery) {
            CriteriaContextStack.assertNonNull(subQuery, "subQuery must non-null.");
            final int selectionCount;
            selectionCount = CriteriaUtils.selectionCount(subQuery);
            if (selectionCount != this.fieldList.size()) {
                String m = String.format("SubQuery selection list size[%s] and column list size[%s] not match."
                        , selectionCount, this.fieldList.size());
                throw CriteriaContextStack.criteriaError(m);
            }
            this.subQuery = subQuery;
            return this.endSubQuery();
        }


    }//SubQueryClause

    private static CriteriaException nonDomainInstance(@Nullable Object domain, TableMeta<?> table) {
        String m = String.format("%s isn't %s instance.", domain, table.javaType().getName());
        return CriteriaContextStack.criteriaError(m);
    }


}
