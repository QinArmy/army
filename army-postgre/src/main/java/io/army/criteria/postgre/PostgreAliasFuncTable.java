package io.army.criteria.postgre;

import io.army.criteria.DerivedTable;

import java.util.List;

/**
 * @see <a href="https://www.postgresql.org/docs/12/sql-select.html">Postgre from clause of select about from_item.</a>
 */
public interface PostgreAliasFuncTable extends DerivedTable {

    String tableAlias();

    /**
     * @return a unmodifiable list
     */
    List<PostgreFuncColExp<?>> columnExprList();
}
