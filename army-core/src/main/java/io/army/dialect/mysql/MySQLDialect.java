package io.army.dialect.mysql;


import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;

public enum MySQLDialect implements Dialect {

    MySQL55(55),
    MySQL56(56),
    MySQL57(57),
    MySQL80(80);

    private final byte version;

    MySQLDialect(int version) {
        this.version = (byte) version;
    }

    @Override
    public final Database database() {
        return Database.MySQL;
    }

    @Override
    public final int version() {
        return this.version;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLDialect.class.getSimpleName(), this.name());
    }


    public static MySQLDialect from(final ServerMeta meta) {
        if (meta.database() != Database.MySQL) {
            throw new IllegalArgumentException();
        }
        final MySQLDialect dialect;
        switch (meta.major()) {
            case 5:
                switch (meta.minor()) {
                    case 5:
                        dialect = MySQLDialect.MySQL55;
                        break;
                    case 6:
                        dialect = MySQLDialect.MySQL56;
                        break;
                    case 7:
                        dialect = MySQLDialect.MySQL57;
                        break;
                    default:
                        throw Database.unsupportedVersion(meta);
                }
                break;
            case 8:
                dialect = MySQLDialect.MySQL80;
                break;
            default:
                throw Database.unsupportedVersion(meta);
        }
        return dialect;
    }


}
