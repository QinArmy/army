package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner.TableBlock;
import io.army.criteria.mysql.MySQL57IndexHint;

import java.util.List;

public interface MySQLTable57Block extends TableBlock {

    /**
     * @return a unmodifiable list
     */
    List<MySQL57IndexHint> indexHintList();
}
