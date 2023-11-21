package io.army.jdbd;

import io.army.ArmyException;
import io.army.datasource.RoutingDataSource;
import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.reactive.executor.ReactiveLocalStmtExecutor;
import io.army.reactive.executor.ReactiveMetaExecutor;
import io.army.reactive.executor.ReactiveRmStmtExecutor;
import io.army.reactive.executor.ReactiveStmtExecutorFactory;
import io.army.session.DataAccessException;
import io.army.session.DriverException;
import io.army.session.Option;
import io.army.session.ServerException;
import io.army.session.executor.ExecutorSupport;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import io.jdbd.JdbdException;
import io.jdbd.session.DatabaseSessionFactory;
import io.jdbd.session.LocalDatabaseSession;
import io.jdbd.session.RmDatabaseSession;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Function;

/**
 * <p>This class is a implementation of {@link ReactiveStmtExecutorFactory} with jdbd spi.
 *
 * @see <a href="https://github.com/QinArmy/jdbd">jdbd-spi</a>
 * @since 1.0
 */
final class JdbdStmtExecutorFactory implements ReactiveStmtExecutorFactory {

    static JdbdStmtExecutorFactory create(JdbdStmtExecutorFactoryProvider provider, ExecutorEnv executorEnv) {
        return new JdbdStmtExecutorFactory(provider, executorEnv);
    }

    private static final AtomicIntegerFieldUpdater<JdbdStmtExecutorFactory> FACTORY_CLOSED =
            AtomicIntegerFieldUpdater.newUpdater(JdbdStmtExecutorFactory.class, "factoryClosed");

    final String sessionFactoryName;

    final ExecutorEnv executorEnv;

    final MappingEnv mappingEnv;

    final ServerMeta serverMeta;

    final boolean truncatedTimeType;

    private final DatabaseSessionFactory sessionFactory;

    private final LocalExecutorFunction localFunc;

    private final RmExecutorFunction rmFunc;

    private final Function<Option<?>, io.jdbd.session.Option<?>> armyToJdbdOptionFunc;

    private final Function<io.jdbd.session.Option<?>, Option<?>> jdbdToArmyOptionFunc;

    private volatile int factoryClosed;

    /**
     * private construcotr
     */
    @SuppressWarnings("unchecked")
    private JdbdStmtExecutorFactory(JdbdStmtExecutorFactoryProvider provider, ExecutorEnv executorEnv) {
        this.sessionFactoryName = provider.factoryName;
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

        this.armyToJdbdOptionFunc = (Function<Option<?>, io.jdbd.session.Option<?>>) executorFuncArray[2];
        this.jdbdToArmyOptionFunc = (Function<io.jdbd.session.Option<?>, Option<?>>) executorFuncArray[3];

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
    public Mono<ReactiveMetaExecutor> metaExecutor() {
        if (isClosed()) {
            return Mono.error(ExecutorSupport.executorFactoryClosed(this));
        }
        DatabaseSessionFactory factory = this.sessionFactory;
        if (factory instanceof RoutingDataSource) {
            factory = (DatabaseSessionFactory) ((RoutingDataSource<?>) factory).writableDataSource();
        }
        return Mono.from(factory.localSession())
                .map(session -> JdbdMetaExecutor.create(this.sessionFactoryName, this, session))
                .onErrorMap(this::wrapExecuteErrorIfNeed);
    }


    @Override
    public Mono<ReactiveLocalStmtExecutor> localExecutor(final @Nullable String sessionName, final boolean readOnly) {
        final Mono<ReactiveLocalStmtExecutor> mono;
        if (isClosed()) {
            mono = Mono.error(ExecutorSupport.executorFactoryClosed(this));
        } else if (sessionName == null) {
            // no bug,never here
            mono = Mono.error(new NullPointerException());
        } else {
            DatabaseSessionFactory factory = this.sessionFactory;
            if (readOnly && factory instanceof RoutingDataSource) {
                factory = (DatabaseSessionFactory) ((RoutingDataSource<?>) factory).readOnlyDataSource();
            }
            mono = Mono.from(factory.localSession())
                    .map(session -> this.localFunc.apply(this, session, sessionName))
                    .onErrorMap(this::wrapExecuteErrorIfNeed);
        }
        return mono;
    }

    @Override
    public Mono<ReactiveRmStmtExecutor> rmExecutor(final @Nullable String sessionName, final boolean readOnly) {
        final Mono<ReactiveRmStmtExecutor> mono;
        if (isClosed()) {
            mono = Mono.error(ExecutorSupport.executorFactoryClosed(this));
        } else if (sessionName == null) {
            // no bug,never here
            mono = Mono.error(new NullPointerException());
        } else {
            DatabaseSessionFactory factory = this.sessionFactory;
            if (readOnly && factory instanceof RoutingDataSource) {
                factory = (DatabaseSessionFactory) ((RoutingDataSource<?>) factory).readOnlyDataSource();
            }
            mono = Mono.from(factory.rmSession())
                    .map(session -> this.rmFunc.apply(this, session, sessionName))
                    .onErrorMap(this::wrapExecuteErrorIfNeed);
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
                .append(this.sessionFactoryName)
                .append(" , hash : ")
                .append(System.identityHashCode(this))
                .append(" ]")
                .toString();
    }

    ArmyException wrapExecuteError(final Exception cause) {
        final ArmyException e;
        if (cause instanceof ArmyException) {
            e = (ArmyException) cause;
        } else if (!(cause instanceof JdbdException)) {
            e = _Exceptions.unknownError(cause);
        } else if (cause instanceof io.jdbd.result.ServerException) {
            final io.jdbd.result.ServerException se = (io.jdbd.result.ServerException) cause;
            e = new ServerException(cause, se.getSqlState(), se.getVendorCode(), mapToArmyOptionFunc(se::valueOf));
        } else {
            final JdbdException je = (JdbdException) cause;
            e = new DriverException(cause, je.getSqlState(), je.getVendorCode());
        }
        return e;
    }

    Throwable wrapExecuteErrorIfNeed(final Throwable cause) {
        if (!(cause instanceof Exception)) {
            return cause;
        }
        return wrapExecuteError((Exception) cause);
    }


    Function<io.jdbd.session.Option<?>, ?> mapToJdbdOptionFunc(final @Nullable Function<Option<?>, ?> optionFunc) {
        if (optionFunc == Option.EMPTY_OPTION_FUNC || optionFunc == null) {
            return io.jdbd.session.Option.EMPTY_OPTION_FUNC;
        }
        return jdbdOption -> {
            final Option<?> armyOption;
            armyOption = mapToArmyOption(jdbdOption);
            if (armyOption == null) {
                return null;
            }
            return optionFunc.apply(armyOption);
        };
    }


    Function<Option<?>, ?> mapToArmyOptionFunc(final @Nullable Function<io.jdbd.session.Option<?>, ?> optionFunc) {
        if (optionFunc == io.jdbd.session.Option.EMPTY_OPTION_FUNC || optionFunc == null) {
            return Option.EMPTY_OPTION_FUNC;
        }
        return armyOption -> {
            final io.jdbd.session.Option<?> jdbdOption;
            jdbdOption = mapToJdbdOption(armyOption);
            if (jdbdOption == null) {
                return null;
            }
            return optionFunc.apply(jdbdOption);
        };
    }


    @Nullable
    io.jdbd.session.Option<?> mapToJdbdOption(final @Nullable Option<?> option) {
        final io.jdbd.session.Option<?> jdbdOption;
        if (option == null) {
            jdbdOption = null;
        } else if (option == Option.IN_TRANSACTION) {
            jdbdOption = io.jdbd.session.Option.IN_TRANSACTION;
        } else if (option == Option.ISOLATION) {
            jdbdOption = io.jdbd.session.Option.ISOLATION;
        } else if (option == Option.READ_ONLY) {
            jdbdOption = io.jdbd.session.Option.READ_ONLY;
        } else if (option == Option.XID) {
            jdbdOption = io.jdbd.session.Option.XID;
        } else if (option == Option.XA_STATES) {
            jdbdOption = io.jdbd.session.Option.XA_STATES;
        } else if (option == Option.XA_FLAGS) {
            jdbdOption = io.jdbd.session.Option.XA_FLAGS;
        } else if (option == Option.NAME) {
            jdbdOption = io.jdbd.session.Option.NAME;
        } else if (option == Option.CHAIN) {
            jdbdOption = io.jdbd.session.Option.CHAIN;
        } else if (option == Option.RELEASE) {
            jdbdOption = io.jdbd.session.Option.RELEASE;
        } else if (option == Option.AUTO_COMMIT) {
            jdbdOption = io.jdbd.session.Option.AUTO_COMMIT;
        } else if (option == Option.WAIT) {
            jdbdOption = io.jdbd.session.Option.WAIT;
        } else if (option == Option.LOCK_TIMEOUT) {
            jdbdOption = io.jdbd.session.Option.LOCK_TIMEOUT;
        } else if (option == Option.READ_ONLY_SESSION) {
            jdbdOption = io.jdbd.session.Option.READ_ONLY_SESSION;
        } else {
            jdbdOption = this.armyToJdbdOptionFunc.apply(option);
        }
        return jdbdOption;
    }

    @Nullable
    Option<?> mapToArmyOption(final @Nullable io.jdbd.session.Option<?> option) {
        final Option<?> armyOption;
        if (option == null) {
            armyOption = null;
        } else if (option == io.jdbd.session.Option.IN_TRANSACTION) {
            armyOption = Option.IN_TRANSACTION;
        } else if (option == io.jdbd.session.Option.ISOLATION) {
            armyOption = Option.ISOLATION;
        } else if (option == io.jdbd.session.Option.READ_ONLY) {
            armyOption = Option.READ_ONLY;
        } else if (option == io.jdbd.session.Option.XID) {
            armyOption = Option.XID;
        } else if (option == io.jdbd.session.Option.XA_STATES) {
            armyOption = Option.XA_STATES;
        } else if (option == io.jdbd.session.Option.XA_FLAGS) {
            armyOption = Option.XA_FLAGS;
        } else if (option == io.jdbd.session.Option.NAME) {
            armyOption = Option.NAME;
        } else if (option == io.jdbd.session.Option.CHAIN) {
            armyOption = Option.CHAIN;
        } else if (option == io.jdbd.session.Option.RELEASE) {
            armyOption = Option.RELEASE;
        } else if (option == io.jdbd.session.Option.AUTO_COMMIT) {
            armyOption = Option.AUTO_COMMIT;
        } else if (option == io.jdbd.session.Option.WAIT) {
            armyOption = Option.WAIT;
        } else if (option == io.jdbd.session.Option.LOCK_TIMEOUT) {
            armyOption = Option.LOCK_TIMEOUT;
        } else if (option == io.jdbd.session.Option.READ_ONLY_SESSION) {
            armyOption = Option.READ_ONLY_SESSION;
        } else {
            armyOption = this.jdbdToArmyOptionFunc.apply(option);
        }
        return armyOption;
    }

    /*-------------------below private instace methods -------------------*/

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
        final Function<Option<?>, io.jdbd.session.Option<?>> armyToJdbdOptionFunc;
        final Function<io.jdbd.session.Option<?>, Option<?>> jdbdToArmyOptionFunc;

        switch (serverDatabase) {
            case MySQL:
                localFunc = MySQLStmtExecutor::localExecutor;
                rmFunc = MySQLStmtExecutor::rmExecutor;
                armyToJdbdOptionFunc = MySQLStmtExecutor::mapToJdbdDialectOption;
                jdbdToArmyOptionFunc = MySQLStmtExecutor::mapToArmyDialectOption;
                break;
            case PostgreSQL:
                localFunc = PostgreStmtExecutor::localExecutor;
                rmFunc = PostgreStmtExecutor::rmExecutor;
                armyToJdbdOptionFunc = PostgreStmtExecutor::mapToJdbdDialectOption;
                jdbdToArmyOptionFunc = PostgreStmtExecutor::mapToArmyDialectOption;
                break;
            case Oracle:
            case H2:
            default:
                String m = String.format("currently,don't support %s", serverDatabase.name());
                throw new UnsupportedOperationException(m);
        }
        return new Object[]{localFunc, rmFunc, armyToJdbdOptionFunc, jdbdToArmyOptionFunc};
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
