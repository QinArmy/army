package io.army.boot;


import io.army.SessionFactoryException;
import io.army.codec.FieldCodec;
import io.army.env.Environment;
import io.army.interceptor.DomainInterceptor;
import io.army.sync.SessionFactory;

import javax.sql.DataSource;
import java.util.Collection;

/**
 * 设计为接口的原因
 * <ul>
 *     <li>隐藏实现,控制访问级别</li>
 * </ul>
 */
public interface SessionFactoryBuilder {

    SessionFactoryBuilder datasource(DataSource dataSource);

    SessionFactoryBuilder environment(Environment environment);

    SessionFactoryBuilder domainInterceptor(Collection<DomainInterceptor> domainInterceptors);

    SessionFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs);

    SessionFactoryBuilder name(String sessionFactoryName);

    SessionFactoryBuilder factoryAdvice(Collection<SessionFactoryAdvice> interceptorList);

    SessionFactory build() throws SessionFactoryException;

    static SessionFactoryBuilder builder() {
        return new SessionFactoryBuilderImpl();
    }

    static SessionFactoryBuilder mockBuilder() {
        return new MockSessionFactoryBuilder();
    }


}
