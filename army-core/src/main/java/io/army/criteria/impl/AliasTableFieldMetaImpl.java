package io.army.criteria.impl;

import io.army.criteria.Selection;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.util.List;

final class AliasTableFieldMetaImpl<T extends IDomain, F> extends AbstractExpression<F> implements FieldMeta<T, F> {

    private final FieldMeta<T, F> fieldMeta;

    private final String tableAlias;

    AliasTableFieldMetaImpl(FieldMeta<T, F> fieldMeta, String tableAlias) {
        Assert.notNull(fieldMeta, "fieldMeta required");
        Assert.hasText(tableAlias, "tableAlias required");
        Assert.isTrue(!tableAlias.contains("."),"tableAlias must no '.'");

        this.fieldMeta = fieldMeta;
        this.tableAlias = tableAlias;
    }

    @Override
    protected void appendSQLBeforeWhitespace(SQL sql, StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        builder.append(tableAlias)
                .append(".")
                .append(sql.quoteIfNeed(fieldMeta.fieldName()));
    }

    @Override
    public Selection as() {
        return as(fieldMeta.propertyName());
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
    public MappingType mappingType() {
        return fieldMeta.mappingType();
    }
}
