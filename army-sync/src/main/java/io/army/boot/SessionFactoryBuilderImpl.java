package io.army.boot;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.SessionFactoryException;
import io.army.codec.FieldCodec;
import io.army.interceptor.DomainInterceptor;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;

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
        try {
            SessionFactoryImpl sessionFactory = new SessionFactoryImpl(this.name, environment, dataSource
                    , actualDomainInterceptors, actualFieldCodecs);

            if (!CollectionUtils.isEmpty(this.interceptorList)) {
                for (SessionFactoryInterceptor interceptor : interceptorList) {
                    interceptor.beforeInit(sessionFactory);
                }
            }
            // init session factory
            sessionFactory.initSessionFactory();

            if (!CollectionUtils.isEmpty(this.interceptorList)) {
                for (SessionFactoryInterceptor interceptor : interceptorList) {
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


}
