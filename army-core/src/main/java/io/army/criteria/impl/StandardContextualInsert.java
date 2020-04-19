package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Insert;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.DomainValueWrapper;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.criteria.impl.inner.SubQueryValueWrapper;
import io.army.criteria.impl.inner.ValueWrapper;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.*;
import java.util.function.Function;

final class StandardContextualInsert<T extends IDomain, C> extends AbstractSQL implements Insert
        , Insert.InsertOptionAble<T, C>, Insert.InsertIntoAble<T, C>, Insert.InsertValuesAble<T, C>, Insert.InsertAble
        , InnerStandardInsert {

    private final CriteriaContext criteriaContext;

    private final C criteria;

    private boolean defaultIfNull;

    private Map<FieldMeta<?, ?>, Expression<?>> expFieldValueMap;

    private List<FieldMeta<?, ?>> fieldMetaList;

    private ValueWrapper valueWrapper;

    private boolean prepared;

    StandardContextualInsert(TableMeta<T> tableMeta, C criteria) {
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow InsertOptionAble method ##################################*/

    @Override
    public final <F> InsertOptionAble<T, C> commonValue(FieldMeta<T, F> fieldMeta, Expression<F> valueExp) {
        if (this.expFieldValueMap == null) {
            this.expFieldValueMap = new HashMap<>();
        }
        this.expFieldValueMap.put(fieldMeta, valueExp);
        return this;
    }

    @Override
    public final <F, S extends Expression<F>> InsertOptionAble<T, C> commonValue(FieldMeta<T, F> fieldMeta
            , Function<C, S> function) {
        if (this.expFieldValueMap == null) {
            this.expFieldValueMap = new HashMap<>();
        }
        this.expFieldValueMap.put(fieldMeta, function.apply(this.criteria));
        return this;
    }

    @Override
    public final InsertIntoAble<T, C> defaultIfNull() {
        this.defaultIfNull = true;
        return this;
    }

    /*################################## blow InsertIntoAble method ##################################*/

    @Override
    public final InsertValuesAble<T, C> insertInto(List<FieldMeta<T, ?>> fieldMetas) {
        if (this.fieldMetaList == null) {
            this.fieldMetaList = new ArrayList<>(fieldMetas.size());
        }
        this.fieldMetaList.addAll(fieldMetas);
        return this;
    }

    @Override
    public final InsertValuesAble<T, C> insertInto(TableMeta<T> tableMeta) {
        Collection<FieldMeta<T, ?>> fieldMetas = tableMeta.fieldCollection();
        if (this.fieldMetaList == null) {
            this.fieldMetaList = new ArrayList<>(fieldMetas.size());
        }
        this.fieldMetaList.addAll(fieldMetas);
        return this;
    }

    @Override
    public final InsertAble insertInto(T domain) {
        this.valueWrapper = (DomainValueWrapper) () -> Collections.singletonList(domain);
        return this;
    }

    @Override
    public final InsertAble batchInsertInto(List<T> domainList) {
        this.valueWrapper = (DomainValueWrapper) () -> Collections.unmodifiableList(domainList);
        return this;
    }

    /*################################## blow InsertValuesAble method ##################################*/

    @Override
    public final InsertAble values(Function<C, SubQuery> function) {
        final SubQuery subQuery = function.apply(this.criteria);
        this.valueWrapper = (SubQueryValueWrapper) () -> subQuery;
        return this;
    }

    @Override
    public final InsertAble values(List<T> domainList) {
        this.valueWrapper = (DomainValueWrapper) () -> Collections.unmodifiableList(domainList);
        return this;
    }

    /*################################## blow InsertAble method ##################################*/

    @Override
    public final Insert asInsert() {
        if (this.prepared) {
            return this;
        }
        Assert.state(valueWrapper != null, "valueWrapper is empty,error.");

        CriteriaContextHolder.clearContext(this.criteriaContext);
        this.criteriaContext.clear();

        if (this.expFieldValueMap == null) {
            this.expFieldValueMap = Collections.emptyMap();
        } else {
            this.expFieldValueMap = Collections.unmodifiableMap(this.expFieldValueMap);
        }
        if (this.fieldMetaList == null) {
            this.fieldMetaList = Collections.emptyList();
        } else {
            this.fieldMetaList = Collections.unmodifiableList(this.fieldMetaList);
        }
        this.prepared = true;
        return this;
    }

    /*################################## blow InnerStandardInsert method ##################################*/

    @Override
    public final boolean defaultValueIfNull() {
        return this.defaultIfNull;
    }

    @Override
    public final Map<FieldMeta<?, ?>, Expression<?>> expFieldValueMap() {
        return this.expFieldValueMap;
    }

    @Override
    public final List<FieldMeta<?, ?>> fieldMetaList() {
        return this.fieldMetaList;
    }

    @Override
    public final ValueWrapper valueWrapper() {
        return this.valueWrapper;
    }

    /*################################## blow package method ##################################*/

    @Override
    final boolean prepared() {
        return this.prepared;
    }

    @Override
    final void onAddTable(TableMeta<?> table, String tableAlias) {
        throw new UnsupportedOperationException();
    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        throw new UnsupportedOperationException();
    }

    @Override
    final int tableWrapperCount() {
        return 0;
    }

    @Override
    public final List<IPredicate> predicateList() {
        return Collections.emptyList();
    }

    @Override
    public final void clear() {
        Assert.state(this.prepared, "not invoke asInsert().");
        this.expFieldValueMap = null;
        this.fieldMetaList = null;
        this.valueWrapper = null;
    }
}
