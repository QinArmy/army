package io.army.dialect;

import io.army.beans.ReadonlyWrapper;
import io.army.meta.TableMeta;

import java.util.List;

public interface TableDML  extends SQL{

    /**
     * @return a modifiable list
     */
    List<SQLWrapper> insert(TableMeta<?> tableMeta, ReadonlyWrapper entityWrapper);



}
