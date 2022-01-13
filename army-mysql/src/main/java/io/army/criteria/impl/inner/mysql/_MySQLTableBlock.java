package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._DialectTableBlock;

import java.util.List;

public interface _MySQLTableBlock extends _DialectTableBlock {

    /**
     * @return a unmodifiable list
     */
    List<String> partitionList();

    /**
     * @return a unmodifiable list
     */
    List<? extends _IndexHint> indexHintList();



}
