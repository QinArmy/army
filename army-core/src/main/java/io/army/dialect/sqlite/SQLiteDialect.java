package io.army.dialect.sqlite;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;
import io.army.util._StringUtils;


/**
 * <p>This enum representing SQLite dialect version.
 *
 * @see <a href="https://www.sqlite.org/index.html">SQLite</a>
 * @since 0.6.0
 */
public enum SQLiteDialect implements Dialect {

    SQLite34(34);

    private final byte version;

    SQLiteDialect(int version) {
        assert version <= Byte.MAX_VALUE;
        this.version = (byte) version;
    }

    @Override
    public final Database database() {
        return Database.SQLite;
    }

    @Override
    public final int compareWith(final Dialect o) throws IllegalArgumentException {
        if (!(o instanceof SQLiteDialect)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return this.version - ((SQLiteDialect) o).version;
    }

    @Override
    public final boolean isFamily(Dialect o) {
        return o instanceof SQLiteDialect;
    }

    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


    public static SQLiteDialect from(final ServerMeta meta) {
        final SQLiteDialect dialect;
        switch (meta.major()) {
            case 3:
                switch (meta.minor()) {
                    case 4:
                        dialect = SQLiteDialect.SQLite34;
                        break;
                    case 3:
                    case 2:
                    default:
                        throw Database.unsupportedVersion(meta);
                }
                break;
            case 2:
            case 1:
            default:
                throw Database.unsupportedVersion(meta);
        }

        return dialect;
    }


}
