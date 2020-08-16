package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.criteria.mysql.MySQLIndexHint;

import java.util.List;

@DeveloperForbid
public interface MySQLTable57Wrapper extends TableWrapper {

    /**
     * @return a unmodifiable list
     */
    List<MySQLIndexHint> indexHintList();
}
