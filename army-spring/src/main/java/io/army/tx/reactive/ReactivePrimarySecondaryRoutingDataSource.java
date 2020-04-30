package io.army.tx.reactive;

import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ReactivePrimarySecondaryRoutingDataSource implements ReactiveDataSource {

    private final DataSource dataSource;

    public ReactivePrimarySecondaryRoutingDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Mono<Connection> getConnection() throws SQLException {
        return null;
    }

}
