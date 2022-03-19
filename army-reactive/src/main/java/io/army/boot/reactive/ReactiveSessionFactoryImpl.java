package io.army.boot.reactive;

import io.army.Database;
import io.army.cache.SessionCacheFactory;
import io.army.dialect._Dialect;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.reactive.GenericReactiveApiSession;
import io.army.reactive.ProxyReactiveSession;
import io.army.reactive.Session;
import io.army.reactive.SessionFactory;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;
import io.army.session.*;
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
 * This class is a implementation of {@link SessionFactory}
 */
class ReactiveSessionFactoryImpl extends AbstractSessionFactory implements InnerReactiveSessionFactory {

    private static final EnumSet<FactoryMode> SUPPORT_SHARDING_SET = EnumSet.of(
            FactoryMode.NO_SHARDING
            , FactoryMode.TABLE_SHARDING);

    private final DatabaseSessionFactory databaseSessionFactory;

    private final _Dialect dialect;

    private final int tableCountPerDatabase;

    private final CurrentSessionContext currentSessionContext;

    private final ProxyReactiveSession proxyReactiveSession;

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
        super(factoryBuilder);
//        if (!SUPPORT_SHARDING_SET.contains(this.factoryMode)) {
//            throw new SessionFactoryException("ShardingMode[%s] is supported by %s.", getClass().getName());
//        }

        this.databaseSessionFactory = Objects.requireNonNull(factoryBuilder.databaseSessionFactory());
        this.dialect = SessionFactoryUtils.createDialect(this, actualDatabase);
        this.tableCountPerDatabase = 0;
        SessionFactoryUtils.assertReactiveTableCountOfSharding(this.tableCountPerDatabase, this);

        this.currentSessionContext = SessionFactoryUtils.createCurrentSessionContext(this);
        this.proxyReactiveSession = ProxyReactiveSessionImpl.build(this, this.currentSessionContext);

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
    public ServerMeta serverMeta() {
        return null;
    }


    // @Override
    public Database actualDatabase() {
        return this.dialect.database();
    }

    @Override
    public boolean supportSavePoints() {
        return false;
    }
    /*################################## blow InnerReactiveApiSessionFactory method ##################################*/

    @Override
    public SessionCacheFactory sessionCacheFactory() {
        return this.sessionCacheFactory;
    }

    @Override
    public Function<Throwable, Throwable> composeExceptionFunction() {
        return null;
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
        // return this.springApplication;
        return false;
    }

    @Nullable
    //@Override
    public GenericTmSessionFactory tmSessionFactory() {
        // always null
        return null;
    }

    @Override
    public boolean factoryClosed() {
        return this.factoryClosed.get();
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
    public _Dialect dialect() {
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



    private Mono<Void> assertSchemaMatch(DatabaseSchemaMetaData databaseSchema) {
        if (Objects.equals(databaseSchema.getCatalog(), this.schemaMeta.catalog())
                && Objects.equals(databaseSchema.getSchema(), this.schemaMeta.schema())) {
            return Mono.empty();
        }
//        return Mono.error(new SessionFactoryException("config schema[%s.%s] but database schema[%s.%s]."
//                , this.schemaMeta.catalog(), this.schemaMeta.schema()
//                , databaseSchema.getCatalog(), databaseSchema.getSchema()
//        ));
        return Mono.empty();
    }

    /*################################## blow private static inner class ##################################*/

    private static final class SessionBuilder implements SessionFactory.ReactiveSessionBuilder {

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
        public Mono<Session> build() throws SessionException {
//            if (this.sessionFactory.readOnly && !this.readOnly) {
//                throw new CreateSessionException("%s is read only,cannot create read-write session."
//                        , this.sessionFactory);
//            }
//            return this.sessionFactory.databaseSessionFactory.getSession()
//                    .flatMap(this::createSession)
//                    ;
            return Mono.empty();
        }

        private Mono<Session> createSession(DatabaseSession databaseSession) {
            final CurrentSessionContext sessionContext = this.sessionFactory.currentSessionContext;
            if (this.current && !(sessionContext instanceof UpdatableCurrentSessionContext)) {
                return Mono.error(new CreateSessionException("%s not support set current session."
                        , CurrentSessionContext.class.getName()));
            }
            ReactiveSessionImpl session;
            session = new ReactiveSessionImpl(this.sessionFactory, databaseSession, this.readOnly, this.current);

            Mono<Session> mono;
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
