package io.army.boot.sync;

import io.army.NoCurrentSessionException;
import io.army.sync.CurrentSession;
import io.army.sync.CurrentSessionContext;
import io.army.sync.SessionFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SuppressWarnings("unused")
final class SpringCurrentSessionContext implements CurrentSessionContext {


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
    public CurrentSession session() throws NoCurrentSessionException {
        final Object currentSession;
        currentSession = TransactionSynchronizationManager.getResource(this.sessionFactory);
        if (!(currentSession instanceof CurrentSession)) {
            throw new NoCurrentSessionException("no current session");
        }
        return (CurrentSession) currentSession;
    }


    @Override
    public boolean hasCurrentSession() {
        return TransactionSynchronizationManager.getResource(this.sessionFactory) instanceof CurrentSession;
    }


}
