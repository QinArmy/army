package io.army.dialect.oracle;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;

public enum OracleDialect implements Dialect {

    // ORACLE10(10)
    ;


    private final byte version;

    OracleDialect(int version) {
        this.version = (byte) version;
    }

    @Override
    public final Database database() {
        return Database.Oracle;
    }

    @Override
    public final int version() {
        return this.version;
    }

    @Override
    public final String toString() {
        return String.format("%s.%s", OracleDialect.class.getSimpleName(), this.name());
    }


    public static OracleDialect from(final ServerMeta meta) {
        //TODO
        throw new UnsupportedOperationException();
    }


}
