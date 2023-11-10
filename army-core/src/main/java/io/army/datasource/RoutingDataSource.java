package io.army.datasource;


public interface RoutingDataSource<R> {

    R writableDataSource();

    R readOnlyDataSource();


}
