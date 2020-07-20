package io.army.sharding;

import io.army.beans.ReadonlyWrapper;
import io.army.criteria.FieldValueEqualPredicate;
import io.army.criteria.IPredicate;
import io.army.criteria.SubQuery;
import io.army.criteria.TableAble;
import io.army.criteria.impl.inner.InnerSubQuery;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

public  abstract class RouteUtils {

   protected   RouteUtils() {
        throw new UnsupportedOperationException();
    }




    @Nullable
   protected static Object findRouteKeyFromWhereClause(TableMeta<?>  tableMeta,List<IPredicate> predicateList
            ,final  boolean database){

        List<FieldMeta<?,?>> routeFields = tableMeta.routeFieldList(database);

        Object routeKey = null;
        for (IPredicate predicate : predicateList) {
            if (!(predicate instanceof FieldValueEqualPredicate)) {
                continue;
            }
            FieldValueEqualPredicate p = (FieldValueEqualPredicate) predicate;
            if (routeFields.contains(p.fieldExp())) {
                // success,find route key.
                routeKey = p.value();
                break;
            }
        }
        return routeKey;
    }




    @Nullable
    protected static RouteWrapper findRouteFromWhereClause(List<? extends TableWrapper> tableWrapperList
            , List<IPredicate> predicateList, final boolean dataSource) {
        RouteWrapper routeWrapper = null;

        tableLevel:
        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();

            if (!(tableAble instanceof TableMeta)) {
                continue;
            }
            List<FieldMeta<?, ?>> dataSourceRouteFields = ((TableMeta<?>) tableAble).routeFieldList(dataSource);
            for (IPredicate predicate : predicateList) {
                if (!(predicate instanceof FieldValueEqualPredicate)) {
                    continue;
                }
                FieldValueEqualPredicate p = (FieldValueEqualPredicate) predicate;
                FieldMeta<?, ?> fieldMeta = p.fieldExp();
                if (dataSourceRouteFields.contains(fieldMeta)) {
                    routeWrapper = RouteWrapper.buildRouteKey(fieldMeta.tableMeta(), p.value());
                    break tableLevel;
                }
            }
        }
        return routeWrapper;
    }

    @Nullable
    protected static RouteWrapper findRouteFromTableList(List<? extends TableWrapper> tableWrapperList
            , final boolean dataSource) {
        int routeIndex = -1;
        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();
            if (!(tableAble instanceof TableMeta)) {
                continue;
            }
            if (dataSource) {
                routeIndex = tableWrapper.dataSourceIndex();
            } else {
                routeIndex = tableWrapper.tableIndex();
            }
            if (routeIndex >= 0) {
                break;
            }
        }

        RouteWrapper routeWrapper;
        if (routeIndex < 0) {
            routeWrapper = null;
        } else {
            routeWrapper = RouteWrapper.buildRouteIndex(routeIndex);
        }
        return routeWrapper;
    }


    @Nullable
    protected static RouteWrapper findRouteFromSubQuery(SubQuery subQuery, final boolean dataSource) {
        InnerSubQuery innerSubQuery = (InnerSubQuery) subQuery;
        List<? extends TableWrapper> tableWrappers = innerSubQuery.tableWrapperList();

        RouteWrapper routeWrapper;
        // 1. try find from sub query's where clause
        routeWrapper = findRouteFromWhereClause(tableWrappers, innerSubQuery.predicateList(), dataSource);

        if (routeWrapper == null) {
            // 2. try find from table list of sub query.
            routeWrapper = findRouteFromTableList(innerSubQuery.tableWrapperList(), dataSource);
            if (routeWrapper == null) {
                // 3. try find from sub query of sub query
                routeWrapper = findRouteFromSubQueryList(tableWrappers, dataSource);
            }
        }
        return routeWrapper;
    }

    @Nullable
    protected static RouteWrapper findRouteFromSubQueryList(List<? extends TableWrapper> tableWrapperList
            , final boolean dataSource) {
        RouteWrapper routeWrapper = null;
        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();

            if (!(tableAble instanceof SubQuery)) {
                continue;
            }
            routeWrapper = findRouteFromSubQuery((SubQuery) tableAble, dataSource);
        }
        return routeWrapper;
    }


    @Nullable
    protected static Object findRouteKeyFromWhereClause(List<FieldMeta<?, ?>> routeFieldList
            ,List<IPredicate> predicateList){
        Object routeKey = null;

        for (IPredicate predicate : predicateList) {
            if (!(predicate instanceof FieldValueEqualPredicate)) {
                continue;
            }
            FieldValueEqualPredicate p = (FieldValueEqualPredicate) predicate;
            if (routeFieldList.contains(p.fieldExp())) {
                routeKey = p.value();
                break ;
            }
        }
        return routeKey;
    }

    @Nullable
    protected static Object findRouteKeyFormNamedParams(List<FieldMeta<?, ?>> routeFieldList
            , ReadonlyWrapper namedParamWrapper) {

        Object routeKey = null;
        for (FieldMeta<?, ?> routeField : routeFieldList) {
            routeKey = namedParamWrapper.getPropertyType(routeField.propertyName());
            if (routeKey != null) {
                break;
            }
        }
        return routeKey;
    }

}
