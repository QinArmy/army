package io.army.dialect;


import io.army.codec.JsonCodec;
import io.army.dialect.mysql.MySQLDialect;
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


    private static final class MockJsonCodec implements JsonCodec {

        @Override
        public String encode(Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object decode(String json) {
            throw new UnsupportedOperationException();
        }


    }//MockJsonCodec


    private static DialectParser createDialectParser(final Dialect dialect) {
        final ServerMeta meta;
        switch (dialect.database()) {
            case MySQL:
                meta = createMySQLServerMeta((MySQLDialect) dialect);
                break;
            case Oracle:
            case PostgreSQL:
            default:
                throw _Exceptions.unexpectedEnum(dialect.database());
        }
        final MappingEnv mappingEnv;
        mappingEnv = MappingEnv.create(false, meta, null, new MockJsonCodec());
        return _DialectFactory.createDialect(new MockDialectEnv(mappingEnv));
    }


    private static ServerMeta createMySQLServerMeta(final MySQLDialect dialect) {
        final ServerMeta.Builder builder;
        builder = ServerMeta.builder()
                .name("MySQL")
                .database(Database.MySQL)
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
        return builder.build();
    }


}
