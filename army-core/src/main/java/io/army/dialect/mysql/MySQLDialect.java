package io.army.dialect.mysql;


import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;
import io.army.util._StringUtils;

/**
 * <p>This enum representing MySQL dialect version.
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/">MySQL</a>
 * @since 1.0
 */
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


    @Override
    public final int compareWith(final Dialect o) {
        if (!(o instanceof MySQLDialect)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return this.version - ((MySQLDialect) o).version;
    }

    @Override
    public final boolean isFamily(final Dialect o) {
        return o instanceof MySQLDialect;
    }


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


    public static MySQLDialect from(final ServerMeta meta) {
        assert meta.serverDatabase() == Database.MySQL;

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
