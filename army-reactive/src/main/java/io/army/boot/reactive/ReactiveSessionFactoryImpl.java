package io.army.boot.reactive;

import io.army.CreateSessionException;
import io.army.SessionException;
import io.army.SessionFactoryException;
import io.army.boot.DomainValuesGenerator;
import io.army.boot.migratioin.ReactiveMigrator;
import io.army.cache.SessionCacheFactory;
import io.army.criteria.NotFoundRouteException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.reactive.GenericReactiveApiSession;
import io.army.reactive.ProxyReactiveSession;
import io.army.reactive.ReactiveSession;
import io.army.reactive.ReactiveSessionFactory;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;
import io.army.session.AbstractSessionFactory;
import io.army.session.FactoryMode;
import io.army.session.GenericTmSessionFactory;
import io.army.sharding.TableRoute;
import io.jdbd.meta.DatabaseSchemaMetaData;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.DatabaseSessionFactory;
import reactor.core.publisher.Mono;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * This class is a implementation of {@link io.army.reactive.ReactiveSessionFactory}
 */
class ReactiveSessionFactoryImpl extends AbstractSessionFactory implements InnerReactiveSessionFactory {

    private static final EnumSet<FactoryMode> SUPPORT_SHARDING_SET = EnumSet.of(
            FactoryMode.NO_SHARDING
            , FactoryMode.TABLE_SHARDING);

    private final DatabaseSessionFactory databaseSessionFactory;

    private final Dialect dialect;

    private final int tableCountPerDatabase;

    private final CurrentSessionContext currentSessionContext;

    private final ProxyReactiveSession proxyReactiveSession;

    private final DomainValuesGenerator domainValuesGenerator;

    private final SessionCacheFactory sessionCacheFactory;

    private final Map<TableMeta<?>, ReactiveDomainInsertAdvice> insertAdviceMap;

    private final Map<TableMeta<?>, ReactiveDomainUpdateAdvice> updateAdviceMap;

    private final Map<TableMeta<?>, ReactiveDomainDeleteAdvice> deleteAdviceMap;

    private final InsertSQLExecutor insertSQLExecutor;

    private final SelectSQLExecutor selectSQLExecutor;

    private final UpdateSQLExecutor updateSQLExecutor;

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private final AtomicBoolean factoryClosed = new AtomicBoolean(false);

    ReactiveSessionFactoryImpl(ReactiveSessionFactoryBuilderImpl factoryBuilder, Database actualDatabase) {
        super(factoryBuilder, null);
        if (!SUPPORT_SHARDING_SET.contains(this.factoryMode)) {
            throw new SessionFactoryException("ShardingMode[%s] is supported by %s.", getClass().getName());
        }

        this.databaseSessionFactory = Objects.requireNonNull(factoryBuilder.databaseSessionFactory());
        this.dialect = SessionFactoryUtils.createDialect(this, actualDatabase);
        this.tableCountPerDatabase = factoryBuilder.tableCountPerDatabase();
        SessionFactoryUtils.assertReactiveTableCountOfSharding(this.tableCountPerDatabase, this);

        this.currentSessionContext = SessionFactoryUtils.createCurrentSessionContext(this);
        this.proxyReactiveSession = ProxyReactiveSessionImpl.build(this, this.currentSessionContext);
        this.domainValuesGenerator = DomainValuesGenerator.build(this);

        this.sessionCacheFactory = SessionCacheFactory.build(this);
        // create domain advice map
        this.insertAdviceMap = SessionFactoryUtils.createDomainInsertAdviceMap(factoryBuilder.domainInsertAdvices()
                , ReactiveDomainInsertAdvice.class);
        this.updateAdviceMap = SessionFactoryUtils.createDomainInsertAdviceMap(factoryBuilder.domainUpdateAdvices()
                , ReactiveDomainUpdateAdvice.class);
        this.deleteAdviceMap = SessionFactoryUtils.createDomainInsertAdviceMap(factoryBuilder.domainDeleteAdvices()
                , ReactiveDomainDeleteAdvice.class);

        // create sql executor
        this.insertSQLExecutor = InsertSQLExecutor.build(this);
        this.selectSQLExecutor = SelectSQLExecutor.build(this);
        this.updateSQLExecutor = UpdateSQLExecutor.build(this);
    }


    @Override
    public int databaseIndex() {
        // always 0,because of single database
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

    /*################################## blow InnerReactiveApiSessionFactory method ##################################*/

    @Override
    public SessionCacheFactory sessionCacheFactory() {
        return this.sessionCacheFactory;
    }

    @Override
    public Function<Throwable, Throwable> composeExceptionFunction() {
        return this.exceptionFunction;
    }

    @Override
    public CurrentSessionContext currentSessionContext() {
        return this.currentSessionContext;
    }

    @Nullable
    @Override
    public ReactiveDomainInsertAdvice domainInsertAdviceComposite(TableMeta<?> tableMeta) {
        return this.insertAdviceMap.get(tableMeta);
    }

    @Nullable
    @Override
    public ReactiveDomainUpdateAdvice domainUpdateAdviceComposite(TableMeta<?> tableMeta) {
        return this.updateAdviceMap.get(tableMeta);
    }

    @Nullable
    @Override
    public ReactiveDomainDeleteAdvice domainDeleteAdviceComposite(TableMeta<?> tableMeta) {
        return this.deleteAdviceMap.get(tableMeta);
    }

    public boolean springApplication() {
        return this.springApplication;
    }

    @Override
    public boolean supportZone() {
        return this.dialect.supportZone();
    }

    @Nullable
    @Override
    public GenericTmSessionFactory tmSessionFactory() {
        // always null
        return null;
    }

    @Override
    public boolean factoryClosed() {
        return this.factoryClosed.get();
    }

    @Override
    public TableRoute tableRoute(TableMeta<?> tableMeta) throws NotFoundRouteException {
        return null;
    }

    @Override
    public ProxyReactiveSession proxySession() {
        return this.proxyReactiveSession;
    }

    @Override
    public ReactiveSessionBuilder builder() {
        return new SessionBuilder(this);
    }

    @Override
    public Mono<Boolean> hasCurrentSession() {
        return this.currentSessionContext.currentSession()
                .map(GenericReactiveApiSession::hasTransaction);
    }

    @Override
    public Mono<Void> close() {
        return Mono.just(this.factoryClosed.get())
                .filter(Boolean.FALSE::equals)
                // do close
                .doOnSuccess(e -> this.factoryClosed.compareAndSet(false, true))
                .then()
                ;
    }

    /*################################## blow InnerGenericRmSessionFactory method ##################################*/

    @Override
    public Dialect dialect() {
        return this.dialect;
    }

    @Override
    public SelectSQLExecutor selectSQLExecutor() {
        return this.selectSQLExecutor;
    }

    @Override
    public InsertSQLExecutor insertSQLExecutor() {
        return this.insertSQLExecutor;
    }

    @Override
    public UpdateSQLExecutor updateSQLExecutor() {
        return this.updateSQLExecutor;
    }

    /*################################## blow package method ##################################*/

    Mono<Void> initializing() {
        if (this.initialized.get()) {
            return Mono.empty();
        }
//        return SessionFactoryUtils.tryObtainPrimaryFactory(this.databaseSessionFactory)
//                .getSession()
//                .flatMap(this::validateSchemaMeta)
//                .flatMap(this::migrateMetaToDatabase)
//                .flatMap(DatabaseSession::close)
//                .doOnSuccess(e -> this.initialized.compareAndSet(false, true))
//                .then();
        return Mono.empty();
    }



    /*################################## blow private method ##################################*/

    private Mono<DatabaseSession> migrateMetaToDatabase(DatabaseSession session) {
        return ReactiveMigrator.build()
                .migrate(session, this)
                .thenReturn(session)
                ;
    }

    private Mono<DatabaseSession> validateSchemaMeta(DatabaseSession session) {
        if (this.schemaMeta.defaultSchema()) {
            return Mono.just(session);
        }
//        return session.getDatabaseMetaData()
//                .getSchema()
//             //   .flatMap(this::assertSchemaMatch)
//                .thenReturn(session);
        return Mono.empty();
    }

    private Mono<Void> assertSchemaMatch(DatabaseSchemaMetaData databaseSchema) {
        if (Objects.equals(databaseSchema.getCatalog(), this.schemaMeta.catalog())
                && Objects.equals(databaseSchema.getSchema(), this.schemaMeta.schema())) {
            return Mono.empty();
        }
        return Mono.error(new SessionFactoryException("config schema[%s.%s] but database schema[%s.%s]."
                , this.schemaMeta.catalog(), this.schemaMeta.schema()
                , databaseSchema.getCatalog(), databaseSchema.getSchema()
        ));
    }

    /*################################## blow private static inner class ##################################*/

    private static final class SessionBuilder implements ReactiveSessionFactory.ReactiveSessionBuilder {

        private final ReactiveSessionFactoryImpl sessionFactory;

        private boolean current;

        private boolean readOnly;

        private SessionBuilder(ReactiveSessionFactoryImpl sessionFactory) {
            this.sessionFactory = sessionFactory;
        }

        @Override
        public ReactiveSessionBuilder currentSession(boolean current) {
            this.current = current;
            return this;
        }

        @Override
        public ReactiveSessionBuilder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        @Override
        public Mono<ReactiveSession> build() throws SessionException {
//            if (this.sessionFactory.readOnly && !this.readOnly) {
//                throw new CreateSessionException("%s is read only,cannot create read-write session."
//                        , this.sessionFactory);
//            }
//            return this.sessionFactory.databaseSessionFactory.getSession()
//                    .flatMap(this::createSession)
//                    ;
            return Mono.empty();
        }

        private Mono<ReactiveSession> createSession(DatabaseSession databaseSession) {
            final CurrentSessionContext sessionContext = this.sessionFactory.currentSessionContext;
            if (this.current && !(sessionContext instanceof UpdatableCurrentSessionContext)) {
                return Mono.error(new CreateSessionException("%s not support set current session."
                        , CurrentSessionContext.class.getName()));
            }
            ReactiveSessionImpl session;
            session = new ReactiveSessionImpl(this.sessionFactory, databaseSession, this.readOnly, this.current);

            Mono<ReactiveSession> mono;
            if (sessionContext instanceof UpdatableCurrentSessionContext) {
                UpdatableCurrentSessionContext currentSessionContext = (UpdatableCurrentSessionContext) sessionContext;
                mono = currentSessionContext.currentSession(session)
                        .thenReturn(session);
            } else {
                mono = Mono.just(session);
            }
            return mono;
        }
    }
}
