package io.army.criteria.impl;

import io.army.ArmyException;
import io.army.annotation.Codec;
import io.army.annotation.Column;
import io.army.annotation.UpdateMode;
import io.army.criteria.Selection;
import io.army.criteria.SqlContext;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
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
abstract class DefaultFieldMeta<T extends IDomain, F> extends AbstractExpression<F>
        implements FieldMeta<T, F>, Selection {

    private static final String ID = _MetaBridge.ID;

    private static final ConcurrentMap<DefaultFieldMeta<?, ?>, DefaultFieldMeta<?, ?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentMap<FieldMeta<?, ?>, Boolean> CODEC_MAP = new ConcurrentHashMap<>();

    /**
     * @see DefaultTableMeta#getTableMeta(Class)
     */
    @SuppressWarnings("unchecked")
    static <T extends IDomain, F> FieldMeta<T, F> createFieldMeta(final TableMeta<T> table, final Field field) {
        if (_MetaBridge.ID.equals(field.getName())) {
            throw new IllegalArgumentException("id can't invoke this method.");
        }
        final DefaultSimpleFieldMeta<T, F> fieldMeta;
        fieldMeta = new DefaultSimpleFieldMeta<>(table, field);

        final DefaultFieldMeta<?, ?> cache;
        cache = INSTANCE_MAP.putIfAbsent(fieldMeta, fieldMeta);

        final DefaultSimpleFieldMeta<T, F> simple;
        if (cache == null) {
            simple = fieldMeta;
        } else if (cache instanceof DefaultSimpleFieldMeta) {
            // drop fieldMeta ,return cache.
            simple = (DefaultSimpleFieldMeta<T, F>) cache;
        } else {
            String m = String.format("%s.%s can't mapping to simple %s.", table.javaType().getName()
                    , field.getName(), FieldMeta.class.getName());
            throw new IllegalArgumentException(m);
        }
        return simple;

    }

    /**
     * @see DefaultTableMeta#getTableMeta(Class)
     */
    @SuppressWarnings("unchecked")
    static <T extends IDomain, F> IndexFieldMeta<T, F> createIndexFieldMeta(final TableMeta<T> table, final Field field
            , final IndexMeta<T> indexMeta, final int columnCount, final @Nullable Boolean fieldAsc) {
        final DefaultIndexFieldMeta<T, F> newFieldMeta;
        // create new IndexFieldMeta
        if (indexMeta.unique() && columnCount == 1) {
            if (ID.equals(field.getName())) {
                newFieldMeta = new DefaultPrimaryFieldMeta<>(table, field, indexMeta, fieldAsc);
            } else {
                newFieldMeta = new DefaultUniqueFieldMeta<>(table, field, indexMeta, fieldAsc);
            }
        } else {
            newFieldMeta = new DefaultIndexFieldMeta<>(table, field, indexMeta, fieldAsc);
        }

        final DefaultFieldMeta<?, ?> cache;
        cache = INSTANCE_MAP.putIfAbsent(newFieldMeta, newFieldMeta);

        final DefaultIndexFieldMeta<T, F> indexField;
        if (cache == null) {
            indexField = newFieldMeta;
        } else if (!(cache instanceof DefaultIndexFieldMeta)) {
            String m = String.format("%s.%s can't mapping to  %s.", table.javaType().getName()
                    , field.getName(), IndexFieldMeta.class.getName());
            throw new IllegalArgumentException(m);
        } else if (indexMeta.unique() && columnCount == 1) {
            if (!(cache instanceof DefaultUniqueFieldMeta)) {
                String m = String.format("%s.%s can't mapping to  %s.", table.javaType().getName()
                        , field.getName(), UniqueFieldMeta.class.getName());
                throw new IllegalArgumentException(m);
            }
            if (!_MetaBridge.ID.equals(field.getName())) {
                // drop newFieldMeta ,return cache
                indexField = (DefaultUniqueFieldMeta<T, F>) cache;
            } else if (cache instanceof DefaultPrimaryFieldMeta) {
                // drop newFieldMeta ,return cache
                indexField = (DefaultPrimaryFieldMeta<T, F>) cache;
            } else {
                throw new IllegalStateException("INSTANCE_MAP error");
            }
        } else {
            // drop newFieldMeta ,return cache
            indexField = (DefaultIndexFieldMeta<T, F>) cache;
        }
        return indexField;
    }

    static Set<FieldMeta<?, ?>> codecFieldMetaSet() {
        return CODEC_MAP.keySet();
    }

    private static void assertNotParentFiled(TableMeta<?> table, Field field) {
        if ((table instanceof ChildTableMeta) && !_MetaBridge.ID.equals(field.getName())) {
            ChildTableMeta<?> childMeta = (ChildTableMeta<?>) table;
            if (childMeta.parentMeta().mappingField(field.getName())) {
                String m = String.format("mapping field belong to ParentTableMeta[%s]"
                        , childMeta.parentMeta());
                throw new MetaException(m);
            }
        }
    }


    private final TableMeta<T> table;

    private final String fieldName;

    private final Class<F> javaType;

    private final String columnName;

    private final String comment;

    private final String defaultValue;

    private final MappingType mappingType;

    private final boolean nullable;

    private final boolean insertable;

    private final UpdateMode updateMode;

    private final int precision;

    private final int scale;

    private final GeneratorMeta generatorMeta;

    private final boolean codec;

    @SuppressWarnings("unchecked")
    private DefaultFieldMeta(final TableMeta<T> table, final Field field) throws MetaException {
        Objects.requireNonNull(table);
        Objects.requireNonNull(field);

        Assert.isAssignable(field.getDeclaringClass(), table.javaType());
        assertNotParentFiled(table, field);

        this.table = table;
        this.fieldName = field.getName();
        this.javaType = (Class<F>) field.getType();
        try {
            final Column column = FieldMetaUtils.columnMeta(table.javaType(), field);

            this.precision = column.precision();
            this.scale = column.scale();
            this.columnName = FieldMetaUtils.columnName(column, field);
            final boolean isDiscriminator = FieldMetaUtils.isDiscriminator(this);

            this.mappingType = FieldMetaUtils.columnMappingMeta(table, field, isDiscriminator);
            this.insertable = _MetaBridge.RESERVED_PROPS.contains(this.fieldName)
                    || isDiscriminator
                    || column.insertable();
            this.updateMode = FieldMetaUtils.columnUpdatable(this, column, isDiscriminator);

            this.comment = FieldMetaUtils.columnComment(column, this, isDiscriminator);
            this.nullable = !_MetaBridge.RESERVED_PROPS.contains(this.fieldName)
                    && !isDiscriminator
                    && column.nullable();
            this.defaultValue = FieldMetaUtils.columnDefault(column, this, isDiscriminator);
            this.generatorMeta = FieldMetaUtils.columnGeneratorMeta(field, this, isDiscriminator);
            this.codec = field.getAnnotation(Codec.class) != null;
        } catch (ArmyException e) {
            throw e;
        } catch (RuntimeException e) {
            String m = String.format("Domain class[%s] mapping field[%s] meta error."
                    , table.javaType().getName(), field.getName());
            throw new MetaException(m, e);
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
        return _MetaBridge.ID.equals(this.fieldName);
    }

    @Override
    public final boolean unique() {
        return this instanceof UniqueFieldMeta;
    }

    @Override
    public final boolean index() {
        return this instanceof IndexFieldMeta;
    }

    @Override
    public final boolean nullable() {
        return this.nullable;
    }

    @Override
    public final TableMeta<T> tableMeta() {
        return this.table;
    }

    @Override
    public final Class<F> javaType() {
        return this.javaType;
    }

    @Override
    public final MappingType mappingMeta() {
        return this.mappingType;
    }

    @Override
    public final boolean insertable() {
        return this.insertable;
    }

    @Override
    public final UpdateMode updateMode() {
        return this.updateMode;
    }

    @Override
    public final String comment() {
        return this.comment;
    }

    @Override
    public final String defaultValue() {
        return this.defaultValue;
    }

    @Override
    public final boolean codec() {
        return this.codec;
    }

    @Override
    public final int precision() {
        return this.precision;
    }

    @Override
    public final int scale() {
        return this.scale;
    }

    @Override
    public final String columnName() {
        return this.columnName;
    }


    @Override
    public final String fieldName() {
        return this.fieldName;
    }

    @Nullable
    @Override
    public final GeneratorMeta generator() {
        return this.generatorMeta;
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
                .append(']')
                .toString();
    }

    @Override
    public final void appendSQL(SqlContext context) {
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

    private static class DefaultSimpleFieldMeta<T extends IDomain, F> extends DefaultFieldMeta<T, F> {

        private DefaultSimpleFieldMeta(TableMeta<T> table, Field field) throws MetaException {
            super(table, field);
        }

    }

    private static class DefaultIndexFieldMeta<T extends IDomain, F> extends DefaultFieldMeta<T, F>
            implements IndexFieldMeta<T, F> {

        private final IndexMeta<T> indexMeta;

        private final Boolean fieldAsc;

        private DefaultIndexFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta
                , @Nullable Boolean fieldAsc) throws MetaException {
            super(table, field);
            Objects.requireNonNull(indexMeta);

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
            if (!indexMeta.unique()) {
                throw new MetaException("indexMeta[%s] not unique.", indexMeta);
            }
        }
    }

    private static final class DefaultPrimaryFieldMeta<T extends IDomain, F> extends DefaultUniqueFieldMeta<T, F>
            implements PrimaryFieldMeta<T, F> {

        private DefaultPrimaryFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta
                , @Nullable Boolean fieldAsc) throws MetaException {
            super(table, field, indexMeta, fieldAsc);
            if (!_MetaBridge.ID.equals(field.getName())) {
                throw new MetaException("indexMeta[%s] not primary.", indexMeta);
            }
        }
    }


}
