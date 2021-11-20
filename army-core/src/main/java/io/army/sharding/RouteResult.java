package io.army.sharding;

public final class RouteResult {

    public static RouteResult table(final int tableIndex) {
        return new RouteResult(0, tableIndex);
    }

    public static RouteResult route(final int databaseIndex, final int tableIndex) {
        return new RouteResult(databaseIndex, tableIndex);
    }

    public final int databaseIndex;

    public final int tableIndex;

    private RouteResult(final int databaseIndex, final int tableIndex) {
        if (databaseIndex < 0 || tableIndex < 0) {
            throw new IllegalArgumentException("databaseIndex or tableIndex error.");
        }
        this.databaseIndex = databaseIndex;
        this.tableIndex = tableIndex;
    }


}
