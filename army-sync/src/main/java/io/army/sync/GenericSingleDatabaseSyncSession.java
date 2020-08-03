package io.army.sync;

import io.army.boot.sync.GenericSyncApiSession;
import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;

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
public interface GenericSingleDatabaseSyncSession extends GenericSyncApiSession {

    int[] batchUpdate(Update update);

    int[] batchUpdate(Update update, Visible visible);

    long[] batchLargeUpdate(Update update);

    long[] batchLargeUpdate(Update update, Visible visible);

    int[] batchDelete(Delete delete);

    int[] batchDelete(Delete delete, Visible visible);

    long[] batchLargeDelete(Delete delete);

    long[] batchLargeDelete(Delete delete, Visible visible);

}
