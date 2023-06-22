package io.army.dialect;

import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadAccessor;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.List;

/**
 * <p>
 * This class is base class of following <ul>
 * <li>{@link NarrowDmlStmtContext}</li>
 * <li>{@link MultiTableQueryContext}</li>
 * <li>{@link ParensSelectContext}</li>
 * </ul>
 * </p>
 *
 * @see 1.0
 */
abstract class BatchSpecStatementContext extends StatementContext implements BatchSpecContext {


    final List<?> paramList;

    final int paramSize;

    final ReadAccessor accessor;

    final boolean multiStmtBatch;


    private int paramIndex;


    BatchSpecStatementContext(@Nullable StatementContext parentOrOuterContext, _Statement stmt, ArmyParser parser,
                              Visible visible) {
        super(parentOrOuterContext, parser, visible);

        if (stmt instanceof _BatchStatement) {
            this.paramList = ((_BatchStatement) stmt).paramList();
            this.paramSize = this.paramList.size();
        } else {
            this.paramList = null;
            this.paramSize = 0;
        }


        if (this.paramList != null && parentOrOuterContext instanceof MultiStmtContext) {
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
    public final int nextGroup() {
        if (this.accessor == null) {
            // no bug,never here
            throw new IllegalStateException("don't support named literal.");
        }
        final int nextIndex = ++this.paramIndex;
        if (nextIndex < 0 || nextIndex >= this.paramSize) {
            String m = String.format("index[%s] not in [0,%s]", nextIndex, this.paramSize);
            throw new ArrayIndexOutOfBoundsException(m);
        }
        return nextIndex;
    }


    @Override
    public final int groupSize() {
        return this.paramSize;
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
