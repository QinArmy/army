package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Update;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.modelgen._MetaBridge;
import io.army.util.CollectionUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class BatchUpdate<T extends IDomain, C, WR, WA, SR, BR> extends QueryDmlStatement<C, WR, WA>
        implements Update, Update.UpdateSpec, Update.BatchSetClause<T, C, SR>,
        Statement.BatchParamClause<C, BR>, _Update, _BatchDml {

    final CriteriaContext criteriaContext;

    List<SetTargetPart> targetPartList = new ArrayList<>();

    List<SetValuePart> valuePartList = new ArrayList<>();

    private List<ReadWrapper> paramList = new ArrayList<>();

    BatchUpdate(@Nullable C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        if (this instanceof WithElement) {
            CriteriaContextStack.push(this.criteriaContext);
        } else {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }
    }


    @Override
    public final SR set(List<FieldMeta<?, ?>> fieldList) {
        if (fieldList.size() == 0) {
            throw _Exceptions.updateFieldListEmpty();
        }
        for (FieldMeta<?, ?> field : fieldList) {
            this.set(field, SQLs.namedParam(field));
        }
        return (SR) this;
    }

    @Override
    public final <F> SR set(FieldMeta<?, F> field, Expression<F> value) {
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
        this.targetPartList.add(field);
        this.valuePartList.add((_Expression<?>) value);
        return (SR) this;
    }

    @Override
    public final <F> SR set(FieldMeta<?, F> field) {
        return this.set(field, SQLs.namedParam(field));
    }

    @Override
    public final <F> SR setNull(FieldMeta<?, F> field) {
        return this.set(field, SQLs.nullWord());
    }

    @Override
    public final <F> SR setDefault(FieldMeta<?, F> field) {
        return this.set(field, SQLs.defaultWord());
    }

    @Override
    public final <F extends Number> SR setPlus(FieldMeta<?, F> field) {
        return this.set(field, field.plus(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public final <F extends Number> SR setMinus(FieldMeta<?, F> field) {
        return this.set(field, field.minus(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public final <F extends Number> SR setMultiply(FieldMeta<?, F> field) {
        return this.set(field, field.multiply(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public final <F extends Number> SR setDivide(FieldMeta<?, F> field) {
        return this.set(field, field.divide(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public final <F extends Number> SR setMod(FieldMeta<?, F> field) {
        return this.set(field, field.mod(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public final SR ifSet(Function<C, List<FieldMeta<?, ?>>> function) {
        final List<FieldMeta<?, ?>> fieldList;
        fieldList = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(fieldList)) {
            this.set(fieldList);
        }
        return (SR) this;
    }

    @Override
    public final <F> SR ifSet(Predicate<C> test, FieldMeta<?, F> field) {
        if (test.test(this.criteria)) {
            this.set(field, SQLs.namedParam(field));
        }
        return (SR) this;
    }

    @Override
    public final <F> SR ifSet(FieldMeta<?, F> filed, Function<C, Expression<F>> function) {
        final Expression<F> expression;
        expression = function.apply(this.criteria);
        if (expression != null) {
            this.set(filed, expression);
        }
        return (SR) this;
    }


    @Override
    public final BR paramMaps(List<Map<String, Object>> mapList) {
        if (mapList.size() == 0) {
            throw _Exceptions.batchParamEmpty();
        }
        final List<ReadWrapper> namedParamList = this.paramList;
        for (Map<String, Object> map : mapList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(map));
        }
        return (BR) this;
    }

    @Override
    public final BR paramMaps(Supplier<List<Map<String, Object>>> supplier) {
        return this.paramMaps(supplier.get());
    }

    @Override
    public final BR paramMaps(Function<C, List<Map<String, Object>>> function) {
        return this.paramMaps(function.apply(this.criteria));
    }

    @Override
    public final BR paramBeans(List<Object> beanList) {
        if (beanList.size() == 0) {
            throw _Exceptions.batchParamEmpty();
        }
        final List<ReadWrapper> namedParamList = this.paramList;
        for (Object bean : beanList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(bean));
        }
        return (BR) this;
    }

    @Override
    public final BR paramBeans(Supplier<List<Object>> supplier) {
        return this.paramBeans(supplier.get());
    }

    @Override
    public final BR paramBeans(Function<C, List<Object>> function) {
        return this.paramBeans(function.apply(this.criteria));
    }


    @Override
    public final List<ReadWrapper> wrapperList() {
        _Assert.prepared(this.prepared);
        return this.paramList;
    }

    @Override
    public final List<? extends SetTargetPart> fieldList() {
        _Assert.prepared(this.prepared);
        return this.targetPartList;
    }

    @Override
    public final List<? extends SetValuePart> valueExpList() {
        return this.valuePartList;
    }

    @Override
    public final Update asUpdate() {
        _Assert.nonPrepared(this.prepared);

        if (this instanceof WithElement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        final List<SetTargetPart> targetParts = this.targetPartList;
        final List<SetValuePart> valueParts = this.valuePartList;
        if (CollectionUtils.isEmpty(targetParts)) {
            throw _Exceptions.updateFieldListEmpty();
        }
        if (targetParts.size() != valueParts.size()) {
            // no bug ,never here
            throw new IllegalStateException("target and value size not match.");
        }
        this.targetPartList = CollectionUtils.unmodifiableList(targetParts);
        this.valuePartList = CollectionUtils.asUnmodifiableList(valueParts);

        final List<_Predicate> predicates = this.predicateList;
        if (CollectionUtils.isEmpty(predicates)) {
            throw _Exceptions.dmlNoWhereClause();
        }
        this.predicateList = CollectionUtils.asUnmodifiableList(predicates);

        final List<ReadWrapper> paramList = this.paramList;
        if (CollectionUtils.isEmpty(paramList)) {
            throw _Exceptions.batchParamEmpty();
        }
        this.paramList = CollectionUtils.unmodifiableList(paramList);

        this.onAsUpdate();

        this.prepared = true;
        return this;
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = false;

        this.targetPartList = null;
        this.valuePartList = null;
        this.predicateList = null;
        this.paramList = null;

        this.onClear();

    }


    void onAsUpdate() {

    }


}
