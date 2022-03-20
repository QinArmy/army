package io.army.stmt;


public interface PairStmt extends Stmt {

    SimpleStmt parentStmt();

    SimpleStmt childStmt();
}
