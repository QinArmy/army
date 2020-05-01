package io.army.boot;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.SessionFactoryException;
import io.army.codec.FieldCodec;
import io.army.interceptor.DomainInterceptor;
import io.army.util.Assert;

import java.util.*;

final class SessionFactoryBuilderImpl extends AbstractSessionFactoryBuilder {

    SessionFactoryBuilderImpl() {
    }

    @Override
    public SessionFactory build() throws SessionFactoryException {

        Assert.notNull(this.dataSource, "dataSource required");
        Assert.notNull(this.environment, "environment required");

        Collection<DomainInterceptor> actualDomainInterceptors = this.domainInterceptors;
        if (actualDomainInterceptors == null) {
            actualDomainInterceptors = Collections.emptyList();
        }
        Collection<FieldCodec> actualFieldCodecs = this.fieldCodecs;
        if (actualFieldCodecs == null) {
            actualFieldCodecs = Collections.emptyList();
        }

        final List<SessionFactoryInterceptor> factoryInterceptorList = createFactoryInterceptorList();
        try {
            SessionFactoryImpl sessionFactory = new SessionFactoryImpl(this.name, environment, dataSource
                    , actualDomainInterceptors, actualFieldCodecs);

            if (!factoryInterceptorList.isEmpty()) {
                for (SessionFactoryInterceptor interceptor : interceptors) {
                    interceptor.beforeInit(sessionFactory);
                }
            }
            // init session factory
            sessionFactory.initSessionFactory();

            if (!factoryInterceptorList.isEmpty()) {
                for (SessionFactoryInterceptor interceptor : interceptors) {
                    interceptor.afterInit(sessionFactory);
                }
            }
            return sessionFactory;
        } catch (SessionFactoryException e) {
            throw e;
        } catch (Throwable e) {
            throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                    , e, "create session factory error.");
        }
    }



    /*################################## blow private method ##################################*/

    List<SessionFactoryInterceptor> createFactoryInterceptorList() {
        List<SessionFactoryInterceptor> list = new ArrayList<>(this.interceptors);
        list.sort(Comparator.comparingInt(SessionFactoryInterceptor::order));
        return Collections.unmodifiableList(list);
    }

}
