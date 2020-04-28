package io.army;

import java.io.Flushable;

/**
 * created  on 2018/9/1.
 */
public interface GenericSession extends AutoCloseable, Flushable {

    SessionOptions options();

    boolean readonly();

    boolean closed();

    GenericSessionFactory sessionFactory();



    boolean showSql();

    boolean hasTransaction();


    @Override
    void close() throws SessionException;

    @Override
    void flush() throws SessionException;
}
