package io.army.dialect;

import io.army.meta.TableMeta;

public interface _DmlContext extends _StmtContext {


    /**
     * @return primary domain table.
     */
    TableMeta<?> tableMeta();

    /**
     * @return primary tableIndex
     */
    byte tableIndex();

    String tableSuffix();


}
