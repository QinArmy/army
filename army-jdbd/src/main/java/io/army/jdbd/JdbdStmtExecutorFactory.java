package io.army.jdbd;

import io.army.ArmyException;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.reactive.executor.LocalStmtExecutor;
import io.army.reactive.executor.MetaExecutor;
import io.army.reactive.executor.RmStmtExecutor;
import io.army.reactive.executor.StmtExecutorFactory;
import io.army.session.DataAccessException;
import io.jdbd.JdbdException;
import io.jdbd.session.DatabaseSessionFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

final class JdbdStmtExecutorFactory implements StmtExecutorFactory {

    static JdbdStmtExecutorFactory create(DatabaseSessionFactory sessionFactory, ExecutorEnv executorEnv) {
        return new JdbdStmtExecutorFactory(sessionFactory, executorEnv);
    }


    final ExecutorEnv executorEnv;

    final MappingEnv mappingEnv;

    final ServerMeta serverMeta;

    private final DatabaseSessionFactory sessionFactory;

    private final AtomicBoolean factoryClosed = new AtomicBoolean(false);

    private JdbdStmtExecutorFactory(DatabaseSessionFactory sessionFactory, ExecutorEnv executorEnv) {
        this.sessionFactory = sessionFactory;
        this.executorEnv = executorEnv;
        this.mappingEnv = executorEnv.mappingEnv();
        this.serverMeta = executorEnv.serverMeta();


    }

    @Override
    public Mono<MetaExecutor> metaExecutor() {
        return null;
    }

    @Override
    public Mono<LocalStmtExecutor> localStmtExecutor() {
        return null;
    }

    @Override
    public Mono<RmStmtExecutor> rmStmtExecutor() {
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
