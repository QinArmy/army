package io.army.stmt;

public interface PairBatchStmt extends Stmt {

    BatchStmt firstStmt();

    BatchStmt secondStmt();

}
