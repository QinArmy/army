package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.SubQuery;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.util.Assert;

import java.util.Collections;
import java.util.List;

final class StandardContextualChildSubQueryInsert<T extends IDomain, C> extends AbstractSQLDebug implements Insert
        , Insert.InsertSpec {

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

    private int databaseIndex = -1;

    private int tableIndex = -1;

    private boolean prepared;

    private StandardContextualChildSubQueryInsert(ChildTableMeta<T> tableMeta, C criteria) {
        Assert.notNull(tableMeta, "tableMeta required");
        Assert.notNull(criteria, "criteria required");

        this.tableMeta = tableMeta;
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow ParentSubQueryTargetFieldSpec method ##################################*/
//
//    @Override
//    ParentTableRouteSpec<T, C> parentFields(List<FieldMeta<T, ?>> fieldMetas) {
//        this.parentFieldList = new ArrayList<>(fieldMetas);
//        return this;
//    }
//
//    @Override
//    ParentTableRouteSpec<T, C> parentFields(Function<C, List<FieldMeta<T, ?>>> function) {
//        this.parentFieldList = new ArrayList<>(function.apply(this.criteria));
//        return this;
//    }

    /*################################## blow ParentSubQuerySpec method ##################################*/
//
//    @Override
//    ParentSubQuerySpec<T, C> route(int databaseIndex, int tableIndex) {
//        this.databaseIndex = databaseIndex;
//        this.tableIndex = tableIndex;
//        return this;
//    }
//
//    @Override
//    ParentSubQuerySpec<T, C> route(int tableIndex) {
//        this.tableIndex = tableIndex;
//        return this;
//    }
//
//    @Override
//    ChildSubQueryTargetFieldSpec<T, C> parentSubQuery(Function<C, SubQuery> function) {
//        this.parentSubQuery = function.apply(this.criteria);
//        return this;
//    }


    /*################################## blow ChildSubQueryTargetFieldSpec method ##################################*/
//
//    @Override
//    ChildSubQuerySpec<C> childFields(List<FieldMeta<T, ?>> fieldMetas) {
//        this.childFieldList = new ArrayList<>(fieldMetas);
//        return this;
//    }
//
//    @Override
//    ChildSubQuerySpec<C> childFields(Function<C, List<FieldMeta<T, ?>>> function) {
//        this.childFieldList = new ArrayList<>(function.apply(this.criteria));
//        return this;
//    }

    /*################################## blow ChildSubQuerySpec method ##################################*/

//    @Override
//    InsertSpec childSubQuery(Function<C, SubQuery> function) {
//        this.childSubQuery = function.apply(this.criteria);
//        return this;
//    }


    /*################################## blow InsertSpec method ##################################*/

    @Override
    public Insert asInsert() {
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
    public void prepared() {
        Assert.prepared(this.prepared);
    }

    /*################################## blow InnerStandardChildSubQueryInsert method ##################################*/
//
//    @Override
//    List<IPredicate> predicateList() {
//       throw new UnsupportedOperationException();
//    }
//
//    @Override
//    List<FieldMeta<?, ?>> parentFieldList() {
//        return this.parentFieldList;
//    }
//
//    @Override
//    SubQuery parentSubQuery() {
//        return this.parentSubQuery;
//    }
//
//    @Override
//    SubQuery subQuery() {
//        return this.childSubQuery;
//    }
//
//    @Override
//    ChildTableMeta<?> table() {
//        return this.tableMeta;
//    }
//
//    @Override
//    List<FieldMeta<?, ?>> fieldList() {
//        return this.childFieldList;
//    }
//
//    @Override
//    String tableAlias() {
//        return "";
//    }
//
//    @Override
//    int databaseIndex() {
//        return this.databaseIndex;
//    }
//
//    @Override
//    int tableIndex() {
//        return this.tableIndex;
//    }
//
//    @Override
//    void clear() {
//        this.parentFieldList = null;
//        this.parentSubQuery = null;
//        this.childFieldList = null;
//        this.childSubQuery = null;
//        this.prepared = false;
//    }


}
