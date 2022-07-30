package io.army.dialect;


import io.army.dialect.h2.H2Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.dialect.oracle.OracleDialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.meta.ServerMeta;
import io.army.util._Exceptions;

import java.util.function.Function;
import java.util.function.Supplier;


public enum Database {

    MySQL(MySQLDialect::values, MySQLDialect::valueOf),
    Oracle(OracleDialect::values, OracleDialect::valueOf),
    PostgreSQL(PostgreDialect::values, PostgreDialect::valueOf),
    H2(H2Dialect::values, H2Dialect::valueOf);


    private final Supplier<Dialect[]> supplier;

    private final Function<String, Dialect> function;


    Database(Supplier<Dialect[]> supplier, Function<String, Dialect> function) {
        this.supplier = supplier;
        this.function = function;
    }


    public final Dialect[] dialects() {
        return this.supplier.get();
    }

    public final Dialect dialectOf(String name) {
        return this.function.apply(name);
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", Database.class.getName(), this.name());
    }

    public static Dialect from(final ServerMeta meta) {
        final Dialect dialect;
        switch (meta.database()) {
            case MySQL:
                dialect = MySQLDialect.from(meta);
                break;
            case PostgreSQL:
                dialect = PostgreDialect.from(meta);
                break;
            case Oracle:
                dialect = OracleDialect.from(meta);
                break;
            case H2:
                dialect = H2Dialect.from(meta);
                break;
            default:
                throw _Exceptions.unexpectedEnum(meta.database());
        }
        return dialect;
    }


    public static IllegalArgumentException unsupportedVersion(ServerMeta meta) {
        return new IllegalArgumentException(String.format("unsupported %s", meta));
    }


}
