package io.army.jdbd;

import io.army.ArmyException;
import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.executor.ExecutorEnv;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.reactive.executor.ReactiveMetaExecutor;
import io.army.reactive.executor.ReactiveRmStmtExecutor;
import io.army.reactive.executor.ReactiveStmtExecutorFactory;
import io.army.session.DataAccessException;
import io.army.session.Option;
import io.army.session.executor.ExecutorSupport;
import io.army.util._StringUtils;
import io.jdbd.JdbdException;
import io.jdbd.session.DatabaseSessionFactory;
import io.jdbd.session.LocalDatabaseSession;
import io.jdbd.session.RmDatabaseSession;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * <p>This class is a implementation of {@link ReactiveStmtExecutorFactory} with jdbd spi.
 *
 * @since 1.0
 */
final class JdbdStmtExecutorFactory implements ReactiveStmtExecutorFactory {

    static JdbdStmtExecutorFactory create(JdbdStmtExecutorFactoryProvider provider, ExecutorEnv executorEnv) {
        return new JdbdStmtExecutorFactory(provider, executorEnv);
    }

    private static final AtomicIntegerFieldUpdater<JdbdStmtExecutorFactory> FACTORY_CLOSED =
            AtomicIntegerFieldUpdater.newUpdater(JdbdStmtExecutorFactory.class, "factoryClosed");

    final String name;

    final ExecutorEnv executorEnv;

    final MappingEnv mappingEnv;

    final ServerMeta serverMeta;

    final boolean truncatedTimeType;
    private final DatabaseSessionFactory sessionFactory;

    private final LocalExecutorFunction localFunc;

    private final RmExecutorFunction rmFunc;

    private volatile int factoryClosed;

    /**
     * private construcotr
     */
    private JdbdStmtExecutorFactory(JdbdStmtExecutorFactoryProvider provider, ExecutorEnv executorEnv) {
        this.name = provider.factoryName;
        this.sessionFactory = provider.sessionFactory;
        this.executorEnv = executorEnv;
        this.mappingEnv = executorEnv.mappingEnv();
        this.serverMeta = executorEnv.serverMeta();

        final ArmyEnvironment env = executorEnv.environment();

        this.truncatedTimeType = env.getOrDefault(ArmyKey.TRUNCATED_TIME_TYPE);

        final Object[] executorFuncArray;
        executorFuncArray = createLocalExecutorFunc(this.serverMeta.serverDatabase());
        this.localFunc = (LocalExecutorFunction) executorFuncArray[0];
        this.rmFunc = (RmExecutorFunction) executorFuncArray[1];


    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean supportSavePoints() {
        // true,jdbd provider save point spi
        return true;
    }


    @Override
    public String driverSpiVendor() {
        return "io.jdbd";
    }

    @Override
    public String executorVendor() {
        return "io.army";
    }

    @Override
    public Mono<ReactiveMetaExecutor> metaExecutor(final @Nullable String name) {
        final Mono<ReactiveMetaExecutor> mono;
        if (isClosed()) {
            mono = Mono.error(ExecutorSupport.executorFactoryClosed(this));
        } else if (name == null) {
            mono = Mono.error(new NullPointerException());
        } else {
            mono = Mono.from(this.sessionFactory.localSession())
                    .map(session -> JdbdMetaExecutor.create(name, this, session))
                    .onErrorMap(ExecutorSupport::wrapIfNeed);
        }
        return mono;
    }


    @Override
    public Mono<ReactiveLocalStmtExecutor> localExecutor(final @Nullable String name) {
        final Mono<ReactiveLocalStmtExecutor> mono;
        if (isClosed()) {
            mono = Mono.error(ExecutorSupport.executorFactoryClosed(this));
        } else if (name == null) {
            mono = Mono.error(new NullPointerException());
        } else {
            mono = Mono.from(this.sessionFactory.localSession())
                    .map(session -> this.localFunc.apply(this, session, name))
                    .onErrorMap(ExecutorSupport::wrapIfNeed);
        }
        return mono;
    }

    @Override
    public Mono<ReactiveRmStmtExecutor> rmExecutor(final @Nullable String name) {
        final Mono<ReactiveRmStmtExecutor> mono;
        if (isClosed()) {
            mono = Mono.error(ExecutorSupport.executorFactoryClosed(this));
        } else if (name == null) {
            mono = Mono.error(new NullPointerException());
        } else {
            mono = Mono.from(this.sessionFactory.rmSession())
                    .map(session -> this.rmFunc.apply(this, session, name))
                    .onErrorMap(ExecutorSupport::wrapIfNeed);
        }
        return mono;
    }

    @Override
    public <T> T valueOf(Option<T> option) {
        return null;
    }

    @Override
    public boolean isClosed() {
        return this.factoryClosed != 0;
    }

    @Override
    public <T> Mono<T> close() {
        return Mono.defer(this::closeFactory);
    }

    @Override
    public String toString() {
        return _StringUtils.builder(48)
                .append(getClass().getName())
                .append("[ name : ")
                .append(this.name)
                .append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }

    private <T> Mono<T> closeFactory() {
        final Mono<T> mono;
        if (FACTORY_CLOSED.compareAndSet(this, 0, 1)) {
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


    /*-------------------below private static methods -------------------*/

    private static Object[] createLocalExecutorFunc(final Database serverDatabase) {
        final LocalExecutorFunction localFunc;
        final RmExecutorFunction rmFunc;
        switch (serverDatabase) {
            case MySQL:
                localFunc = MySQLStmtExecutor::localExecutor;
                rmFunc = MySQLStmtExecutor::rmExecutor;
                break;
            case PostgreSQL:
                localFunc = PostgreStmtExecutor::localExecutor;
                rmFunc = PostgreStmtExecutor::rmExecutor;
                break;
            case Oracle:
            case H2:
            default:
                throw new UnsupportedOperationException();
        }
        return new Object[]{localFunc, rmFunc};
    }


    @FunctionalInterface
    private interface LocalExecutorFunction {
        ReactiveLocalStmtExecutor apply(JdbdStmtExecutorFactory factory, LocalDatabaseSession session, String name);

    }

    @FunctionalInterface
    private interface RmExecutorFunction {
        ReactiveRmStmtExecutor apply(JdbdStmtExecutorFactory factory, RmDatabaseSession session, String name);
    }


}
