/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.spring.sync;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.core.env.Environment;

import java.util.Properties;

public abstract class HikariDataSourceUtils extends DataSourceUtils {

    private HikariDataSourceUtils() {
        throw new UnsupportedOperationException();
    }


    public static HikariDataSource createDataSource(Environment env, Properties properties, final String tag, String role) {
        final HikariConfig config;
        config = createHikariConfig(env, properties, tag, role);
        return new HikariDataSource(config);
    }


    public static HikariConfig createHikariConfig(Environment env, Properties properties, final String tag, String role) {
        HikariConfig ds = new HikariConfig();

        final String url;
        url = putJdbcProperties(env, properties, tag, role);

        ds.setJdbcUrl(url);
        ds.setDataSourceProperties(properties);
        ds.setDriverClassName(getDriver(env, tag));
        ds.setConnectionTestQuery(env.getProperty(String.format("spring.datasource.%s.%s.connectionTestQuery", tag, role), "SELECT 1 "));

        ds.setKeepaliveTime(env.getProperty(String.format("spring.datasource.%s.%s.keepaliveTime", tag, role), Long.class, 0L));

        return ds;

    }


}
