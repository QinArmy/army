package io.army.sharding;


public interface TableRoute extends Route {

    int tableCount();

    int tableIndex(Object routeKey);

    String tableSuffix(Object routeKey);

    /**
     * @return start with {@literal '_'}
     */
    String convertToSuffix(int tableIndex);

    String suffixPlaceHolder();
}
