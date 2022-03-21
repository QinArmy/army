package io.army.reactive;

import io.army.meta.ServerMeta;
import io.army.session.SessionException;
import io.army.session._AbstractSessionFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is a implementation of {@link SessionFactory}
 */
final class LocalSessionFactory extends _AbstractSessionFactory implements SessionFactory {


    private final AtomicBoolean factoryClosed = new AtomicBoolean(false);

    LocalSessionFactory(LocalSessionFactoryBuilder builder) {
        super(builder);
    }


    @Override
    public ServerMeta serverMeta() {
        return null;
    }


    @Override
    public boolean supportSavePoints() {
        return false;
    }
    /*################################## blow InnerReactiveApiSessionFactory method ##################################*/


    @Override
    public boolean factoryClosed() {
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

        private final LocalSessionFactory sessionFactory;


        boolean readOnly;

        private LocalSessionBuilder(LocalSessionFactory sessionFactory) {
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
