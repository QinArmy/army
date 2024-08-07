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

import io.army.dialect.Database;
import io.army.dialect.MySQLDialect;
import io.army.dialect.PostgreDialect;
import io.army.dialect.sqlite.SQLiteDialect;
import io.army.env.*;
import io.army.example.common.SimpleFieldGeneratorFactory;
import io.army.example.util.FastJsonCodec;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.jdbd.Driver;
import io.jdbd.session.DatabaseSessionFactory;

import java.util.Collections;
import java.util.Map;

public abstract class FactoryUtils {

    private FactoryUtils() {
    }


    public static SyncSessionFactory createArmyBankSyncFactory(final Database database) {

        return SyncFactoryBuilder.builder()
                .name(mapDatabaseToFactoryName(database))
                .packagesToScan(Collections.singletonList("io.army.example.bank.domain"))
                .datasource(DataSourceUtils.createDataSource(database))
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
            case SQLite:
                map.put(ArmyKey.DIALECT.name, SQLiteDialect.SQLite34);
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

        map.put(ArmyKey.SQL_LOG_PARSING_COST_TIME.name, Boolean.TRUE);

        map.put(ArmyKey.SQL_LOG_EXECUTION_COST_TIME.name, Boolean.TRUE);

        map.put(ArmyKey.QUALIFIED_TABLE_NAME_ENABLE.name, Boolean.FALSE);


        map.put(ArmyKey.DATABASE_NAME_MODE.name, NameMode.DEFAULT);
        map.put(ArmyKey.TABLE_NAME_MODE.name, NameMode.DEFAULT);
        map.put(ArmyKey.COLUMN_NAME_MODE.name, NameMode.DEFAULT);
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
            case SQLite:
                name = "sqlite-bank";
                break;
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return name;
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
