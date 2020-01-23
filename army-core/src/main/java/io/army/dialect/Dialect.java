package io.army.dialect;

import javax.annotation.Nonnull;

/**
 * A common interface to all dialect of database.
 * created  on 2019-02-22.
 */
public interface Dialect extends TableDDL,  TableDML, TableDQL  {

    DataBase database();


    boolean supportZoneId();



}
