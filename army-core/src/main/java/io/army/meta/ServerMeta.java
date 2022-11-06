package io.army.meta;

import io.army.dialect.Database;

public interface ServerMeta {

    String name();

    Database database();

    String version();

    int major();

    int minor();

    boolean meetsMinimum(int major, int minor);

   default   boolean isSupportSavePoints(){
       throw new UnsupportedOperationException();
   }


    static ServerMeta create(String name, Database database, String version, int major, int minor) {
        return ServerMetaImpl.create(name, database, version, major, minor);
    }


}
