package io.army.dialect;

import io.army.criteria.Select;
import io.army.criteria.SelectPart;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._PartQuery;
import io.army.meta.FieldMeta;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

final class UnionSelectContext extends _BaseSqlContext implements _UnionQueryContext, _SelectContext {

    static UnionSelectContext create(Dialect dialect, Select select, Visible visible) {
        return new UnionSelectContext(dialect, select, visible);
    }


    private final List<SelectPart> selectPartList;


    private UnionSelectContext(Dialect dialect, Select select, Visible visible) {
        super(dialect, visible);
        this.selectPartList = ((_PartQuery) select).selectPartList();
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(FieldMeta<?, ?> field) {
        throw _Exceptions.unknownColumn(null, field);
    }

    @Override
    public Stmt build() {
        return Stmts.selectStmt(this.sqlBuilder.toString(), this.paramList, this.selectPartList);
    }


}
