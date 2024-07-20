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


import io.army.codec.JsonCodec;
import io.army.codec.XmlCodec;
import io.army.criteria.Visible;
import io.army.env.ArmyEnvironment;
import io.army.env.StandardEnvironment;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ServerMeta;
import io.army.option.Option;
import io.army.session.SessionSpec;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public abstract class _MockDialects implements DialectEnv {


    public static DialectParser from(final Dialect dialect) {
        return MockDialectEnv.DIALECT_MAP.computeIfAbsent(dialect, _MockDialects::createDialectParser);
    }

    public static SessionSpec sessionSpecFor(final Visible visible) {
        final SessionSpec mockSession;
        switch (visible) {
            case ONLY_VISIBLE:
                mockSession = MockSession.MOCK_ONLY_VISIBLE;
                break;
            case ONLY_NON_VISIBLE:
                mockSession = MockSession.MOCK_ONLY_NON_VISIBLE;
                break;
            case BOTH:
                mockSession = MockSession.MOCK_BOTH;
                break;
            default:
                throw _Exceptions.unexpectedEnum(visible);
        }
        return mockSession;
    }

    private static void tryLoadJsonCodec() {
        String m = "META-INF/qinarmy/army_json_codec";

    }

    private final ServerMeta serverMeta;


    private _MockDialects(ServerMeta serverMeta) {
        this.serverMeta = serverMeta;
    }

    @Override
    public final String factoryName() {
        return "mock factory";
    }

    @Override
    public final Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap() {
        return Collections.emptyMap();
    }

    @Override
    public final boolean isReactive() {
        return false;
    }

    @Override
    public final ServerMeta serverMeta() {
        return this.serverMeta;
    }

    @Nullable
    @Override
    public final ZoneOffset zoneOffset() {
        return null;
    }

    @Nullable
    @Override
    public final JsonCodec jsonCodec() {
        return null;
    }

    @Nullable
    @Override
    public final XmlCodec xmlCodec() {
        return null;
    }



    private enum MockSession implements SessionSpec {

        MOCK_ONLY_VISIBLE(Visible.ONLY_VISIBLE),
        MOCK_ONLY_NON_VISIBLE(Visible.ONLY_NON_VISIBLE),
        MOCK_BOTH(Visible.BOTH);

        private final Visible visible;

        MockSession(Visible visible) {
            this.visible = visible;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public final <T> T valueOf(Option<T> option) {
            if (option == Option.BACKSLASH_ESCAPES) {
                return (T) Boolean.TRUE;
            }
            return null;
        }

        @Override
        public final Visible visible() {
            return this.visible;
        }


    } // MockSession


    private static final class MockDialectEnv extends _MockDialects {

        private static final ConcurrentMap<Dialect, DialectParser> DIALECT_MAP = _Collections.concurrentHashMap();

        private final ArmyEnvironment env;

        private MockDialectEnv(ServerMeta serverMeta) {
            super(serverMeta);
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
        return DialectParserFactory.createDialectParser(new MockDialectEnv(meta));
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
            case POSTGRE16:
                builder.version("16.0.0")
                        .major(16)
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
