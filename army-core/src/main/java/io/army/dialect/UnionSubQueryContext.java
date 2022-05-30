package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.util._Exceptions;

class UnionSubQueryContext extends StatementContext implements UnionQueryContext, SubQueryContext {

    static UnionSubQueryContext create(_SqlContext outerContext) {
        return new UnionSubQueryContext((StatementContext) outerContext);
    }

    static UnionSubQueryContext forLateral(_SqlContext outerContext) {
        return new LateralUnionSubQueryContext((StatementContext) outerContext);
    }


    private UnionSubQueryContext(StatementContext outerContext) {
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


    private static final class LateralUnionSubQueryContext extends UnionSubQueryContext
            implements LateralSubQueryContext {

        private LateralUnionSubQueryContext(StatementContext outerContext) {
            super(outerContext);
        }

    }// LateralUnionSubQueryContext


}
