package io.army.criteria.impl;

import io.army.beans.DomainWrapper;
import io.army.criteria.Insert;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.criteria.impl.inner.InnerValuesInsert;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

final class StandardBatchInsert<T extends IDomain> extends AbstractSQLDebug implements Insert, Insert.InsertAble
        , Insert.BatchInsertIntoAble<T>, Insert.BatchInsertValuesAble<T>, Insert.BatchInsertOptionAble<T>
        , InnerStandardInsert, InnerValuesInsert {


    static <T extends IDomain> StandardBatchInsert<T> build(TableMeta<T> tableMeta) {
        return new StandardBatchInsert<>(tableMeta);
    }

    private final TableMeta<T> tableMeta;

    private boolean dataMigration;

    private List<FieldMeta<?, ?>> fieldList;

    private List<DomainWrapper> valueList;

    private boolean prepared;

    private StandardBatchInsert(TableMeta<T> tableMeta) {
        Assert.notNull(tableMeta, "tableMeta required");
        this.tableMeta = tableMeta;
    }

    @Override
    public BatchInsertOptionAble<T> dataMigration() {
        this.dataMigration = true;
        return this;
    }

    /*################################## blow BatchInsertIntoAble method ##################################*/

    @Override
    public final BatchInsertValuesAble<T> insertInto(Collection<FieldMeta<? super T, ?>> fieldMetaList) {
        this.fieldList = new ArrayList<>(fieldMetaList);
        return this;
    }

    @Override
    public final BatchInsertValuesAble<T> insertInto(Supplier<Collection<FieldMeta<? super T, ?>>> supplier) {
        this.fieldList = new ArrayList<>(supplier.get());
        return this;
    }

    @Override
    public final BatchInsertValuesAble<T> insertInto(TableMeta<T> tableMeta) {
        this.fieldList = new ArrayList<>(tableMeta.fieldCollection());
        return this;
    }

    /*################################## blow BatchInsertTableRouteAble method ##################################*/


    @Override
    public final InsertAble values(List<T> domainList) {
        this.valueList = StandardInsert.createDomainWrapper(this.tableMeta, domainList);
        return this;
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

    /*################################## blow InnerStandardBatchInsert method ##################################*/

    @Override
    public final List<DomainWrapper> valueList() {
        return this.valueList;
    }

    @Override
    public TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final String tableAlias() {
        return "";
    }

    @Override
    public final int tableIndex() {
        return -1;
    }

    @Override
    public final int databaseIndex() {
        return -1;
    }

    @Override
    public List<FieldMeta<?, ?>> fieldList() {
        return this.fieldList;
    }

    @Override
    public boolean migrationData() {
        return this.dataMigration;
    }

    @Override
    public final void clear() {
        this.fieldList = null;
        this.valueList = null;
        this.prepared = false;
    }
}
