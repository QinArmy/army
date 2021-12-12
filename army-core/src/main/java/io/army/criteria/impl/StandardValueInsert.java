package io.army.criteria.impl;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ObjectWrapper;
import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util.ArrayUtils;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

final class StandardValueInsert<T extends IDomain, C> extends AbstractSQLDebug implements Insert
        , Insert.InsertSpec, Insert.InsertIntoSpec<T, C>, Insert.InsertValuesSpec<T, C>, Insert.InsertOptionSpec<T, C>
        , _ValuesInsert {

    static <T extends IDomain> StandardValueInsert<T, Void> create(TableMeta<T> table) {
        return new StandardValueInsert<>(table, null);
    }

    static <T extends IDomain, C> StandardValueInsert<T, C> create(TableMeta<T> table, C criteria) {
        Objects.requireNonNull(criteria);
        return new StandardValueInsert<>(table, criteria);
    }

    private static final Set<String> FIELD_NAMES = ArrayUtils.asSet(
            _MetaBridge.ID,
            _MetaBridge.CREATE_TIME,
            _MetaBridge.UPDATE_TIME,
            _MetaBridge.VERSION);

    private final TableMeta<T> table;

    private final C criteria;

    private boolean migration;

    private List<FieldMeta<?, ?>> fieldList;

    private List<FieldMeta<?, ?>> parentFieldList;

    private Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap;

    private List<ObjectWrapper> domainList;

    private boolean prepared;

    private StandardValueInsert(final TableMeta<T> table, @Nullable C criteria) {
        this.table = table;
        this.criteria = criteria;

    }

    /*################################## blow InsertOptionSpec method ##################################*/

    @Override
    public InsertOptionSpec<T, C> migration() {
        this.migration = true;
        return this;
    }

    /*################################## blow InsertIntoSpec method ##################################*/

    @Override
    public InsertValuesSpec<T, C> insertInto(Collection<FieldMeta<? super T, ?>> fieldMetas) {
        final List<FieldMeta<?, ?>> fieldList = new ArrayList<>(), parentFieldList;
        final TableMeta<?> table = this.table, parentTable;
        if (table instanceof ChildTableMeta) {
            parentTable = ((ChildTableMeta<?>) table).parentMeta();
            parentFieldList = new ArrayList<>();
        } else {
            parentTable = null;
            parentFieldList = Collections.emptyList();
        }
        TableMeta<?> belongOf;
        for (FieldMeta<? super T, ?> fieldMeta : fieldMetas) {
            belongOf = fieldMeta.tableMeta();
            if (belongOf == table) {
                fieldList.add(fieldMeta);
            } else if (belongOf == parentTable) {
                parentFieldList.add(fieldMeta);
            } else {
                throw _Exceptions.unknownField(fieldMeta);
            }
        }
        this.fieldList = fieldList;
        this.parentFieldList = parentFieldList;
        return this;
    }

    @Override
    public InsertValuesSpec<T, C> insertInto(Supplier<Collection<FieldMeta<? super T, ?>>> supplier) {
        this.insertInto(Objects.requireNonNull(supplier.get()));
        return this;
    }

    @Override
    public InsertValuesSpec<T, C> insertInto(Function<C, Collection<FieldMeta<? super T, ?>>> function) {
        this.insertInto(Objects.requireNonNull(function.apply(this.criteria)));
        return this;
    }

    @Override
    public InsertValuesSpec<T, C> insertInto(final TableMeta<T> table) {
        if (table != this.table) {
            throw _Exceptions.tableNotMatch(table, this.table);
        }
        this.parentFieldList = this.fieldList = null;
        return this;
    }


    /*################################## blow InsertValuesSpec method ##################################*/

    @Override
    public <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, F> fieldMeta, F value) {
        this.set(fieldMeta, SQLs.param(fieldMeta, value));
        return this;
    }

    @Override
    public <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, F> fieldMeta, Expression<F> value) {
        final TableMeta<?> table = this.table;
        if (fieldMeta == table.discriminator()) {
            throw new CriteriaException("field couldn't be discriminator.");
        } else if (!this.migration) {
            if (FIELD_NAMES.contains(fieldMeta.fieldName())) {
                String m = String.format("Non-migration,field couldn't in %s", FIELD_NAMES);
                throw new CriteriaException(m);
            } else if (table.generatorChain().contains(fieldMeta)) {
                String m = String.format("Non-migration,field couldn't be generator field of %s", table);
                throw new CriteriaException(m);
            } else if (table instanceof ChildTableMeta
                    && ((ChildTableMeta<?>) table).parentMeta().generatorChain().contains(fieldMeta)) {
                String m = String.format("Non-migration,field couldn't be generator field of %s"
                        , ((ChildTableMeta<?>) table).parentMeta());
                throw new CriteriaException(m);
            }
        }
        Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap = this.commonExpMap;
        if (commonExpMap == null) {
            commonExpMap = new HashMap<>();
            this.commonExpMap = commonExpMap;
        }
        commonExpMap.put(fieldMeta, (_Expression<?>) value);
        return this;
    }

    @Override
    public <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, F> fieldMeta, Function<C, Expression<F>> function) {
        this.set(fieldMeta, Objects.requireNonNull(function.apply(this.criteria)));
        return this;
    }

    @Override
    public <F> InsertValuesSpec<T, C> setDefault(final FieldMeta<? super T, F> fieldMeta) {
        this.set(fieldMeta, SQLs.defaultKeyWord());
        return this;
    }

    @Override
    public InsertSpec value(T domain) {
        this.domainList = Collections.singletonList(
                ObjectAccessorFactory.forBeanPropertyAccess(domain));
        return this;
    }

    @Override
    public InsertSpec values(List<T> domainList) {
        final List<ObjectWrapper> wrapperList = new ArrayList<>(domainList.size());
        for (IDomain domain : domainList) {
            wrapperList.add(ObjectAccessorFactory.forDomainPropertyAccess(domain, table));
        }
        this.domainList = wrapperList;
        return this;
    }

    @Override
    public InsertSpec value(Function<C, T> function) {
        this.value(function.apply(this.criteria));
        return this;
    }

    @Override
    public InsertSpec values(Function<C, List<T>> function) {
        this.values(function.apply(this.criteria));
        return this;
    }

    /*################################## blow InnerStandardInsert method ##################################*/

    @Override
    public TableMeta<?> table() {
        if (!this.prepared) {
            throw _Exceptions.nonPrepared(this);
        }
        return this.table;
    }

    @Override
    public List<FieldMeta<?, ?>> fieldList() {
        if (!this.prepared) {
            throw _Exceptions.nonPrepared(this);
        }
        return this.fieldList;
    }

    @Override
    public List<FieldMeta<?, ?>> parentFieldList() {
        return this.parentFieldList;
    }

    @Override
    public Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap() {
        return this.commonExpMap;
    }

    @Override
    public List<ObjectWrapper> domainList() {
        return this.domainList;
    }

    @Override
    public boolean migrationData() {
        return this.migration;
    }

    @Override
    public void clear() {
        this.fieldList = null;
        this.domainList = null;
        this.commonExpMap = null;
        this.prepared = false;
    }


    /*################################## blow InsertSpec method ##################################*/

    @Override
    public Insert asInsert() {
        if (this.prepared) {
            return this;
        }
        final List<FieldMeta<?, ?>> fieldList = this.fieldList, parentFieldList = this.parentFieldList;
        if (CollectionUtils.isEmpty(fieldList)) {
            this.fieldList = Collections.emptyList();
        } else {
            this.fieldList = Collections.unmodifiableList(fieldList);
        }

        if (CollectionUtils.isEmpty(parentFieldList)) {
            this.parentFieldList = Collections.emptyList();
        } else {
            this.parentFieldList = Collections.unmodifiableList(parentFieldList);
        }

        final List<ObjectWrapper> domainList = this.domainList;
        if (CollectionUtils.isEmpty(domainList)) {
            this.domainList = Collections.emptyList();
        } else {
            this.domainList = Collections.unmodifiableList(domainList);
        }

        final Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap = this.commonExpMap;
        if (CollectionUtils.isEmpty(commonExpMap)) {
            this.commonExpMap = Collections.emptyMap();
        } else {
            this.commonExpMap = Collections.unmodifiableMap(commonExpMap);
        }
        this.prepared = true;
        return this;
    }



    /*################################## blow SQLStatement method ##################################*/

    @Override
    public boolean prepared() {
        return this.prepared;
    }


}
