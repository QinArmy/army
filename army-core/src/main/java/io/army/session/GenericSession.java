package io.army.session;

/**
 *
 */
public interface GenericSession {

    boolean readonly();

    boolean closed();

    boolean hasTransaction();

    GenericSessionFactory sessionFactory();

}
