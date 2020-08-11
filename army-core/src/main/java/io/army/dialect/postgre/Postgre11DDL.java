package io.army.dialect.postgre;

import io.army.dialect.*;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.IndexMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SQLDataType;
import io.army.util.StringUtils;

/**
 * This class is a implementation of {@link io.army.dialect.DDL} for Postgre 11.x .
 */
class Postgre11DDL extends AbstractDDL {


    Postgre11DDL(Postgre11Dialect dialect) {
        super(dialect);
    }

    /*################################## blow DDL method ##################################*/

    @Override
    protected final void internalModifyTableComment(DDLContext context) {
        independentTableComment(context);
    }

    @Override
    protected final void internalModifyColumnComment(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        independentColumnComment(fieldMeta, context);
    }

    @Override
    protected final void tableOptionsClause(DDLContext context) {
        //no-op
    }

    @Override
    protected final void doDefaultExpression(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) {
        SQLDataType sqlDataType = fieldMeta.mappingMeta().sqlDataType(database());
        String defaultExp = fieldMeta.defaultValue();
        if (sqlDataType instanceof PostgreDataType) {
            PostgreDataType dataType = (PostgreDataType) sqlDataType;
            if (Postgre11DDLUtils.needQuoteForDefault(dataType)
                    && (!defaultExp.startsWith("'") || !defaultExp.endsWith("'"))) {
                throw DDLUtils.createDefaultValueSyntaxException(fieldMeta);
            }

        }
        builder.append(defaultExp);
    }

    @Override
    protected final boolean supportSQLDateType(SQLDataType dataType) {
        return dataType instanceof PostgreDataType || dataType.database().family() == Database.Postgre;
    }

    @Override
    protected final void independentIndexDefinitionClause(IndexMeta<?> indexMeta, DDLContext context) {
        SQLBuilder builder = context.sqlBuilder();
        builder.append("CREATE ");
        if (indexMeta.unique()) {
            builder.append("UNIQUE");
        }
        builder.append(" INDEX IF NOT EXISTS");
        context.appendIdentifier(indexMeta.name());
        builder.append(" ON");
        context.appendTable();

        String type = indexMeta.type();
        if (StringUtils.hasText(type)) {
            builder.append(" USING ")
                    .append(type);
        }
        for (IndexFieldMeta<?, ?> indexFieldMeta : indexMeta.fieldList()) {
            builder.append(indexFieldMeta);
            Boolean asc = indexFieldMeta.fieldAsc();
            if (Boolean.TRUE.equals(asc)) {
                builder.append(" ASC");
            } else if (Boolean.FALSE.equals(asc)) {
                builder.append(" DESC");
            }
        }

    }

    @Override
    protected final void independentTableComment(DDLContext context) {
        SQLBuilder builder = context.sqlBuilder();
        builder.append("COMMENT ON TABLE ");
        context.appendTable();
        builder.append(" IS '")
                .append(DDLUtils.escapeQuote(context.tableMeta().comment().trim()))
                .append("'");
    }

    @Override
    protected final void independentColumnComment(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        SQLBuilder builder = context.sqlBuilder();
        builder.append("COMMENT ON COLUMN ");
        context.appendFieldWithTable(fieldMeta);
        builder.append(" IS '")
                .append(DDLUtils.escapeQuote(fieldMeta.comment().trim()))
                .append("'");
    }


    @Override
    protected final boolean useIndependentIndexDefinition() {
        //always true
        return true;
    }

    @Override
    protected final boolean useIndependentComment() {
        //always true
        return true;
    }
}
