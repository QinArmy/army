package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class StandardInsert<T extends IDomain> extends AbstractSQLDebug implements Insert
        , Insert.InsertAble, Insert.InsertIntoAble<T>, Insert.InsertValuesAble<T>, Insert.InsertOptionAble<T>
        , InnerStandardInsert {

    static <T extends IDomain> StandardInsert<T> build(TableMeta<T> tableMeta) {
        return new StandardInsert<>(tableMeta);
    }

    private final TableMeta<T> tableMeta;

    private boolean dataMigration;

    private List<FieldMeta<?, ?>> fieldList;

    private List<IDomain> valueList;

    private boolean prepared;

    private StandardInsert(TableMeta<T> tableMeta) {
        this.tableMeta = tableMeta;
    }

    /*################################## blow InsertOptionAble method ##################################*/

    @Override
    public InsertOptionAble<T> dataMigration() {
        this.dataMigration = true;
        return this;
    }

    /*################################## blow InsertIntoAble method ##################################*/

    @Override
    public final InsertValuesAble<T> insertInto(Collection<FieldMeta<? super T, ?>> fieldMetas) {
        this.fieldList = new ArrayList<>(fieldMetas);
        return this;
    }

    @Override
    public final InsertValuesAble<T> insertInto(TableMeta<T> tableMeta) {
        this.fieldList = new ArrayList<>(tableMeta.fieldCollection());
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
    public final List<FieldMeta<?, ?>> fieldList() {
        return this.fieldList;
    }

    @Override
    public final List<IDomain> valueList() {
        return this.valueList;
    }

    @Override
    public boolean migrationData() {
        return this.dataMigration;
    }

    @Override
    public void clear() {
        this.fieldList = null;
        this.valueList = null;
        this.prepared = false;
    }
    /*################################## blow InsertAble method ##################################*/

    @Override
    public final Insert asInsert() {
        if (this.prepared) {
            return this;
        }
        Assert.state(!CollectionUtils.isEmpty(this.fieldList), "fieldList is empty,error.");
        Assert.state(!CollectionUtils.isEmpty(this.valueList), "valueList is empty,error.");

        this.fieldList = Collections.unmodifiableList(this.fieldList);
        this.valueList = Collections.unmodifiableList(this.valueList);

        this.prepared = true;
        return this;
    }

    /*################################## blow SQLStatement method ##################################*/

    @Override
    public final boolean prepared() {
        return this.prepared;
    }
}
