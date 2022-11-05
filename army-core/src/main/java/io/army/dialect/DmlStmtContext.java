package io.army.dialect;

import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadAccessor;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._DmlStatement;
import io.army.lang.Nullable;
import io.army.stmt.DmlStmtParams;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

abstract class DmlStmtContext extends StatementContext implements DmlContext.MultiStmtBatch, DmlStmtParams {


    final boolean versionPredicate;

    private final List<Selection> selectionList;

    private final List<?> paramList;

    private final ReadAccessor accessor;

    private int paramIndex;


    DmlStmtContext(@Nullable StatementContext outerContext, _DmlStatement stmt
            , ArmyParser parser, Visible visible) {
        super(outerContext, parser, visible);

        this.versionPredicate = _DialectUtils.hasOptimistic(stmt.wherePredicateList());

        this.selectionList = null;

        if (stmt instanceof _BatchDml) {
            this.paramList = ((_BatchDml) stmt).paramList();
            if (outerContext instanceof _MultiStatementContext) {
                this.accessor = ObjectAccessorFactory.readOnlyFromInstance(this.paramList.get(0));
                this.paramIndex = 0;
            } else {
                this.accessor = null;
            }
        } else {
            this.paramList = null;
            this.accessor = null;
        }

    }

    @Override
    public final void nextElement() {
        final List<?> paramList = this.paramList;
        if (paramList == null) {
            throw _Exceptions.independentDmlDontSupportNamedValue();
        }
        final int paramSize, paramIndex;
        paramSize = paramList.size();
        paramIndex = this.paramIndex;
        assert paramIndex >= 0 && paramIndex < (paramSize - 1);
        this.paramIndex++;
    }

    @Override
    public final int currentIndex() {
        return this.paramIndex;
    }

    @Override
    public final Stmt build() {
        if (this.accessor != null) {
            //now,multi-multi statement
            throw new UnsupportedOperationException();
        }
        final List<?> paramList = this.paramList;
        final Stmt stmt;
        if (paramList != null) {
            stmt = Stmts.batchDml(this, paramList);
        } else if (this.hasNamedParam()) {
            throw _Exceptions.namedParamInNonBatch();
        } else {
            stmt = Stmts.dml(this);
        }
        return stmt;
    }

    @Override
    public final boolean hasVersion() {
        return this.versionPredicate;
    }


    @Override
    public final List<Selection> selectionList() {
        final List<Selection> list = this.selectionList;
        if (list == null) {
            throw new UnsupportedOperationException();
        }
        return list;
    }


    @Override
    final Object currentRowNamedValue(String name) {
        final List<?> paramList = this.paramList;
        final ReadAccessor accessor = this.accessor;
        if (paramList == null || accessor == null) {
            throw _Exceptions.independentDmlDontSupportNamedValue();
        }
        final int paramSize, paramIndex;
        paramSize = paramList.size();
        paramIndex = this.paramIndex;
        assert paramIndex >= 0 && paramIndex < paramSize;
        return accessor.get(paramList.get(paramIndex), name);
    }


}
