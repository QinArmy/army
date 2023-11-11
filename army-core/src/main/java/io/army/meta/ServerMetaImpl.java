package io.army.meta;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Objects;

final class ServerMetaImpl implements ServerMeta {


    static Builder builder() {
        return new ServerMetaBuilder();
    }

    private final String name;

    private final Database database;

    private final Database dialectDatabase;

    private final String catalogName;

    private final String schemaName;

    private final String version;

    private final int major;

    private final int minor;

    private final int subMinor;

    private final Dialect usedDialect;

    private final boolean supportSavePoint;


    private ServerMetaImpl(ServerMetaBuilder builder) {
        this.name = builder.name;
        this.database = builder.database;
        this.catalogName = builder.catalogName;
        this.schemaName = builder.schemaName;

        this.version = builder.version;
        this.major = builder.major;
        this.minor = builder.minor;
        this.subMinor = builder.subMinor;

        this.usedDialect = builder.usedDialect;

        this.supportSavePoint = builder.supportSavePoint;

        if (_StringUtils.isEmpty(this.name)
                || this.database == null
                || _StringUtils.isEmpty(this.version)
                || this.major < 0
                || this.minor < 0
                || this.subMinor < 0
                || this.usedDialect == null) {
            throw new IllegalArgumentException(String.format("server meta %s error.", this));
        } else if (!this.database.isCompatible(this.usedDialect)) {
            throw _Exceptions.databaseNotCompatible(this.usedDialect, this.database);
        }
        this.dialectDatabase = this.usedDialect.database();

    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Database dialectDatabase() {
        return this.dialectDatabase;
    }

    @Override
    public Database serverDatabase() {
        return this.database;
    }

    @Override
    public String catalog() {
        return this.catalogName;
    }

    @Override
    public String schema() {
        return this.schemaName;
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
    public Dialect usedDialect() {
        return this.usedDialect;
    }

    @Override
    public boolean isSupportSavePoints() {
        return this.supportSavePoint;
    }


    @Override
    public boolean meetsMinimum(final int major, final int minor, final int subMinor) {
        final boolean meets;
        if (this.major != major) {
            meets = this.major > major;
        } else if (this.minor != minor) {
            meets = this.minor > minor;
        } else {
            meets = this.subMinor > subMinor;
        }
        return meets;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.database, this.catalogName, this.schemaName,
                this.version, this.major, this.minor, this.subMinor,
                this.usedDialect, this.supportSavePoint
        );
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof ServerMetaImpl) {
            final ServerMetaImpl o = (ServerMetaImpl) obj;
            match = this.name.equals(o.name)
                    && this.database == o.database
                    && Objects.equals(this.catalogName, o.catalogName)
                    && Objects.equals(this.schemaName, o.schemaName)
                    && this.version.equals(o.version)
                    && this.major == o.major
                    && this.minor == o.minor
                    && this.subMinor == o.subMinor
                    && this.usedDialect == o.usedDialect
                    && this.supportSavePoint == o.supportSavePoint;
        } else {
            match = false;
        }
        return match;
    }


    @Override
    public String toString() {
        return _StringUtils.builder()
                .append(ServerMetaImpl.class.getName())
                .append("[name:")
                .append(this.name)
                .append(",database:")
                .append(this.database.name())
                .append(",catalog:")
                .append(this.catalogName)
                .append(",schema:")
                .append(this.schemaName)
                .append(",version:")
                .append(this.version)
                .append(",major:")
                .append(this.major)
                .append(",minor:")
                .append(this.minor)
                .append(",subMinor:")
                .append(this.subMinor)
                .append(",usedDialect:")
                .append(this.usedDialect.name())
                .append(",supportSavePoint:")
                .append(this.supportSavePoint)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append("]")
                .toString();
    }


    private static final class ServerMetaBuilder implements ServerMeta.Builder {

        private String name;

        private Database database;

        private String catalogName;

        private String schemaName;

        private String version;

        private int major;

        private int minor;

        private int subMinor;

        private Dialect usedDialect;

        private boolean supportSavePoint;

        @Override
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder database(Database database) {
            this.database = database;
            return this;
        }

        @Override
        public Builder catalog(String catalogName) {
            this.catalogName = catalogName;
            return this;
        }

        @Override
        public Builder schema(String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        @Override
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        @Override
        public Builder major(int major) {
            this.major = major;
            return this;
        }

        @Override
        public Builder minor(int minor) {
            this.minor = minor;
            return this;
        }

        @Override
        public Builder subMinor(int subMinor) {
            this.subMinor = subMinor;
            return this;
        }

        @Override
        public Builder usedDialect(Dialect dialect) {
            this.usedDialect = dialect;
            return this;
        }

        @Override
        public Builder supportSavePoint(boolean support) {
            this.supportSavePoint = support;
            return this;
        }

        @Override
        public ServerMeta build() {
            return new ServerMetaImpl(this);
        }


    }//ServerMetaBuilder


}
