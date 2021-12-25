package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.mysql.MySQL57IndexHint;

import java.util.List;

public interface MySQLTable57Block extends _TableBlock {

    /**
     * @return a unmodifiable list
     */
    List<MySQL57IndexHint> indexHintList();
}
