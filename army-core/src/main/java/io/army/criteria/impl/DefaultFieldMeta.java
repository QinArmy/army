package io.army.criteria.impl;

import io.army.ArmyRuntimeException;
import io.army.annotation.Codec;
import io.army.annotation.Column;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util.AnnotationUtils;
import io.army.util.Assert;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @since 1.0
 */
class DefaultFieldMeta<T extends IDomain, F> extends AbstractExpression<F> implements FieldMeta<T, F>, Selection {

    private static final String ID = _MetaBridge.ID;

    private static final ConcurrentMap<DefaultFieldMeta<?, ?>, Boolean> INSTANCE_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentMap<FieldMeta<?, ?>, Boolean> CODEC_MAP = new ConcurrentHashMap<>();

    static <T extends IDomain, F> FieldMeta<T, F> createFieldMeta(final TableMeta<T> table
            , final Field field) {
        final DefaultFieldMeta<T, F> fieldMeta;
        fieldMeta = new DefaultFieldMeta<>(table, field, false, false);
        if (INSTANCE_MAP.putIfAbsent(fieldMeta, Boolean.TRUE) != null) {
            String m = String.format("%s duplication.", fieldMeta);
            throw new IllegalStateException(m);
        }
        return fieldMeta;

    }

    static <T extends IDomain, F> IndexFieldMeta<T, F> createIndexFieldMeta(final TableMeta<T> table, final Field field
            , final IndexMeta<T> indexMeta, final int columnCount, final @Nullable Boolean fieldAsc) {
        final DefaultIndexFieldMeta<T, F> fieldMeta;
        // create new IndexFieldMeta
        if (indexMeta.unique() && columnCount == 1) {
            if (ID.equals(field.getName())) {
                fieldMeta = new DefaultPrimaryFieldMeta<>(table, field, indexMeta, fieldAsc);
            } else {
                fieldMeta = new DefaultUniqueFieldMeta<>(table, field, indexMeta, fieldAsc);
            }
        } else {
            fieldMeta = new DefaultIndexFieldMeta<>(table, field, indexMeta, false, fieldAsc);
        }
        if (INSTANCE_MAP.putIfAbsent(fieldMeta, Boolean.TRUE) != null) {
            String m = String.format("%s duplication.", fieldMeta);
            throw new IllegalStateException(m);
        }
        return fieldMeta;
    }

    static Set<FieldMeta<?, ?>> codecFieldMetaSet() {
        return CODEC_MAP.keySet();
    }

    private static void assertNotParentFiled(TableMeta<?> table, Field field) {
        if ((table instanceof ChildTableMeta) && !_MetaBridge.ID.equals(field.getName())) {
            ChildTableMeta<?> childMeta = (ChildTableMeta<?>) table;
            if (childMeta.parentMeta().mappingProp(field.getName())) {
                String m = String.format("mapping field belong to ParentTableMeta[%s]"
                        , childMeta.parentMeta());
                throw new MetaException(m);
            }
        }
    }


    private final TableMeta<T> table;

    private final String fieldName;

    private final boolean unique;

    private final boolean index;

    private final Class<F> javaType;

    private final String columnName;

    private final String comment;

    private final String defaultValue;

    private final MappingType mappingType;

    private final boolean nullable;

    private final boolean insertable;

    private final boolean updatable;

    private final int precision;

    private final int scale;

    private final GeneratorMeta generatorMeta;

    private final boolean codec;

    @SuppressWarnings("unchecked")
    private DefaultFieldMeta(final TableMeta<T> table, final @NonNull Field field, final boolean unique,
                             final boolean index) throws MetaException {
        Objects.requireNonNull(table);
        Objects.requireNonNull(field);

        Assert.isAssignable(field.getDeclaringClass(), table.javaType());
        assertNotParentFiled(table, field);

        this.table = table;
        this.fieldName = field.getName();
        this.javaType = (Class<F>) field.getType();
        try {
            this.unique = unique;
            this.index = index;

            final Column column = FieldMetaUtils.columnMeta(table.javaType(), field);

            this.precision = column.precision();
            this.scale = column.scale();
            this.columnName = FieldMetaUtils.columnName(column, field);
            this.mappingType = FieldMetaUtils.columnMappingMeta(field);

            final boolean isDiscriminator = FieldMetaUtils.isDiscriminator(this);

            this.insertable = FieldMetaUtils.columnInsertable(this, column, isDiscriminator);
            this.updatable = FieldMetaUtils.columnUpdatable(table, this.fieldName, column, isDiscriminator);

            this.comment = FieldMetaUtils.columnComment(column, this);
            this.nullable = FieldMetaUtils.columnNullable(column, this, isDiscriminator);
            this.defaultValue = FieldMetaUtils.columnDefault(column, this);
            this.generatorMeta = FieldMetaUtils.columnGeneratorMeta(field, this, isDiscriminator);

            this.codec = AnnotationUtils.getAnnotation(field, Codec.class) != null;
            if (this.codec) {
                CODEC_MAP.put(this, Boolean.TRUE);
            }
        } catch (ArmyRuntimeException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new MetaException(e, "debugSQL entity[%s] mapping property[%s] meta error"
                    , table.javaType().getName(), fieldName);
        }
    }


    @Override
    public final String alias() {
        return this.fieldName;
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
        return ID.equals(fieldName);
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
    public final Class<F> javaType() {
        return this.javaType;
    }

    @Override
    public final MappingType mappingMeta() {
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
    public final boolean codec() {
        return this.codec;
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
    public final String columnName() {
        return columnName;
    }


    @Override
    public final String fieldName() {
        return fieldName;
    }

    @Nullable
    @Override
    public final GeneratorMeta generator() {
        return generatorMeta;
    }

    @Override
    public final boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof DefaultFieldMeta) {
            final DefaultFieldMeta<?, ?> o = (DefaultFieldMeta<?, ?>) obj;
            match = this.table.javaType() == o.table.javaType()
                    && this.fieldName.equals(o.fieldName);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.table.javaType(), this.fieldName);
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        if (this instanceof PrimaryFieldMeta) {
            builder.append(PrimaryFieldMeta.class.getSimpleName());
        } else if (this instanceof IndexFieldMeta) {
            builder.append(IndexFieldMeta.class.getSimpleName());
        } else {
            builder.append(FieldMeta.class.getSimpleName());
        }
        return builder.append('[')
                .append(this.table.javaType().getName())
                .append('.')
                .append(this.fieldName)
                .append(']').toString();
    }

    @Override
    public final void appendSQL(SQLContext context) {
        context.appendField(this);
    }


    @Override
    public final boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        return fieldMetas.contains(this);
    }

    @Override
    public final boolean containsFieldOf(TableMeta<?> tableMeta) {
        return tableMeta == this.table;
    }

    @Override
    public final int containsFieldCount(TableMeta<?> tableMeta) {
        return tableMeta == this.table ? 1 : 0;
    }

    @Override
    public final boolean containsSubQuery() {
        return false;
    }

    /*################################## blow private method ##################################*/

    private static class DefaultIndexFieldMeta<T extends IDomain, F> extends DefaultFieldMeta<T, F>
            implements IndexFieldMeta<T, F> {

        private final IndexMeta<T> indexMeta;

        private final Boolean fieldAsc;

        private DefaultIndexFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta, boolean uniqueField
                , @Nullable Boolean fieldAsc) throws MetaException {
            super(table, field, indexMeta.unique() && uniqueField, true);
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
            super(table, field, indexMeta, true, fieldAsc);
            if (!indexMeta.unique()) {
                throw new MetaException("indexMeta[%s] not unique.", indexMeta);
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
                throw new MetaException("indexMeta[%s] not primary.", indexMeta);
            }
        }
    }


}
