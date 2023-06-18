package io.army.stmt;

import java.util.function.UnaryOperator;

/**
 * @see SimpleStmt
 * @see PairStmt
 * @see BatchStmt
 */
public interface Stmt {

    boolean hasOptimistic();

    String printSql(UnaryOperator<String> function);

    @Override
    String toString();

}
