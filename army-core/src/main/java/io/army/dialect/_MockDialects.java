package io.army.dialect;


import io.army.bean.ObjectAccessor;
import io.army.bean.ReadWrapper;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.env.StandardEnvironment;
import io.army.mapping.MappingEnvironment;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class _MockDialects {

    private static final ConcurrentMap<Dialect, _Dialect> DIALECT_MAP = new ConcurrentHashMap<>();

    public static _Dialect from(final Dialect dialect) {
        return DIALECT_MAP.computeIfAbsent(dialect, _MockDialects::createDialect);
    }


    private static _Dialect createDialect(final Dialect mode) {
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
        return _DialectFactory.createDialect(new MockEnvironment(meta));
    }

    final ServerMeta serverMeta;


    private _MockDialects(ServerMeta serverMeta) {
        this.serverMeta = serverMeta;
    }


    private static final class MockEnvironment extends _MockDialects implements _DialectEnv {

        private final ArmyEnvironment env;

        private MockEnvironment(ServerMeta serverMeta) {
            super(serverMeta);
            this.env = StandardEnvironment.from(Collections.emptyMap());
        }

        @Override
        public ServerMeta serverMeta() {
            return this.serverMeta;
        }

        @Override
        public ZoneOffset zoneOffset() {
            return _TimeUtils.systemZoneOffset();
        }

        @Override
        public _FieldValueGenerator fieldValuesGenerator() {
            return MockDomainValuesGenerator.INSTANCE;
        }

        @Override
        public MappingEnvironment mappingEnvironment() {
            return null;
        }

        @Override
        public ArmyEnvironment environment() {
            return this.env;
        }

    }//MockEnvironment


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


    private static final class MockDomainValuesGenerator extends _AbstractFieldValuesGenerator {

        private static final MockDomainValuesGenerator INSTANCE = new MockDomainValuesGenerator();

        @Override
        protected ZoneOffset zoneOffset() {
            return _TimeUtils.systemZoneOffset();
        }

        @Override
        protected void generatorChan(TableMeta<?> table, IDomain domain, ObjectAccessor accessor, ReadWrapper readWrapper) {
            for (FieldMeta<?> field : table.fieldChain()) {
                accessor.set(domain, field.fieldName(), null);
            }
            if (table instanceof ChildTableMeta) {
                for (FieldMeta<?> field : ((ChildTableMeta<?>) table).parentMeta().fieldChain()) {
                    accessor.set(domain, field.fieldName(), null);
                }
            }

        }

    }//MockDomainValuesGenerator


}
