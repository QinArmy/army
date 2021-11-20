package io.army.sync;

import io.army.SessionException;
import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;

import java.io.Flushable;
import java.util.List;

public interface GenericSyncApiSession extends GenericSyncSession, Flushable {


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

    @Override
    void flush() throws SessionException;
}
