package io.army.sharding;

import io.army.beans.ReadWrapper;
import io.army.criteria.impl.inner._MultiDML;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Select;
import io.army.criteria.impl.inner._SingleDml;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

public abstract class _DatabaseRouteUtils extends _RouteUtils {


    @Nullable
    static RouteWrapper findRouteForSelect(_Select select) {
        RouteWrapper routeWrapper;
        List<_Predicate> predicateList = select.predicateList();
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
    static Object findRouteKeyInsert(TableMeta<?> tableMeta, ReadWrapper wrapper) {
        List<FieldMeta<?, ?>> routeFieldList = tableMeta.routeFieldList(true);
        // obtain route key
        Object routeKey = null;
        for (FieldMeta<?, ?> routeField : routeFieldList) {
            Object value = wrapper.getType(routeField.fieldName());
            if (value != null) {
                routeKey = value;
                break;
            }
        }
        return routeKey;
    }


    @Nullable
    static RouteWrapper findRouteForSingleDML(_SingleDml dml) {
        TableMeta<?> tableMeta = dml.table();
        List<FieldMeta<?, ?>> dataSourceRouteFields = tableMeta.routeFieldList(true);
        RouteWrapper routeWrapper = null;
        // 1. try find from where clause.
        Object routeKey = findRouteKeyFromWhereClause(dataSourceRouteFields, dml.predicateList());
        if (routeKey != null) {
            routeWrapper = RouteWrapper.buildRouteKey(tableMeta, routeKey);
        }
        if (routeWrapper == null) {
            // 2. try find from table .
            int routeIndex = 0;
            if (routeIndex >= 0) {
                //success ,find route index
                routeWrapper = RouteWrapper.buildRouteIndex(routeIndex);
            }
        }
        return routeWrapper;
    }

    @Nullable
    static RouteWrapper findRouteForMultiDML(_MultiDML dml) {
//        RouteWrapper routeWrapper;
//        routeWrapper = findRouteFromWhereClause(dml.tableWrapperList(), dml.predicateList(), true);
//        if (routeWrapper == null) {
//            routeWrapper = findRouteFromTableList(dml.tableWrapperList(), true);
//        }
        return null;
    }


}
