package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.annotation.Column;
import io.army.criteria.MetaException;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.meta.Field;
import io.army.meta.SQLType;
import io.army.meta.TableMeta;
import io.army.util.MetaUtils;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;

/**
 * created  on 2018/11/18.
 */
public final class DefaultField<T extends IDomain, F> extends AbstractExpression<F> implements Field<T, F> {

    private static final String ID = "id";


    private final TableMeta<T> table;

    private final String propertyName;

    private final boolean unique;

    private final boolean index;

    private final Class<F> propertyClass;

    private final String fieldName;

    private final String comment;

    private final String defaultValue;

    private final JDBCType jdbcType;

    private final int length;

    private final boolean insertable;

    private final boolean updatable;


    private final int precision;

    private final int scale;


    @SuppressWarnings("unchecked")
    public DefaultField(final @NonNull TableMeta<T> table, final @NonNull String propertyName) throws MetaException {
        this.table = table;
        this.propertyName = propertyName;

        try {
            Column column = MetaUtils.columnMeta(table.javaType(), propertyName);

            unique = table.uniquePropList().contains(propertyName);
            index = table.indexPropList().contains(propertyName);
            propertyClass = (Class<F>) MetaUtils.fieldClass(table.javaType(), propertyName);
            fieldName = column.name();

            comment = column.comment();
            defaultValue = column.defaultValue();
            jdbcType = MetaUtils.jdbcType(table.javaType(), propertyName);
            length = MetaUtils.length(column, propertyClass);

            insertable = column.insertable();
            updatable = column.updatable();

            precision = column.precision();
            scale = column.scale();

            // add to table
            ((DefaultTable<T>) table).addField(this);
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
        return jdbcType;
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
    public int length() {
        return length;
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
    public SQLType sqlType() {
        return null;
    }

    @Override
    public String toString() {
        return table.tableName() + "." + fieldName;
    }
}
