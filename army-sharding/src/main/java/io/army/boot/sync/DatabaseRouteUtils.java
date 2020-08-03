package io.army.boot.sync;

import io.army.ErrorCode;
import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.CriteriaException;
import io.army.criteria.CriteriaRouteKeyException;
import io.army.criteria.FieldValueEqualPredicate;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner.InnerSelect;
import io.army.criteria.impl.inner.InnerSingleDML;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sharding.DatabaseRoute;
import io.army.sharding.RouteUtils;
import io.army.sharding.RouteWrapper;

import java.util.*;

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
            FieldMeta<?, ?> fieldMeta = p.fieldMeta();
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
                routeIndex = dml.databaseIndex();
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
            , DatabaseRoute router, final boolean dataSource) {
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
            FieldMeta<?, ?> fieldMeta = p.fieldMeta();
            if (routeFieldList.contains(fieldMeta)) {
                // success,find route key.
                routeKey = p.value();
                break;
            }
        }
        return routeKey;
    }


}
