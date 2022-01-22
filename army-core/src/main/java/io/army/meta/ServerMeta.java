package io.army.meta;

import io.army.Database;
import io.army.lang.Nullable;

public interface ServerMeta {

    String name();

    Database database();

    String version();

    int major();

    int minor();

    boolean meetsMinimum(int major, int minor);

    @Nullable
    default String sessionVar(String name) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default boolean supportSavePoint() {
        throw new UnsupportedOperationException();
    }


}
