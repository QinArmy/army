package io.army.reactive;

import io.army.meta.ServerMeta;
import io.army.session.SessionException;
import io.army.session._AbstractSessionFactory;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is a implementation of {@link SessionFactory}
 */
final class ReactiveLocalSessionFactory extends _AbstractSessionFactory implements SessionFactory {


    private final AtomicBoolean factoryClosed = new AtomicBoolean(false);

    ReactiveLocalSessionFactory(LocalSessionFactoryBuilder builder) {
        super(builder);
    }


    @Override
    public ServerMeta serverMeta() {
        return null;
    }

    @Override
    public ZoneId zoneId() {
        return null;
    }

    @Override
    public boolean isSupportSavePoints() {
        return false;
    }

    @Override
    public boolean isReactive() {
        return true;
    }


    /*################################## blow InnerReactiveApiSessionFactory method ##################################*/


    @Override
    public boolean isClosed() {
        return this.factoryClosed.get();
    }

    @Override
    public SessionFactory.SessionBuilder builder() {
        return new LocalSessionBuilder(this);
    }

    @Override
    public Mono<Void> close() {
        return Mono.empty();
    }

    /*################################## blow private static inner class ##################################*/

    static final class LocalSessionBuilder implements SessionFactory.SessionBuilder {

        private final ReactiveLocalSessionFactory sessionFactory;


        boolean readOnly;

        private LocalSessionBuilder(ReactiveLocalSessionFactory sessionFactory) {
            this.sessionFactory = sessionFactory;
        }

        @Override
        public SessionFactory.SessionBuilder readonly(boolean readonly) {
            this.readOnly = readonly;
            return this;
        }

        @Override
        public Mono<Session> build() throws SessionException {
            return Mono.empty();
        }

    }//SessionBuilder


}
