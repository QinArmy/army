package io.army.stmt;


/**
 * @see SingleSqlStmt
 * @since 1.0
 */
public interface PairStmt extends Stmt.PairStmtSpec {

    @Override
    SimpleStmt firstStmt();

    @Override
    SimpleStmt secondStmt();
}
