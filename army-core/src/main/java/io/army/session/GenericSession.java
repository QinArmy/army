package io.army.session;

import io.army.domain.IDomain;
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
    <T extends IDomain> TableMeta<T> table(Class<T> domainClass);

}
