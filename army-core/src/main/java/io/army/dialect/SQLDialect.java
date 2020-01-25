package io.army.dialect;


import org.springframework.lang.Nullable;

public enum SQLDialect {

    MySQL("MySQL"),
    MySQL57(MySQL.productName,MySQL),
    MySQL80(MySQL.productName,MySQL),

    Oracle("Oracle"),
    OceanBase("OceanBase"),
    Postgre("Postgre"),
    SQL_Server("SQL Server"),
    Db2("Db2");

    private final String productName;

    private final SQLDialect family;

    SQLDialect(String productName) {
        this(productName,null);
    }

    SQLDialect(String productName, @Nullable SQLDialect family) {
        this.productName = productName;
        if(family == null){
            this.family = this;
        }else {
            this.family = family;
        }

    }

    public SQLDialect family(){
        return family;
    }

    public String productName() {
        return productName;
    }

    public static boolean sameFamily(SQLDialect sqlDialectA,SQLDialect sqlDialectB){
        return sqlDialectA.family == sqlDialectB.family;
    }
}
