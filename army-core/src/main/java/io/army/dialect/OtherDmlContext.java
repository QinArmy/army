package io.army.dialect;

import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.function.Predicate;

final class OtherDmlContext extends StatementContext implements _OtherDmlContext {

    static OtherDmlContext create(@Nullable _SqlContext outerContext,Predicate<FieldMeta<?>> predicate
            , ArmyParser parser,  Visible visible) {
        return new OtherDmlContext((StatementContext) outerContext,predicate,parser, visible);
    }

    static OtherDmlContext forChild(@Nullable _SqlContext outerContext,Predicate<FieldMeta<?>> predicate
            ,OtherDmlContext parentContext) {
        return new OtherDmlContext((StatementContext) outerContext, predicate,parentContext);
    }

    private final OtherDmlContext parentContext;
    private final Predicate<FieldMeta<?>> predicate;


    private OtherDmlContext(@Nullable StatementContext outerContext,Predicate<FieldMeta<?>> predicate
            ,ArmyParser parser, Visible visible) {
        super(outerContext,parser, visible);
        this.predicate = predicate;
        this.parentContext = null;
    }

    private OtherDmlContext(@Nullable  StatementContext outerContext, Predicate<FieldMeta<?>> predicate
            ,OtherDmlContext parentContext) {
        super(outerContext, parentContext.parser,parentContext.visible);
        this.predicate = predicate;
        this.parentContext = parentContext;
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
    public _OtherDmlContext parentContext() {
        return this.parentContext;
    }

    @Override
    public SimpleStmt build() {
        return Stmts.minSimple(this);
    }


}
