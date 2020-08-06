package io.army.boot.sync;

import io.army.NoCurrentSessionException;
import io.army.context.spi.CurrentSessionContext;
import io.army.sync.GenericSyncApiSessionFactory;
import org.springframework.core.NamedThreadLocal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class DefaultCurrentSessionContext implements CurrentSessionContext {

    private static final ConcurrentMap<GenericSyncApiSessionFactory, DefaultCurrentSessionContext> CONTEXT_CACHE =
            new ConcurrentHashMap<>(2);

    static DefaultCurrentSessionContext build(GenericSyncApiSessionFactory sessionFactory) {
        return CONTEXT_CACHE.computeIfAbsent(sessionFactory, key -> new DefaultCurrentSessionContext());
    }

    private static final ThreadLocal<GenericSyncApiSession> HOLDER = new NamedThreadLocal<>(
            "GenericSyncApiSession holder");

    @Override
    public boolean hasCurrentSession() {
        return HOLDER.get() != null;
    }

    @Override
    public GenericSyncApiSession currentSession() throws NoCurrentSessionException {
        GenericSyncApiSession session = HOLDER.get();
        if (session == null) {
            throw new NoCurrentSessionException("no current session.");
        }
        return session;
    }

    @Override
    public void currentSession(GenericSyncApiSession session) throws IllegalStateException {
        final GenericSyncApiSession holdSession = HOLDER.get();
        if (holdSession != null && holdSession != session) {
            throw new IllegalStateException(String.format("existed current session[%s]", holdSession));
        }
        if (holdSession == null) {
            HOLDER.set(session);
        }
    }

    @Override
    public void removeCurrentSession(GenericSyncApiSession session) throws IllegalStateException {
        final GenericSyncApiSession holdSession = HOLDER.get();
        if (holdSession != null && holdSession != session) {
            throw new IllegalStateException(String.format("existed current session[%s]", holdSession));
        }
        if (holdSession != null) {
            HOLDER.remove();
        }
    }
}
