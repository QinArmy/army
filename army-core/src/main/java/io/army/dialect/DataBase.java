package io.army.dialect;


public enum DataBase {

    MySQL,
    MySQL57(MySQL),
    MySQL80(MySQL),

    ;

    private final DataBase family;

    DataBase() {
        this(null);
    }

    DataBase(DataBase family) {
        if (family == null) {
            this.family = family;
        } else {
            this.family = family;
        }
    }

    public DataBase family() {
        return family;
    }
}
