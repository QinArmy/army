package io.army.boot.reactive;

import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.advice.GenericSessionFactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;
import io.army.reactive.ReactiveSessionFactory;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;
import io.army.util.Assert;
import io.jdbd.DatabaseSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

final class ReactiveSessionFactoryBuilderImpl
        extends GenericReactiveSessionFactoryBuilderImpl<ReactiveSessionFactoryBuilder>
        implements ReactiveSessionFactoryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ReactiveSessionFactoryBuilderImpl.class);


    private DatabaseSessionFactory databaseSessionFactory;

    ReactiveSessionFactoryBuilderImpl(boolean springApplication) {
        super(springApplication);
        this.tableCountPerDatabase = 1;
        this.shardingMode = ShardingMode.NO_SHARDING;
    }

    @Override
    public ReactiveSessionFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        this.fieldCodecs = fieldCodecs;
        return this;
    }

    @Override
    public ReactiveSessionFactoryBuilder name(String sessionFactoryName) {
        this.name = sessionFactoryName;
        return this;
    }

    @Override
    public ReactiveSessionFactoryBuilder environment(ArmyEnvironment environment) {
        this.environment = environment;
        return this;
    }

    @Override
    public ReactiveSessionFactoryBuilder tableCountPerDatabase(int tableCountPerDatabase) {
        this.tableCountPerDatabase = tableCountPerDatabase;
        return this;
    }

    @Override
    public ReactiveSessionFactoryBuilder factoryAdvice(Collection<GenericSessionFactoryAdvice> factoryAdvices) {
        this.factoryAdvices = factoryAdvices;
        return this;
    }

    @Override
    public ReactiveSessionFactoryBuilder waitCreateSeconds(int seconds) {
        this.waitCreateSeconds = seconds;
        return this;
    }

    @Override
    public ReactiveSessionFactoryBuilder domainInsertAdvice(Collection<ReactiveDomainInsertAdvice> insertAdvices) {
        this.domainInsertAdvices = insertAdvices;
        return this;
    }

    @Override
    public ReactiveSessionFactoryBuilder domainUpdateInsertAdvice(Collection<ReactiveDomainUpdateAdvice> updateAdvices) {
        this.domainUpdateAdvices = updateAdvices;
        return this;
    }

    @Override
    public ReactiveSessionFactoryBuilder domainDeleteInsertAdvice(Collection<ReactiveDomainDeleteAdvice> deleteAdvices) {
        this.domainDeleteAdvices = deleteAdvices;
        return this;
    }

    @Override
    public ReactiveSessionFactoryBuilder datasource(Object databaseSessionFactory) {
        if (databaseSessionFactory instanceof DatabaseSessionFactory) {
            this.databaseSessionFactory = (DatabaseSessionFactory) databaseSessionFactory;
        } else {
            throw new IllegalArgumentException(
                    String.format("not support %s in %s", databaseSessionFactory.getClass().getName()
                            , ReactiveSessionFactoryBuilder.class.getPackage().getImplementationVersion()));
        }
        return this;
    }

    @Override
    public ReactiveSessionFactoryBuilder shardingMode(ShardingMode shardingMode) {
        this.shardingMode = shardingMode;
        return this;
    }

    @Override
    public ReactiveSessionFactory build() throws SessionFactoryException {
        final long startTime = System.currentTimeMillis();

        Assert.hasText(this.name, "name required");
        Assert.notNull(this.environment, "environment required");
        Assert.notNull(this.databaseSessionFactory, "databaseSessionFactory required");
        Assert.notNull(this.shardingMode, "shardingMode required");

        final int waitSeconds = this.waitCreateSeconds;
        if (waitSeconds < 1) {
            throw new IllegalArgumentException("waitCreateSeconds must great than 0 .");
        }

        final GenericSessionFactoryAdvice factoryAdvice = getFactoryAdviceComposite();
        //1. invoke beforeInstance session factory advice
        factoryAdvice.beforeInstance(this.environment);

        final Thread currentThread = Thread.currentThread();
        final AtomicReference<Throwable> errorReference = new AtomicReference<>(null);
        final AtomicReference<ReactiveSessionFactoryImpl> sessionFactoryReference = new AtomicReference<>(null);


        this.databaseSessionFactory.getSession()
                // 2. query database info
                .flatMap(ReactiveSessionFactoryUtils::queryDatabase)
                // 3. create session factory
                .map(database -> new ReactiveSessionFactoryImpl(this, database))
                //4. invoke beforeInitialize session factory advice
                .doOnNext(factoryAdvice::beforeInitialize)
                //5. initializing session factory
                .flatMap(factory -> factory.initializing().thenReturn(factory))
                //6. invoke afterInitialize session factory advice
                .doOnNext(factoryAdvice::afterInitialize)
                // if error,record error
                .doOnError(ex -> {
                    errorReference.set(ex);
                    // interrupt build method's thread.
                    currentThread.interrupt();
                })
                //7. update sessionFactoryReference
                .subscribe(factory -> {
                    sessionFactoryReference.set(factory);
                    // interrupt build method's thread.
                    currentThread.interrupt();
                })
        ;

        try {
            Thread.sleep(waitSeconds * 1000L);
            throw new SessionFactoryException("ReactiveSessionFactory create timeout.");
        } catch (InterruptedException e) {
            if (Thread.interrupted()) {
                LOG.debug("clear {}.build() interrupted", getClass().getName());
            }
            Throwable ex = errorReference.get();
            if (ex != null) {
                throw new SessionFactoryException(ex, "ReactiveSessionFactory create occur error");
            }
            ReactiveSessionFactory sessionFactory = sessionFactoryReference.get();
            if (sessionFactory == null) {
                throw new SessionFactoryException(
                        "ReactiveSessionFactory create occur error,because thread unexpect interrupt.");
            }
            LOG.info("{} create cost {} ms.", sessionFactory, System.currentTimeMillis() - startTime);
            return sessionFactory;
        }
    }


}
