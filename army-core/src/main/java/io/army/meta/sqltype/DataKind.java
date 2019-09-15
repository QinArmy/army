package io.army.meta.sqltype;

public enum DataKind {

    NUMBER(null),
    INT(NUMBER),
    FLOAT(NUMBER),
    DECIMAL(NUMBER),

    TEXT(null),

    DATE_TIME(null),

    DATE(DATE_TIME),
    TIME(DATE_TIME),
    YEAR(DATE_TIME);


    private final DataKind family;

    /**
     * @param family if null ,family is itself
     */
    DataKind(DataKind family) {
        this.family = family == null ? this : family;
    }

    public DataKind family() {
        return family;
    }

    public boolean isTimeType(DataKind dataKind) {
        return dataKind.family() == DataKind.DATE_TIME;
    }

    public boolean isNumber(DataKind dataKind) {
        return dataKind.family() == NUMBER;
    }

    public boolean isText(DataKind dataKind) {
        return dataKind == DataKind.TEXT;
    }


}
