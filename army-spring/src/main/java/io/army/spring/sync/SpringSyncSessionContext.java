package io.army.spring.sync;

import io.army.session.NoCurrentSessionException;
import io.army.session.SessionFactory;
import io.army.sync.SyncSession;
import io.army.sync.SyncSessionContext;
import io.army.sync.SyncSessionFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

final class SpringSyncSessionContext implements SyncSessionContext {

    static SpringSyncSessionContext create(SyncSessionFactory factory) {
        return new SpringSyncSessionContext(factory);
    }

    private final SyncSessionFactory factory;

    private SpringSyncSessionContext(SyncSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public SyncSessionFactory sessionFactory() {
        return this.factory;
    }


    @Override
    public <T extends SessionFactory> T sessionFactory(Class<T> factoryClass) {
        return factoryClass.cast(this.factory);
    }

    @Override
    public boolean hasCurrentSession() {
        return TransactionSynchronizationManager.getResource(this.factory) instanceof SyncSession;
    }

    @Override
    public SyncSession currentSession() throws NoCurrentSessionException {
        final Object session;
        session = TransactionSynchronizationManager.getResource(this.factory);
        if (!(session instanceof SyncSession)) {
            throw new NoCurrentSessionException("no current session");
        }
        return (SyncSession) session;
    }

    @Override
    public <T extends SyncSession> T currentSession(Class<T> sessionClass) throws NoCurrentSessionException {
        return sessionClass.cast(currentSession());
    }


}
