package io.army.dialect;

import io.army.ShardingMode;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.IPredicate;
import io.army.criteria.NotFoundRouteException;
import io.army.criteria.impl.inner.InnerSelect;
import io.army.criteria.impl.inner.InnerSingleDML;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sharding.RouteUtils;
import io.army.sharding.RouteWrapper;
import io.army.sharding.TableRoute;

import java.util.List;

abstract class TableRouteUtils extends RouteUtils {


    static String valueInsertPrimaryRouteSuffix(TableMeta<?> tableMeta, Dialect dialect, ReadonlyWrapper beanWrapper) {
        if (notSupportRoute(dialect, tableMeta)) {
            return "";
        }

        List<FieldMeta<?, ?>> routeFields = tableMeta.routeFieldList(false);
        // obtain route key
        Object routeKey = null;
        for (FieldMeta<?, ?> routeField : routeFields) {
            Object value = beanWrapper.getType(routeField.propertyName());
            if (value != null) {
                routeKey = value;
                break;
            }
        }
        if (routeKey == null) {
            throw new NotFoundRouteException("Value insert ,TableMeta[%s] not found primary route.", tableMeta);
        }
        // route table suffix by route key
//        return dialect.sessionFactory()
//                .tableRoute(tableMeta)
//                .tableSuffix(routeKey);
        return null;
    }

    static String subQueryInsertPrimaryRouteSuffix(InnerSingleDML innerSingleDML, Dialect dialect) {
        TableMeta<?> tableMeta = innerSingleDML.tableMeta();
        if (notSupportRoute(dialect, tableMeta)) {
            return "";
        }
        int tableIndex = innerSingleDML.tableIndex();
        if (tableIndex < 0) {
            throw new NotFoundRouteException("Value insert ,TableMeta[%s] not found primary route."
                    , innerSingleDML.tableMeta());
        }
        // route table suffix by route key
//        return dialect.sessionFactory()
//                .tableRoute(tableMeta)
//                .tableSuffix(tableMeta);
        return null;
    }

    static String singleDmlPrimaryRouteSuffix(InnerSingleDML singleTableSQL, Dialect dialect) {
        TableMeta<?> tableMeta = singleTableSQL.tableMeta();
        if (notSupportRoute(dialect, tableMeta)) {
            return "";
        }
        String primaryRouteSuffix = findTableSuffix(tableMeta, singleTableSQL.tableIndex()
                , singleTableSQL.predicateList(), dialect);
        if (primaryRouteSuffix == null) {
            throw new NotFoundRouteException("Single dml ,TableMeta[%s] not found primary route.", tableMeta);
        }
        return primaryRouteSuffix;
    }


    static String selectPrimaryRouteSuffix(InnerSelect select, Dialect dialect) {
        if (dialect.sessionFactory().shardingMode() == ShardingMode.NO_SHARDING) {
            return "";
        }
        RouteWrapper routeWrapper = findRouteForSelect(select, false);
        String routeSuffix;
        if (routeWrapper == null) {
            throw new NotFoundRouteException("Select[%s] no route.", select);
        } else {
            routeSuffix = convertToSuffix(routeWrapper, dialect);
        }
        return routeSuffix;
    }

    @Nullable
    static String findRouteSuffixForTable(TableMeta<?> tableMeta, int tableIndex, List<IPredicate> predicateList
            , Dialect dialect) {
        List<FieldMeta<?, ?>> routeFieldList = tableMeta.routeFieldList(false);
        Object routeKey = findRouteKeyFromWhereClause(routeFieldList, predicateList);
        TableRoute tableRoute = dialect.sessionFactory().tableRoute(tableMeta);
        String routeSuffix = null;
//        if (routeKey == null) {
//            if (tableIndex >= 0) {
//                routeSuffix = tableRoute.convertToSuffix(tableIndex);
//            }
//        } else {
//            routeSuffix = tableRoute.tableSuffix(routeKey);
//        }
        return null;
    }


    @Nullable
    static String findTableSuffix(TableMeta<?> tableMeta, int tableIndex, List<IPredicate> predicateList
            , Dialect dialect) {

        List<FieldMeta<?, ?>> routeFieldList = tableMeta.routeFieldList(false);
        Object routeKey;
        // 1. try to find route key from where clause.
        routeKey = findRouteKeyFromWhereClause(routeFieldList, predicateList);
        String suffix = null;
//        TableRoute route = dialect.sessionFactory().tableRoute(tableMeta);
//        if (routeKey == null) {
//            // 2. step 1 failure,try find table index from table info.
//            if (tableIndex >= 0) {
//                suffix = route.convertToSuffix(tableIndex);
//            }
//        } else {
//            suffix = route.tableSuffix(routeKey);
//        }
        return null;

    }




    /*################################## blow private method ##################################*/

    private static boolean notSupportRoute(Dialect dialect, TableMeta<?> tableMeta) {
        return dialect.sessionFactory().shardingMode() == ShardingMode.NO_SHARDING
                || tableMeta.routeFieldList(false).isEmpty();
    }


}
