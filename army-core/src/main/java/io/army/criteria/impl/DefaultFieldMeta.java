package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.annotation.Column;
import io.army.criteria.MetaException;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.sql.JDBCType;

/**
 * created  on 2018/11/18.
 */
class DefaultFieldMeta<T extends IDomain, F> extends AbstractExpression<F> implements FieldMeta<T, F> {

    private static final String ID = "id";


    private final TableMeta<T> table;

    private final String propertyName;

    private final boolean unique;

    private final boolean index;

    private final Class<F> propertyClass;

    private final String fieldName;

    private final String comment;

    private final String defaultValue;

    private final MappingType<T> mappingType;

    private final boolean insertable;

    private final boolean updatable;


    private final int precision;

    private final int scale;


    @SuppressWarnings("unchecked")
    DefaultFieldMeta(final @NonNull TableMeta<T> table, final @NonNull Field field, final boolean unique,
                     final boolean index)
            throws MetaException {
        Assert.isAssignable(field.getDeclaringClass(), table.javaType());

        this.table = table;
        this.propertyName = field.getName();

        try {
            this.unique = unique;
            this.index = index;

            Column column = MetaUtils.columnMeta(table.javaType(), field);
            propertyClass = (Class<F>) field.getType();
            fieldName = column.name();

            comment = column.comment();
            mappingType = MetaUtils.mappingType(field);

            insertable = column.insertable();
            updatable = column.updatable();

            precision = MetaUtils.precision(column, this.mappingType.precision());
            scale = MetaUtils.scale(column, this.mappingType.precision());

            defaultValue = column.defaultValue();
        } catch (RuntimeException e) {
            throw new MetaException(ErrorCode.META_ERROR, e, "Table[%s].%s error", table.tableName(), propertyName);
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
    public boolean isPrimary() {
        return ID.equals(propertyName);
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public boolean isIndex() {
        return index;
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
    public boolean isInsertalbe() {
        return insertable;
    }

    @Override
    public boolean isUpdatable() {
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
    public String toString() {
        return table.tableName() + "." + fieldName;
    }
}
