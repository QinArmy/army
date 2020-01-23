package io.army.dialect;


import org.springframework.lang.Nullable;

public enum DataBase {

    MySQL("MySQL"),
    MySQL57(MySQL.productName,MySQL),
    MySQL80(MySQL.productName,MySQL),

    Oracle("Oracle"),
    OceanBase("OceanBase"),
    Postgre("Postgre"),
    SQL_Server("SQL Server"),
    Db2("Db2");

    private final String productName;

    private final DataBase dataBase;

    DataBase(String productName) {
        this(productName,null);
    }

    DataBase(String productName,@Nullable DataBase dataBase) {
        this.productName = productName;
        if(dataBase == null){
            this.dataBase = this;
        }else {
            this.dataBase = dataBase;
        }

    }

    public DataBase dataBase(){
        return dataBase;
    }

    public String productName() {
        return productName;
    }
}
