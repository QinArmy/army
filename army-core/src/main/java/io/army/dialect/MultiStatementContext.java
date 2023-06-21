package io.army.dialect;


import io.army.criteria.impl.inner._Statement;
import io.army.stmt.MultiStmt;

import java.util.function.BiConsumer;

interface MultiStatementContext extends _PrimaryContext {


    default <S extends _Statement, C extends _PrimaryContext> void appendStmt(BiConsumer<S, C> consumer, S statement, C context) {
        throw new UnsupportedOperationException();
    }

    default <S extends _Statement, C extends BatchSpecContext> void appendBatch(BiConsumer<S, C> consumer, S statement, C context) {
        throw new UnsupportedOperationException();
    }


    void batchStmtStart(int batchSize);

    void addBatchItem(MultiStmt.StmtItem item);

    MultiStatementContext batchStmtEnd();


}
