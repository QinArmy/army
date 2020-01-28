package io.army.boot;


import io.army.SessionFactory;
import io.army.SessionFactoryOptions;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;

import javax.sql.DataSource;
import java.time.ZoneId;

/**
 * 设计为接口的原因
 * <ul>
 *     <li>隐藏实现,控制访问级别</li>
 * </ul>
 */
public interface SessionFactoryBuilder  {

    SessionFactoryBuilder packagesToScan(String... packagesToScan);

    SessionFactoryBuilder zoneId(ZoneId schemaZoneId);

    SessionFactoryBuilder datasource(DataSource dataSource);

    SessionFactoryBuilder sqlDialect(SQLDialect sqlDialect);

    SessionFactoryBuilder readonly(boolean readonly);

    SessionFactoryBuilder showSql(boolean formatSql);

    SessionFactoryBuilder formatSql(boolean formatSql);

    SessionFactoryBuilder catalog(String catalog);

    SessionFactoryBuilder schema(String schema);

    SessionFactory build();

    static SessionFactoryBuilder builder(){
        return new SessionFactoryBuilderImpl();
    }


}
