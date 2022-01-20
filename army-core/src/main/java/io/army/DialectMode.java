package io.army;

import io.army.dialect.Database;


public enum DialectMode {

    MySQL57((byte) 57) {
        @Override
        public Database database() {
            return Database.MySQL;
        }
    },
    MYSQL80((byte) 80) {
        @Override
        public Database database() {
            return Database.MySQL;
        }
    };


    private final byte version;

    DialectMode(byte version) {
        this.version = version;
    }

    public abstract Database database();


    public final int version() {
        return this.version;
    }


}
