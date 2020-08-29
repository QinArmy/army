package io.army.boot.reactive;

import io.army.NoCurrentSessionException;
import io.army.reactive.GenericReactiveApiSession;
import io.army.reactive.GenericReactiveApiSessionFactory;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.NoSuchElementException;

final class DefaultCurrentSessionContext implements UpdatableCurrentSessionContext {

    static DefaultCurrentSessionContext build(GenericReactiveApiSessionFactory sessionFactory) {
        return new DefaultCurrentSessionContext(sessionFactory);
    }

    private final GenericReactiveApiSessionFactory sessionFactory;

    private DefaultCurrentSessionContext(GenericReactiveApiSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Mono<Boolean> hasCurrentSession() {
        return Mono.subscriberContext()
                .map(cxt -> cxt.hasKey(this.sessionFactory));
    }

    @Override
    public Mono<GenericReactiveApiSession> currentSession() throws NoCurrentSessionException {
        return Mono.subscriberContext()
                .map(cxt -> cxt.get(this.sessionFactory))
                .onErrorMap(this::mapNoCurrentSessionException)
                .cast(GenericReactiveApiSession.class)
                ;
    }

    @Override
    public Mono<Void> currentSession(GenericReactiveApiSession session) throws IllegalStateException {
        return Mono.subscriberContext()
                .map(cxt -> doCurrentSession(cxt, session))
                .then()
                ;
    }

    @Override
    public Mono<Void> removeCurrentSession(GenericReactiveApiSession session) throws IllegalStateException {
        return Mono.subscriberContext()
                .map(cxt -> doRemoveCurrentSession(cxt, session))
                .then()
                ;
    }

    private Context doRemoveCurrentSession(Context context, GenericReactiveApiSession session) {
        GenericReactiveApiSessionFactory sessionFactory = this.sessionFactory;
        Context newContext;
        if (context.hasKey(sessionFactory)) {
            if (context.get(sessionFactory) != session) {
                throw new IllegalStateException("current session not match,deny remove.");
            } else {
                newContext = context.delete(session);
            }
        } else {
            newContext = context;
        }
        return newContext;
    }

    private Throwable mapNoCurrentSessionException(Throwable ex) {
        Throwable e = ex;
        if (ex instanceof NoSuchElementException) {
            e = new NoCurrentSessionException("no current session");
        }
        return e;
    }

    private Context doCurrentSession(Context context, GenericReactiveApiSession session) {
        GenericReactiveApiSessionFactory sessionFactory = this.sessionFactory;
        Context newContext;
        if (context.hasKey(sessionFactory)) {
            if (context.get(sessionFactory) != session) {
                throw new IllegalStateException("current session already exists.");
            } else {
                newContext = context;
            }
        } else {
            newContext = context.put(sessionFactory, session);
        }
        return newContext;
    }


}
