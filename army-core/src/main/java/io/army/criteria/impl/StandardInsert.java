package io.army.criteria.impl;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ObjectWrapper;
import io.army.criteria.IPredicate;
import io.army.criteria.Insert;
import io.army.criteria.impl.inner._StandardInsert;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

class StandardInsert<T extends IDomain> extends AbstractSQLDebug implements Insert
        , Insert.InsertSpec, Insert.InsertIntoSpec<T>, Insert.InsertValuesSpec<T>, Insert.InsertOptionSpec<T>
        , _StandardInsert {

    static <T extends IDomain> StandardInsert<T> build(TableMeta<T> tableMeta) {
        return new StandardInsert<>(tableMeta);
    }


    private final TableMeta<T> tableMeta;

    private boolean dataMigration;

    private List<FieldMeta<?, ?>> fieldList;

    private List<ObjectWrapper> wrapperList;

    private boolean prepared;

     StandardInsert(TableMeta<T> tableMeta) {
        this.tableMeta = tableMeta;
    }

     /*################################## blow InsertOptionSpec method ##################################*/

    @Override
    public final InsertOptionSpec<T> migration() {
        this.dataMigration = true;
        return this;
    }

     /*################################## blow InsertIntoSpec method ##################################*/

     @Override
     public final InsertValuesSpec<T> insertInto(Collection<FieldMeta<? super T, ?>> fieldMetas) {
         this.fieldList = new ArrayList<>(fieldMetas);
         return this;
     }

     @Override
     public final InsertValuesSpec<T> insertInto(Supplier<Collection<FieldMeta<? super T, ?>>> supplier) {
         this.fieldList = new ArrayList<>(supplier.get());
         return this;
     }

     @Override
     public final InsertValuesSpec<T> insertInto(TableMeta<T> tableMeta) {
         Assert.isTrue(tableMeta == this.tableMeta
                 , () -> String.format("TableMeta[%s] and target[%s] not match.", tableMeta, this.tableMeta));

         this.fieldList = new ArrayList<>();
         if (tableMeta instanceof ChildTableMeta) {
             this.fieldList.addAll(((ChildTableMeta<?>) tableMeta).parentMeta().fieldCollection());
         }
         this.fieldList.addAll(tableMeta.fieldCollection());
         return this;
     }


     /*################################## blow InsertValuesSpec method ##################################*/


     @Override
     public final InsertSpec value(T domain) {
         this.wrapperList = Collections.singletonList(
                 ObjectAccessorFactory.forDomainPropertyAccess(domain, this.tableMeta));
         return this;
     }

     @Override
     public final InsertSpec values(List<T> domainList) {
         List<ObjectWrapper> wrapperList = new ArrayList<>(domainList.size());
         for (IDomain domain : domainList) {
             wrapperList.add(ObjectAccessorFactory.forDomainPropertyAccess(domain, tableMeta));
         }
         this.wrapperList = wrapperList;
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
    public final List<IPredicate> predicateList() {
        return Collections.emptyList();
    }

    @Override
    public final List<FieldMeta<?, ?>> fieldList() {
        return this.fieldList;
    }

    @Override
    public final List<ObjectWrapper> domainList() {
        return this.wrapperList;
    }

    @Override
    public final boolean migrationData() {
        return this.dataMigration;
    }

    @Override
    public final void clear() {
        this.fieldList = null;
        this.wrapperList = null;
        this.prepared = false;
    }
     /*################################## blow InsertSpec method ##################################*/

    @Override
    public final Insert asInsert() {
        if (this.prepared) {
            return this;
        }
        Assert.state(!CollectionUtils.isEmpty(this.fieldList), "fieldList is empty,error.");
        Assert.state(!CollectionUtils.isEmpty(this.wrapperList), "valueList is empty,error.");

        this.fieldList = Collections.unmodifiableList(this.fieldList);
        this.wrapperList = Collections.unmodifiableList(this.wrapperList);

        this.prepared = true;
        return this;
    }

    /*################################## blow SQLStatement method ##################################*/

    @Override
    public final boolean prepared() {
        return this.prepared;
    }
}
