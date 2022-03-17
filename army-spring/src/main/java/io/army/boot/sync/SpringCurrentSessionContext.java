package io.army.boot.sync;

import io.army.NoCurrentSessionException;
import io.army.sync.CurrentSessionContext;
import io.army.sync.SessionFactory;
import io.army.sync.SyncSession;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SuppressWarnings("unused")
public final class SpringCurrentSessionContext implements CurrentSessionContext {


    public static SpringCurrentSessionContext create(SessionFactory factory) {
        return new SpringCurrentSessionContext(factory);
    }

    private final SessionFactory sessionFactory;

    private SpringCurrentSessionContext(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public SessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public SyncSession currentSession() throws NoCurrentSessionException {
        final Object currentSession;
        currentSession = TransactionSynchronizationManager.getResource(this.sessionFactory);
        if (!(currentSession instanceof SyncSession)) {
            throw new NoCurrentSessionException("no current session");
        }
        return (SyncSession) currentSession;
    }


    @Override
    public boolean hasCurrentSession() {
        return TransactionSynchronizationManager.getResource(this.sessionFactory) instanceof SyncSession;
    }


}
