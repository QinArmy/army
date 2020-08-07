package io.army.dialect.postgre;

import io.army.dialect.AbstractDDL;
import io.army.dialect.DDLContext;
import io.army.meta.FieldMeta;

import java.sql.JDBCType;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is a implementation of {@link io.army.dialect.DDL} for Postgre 11.0 .
 */
class Postgre110DDL extends AbstractDDL {


    Postgre110DDL(Postgre110Dialect dialect) {
        super(dialect);
    }

    /*################################## blow DDL method ##################################*/

    @Override
    public void clearForDDL() {
        super.clearForDDL();
    }

    @Override
    protected void tableOptionsClause(DDLContext context) {

    }

    @Override
    protected void doFieldDefaultValue(FieldMeta<?, ?> fieldMeta, DDLContext context) {

    }

    @Override
    protected void defaultOfCreateAndUpdate(FieldMeta<?, ?> fieldMeta, DDLContext context) {

    }


    @Override
    protected Map<JDBCType, Function<FieldMeta<?, ?>, String>> createJdbcFunctionMap() {
        return Postgre110DDLUtils.createJdbcFunctionMap();
    }


    @Override
    protected boolean hasDefaultClause(FieldMeta<?, ?> fieldMeta) {
        return false;
    }


    @Override
    protected final boolean useIndependentIndexDefinition() {
        return true;
    }

    @Override
    protected final boolean useIndependentComment() {
        return true;
    }
}
