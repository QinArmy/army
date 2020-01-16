package io.army;

import java.time.ZoneId;

/**
 * 设计为接口的原因
 * <ul>
 *     <li>隐藏实现,控制访问级别</li>
 * </ul>
 */
public interface SessionFactoryBuilder extends SessionFactoryOptions {

    SessionFactoryBuilder setPackagesToScan(String... packagesToScan);


    SessionFactoryBuilder setZoneId(ZoneId schemaZoneId);

    SessionFactory build();

    static SessionFactoryBuilder builder(){
        return new SessionFactoryBuilderImpl();
    }


}
