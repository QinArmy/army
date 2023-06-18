package io.army.dialect;

import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadAccessor;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.stmt.Stmt;
import io.army.util._Exceptions;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.function.BiConsumer;

abstract class BatchSpecContext extends StatementContext implements MyBatchSpecContext {


    private final List<?> paramList;

    private final int paramSize;

    private final ReadAccessor accessor;

    private final boolean multiStmtBatch;

    private final MultiStatementContext multiStmtContext;

    private int paramIndex;


    BatchSpecContext(@Nullable StatementContext parentOrOuterContext, _Statement stmt, ArmyParser parser,
                     Visible visible) {
        super(parentOrOuterContext, parser, visible);

        if (stmt instanceof _BatchStatement) {
            this.paramList = ((_BatchStatement) stmt).paramList();
            this.paramSize = this.paramList.size();
        } else {
            this.paramList = null;
            this.paramSize = 0;
        }

        if (parentOrOuterContext instanceof MultiStatementContext) {
            this.multiStmtContext = (MultiStatementContext) parentOrOuterContext;
        } else {
            this.multiStmtContext = null;
        }

        if (this.paramList != null && parentOrOuterContext instanceof MultiStatementContext) {
            this.accessor = ObjectAccessorFactory.readOnlyFromInstance(this.paramList.get(0));
            this.multiStmtBatch = parentOrOuterContext instanceof MultiStmtBatchContext;
            this.paramIndex = 0;
        } else {
            this.accessor = null;
            this.multiStmtBatch = false;
            this.paramIndex = -1;

        }

    }


    @Override
    public final <S extends _Statement, C extends MyBatchSpecContext> void multiStmtBatch(final BiConsumer<S, C> consumer, final S statement, final C context) {
        final List<?> paramList = this.paramList;
        int currentIndex = this.paramIndex;
        if (context != this) {
            //no bug,never here
            throw new IllegalArgumentException();
        } else if (paramList == null) {
            //no bug,never here
            throw _Exceptions.independentDmlDontSupportNamedValue();
        } else if (!this.multiStmtBatch || currentIndex != 0) {
            //no bug,never here
            throw new IllegalStateException("not multi-statement batch statement");
        }
        final StringBuilder sqlBuilder = this.sqlBuilder;
        final int paramSize = this.paramSize;
        for (int i = 0; i < paramSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_SEMICOLON_TWO_LINE);
            }
            consumer.accept(statement, context);
            currentIndex++;
            this.paramIndex++;
        }

        if (currentIndex != this.paramIndex) {
            //no bug,never here
            throw new ConcurrentModificationException();
        }

    }

    @Override
    public final int groupSize() {
        return this.paramSize;
    }

    @Override
    public final Stmt build() {
        return null;
    }


    @Override
    final Object currentRowNamedValue(final String name) {
        final List<?> paramList = this.paramList;
        final ReadAccessor accessor = this.accessor;
        if (paramList == null || accessor == null) {
            throw _Exceptions.independentDmlDontSupportNamedValue();
        }
        final int paramIndex;
        paramIndex = this.paramIndex;
        if (!(paramIndex > -1 && paramIndex < this.paramSize)) {
            //no bug,never here
            throw new IllegalStateException("paramIndex error");
        }
        return accessor.get(paramList.get(paramIndex), name);
    }


}
