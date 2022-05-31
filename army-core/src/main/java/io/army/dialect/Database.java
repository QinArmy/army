package io.army.dialect;


public enum Database {

    MySQL,
    Oracle,
    PostgreSQL,
    H2,
    Firebird;


    @Override
    public final String toString() {
        return String.format("%s.%s", Database.class.getName(), this.name());
    }


}
