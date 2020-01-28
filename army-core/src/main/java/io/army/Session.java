package io.army;

import io.army.domain.IDomain;

import java.io.Closeable;
import java.io.Serializable;

/**
 * created  on 2018/9/1.
 */
public interface  Session extends AutoCloseable {

    SessionOptions options();

    boolean readonly();

    boolean closed();

    Serializable save(IDomain entity);

}
