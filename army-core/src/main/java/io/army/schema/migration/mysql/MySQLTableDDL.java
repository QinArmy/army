package io.army.schema.migration.mysql;

import io.army.dialect.DataBase;
import io.army.dialect.TableDDL;

public interface MySQLTableDDL extends TableDDL {

    static MySQLTableDDL newInstance(DataBase dataBase) {
        MySQLTableDDL tableDDL;
        switch (dataBase) {
            case MySQL:
            case MySQL57:
                tableDDL = new MySQL57TableDDL();
                break;
            case MySQL80:
                tableDDL = new MySQL80TableDDL();
                break;
            default:
                throw new IllegalArgumentException(String.format("unsupported %s", dataBase));
        }
        return tableDDL;
    }
}
