package io.army.criteria.impl;

import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ObjectWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class ValueInsert<T extends IDomain, C, OR, IR, SR> extends AbstractInsert<T, C, IR> implements
        Insert.OptionClause<OR>, Insert.CommonExpClause<T, C, SR>, Insert.ValueClause<T, C>, _ValuesInsert {


    private boolean migration;

    private NullHandleMode nullHandleMode = NullHandleMode.INSERT_DEFAULT;

    private boolean prepared;

    private Map<FieldMeta<?>, _Expression> commonExpMap;

    private List<ObjectWrapper> wrapperList;

    ValueInsert(TableMeta<T> table, CriteriaContext criteriaContext) {
        super(table, criteriaContext);

    }

    @Override
    public final boolean isPrepared() {
        return this.prepared;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final OR migration() {
        this.migration = true;
        return (OR) this;
    }

    @Override
    public final OR nullHandle(NullHandleMode mode) {
        Objects.requireNonNull(mode);
        this.nullHandleMode = mode;
        return (OR) this;
    }

    @Override
    public final SR set(final FieldMeta<? super T> field, final @Nullable Object paramOrExp) {
        Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
        if (commonExpMap == null) {
            commonExpMap = new HashMap<>();
            this.commonExpMap = commonExpMap;
        }
        final Expression exp;
        if (paramOrExp == null) {
            exp = SQLs.nullWord();
        } else if (paramOrExp instanceof Expression) {
            exp = (Expression) paramOrExp;
        } else if (field.mappingType() instanceof _ArmyNoInjectionMapping) {
            exp = SQLs.literal(field, paramOrExp);
        } else {
            exp = SQLs.param(field, paramOrExp);
        }
        if (commonExpMap.putIfAbsent(field, (ArmyExpression) exp) != null) {
            String m = String.format("duplication common expression for %s.", field);
            throw new CriteriaException(m);
        }
        return (SR) this;
    }

    @Override
    public final SR set(FieldMeta<? super T> field, Function<C, Object> function) {
        return this.set(field, function.apply(this.criteria));
    }

    @Override
    public final SR set(FieldMeta<? super T> field, Supplier<Object> supplier) {
        return this.set(field, supplier.get());
    }

    @Override
    public final SR setDefault(FieldMeta<? super T> field) {
        return this.set(field, SQLs.defaultWord());
    }

    @Override
    public final SR setNull(FieldMeta<? super T> field) {
        return this.set(field, SQLs.nullWord());
    }


    @Override
    public final InsertSpec value(T domain) {
        this.wrapperList = Collections.singletonList(ObjectAccessorFactory.forBeanPropertyAccess(domain));
        return this;
    }

    @Override
    public final InsertSpec value(Function<C, T> function) {
        return this.value(function.apply(this.criteria));
    }

    @Override
    public final InsertSpec value(Supplier<T> supplier) {
        return this.value(supplier.get());
    }

    @Override
    public final InsertSpec value(Function<String, Object> function, String keyName) {
        final Object domain;
        domain = function.apply(keyName);
        if (domain == null || domain.getClass() != this.table.javaType()) {
            throw domainTypeNotMatch(domain);
        }
        return this.value((T) domain);
    }

    @Override
    public final InsertSpec values(List<T> domainList) {
        final List<ObjectWrapper> wrapperList = new ArrayList<>(domainList.size());
        for (T domain : domainList) {
            wrapperList.add(ObjectAccessorFactory.forBeanPropertyAccess(domain));
        }
        this.wrapperList = Collections.unmodifiableList(wrapperList);
        return this;
    }

    @Override
    public final InsertSpec values(Function<C, List<T>> function) {
        return this.values(function.apply(this.criteria));
    }

    @Override
    public final InsertSpec values(Supplier<List<T>> supplier) {
        return this.values(supplier.get());
    }

    @Override
    public final InsertSpec values(Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (!(value instanceof List)) {
            String m = String.format("%s return isn't %s.", Function.class.getName(), List.class.getName());
            throw new CriteriaException(m);
        }
        final List<?> domainList = (List<?>) value;
        final Class<T> javaType = this.table.javaType();

        final List<ObjectWrapper> wrapperList = new ArrayList<>(domainList.size());
        for (Object domain : domainList) {
            if (domain == null || domain.getClass() != javaType) {
                throw domainTypeNotMatch(domain);
            }
            wrapperList.add(ObjectAccessorFactory.forBeanPropertyAccess(domain));
        }
        this.wrapperList = Collections.unmodifiableList(wrapperList);
        return this;
    }

    @Override
    public final Insert asInsert() {
        _Assert.nonPrepared(this.prepared);

        if (this instanceof WithElement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        if (CollectionUtils.isEmpty(this.wrapperList)) {
            throw _Exceptions.castCriteriaApi();
        }
        final Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
        if (CollectionUtils.isEmpty(commonExpMap)) {
            this.commonExpMap = Collections.emptyMap();
        } else {
            this.commonExpMap = Collections.unmodifiableMap(commonExpMap);
        }
        this.onAsInsert();
        this.prepared = true;
        return this;
    }

    @Override
    public final boolean isMigration() {
        return this.migration;
    }

    @Override
    public final NullHandleMode nullHandle() {
        final NullHandleMode mode = this.nullHandleMode;
        assert mode != null;
        return mode;
    }

    @Override
    public final Map<FieldMeta<?>, _Expression> commonExpMap() {
        prepared();
        return this.commonExpMap;
    }

    @Override
    public final List<ObjectWrapper> domainList() {
        prepared();
        return this.wrapperList;
    }

    @Override
    public final void clear() {
        super.clear();
        this.prepared = false;
        this.migration = false;
        this.commonExpMap = null;
        this.wrapperList = null;
        this.onClear();
    }

    void onClear() {

    }

    void onAsInsert() {

    }

    private CriteriaException domainTypeNotMatch(@Nullable Object domain) {
        final String m;
        if (domain == null) {
            m = String.format("null isn't %s type.", this.table.javaType().getName());
        } else {
            m = String.format("%s isn't %s type.", domain.getClass().getName(), this.table.javaType().getName());
        }
        return new CriteriaException(m);
    }


}
