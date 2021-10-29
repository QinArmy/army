package io.army.jdbc;

import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.ExecutorProvider;

import javax.sql.DataSource;

public final class JdbcExecutorProvider implements ExecutorProvider {

    private static final JdbcExecutorProvider INSTANCE = new JdbcExecutorProvider();

    public static JdbcExecutorProvider getInstance() {
        return INSTANCE;
    }

    private JdbcExecutorProvider() {
    }


    @Override
    public ExecutorFactory createFactory(final Object dataSource) {
        if (!(dataSource instanceof DataSource)) {
            throw new IllegalArgumentException("dataSource isn't javax.sql.DataSource instance.");
        }
        return new JdbcTxExecutorFactory((DataSource) dataSource);
    }


}
