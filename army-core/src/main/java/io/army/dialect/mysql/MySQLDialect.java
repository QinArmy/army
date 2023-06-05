package io.army.dialect.mysql;


import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;
import io.army.util._StringUtils;

public enum MySQLDialect implements Dialect {

    MySQL55(55),
    MySQL56(56),
    MySQL57(57),
    MySQL80(80);

    private final byte version;

    MySQLDialect(int version) {
        assert version <= Byte.MAX_VALUE;
        this.version = (byte) version;
    }

    @Override
    public final Database database() {
        return Database.MySQL;
    }


    public final int compareWith(MySQLDialect o) {
        return this.version - o.version;
    }


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


    public static MySQLDialect from(final ServerMeta meta) {
        assert meta.dialectDatabase() == Database.MySQL;

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
