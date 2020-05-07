package io.army.criteria.impl;

import io.army.criteria.AliasField;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;

import java.sql.JDBCType;


final class AliasFieldExpImpl<T extends IDomain, F> extends AbstractExpression<F>
        implements AliasField<T, F> {

    private final FieldMeta<T, F> fieldMeta;

    private final String tableAlias;

    AliasFieldExpImpl(FieldMeta<T, F> fieldMeta, String tableAlias) {
        Assert.notNull(fieldMeta, "fieldMeta required");
        Assert.hasText(tableAlias, "tableAlias required");
        Assert.isTrue(!tableAlias.contains("."), "tableAlias must no '.'");

        this.fieldMeta = fieldMeta;
        this.tableAlias = tableAlias;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        context.appendField(tableAlias, fieldMeta);
    }


    @Override
    public Selection as(String alias) {
        if (this.fieldMeta.propertyName().equals(alias)) {
            return this;
        } else {
            return new FieldSelectionImpl<>(this, alias);
        }
    }


    @Override
    public String beforeAs() {
        return tableAlias + "." + fieldMeta.fieldName();
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public FieldMeta<T, F> fieldMeta() {
        return this.fieldMeta;
    }

    @Override
    public boolean primary() {
        return fieldMeta.primary();
    }

    @Override
    public boolean unique() {
        return fieldMeta.unique();
    }

    @Override
    public boolean index() {
        return fieldMeta.index();
    }

    @Override
    public boolean nullable() {
        return fieldMeta.nullable();
    }

    @Override
    public TableMeta<T> tableMeta() {
        return fieldMeta.tableMeta();
    }

    @Override
    public GeneratorMeta generator() {
        return fieldMeta.generator();
    }

    @Override
    public JDBCType jdbcType() {
        return fieldMeta.jdbcType();
    }

    @Override
    public Class<F> javaType() {
        return fieldMeta.javaType();
    }

    @Override
    public boolean insertalbe() {
        return fieldMeta.insertalbe();
    }

    @Override
    public boolean updatable() {
        return fieldMeta.updatable();
    }

    @Override
    public String comment() {
        return fieldMeta.comment();
    }

    @Override
    public String defaultValue() {
        return fieldMeta.defaultValue();
    }

    @Override
    public int precision() {
        return fieldMeta.precision();
    }

    @Override
    public int scale() {
        return fieldMeta.scale();
    }

    @Override
    public String fieldName() {
        return fieldMeta.fieldName();
    }

    @Override
    public String propertyName() {
        return fieldMeta.propertyName();
    }

    @Override
    public MappingMeta mappingMeta() {
        return fieldMeta.mappingMeta();
    }
}
