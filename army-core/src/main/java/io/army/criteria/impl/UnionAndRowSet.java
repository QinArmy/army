package io.army.criteria.impl;

import io.army.criteria.DialectStatement;
import io.army.criteria.RowSet;

/**
 * <p>
 * This interface representing the type that is returned by below methods:
 * <ul>
 *     <li> {@link DialectStatement.DialectUnionClause#union()}</li>
 *     <li> {@link DialectStatement.DialectUnionClause#unionAll()}</li>
 *     <li> {@link DialectStatement.DialectUnionClause#unionDistinct()}</li>
 * </ul>
 * </p>
 *  <p>
 *      This package interface
 *  </p>
 *
 * @since 1.0
 */
interface UnionAndRowSet extends RowSet {

    RowSet leftRowSet();

    UnionType unionType();

}
