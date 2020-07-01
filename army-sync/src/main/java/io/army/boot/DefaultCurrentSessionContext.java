package io.army.boot;

import io.army.GenericSyncSessionFactory;
import io.army.NoCurrentSessionException;
import io.army.Session;
import io.army.context.spi.CurrentSessionContext;
import io.army.util.Assert;
import org.springframework.core.NamedThreadLocal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class DefaultCurrentSessionContext implements CurrentSessionContext {

    private static final ConcurrentMap<GenericSyncSessionFactory, DefaultCurrentSessionContext> CONTEXT_CACHE =
            new ConcurrentHashMap<>(2);

    static DefaultCurrentSessionContext build(GenericSyncSessionFactory sessionFactory) {
        return CONTEXT_CACHE.computeIfAbsent(sessionFactory, key -> new DefaultCurrentSessionContext());
    }

    private static final ThreadLocal<Session> HOLDER = new NamedThreadLocal<>("session holder");

    @Override
    public boolean hasCurrentSession() {
        return HOLDER.get() != null;
    }

    @Override
    public Session currentSession() throws NoCurrentSessionException {
        Session session = HOLDER.get();
        if (session == null) {
            throw new NoCurrentSessionException("no current session.");
        }
        return session;
    }

    @Override
    public void currentSession(Session session) throws IllegalStateException {
        final Session holdSession = HOLDER.get();
        Assert.state(holdSession == null || holdSession == session
                , () -> String.format("existed current session[%s]", holdSession));

        if (holdSession == null) {
            HOLDER.set(session);
        }

    }

    @Override
    public void removeCurrentSession(Session session) throws IllegalStateException {
        final Session holdSession = HOLDER.get();
        Assert.state(holdSession == null || holdSession == session
                , () -> String.format("existed current session[%s]", holdSession));

        if (holdSession != null) {
            HOLDER.remove();
        }
    }
}
