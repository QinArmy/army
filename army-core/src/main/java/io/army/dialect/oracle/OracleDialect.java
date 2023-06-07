package io.army.dialect.oracle;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;
import io.army.util._StringUtils;

public enum OracleDialect implements Dialect {

    ORACLE10(10)
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
    public final int compareWith(Dialect o) throws IllegalArgumentException {
        if (!(o instanceof OracleDialect)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return this.version - ((OracleDialect) o).version;
    }

    @Override
    public final boolean isFamily(Dialect o) {
        return o instanceof OracleDialect;
    }

    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


    public static OracleDialect from(final ServerMeta meta) {
        //TODO
        throw new UnsupportedOperationException();
    }


}
