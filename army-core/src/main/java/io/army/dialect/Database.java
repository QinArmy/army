package io.army.dialect;


public enum Database {

    MySQL("MySQL", 0),
    MySQL57(MySQL, 57),
    MySQL80(MySQL, 80),

    Oracle("Oracle", 0),

    Postgre("Postgre", 0),
    Postgre11(Postgre, 11),
    Postgre12(Postgre, 12);

    // SQL_Server("SQL Server"),

    // Db2("Db2");

    private final String productName;

    private final Database family;

    private final int version;

    Database(String productName, int version) {
        this.productName = productName;
        this.family = this;
        this.version = version;
    }

    Database(Database family, int version) {
        this.productName = family.productName;
        this.family = family;
        this.version = version;
    }

    public Database family() {
        return this.family;
    }

    public String productName() {
        return productName;
    }

    public int version() {
        return this.version;
    }

    public boolean compatible(Database database) {
        return this.family == database.family && this.version >= database.version;
    }

}
