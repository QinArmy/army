package io.army.criteria;

import java.util.List;

/**
 * @see <a href="https://www.postgresql.org/docs/12/sql-select.html">Postgre from clause of select about from_item.</a>
 */
public interface PostgreFuncTable extends DerivedTable {

    /**
     * @return a unmodifiable list
     */
    List<PostgreFuncColExp<?>> columnExprList();

}
