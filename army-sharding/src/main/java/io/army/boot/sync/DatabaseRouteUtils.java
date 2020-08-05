package io.army.boot.sync;

import io.army.beans.ReadonlyWrapper;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner.InnerMultiDML;
import io.army.criteria.impl.inner.InnerSelect;
import io.army.criteria.impl.inner.InnerSingleDML;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sharding.RouteUtils;
import io.army.sharding.RouteWrapper;

import java.util.List;

abstract class DatabaseRouteUtils extends RouteUtils {


    @Nullable
    static RouteWrapper findRouteForSelect(InnerSelect select) {
        RouteWrapper routeWrapper;
        List<IPredicate> predicateList = select.predicateList();
        if (predicateList.isEmpty()) {
            routeWrapper = findRouteFromTableList(select.tableWrapperList(), true);
        } else {
            routeWrapper = findRouteFromWhereClause(select.tableWrapperList(), predicateList, true);
            if (routeWrapper == null) {
                routeWrapper = findRouteFromTableList(select.tableWrapperList(), true);
            }
        }
        if (routeWrapper == null) {
            routeWrapper = findRouteFromSubQueryList(select.tableWrapperList(), true);
        }
        return routeWrapper;
    }

    @Nullable
    static Object findRouteKeyInsert(TableMeta<?> tableMeta, ReadonlyWrapper wrapper) {
        List<FieldMeta<?, ?>> routeFieldList = tableMeta.routeFieldList(true);
        // obtain route key
        Object routeKey = null;
        for (FieldMeta<?, ?> routeField : routeFieldList) {
            Object value = wrapper.getPropertyType(routeField.propertyName());
            if (value != null) {
                routeKey = value;
                break;
            }
        }
        return routeKey;
    }


    @Nullable
    static RouteWrapper findRouteForSingleDML(InnerSingleDML dml) {
        TableMeta<?> tableMeta = dml.tableMeta();
        List<FieldMeta<?, ?>> dataSourceRouteFields = tableMeta.routeFieldList(true);
        RouteWrapper routeWrapper = null;
        // 1. try find from where clause.
        Object routeKey = findRouteKeyFromWhereClause(dataSourceRouteFields, dml.predicateList());
        if (routeKey != null) {
            routeWrapper = RouteWrapper.buildRouteKey(tableMeta, routeKey);
        }
        if (routeWrapper == null) {
            // 2. try find from table .
            int routeIndex = dml.databaseIndex();
            if (routeIndex >= 0) {
                //success ,find route index
                routeWrapper = RouteWrapper.buildRouteIndex(routeIndex);
            }
        }
        return routeWrapper;
    }

    @Nullable
    static RouteWrapper findRouteForMultiDML(InnerMultiDML dml) {
        RouteWrapper routeWrapper;
        routeWrapper = findRouteFromWhereClause(dml.tableWrapperList(), dml.predicateList(), true);
        if (routeWrapper == null) {
            routeWrapper = findRouteFromTableList(dml.tableWrapperList(), true);
        }
        return routeWrapper;
    }


}
