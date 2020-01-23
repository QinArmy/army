package io.army.schema.migration.mysql;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.dialect.AbstractTableDDL;

import java.util.Set;

class MySQL57TableDDL extends AbstractTableDDL implements  MySQLTableDDL{

    @Override
    public String quoteIfNeed(String text) {
        return null;
    }

    @Override
    public boolean isKeyWord(String text) {
        return false;
    }



    @Override
    protected void appendTableOptions(StringBuilder builder, TableMeta<?> tableMeta) {

    }

    @Override
    protected String createUpdateDefault(String func, FieldMeta<?, ?> fieldMeta) {
        return null;
    }

    @Override
    protected String dataTypeText(FieldMeta<?, ?> fieldMeta) {
        return null;
    }
}
