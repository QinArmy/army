package io.army.dialect.h2;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;

public enum H2Dialect implements Dialect {

    // H214(14)
    ;


    private final byte version;

    H2Dialect(int version) {
        this.version = (byte) version;
    }

    @Override
    public final Database database() {
        return Database.H2;
    }

    @Override
    public final int compareWith(Dialect o) throws IllegalArgumentException {
        if (!(o instanceof H2Dialect)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return this.version - ((H2Dialect) o).version;
    }

    @Override
    public final boolean isFamily(Dialect o) {
        return o instanceof H2Dialect;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", H2Dialect.class.getSimpleName(), this.name());
    }


    public static H2Dialect from(ServerMeta meta) {
        throw new UnsupportedOperationException();
    }


}
