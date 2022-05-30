package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.util._Exceptions;

final class UnionSubQueryContext extends StmtContext implements _UnionQueryContext, _SubQueryContext {

    static UnionSubQueryContext create(_SqlContext outerContext) {
        return new UnionSubQueryContext((StmtContext) outerContext);
    }


    private UnionSubQueryContext(StmtContext outerContext) {
        super(outerContext);
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(null, field);
    }


    @Override
    public void appendThisField(String tableAlias, FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }


    @Override
    public void appendThisField(FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(null, field);
    }

    @Override
    public SimpleStmt build() {
        throw SimpleSubQueryContext.dontSupportBuild();
    }


}
