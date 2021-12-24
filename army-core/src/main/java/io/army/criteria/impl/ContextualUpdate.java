package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.dialect._DialectUtils;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>
 * This class representing standard single domain update statement.
 * </p>
 *
 * @param <T> domain java type
 * @param <C> criteria java type used to dynamic update and sub query
 */
final class ContextualUpdate<T extends IDomain, C> extends AbstractSQLDebug implements
        Update, Update.UpdateSpec, Update.WhereSpec<T, C>, Update.RouteSpec<T, C>
        , Update.WhereAndSpec<T, C>, _SingleUpdate {

    static DomainUpdateSpec<Void> create() {
        return new DomainUpdateSpecImpl<>(null);
    }

    static <C> DomainUpdateSpec<C> create(C criteria) {
        return new DomainUpdateSpecImpl<>(Objects.requireNonNull(criteria));
    }

    private final TableMeta<T> table;

    private final String tableAlias;

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private byte databaseIndex = -1;

    private byte tableIndex = -1;

    private List<FieldMeta<?, ?>> fieldList = new ArrayList<>();

    private List<_Expression<?>> valueExpList = new ArrayList<>();

    private List<_Predicate> predicateList = new ArrayList<>();


    private boolean prepared;

    private ContextualUpdate(TableMeta<T> table, String tableAlias, @Nullable C criteria) {
        _Assert.hasText(tableAlias, "tableAlias required");
        this.table = table;
        this.criteria = criteria;
        this.tableAlias = tableAlias;
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    /*################################## blow RouteSpec method ##################################*/

    @Override
    public SetSpec<T, C> route(int databaseIndex, int tableIndex) {
        this.databaseIndex = _Assert.databaseRoute(this.table, databaseIndex);
        this.tableIndex = _Assert.tableRoute(this.table, tableIndex);
        return this;
    }

    @Override
    public SetSpec<T, C> route(int tableIndex) {
        this.tableIndex = _Assert.tableRoute(this.table, tableIndex);
        return this;
    }

    @Override
    public SetSpec<T, C> routeAll() {
        this.databaseIndex = this.tableIndex = Byte.MIN_VALUE;
        return this;
    }

    /*################################## blow SetSpec method ##################################*/

    @Override
    public <F> WhereSpec<T, C> set(FieldMeta<? super T, F> field, @Nullable F value) {
        return this.set(field, SQLs.param(field, value));
    }

    @Override
    public <F> WhereSpec<T, C> set(final FieldMeta<? super T, F> field, final Expression<F> value) {
        if (field.updateMode() == UpdateMode.IMMUTABLE) {
            throw _Exceptions.immutableField(field);
        }
        final String fieldName = field.fieldName();
        if (fieldName.equals(_MetaBridge.UPDATE_TIME) || fieldName.equals(_MetaBridge.VERSION)) {
            throw _Exceptions.armyManageField(field);
        }
        if (!field.nullable() && ((_Expression<?>) value).nullableExp()) {
            throw _Exceptions.nonNullField(field);
        }
        this.fieldList.add(field);
        this.valueExpList.add((_Expression<?>) value);
        return this;
    }

    @Override
    public <F> WhereSpec<T, C> setNull(FieldMeta<? super T, F> field) {
        if (!field.nullable()) {
            throw _Exceptions.immutableField(field);
        }
        return this.set(field, SQLs.nullWord());
    }

    @Override
    public <F> WhereSpec<T, C> setDefault(FieldMeta<? super T, F> field) {
        return this.set(field, SQLs.defaultWord());
    }

    @Override
    public <F extends Number> WhereSpec<T, C> setPlus(FieldMeta<? super T, F> field, F value) {
        return this.set(field, field.plus(value));
    }

    @Override
    public <F extends Number> WhereSpec<T, C> setPlus(FieldMeta<? super T, F> field, Expression<F> value) {
        return this.set(field, field.plus(value));
    }

    @Override
    public <F extends Number> WhereSpec<T, C> setMinus(FieldMeta<? super T, F> field, F value) {
        return this.set(field, field.minus(value));
    }

    @Override
    public <F extends Number> WhereSpec<T, C> setMinus(FieldMeta<? super T, F> field, Expression<F> value) {
        return this.set(field, field.minus(value));
    }

    @Override
    public <F extends Number> WhereSpec<T, C> setMultiply(FieldMeta<? super T, F> field, F value) {
        return this.set(field, field.multiply(value));
    }

    @Override
    public <F extends Number> WhereSpec<T, C> setMultiply(FieldMeta<? super T, F> field, Expression<F> value) {
        return this.set(field, field.multiply(value));
    }

    @Override
    public <F extends Number> WhereSpec<T, C> setDivide(FieldMeta<? super T, F> field, F value) {
        return this.set(field, field.divide(value));
    }

    @Override
    public <F extends Number> WhereSpec<T, C> setDivide(FieldMeta<? super T, F> field, Expression<F> value) {
        return this.set(field, field.divide(value));
    }

    @Override
    public <F extends Number> WhereSpec<T, C> setMod(FieldMeta<? super T, F> field, F value) {
        return this.set(field, field.mod(value));
    }

    @Override
    public <F extends Number> WhereSpec<T, C> setMod(FieldMeta<? super T, F> field, Expression<F> value) {
        return this.set(field, field.mod(value));
    }

    @Override
    public <F> WhereSpec<T, C> ifSet(FieldMeta<? super T, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, SQLs.param(field, value));
        }
        return this;
    }

    @Override
    public <F> WhereSpec<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, @Nullable F value) {
        if (predicate.test(this.criteria)) {
            this.set(target, SQLs.param(target, value));
        }
        return this;
    }

    @Override
    public <F> WhereSpec<T, C> ifSet(FieldMeta<? super T, F> field
            , Function<C, Expression<F>> function) {
        final Expression<F> expression;
        expression = function.apply(this.criteria);
        if (expression != null) {
            this.set(field, expression);
        }
        return this;
    }

    @Override
    public <F extends Number> WhereSpec<T, C> ifSetPlus(FieldMeta<? super T, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, field.plus(value));
        }
        return this;
    }

    @Override
    public <F extends Number> WhereSpec<T, C> ifSetMinus(FieldMeta<? super T, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, field.minus(value));
        }
        return this;
    }

    @Override
    public <F extends Number> WhereSpec<T, C> ifSetMultiply(FieldMeta<? super T, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, field.multiply(value));
        }
        return this;
    }

    @Override
    public <F extends Number> WhereSpec<T, C> ifSetDivide(FieldMeta<? super T, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, field.divide(value));
        }
        return this;
    }

    @Override
    public <F extends Number> WhereSpec<T, C> ifSetMod(FieldMeta<? super T, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, field.mod(value));
        }
        return this;
    }

    @Override
    public <F extends Number> WhereSpec<T, C> ifSetPlus(Predicate<C> test, FieldMeta<? super T, F> field, F value) {
        if (test.test(this.criteria)) {
            this.set(field, field.plus(value));
        }
        return this;
    }

    @Override
    public <F extends Number> WhereSpec<T, C> ifSetMinus(Predicate<C> test, FieldMeta<? super T, F> field, F value) {
        if (test.test(this.criteria)) {
            this.set(field, field.minus(value));
        }
        return this;
    }

    @Override
    public <F extends Number> WhereSpec<T, C> ifSetMultiply(Predicate<C> test, FieldMeta<? super T, F> field, F value) {
        if (test.test(this.criteria)) {
            this.set(field, field.multiply(value));
        }
        return this;
    }

    @Override
    public <F extends Number> WhereSpec<T, C> ifSetDivide(Predicate<C> test, FieldMeta<? super T, F> field, F value) {
        if (test.test(this.criteria)) {
            this.set(field, field.divide(value));
        }
        return this;
    }

    @Override
    public <F extends Number> WhereSpec<T, C> ifSetMod(Predicate<C> test, FieldMeta<? super T, F> field, F value) {
        if (test.test(this.criteria)) {
            this.set(field, field.mod(value));
        }
        return this;
    }

    /*################################## blow DomainWhereSpec method ##################################*/


    @Override
    public UpdateSpec where(List<IPredicate> predicates) {
        final List<_Predicate> list = this.predicateList;
        for (IPredicate predicate : predicates) {
            list.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public UpdateSpec where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }

    @Override
    public WhereAndSpec<T, C> where(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    /*################################## blow WhereAndSpec method ##################################*/

    @Override
    public WhereAndSpec<T, C> and(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    @Override
    public WhereAndSpec<T, C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public WhereAndSpec<T, C> ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    /*################################## blow SQLStatement method ##################################*/

    @Override
    public void prepared() {
        _Assert.prepared(this.prepared);
    }

    /*################################## blow UpdateSpec method ##################################*/

    @Override
    public Update asUpdate() {
        _Assert.nonPrepared(this.prepared);

        CriteriaContextStack.clearContextStack(this.criteriaContext);

        final List<FieldMeta<?, ?>> fieldList = this.fieldList;
        final List<_Expression<?>> valueExpList = this.valueExpList;
        if (fieldList.size() != valueExpList.size()) {
            throw _Exceptions.updateFieldExpNotMatch();
        }
        switch (fieldList.size()) {
            case 0:
                throw _Exceptions.noUpdateField(this);
            case 1: {
                this.fieldList = Collections.singletonList(fieldList.get(0));
                this.valueExpList = Collections.singletonList(valueExpList.get(0));
            }
            break;
            default: {
                this.fieldList = Collections.unmodifiableList(fieldList);
                this.valueExpList = Collections.unmodifiableList(valueExpList);
            }
        }
        final List<_Predicate> predicateList = this.predicateList;
        switch (predicateList.size()) {
            case 0:
                throw new IllegalStateException("update no where clause.");
            case 1:
                this.predicateList = Collections.singletonList(predicateList.get(0));
                break;
            default:
                this.predicateList = Collections.unmodifiableList(predicateList);
        }
        this.prepared = true;
        return this;
    }

    /*################################## blow InnerStandardDomainUpdate method ##################################*/

    @Override
    public TableMeta<?> table() {
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public byte databaseIndex() {
        return this.databaseIndex;
    }

    @Override
    public byte tableIndex() {
        return this.tableIndex;
    }

    @Override
    public List<_Predicate> predicateList() {
        _Assert.prepared(this.prepared);
        return this.predicateList;
    }

    @Override
    public List<FieldMeta<?, ?>> fieldList() {
        return this.fieldList;
    }

    @Override
    public List<_Expression<?>> valueExpList() {
        return this.valueExpList;
    }

    @Override
    public void clear() {
        this.prepared = false;

        this.fieldList = null;
        this.valueExpList = null;
        this.predicateList = null;
        this.databaseIndex = -1;
        this.tableIndex = -1;
    }


    private static final class DomainUpdateSpecImpl<C> implements DomainUpdateSpec<C> {

        private final C criteria;

        private DomainUpdateSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public <T extends IDomain> RouteSpec<T, C> update(final TableMeta<T> table, final String tableAlias) {
            _DialectUtils.validateTableAlias(table, tableAlias);
            return new ContextualUpdate<>(table, tableAlias, this.criteria);
        }

    }


}

