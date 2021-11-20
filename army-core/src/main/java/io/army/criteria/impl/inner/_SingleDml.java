package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.meta.TableMeta;

import java.util.List;

public interface _SingleDml extends _Statement {

    String tableAlias();

    TableMeta<?> tableMeta();

    int databaseIndex();

    int tableIndex();

    /**
     * @return a unmodifiable list
     */
    List<IPredicate> predicateList();
}
