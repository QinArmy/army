package io.army.tx.reactive;

import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @see javax.sql.DataSource
 */
public interface ReactiveDataSource {


    /**
     * <p>Attempts to establish a connection with the data source that
     * this {@code DataSource} object represents.
     *
     * @return a connection to the data source
     * @throws SQLException                 if a database access error occurs
     * @throws java.sql.SQLTimeoutException when the driver has determined that the
     *                                      timeout value specified by the {@code setLoginTimeout} method
     *                                      has been exceeded and has at least tried to cancel the
     *                                      current database connection attempt
     */
    Mono<Connection> getConnection() throws SQLException;


}
