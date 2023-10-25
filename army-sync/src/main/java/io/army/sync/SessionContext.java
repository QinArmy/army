package io.army.sync;

import io.army.session.NoCurrentSessionException;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This interface's implementation must definite a public static build method like below.
 * <p>
 * {@code public class CurrentSessionContextImpl implements CurrentSessionContext {
 * <p>
 * public static CurrentSessionContextImpl build(GenericSyncApiSessionFactory sessionFactory){
 * ...
 * }
 * }
 * }
 */
public interface SessionContext {

    SyncLocalSessionFactory sessionFactory();

    boolean hasCurrentSession();

    /**
     * Retrieve the current session according to the scoping defined
     * by this implementation.
     *
     * @return The current session.
     * @throws NoCurrentSessionException Typically indicates an issue
     *                                   locating or creating the current session.
     */
    SyncSession currentSession() throws NoCurrentSessionException;

    void executeWithTempSession(Consumer<SyncSession> consumer);

    void executeWithTempSession(boolean readonly, Consumer<SyncSession> consumer);

    <T> T returnWithTempSession(Function<SyncSession, T> function);

    <T> T returnWithTempSession(boolean readonly, Function<SyncSession, T> function);

}
