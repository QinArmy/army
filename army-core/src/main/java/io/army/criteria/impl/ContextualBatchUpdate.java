package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadWrapper;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._BatchSingleUpdate;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.dialect._DialectUtils;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard batch domain update statement.
 * </p>
 *
 * @param <T> domain java type
 * @param <C> criteria java type used to dynamic update and sub query
 */
final class ContextualBatchUpdate<T extends IDomain, C> extends AbstractSQLDebug
        implements Update, Update.BatchSetSpec<T, C>, Update.BatchWhereSpec<T, C>
        , Update.BatchWhereAndSpec<C>, Update.BatchParamSpec<C>, Update.UpdateSpec
        , _BatchSingleUpdate {

    static BatchUpdateSpec<Void> create() {
        return new BatchUpdateSpecImpl<>(null);
    }

    static <C> BatchUpdateSpec<C> create(C criteria) {
        return new BatchUpdateSpecImpl<>(Objects.requireNonNull(criteria));
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

    private List<ReadWrapper> paramList = new ArrayList<>();

    private boolean prepared;

    private ContextualBatchUpdate(TableMeta<T> table, String tableAlias, @Nullable C criteria) {
        this.table = table;
        this.tableAlias = tableAlias;
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }


    /*################################## blow BatchSetSpec method ##################################*/

    @Override
    public <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> field, Expression<F> valueExp) {
        if (field.updateMode() == UpdateMode.IMMUTABLE) {
            throw _Exceptions.immutableField(field);
        }
        this.fieldList.add(field);
        this.valueExpList.add((_Expression<?>) valueExp);
        return this;
    }

    @Override
    public <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> field) {
        return this.set(field, SQLs.namedParam(field));
    }

    @Override
    public <F> BatchWhereSpec<T, C> setNull(FieldMeta<? super T, F> field) {
        if (!field.nullable()) {
            throw _Exceptions.immutableField(field);
        }
        return this.set(field, SQLs.nullWord());
    }

    @Override
    public <F> BatchWhereSpec<T, C> setDefault(FieldMeta<? super T, F> field) {
        return this.set(field, SQLs.defaultWord());
    }

    @Override
    public <F extends Number> BatchWhereSpec<T, C> setPlus(FieldMeta<? super T, F> field) {
        return this.set(field, field.plus(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public <F extends Number> BatchWhereSpec<T, C> setMinus(FieldMeta<? super T, F> field) {
        return this.set(field, field.minus(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public <F extends Number> BatchWhereSpec<T, C> setMultiply(FieldMeta<? super T, F> field) {
        return this.set(field, field.multiply(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public <F extends Number> BatchWhereSpec<T, C> setDivide(FieldMeta<? super T, F> field) {
        return this.set(field, field.divide(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public <F extends Number> BatchWhereSpec<T, C> setMod(FieldMeta<? super T, F> field) {
        return this.set(field, field.mod(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public <F> BatchWhereSpec<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> field) {
        if (test.test(this.criteria)) {
            this.set(field, SQLs.namedParam(field));
        }
        return this;
    }

    @Override
    public <F> BatchWhereSpec<T, C> ifSet(FieldMeta<? super T, F> filed, Function<C, Expression<F>> function) {
        final Expression<F> expression;
        expression = function.apply(this.criteria);
        if (expression != null) {
            this.set(filed, expression);
        }
        return this;
    }

    /*################################## blow BatchWhereSpec method ##################################*/


    @Override
    public BatchParamSpec<C> where(List<IPredicate> predicates) {
        final List<_Predicate> list = this.predicateList;
        for (IPredicate predicate : predicates) {
            list.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public BatchParamSpec<C> where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }

    @Override
    public BatchParamSpec<C> where(Supplier<List<IPredicate>> supplier) {
        return this.where(supplier.get());
    }

    @Override
    public BatchWhereAndSpec<C> where(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    /*################################## blow BatchWhereAndSpec method ##################################*/

    @Override
    public BatchWhereAndSpec<C> and(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }


    @Override
    public BatchWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    @Override
    public BatchWhereAndSpec<C> ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    /*################################## blow BatchNamedParamSpec method ##################################*/

    @Override
    public UpdateSpec paramMaps(final List<Map<String, Object>> mapList) {
        final List<ReadWrapper> namedParamList = this.paramList;
        for (Map<String, Object> map : mapList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(map));
        }
        return this;
    }

    @Override
    public UpdateSpec paramMaps(Function<C, List<Map<String, Object>>> function) {
        return this.paramMaps(function.apply(this.criteria));
    }

    @Override
    public UpdateSpec paramBeans(final List<Object> beanList) {
        List<ReadWrapper> namedParamList = this.paramList;
        for (Object bean : beanList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(bean));
        }
        return this;
    }

    @Override
    public UpdateSpec paramBeans(Function<C, List<Object>> function) {
        return this.paramBeans(function.apply(this.criteria));
    }

    /*################################## blow update method ##################################*/

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

        this.predicateList = CriteriaUtils.predicateList(this.predicateList);
        this.paramList = CriteriaUtils.namedParamList(this.paramList);

        this.prepared = true;
        return this;
    }

    /*################################## blow InnerStandardBatchUpdate method ##################################*/

    @Override
    public List<ReadWrapper> wrapperList() {
        _Assert.prepared(this.prepared);
        return this.paramList;
    }

    @Override
    public TableMeta<?> table() {
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
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
    public List<_Predicate> predicateList() {
        _Assert.prepared(this.prepared);
        return this.predicateList;
    }

    @Override
    public void clear() {
        this.prepared = false;

        this.fieldList = null;
        this.valueExpList = null;
        this.predicateList = null;
        this.paramList = null;

        this.databaseIndex = -1;
        this.tableIndex = -1;
    }


    private static final class BatchUpdateSpecImpl<C> implements BatchUpdateSpec<C> {

        private final C criteria;

        private BatchUpdateSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public <T extends IDomain> BatchSetSpec<T, C> update(final TableMeta<T> table, final String tableAlias) {
            _DialectUtils.validateUpdateTableAlias(table, tableAlias);
            return new ContextualBatchUpdate<>(table, tableAlias, this.criteria);
        }

    }


}
