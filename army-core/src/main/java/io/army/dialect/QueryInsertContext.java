package io.army.dialect;

import io.army.criteria.LiteralMode;
import io.army.criteria.Selection;
import io.army.criteria.SubQuery;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Insert;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

final class QueryInsertContext extends StatementContext implements _QueryInsertContext {

    static QueryInsertContext forSingle(_Insert._QueryInsert stmt, ArmyParser dialect, Visible visible) {
        return new QueryInsertContext(dialect, stmt, visible);
    }

    static QueryInsertContext forParent(_Insert._ChildQueryInsert domainStmt, ArmyParser dialect, Visible visible) {
        return new QueryInsertContext(dialect, domainStmt, visible);
    }

    static QueryInsertContext forChild(QueryInsertContext parentContext, _Insert._ChildQueryInsert domainStmt
            , ArmyParser dialect, Visible visible) {
        return new QueryInsertContext(parentContext, domainStmt, dialect, visible);
    }


    private final TableMeta<?> insertTable;

    private final List<FieldMeta<?>> fieldList;

    private final SubQuery subQuery;

    private final List<Selection> selectionList;

    private final boolean duplicateKeyClause;

    private boolean columnListClauseEnd;

    private boolean queryClauseEnd;

    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     *
     * @see #forSingle(_Insert._QueryInsert, ArmyParser, Visible)
     * @see #forParent(_Insert._ChildQueryInsert, ArmyParser, Visible)
     */
    private QueryInsertContext(ArmyParser dialect, _Insert._QueryInsert domainStmt, Visible visible) {
        super(dialect, visible);

        final _Insert._QueryInsert nonChildStmt;
        if (domainStmt instanceof _Insert._ChildQueryInsert) {
            nonChildStmt = ((_Insert._ChildQueryInsert) domainStmt).parentStmt();
        } else {
            nonChildStmt = domainStmt;

        }
        this.insertTable = nonChildStmt.table();
        this.fieldList = nonChildStmt.fieldList();// because have validated by the implementation of Insert
        this.subQuery = nonChildStmt.subQuery();
        this.selectionList = _DialectUtils.flatSelectItem(this.subQuery.selectItemList());

        this.duplicateKeyClause = nonChildStmt instanceof _Insert._SupportConflictClauseSpec;

        assert this.fieldList.size() == this.selectionList.size();

    }

    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     * </p>
     *
     * @see #forChild(QueryInsertContext, _Insert._ChildQueryInsert, ArmyParser, Visible)
     */
    private QueryInsertContext(QueryInsertContext parentContext, _Insert._ChildQueryInsert domainStmt
            , ArmyParser dialect, Visible visible) {
        super(dialect, visible);

        this.insertTable = domainStmt.table();
        this.fieldList = domainStmt.fieldList();// because have validated by the implementation of Insert
        this.subQuery = domainStmt.subQuery();
        this.selectionList = _DialectUtils.flatSelectItem(this.subQuery.selectItemList());

        this.duplicateKeyClause = domainStmt instanceof _Insert._SupportConflictClauseSpec;

        assert this.insertTable instanceof ChildTableMeta
                && parentContext.insertTable == ((ChildTableMeta<?>) this.insertTable).parentMeta()
                && this.fieldList != parentContext.fieldList
                && this.subQuery != parentContext.subQuery;

        assert this.fieldList.size() == this.selectionList.size();
    }


    @Override
    public TableMeta<?> insertTable() {
        return this.insertTable;
    }

    @Override
    public LiteralMode literalMode() {
        // query insert don't prefer literal,always DEFAULT
        return LiteralMode.DEFAULT;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(final FieldMeta<?> field) {
        if (!(this.queryClauseEnd && this.duplicateKeyClause && field.tableMeta() == this.insertTable)) {
            throw _Exceptions.unknownColumn(field);
        }

        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE);
        this.parser.safeObjectName(field, sqlBuilder);
    }

    @Override
    public void appendFieldList() {
        assert !this.columnListClauseEnd;

        final ArmyParser dialect = this.parser;
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_LEFT_PAREN);

        final List<FieldMeta<?>> fieldList = this.fieldList;
        final int fieldSize = fieldList.size();

        for (int i = 0; i < fieldSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            } else {
                sqlBuilder.append(_Constant.SPACE);
            }

            dialect.safeObjectName(fieldList.get(i), sqlBuilder);
        }

        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        this.columnListClauseEnd = true;
    }

    @Override
    public void appendSubQuery() {
        assert this.columnListClauseEnd && !this.queryClauseEnd;

        this.parser.subQueryOfQueryInsert(this, this.subQuery);

        this.queryClauseEnd = true;

    }

    @Override
    public List<Selection> selectionList() {
        return this.selectionList;
    }

    @Override
    public SimpleStmt build() {
        return Stmts.queryStmt(this);
    }


}
