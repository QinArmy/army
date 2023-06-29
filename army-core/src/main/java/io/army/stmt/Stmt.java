package io.army.stmt;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @see SimpleStmt
 * @see PairStmt
 * @see BatchStmt
 */
public interface Stmt {

    boolean hasOptimistic();


    void printSql(BiConsumer<String, Consumer<String>> beautifyFunc, Consumer<String> appender);

    @Override
    String toString();


}
