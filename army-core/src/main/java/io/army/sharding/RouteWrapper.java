package io.army.sharding;

import io.army.meta.TableMeta;

public final class RouteWrapper {

    public static RouteWrapper buildRouteKey(TableMeta<?> tableMeta, Object routeKey) {
        return new RouteWrapper(tableMeta, routeKey);
    }

    public static RouteWrapper buildRouteIndex(int routeIndex) {
        return new RouteWrapper(routeIndex);
    }

    private final boolean routeIndex;

    private final Object value;

    private final TableMeta<?> tableMeta;

    private RouteWrapper(int routeIndexValue) {
        this.routeIndex = true;
        this.value = routeIndexValue;
        this.tableMeta = null;
    }

    private RouteWrapper(TableMeta<?> tableMeta, Object routeKey) {
        this.routeIndex = false;
        this.tableMeta = tableMeta;
        this.value = routeKey;

    }

    public final boolean routeIndex() {
        return this.routeIndex;
    }

    public final TableMeta<?> tableMeta() {
        if (this.routeIndex) {
            throw new IllegalStateException("value isn't route key.");
        }
        if (this.tableMeta == null) {
            throw new IllegalStateException("error,tableMeta is null");
        }
        return this.tableMeta;
    }

    public final Object routeKey() {
        if (this.routeIndex) {
            throw new IllegalStateException("value isn't route key.");
        }
        return this.value;
    }

    public final int routeIndexValue() {
        if (!this.routeIndex) {
            throw new IllegalStateException("value isn't route index value.");
        }
        return (Integer) this.value;
    }
}
