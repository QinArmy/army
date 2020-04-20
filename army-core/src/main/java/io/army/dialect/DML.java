package io.army.dialect;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.domain.IDomain;

import java.util.List;

public interface DML extends SQL {

    /**
     * @return a modifiable list
     */
    List<SQLWrapper> insert(IDomain domain);

    default List<SQLWrapper> insert(Insert insert) {
        throw new UnsupportedOperationException();
    }

    default List<BatchSQLWrapper> batchInsert(Insert insert) {
        throw new UnsupportedOperationException();
    }


    /**
     * key points:
     * <ol>
     *     <li>singleUpdate update_time</li>
     *     <li>singleUpdate version</li>
     *     <li>logic singleDelete</li>
     * </ol>
     *
     * @return a modifiable list
     */
    default List<SQLWrapper> update(Update update, Visible visible) {
        throw new UnsupportedOperationException();
    }

    default List<SQLWrapper> delete(Delete.DeleteAble deleteAble, Visible visible) {
        throw new UnsupportedOperationException();
    }


}
