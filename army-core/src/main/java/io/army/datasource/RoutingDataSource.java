package io.army.datasource;

@Deprecated
public interface RoutingDataSource<D> {

   D getPrimaryDataSource();
}
