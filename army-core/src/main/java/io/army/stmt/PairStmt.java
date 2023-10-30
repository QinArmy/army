package io.army.stmt;


/**
 * @see SingleSqlStmt
 * @since 1.0
 */
public interface PairStmt extends Stmt {

    SimpleStmt firstStmt();

    SimpleStmt secondStmt();
}
