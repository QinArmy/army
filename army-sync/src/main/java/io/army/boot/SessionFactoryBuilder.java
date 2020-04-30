package io.army.boot;


import io.army.SessionFactory;
import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;

import javax.sql.DataSource;
import java.util.List;

/**
 * 设计为接口的原因
 * <ul>
 *     <li>隐藏实现,控制访问级别</li>
 * </ul>
 */
public interface SessionFactoryBuilder {

    SessionFactoryBuilder datasource(DataSource dataSource);

    SessionFactoryBuilder sqlDialect(SQLDialect sqlDialect);

    SessionFactoryBuilder catalog(String catalog);

    SessionFactoryBuilder schema(String schema);

    SessionFactoryBuilder environment(Environment environment);

    SessionFactoryBuilder shardingMode(ShardingMode shardingMode);

    SessionFactoryBuilder currentSessionContext(Class<?> clazz);

    SessionFactoryBuilder interceptor(SessionFactoryInterceptor interceptor);

    SessionFactoryBuilder name(String sessionFactoryName);

    SessionFactoryBuilder interceptorList(List<SessionFactoryInterceptor> interceptorList);

    SessionFactory build() throws SessionFactoryException;

    static SessionFactoryBuilder builder() {
        return new SessionFactoryBuilderImpl();
    }

    static SessionFactoryBuilder mockBuilder() {
        return new MockSessionFactoryBuilder();
    }


}
