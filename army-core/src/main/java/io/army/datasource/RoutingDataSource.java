package io.army.datasource;


public interface RoutingDataSource<D> {

   D getPrimaryDataSource();
}
