package io.army.dialect;

import io.army.beans.ReadonlyWrapper;
import io.army.criteria.*;
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
    default List<SQLWrapper> update(UpdateAble updateAble, Visible visible) {
        throw new UnsupportedOperationException();
    }

    default List<SQLWrapper> delete(DeleteAble.SingleDeleteAble deleteAble, Visible visible) {
        throw new UnsupportedOperationException();
    }

    default List<SQLWrapper> select(Select select) {
        throw new UnsupportedOperationException();
    }

    default void subQuery(SubQuery subQuery,SQLContext context) {
        throw new UnsupportedOperationException();
    }


}
