package io.army.dialect;

import io.army.criteria.Visible;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.function.Predicate;

final class OtherDmlContext extends StatementContext implements _OtherDmlContext {

    static OtherDmlContext create(ArmyParser parser, Predicate<FieldMeta<?>> predicate, Visible visible) {
        return new OtherDmlContext(parser, predicate, visible);
    }

    private final Predicate<FieldMeta<?>> predicate;


    private OtherDmlContext(ArmyParser parser, Predicate<FieldMeta<?>> predicate, Visible visible) {
        super(parser, visible);
        this.predicate = predicate;
    }

    @Override
    public void appendField(final String tableAlias, final FieldMeta<?> field) {
        if (!this.predicate.test(field)) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
    }

    @Override
    public void appendField(final FieldMeta<?> field) {
        if (!this.predicate.test(field)) {
            throw _Exceptions.unknownColumn(field);
        }
    }


    @Override
    public SimpleStmt build() {
        return Stmts.minSimple(this);
    }


}
