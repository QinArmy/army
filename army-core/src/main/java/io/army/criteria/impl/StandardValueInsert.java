package io.army.criteria.impl;

import io.army.DialectMode;
import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ObjectWrapper;
import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.dialect._DmlUtils;
import io.army.dialect._MockDialects;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.PairStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.util.CollectionUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard value insert statement.
 * </p>
 *
 * @param <T> domain java type.
 * @param <C> criteria java type used to dynamic statement.
 */
final class StandardValueInsert<T extends IDomain, C> implements Insert
        , Insert.InsertSpec, Insert.InsertIntoSpec<T, C>, Insert.InsertValuesSpec<T, C>, Insert.InsertOptionSpec<T, C>
        , _ValuesInsert {


    static <T extends IDomain, C> StandardValueInsert<T, C> create(TableMeta<T> table, @Nullable C criteria) {
        return new StandardValueInsert<>(table, criteria);
    }

    private final TableMeta<T> table;

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private boolean migration;

    private List<FieldMeta<?, ?>> fieldList;

    private Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap;

    private List<ObjectWrapper> domainList;

    private boolean prepared;

    private StandardValueInsert(final TableMeta<T> table, @Nullable C criteria) {
        this.table = table;
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextStack.setContextStack(this.criteriaContext);
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
        this.insertInto(function.apply(this.criteria));
        return this;
    }

    @Override
    public InsertValuesSpec<T, C> insertInto(Supplier<Collection<FieldMeta<? super T, ?>>> supplier) {
        this.insertInto(supplier.get());
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
    public InsertValuesSpec<T, C> set(FieldMeta<? super T, ?> field, @Nullable Object value) {
        return this.set(field, SQLs.paramWithExp(field, value));
    }


    @Override
    public InsertValuesSpec<T, C> set(FieldMeta<? super T, ?> field, Expression<?> value) {
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
    public <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, ?> field, Function<C, Expression<F>> function) {
        return this.set(field, function.apply(this.criteria));
    }

    @Override
    public <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, ?> field, Supplier<Expression<F>> supplier) {
        return this.set(field, supplier.get());
    }

    @Override
    public InsertValuesSpec<T, C> setDefault(final FieldMeta<? super T, ?> field) {
        return this.set(field, SQLs.defaultWord());
    }

    @Override
    public InsertValuesSpec<T, C> setNull(FieldMeta<? super T, ?> field) {
        return this.set(field, SQLs.nullWord());
    }

    @Override
    public InsertSpec value(T domain) {
        this.domainList = Collections.singletonList(
                ObjectAccessorFactory.forBeanPropertyAccess(domain));
        return this;
    }

    @Override
    public InsertSpec value(Supplier<T> supplier) {
        return this.value(supplier.get());
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
        return this.value(function.apply(this.criteria));
    }

    @Override
    public InsertSpec values(Function<C, List<T>> function) {
        return this.values(function.apply(this.criteria));
    }

    @Override
    public InsertSpec values(Supplier<List<T>> supplier) {
        return this.values(supplier.get());
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
        _Assert.nonPrepared(this.prepared);

        CriteriaContextStack.clearContextStack(this.criteriaContext);

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

    @Override
    public String toString() {
        final String s;
        if (this.prepared) {
            s = this.mockAsString(DialectMode.MySQL57);
        } else {
            s = super.toString();
        }
        return s;
    }

    @Override
    public void mock(DialectMode mode) {
        System.out.println(mockAsString(mode));
    }

    @Override
    public String mockAsString(DialectMode mode) {
        final Stmt stmt;
        stmt = mockAsStmt(mode);
        final StringBuilder builder = new StringBuilder();
        if (stmt instanceof SimpleStmt) {
            builder.append("insert sql:\n")
                    .append(((SimpleStmt) stmt).sql());
        } else if (stmt instanceof PairStmt) {
            builder.append("parent insert sql:\n")
                    .append(((PairStmt) stmt).parentStmt().sql())
                    .append("child insert sql:\n")
                    .append(((PairStmt) stmt).childStmt().sql());
        } else {
            throw new IllegalStateException("Unknown stmt type.");
        }
        return builder.toString();
    }

    @Override
    public Stmt mockAsStmt(DialectMode mode) {
        return _MockDialects.from(mode).insert(this, Visible.ONLY_VISIBLE);
    }

    /*################################## blow SQLStatement method ##################################*/

    @Override
    public void prepared() {
        _Assert.prepared(this.prepared);
    }


}
