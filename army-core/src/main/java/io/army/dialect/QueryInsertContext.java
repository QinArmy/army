package io.army.dialect;

import io.army.criteria.SubQuery;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._RowSet;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;

import java.util.List;

final class QueryInsertContext extends InsertContext implements _QueryInsertContext {

    static QueryInsertContext forSingle(@Nullable _SqlContext outerContext, _Insert._QueryInsert stmt
            , ArmyParser dialect, Visible visible) {
        return new QueryInsertContext((StatementContext) outerContext, stmt, dialect, visible);
    }

    static QueryInsertContext forParent(@Nullable _SqlContext outerContext, _Insert._ChildQueryInsert domainStmt
            , ArmyParser dialect, Visible visible) {
        assert outerContext == null || outerContext instanceof _MultiStatementContext;
        return new QueryInsertContext((StatementContext) outerContext, domainStmt, dialect, visible);
    }

    static QueryInsertContext forChild(@Nullable _SqlContext outerContext, _Insert._ChildQueryInsert domainStmt
            , QueryInsertContext parentContext) {
        return new QueryInsertContext((StatementContext) outerContext, domainStmt, parentContext);
    }


    private final SubQuery subQuery;

    private final int subQuerySelectionSize;

    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     *
     * @see #forSingle(_SqlContext, _Insert._QueryInsert, ArmyParser, Visible)
     * @see #forParent(_SqlContext, _Insert._ChildQueryInsert, ArmyParser, Visible)
     */
    private QueryInsertContext(@Nullable StatementContext outerContext, _Insert._QueryInsert domainStmt
            , ArmyParser parser, Visible visible) {
        super(outerContext, domainStmt, parser, visible);


        final _Insert._QueryInsert targetStmt;
        if (domainStmt instanceof _Insert._ChildQueryInsert) {
            targetStmt = ((_Insert._ChildQueryInsert) domainStmt).parentStmt();
        } else {
            targetStmt = domainStmt;
            if (this.insertTable instanceof ParentTableMeta) {
                domainStmt.validateOnlyParen();
            }
        }
        this.subQuery = targetStmt.subQuery();
        this.subQuerySelectionSize = ((_RowSet) this.subQuery).selectionSize();
        assert this.fieldList.size() == this.subQuerySelectionSize;

    }

    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     * </p>
     *
     * @see #forChild(_SqlContext, _Insert._ChildQueryInsert, QueryInsertContext)
     */
    private QueryInsertContext(@Nullable StatementContext outerContext, _Insert._ChildQueryInsert domainStmt
            , QueryInsertContext parentContext) {
        super(outerContext, domainStmt, parentContext);

        this.subQuery = domainStmt.subQuery();

        assert this.insertTable instanceof ChildTableMeta
                && parentContext.insertTable == ((ChildTableMeta<?>) this.insertTable).parentMeta()
                && this.fieldList != parentContext.fieldList
                && this.subQuery != parentContext.subQuery;

        this.subQuerySelectionSize = ((_RowSet) this.subQuery).selectionSize();
        assert this.fieldList.size() == this.subQuerySelectionSize;
    }


    @Override
    int doAppendSubQuery(final int outputColumnSize, final List<FieldMeta<?>> fieldList) {

        assert outputColumnSize == this.subQuerySelectionSize;
        this.parser.handleQuery(this.subQuery, this);
        return this.subQuerySelectionSize;
    }



    @Override
    public SimpleStmt build() {
        final SimpleStmt stmt;
        if (this.returningList.size() == 0) {
            stmt = Stmts.minSimple(this);
        } else {
            stmt = Stmts.queryStmt(this);
        }
        return stmt;
    }


}
