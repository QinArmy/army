package io.army.boot.sync;


import io.army.SessionFactoryException;
import io.army.boot.GenericFactoryBuilder;
import io.army.boot.SessionFactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.env.Environment;
import io.army.interceptor.DomainAdvice;
import io.army.sync.SessionFactory;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

/**
 * 设计为接口的原因
 * <ul>
 *     <li>隐藏实现,控制访问级别</li>
 * </ul>
 */
public interface SessionFactoryBuilder extends GenericFactoryBuilder {

    @Override
    SessionFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs);

    @Override
    SessionFactoryBuilder name(String sessionFactoryName);

    @Override
    SessionFactoryBuilder environment(Environment environment);

    @Override
    SessionFactoryBuilder factoryAdvice(List<SessionFactoryAdvice> factoryAdviceList);

    SessionFactoryBuilder datasource(DataSource dataSource);

    SessionFactoryBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors);

    SessionFactoryBuilder tableCount(int tableCount);

    SessionFactory build() throws SessionFactoryException;

    static SessionFactoryBuilder builder() {
        return new SessionFactoryBuilderImpl();
    }

    static SessionFactoryBuilder mockBuilder() {
        return new MockSessionFactoryBuilder();
    }


}
