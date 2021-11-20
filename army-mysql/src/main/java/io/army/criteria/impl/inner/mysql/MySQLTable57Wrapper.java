package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner.TableWrapper;
import io.army.criteria.mysql.MySQL57IndexHint;

import java.util.List;

public interface MySQLTable57Wrapper extends TableWrapper {

    /**
     * @return a unmodifiable list
     */
    List<MySQL57IndexHint> indexHintList();
}
