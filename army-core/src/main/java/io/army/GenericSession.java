package io.army;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.io.Flushable;
import java.util.List;

/**
 * created  on 2018/9/1.
 */
public interface GenericSession extends AutoCloseable, Flushable {

    SessionOptions options();

    boolean readonly();

    boolean closed();

    GenericSessionFactory sessionFactory();

    void save(IDomain entity);

    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id);

    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id, Visible visible);

    <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList, List<Object> valueList);

    <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible);

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

    boolean hasTransaction();


    @Override
    void close() throws SessionException;

    @Override
    void flush() throws SessionException;
}
