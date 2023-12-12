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

    StmtType stmtType();

    void printSql(BiConsumer<String, Consumer<String>> beautifyFunc, Consumer<String> appender);

    @Override
    String toString();


    /**
     * <p>This interface representing  pair sql statement spec.
     * <p>This interface is base interface of following :
     * <ul>
     *     <li>{@link PairStmt}</li>
     *     <li>{@link PairBatchStmt}</li>
     * </ul>
     * *
     *
     * @since 1.0
     */
    interface PairStmtSpec extends Stmt {

        SingleSqlStmt firstStmt();

        SingleSqlStmt secondStmt();

    }


}
