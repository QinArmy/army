package io.army.dialect;

import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
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

    private final List<Selection> querySelectionList;


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

        final _Insert._QueryInsert nonChildStmt;
        if (domainStmt instanceof _Insert._ChildQueryInsert) {
            nonChildStmt = ((_Insert._ChildQueryInsert) domainStmt).parentStmt();
        } else {
            nonChildStmt = domainStmt;

        }
        this.subQuery = nonChildStmt.subQuery();
        this.querySelectionList = _DialectUtils.flatSelectItem(this.subQuery.selectItemList());

        assert this.fieldList.size() == this.querySelectionList.size();

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
        this.querySelectionList = _DialectUtils.flatSelectItem(this.subQuery.selectItemList());

        assert this.insertTable instanceof ChildTableMeta
                && parentContext.insertTable == ((ChildTableMeta<?>) this.insertTable).parentMeta()
                && this.fieldList != parentContext.fieldList
                && this.subQuery != parentContext.subQuery;

        assert this.fieldList.size() == this.querySelectionList.size();
    }


    @Override
    int doAppendSubQuery(final int outputColumnSize, final List<FieldMeta<?>> fieldList) {

        final List<Selection> querySelectionList;
        querySelectionList = this.querySelectionList;
        final int selectionSize;
        selectionSize = querySelectionList.size();

        assert outputColumnSize == selectionSize;
        this.parser.handleQuery(this.subQuery, this);
        return selectionSize;
    }


    @Override
    public List<Selection> selectionList() {
        return this.querySelectionList;
    }

    @Override
    public SimpleStmt build() {
        //TODO postgre
        return Stmts.minSimple(this);
    }


}
