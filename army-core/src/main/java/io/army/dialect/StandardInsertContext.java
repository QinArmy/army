package io.army.dialect;

import io.army.beans.DomainWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Visible;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.wrapper.DomainSQLWrapper;
import io.army.wrapper.SimpleSQLWrapper;

final class StandardInsertContext extends AbstractSQLContext implements InsertContext {

    static StandardInsertContext build(Dialect dialect, Visible visible, ReadonlyWrapper wrapper
            , TableMeta<?> physicalTable) {
        return new StandardInsertContext(dialect, visible, physicalTable);
    }

    private final StringBuilder fieldBuilder;

    private final TableMeta<?> physicalTable;

    public StandardInsertContext(Dialect dialect, Visible visible, TableMeta<?> physicalTable) {
        super(dialect, visible);
        this.fieldBuilder = new StringBuilder();
        this.physicalTable = physicalTable;
    }


    @Override
    public final void appendTable(TableMeta<?> tableMeta) {
        if (tableMeta != this.physicalTable) {
            throw DialectUtils.createUnKnownTableException(tableMeta);
        }
        this.fieldBuilder.append(" ")
                .append(this.dialect.quoteIfNeed(tableMeta.tableName()));

    }

    @Override
    public final StringBuilder fieldsBuilder() {
        return this.fieldBuilder;
    }

    @Override
    public TableContext tableContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void appendField(FieldMeta<?, ?> fieldMeta) {
        if (fieldMeta.tableMeta() != this.physicalTable) {
            throw DialectUtils.createNoLogicalTableException(fieldMeta);
        }
        this.fieldBuilder
                .append(" ")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
    }

    @Override
    protected SimpleSQLWrapper doBuild() {
        return SimpleSQLWrapper.build(
                this.fieldBuilder.toString() + this.sqlBuilder.toString()
                , this.paramList
        );
    }

    @Override
    protected DomainSQLWrapper doBuild(DomainWrapper beanWrapper) {
        return DomainSQLWrapper.build(
                this.fieldBuilder.toString() + this.sqlBuilder.toString()
                , this.paramList
                , beanWrapper
        );
    }
}
