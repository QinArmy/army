package io.army.util;

import io.army.criteria.CriteriaException;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.session.GenericRmSessionFactory;
import io.army.sharding.DatabaseRoute;
import io.army.sharding.TableRoute;

/**
 * @since 1.0
 */
public abstract class Assert extends org.springframework.util.Assert {


    public static String assertHasText(@Nullable String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
        return text;
    }

    public static void prepared(boolean prepared) {
        if (prepared) {
            throw new IllegalStateException(String.format("%s is non-prepared state.", Statement.class.getName()));
        }
    }

    public static void nonPrepared(boolean prepared) {
        if (!prepared) {
            throw new IllegalStateException(String.format("%s is prepared state.", Statement.class.getName()));
        }
    }

    public static void identifierHasText(String identifier) {
        if (!StringUtils.hasText(identifier)) {
            throw new CriteriaException("Criteria identifier must has text.");
        }
    }

    public static void hasTable(@Nullable TableMeta<?> table) {
        if (table == null) {
            throw new CriteriaException("Criteria must has table.");
        }
    }

    public static void supportRoute(final TableMeta<?> table, final int databaseIndex, final int tableIndex) {
        final Class<?> routeClass = table.routeClass();
        if (routeClass == null) {
            if (databaseIndex > 0 || tableIndex > 0) {
                throw _Exceptions.notSupportSharding(table);
            }

        } else {
            if (databaseIndex > 0 && !DatabaseRoute.class.isAssignableFrom(routeClass)) {
                throw _Exceptions.notSupportDatabaseSharding(table);
            }
            if (tableIndex > 0 && !TableRoute.class.isAssignableFrom(routeClass)) {
                throw _Exceptions.notSupportTableSharding(table);
            }
        }

    }

    public static byte databaseRoute(final TableMeta<?> table, final int databaseIndex) {
        if (table.databaseRouteFields().isEmpty()) {
            if (databaseIndex != -1) {
                throw _Exceptions.notSupportSharding(table);
            }
        } else if (databaseIndex > 99) {
            throw new CriteriaException(String.format("databaseIndex[%s] not in [0,99].", databaseIndex));
        }
        return (byte) (databaseIndex < 0 ? -1 : databaseIndex);
    }

    public static byte tableRoute(final TableMeta<?> table, final int tableIndex) {
        final byte tableCount = table.tableCount();
        if (tableCount == 1) {
            if (tableIndex != -1) {
                throw _Exceptions.notSupportSharding(table);
            }
        } else if (tableIndex > 99) {
            throw new CriteriaException(String.format("%s tableIndex[%s] not in [0,%s].", table, tableIndex, tableCount));
        }
        return (byte) (tableIndex < 0 ? -1 : tableIndex);
    }

    public static void databaseRoute(_Statement stmt, final int routeDatabase, GenericRmSessionFactory factory) {
        if (routeDatabase >= 0 && routeDatabase != factory.databaseIndex()) {
            throw _Exceptions.databaseRouteError(stmt, factory);
        }
    }


}
