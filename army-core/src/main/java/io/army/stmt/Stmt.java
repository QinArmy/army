package io.army.stmt;

/**
 * @see SimpleStmt
 * @see PairStmt
 * @see BatchStmt
 * @see PairBatchStmt
 */
public interface Stmt {

    default int getTimeout() {
        throw new UnsupportedOperationException();
    }

}
