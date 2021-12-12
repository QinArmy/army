package io.army.dialect.mysql;

import io.army.beans.ObjectWrapper;
import io.army.criteria.Visible;
import io.army.criteria._SqlContext;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.dialect.AbstractDmlDialect;
import io.army.dialect._ValueInsertContext;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.stmt.Stmt;

import java.util.List;

class MySQL57DmlDialect extends AbstractDmlDialect {

    MySQL57DmlDialect(MySQL57Dialect dialect) {
        super(dialect);
    }

    /*################################## blow DML method ##################################*/


    /*################################## blow AbstractDML method ##################################*/


    @Override
    public String safeFieldName(FieldMeta<?, ?> fieldMeta) {
        return null;
    }

    @Override
    public String constant(MappingType type, Object value) {
        return null;
    }

    @Override
    protected final void tableOnlyModifier(_SqlContext context) {
        // do nothing .
    }


    @Override
    protected final _ValueInsertContext createValueInsertContext(_ValuesInsert insert, final byte tableIndex
            , List<ObjectWrapper> domainList, Visible visible) {
        return super.createValueInsertContext(insert, tableIndex, domainList, visible);
    }

    @Override
    protected final Stmt standardValueInsert(_ValueInsertContext ctx) {
        return super.standardValueInsert(ctx);
    }


}
