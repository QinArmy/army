package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.stmt.Stmt;
import io.army.util._Exceptions;

final class UnionSubQueryContext extends _BaseSqlContext implements _UnionQueryContext, _SubQueryContext {

    static UnionSubQueryContext create(_SqlContext outerContext) {
        return new UnionSubQueryContext((_BaseSqlContext) outerContext);
    }


    private UnionSubQueryContext(_BaseSqlContext outerContext) {
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
    public void appendOuterField(String tableAlias, FieldMeta<?> field) {
        this.appendOuterField(field);
    }

    @Override
    public void appendOuterField(FieldMeta<?> field) {
        throw new UnsupportedOperationException("Union sub query context don't support this operation.");
    }

    @Override
    public Stmt build() {
        throw SimpleSubQueryContext.dontSupportBuild();
    }


}
