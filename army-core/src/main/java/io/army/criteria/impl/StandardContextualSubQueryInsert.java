package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.InnerStandardSubQueryInsert;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

final class StandardContextualSubQueryInsert<T extends IDomain, C> extends AbstractSQLDebug implements Insert
        , Insert.InsertAble, Insert.SubQueryTargetFieldAble<T, C>, Insert.SubQueryValueAble<C>
        , InnerStandardSubQueryInsert {

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private final TableMeta<?> tableMeta;

    private List<FieldMeta<?, ?>> fieldList;

    private SubQuery subQuery;

    private boolean prepared;

    StandardContextualSubQueryInsert(TableMeta<T> tableMeta, C criteria) {
        if (tableMeta instanceof ChildTableMeta) {
            throw new IllegalArgumentException("StandardContextualSubQueryInsert not support ChildTableMeta");
        }
        this.criteria = criteria;
        this.tableMeta = tableMeta;
        this.criteriaContext = new AbstractSQL.CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow SubQueryTargetFieldAble method ##################################*/

    @Override
    public final SubQueryValueAble<C> insertInto(List<FieldMeta<T, ?>> fieldMetaList) {
        this.fieldList = new ArrayList<>(fieldMetaList);
        return this;
    }

    /*################################## blow SubQueryValueAble method ##################################*/

    @Override
    public final InsertAble values(SubQuery subQuery) {
        this.subQuery = subQuery;
        return this;
    }

    @Override
    public final InsertAble values(Function<C, SubQuery> function) {
        this.subQuery = function.apply(this.criteria);
        return this;
    }

    /*################################## blow InnerStandardSubQueryInsert method ##################################*/

    @Override
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final SubQuery subQuery() {
        return this.subQuery;
    }

    @Override
    public final List<FieldMeta<?, ?>> fieldList() {
        return this.fieldList;
    }

    @Override
    public final void clear() {
        this.fieldList = null;
    }

    /*################################## blow InsertAble method ##################################*/

    @Override
    public final Insert asInsert() {
        if (this.prepared) {
            return this;
        }

        CriteriaContextHolder.clearContext(this.criteriaContext);
        this.criteriaContext.clear();

        Assert.state(!CollectionUtils.isEmpty(this.fieldList), "fieldList is empty,error.");
        Assert.state(this.subQuery != null, "values(SubQuery) or values(Function<C, SubQuery> ) must be invoked.");

        this.fieldList = Collections.unmodifiableList(this.fieldList);
        this.prepared = true;
        return this;
    }

    /*################################## blow SQLStatement method ##################################*/

    @Override
    public final boolean prepared() {
        return this.prepared;
    }
}
