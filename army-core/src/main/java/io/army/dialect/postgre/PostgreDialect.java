package io.army.dialect.postgre;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;

public enum PostgreDialect implements Dialect {

    // POSTGRE8(8)
    ;


    private final byte version;

    PostgreDialect(int version) {
        this.version = (byte) version;
    }


    @Override
    public final Database database() {
        return Database.PostgreSQL;
    }

    @Override
    public final int version() {
        return this.version;
    }

    @Override
    public final String toString() {
        return String.format("%s.%s", PostgreDialect.class.getSimpleName(), this.name());
    }


    public static PostgreDialect from(final ServerMeta meta) {
        //TODO
        throw new UnsupportedOperationException();
    }


}
