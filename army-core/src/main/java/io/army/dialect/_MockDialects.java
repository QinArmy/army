package io.army.dialect;


import io.army.codec.JsonCodec;
import io.army.env.ArmyEnvironment;
import io.army.env.StandardEnvironment;
import io.army.generator.FieldGenerator;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;
import io.army.meta.ServerMeta;
import io.army.util._Exceptions;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class _MockDialects implements _DialectEnv {

    private static final ConcurrentMap<Dialect, DialectParser> DIALECT_MAP = new ConcurrentHashMap<>();


    public static DialectParser from(final Dialect dialect) {
        return DIALECT_MAP.computeIfAbsent(dialect, _MockDialects::createDialect);
    }


    private static DialectParser createDialect(final Dialect mode) {
        final ServerMeta meta;
        switch (mode) {
            case MySQL55:
                meta = new MockServerMeta("MySQL", Database.MySQL, "5.5.36", 5, 5);
                break;
            case MySQL56:
                meta = new MockServerMeta("MySQL", Database.MySQL, "5.6.36", 5, 6);
                break;
            case MySQL57:
                meta = new MockServerMeta("MySQL", Database.MySQL, "5.7.36", 5, 7);
                break;
            case MySQL80:
                meta = new MockServerMeta("MySQL", Database.MySQL, "8.0.27", 8, 0);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return _DialectFactory.createDialect(new MockDialectEnv(new MockMappingEnv(meta)));
    }

    private final MappingEnv mappingEnv;
    private final ServerMeta serverMeta;


    private _MockDialects(MappingEnv mappingEnv) {
        this.mappingEnv = mappingEnv;
        this.serverMeta = mappingEnv.serverMeta();
    }


    @Override
    public final ServerMeta serverMeta() {
        return this.serverMeta;
    }

    @Override
    public final ZoneOffset envZoneId() {
        return this.mappingEnv.zoneOffset();
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


    private static final class MockServerMeta implements ServerMeta {

        private final String name;

        private final Database database;

        private final String version;

        private final int major;

        private final int minor;

        private MockServerMeta(String name, Database database, String version, int major, int minor) {
            this.name = name;
            this.database = database;
            this.version = version;
            this.major = major;
            this.minor = minor;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Database database() {
            return this.database;
        }

        @Override
        public String version() {
            return this.version;
        }

        @Override
        public int major() {
            return this.major;
        }

        @Override
        public int minor() {
            return this.minor;
        }

        @Override
        public boolean meetsMinimum(final int major, final int minor) {
            return this.major > major || (this.major == major && this.minor >= minor);
        }


        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MockServerMeta{");
            sb.append("name='").append(name).append('\'');
            sb.append(", database=").append(database);
            sb.append(", version='").append(version).append('\'');
            sb.append(", major=").append(major);
            sb.append(", minor=").append(minor);
            sb.append('}');
            return sb.toString();
        }


    }//MockServerMeta


    private static final class MockMappingEnv implements MappingEnv {

        private final ServerMeta serverMeta;

        private final ZoneOffset zoneOffset;

        private MockMappingEnv(ServerMeta serverMeta) {
            this.serverMeta = serverMeta;
            this.zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        }

        @Override
        public boolean isReactive() {
            return false;
        }

        @Override
        public ServerMeta serverMeta() {
            return this.serverMeta;
        }

        @Override
        public ZoneOffset zoneOffset() {
            return this.zoneOffset;
        }

        @Override
        public JsonCodec jsonCodec() {
            throw new UnsupportedOperationException();
        }


    }//MockMappingEnv


}
