package io.army.dialect;

import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.meta.TableMeta;

import java.util.List;

public interface DML extends SQL {

    /**
     * @return a modifiable list
     */
    List<SQLWrapper> insert(TableMeta<?> tableMeta, ReadonlyWrapper entityWrapper);


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
