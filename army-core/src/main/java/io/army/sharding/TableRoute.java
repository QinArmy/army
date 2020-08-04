package io.army.sharding;


public interface TableRoute extends Route {

    boolean containTable(int tableIndex);

    int tableIndex(Object routeKey);

    String tableSuffix(Object routeKey);

    /**
     * @return start with {@literal '_'}
     */
    String convertToSuffix(int tableIndex);

    String suffixPlaceHolder();
}
