package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.SubQuery;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

final class StandardContextualSubQueryInsert<T extends IDomain, C> extends AbstractSQLDebug implements Insert
        , Insert.InsertSpec {

    static <T extends IDomain, C> StandardContextualSubQueryInsert<T, C> build(TableMeta<T> tableMeta, C criteria) {
        return new StandardContextualSubQueryInsert<>(tableMeta, criteria);
    }

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private final TableMeta<?> tableMeta;

    private List<FieldMeta<?, ?>> fieldList;

    private SubQuery subQuery;

    private int databaseIndex = -1;

    private int tableIndex = -1;

    private boolean prepared;

    private StandardContextualSubQueryInsert(TableMeta<T> tableMeta, C criteria) {
        if (tableMeta instanceof ChildTableMeta) {
            throw new IllegalArgumentException("StandardContextualSubQueryInsert only support Simple Mapping Mode");
        }
        this.criteria = criteria;
        this.tableMeta = tableMeta;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    /*################################## blow SubQueryTargetFieldSpec method ##################################*/
//
//    @Override
//    SimpleTableRouteSpec<C> insertInto(List<FieldMeta<T, ?>> fieldMetaList) {
//        this.fieldList = new ArrayList<>(fieldMetaList);
//        return this;
//    }
//
//    @Override
//    SimpleTableRouteSpec<C> insertInto(Function<C, List<FieldMeta<T, ?>>> function) {
//        this.fieldList = new ArrayList<>(function.apply(this.criteria));
//        return this;
//    }
//
//    /*################################## blow SubQueryValueSpec method ##################################*/
//
//    @Override
//    SubQueryValueSpec<C> route(int databaseIndex, int tableIndex) {
//        this.databaseIndex = databaseIndex;
//        this.tableIndex = tableIndex;
//        return this;
//    }
//
//    @Override
//    SubQueryValueSpec<C> route(int tableIndex) {
//        this.tableIndex = tableIndex;
//        return this;
//    }
//
//    @Override
//    InsertSpec subQuery(Function<C, SubQuery> function) {
//        this.subQuery = function.apply(this.criteria);
//        return this;
//    }

    /*################################## blow InnerStandardSubQueryInsert method ##################################*/
//
//    @Override
//    TableMeta<?> table() {
//        return this.tableMeta;
//    }
//
//    @Override
//    String tableAlias() {
//        return "";
//    }
//
//    @Override
//    int tableIndex() {
//        return this.tableIndex;
//    }
//
//    @Override
//    int databaseIndex() {
//        return this.databaseIndex;
//    }
//
//    @Override
//    List<IPredicate> predicateList() {
//       throw new UnsupportedOperationException();
//    }
//
//    @Override
//    SubQuery subQuery() {
//        return this.subQuery;
//    }
//
//    @Override
//    List<FieldMeta<?, ?>> fieldList() {
//        return this.fieldList;
//    }
//
//    @Override
//    void clear() {
//        this.fieldList = null;
//        this.subQuery = null;
//        this.prepared = false;
//    }

    /*################################## blow InsertSpec method ##################################*/

    @Override
    public Insert asInsert() {
        if (this.prepared) {
            return this;
        }

        CriteriaContextStack.clearContextStack(this.criteriaContext);

        Assert.state(!CollectionUtils.isEmpty(this.fieldList), "fieldList is empty,error.");
        Assert.state(this.subQuery != null, "values(SubQuery) or values(Function<C, SubQuery> ) must be invoked.");

        this.fieldList = Collections.unmodifiableList(this.fieldList);
        this.prepared = true;
        return this;
    }

    /*################################## blow SQLStatement method ##################################*/

    @Override
    public void prepared() {
        Assert.prepared(this.prepared);
    }
}
