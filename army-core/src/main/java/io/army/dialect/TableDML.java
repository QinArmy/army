package io.army.dialect;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public interface TableDML  extends SQL{

    SQLWrapper insert(TableMeta<?> tableMeta, IDomain entity);



}
