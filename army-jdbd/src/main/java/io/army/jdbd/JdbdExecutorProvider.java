package io.army.jdbd;

import io.army.ArmyException;
import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;
import io.army.meta.ServerMeta;
import io.army.reactive.executor.ExecutorProvider;
import io.army.reactive.executor.StmtExecutorFactory;
import io.army.util._ClassUtils;
import io.jdbd.session.DatabaseSessionFactory;
import reactor.core.publisher.Mono;

/**
 * <p>This class is a implementation of {@link ExecutorProvider} with jdbd spi.
 *
 * @since 10
 */
public final class JdbdExecutorProvider implements ExecutorProvider {

    public static JdbdExecutorProvider create(final Object sessionFactory) {
        if (!(sessionFactory instanceof DatabaseSessionFactory)) {
            String m = String.format("%s support only %s,but passing %s",
                    JdbdExecutorProvider.class.getName(),
                    DatabaseSessionFactory.class.getName(),
                    _ClassUtils.safeClassName(sessionFactory)
            );
            throw new ArmyException(m);
        }
        return new JdbdExecutorProvider((DatabaseSessionFactory) sessionFactory);
    }


    private final DatabaseSessionFactory sessionFactory;

    private JdbdExecutorProvider(DatabaseSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Mono<ServerMeta> createServerMeta(Dialect usedDialect) {
        return null;
    }

    @Override
    public Mono<StmtExecutorFactory> createExecutorFactory(ExecutorEnv env) {
        return null;
    }


}
