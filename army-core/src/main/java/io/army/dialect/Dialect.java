package io.army.dialect;


import io.army.meta.ServerMeta;
import io.army.session.Database;
import io.army.util._Exceptions;

public enum Dialect {

    MySQL56(Database.MySQL, (byte) 56),
    MySQL57(Database.MySQL, (byte) 57),
    MySQL80(Database.MySQL, (byte) 80);


    public final Database database;

    private final byte version;

    Dialect(Database database, byte version) {
        this.database = database;
        this.version = version;
    }

    public final Database database() {
        return this.database;
    }

    public final int version() {
        return this.version;
    }

    @Override
    public final String toString() {
        return String.format("%s.%s", Dialect.class.getName(), this.name());
    }


    public static Dialect from(final ServerMeta meta) {
        final Dialect dialect;
        switch (meta.database()) {
            case MySQL:
                dialect = from(meta);
                break;
            case PostgreSQL:
            case Oracle:
            case H2:
            case Firebird:
            default:
                throw _Exceptions.unexpectedEnum(meta.database());
        }
        return dialect;
    }

    private static Dialect fromMySQL(final ServerMeta meta) {
        final Dialect dialect;
        switch (meta.major()) {
            case 5:
                switch (meta.minor()) {
                    case 6:
                        dialect = Dialect.MySQL56;
                        break;
                    case 7:
                        dialect = Dialect.MySQL57;
                        break;
                    default:
                        throw unsupportedVersion(meta);
                }
                break;
            case 8:
                dialect = Dialect.MySQL80;
                break;
            default:
                throw unsupportedVersion(meta);
        }
        return dialect;
    }


    private static IllegalArgumentException unsupportedVersion(ServerMeta meta) {
        return new IllegalArgumentException(String.format("unsupported %s", meta));
    }


}
