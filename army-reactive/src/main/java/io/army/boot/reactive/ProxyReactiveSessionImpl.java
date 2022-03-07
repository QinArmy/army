package io.army.boot.reactive;

import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.reactive.ProxyReactiveSession;
import io.army.reactive.ReactiveSessionFactory;
import io.army.reactive.Session;
import reactor.core.publisher.Flux;

class ProxyReactiveSessionImpl extends AbstractProxyReactiveSession<ReactiveSessionFactory>
        implements ProxyReactiveSession {

    static ProxyReactiveSessionImpl build(ReactiveSessionFactory sessionFactory
            , CurrentSessionContext currentSessionContext) {
        return new ProxyReactiveSessionImpl(sessionFactory, currentSessionContext);
    }

    private ProxyReactiveSessionImpl(ReactiveSessionFactory sessionFactory
            , CurrentSessionContext currentSessionContext) {
        super(sessionFactory, currentSessionContext);
    }

    @Override
    public ReactiveSessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public Flux<Integer> batchUpdate(Update update) {
        return this.currentSessionContext.currentSession()
                .cast(Session.class)
                .flatMapMany(session -> session.batchUpdate(update))
                ;
    }

    @Override
    public Flux<Integer> batchUpdate(Update update, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .cast(Session.class)
                .flatMapMany(session -> session.batchUpdate(update, visible))
                ;
    }

    @Override
    public Flux<Long> batchLargeUpdate(Update update) {
        return this.currentSessionContext.currentSession()
                .cast(Session.class)
                .flatMapMany(session -> session.batchLargeUpdate(update))
                ;
    }

    @Override
    public Flux<Long> batchLargeUpdate(Update update, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .cast(Session.class)
                .flatMapMany(session -> session.batchLargeUpdate(update, visible))
                ;
    }

    @Override
    public Flux<Integer> batchDelete(Delete delete) {
        return this.currentSessionContext.currentSession()
                .cast(Session.class)
                .flatMapMany(session -> session.batchDelete(delete))
                ;
    }

    @Override
    public Flux<Integer> batchDelete(Delete delete, final Visible visible) {
        return this.currentSessionContext.currentSession()
                .cast(Session.class)
                .flatMapMany(session -> session.batchDelete(delete, visible))
                ;
    }

    @Override
    public Flux<Long> batchLargeDelete(Delete delete) {
        return this.currentSessionContext.currentSession()
                .cast(Session.class)
                .flatMapMany(session -> session.batchLargeDelete(delete))
                ;
    }

    @Override
    public Flux<Long> batchLargeDelete(Delete delete, Visible visible) {
        return this.currentSessionContext.currentSession()
                .cast(Session.class)
                .flatMapMany(session -> session.batchLargeDelete(delete, visible))
                ;
    }
}
