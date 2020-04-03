package io.army.criteria.postgre;

import io.army.criteria.DerivedTable;

/**
 * @see <a href="https://www.postgresql.org/docs/12/sql-select.html">Postgre from clause of select about from_item.</a>
 */
public interface PostgreRowsFromTable extends DerivedTable {

    String tableAlias();

}