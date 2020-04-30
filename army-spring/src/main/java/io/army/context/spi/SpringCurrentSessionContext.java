package io.army.context.spi;

import io.army.ArmyAccessException;
import io.army.NoCurrentSessionException;
import io.army.Session;
import io.army.SessionFactory;
import io.army.util.Assert;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public final class SpringCurrentSessionContext implements CurrentSessionContext {

    public static SpringCurrentSessionContext build(SessionFactory sessionFactory) {
        return new SpringCurrentSessionContext(sessionFactory);
    }

    private final SessionFactory sessionFactory;

    public SpringCurrentSessionContext(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Session currentSession() throws ArmyAccessException {
        Session session = (Session) TransactionSynchronizationManager.getResource(sessionFactory);
        if (session == null) {
            throw new NoCurrentSessionException("no current session");
        }
        return session;
    }


    @Override
    public boolean hasCurrentSession() {
        return TransactionSynchronizationManager.hasResource(sessionFactory);
    }


    @Override
    public void currentSession(Session session) throws IllegalStateException {
        Session bindSession = (Session) TransactionSynchronizationManager.getResource(sessionFactory);
        Assert.state(bindSession == session
                , () -> String.format("currentSession(Session) method not supported by %s.", getClass().getName()));
    }

    @Override
    public void removeCurrentSession(Session session) throws IllegalStateException {
        Session bindSession = (Session) TransactionSynchronizationManager.getResource(sessionFactory);
        Assert.state(bindSession == null
                , () -> String.format("removeCurrentSession(Session) method not supported by %s.", getClass().getName()));
    }
}
