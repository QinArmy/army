package io.army.boot;

import io.army.beans.DomainReadonlyWrapper;
import io.army.cache.DomainUpdateAdvice;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Update;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.InnerStandardUpdate;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

final class CacheDomainUpdate implements Update, InnerStandardUpdate {

    static CacheDomainUpdate build(DomainUpdateAdvice advice) {

        final Set<FieldMeta<?, ?>> set = advice.targetFieldSet();

        List<FieldMeta<?, ?>> targetList = new ArrayList<>(set.size());
        List<Expression<?>> valueList = new ArrayList<>(targetList.size());
        DomainReadonlyWrapper readonlyWrapper = advice.readonlyWrapper();

        for (FieldMeta<?, ?> fieldMeta : set) {
            targetList.add(fieldMeta);
            Object value = readonlyWrapper.getPropertyValue(fieldMeta.propertyName());
            if (value == null) {
                valueList.add(SQLS.asNull(fieldMeta.mappingMeta()));
            } else {
                valueList.add(SQLS.param(value, fieldMeta.mappingMeta()));
            }
        }
        return new CacheDomainUpdate(advice, targetList, valueList);
    }

    private final TableMeta<?> tableMeta;

    private final String tableAlias;

    private List<FieldMeta<?, ?>> targetFieldList;

    private List<Expression<?>> valueExpList;

    private List<IPredicate> predicateList;

    private CacheDomainUpdate(DomainUpdateAdvice advice, List<FieldMeta<?, ?>> targetFieldList
            , List<Expression<?>> valueExpList) {

        this.tableMeta = advice.readonlyWrapper().tableMeta();
        this.tableAlias = "t";

        this.targetFieldList = Collections.unmodifiableList(targetFieldList);
        this.valueExpList = Collections.unmodifiableList(valueExpList);
        this.predicateList = advice.predicateList();
    }


    @Override
    public TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public List<FieldMeta<?, ?>> targetFieldList() {
        return this.targetFieldList;
    }

    @Override
    public List<Expression<?>> valueExpList() {
        return this.valueExpList;
    }

    @Override
    public List<IPredicate> predicateList() {
        return this.predicateList;
    }

    @Override
    public void clear() {
        this.targetFieldList = null;
        this.valueExpList = null;
        this.predicateList = null;
    }

    @Override
    public boolean prepared() {
        return true;
    }
}
