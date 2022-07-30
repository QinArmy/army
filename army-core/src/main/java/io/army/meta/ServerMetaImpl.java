package io.army.meta;

import io.army.dialect.Database;

import java.util.Objects;

final class ServerMetaImpl implements ServerMeta {

    static ServerMetaImpl create(String name, Database database, String version, int major, int minor) {
        return new ServerMetaImpl(name, database, version, major, minor);
    }

    private final String name;

    private final Database database;

    private final String version;

    private final int major;

    private final int minor;

    private ServerMetaImpl(String name, Database database, String version, int major, int minor) {
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
    public int hashCode() {
        return Objects.hash(this.name, this.database, this.version, this.major, this.minor);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof ServerMeta) {
            final ServerMeta o = (ServerMeta) obj;
            match = this.name.equals(o.name())
                    && this.database == o.database()
                    && this.version.equals(o.version())
                    && this.major == o.major()
                    && this.minor == o.minor();
        } else {
            match = false;
        }
        return match;
    }


    @Override
    public String toString() {
        return createBuilder()
                .append("[name:")
                .append(this.name)
                .append(",database:")
                .append(this.database.name())
                .append(",version:")
                .append(this.version)
                .append(",major:")
                .append(this.major)
                .append(",minor:")
                .append(this.minor)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append("]")
                .toString();
    }

    private static StringBuilder createBuilder() {
        return new StringBuilder(ServerMetaImpl.class.getSimpleName());
    }


}
