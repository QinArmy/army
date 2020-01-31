package io.army.dialect;

import io.army.beans.BeanWrapper;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.util.List;

public interface TableDML  extends SQL{

    /**
     * @return a modifiable list
     */
    List<SQLWrapper> insert(TableMeta<?> tableMeta, BeanWrapper entityWrapper);



}
