package io.army.context.spi;

import io.army.DataAccessException;
import io.army.NoCurrentSessionException;
import io.army.sync.GenericSyncApiSession;

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

    boolean hasCurrentSession();

    /**
     * Retrieve the current session according to the scoping defined
     * by this implementation.
     *
     * @return The current session.
     * @throws DataAccessException Typically indicates an issue
     *                             locating or creating the current session.
     */
    GenericSyncApiSession currentSession() throws NoCurrentSessionException;

    void currentSession(GenericSyncApiSession session) throws IllegalStateException;

    void removeCurrentSession(GenericSyncApiSession session) throws IllegalStateException;

}
