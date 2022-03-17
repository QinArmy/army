package io.army.sync;

import io.army.NoCurrentSessionException;

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
public interface CurrentSessionContext {

    SessionFactory sessionFactory();

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


}
