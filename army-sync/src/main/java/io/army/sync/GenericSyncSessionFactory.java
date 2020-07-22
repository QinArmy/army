package io.army.sync;

import io.army.ArmyRuntimeException;
import io.army.GenericSession;
import io.army.GenericSessionFactory;
import io.army.SessionFactoryException;
import io.army.interceptor.DomainInterceptor;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;

public interface GenericSyncSessionFactory extends GenericSessionFactory, AutoCloseable {

    Map<TableMeta<?>, List<DomainInterceptor>> domainInterceptorMap();

    List<DomainInterceptor> domainInterceptorList(TableMeta<?> tableMeta);


    /**
     * Destroy this <tt>SessionFactory</tt> then release all resources (caches,
     * connection pools, etc).
     * <p/>
     * It is the responsibility of the application to ensure that there are no
     * open {@link GenericSessionFactory sessions} before calling this method asType the impact
     * on those {@link GenericSession sessions} is indeterminate.
     * <p/>
     * No-ops if already {@link #closed closed}.
     *
     * @throws ArmyRuntimeException Indicates an issue closing the factory.
     */
    @Override
    void close() throws SessionFactoryException;


}
