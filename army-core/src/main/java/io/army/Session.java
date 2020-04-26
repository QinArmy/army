package io.army;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.util.List;

/**
 * created  on 2018/9/1.
 */
public interface Session extends AutoCloseable {

    SessionOptions options();

    boolean readonly();

    boolean closed();

    SessionFactory sessionFactory();

    void save(IDomain entity);

    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id);

    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id, Visible visible);

    <T extends IDomain> List<T> select(Select select);

    <T extends IDomain> List<T> select(Select select, Visible visible);

    /**
     * @param update will execute singleUpdate dml instance.
     * @return a unmodifiable list, at most two element.
     */
    List<Integer> update(Update update);

    List<Integer> update(Update update, Visible visible);

    void insert(Insert insert);

    int delete(Delete delete);

    boolean showSql();

}
