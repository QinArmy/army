package io.army.dialect;

import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._DmlStatement;
import io.army.criteria.impl.inner._ReturningDml;
import io.army.criteria.impl.inner._SelectItem;
import io.army.lang.Nullable;
import io.army.stmt.DmlStmtParams;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link SingleTableDmlContext}</li>
 *         <li>{@link MultiTableDmlContext}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
abstract class NarrowDmlStmtContext extends BatchSpecStatementContext implements NarrowDmlContext, DmlStmtParams {


    final boolean versionPredicate;

    private final List<? extends _SelectItem> returningList;


    NarrowDmlStmtContext(@Nullable StatementContext parentOrOuterContext, _DmlStatement stmt
            , ArmyParser parser, Visible visible) {
        super(parentOrOuterContext, stmt, parser, visible);

        this.versionPredicate = _DialectUtils.hasOptimistic(stmt.wherePredicateList());

        if (stmt instanceof _ReturningDml) {
            this.returningList = ((_ReturningDml) stmt).returningList();
        } else {
            this.returningList = Collections.emptyList();
        }


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
    public final boolean hasOptimistic() {
        return this.versionPredicate;
    }


    @Override
    public final int idSelectionIndex() {
        //TODO for firebird
        return -1;
    }

    @Override
    public final List<Selection> selectionList() {
        final List<? extends _SelectItem> list = this.returningList;
        if (list == null) {
            throw new UnsupportedOperationException();
        }
        return _DialectUtils.flatSelectItem(list);
    }




}
