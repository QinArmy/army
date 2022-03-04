package io.army.session;

/**
 *
 */
public interface GenericSession {

    boolean readonly();

    boolean closed();

    boolean hasTransaction();

    default String name() {
        throw new UnsupportedOperationException();
    }

    GenericSessionFactory sessionFactory();

}
