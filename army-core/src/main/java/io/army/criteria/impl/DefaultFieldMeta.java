package io.army.criteria.impl;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.annotation.Column;
import io.army.criteria.MetaException;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.util.Objects;

/**
 * created  on 2018/11/18.
 */
class DefaultFieldMeta<T extends IDomain, F> extends AbstractExpression<F> implements FieldMeta<T, F> {

    private static final String ID = TableMeta.ID;


    private final TableMeta<T> table;

    private final String propertyName;

    private final boolean unique;

    private final boolean index;

    private final Class<F> propertyClass;

    private final String fieldName;

    private final String comment;

    private final String defaultValue;

    private final MappingType mappingType;

    private final boolean nullable;

    private final boolean insertable;

    private final boolean updatable;

    private final int precision;

    private final int scale;

    private final GeneratorMeta generatorMeta;


    @SuppressWarnings("unchecked")
    DefaultFieldMeta(final @NonNull TableMeta<T> table, final @NonNull Field field, final boolean unique,
                     final boolean index)
            throws MetaException {
        Assert.notNull(table, "table required");
        Assert.notNull(field, "field required");
        Assert.isAssignable(field.getDeclaringClass(), table.javaType());

        this.table = table;
        this.propertyName = field.getName();
        this.propertyClass = (Class<F>) field.getType();
        try {
            this.unique = unique;
            this.index = index;

            Column column = FieldMetaUtils.columnMeta(table.javaType(), field);

            this.precision = column.precision();
            this.scale = column.scale();
            this.fieldName = FieldMetaUtils.columnName(column, field);
            this.mappingType = FieldMetaUtils.columnMappingType(field);

            boolean isDiscriminator = FieldMetaUtils.isDiscriminator(this);

            this.insertable = FieldMetaUtils.columnInsertable(this.propertyName, column, isDiscriminator);
            this.updatable = FieldMetaUtils.columnUpdatable(this.propertyName, column, isDiscriminator);

            this.comment = FieldMetaUtils.columnComment(column, this, isDiscriminator);
            this.nullable = FieldMetaUtils.columnNullable(column, this, isDiscriminator);
            this.defaultValue = FieldMetaUtils.columnDefault(column, this);
            this.generatorMeta = FieldMetaUtils.columnGeneratorMeta(field, this, isDiscriminator);

        } catch (ArmyRuntimeException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new MetaException(ErrorCode.META_ERROR, e, "build entity[%s] mapping property[%s] meta error"
                    , table.javaType().getName(), propertyName);
        }
    }

    @Override
    public Selection as(String alias) {
        return new FieldSelection<>(this, "", alias);
    }

    @Override
    public Selection as(@NonNull String tableAlias, @NonNull String alias) {
        return new FieldSelection<>(this, tableAlias, alias);
    }

    @Override
    public boolean primary() {
        return ID.equals(propertyName);
    }

    @Override
    public boolean unique() {
        return unique;
    }

    @Override
    public boolean index() {
        return index;
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

    @Override
    public TableMeta<T> table() {
        return table;
    }

    @Override
    public Class<F> javaType() {
        return propertyClass;
    }

    @Override
    public JDBCType jdbcType() {
        return mappingType.jdbcType();
    }

    @Override
    public MappingType mappingType() {
        return mappingType;
    }

    @Override
    public boolean insertalbe() {
        return insertable;
    }

    @Override
    public boolean updatable() {
        return updatable;
    }

    @Override
    public String comment() {
        return comment;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public int precision() {
        return precision;
    }

    @Override
    public int scale() {
        return scale;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }


    @Override
    public String propertyName() {
        return propertyName;
    }

    @Nullable
    @Override
    public GeneratorMeta generator() {
        return generatorMeta;
    }

    @Override
    public final boolean equals(Object obj) {
        // save column only one FieldMeta instance
       return this == obj;
    }

    @Override
    public final int hashCode() {
        // save column only one FieldMeta instance
        return super.hashCode();
    }

    @Override
    public String toString() {
        return defaultToString();
    }

    private String defaultToString() {
        return new StringBuilder()
                .append("\n")
                .append(this.table.javaType().getName())
                .append(" mapping ")
                .append(this.table().tableName())
                .append(" [\n")
                .append(this.propertyName())
                .append(" mapping ")
                .append(this.fieldName())
                .append("\n]")
                .toString();
    }

    /*################################## blow private method ##################################*/



}
