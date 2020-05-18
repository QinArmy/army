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
import io.army.meta.mapping.MappingMeta;
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

    private static final ConcurrentMap<String, FieldMeta<?, ?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    static <T extends IDomain> FieldMeta<T, ?> createFieldMeta(final @NonNull TableMeta<T> table
            , final @NonNull Field field) {
        final String fieldMetaKey = table.javaType().getName() + "." + field.getName();
        FieldMeta<T, ?> fieldMeta;
        fieldMeta = (FieldMeta<T, ?>) INSTANCE_MAP.get(fieldMetaKey);
        if (fieldMeta != null) {
            return fieldMeta;
        }

        assertNotParentFiled(table, field);

        fieldMeta = new DefaultFieldMeta<>(table, field, false, false);
        FieldMeta<?, ?> actualFieldMeta = INSTANCE_MAP.putIfAbsent(fieldMetaKey, fieldMeta);

        if (actualFieldMeta != null && actualFieldMeta != fieldMeta) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] property[%s] is duplication."
                    , table.javaType().getName(), field.getName());
        }
        return fieldMeta;
    }

    @SuppressWarnings("unchecked")
    static <T extends IDomain, F> IndexFieldMeta<T, F> createIndexFieldMeta(TableMeta<T> table, Field field
            , IndexMeta<T> indexMeta, @Nullable Boolean fieldAsc) {

        final String fieldMetaKey = table.javaType().getName() + "." + field.getName();
        IndexFieldMeta<T, F> fieldMeta;
        fieldMeta = (IndexFieldMeta<T, F>) INSTANCE_MAP.get(fieldMetaKey);
        if (fieldMeta != null) {
            return fieldMeta;
        }
        assertNotParentFiled(table, field);

        // create new IndexFieldMeta
        if (indexMeta.unique() && indexMeta.fieldList().size() == 1) {
            if (ID.equals(field.getName())) {
                fieldMeta = new DefaultPrimaryFieldMeta<>(table, field, indexMeta, fieldAsc);
            } else {
                fieldMeta = new DefaultUniqueFieldMeta<>(table, field, indexMeta, fieldAsc);
            }
        } else {
            fieldMeta = new DefaultIndexFieldMeta<>(table, field, indexMeta, fieldAsc);
        }

        FieldMeta<?, ?> actualFieldMeta = INSTANCE_MAP.putIfAbsent(fieldMetaKey, fieldMeta);

        if (actualFieldMeta != null && actualFieldMeta != fieldMeta) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] property[%s] is duplication."
                    , table.javaType().getName(), field.getName());
        }
        return fieldMeta;
    }

    private static void assertNotParentFiled(TableMeta<?> table, Field field) {
        if ((table instanceof ChildTableMeta)
                && !TableMeta.ID.equals(field.getName())) {
            ChildTableMeta<?> childMeta = (ChildTableMeta<?>) table;
            if (childMeta.parentMeta().mappingProp(field.getName())) {
                throw new MetaException(ErrorCode.META_ERROR, "mapping property belong to ParentTableMeta[%s]"
                        , childMeta.parentMeta());
            }
        }
    }

    private static void assertNotDuplication(Field field, Class<?> domainClass) {
        if (INSTANCE_MAP.containsKey(domainClass.getName() + "." + field.getName())) {
            throw new IllegalStateException(String.format("domain[%s] FieldMeta[%s] duplication."
                    , domainClass.getName(), field.getName()));
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

    private final MappingMeta mappingType;

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

            final boolean isDiscriminator = FieldMetaUtils.isDiscriminator(this);

            this.insertable = FieldMetaUtils.columnInsertable(this, column, isDiscriminator);
            this.updatable = FieldMetaUtils.columnUpdatable(table, this.propertyName, column, isDiscriminator);

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
        return new FieldSelectionImpl<>(this, alias);
    }

    @Override
    public final FieldMeta<?, ?> fieldMeta() {
        return this;
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
    public final MappingMeta mappingMeta() {
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
    public final String beforeAs() {
        return table.tableName()
                .concat(".")
                .concat(fieldName);
    }

    @Override
    protected final void afterSpace(SQLContext context) {
        context.appendField(this);
    }


    /*################################## blow private method ##################################*/

    private static class DefaultIndexFieldMeta<T extends IDomain, F> extends DefaultFieldMeta<T, F>
            implements IndexFieldMeta<T, F> {

        private final IndexMeta<T> indexMeta;

        private final Boolean fieldAsc;

        private DefaultIndexFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta
                , @Nullable Boolean fieldAsc) throws MetaException {
            super(table, field, indexMeta.unique() && indexMeta.fieldList().size() == 1, true);
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

    private static class DefaultUniqueFieldMeta<T extends IDomain, F> extends DefaultIndexFieldMeta<T, F>
            implements UniqueFieldMeta<T, F> {

        private DefaultUniqueFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta
                , @Nullable Boolean fieldAsc) throws MetaException {
            super(table, field, indexMeta, fieldAsc);
            if (!indexMeta.unique() || indexMeta.fieldList().size() != 1) {
                throw new MetaException(ErrorCode.META_ERROR, "indexMeta[%s] not unique.", indexMeta);
            }
        }
    }

    private static final class DefaultPrimaryFieldMeta<T extends IDomain, F> extends DefaultUniqueFieldMeta<T, F>
            implements PrimaryFieldMeta<T, F> {

        private DefaultPrimaryFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta
                , @Nullable Boolean fieldAsc)
                throws MetaException {
            super(table, field, indexMeta, fieldAsc);
            if (!ID.equals(field.getName())) {
                throw new MetaException(ErrorCode.META_ERROR, "indexMeta[%s] not primary.", indexMeta);
            }
        }
    }


}
