package io.army.dialect.h2;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;

public enum H2Dialect implements Dialect {

    H214(14);


    private final byte version;

    H2Dialect(int version) {
        this.version = (byte) version;
    }

    @Override
    public final Database database() {
        return Database.H2;
    }

    @Override
    public final int version() {
        return this.version;
    }

    @Override
    public final String toString() {
        return String.format("%s.%s", H2Dialect.class.getSimpleName(), this.name());
    }


    public static H2Dialect from(ServerMeta meta) {
        throw new UnsupportedOperationException();
    }


}
