package io.army.boot;

import io.army.ErrorCode;
import io.army.IBean;
import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSingleDML;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sharding.DataSourceRoute;
import io.army.sharding.RouteUtils;
import io.army.sharding.RouteWrapper;

import java.util.*;

abstract class DatabaseRouteUtils extends RouteUtils {


    @Nullable
    static RouteWrapper findRouteForSelect(List<? extends TableWrapper> tableWrapperList
            , List<IPredicate> predicateList
            , final boolean dataSource) {
        RouteWrapper routeWrapper;
        if (predicateList.isEmpty()) {
            routeWrapper = findRouteFromTableList(tableWrapperList, dataSource);
        } else {
            routeWrapper = findRouteFromWhereClause(tableWrapperList, predicateList, dataSource);
            if (routeWrapper == null) {
                routeWrapper = findRouteFromTableList(tableWrapperList, dataSource);
            }
        }
        if (routeWrapper == null) {
            routeWrapper = findRouteFromSubQueryList(tableWrapperList, dataSource);
        }
        return routeWrapper;
    }


    @Nullable
    static RouteWrapper findRouteForSingleDML(InnerSingleDML dml, final boolean dataSource) {
        TableMeta<?> tableMeta = dml.tableMeta();
        List<FieldMeta<?, ?>> dataSourceRouteFields = tableMeta.routeFieldList(dataSource);
        RouteWrapper routeWrapper = null;
        // 1. try find from where clause.
        for (IPredicate predicate : dml.predicateList()) {
            if (!(predicate instanceof FieldValueEqualPredicate)) {
                continue;
            }
            FieldValueEqualPredicate p = (FieldValueEqualPredicate) predicate;
            FieldMeta<?, ?> fieldMeta = p.fieldExp();
            if (dataSourceRouteFields.contains(fieldMeta)) {
                // success,find route key.
                routeWrapper = RouteWrapper.buildRouteKey(fieldMeta.tableMeta(), p.value());
                break;
            }
        }
        if (routeWrapper == null) {
            // 2. try find from table .
            int routeIndex;
            if (dataSource) {
                routeIndex = dml.dataSourceIndex();
            } else {
                routeIndex = dml.tableIndex();
            }
            if (routeIndex >= 0) {
                //success ,find route index
                routeWrapper = RouteWrapper.buildRouteIndex(routeIndex);
            }
        }
        return routeWrapper;
    }


    static Map<Integer, Set<Integer>> findRouteFromNamedPredicates(InnerBatchSingleDML dml
            , DataSourceRoute router, final boolean dataSource) {
        final TableMeta<?> tableMeta = dml.tableMeta();
        final List<Object> namedParamList = dml.namedParamList();
        final List<FieldMeta<?, ?>> routeFieldList = tableMeta.routeFieldList(dataSource);

        Map<Integer, Set<Integer>> routeIndexSetMap = new HashMap<>();
        final int size = namedParamList.size();
        for (int i = 0; i < size; i++) {
            Object namedParam = namedParamList.get(i);

            if (namedParam instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> paramMap = (Map<String, Object>) namedParam;
                for (FieldMeta<?, ?> fieldMeta : routeFieldList) {
                    Object paramValue = paramMap.get(fieldMeta.propertyName());
                    if (paramValue != null) {
                        Set<Integer> routeIndexSet = routeIndexSetMap.computeIfAbsent(
                                router.dataSourceRoute(paramValue), k -> new HashSet<>());
                        routeIndexSet.add(i);
                        break;
                    }
                }
            } else if (namedParam instanceof IBean) {
                ReadonlyWrapper wrapper = ObjectAccessorFactory.forBeanPropertyAccess(namedParam);
                for (FieldMeta<?, ?> fieldMeta : routeFieldList) {
                    Object paramValue = wrapper.getPropertyValue(fieldMeta.propertyName());
                    if (paramValue != null) {
                        Set<Integer> routeIndexSet = routeIndexSetMap.computeIfAbsent(
                                router.dataSourceRoute(paramValue), k -> new HashSet<>());
                        routeIndexSet.add(i);
                        break;
                    }
                }
            } else {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "Batch DML only support Map named param or IBean named param.");
            }
        }
        if (!routeIndexSetMap.isEmpty() && routeIndexSetMap.size() != size) {
            throw new CriteriaRouteKeyException("Batch dml named param count[%s] and route count[%s] not match."
                    , size, routeIndexSetMap.size());
        }
        return Collections.unmodifiableMap(routeIndexSetMap);
    }


    @Nullable
    static Object findRouteFromNonNamedPredicates(InnerBatchSingleDML dml, final boolean dataSource) {
        final List<FieldMeta<?, ?>> routeFieldList = dml.tableMeta().routeFieldList(dataSource);
        Object routeKey = null;
        for (IPredicate predicate : dml.predicateList()) {
            if (!(predicate instanceof FieldValueEqualPredicate)) {
                continue;
            }
            FieldValueEqualPredicate p = (FieldValueEqualPredicate) predicate;
            FieldMeta<?, ?> fieldMeta = p.fieldExp();
            if (routeFieldList.contains(fieldMeta)) {
                // success,find route key.
                routeKey = p.value();
                break;
            }
        }
        return routeKey;
    }


}
