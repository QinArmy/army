package io.army.dialect;

import io.army.criteria.Visible;
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;

final class LiteralMultiStmtContext extends StatementContext {


    LiteralMultiStmtContext(ArmyParser parser, Visible visible) {
        super(null, parser, visible);
    }


    @Override
    public SimpleStmt build() {
        return null;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {

    }

    @Override
    public void appendField(FieldMeta<?> field) {

    }
}
