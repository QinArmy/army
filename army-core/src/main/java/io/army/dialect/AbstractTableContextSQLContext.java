package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.criteria.Visible;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;

public abstract class AbstractTableContextSQLContext extends AbstractSQLContext implements TableContextSQLContext {





    protected final TableContext tableContext;


    protected AbstractTableContextSQLContext(Dialect dialect, Visible visible, TableContext tableContext) {
        super(dialect, visible);
        this.tableContext = tableContext;
    }

    protected AbstractTableContextSQLContext(TableContextSQLContext original, TableContext tableContext) {
        super(original);
        this.tableContext = tableContext;
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        sqlBuilder.append(" ")
                .append(this.dialect.quoteIfNeed(tableAlias))
                .append(".")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
    }

    @Override
    public void appendField(FieldMeta<?, ?> fieldMeta) {
        this.appendField(findTableAlias(fieldMeta), fieldMeta);
    }


    @Override
    public final void appendTable(TableMeta<?> tableMeta) {
        if (!this.tableContext.tableCountMap.containsKey(tableMeta)) {
            throw DialectUtils.createUnKnownTableException(tableMeta);
        }
        this.sqlBuilder
                .append(" ")
                .append(this.dialect.quoteIfNeed(tableMeta.tableName()));
    }

    @Override
    public final TableContext tableContext() {
        return this.tableContext;
    }

    @Override
    public final void appendText(String textValue) {
        sqlBuilder.append(" ")
                .append(dialect.quoteIfNeed(textValue));
    }

    @Override
    public final void appendTextValue(MappingMeta mappingType, Object value) {
        sqlBuilder.append(
                DialectUtils.quoteIfNeed(
                        mappingType
                        , mappingType.nonNullTextValue(value)
                )
        );
    }





    protected final String findTableAlias(FieldMeta<?, ?> fieldMeta) throws CriteriaException {
        Integer count = this.tableContext.tableCountMap.get(fieldMeta.tableMeta());
        String tableAlias;
        if (count == null) {
            tableAlias = findTableAliasFromParent(fieldMeta);
        } else if (count.equals(1)) {
            tableAlias = this.tableContext.tableAliasMap.get(fieldMeta.tableMeta());
        } else {
            throw DialectUtils.createNoLogicalTableException(fieldMeta);
        }
        if (tableAlias == null) {
            // fromContext or parentFromContext error.
            throw DialectUtils.createArmyCriteriaException();
        }
        return tableAlias;
    }

    protected String findTableAliasFromParent(FieldMeta<?, ?> fieldMeta) throws CriteriaException {
        throw DialectUtils.createUnKnownFieldException(fieldMeta);
    }



}
