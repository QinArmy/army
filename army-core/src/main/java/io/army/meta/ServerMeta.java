package io.army.meta;

import io.army.session.Database;

public interface ServerMeta {

    String name();

    Database database();

    String version();

    int major();

    int minor();

    boolean meetsMinimum(int major, int minor);


}
