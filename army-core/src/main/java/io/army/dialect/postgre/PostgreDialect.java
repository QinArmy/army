package io.army.dialect.postgre;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;
import io.army.util._StringUtils;

public enum PostgreDialect implements Dialect {

    POSTGRE11(11),
    POSTGRE12(12),
    POSTGRE13(13),
    POSTGRE14(14),
    POSTGRE15(15);


    private final byte version;

    PostgreDialect(final int version) {
        assert version <= Byte.MAX_VALUE;
        this.version = (byte) version;
    }


    @Override
    public final Database database() {
        return Database.Postgre;
    }


    @Override
    public final int compareWith(final Dialect o) {
        if (!(o instanceof PostgreDialect)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return this.version - ((PostgreDialect) o).version;
    }

    @Override
    public final boolean isFamily(final Dialect o) {
        return o instanceof PostgreDialect;
    }


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


    public static PostgreDialect from(final ServerMeta meta) {
        final PostgreDialect dialect;
        switch (meta.major()) {
            case 11:
                dialect = PostgreDialect.POSTGRE11;
                break;
            case 12:
                dialect = PostgreDialect.POSTGRE12;
                break;
            case 13:
                dialect = PostgreDialect.POSTGRE13;
                break;
            case 14:
                dialect = PostgreDialect.POSTGRE14;
                break;
            case 15:
                dialect = PostgreDialect.POSTGRE15;
                break;
            default:
                throw Database.unsupportedVersion(meta);
        }
        return dialect;

    }


}
