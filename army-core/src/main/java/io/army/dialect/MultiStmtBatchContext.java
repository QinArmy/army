package io.army.dialect;

import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._Statement;
import io.army.meta.FieldMeta;
import io.army.stmt.MultiStmtBatchStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.BiConsumer;

final class MultiStmtBatchContext extends StatementContext implements MultiStatementContext {

    static MultiStmtBatchContext create(ArmyParser parser, Visible visible, int batchSize) {
        if (batchSize < 1) {
            throw new IllegalArgumentException();
        }
        return new MultiStmtBatchContext(parser, visible, batchSize);
    }


    private final int batchSize;

    private boolean optimistic;

    private List<Selection> selectionList;

    private MultiStmtBatchContext(ArmyParser parser, Visible visible, int batchSize) {
        super(null, parser, visible);
        this.batchSize = batchSize;
    }

    @Override
    public boolean hasOptimistic() {
        return this.optimistic;
    }

    @Override
    public <S extends _Statement, C extends _PrimaryContext> void appendStmt(final BiConsumer<S, C> consumer,
                                                                             final S statement, final C context) {
        //no bug,never here
        throw new UnsupportedOperationException();

    }

    @Override
    public <S extends _Statement, C extends MyBatchSpecContext> void appendBatch(final BiConsumer<S, C> consumer,
                                                                                 final S statement, final C context) {

        final int batchSize = this.batchSize;
        final StringBuilder sqlBuilder = this.sqlBuilder;
        if (!(statement instanceof _BatchStatement)
                || batchSize != context.groupSize()
                || statement instanceof _Insert
                || context.sqlBuilder() != sqlBuilder) {
            //no bug,never here
            throw new IllegalArgumentException();
        } else if (this.selectionList != null) {
            //no bug,never here
            throw new IllegalStateException();
        }


        this.optimistic = context.hasOptimistic();
        this.selectionList = context.selectionList();

        for (int i = 0; i < batchSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_SEMICOLON_TWO_LINE);
            }
            if (context.nextGroup() != i) {
                //no bug,never here
                throw new IllegalArgumentException("context batch index error");
            }
            consumer.accept(statement, context);

        }

        if (this.hasParam()) {
            throw _Exceptions.multiStmtDontSupportParam();
        } else if (!this.hasNamedLiteral()) {
            throw _Exceptions.multiStmtForBatchRequiredNamedLiteral();
        }


    }

    @Override
    public MultiStmtBatchStmt build() {
        if (this.selectionList == null) {
            //no bug,never here
            throw new IllegalStateException();
        }
        return Stmts.multiStmtBatchStmt(this, this.batchSize);
    }


    @Override
    public List<? extends Selection> selectionList() {
        final List<? extends Selection> list = this.selectionList;
        if (list == null) {
            //no bug,never here
            throw new IllegalStateException();
        }
        return list;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        // no bug,never here
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendField(FieldMeta<?> field) {
        // no bug,never here
        throw new UnsupportedOperationException();
    }


}
