package io.army.criteria.impl;

import io.army.beans.DomainWrapper;
import io.army.beans.ObjectAccessorFactory;
import io.army.criteria.Insert;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.*;
import java.util.function.Supplier;

final class StandardInsert<T extends IDomain> extends AbstractSQLDebug implements Insert
        , Insert.InsertAble, Insert.InsertIntoAble<T>, Insert.InsertValuesAble<T>, Insert.InsertOptionAble<T>
        , InnerStandardInsert {

    static <T extends IDomain> StandardInsert<T> build(TableMeta<T> tableMeta) {
        return new StandardInsert<>(tableMeta);
    }

    static Map<Integer, DomainWrapper> createDomainWrapper(TableMeta<?> tableMeta, List<? extends IDomain> domainList) {
        Map<Integer, DomainWrapper> wrapperMap = new HashMap<>(domainList.size());
        final int size = domainList.size();
        for (int i = 0; i < size; i++) {
            wrapperMap.put(i, ObjectAccessorFactory.forDomainPropertyAccess(domainList.get(i), tableMeta));
        }

        return wrapperMap;
    }

    private final TableMeta<T> tableMeta;

    private boolean dataMigration;

    private List<FieldMeta<?, ?>> fieldList;

    private Map<Integer, DomainWrapper> valueMap;

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
    public final InsertValuesAble<T> insertInto(Supplier<Collection<FieldMeta<? super T, ?>>> supplier) {
        this.fieldList = new ArrayList<>(supplier.get());
        return this;
    }

    @Override
    public final InsertValuesAble<T> insertInto(TableMeta<T> tableMeta) {
        Assert.isTrue(tableMeta == this.tableMeta
                , () -> String.format("TableMeta[%s] and target[%s] not match.", tableMeta, this.tableMeta));

        this.fieldList = new ArrayList<>();
        if (tableMeta instanceof ChildTableMeta) {
            this.fieldList.addAll(((ChildTableMeta<?>) tableMeta).parentMeta().fieldCollection());
        }
        this.fieldList.addAll(tableMeta.fieldCollection());
        return this;
    }


    /*################################## blow InsertValuesAble method ##################################*/


    @Override
    public final InsertAble value(T domain) {
        this.valueMap = Collections.singletonMap(0
                , ObjectAccessorFactory.forDomainPropertyAccess(domain, this.tableMeta));
        return this;
    }

    @Override
    public final InsertAble values(List<T> domainList) {
        this.valueMap = createDomainWrapper(this.tableMeta, domainList);
        return this;
    }

    /*################################## blow InnerStandardInsert method ##################################*/

    @Override
    public final TableMeta<?> tableMeta() {
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
    public final List<FieldMeta<?, ?>> fieldList() {
        return this.fieldList;
    }

    @Override
    public final Map<Integer, DomainWrapper> valueList() {
        return this.valueMap;
    }

    @Override
    public boolean migrationData() {
        return this.dataMigration;
    }

    @Override
    public void clear() {
        this.fieldList = null;
        this.valueMap = null;
        this.prepared = false;
    }
    /*################################## blow InsertAble method ##################################*/

    @Override
    public final Insert asInsert() {
        if (this.prepared) {
            return this;
        }
        Assert.state(!CollectionUtils.isEmpty(this.fieldList), "fieldList is empty,error.");
        Assert.state(!CollectionUtils.isEmpty(this.valueMap), "valueList is empty,error.");

        this.fieldList = Collections.unmodifiableList(this.fieldList);
        this.valueMap = Collections.unmodifiableMap(this.valueMap);

        this.prepared = true;
        return this;
    }

    /*################################## blow SQLStatement method ##################################*/

    @Override
    public final boolean prepared() {
        return this.prepared;
    }
}
