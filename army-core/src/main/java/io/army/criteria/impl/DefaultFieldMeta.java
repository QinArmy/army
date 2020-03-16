package io.army.criteria.impl;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.annotation.Column;
import io.army.criteria.MetaException;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * created  on 2018/11/18.
 */
class DefaultFieldMeta<T extends IDomain, F> extends AbstractExpression<F> implements FieldMeta<T, F>, Selection {

    private static final String ID = TableMeta.ID;

    private static final ConcurrentMap<Field, FieldMeta<?, ?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Class<?>, IndexFieldMeta<?, ?>> ID_INSTANCE_MAP = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    static <T extends IDomain> FieldMeta<T, ?> createFieldMeta(final @NonNull TableMeta<T> table
            , final @NonNull Field field) {
        FieldMeta<T, ?> fieldMeta;
        if (INSTANCE_MAP.containsKey(field)) {
            fieldMeta = (FieldMeta<T, ?>) INSTANCE_MAP.get(field);
            return fieldMeta;
        }

        fieldMeta = new DefaultFieldMeta<>(table, field, false, false);
        FieldMeta<?, ?> actualFieldMeta = INSTANCE_MAP.putIfAbsent(field, fieldMeta);

        if (actualFieldMeta != null && actualFieldMeta != fieldMeta) {
            fieldMeta = (FieldMeta<T, ?>) actualFieldMeta;
        }
        return fieldMeta;
    }

    @SuppressWarnings("unchecked")
    static <T extends IDomain, F> IndexFieldMeta<T, F> createFieldMeta(final @NonNull TableMeta<T> table
            , final @NonNull Field field, IndexMeta<T> indexMeta, final boolean fieldUnique
            , @Nullable Boolean fieldAsc) {

        final boolean isId = TableMeta.ID.equals(field.getName());
        IndexFieldMeta<T, F> fieldMeta;

        if (isId) {
            if (ID_INSTANCE_MAP.containsKey(table.javaType())) {
                fieldMeta = (IndexFieldMeta<T, F>) ID_INSTANCE_MAP.get(table.javaType());
                return fieldMeta;
            }

        } else {
            if (INSTANCE_MAP.containsKey(field)) {
                fieldMeta = (IndexFieldMeta<T, F>) INSTANCE_MAP.get(field);
                return fieldMeta;
            }
        }
        // create new IndexFieldMeta
        fieldMeta = new DefaultIndexFieldMeta<>(table, field, indexMeta, fieldUnique, fieldAsc);

        FieldMeta<?, ?> actualFieldMeta;
        if (isId) {
            actualFieldMeta = ID_INSTANCE_MAP.putIfAbsent(table.javaType(), fieldMeta);
        } else {
            actualFieldMeta = INSTANCE_MAP.putIfAbsent(field, fieldMeta);
        }
        if (actualFieldMeta != null && actualFieldMeta != fieldMeta) {
            fieldMeta = (IndexFieldMeta<T, F>) actualFieldMeta;
        }
        return fieldMeta;
    }

    private static void assertNotDuplication(Field field, Class<?> entityClass) {
        if (field.getName().equals(TableMeta.ID)) {
            Assert.state(!ID_INSTANCE_MAP.containsKey(entityClass)
                    , () -> String.format("field[%s] entity[%s] duplication", field.getName(), entityClass.getName()));
        } else {
            Assert.state(!INSTANCE_MAP.containsKey(field), () -> String.format("field[%s] duplication", field));
        }
    }


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
    private DefaultFieldMeta(final @NonNull TableMeta<T> table, final @NonNull Field field, final boolean unique,
                             final boolean index)
            throws MetaException {
        Assert.notNull(table, "tableMeta required");
        Assert.notNull(field, "field required");
        Assert.isAssignable(field.getDeclaringClass(), table.javaType());
        assertNotDuplication(field, table.javaType());

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
            throw new MetaException(ErrorCode.META_ERROR, e, "debugSQL entity[%s] mapping property[%s] meta error"
                    , table.javaType().getName(), propertyName);
        }
    }


    @Override
    public final String alias() {
        // must override super as ,because one column of field only one instance
        return propertyName;
    }

    @Override
    public final Selection as(String alias) {
        return propertyName.equals(alias)
                ? this
                : new FieldSelection(this, alias);
    }


    @Override
    public final boolean primary() {
        return ID.equals(propertyName);
    }

    @Override
    public final boolean unique() {
        return unique;
    }

    @Override
    public final boolean index() {
        return index;
    }

    @Override
    public final boolean nullable() {
        return nullable;
    }

    @Override
    public final TableMeta<T> tableMeta() {
        return table;
    }

    @Override
    public JDBCType jdbcType() {
        return mappingType.jdbcType();
    }

    @Override
    public final Class<F> javaType() {
        return propertyClass;
    }

    @Override
    public final MappingType mappingType() {
        return mappingType;
    }

    @Override
    public final boolean insertalbe() {
        return insertable;
    }

    @Override
    public final boolean updatable() {
        return updatable;
    }

    @Override
    public final String comment() {
        return comment;
    }

    @Override
    public final String defaultValue() {
        return defaultValue;
    }

    @Override
    public final int precision() {
        return precision;
    }

    @Override
    public final int scale() {
        return scale;
    }

    @Override
    public final String fieldName() {
        return fieldName;
    }


    @Override
    public final String propertyName() {
        return propertyName;
    }

    @Nullable
    @Override
    public final GeneratorMeta generator() {
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
    public String beforeAs() {
        return defaultToString();
    }

    private String defaultToString() {
        return new StringBuilder()
                .append("\n")
                .append(this.table.javaType().getName())
                .append(" mapping ")
                .append(this.tableMeta().tableName())
                .append(" [\n")
                .append(this.propertyName())
                .append(" mapping ")
                .append(this.fieldName())
                .append("\n]")
                .toString();
    }

    @Override
    protected  final void afterSpace(SQLContext context) {
        context.appendField("",this);
    }


    /*################################## blow private method ##################################*/

    private static class DefaultIndexFieldMeta<T extends IDomain, F> extends DefaultFieldMeta<T, F>
            implements IndexFieldMeta<T, F> {

        private final IndexMeta<T> indexMeta;

        private final Boolean fieldAsc;

        private DefaultIndexFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta, boolean fieldUnique,
                                      @Nullable Boolean fieldAsc) throws MetaException {
            super(table, field, fieldUnique, true);
            Assert.notNull(indexMeta, "");

            this.indexMeta = indexMeta;
            this.fieldAsc = fieldAsc;
        }

        @Override
        public IndexMeta<T> indexMeta() {
            return this.indexMeta;
        }

        @Nullable
        @Override
        public Boolean fieldAsc() {
            return this.fieldAsc;
        }
    }


}
