package io.army.sync;

import io.army.boot.sync.GenericSyncApiSession;
import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;

import java.util.List;

/**
 * <p>
 * This interface encapsulate synchronous api than can access database.
 * </p>
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link Session}</li>
 *         <li>{@link ProxySession}</li>
 *     </ul>
 * </p>
 *
 * @see Session
 * @see ProxySession
 */
interface SingleDatabaseSyncSession extends GenericSyncApiSession {

    /**
     * @return a unmodifiable list
     */
    List<Integer> batchUpdate(Update update);

    /**
     * @return a unmodifiable list
     */
    List<Integer> batchUpdate(Update update, Visible visible);

    /**
     * @return a unmodifiable list
     */
    List<Long> batchLargeUpdate(Update update);

    /**
     * @return a unmodifiable list
     */
    List<Long> batchLargeUpdate(Update update, Visible visible);

    /**
     * @return a unmodifiable list
     */
    List<Integer> batchDelete(Delete delete);

    /**
     * @return a unmodifiable list
     */
    List<Integer> batchDelete(Delete delete, Visible visible);

    /**
     * @return a unmodifiable list
     */
    List<Long> batchLargeDelete(Delete delete);

    /**
     * @return a unmodifiable list
     */
    List<Long> batchLargeDelete(Delete delete, Visible visible);

}
