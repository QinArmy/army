package io.army.boot;


import io.army.SessionFactory;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import io.army.env.StandardEnvironment;

import javax.sql.DataSource;
import java.time.ZoneId;

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

    SessionFactoryBuilder environment(StandardEnvironment environment);

    SessionFactory build();

    static SessionFactoryBuilder builder() {
        return new SessionFactoryBuilderImpl();
    }

    static SessionFactoryBuilder mockBuilder() {
        return new MockSessionFactoryBuilder();
    }


}
