package io.army.session;

/**
 *
 */
public interface GenericSession {

    default boolean isReadOnlyStatus() {
        throw new UnsupportedOperationException();
    }

    boolean isReadonlySession();

    boolean closed();

    boolean hasTransaction();

    default String name() {
        throw new UnsupportedOperationException();
    }

    GenericSessionFactory sessionFactory();

}
