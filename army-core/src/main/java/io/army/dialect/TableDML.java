package io.army.dialect;

import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Updatable;
import io.army.meta.TableMeta;

import java.util.List;

public interface TableDML extends SQL {

    /**
     * @return a modifiable list
     */
    List<SQLWrapper> insert(TableMeta<?> tableMeta, ReadonlyWrapper entityWrapper);

    default List<SQLWrapper> update(Updatable updatable) {
        throw new UnsupportedOperationException();
    }


}
