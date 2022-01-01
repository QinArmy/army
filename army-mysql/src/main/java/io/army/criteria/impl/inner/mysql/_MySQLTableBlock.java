package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner._TableBlock;

import java.util.List;

public interface _MySQLTableBlock extends _TableBlock {

    /**
     * @return a unmodifiable list
     */
    List<String> partitionList();

    /**
     * @return a unmodifiable list
     */
    List<SQLModifier> indexHintList();

    /**
     * @return a unmodifiable list
     */
    List<String> indexNameList();


}
