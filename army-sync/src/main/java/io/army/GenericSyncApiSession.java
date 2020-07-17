package io.army;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;

import java.util.List;

/**
 * <p>
 * this interface have three direct sub interfaces:
 *     <ul>
 *         <li>{@link io.army.Session}</li>
 *         <li>{@link ProxySession}</li>
 *         <li>{@code io.army.TmSession}</li>
 *     </ul>
 * </p>
 *
 * @see Session
 * @see ProxySession
 */
public interface GenericSyncApiSession extends GenericSyncSession {

    int[] batchUpdate(Update update);

    int[] batchUpdate(Update update, Visible visible);

    long[] batchLargeUpdate(Update update);

    long[] batchLargeUpdate(Update update, Visible visible);

    void valueInsert(Insert insert);

    void valueInsert(Insert insert, Visible visible);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible);

    int[] batchDelete(Delete delete);

    int[] batchDelete(Delete delete, Visible visible);

    long[] batchLargeDelete(Delete delete);

    long[] batchLargeDelete(Delete delete, Visible visible);

}
