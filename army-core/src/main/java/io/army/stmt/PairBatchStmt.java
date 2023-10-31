package io.army.stmt;

public interface PairBatchStmt extends Stmt.PairStmtSpec {

    @Override
    BatchStmt firstStmt();

    @Override
    BatchStmt secondStmt();

}
