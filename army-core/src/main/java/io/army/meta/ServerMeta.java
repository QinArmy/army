package io.army.meta;

import io.army.dialect.Database;
import io.army.dialect.Dialect;

public interface ServerMeta {

    String name();

    Database database();

    String version();

    int major();

    int minor();

    Dialect usedDialect();

    boolean meetsMinimum(int major, int minor);

    default boolean isSupportSavePoints() {
        throw new UnsupportedOperationException();
    }

    static Builder builder() {
        return ServerMetaImpl.builder();
    }

    interface Builder {

        Builder name(String name);

        Builder database(Database database);

        Builder version(String version);

        Builder major(int major);

        Builder minor(int minor);

        Builder usedDialect(Dialect dialect);

        Builder supportSavePoint(boolean support);

        ServerMeta build();

    }


}
