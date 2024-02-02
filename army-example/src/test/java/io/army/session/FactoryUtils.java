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
import io.army.dialect.Database;
import io.army.dialect.mysql.MySQLDialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SqlLogMode;
import io.army.env.StandardEnvironment;
import io.army.example.common.SimpleFieldGeneratorFactory;
import io.army.example.util.FastJsonCodec;
import io.army.reactive.ReactiveFactoryBuilder;
import io.army.reactive.ReactiveSessionFactory;
import io.army.sync.SyncFactoryBuilder;
import io.army.sync.SyncSessionFactory;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.jdbd.Driver;
import io.jdbd.session.DatabaseSessionFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public abstract class FactoryUtils {

    private FactoryUtils() {
    }


    public static SyncSessionFactory createArmyBankSyncFactory(final Database database) {
        final Properties properties = new Properties();
        properties.put("user", "army_w");
        properties.put("password", "army123");

        if (MyPaths.isMyLocal()) {
            properties.put("sslMode", "DISABLED");
            if (database == Database.MySQL) {
                properties.put("allowMultiQueries", "true");
                properties.put("allowLoadLocalInfile", "true");
            }

        }

        final DruidDataSource dataSource;
        dataSource = new DruidDataSource();
        DataSourceUtils.druidDataSourceProps(dataSource, mapDatabaseToUrl(database), properties);
        return SyncFactoryBuilder.builder()
                .name(mapDatabaseToFactoryName(database))
                .packagesToScan(Collections.singletonList("io.army.example.bank.domain"))
                .datasource(dataSource)
                .environment(createEnvironment(database))
                .jsonCodec(FastJsonCodec.getInstance())
                .fieldGeneratorFactory(new SimpleFieldGeneratorFactory())
                .build();
    }

    public static ReactiveSessionFactory createArmyBankReactiveFactory(final Database database) {
        final String url;
        url = mapDatabaseToJdbdUrl(database);
        final Map<String, Object> map = _Collections.hashMap();

        map.put(Driver.USER, "army_w");
        map.put(Driver.PASSWORD, "army123");
        map.put("factoryWorkerCount", 10);

        if (MyPaths.isMyLocal()) {
            map.put("sslMode", "DISABLED");
            map.put("allowLoadLocalInfile", Boolean.TRUE);
        }


        final DatabaseSessionFactory databaseSessionFactory;
        databaseSessionFactory = Driver.findDriver(url).forDeveloper(url, map);

        final ReactiveSessionFactory factory;
        factory = ReactiveFactoryBuilder.builder()
                .name(mapDatabaseToFactoryName(database))
                .packagesToScan(Collections.singletonList("io.army.example.bank.domain"))
                .datasource(databaseSessionFactory)
                .environment(createEnvironment(database))
                .fieldGeneratorFactory(new SimpleFieldGeneratorFactory())
                .jsonCodec(FastJsonCodec.getInstance())
                .build()
                .block();

        assert factory != null;
        return factory;
    }


    private static ArmyEnvironment createEnvironment(final Database database) {
        final Map<String, Object> map = _Collections.hashMap();
        map.put(ArmyKey.DATABASE.name, database);
        switch (database) {
            case MySQL:
                map.put(ArmyKey.DIALECT.name, MySQLDialect.MySQL80);
                break;
            case PostgreSQL:
                map.put(ArmyKey.DIALECT.name, PostgreDialect.POSTGRE15);
                break;
            default:
                throw _Exceptions.unexpectedEnum(database);
        }

        map.put(ArmyKey.VISIBLE_MODE.name, AllowMode.SUPPORT);

        map.put(ArmyKey.DATASOURCE_CLOSE_METHOD.name, "close");
        map.put(ArmyKey.QUERY_INSERT_MODE.name, AllowMode.SUPPORT);
        map.put(ArmyKey.DDL_MODE.name, DdlMode.UPDATE);

        // map.put(ArmyKey.SQL_LOG_PRINT_META.name,Boolean.TRUE);
        map.put(ArmyKey.SQL_LOG_MODE.name, SqlLogMode.BEAUTIFY_DEBUG);

        map.put(ArmyKey.SQL_PARSING_COST_TIME.name, Boolean.TRUE);

        map.put(ArmyKey.QUALIFIED_TABLE_NAME_ENABLE.name, Boolean.FALSE);


//         map.put(ArmyKey.DATABASE_NAME_MODE.name, NameMode.UPPER_CASE);
//        map.put(ArmyKey.TABLE_NAME_MODE.name, NameMode.UPPER_CASE);
//        map.put(ArmyKey.COLUMN_NAME_MODE.name, NameMode.UPPER_CASE);
        return StandardEnvironment.from(map);
    }

    private static String mapDatabaseToFactoryName(final Database database) {
        final String name;
        switch (database) {
            case MySQL:
                name = "mysql-bank";
                break;
            case PostgreSQL:
                name = "postgre-bank";
                break;
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return name;
    }


    private static String mapDatabaseToUrl(final Database database) {
        final String url;
        switch (database) {
            case MySQL:
                url = "jdbc:mysql://localhost:3306/army_bank";
                break;
            case PostgreSQL:
                url = "jdbc:postgresql://localhost:5432/army_bank";
                break;
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return url;
    }

    private static String mapDatabaseToJdbdUrl(final Database database) {
        final String url;
        switch (database) {
            case MySQL:
                url = "jdbd:mysql://localhost:3306/army_bank";
                break;
            case PostgreSQL:
                url = "jdbd:postgresql://localhost:5432/army_bank";
                break;
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return url;
    }


}
