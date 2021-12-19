package io.army.dialect.mysql;

import io.army.beans.ReadWrapper;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._BaseSqlContext;
import io.army.dialect._InsertBlock;
import io.army.dialect._ValueInsertContext;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;

final class ChildValueInsertContext extends _BaseSqlContext implements _ValueInsertContext {

    static ChildValueInsertContext child(final _ValueInsertContext parentContext) {
        return new ChildValueInsertContext(parentContext);
    }


    private final _ValueInsertContext parentContext;

    final _InsertBlock childBlock;

    private ChildValueInsertContext(final _ValueInsertContext parentContext) {
        super(parentContext.dialect(), parentContext.tableIndex(), parentContext.visible());
        this.parentContext = parentContext;
        final _InsertBlock childBlock = parentContext.childBlock();
        assert childBlock != null;
        this.childBlock = childBlock;
    }


    @Override
    public List<FieldMeta<?, ?>> fieldLis() {
        throw new UnsupportedOperationException();
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
        return Stmts.simple(this.sqlBuilder.toString(), this.paramList);
    }

    @Override
    public SingleTableMeta<?> table() {
        return this.parentContext.table();
    }

    @Override
    public Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap() {
        return this.parentContext.commonExpMap();
    }

    @Override
    public List<? extends ReadWrapper> domainList() {
        return this.parentContext.domainList();
    }

    @Override
    public _InsertBlock childBlock() {
        return this.childBlock;
    }


}
