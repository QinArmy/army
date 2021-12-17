package io.army.dialect;

import io.army.criteria.impl.inner._Predicate;
import io.army.meta.TableMeta;

import java.util.List;

public interface _DmlContext extends _StmtContext {


    /**
     * @return primary domain table.
     */
    TableMeta<?> tableMeta();

    List<_Predicate> predicateList();

}
