package io.army.jdbd;

import io.army.ArmyException;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.reactive.executor.LocalStmtExecutor;
import io.army.reactive.executor.MetaExecutor;
import io.army.reactive.executor.ReactiveStmtExecutorFactory;
import io.army.reactive.executor.RmStmtExecutor;
import io.army.session.DataAccessException;
import io.army.session.Option;
import io.jdbd.JdbdException;
import io.jdbd.session.DatabaseSessionFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

final class JdbdStmtExecutorFactory implements ReactiveStmtExecutorFactory {

    static JdbdStmtExecutorFactory create(JdbdStmtExecutorFactoryProvider provider, ExecutorEnv executorEnv) {
        return new JdbdStmtExecutorFactory(provider, executorEnv);
    }

    final String name;

    final ExecutorEnv executorEnv;

    final MappingEnv mappingEnv;

    final ServerMeta serverMeta;

    final boolean truncatedTimeType;
    private final DatabaseSessionFactory sessionFactory;

    private final AtomicBoolean factoryClosed = new AtomicBoolean(false);

    private JdbdStmtExecutorFactory(JdbdStmtExecutorFactoryProvider provider, ExecutorEnv executorEnv) {
        this.name = provider.factoryName;
        this.sessionFactory = provider.sessionFactory;
        this.executorEnv = executorEnv;
        this.mappingEnv = executorEnv.mappingEnv();
        this.serverMeta = executorEnv.serverMeta();

        final ArmyEnvironment env = executorEnv.environment();

        this.truncatedTimeType = env.getOrDefault(ArmyKey.TRUNCATED_TIME_TYPE);

    }


    @Override
    public String driverSpiVendor() {
        return "io.jdbd";
    }

    @Override
    public boolean supportSavePoints() {
        // true,jdbd provider save point spi
        return true;
    }

    @Override
    public Mono<MetaExecutor> metaExecutor() {
        return null;
    }

    @Override
    public Mono<LocalStmtExecutor> localExecutor() {
        return null;
    }

    @Override
    public Mono<RmStmtExecutor> rmExecutor() {
        return null;
    }

    @Override
    public <T> T valueOf(Option<T> option) {
        return null;
    }

    @Override
    public boolean isClosed() {
        return this.factoryClosed.get();
    }

    @Override
    public <T> Mono<T> close() {
        return Mono.defer(this::closeFactory);
    }


    private <T> Mono<T> closeFactory() {
        final Mono<T> mono;
        if (this.factoryClosed.compareAndSet(false, true)) {
            mono = Mono.from(this.sessionFactory.close())
                    .onErrorMap(this::mapCloseFactoryError)
                    .then(Mono.empty());
        } else {
            mono = Mono.empty();
        }
        return mono;
    }


    private Throwable mapCloseFactoryError(final Throwable cause) {
        final Throwable error;
        if (cause instanceof JdbdException) {
            String m = String.format("close %s occur error , %s",
                    DatabaseSessionFactory.class.getName(), cause.getMessage()
            );
            error = new DataAccessException(m);
        } else if (cause instanceof Exception) {
            String m = String.format("close %s occur unknown error , %s",
                    DatabaseSessionFactory.class.getName(), cause.getMessage()
            );
            error = new ArmyException(m);
        } else {
            error = cause;
        }
        return error;
    }


}
