package io.army.stmt;

import java.util.function.Function;

/**
 * @see SimpleStmt
 * @see PairStmt
 * @see BatchStmt
 */
public interface Stmt {

    boolean hasOptimistic();

    String printSql(Function<String, String> function);

    @Override
    String toString();

}
