package io.army.dialect;

import io.army.criteria.NullMode;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;

import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract class ValuesSyntaxInsertContext extends InsertContext implements _ValueInsertContext {


    final NullMode nullMode;

    ValuesSyntaxInsertContext(ArmyParser parser, _Insert._ValuesSyntaxInsert stmt, Visible visible) {
        super(parser, stmt, visible);
        if (stmt instanceof _Insert._ChildInsert) {
            this.nullMode = ((_Insert._ValuesSyntaxInsert) ((_Insert._ChildInsert) stmt).parentStmt()).nullHandle();
        } else {
            this.nullMode = stmt.nullHandle();
        }
    }

    ValuesSyntaxInsertContext(StatementContext outerContext, _Insert._ValuesSyntaxInsert stmt) {
        super(outerContext, stmt);
        if (stmt instanceof _Insert._ChildInsert) {
            this.nullMode = ((_Insert._ValuesSyntaxInsert) ((_Insert._ChildInsert) stmt).parentStmt()).nullHandle();
        } else {
            this.nullMode = stmt.nullHandle();
        }
    }


    ValuesSyntaxInsertContext(_Insert._ChildInsert stmt, ValuesSyntaxInsertContext parentContext) {
        super(parentContext.parser, stmt, parentContext.visible);
        this.nullMode = ((_Insert._ValuesSyntaxInsert) stmt).nullHandle();
        assert this.nullMode == parentContext.nullMode;
    }

    ValuesSyntaxInsertContext(_Insert._ChildInsert stmt, ValuesSyntaxInsertContext parentContext
            , StatementContext outerContext) {
        super(stmt, parentContext, outerContext);
        this.nullMode = ((_Insert._ValuesSyntaxInsert) stmt).nullHandle();
        assert this.nullMode == parentContext.nullMode;
    }


    @Override
    public final List<Selection> selectionList() {
        //TODO
        return Collections.emptyList();
    }


    static boolean isManageVisible(TableMeta<?> insertTable, Map<FieldMeta<?>, _Expression> defaultValueMap) {
        return insertTable instanceof SingleTableMeta
                && insertTable.containField(_MetaBridge.VISIBLE)
                && !defaultValueMap.containsKey(insertTable.getField(_MetaBridge.VISIBLE));
    }


    static IllegalStateException parentStmtDontExecute(PrimaryFieldMeta<?> filed) {
        String m = String.format("parent stmt don't execute so %s parameter value is null", filed);
        return new IllegalStateException(m);
    }


}
