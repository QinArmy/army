package io.army.boot.sync;

import io.army.DataAccessException;
import io.army.NoCurrentSessionException;
import io.army.context.spi.CurrentSessionContext;
import io.army.sync.GenericSyncApiSessionFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SuppressWarnings("unused")
final class SpringCurrentSessionContext implements CurrentSessionContext {

    /**
     * This method invoked by {@link SyncSessionFactoryUtils#buildCurrentSessionContext(InnerGenericSyncApiSessionFactory)}
     */
    public static SpringCurrentSessionContext build(GenericSyncApiSessionFactory sessionFactory) {
        return new SpringCurrentSessionContext(sessionFactory);
    }

    private final GenericSyncApiSessionFactory sessionFactory;

    private SpringCurrentSessionContext(GenericSyncApiSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public GenericSyncApiSession currentSession() throws DataAccessException {
        GenericSyncApiSession session = (GenericSyncApiSession) TransactionSynchronizationManager
                .getResource(this.sessionFactory);
        if (session == null) {
            throw new NoCurrentSessionException("no current session");
        }
        return session;
    }


    @Override
    public boolean hasCurrentSession() {
        return TransactionSynchronizationManager.getResource(sessionFactory) != null;
    }


    @Override
    public void currentSession(GenericSyncApiSession session) throws IllegalStateException {
        throw new IllegalStateException(
                String.format("currentSession() method isn't supported by %s", getClass().getName()));
    }

    @Override
    public void removeCurrentSession(GenericSyncApiSession session) throws IllegalStateException {
        throw new IllegalStateException(
                String.format("removeCurrentSession() method isn't supported by %s", getClass().getName()));

    }
}
