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

package io.army.dialect;


import io.army.dialect.mysql.MySQLDialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.env.ArmyEnvironment;
import io.army.env.StandardEnvironment;
import io.army.generator.FieldGenerator;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;
import io.army.meta.ServerMeta;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class _MockDialects implements DialectEnv {

    private static final ConcurrentMap<Dialect, DialectParser> DIALECT_MAP = new ConcurrentHashMap<>();


    public static DialectParser from(final Dialect dialect) {
        return DIALECT_MAP.computeIfAbsent(dialect, _MockDialects::createDialectParser);
    }

    private static void tryLoadJsonCodec() {
        String m = "META-INF/qinarmy/army_json_codec";

    }


    private final MappingEnv mappingEnv;
    private final ServerMeta serverMeta;


    private _MockDialects(MappingEnv mappingEnv) {
        this.mappingEnv = mappingEnv;
        this.serverMeta = mappingEnv.serverMeta();
    }

    @Override
    public String factoryName() {
        return "mock factory";
    }

    @Override
    public final Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap() {
        return Collections.emptyMap();
    }

    @Override
    public final MappingEnv mappingEnv() {
        return this.mappingEnv;
    }


    private static final class MockDialectEnv extends _MockDialects {

        private final ArmyEnvironment env;

        private MockDialectEnv(MappingEnv mappingEnv) {
            super(mappingEnv);
            this.env = StandardEnvironment.from(Collections.emptyMap());
        }

        @Override
        public ArmyEnvironment environment() {
            return this.env;
        }


    }//MockDialectEnv


    private static DialectParser createDialectParser(final Dialect dialect) {
        final ServerMeta meta;
        switch (dialect.database()) {
            case MySQL:
                meta = createMySQLServerMeta((MySQLDialect) dialect);
                break;
            case PostgreSQL:
                meta = createPostgreServerMeta((PostgreDialect) dialect);
                break;
            case Oracle:
            default:
                throw _Exceptions.unexpectedEnum(dialect.database());
        }
        final MappingEnv mappingEnv;
        mappingEnv = MappingEnv.builder()
                .serverMeta(meta)
                .build();
        return DialectParserFactory.createDialect(new MockDialectEnv(mappingEnv));
    }


    private static ServerMeta createMySQLServerMeta(final MySQLDialect dialect) {
        final ServerMeta.Builder builder;
        builder = ServerMeta.builder()
                .name("MySQL")
                .database(Database.MySQL)
                .catalog("mock")
                .usedDialect(dialect)
                .supportSavePoint(true);
        switch (dialect) {
            case MySQL55:
                builder.version("5.5.36")
                        .major(5)
                        .minor(5);
                break;
            case MySQL56:
                builder.version("5.6.36")
                        .major(5)
                        .minor(6);
                break;
            case MySQL57:
                builder.version("5.7.36")
                        .major(5)
                        .minor(7);
                break;
            case MySQL80:
                builder.version("8.0.27")
                        .major(8)
                        .minor(0);
                break;
            default:
                throw _Exceptions.unexpectedEnum(dialect);
        }
        return builder
                .driverSpi("java.sql")
                .build();
    }

    private static ServerMeta createPostgreServerMeta(final PostgreDialect dialect) {
        final ServerMeta.Builder builder;
        builder = ServerMeta.builder()
                .name("PostgreSQL")
                .database(Database.PostgreSQL)
                .catalog("army")
                .schema("mock")
                .usedDialect(dialect)
                .supportSavePoint(true);

        switch (dialect) {
            case POSTGRE11:
                builder.version("11.1.20")
                        .major(11)
                        .minor(1);
                break;
            case POSTGRE12:
                builder.version("12.5.13")
                        .major(12)
                        .minor(5);
                break;
            case POSTGRE13:
                builder.version("13.6.36")
                        .major(13)
                        .minor(6);
                break;
            case POSTGRE14:
                builder.version("14.7.36")
                        .major(14)
                        .minor(7);
                break;
            case POSTGRE15:
                builder.version("15.0.0")
                        .major(15)
                        .minor(0);
                break;
            default:
                throw _Exceptions.unexpectedEnum(dialect);
        }

        return builder
                .driverSpi("java.sql")
                .build();
    }


}
