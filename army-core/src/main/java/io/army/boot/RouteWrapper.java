package io.army.boot;

import io.army.meta.TableMeta;

final class RouteWrapper {

    static RouteWrapper buildRouteKey(TableMeta<?> tableMeta, Object value) {
        return new RouteWrapper(tableMeta, value);
    }

    static RouteWrapper buildRouteIndex(int routeIndex) {
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

    final boolean routeIndex() {
        return this.routeIndex;
    }

    final TableMeta<?> tableMeta() {
        if (this.routeIndex) {
            throw new IllegalStateException("value isn't route key.");
        }
        if (this.tableMeta == null) {
            throw new IllegalStateException("error,tableMeta is null");
        }
        return this.tableMeta;
    }

    final Object routeKey() {
        if (this.routeIndex) {
            throw new IllegalStateException("value isn't route key.");
        }
        return this.value;
    }

    final int routeIndexValue() {
        if (!this.routeIndex) {
            throw new IllegalStateException("value isn't route index value.");
        }
        return (Integer) this.value;
    }
}
