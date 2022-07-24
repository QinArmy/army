package io.army.session;


import io.army.meta.TableMeta;

/**
 *
 */
public interface GenericSession {

    boolean isReadOnlyStatus();

    boolean isReadonlySession();

    boolean closed();

    boolean hasTransaction();

    String name();

    GenericSessionFactory sessionFactory();

    /**
     * @throws IllegalArgumentException throw,when not found {@link TableMeta}.
     */
    <T> TableMeta<T> tableMeta(Class<T> domainClass);

}
