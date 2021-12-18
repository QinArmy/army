package io.army.sharding;

import io.army.beans.ReadWrapper;
import io.army.criteria.FieldValueEqualPredicate;
import io.army.criteria.IPredicate;
import io.army.criteria.SubQuery;
import io.army.criteria.TableAble;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Select;
import io.army.criteria.impl.inner._SubQuery;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.Function;

public abstract class _RouteUtils {

    _RouteUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return negative : not found table route.
     */
    public static byte databaseRouteFromRouteField(final List<_Predicate> predicateList
            , final Function<TableMeta<?>, Route> function) {
        byte databaseIndex = -1, index;
        for (_Predicate predicate : predicateList) {
            index = predicate.databaseIndex(function);
            if (index >= 0) {
                databaseIndex = index;
                break;
            }
        }
        return databaseIndex;
    }

    public static String tableSuffix(final byte tableIndex) {
        final String suffix;
        if (tableIndex < 0) {
            throw new ShardingRouteException(String.format("tableIndex[%s] < 0", tableIndex));
        } else if (tableIndex == 0) {
            suffix = "";
        } else if (tableIndex < 10) {
            suffix = "_0" + tableIndex;
        } else if (tableIndex < 100) {
            suffix = "_" + tableIndex;
        } else {
            throw new ShardingRouteException(String.format("tableIndex[%s] > 100", tableIndex));
        }
        return suffix;
    }


    /**
     * @return negative : not found table route.
     */
    public static byte tableRouteFromRouteField(final TableMeta<?> table, final List<_Predicate> predicateList
            , final RouteContext context) {

        if (table.databaseRouteFields().size() == 0 && context.databaseIndex() != 0) {
            throw _Exceptions.routeKeyValueError(table, 0, context.databaseIndex());
        }
        byte tableIndex = -1, index;
        for (_Predicate predicate : predicateList) {
            index = predicate.tableIndex(table, context);
            if (index >= 0) {
                tableIndex = index;
                break;
            }
        }
        return tableIndex;
    }

    /**
     * @see #encodeSubQueryRoute(byte)
     */
    public static byte decodeRoute(final byte route) {
        final byte r;
        if (route == Byte.MIN_VALUE) {// MIN_VALUE representing negative zero.
            r = 0;
        } else if (route < 0) { // negative representing route from sub query.
            r = (byte) -route;
        } else {
            r = route;
        }
        return r;
    }

    /**
     * @param subQueryRoute route from sub query.
     * @see #decodeRoute(byte)
     */
    public static byte encodeSubQueryRoute(final byte subQueryRoute) {
        return subQueryRoute == 0 ? Byte.MIN_VALUE : ((byte) -subQueryRoute);
    }

    @Nullable
    public static String convertToSuffix(final int tableCountPerDatabase, final int tableIndex) {
        if (tableCountPerDatabase <= 0) {
            throw new IllegalArgumentException(
                    String.format("tableCountPerDatabase[%s] must great than 0 .", tableCountPerDatabase));
        }
        if (tableIndex < 0 || tableIndex >= tableCountPerDatabase) {
            throw new IllegalArgumentException(
                    String.format("tableIndex[%s] must int[%s,%s).", tableIndex, 0, tableCountPerDatabase));
        }
        if (tableIndex == 0) {
            return null;
        }

        int tableNum = tableCountPerDatabase - 1, numberLength = 0;
        while (tableNum > 0) {
            tableNum /= 10;
            numberLength++;
        }
        final String indexText = Integer.toString(tableIndex);
        String tableSuffix;
        if (indexText.length() < numberLength) {

            char[] charArray = new char[numberLength + 1];
            charArray[0] = '_';

            final int end = numberLength - indexText.length() + 1;
            for (int i = 1; i < end; i++) {
                charArray[i] = '0';
            }
            for (int i = end; i < charArray.length; i++) {
                charArray[i] = indexText.charAt(i - end);
            }
            tableSuffix = new String(charArray);
        } else {
            tableSuffix = "_" + indexText;
        }
        return tableSuffix;

    }

    @Nullable
    protected static RouteWrapper findRouteForSelect(_Select select, boolean dataSource) {
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
            , List<_Predicate> predicateList, final boolean dataSource) {
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
        _SubQuery innerSubQuery = (_SubQuery) subQuery;
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
            , List<_Predicate> predicateList) {
        Object routeKey = null;

        for (IPredicate predicate : predicateList) {
            if (!(predicate instanceof FieldValueEqualPredicate)) {
                continue;
            }
            FieldValueEqualPredicate p = (FieldValueEqualPredicate) predicate;
            if (routeFieldList.contains(p.fieldMeta())) {
                routeKey = p.value();
                break;
            }
        }
        return routeKey;
    }

    @Nullable
    protected static Object findRouteKeyFormNamedParams(List<FieldMeta<?, ?>> routeFieldList
            , ReadWrapper namedParamWrapper) {

        Object routeKey = null;
        for (FieldMeta<?, ?> routeField : routeFieldList) {
            routeKey = namedParamWrapper.getType(routeField.fieldName());
            if (routeKey != null) {
                break;
            }
        }
        return routeKey;
    }

    protected static String convertToSuffix(RouteWrapper routeWrapper, Dialect dialect) {
        String routeSuffix = null;
        if (routeWrapper.routeIndex()) {
//            routeSuffix = dialect.sessionFactory()
//                    .tableRoute(routeWrapper.tableMeta())
//                    .convertToSuffix(routeWrapper.routeIndexValue());
        } else {
//            routeSuffix = dialect.sessionFactory()
//                    .tableRoute(routeWrapper.tableMeta())
//                    .tableSuffix(routeWrapper.routeKey());
        }
//        if (!routeSuffix.startsWith("_")) {
//            TableRoute tableRoute = dialect.sessionFactory()
//                    .tableRoute(routeWrapper.tableMeta());
//            throw new ShardingRouteException(ErrorCode.ROUTE_ERROR, "TableRoute[%s] return error.", tableRoute);
//        }
        return null;
    }


}
