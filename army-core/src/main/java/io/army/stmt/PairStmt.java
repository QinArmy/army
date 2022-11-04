package io.army.stmt;


public interface PairStmt extends Stmt {

    SimpleStmt firstStmt();

    SimpleStmt secondStmt();
}
