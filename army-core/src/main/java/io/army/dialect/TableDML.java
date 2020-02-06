package io.army.dialect;

import io.army.beans.ReadonlyWrapper;
import io.army.criteria.SingleUpdateAble;
import io.army.criteria.Visible;
import io.army.meta.TableMeta;

import java.util.List;

public interface TableDML extends SQL {

    /**
     * @return a modifiable list
     */
    List<SQLWrapper> insert(TableMeta<?> tableMeta, ReadonlyWrapper entityWrapper);


    /**
     * key points:
     * <ol>
     *     <li>update update_time</li>
     *     <li>update version</li>
     *     <li>logic delete</li>
     * </ol>
     *
     * @return a modifiable list
     */
    default List<SQLWrapper> update(SingleUpdateAble singleUpdateAble, Visible visible) {
        throw new UnsupportedOperationException();
    }


}
