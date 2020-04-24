package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.criteria.impl.inner.InnerStandardBatchInsert;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.*;

final class StandardBatchInsert<T extends IDomain> extends AbstractSQLDebug implements Insert, Insert.InsertAble
        , Insert.BatchInsertIntoAble<T>, Insert.BatchInsertValuesAble<T>
        , InnerStandardBatchInsert {

    private final TableMeta<T> tableMeta;

    private boolean ignoreGenerateValueIfCrash;

    private Map<FieldMeta<?, ?>, Expression<?>> commonValueMap;

    private List<FieldMeta<?, ?>> fieldList;

    private List<IDomain> valueList;

    private boolean prepared;

    StandardBatchInsert(TableMeta<T> tableMeta) {
        Assert.notNull(tableMeta, "tableMeta required");
        this.tableMeta = tableMeta;
    }


    /*################################## blow BatchInsertIntoAble method ##################################*/

    @Override
    public final BatchInsertValuesAble<T> insertInto(Collection<FieldMeta<? super T, ?>> fieldMetaList) {
        this.fieldList = new ArrayList<>(fieldMetaList);
        return this;
    }

    @Override
    public final BatchInsertValuesAble<T> insertInto(TableMeta<T> tableMeta) {
        this.fieldList = new ArrayList<>(tableMeta.fieldCollection());
        return this;
    }

    /*################################## blow BatchInsertValuesAble method ##################################*/

    @Override
    public final InsertAble values(List<T> domainList) {
        this.valueList = new ArrayList<>(domainList);
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

        if (this.commonValueMap == null) {
            this.commonValueMap = Collections.emptyMap();
        } else {
            this.commonValueMap = Collections.unmodifiableMap(this.commonValueMap);
        }
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
    public final Map<FieldMeta<?, ?>, Expression<?>> commonValueMap() {
        return this.commonValueMap;
    }

    @Override
    public final boolean ignoreGenerateValueIfCrash() {
        return this.ignoreGenerateValueIfCrash;
    }

    @Override
    public final List<IDomain> valueList() {
        return this.valueList;
    }

    @Override
    public TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public List<FieldMeta<?, ?>> fieldList() {
        return this.fieldList;
    }

    @Override
    public final void clear() {
        Assert.state(this.prepared, "");
        this.commonValueMap = null;
        this.fieldList = null;
        this.valueList = null;
    }
}
