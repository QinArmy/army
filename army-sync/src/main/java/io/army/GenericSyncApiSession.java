package io.army;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;

import java.util.List;
import java.util.Map;

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

    /**
     * @return a unmodifiable map
     */
    Map<Integer, Integer> batchUpdate(Update update);

    /**
     * @return a unmodifiable map
     */
    Map<Integer, Integer> batchUpdate(Update update, Visible visible);

    /**
     * @return a unmodifiable map
     */
    Map<Integer, Long> batchLargeUpdate(Update update);

    /**
     * @return a unmodifiable map
     */
    Map<Integer, Long> batchLargeUpdate(Update update, Visible visible);

    void valueInsert(Insert insert);

    void valueInsert(Insert insert, Visible visible);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass);

    <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible);

    /**
     * @return a unmodifiable map
     */
    Map<Integer, Integer> batchDelete(Delete delete);

    /**
     * @return a unmodifiable map
     */
    Map<Integer, Integer> batchDelete(Delete delete, Visible visible);

    /**
     * @return a unmodifiable map
     */
    Map<Integer, Long> batchLargeDelete(Delete delete);

    /**
     * @return a unmodifiable map
     */
    Map<Integer, Long> batchLargeDelete(Delete delete, Visible visible);

}
