package io.army.criteria.impl;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ObjectWrapper;
import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.dialect._DmlUtils;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;

/**
 * <p>
 * This class representing standard value insert statement.
 * </p>
 *
 * @param <T> domain java type.
 * @param <C> criteria java type used to dynamic statement.
 */
final class ContextualValueInsert<T extends IDomain, C> extends AbstractSQLDebug implements Insert
        , Insert.InsertSpec, Insert.InsertIntoSpec<T, C>, Insert.InsertValuesSpec<T, C>, Insert.InsertOptionSpec<T, C>
        , _ValuesInsert {

    static <T extends IDomain> ContextualValueInsert<T, Void> create(TableMeta<T> table) {
        return new ContextualValueInsert<>(table, null);
    }

    static <T extends IDomain, C> ContextualValueInsert<T, C> create(TableMeta<T> table, C criteria) {
        Objects.requireNonNull(criteria);
        return new ContextualValueInsert<>(table, criteria);
    }

    private final TableMeta<T> table;

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private boolean migration;

    private List<FieldMeta<?, ?>> fieldList;

    private Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap;

    private List<ObjectWrapper> domainList;

    private boolean prepared;

    private ContextualValueInsert(final TableMeta<T> table, @Nullable C criteria) {
        this.table = table;
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow InsertOptionSpec method ##################################*/

    @Override
    public InsertOptionSpec<T, C> migration() {
        this.migration = true;
        return this;
    }

    /*################################## blow InsertIntoSpec method ##################################*/

    @Override
    public InsertValuesSpec<T, C> insertInto(Collection<FieldMeta<? super T, ?>> fields) {
        final List<FieldMeta<?, ?>> fieldList = new ArrayList<>(fields.size());
        final TableMeta<?> table = this.table, parentTable;
        if (table instanceof ChildTableMeta) {
            parentTable = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            parentTable = null;
        }
        TableMeta<?> belongOf;
        for (FieldMeta<? super T, ?> fieldMeta : fields) {
            if (!fieldMeta.insertable()) {
                throw _Exceptions.nonInsertableField(fieldMeta);
            }
            belongOf = fieldMeta.tableMeta();
            if (belongOf == table || belongOf == parentTable) {
                fieldList.add(fieldMeta);
            } else {
                throw _Exceptions.unknownField(fieldMeta);
            }
        }
        this.fieldList = fieldList;
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
        this.fieldList = null;
        return this;
    }


    /*################################## blow InsertValuesSpec method ##################################*/

    @Override
    public <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, F> fieldMeta, @Nullable F value) {
        this.set(fieldMeta, SQLs.param(fieldMeta, value));
        return this;
    }

    @Override
    public <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, F> field, Expression<F> value) {
        _DmlUtils.checkInsertExpField(this.table, field, (_Expression<?>) value);

        Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap = this.commonExpMap;
        if (commonExpMap == null) {
            commonExpMap = new HashMap<>();
            this.commonExpMap = commonExpMap;
        }
        commonExpMap.put(field, (_Expression<?>) value);
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
        Assert.nonPrepared(this.prepared);

        CriteriaContextHolder.clearContext(this.criteriaContext);

        final List<FieldMeta<?, ?>> fieldList = this.fieldList;
        if (CollectionUtils.isEmpty(fieldList)) {
            this.fieldList = Collections.emptyList();
        } else {
            this.fieldList = Collections.unmodifiableList(fieldList);
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
    public void prepared() {
        Assert.prepared(this.prepared);
    }


}
