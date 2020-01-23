package io.army.boot;


import io.army.SessionFactory;
import io.army.SessionFactoryOptions;
import io.army.dialect.Dialect;

import java.time.ZoneId;

/**
 * 设计为接口的原因
 * <ul>
 *     <li>隐藏实现,控制访问级别</li>
 * </ul>
 */
public interface SessionFactoryBuilder extends SessionFactoryOptions {

    SessionFactoryBuilder packagesToScan(String... packagesToScan);

    SessionFactoryBuilder zoneId(ZoneId schemaZoneId);

    SessionFactoryBuilder dialect(Dialect dialect);

    SessionFactory build();

    static SessionFactoryBuilder builder(){
        return new SessionFactoryBuilderImpl();
    }


}
