package io.army.boot.sync;

import io.army.ArmyException;
import io.army.session.NoCurrentSessionException;
import io.army.sync.Session;
import io.army.sync.SessionContext;
import io.army.sync.SessionFactory;
import io.army.sync.SyncSession;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class SpringSessionContext implements SessionContext {


    public static SpringSessionContext create(SessionFactory factory) {
        return new SpringSessionContext(factory);
    }

    private final SessionFactory sessionFactory;

    private SpringSessionContext(SessionFactory sessionFactory) {
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

    @Override
    public void executeWithTempSession(Consumer<SyncSession> consumer) {
        executeWithTempSession(false, consumer);
    }

    @Override
    public void executeWithTempSession(final boolean readonly, final Consumer<SyncSession> consumer) {
        Session syncSession = null;
        try (Session session = this.sessionFactory.builder().name("temp").readonly(readonly).build()) {
            syncSession = session;
            consumer.accept(session);
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("session %s execute occur error.", syncSession);
            throw new ArmyException(m, e);
        }
    }

    @Override
    public <T> T returnWithTempSession(Function<SyncSession, T> function) {
        return returnWithTempSession(false, function);
    }

    @Override
    public <T> T returnWithTempSession(boolean readonly, Function<SyncSession, T> function) {
        Session syncSession = null;
        try (Session session = this.sessionFactory.builder().name("temp").readonly(readonly).build()) {
            syncSession = session;
            return function.apply(session);
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("session %s execute occur error.", syncSession);
            throw new ArmyException(m, e);
        }
    }


}
