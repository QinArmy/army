package io.army.result;

import io.army.ArmyException;

public interface SyncBatchQuery extends SyncMultiQuery {

    /**
     * Get current batch no (based 1)
     *
     * @return batch no (based 1)
     * @throws ArmyException when multi-result have closed.
     */
    int batchNo() throws ArmyException;

    int batchSize();

}
