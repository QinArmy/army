package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.*;

final class StandardInsert<T extends IDomain> extends AbstractSQLDebug implements Insert
        , Insert.InsertAble, Insert.InsertOptionAble<T>, Insert.InsertIntoAble<T>, Insert.InsertValuesAble<T>
        , InnerStandardInsert {


    private final TableMeta<T> tableMeta;

    private boolean defaultExpIfNull;

    private boolean alwaysUseCommonExp;

    private Map<FieldMeta<?, ?>, Expression<?>> commonValueMap;

    private List<FieldMeta<?, ?>> fieldList;

    private List<IDomain> valueList;

    private boolean prepared;

    StandardInsert(TableMeta<T> tableMeta) {
        this.tableMeta = tableMeta;
    }

    /*################################## blow InsertOptionAble method ##################################*/

    @Override
    public final <F> InsertOptionAble<T> commonValue(FieldMeta<T, F> fieldMeta, Expression<F> valueExp) {
        if (this.commonValueMap == null) {
            this.commonValueMap = new HashMap<>();
        }
        this.commonValueMap.put(fieldMeta, valueExp);
        return this;
    }

    @Override
    public final InsertOptionAble<T> alwaysUseCommonValue() {
        this.alwaysUseCommonExp = true;
        return this;
    }

    @Override
    public final InsertIntoAble<T> defaultIfNull() {
        this.defaultExpIfNull = true;
        return this;
    }

    /*################################## blow private method ##################################*/

    @Override
    public final InsertValuesAble<T> insertInto(Collection<FieldMeta<T, ?>> fieldMetas) {
        this.fieldList = new ArrayList<>(fieldMetas);
        return this;
    }

    @Override
    public final InsertValuesAble<T> insertInto(TableMeta<T> tableMeta) {
        this.fieldList = new ArrayList<>(tableMeta.fieldCollection());
        return this;
    }

    @Override
    public final InsertAble insert(T domain) {
        this.valueList = new ArrayList<>(1);
        this.valueList.add(domain);
        return this;
    }

    @Override
    public final InsertAble insert(List<T> domainList) {
        this.valueList = new ArrayList<>(domainList);
        return this;
    }

    /*################################## blow InsertValuesAble method ##################################*/

    @Override
    public final InsertAble value(T domain) {
        this.valueList = new ArrayList<>(1);
        this.valueList.add(domain);
        return this;
    }

    @Override
    public final InsertAble values(List<T> domainList) {
        this.valueList = new ArrayList<>(domainList);
        return this;
    }

    /*################################## blow InnerStandardInsert method ##################################*/

    @Override
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final Map<FieldMeta<?, ?>, Expression<?>> commonValueMap() {
        return this.commonValueMap;
    }

    @Override
    public final boolean alwaysUseCommonExp() {
        return this.alwaysUseCommonExp;
    }

    @Override
    public final boolean defaultExpIfNull() {
        return this.defaultExpIfNull;
    }

    @Override
    public final List<FieldMeta<?, ?>> fieldList() {
        return this.fieldList;
    }

    @Override
    public final List<IDomain> valueList() {
        return this.valueList;
    }

    @Override
    public void clear() {
        Assert.state(this.prepared, "not invoke asInsert(),state error.");
        this.commonValueMap = null;
        this.fieldList = null;
        this.valueList = null;
    }
    /*################################## blow InsertAble method ##################################*/

    @Override
    public final Insert asInsert() {
        if (this.prepared) {
            return this;
        }
        Assert.state(!CollectionUtils.isEmpty(this.valueList), "valueList is empty,error.");

        if (this.commonValueMap == null) {
            this.commonValueMap = Collections.emptyMap();
        } else {
            this.commonValueMap = Collections.unmodifiableMap(this.commonValueMap);
        }
        if (this.fieldList == null) {
            this.fieldList = Collections.emptyList();
        } else {
            this.fieldList = Collections.unmodifiableList(this.fieldList);
        }
        this.valueList = Collections.unmodifiableList(this.valueList);

        this.prepared = true;
        return this;
    }


}
