package io.army;

import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.domain.IDomain;

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

    /**
     * @param update will execute singleUpdate dml instance.
     * @return a unmodifiable list, at most two element.
     */
    List<Integer> update(Update update);

    List<Integer> update(Update update, Visible visible);

    boolean showSql();

}
