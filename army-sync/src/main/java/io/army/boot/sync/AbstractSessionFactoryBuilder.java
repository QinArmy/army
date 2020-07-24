package io.army.boot.sync;

import io.army.codec.FieldCodec;
import io.army.env.Environment;
import io.army.interceptor.DomainInterceptor;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;

abstract class AbstractSessionFactoryBuilder implements SessionFactoryBuilder {

    DataSource dataSource;

    Environment environment;

    String name;

    Collection<SessionFactoryAdvice> interceptors;

    Collection<DomainInterceptor> domainInterceptors;

    Collection<FieldCodec> fieldCodecs;

    AbstractSessionFactoryBuilder() {

    }


    @Override
    public final SessionFactoryBuilder datasource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public final SessionFactoryBuilder domainInterceptor(Collection<DomainInterceptor> domainInterceptors) {
        this.domainInterceptors = domainInterceptors;
        return this;
    }

    @Override
    public final SessionFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        this.fieldCodecs = fieldCodecs;
        return this;
    }

    @Override
    public final SessionFactoryBuilder environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    @Override
    public final SessionFactoryBuilder name(String sessionFactoryName) {
        this.name = sessionFactoryName;
        return this;
    }

    @Override
    public final SessionFactoryBuilder factoryAdvice(Collection<SessionFactoryAdvice> interceptorList) {
        if (this.interceptors == null) {
            this.interceptors = new ArrayList<>(interceptorList.size());
        }
        this.interceptors.addAll(interceptorList);
        return this;
    }


}
