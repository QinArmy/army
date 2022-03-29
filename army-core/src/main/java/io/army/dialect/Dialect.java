package io.army.dialect;


import io.army.session.Database;

public enum Dialect {

    MySQL57((byte) 57) {
        @Override
        public Database database() {
            return Database.MySQL;
        }
    },
    MySQL80((byte) 80) {
        @Override
        public Database database() {
            return Database.MySQL;
        }
    };


    private final byte version;

    Dialect(byte version) {
        this.version = version;
    }

    public abstract Database database();


    public final int version() {
        return this.version;
    }


}
