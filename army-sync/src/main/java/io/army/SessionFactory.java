package io.army;

import io.army.interceptor.DomainInterceptor;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;

public interface SessionFactory extends GenericSessionFactory {

    Map<TableMeta<?>, List<DomainInterceptor>> domainInterceptorMap();

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
    void close() throws SessionFactoryException;

    ProxySession proxySession();

    SessionBuilder builder();


    interface SessionBuilder {

        SessionBuilder currentSession();

        Session build() throws SessionException;

    }


}
