package io.army.dialect;

import io.army.ShardingMode;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.IPredicate;
import io.army.criteria.NotFoundRouteException;
import io.army.criteria.impl.inner.InnerSingleTableSQL;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sharding.RouteUtils;
import io.army.sharding.TableRoute;

import java.util.List;

abstract class TableRouteUtils extends RouteUtils {


    static String valueInsertPrimaryRouteSuffix(TableMeta<?> tableMeta, Dialect dialect, ReadonlyWrapper beanWrapper) {
        if (!supportRoute(dialect, tableMeta)) {
            return "";
        }

        List<FieldMeta<?, ?>> routeFields = tableMeta.routeFieldList(false);
        // obtain route key
        Object routeKey = null;
        for (FieldMeta<?, ?> routeField : routeFields) {
            Object value = beanWrapper.getPropertyType(routeField.propertyName());
            if (value != null) {
                routeKey = value;
                break;
            }
        }
        if (routeKey == null) {
            throw new NotFoundRouteException("Value insert ,TableMeta[%s] not found primary route.", tableMeta);
        }
        // route table suffix by route key
        return dialect.sessionFactory()
                .tableRoute(tableMeta)
                .tableSuffix(routeKey);
    }

    static String singleTablePrimaryRouteSuffix(InnerSingleTableSQL singleTableSQL, Dialect dialect) {
        TableMeta<?> tableMeta = singleTableSQL.tableMeta();
        if (!supportRoute(dialect, tableMeta)) {
            return "";
        }
        int tableIndex = singleTableSQL.tableIndex();
        if (tableIndex < 0) {
            throw new NotFoundRouteException("Value insert ,TableMeta[%s] not found primary route."
                    , singleTableSQL.tableMeta());
        }
        // route table suffix by route key
        return dialect.sessionFactory()
                .tableRoute(tableMeta)
                .tableSuffix(tableMeta);
    }

    static String singleDmlPrimaryRouteSuffix(InnerSingleTableSQL singleTableSQL, Dialect dialect){
        TableMeta<?> tableMeta = singleTableSQL.tableMeta();
        if (!supportRoute(dialect, tableMeta)) {
            return "";
        }
        String primaryRouteSuffix = findTableSuffix(tableMeta,singleTableSQL.tableIndex()
                ,singleTableSQL.predicateList(),dialect);
        if(primaryRouteSuffix == null){
            throw new NotFoundRouteException("Single dml ,TableMeta[%s] not found primary route.", tableMeta);
        }
        return primaryRouteSuffix;
    }


    @Nullable
    static String findTableSuffix(TableMeta<?> tableMeta, int tableIndex, List<IPredicate> predicateList
            , Dialect dialect) {

        List<FieldMeta<?, ?>> routeFieldList = tableMeta.routeFieldList(false);
        Object routeKey;
        // 1. try to find route key from where clause.
        routeKey = findRouteKeyFromWhereClause(routeFieldList, predicateList);
        String suffix = null;
        TableRoute route = dialect.sessionFactory().tableRoute(tableMeta);
        if (routeKey == null) {
            // 2. step 1 failure,try find table index from table info.
            if (tableIndex >= 0) {
                suffix = route.convertToSuffix(tableIndex);
            }
        }else {
            suffix = route.tableSuffix(routeKey);
        }
        return suffix;

    }




    /*################################## blow private method ##################################*/

    private static boolean supportRoute(Dialect dialect, TableMeta<?> tableMeta) {
        return dialect.sessionFactory().shardingMode() != ShardingMode.NO_SHARDING
                && !tableMeta.routeFieldList(false).isEmpty();
    }


}
