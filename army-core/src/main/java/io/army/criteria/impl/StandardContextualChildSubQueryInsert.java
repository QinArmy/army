package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.InnerStandardChildSubQueryInsert;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

final class StandardContextualChildSubQueryInsert<T extends IDomain, C> extends AbstractSQLDebug implements Insert
        , InnerStandardChildSubQueryInsert, Insert.ParentSubQueryTargetFieldAble<T, C>, Insert.ParentSubQueryAble<T, C>
        , Insert.ChildSubQueryTargetFieldAble<T, C>, Insert.ChildSubQueryAble<C>, Insert.InsertAble {

    static <T extends IDomain, C> StandardContextualChildSubQueryInsert<T, C> build(
            ChildTableMeta<T> tableMeta, C criteria) {
        return new StandardContextualChildSubQueryInsert<>(tableMeta, criteria);
    }


    private final ChildTableMeta<T> tableMeta;

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private List<FieldMeta<?, ?>> parentFieldList;

    private SubQuery parentSubQuery;

    private List<FieldMeta<?, ?>> childFieldList;

    private SubQuery childSubQuery;


    private boolean prepared;

    private StandardContextualChildSubQueryInsert(ChildTableMeta<T> tableMeta, C criteria) {
        Assert.notNull(tableMeta, "tableMeta required");
        Assert.notNull(criteria, "criteria required");

        this.tableMeta = tableMeta;
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow ParentSubQueryTargetFieldAble method ##################################*/

    @Override
    public ParentSubQueryAble<T, C> parentFields(List<FieldMeta<? super T, ?>> fieldMetas) {
        this.parentFieldList = new ArrayList<>(fieldMetas);
        return this;
    }

    @Override
    public ParentSubQueryAble<T, C> parentFields(Function<C, List<FieldMeta<? super T, ?>>> function) {
        this.parentFieldList = new ArrayList<>(function.apply(this.criteria));
        return this;
    }

    /*################################## blow ParentSubQueryAble method ##################################*/

    @Override
    public ChildSubQueryTargetFieldAble<T, C> parentSubQuery(Function<C, SubQuery> function) {
        this.parentSubQuery = function.apply(this.criteria);
        return this;
    }


    /*################################## blow ChildSubQueryTargetFieldAble method ##################################*/

    @Override
    public ChildSubQueryAble<C> childFields(List<FieldMeta<T, ?>> fieldMetas) {
        this.childFieldList = new ArrayList<>(fieldMetas);
        return this;
    }

    @Override
    public ChildSubQueryAble<C> childFields(Function<C, List<FieldMeta<T, ?>>> function) {
        this.childFieldList = new ArrayList<>(function.apply(this.criteria));
        return this;
    }

    /*################################## blow ChildSubQueryAble method ##################################*/

    @Override
    public InsertAble childSubQuery(Function<C, SubQuery> function) {
        this.childSubQuery = function.apply(this.criteria);
        return this;
    }

    /*################################## blow InsertAble method ##################################*/

    @Override
    public final Insert asInsert() {
        if (this.prepared) {
            return this;
        }
        CriteriaContextHolder.clearContext(this.criteriaContext);

        Assert.notEmpty(this.parentFieldList, "parent fields required");
        Assert.notNull(this.parentSubQuery, "parent sub query required");
        Assert.notEmpty(this.childFieldList, "child fields required");
        Assert.notNull(this.childSubQuery, "child sub query required");

        this.parentFieldList = Collections.unmodifiableList(this.parentFieldList);
        this.childFieldList = Collections.unmodifiableList(this.childFieldList);
        this.prepared = true;
        return this;
    }

    /*################################## blow Insert method ##################################*/

    @Override
    public final boolean prepared() {
        return this.prepared;
    }

    /*################################## blow InnerStandardChildSubQueryInsert method ##################################*/

    @Override
    public final List<FieldMeta<?, ?>> parentFieldList() {
        return this.parentFieldList;
    }

    @Override
    public final SubQuery parentSubQuery() {
        return this.parentSubQuery;
    }

    @Override
    public final SubQuery subQuery() {
        return this.childSubQuery;
    }

    @Override
    public final ChildTableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final List<FieldMeta<?, ?>> fieldList() {
        return this.childFieldList;
    }

    @Override
    public final void clear() {
        this.parentFieldList = null;
        this.parentSubQuery = null;
        this.childFieldList = null;
        this.childSubQuery = null;
        this.prepared = false;
    }
}
