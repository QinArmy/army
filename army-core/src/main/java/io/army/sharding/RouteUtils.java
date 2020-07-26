package io.army.sharding;

import io.army.ErrorCode;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.FieldValueEqualPredicate;
import io.army.criteria.IPredicate;
import io.army.criteria.SubQuery;
import io.army.criteria.TableAble;
import io.army.criteria.impl.inner.InnerSelect;
import io.army.criteria.impl.inner.InnerSubQuery;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

public abstract class RouteUtils {

    protected RouteUtils() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    protected static RouteWrapper findRouteForSelect(InnerSelect select, boolean dataSource) {
        RouteWrapper routeWrapper;
        routeWrapper = findRouteFromWhereClause(select.tableWrapperList(), select.predicateList(), dataSource);
        if (routeWrapper != null) {
            return routeWrapper;
        }
        routeWrapper = findRouteFromTableList(select.tableWrapperList(), dataSource);
        if (routeWrapper != null) {
            return routeWrapper;
        }
        return findRouteFromSubQueryList(select.tableWrapperList(), dataSource);
    }


    @Nullable
    protected static RouteWrapper findRouteFromWhereClause(List<? extends TableWrapper> tableWrapperList
            , List<IPredicate> predicateList, final boolean dataSource) {
        RouteWrapper routeWrapper = null;

        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();

            if (!(tableAble instanceof TableMeta)) {
                continue;
            }
            TableMeta<?> tableMeta = (TableMeta<?>) tableAble;
            List<FieldMeta<?, ?>> routeFieldList = tableMeta.routeFieldList(dataSource);
            Object routeKey = findRouteKeyFromWhereClause(routeFieldList, predicateList);
            if (routeKey != null) {
                routeWrapper = RouteWrapper.buildRouteKey(tableMeta, routeKey);
                break;
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
                routeIndex = tableWrapper.databaseIndex();
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
            , List<IPredicate> predicateList) {
        Object routeKey = null;

        for (IPredicate predicate : predicateList) {
            if (!(predicate instanceof FieldValueEqualPredicate)) {
                continue;
            }
            FieldValueEqualPredicate p = (FieldValueEqualPredicate) predicate;
            if (routeFieldList.contains(p.fieldExp())) {
                routeKey = p.value();
                break;
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

    protected static String convertToSuffix(RouteWrapper routeWrapper, Dialect dialect) {
        String routeSuffix;
        if (routeWrapper.routeIndex()) {
            routeSuffix = dialect.sessionFactory()
                    .tableRoute(routeWrapper.tableMeta())
                    .convertToSuffix(routeWrapper.routeIndexValue());
        } else {
            routeSuffix = dialect.sessionFactory()
                    .tableRoute(routeWrapper.tableMeta())
                    .tableSuffix(routeWrapper.routeKey());
        }
        if (!routeSuffix.startsWith("_")) {
            TableRoute tableRoute = dialect.sessionFactory()
                    .tableRoute(routeWrapper.tableMeta());
            throw new ShardingRouteException(ErrorCode.ROUTE_ERROR, "TableRoute[%s] return error.", tableRoute);
        }
        return routeSuffix;
    }


}
