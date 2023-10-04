package io.army.session;


import io.army.criteria.Visible;
import io.army.meta.TableMeta;

/**
 *
 */
public interface Session extends CloseableSpec, OptionSpec {

    boolean isReadOnlyStatus();

    boolean isReadonlySession();


    boolean inTransaction();

    String name();

    Visible visible();

    boolean isAllowQueryInsert();

    SessionFactory sessionFactory();

    /**
     * @throws IllegalArgumentException throw,when not found {@link TableMeta}.
     */
    <T> TableMeta<T> tableMeta(Class<T> domainClass);

    @Override
    String toString();

}
