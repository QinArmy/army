package io.army;

import io.army.criteria.SingleUpdateAble;
import io.army.criteria.Visible;
import io.army.domain.IDomain;

import java.io.Closeable;
import java.io.Serializable;
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
     *
     * @param updateAble will execute update sql instance.
     * @return a unmodifiable list, at most two element.
     */
    List<Integer> update(SingleUpdateAble updateAble);

    List<Integer> update(SingleUpdateAble updateAble, Visible visible);

}
