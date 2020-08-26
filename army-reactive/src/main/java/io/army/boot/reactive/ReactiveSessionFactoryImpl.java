package io.army.boot.reactive;

import io.army.AbstractGenericSessionFactory;
import io.army.GenericTmSessionFactory;
import io.army.boot.DomainValuesGenerator;
import io.army.cache.SessionCacheFactory;
import io.army.context.spi.ReactiveCurrentSessionContext;
import io.army.criteria.NotFoundRouteException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.TableMeta;
import io.army.reactive.ProxyReactiveSession;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;
import io.army.sharding.TableRoute;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * This class is a implementation of {@link io.army.reactive.ReactiveSessionFactory}
 */
class ReactiveSessionFactoryImpl extends AbstractGenericSessionFactory implements InnerReactiveSessionFactory {


    private final ReactiveInsertSQLExecutor insertSQLExecutor;

    private final ReactiveSelectSQLExecutor selectSQLExecutor;

    private final ReactiveUpdateSQLExecutor updateSQLExecutor;

    ReactiveSessionFactoryImpl(ReactiveSessionFactoryBuilderImpl factoryBuilder, Database actualDatabase) {
        super(factoryBuilder);

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
        return 1;
    }

    @Override
    public Database actualDatabase() {
        return null;
    }

    @Override
    public DomainValuesGenerator domainValuesGenerator() {
        return null;
    }

    @Override
    public boolean compareDefaultOnMigrating() {
        return false;
    }

    @Override
    public SessionCacheFactory sessionCacheFactory() {
        return null;
    }

    @Override
    public Function<Throwable, RuntimeException> composeExceptionFunction() {
        return null;
    }

    @Override
    public ReactiveCurrentSessionContext currentSessionContext() {
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
}
