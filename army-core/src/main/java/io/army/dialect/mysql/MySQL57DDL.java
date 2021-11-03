package io.army.dialect.mysql;

import io.army.dialect.AbstractDDL;
import io.army.dialect.DDLContext;
import io.army.dialect.DDLUtils;
import io.army.dialect.SQLBuilder;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.sqldatatype.MySQLDataType;
import io.army.sqldatatype.SqlType;
import io.army.util.Assert;

import java.util.Collections;

class MySQL57DDL extends AbstractDDL {


    MySQL57DDL(MySQL57Dialect mysql) {
        super(mysql);
        Assert.notNull(mysql, "mysql required");
    }

    /*################################## blow SQL interface method ##################################*/

    /*################################## blow DDL method ##################################*/

    @Override
    protected final void internalModifyTableComment(DDLContext context) {
        SQLBuilder builder = context.sqlBuilder()
                .append("ALTER TABLE");
        context.appendTable();
        builder.append(" COMMENT = '")
                .append(DDLUtils.escapeQuote(context.tableMeta().comment()))
                .append("'")
        ;
    }

    @Override
    protected final void internalModifyColumnComment(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        doChangeColumn(Collections.singleton(fieldMeta), context);
    }

    @Override
    protected void doDefaultExpression(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) {
        SqlType sqlDataType = fieldMeta.mappingMeta().sqlDataType(database());
        String defaultExp = fieldMeta.defaultValue();
        if (sqlDataType instanceof MySQLDataType) {
            if (MySQL57DDLUtils.needQuoteForDefault((MySQLDataType) sqlDataType)
                    && !(defaultExp.startsWith("'") && defaultExp.endsWith("'"))) {
                throw DDLUtils.createDefaultValueSyntaxException(fieldMeta);
            }
        }
        builder.append(defaultExp);
    }

    @Override
    protected boolean supportSQLDateType(SqlType dataType) {
        return dataType instanceof MySQLDataType;
    }

    /*################################## blow AbstractTableDDL template method ##################################*/

    @Override
    protected void tableOptionsClause(DDLContext context) {
        TableMeta<?> tableMeta = context.tableMeta();
        context.sqlBuilder()
                .append(" ENGINE = InnoDB CHARACTER SET = ")
                .append(MySQL57DDLUtils.tableCharset(tableMeta.charset()))
                .append(" COMMENT '")
                .append(DDLUtils.escapeQuote(tableMeta.comment()))
                .append("'")
        ;
    }

    @Override
    protected final boolean useIndependentIndexDefinition() {
        return false;
    }

    @Override
    protected final boolean useIndependentComment() {
        return false;
    }

    @Override
    protected final void independentIndexDefinitionClause(IndexMeta<?> indexMeta, DDLContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void independentTableComment(DDLContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void independentColumnComment(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        throw new UnsupportedOperationException();
    }

    /*################################## blow protected method ##################################*/


    /*################################## blow private method ##################################*/


}
