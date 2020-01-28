package io.army.criteria.impl;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.annotation.Column;
import io.army.criteria.MetaException;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;
import org.springframework.lang.NonNull;

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

            Column column = MetaUtils.columnMeta(table.javaType(), field);

            this.precision = column.precision();
            this.scale = column.scale();
            this.fieldName = MetaUtils.columnName(column, field);
            this.mappingType = MetaUtils.columnMappingType(field);

            boolean isDiscriminator = MetaUtils.isDiscriminator(this);

            this.insertable = MetaUtils.columnInsertable(this.propertyName,column,isDiscriminator);
            this.updatable = MetaUtils.columnUpdatable(this.propertyName,column,isDiscriminator);

            this.comment = MetaUtils.columnComment(column, this,isDiscriminator);
            this.nullable = MetaUtils.columnNullable(column, this,isDiscriminator);
            this.defaultValue = MetaUtils.columnDefault(column, this);
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


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultFieldMeta)) {
            return false;
        }
        DefaultFieldMeta<?, ?> otherField = (DefaultFieldMeta<?, ?>) obj;
        return this.table().equals(otherField.table())
                && this.javaType() == otherField.javaType()
                && this.propertyName().equals(this.propertyName())
                && this.fieldName().equals(otherField.fieldName())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.table(), this.javaType(), this.propertyName(), this.fieldName());
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

}
