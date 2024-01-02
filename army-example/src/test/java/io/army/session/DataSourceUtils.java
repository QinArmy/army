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

package io.army.session;


import com.alibaba.druid.pool.DruidDataSource;

import java.util.Properties;


public abstract class DataSourceUtils {

    private DataSourceUtils() {
        throw new UnsupportedOperationException();
    }


    public static DruidDataSource druidDataSourceProps(DruidDataSource ds, String url, Properties properties) {


        ds.setUrl(url);
        ds.setDriverClassName(mapDriverName(url));
        ds.setConnectProperties(properties);

        ds.setInitialSize(10);
        ds.setMaxActive(200);
        ds.setMaxWait(27L * 1000L);
        ds.setValidationQuery("SELECT 1 ");

        ds.setTestOnBorrow(Boolean.FALSE);
        ds.setTestWhileIdle(Boolean.TRUE);
        ds.setTestOnReturn(Boolean.TRUE);
        ds.setTimeBetweenEvictionRunsMillis(5L * 1000L);

        ds.setRemoveAbandoned(Boolean.FALSE);
        ds.setMinEvictableIdleTimeMillis(30000L);
        return ds;
    }

    private static String mapDriverName(final String url) {
        final String driverName;
        if (url.startsWith("jdbc:mysql:")) {
            driverName = com.mysql.cj.jdbc.Driver.class.getName();
        } else if (url.startsWith("jdbc:postgresql:")) {
            driverName = org.postgresql.Driver.class.getName();
        } else {
            throw new IllegalArgumentException();
        }
        return driverName;
    }


}
