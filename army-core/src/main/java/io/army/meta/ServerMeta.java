package io.army.meta;

import io.army.dialect.Database;

public interface ServerMeta {

    String name();

    Database database();

    String version();

    int major();

    int minor();

    int subMinor();

    boolean supportSavePoint();


}
