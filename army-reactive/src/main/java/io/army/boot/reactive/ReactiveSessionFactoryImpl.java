package io.army.boot.reactive;

import io.army.AbstractGenericSessionFactory;
import io.army.GenericTmSessionFactory;
import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.boot.DomainValuesGenerator;
import io.army.cache.SessionCacheFactory;
import io.army.criteria.NotFoundRouteException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.TableMeta;
import io.army.reactive.ProxyReactiveSession;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;
import io.army.sharding.TableRoute;
import io.army.util.Assert;
import io.jdbd.DatabaseSessionFactory;
import reactor.core.publisher.Mono;

import java.util.EnumSet;
import java.util.function.Function;

/**
 * This class is a implementation of {@link io.army.reactive.ReactiveSessionFactory}
 */
class ReactiveSessionFactoryImpl extends AbstractGenericSessionFactory implements InnerReactiveSessionFactory {

    private static final EnumSet<ShardingMode> SUPPORT_SHARDING_SET = EnumSet.of(
            ShardingMode.NO_SHARDING
            , ShardingMode.SINGLE_DATABASE_SHARDING);

    private final DatabaseSessionFactory databaseSessionFactory;

    private final Dialect dialect;

    private final int tableCountPerDatabase;

    private final CurrentSessionContext currentSessionContext;

    private final ProxyReactiveSession proxyReactiveSession;

    private final DomainValuesGenerator domainValuesGenerator;

    private final SessionCacheFactory sessionCacheFactory;

    private final ReactiveInsertSQLExecutor insertSQLExecutor;

    private final ReactiveSelectSQLExecutor selectSQLExecutor;

    private final ReactiveUpdateSQLExecutor updateSQLExecutor;

    ReactiveSessionFactoryImpl(ReactiveSessionFactoryBuilderImpl factoryBuilder, Database actualDatabase) {
        super(factoryBuilder);
        if (!SUPPORT_SHARDING_SET.contains(this.shardingMode)) {
            throw new SessionFactoryException("ShardingMode[%s] is supported by %s.", getClass().getName());
        }

        this.databaseSessionFactory = factoryBuilder.databaseSessionFactory();
        Assert.notNull(this.databaseSessionFactory, "databaseSessionFactory required");
        this.dialect = ReactiveSessionFactoryUtils.createDialect(this, actualDatabase);
        this.tableCountPerDatabase = factoryBuilder.tableCountPerDatabase();
        ReactiveSessionFactoryUtils.assertReactiveTableCountOfSharding(this.tableCountPerDatabase, this);
        this.currentSessionContext = ReactiveSessionFactoryUtils.createCurrentSessionContext(this);
        this.proxyReactiveSession = ProxyReactiveSessionImpl.build(this, this.currentSessionContext);
        this.domainValuesGenerator = DomainValuesGenerator.build(this);

        this.sessionCacheFactory = SessionCacheFactory.build(this);

        this.insertSQLExecutor = ReactiveInsertSQLExecutor.build(this);
        this.selectSQLExecutor = ReactiveSelectSQLExecutor.build(this);
        this.updateSQLExecutor = ReactiveUpdateSQLExecutor.build(this);
    }


    @Override
    public int databaseIndex() {
        // always 0
        return 0;
    }

    @Override
    public int tableCountPerDatabase() {
        return this.tableCountPerDatabase;
    }

    @Override
    public Database actualDatabase() {
        return this.dialect.database();
    }

    @Override
    public DomainValuesGenerator domainValuesGenerator() {
        return this.domainValuesGenerator;
    }

    @Override
    public boolean compareDefaultOnMigrating() {
        return this.compareDefaultOnMigrating;
    }

    @Override
    public SessionCacheFactory sessionCacheFactory() {
        return this.sessionCacheFactory;
    }

    @Override
    public Function<Throwable, RuntimeException> composeExceptionFunction() {
        return null;
    }

    @Override
    public CurrentSessionContext currentSessionContext() {
        return null;
    }

    @Override
    public ReactiveDomainInsertAdvice domainInsertAdviceComposite(TableMeta<?> tableMeta) {
        return null;
    }

    @Override
    public ReactiveDomainUpdateAdvice domainUpdateAdviceComposite(TableMeta<?> tableMeta) {
        return null;
    }

    @Override
    public ReactiveDomainDeleteAdvice domainDeleteAdviceComposite(TableMeta<?> tableMeta) {
        return null;
    }

    @Override
    public boolean supportZone() {
        return false;
    }

    @Override
    public GenericTmSessionFactory tmSessionFactory() {
        return null;
    }

    @Override
    public boolean closed() {
        return false;
    }

    @Override
    public TableRoute tableRoute(TableMeta<?> tableMeta) throws NotFoundRouteException {
        return null;
    }

    @Override
    public ProxyReactiveSession proxySession() {
        return null;
    }

    @Override
    public ReactiveSessionBuilder builder() {
        return null;
    }

    @Override
    public boolean hasCurrentSession() {
        return false;
    }

    /*################################## blow InnerGenericRmSessionFactory method ##################################*/

    @Override
    public Dialect dialect() {
        return null;
    }

    @Override
    public ReactiveSelectSQLExecutor selectSQLExecutor() {
        return null;
    }

    @Override
    public ReactiveInsertSQLExecutor insertSQLExecutor() {
        return null;
    }

    @Override
    public ReactiveUpdateSQLExecutor updateSQLExecutor() {
        return null;
    }


    /*################################## blow package method ##################################*/

    Mono<Boolean> initializing() {
        return Mono.empty();
    }

    public boolean springApplication() {
        return this.springApplication;
    }
}
