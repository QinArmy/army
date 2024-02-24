package io.army.dialect;

import io.army.criteria.Selection;
import io.army.meta.FieldMeta;
import io.army.session.SessionSpec;
import io.army.stmt.Stmt;
import io.army.stmt.StmtType;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

final class OtherDqlContext extends StatementContext implements _OtherDqlContext {

    static OtherDqlContext create(@Nullable _SqlContext outerContext, List<? extends Selection> selectionList,
                                  Predicate<FieldMeta<?>> predicate,
                                  ArmyParser parser, SessionSpec sessionSpec) {
        return new OtherDqlContext((StatementContext) outerContext, selectionList, predicate, parser, sessionSpec);
    }

    private final List<? extends Selection> selectionList;

    private final Predicate<FieldMeta<?>> predicate;

    private OtherDqlContext(@Nullable StatementContext outerContext, List<? extends Selection> selectionList,
                            Predicate<FieldMeta<?>> predicate,
                            ArmyParser parser, SessionSpec sessionSpec) {
        super(outerContext, parser, sessionSpec);
        this.selectionList = selectionList;
        this.predicate = predicate;
    }

    @Override
    public void appendField(final String tableAlias, final FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(final FieldMeta<?> field) {
        if (!this.predicate.test(field)) {
            throw _Exceptions.unknownColumn(field);
        }
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE);
        this.parser.safeObjectName(field, sqlBuilder);
    }

    @Override
    public boolean hasOptimistic() {
        return false;
    }

    @Override
    public StmtType stmtType() {
        return StmtType.QUERY;
    }

    @Override
    public List<? extends Selection> selectionList() {
        return this.selectionList;
    }

    @Override
    public Stmt build() {
        return Stmts.queryStmt(this);
    }


}
