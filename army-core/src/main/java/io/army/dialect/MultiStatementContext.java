package io.army.dialect;


import io.army.criteria.impl.inner._Statement;

import java.util.function.BiConsumer;

interface MultiStatementContext extends _SqlContext {


 <S extends _Statement, C extends _PrimaryContext> void appendStmt(BiConsumer<S, C> consumer, S statement, C context);

 <S extends _Statement, C extends MyBatchSpecContext> void appendBatch(BiConsumer<S, C> consumer, S statement, C context);

}
