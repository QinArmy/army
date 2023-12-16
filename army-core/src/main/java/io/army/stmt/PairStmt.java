package io.army.stmt;


/**
 * @see SingleSqlStmt
 * @since 0.6.0
 */
public interface PairStmt extends Stmt.PairStmtSpec {

    @Override
    SimpleStmt firstStmt();

    @Override
    SimpleStmt secondStmt();
}
