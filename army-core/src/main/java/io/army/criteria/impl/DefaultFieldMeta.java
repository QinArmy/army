package io.army.criteria.impl;

import io.army.ArmyException;
import io.army.annotation.*;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Selection;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.ElementMappingType;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @since 1.0
 */
abstract class DefaultFieldMeta<T extends IDomain> extends OperationField<T> implements FieldMeta<T>, _Selection {

    private static final String ID = _MetaBridge.ID;

    private static final ConcurrentMap<DefaultFieldMeta<?>, DefaultFieldMeta<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentMap<FieldMeta<?>, Boolean> CODEC_MAP = new ConcurrentHashMap<>();

    /**
     * @see DefaultTableMeta#getTableMeta(Class)
     */
    @SuppressWarnings("unchecked")
    static <T extends IDomain> FieldMeta<T> createFieldMeta(final TableMeta<T> table, final Field field) {
        if (_MetaBridge.ID.equals(field.getName())) {
            throw new IllegalArgumentException("id can't invoke this method.");
        }
        final DefaultSimpleFieldMeta<T> fieldMeta;
        fieldMeta = new DefaultSimpleFieldMeta<>(table, field);

        final DefaultFieldMeta<?> cache;
        cache = INSTANCE_MAP.putIfAbsent(fieldMeta, fieldMeta);

        final DefaultSimpleFieldMeta<T> simple;
        if (cache == null) {
            simple = fieldMeta;
        } else if (cache instanceof DefaultSimpleFieldMeta) {
            // drop fieldMeta ,return cache.
            simple = (DefaultSimpleFieldMeta<T>) cache;
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
    static <T extends IDomain> IndexFieldMeta<T> createIndexFieldMeta(final TableMeta<T> table, final Field field
            , final IndexMeta<T> indexMeta, final int columnCount, final @Nullable Boolean fieldAsc) {
        final DefaultIndexFieldMeta<T> newFieldMeta;
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

        final DefaultFieldMeta<?> cache;
        cache = INSTANCE_MAP.putIfAbsent(newFieldMeta, newFieldMeta);

        final DefaultIndexFieldMeta<T> indexField;
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
                indexField = (DefaultUniqueFieldMeta<T>) cache;
            } else if (cache instanceof DefaultPrimaryFieldMeta) {
                // drop newFieldMeta ,return cache
                indexField = (DefaultPrimaryFieldMeta<T>) cache;
            } else {
                throw new IllegalStateException("INSTANCE_MAP error");
            }
        } else {
            // drop newFieldMeta ,return cache
            indexField = (DefaultIndexFieldMeta<T>) cache;
        }
        return indexField;
    }

    static Set<FieldMeta<?>> codecFieldMetaSet() {
        return CODEC_MAP.keySet();
    }

    private static void assertNotParentFiled(TableMeta<?> table, Field field) {
        if ((table instanceof ChildTableMeta) && !_MetaBridge.ID.equals(field.getName())) {
            final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) table;
            if (childMeta.parentMeta().containField(field.getName())) {
                String m = String.format("mapping field belong to ParentTableMeta[%s]"
                        , childMeta.parentMeta());
                throw new MetaException(m);
            }
        }
    }


    final DefaultTableMeta<T> table;

    final String fieldName;

    final Class<?> javaType;

    final String columnName;

    private final String comment;

    private final String defaultValue;

    final MappingType mappingType;

    final boolean nullable;

    private final boolean insertable;

    final UpdateMode updateMode;

    private final int precision;

    private final int scale;

    private final GeneratorMeta generatorMeta;

    private final GeneratorType generatorType;

    private final List<Class<?>> elementTypeList;

    private final boolean codec;

    private DefaultFieldMeta(final TableMeta<T> table, final Field field) throws MetaException {
        Objects.requireNonNull(table);
        Objects.requireNonNull(field);

        _Assert.isAssignable(field.getDeclaringClass(), table.javaType());

        this.table = (DefaultTableMeta<T>) table;
        this.fieldName = field.getName();
        this.javaType = field.getType();
        try {
            final Column column = FieldMetaUtils.columnMeta(table.javaType(), field);

            this.precision = column.precision();
            this.scale = column.scale();
            this.columnName = FieldMetaUtils.columnName(column, field);
            final boolean isDiscriminator = FieldMetaUtils.isDiscriminator(this);

            this.mappingType = FieldMetaUtils.fieldMappingType(field, isDiscriminator);
            if (this.mappingType instanceof ElementMappingType) {
                this.elementTypeList = ArrayUtils.asUnmodifiableList(field.getAnnotation(Mapping.class).elements());
            } else {
                this.elementTypeList = Collections.emptyList();
            }

            final Generator generator;
            if (table instanceof ChildTableMeta && _MetaBridge.ID.equals(this.fieldName)) {
                this.insertable = true;
                generator = null;
            } else {
                generator = field.getAnnotation(Generator.class);
                this.insertable = FieldMetaUtils.columnInsertable(this, generator, column, isDiscriminator);
            }
            this.updateMode = FieldMetaUtils.columnUpdatable(this, column, isDiscriminator);
            this.comment = FieldMetaUtils.columnComment(column, this, isDiscriminator);
            this.nullable = !_MetaBridge.RESERVED_PROPS.contains(this.fieldName)
                    && !isDiscriminator
                    && column.nullable();
            this.defaultValue = column.defaultValue();

            this.codec = field.getAnnotation(Codec.class) != null;

            final GeneratorType generatorType;
            if (generator == null) {
                this.generatorType = null;
                this.generatorMeta = null;
            } else if ((generatorType = generator.type()) == GeneratorType.PRECEDE) {
                this.generatorType = generatorType;
                this.generatorMeta = FieldMetaUtils.columnGeneratorMeta(generator, this, isDiscriminator);
            } else if (generatorType == GeneratorType.POST) {
                this.generatorType = generatorType;
                this.generatorMeta = null;
                FieldMetaUtils.validatePostGenerator(this, generator, isDiscriminator);
            } else {
                throw _Exceptions.unexpectedEnum(generatorType);
            }
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
    public final FieldMeta<?> fieldMeta() {
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
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final MappingType mappingType() {
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
    public final GeneratorType generatorType() {
        return this.generatorType;
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
    public final ParamMeta paramMeta() {
        // always this
        return this;
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
    public final List<Class<?>> elementTypes() {
        return this.elementTypeList;
    }

    @Override
    public final boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof DefaultFieldMeta) {
            final DefaultFieldMeta<?> o = (DefaultFieldMeta<?>) obj;
            match = this.table.javaType == o.table.javaType
                    && this.fieldName.equals(o.fieldName);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.table.javaType, this.fieldName);
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
                .append(this.table.javaType.getName())
                .append('.')
                .append(this.fieldName)
                .append(']')
                .toString();
    }

    @Override
    public final void appendSelection(final _SqlContext context) {
        context.appendField(this);

        context.sqlBuilder()
                .append(Constant.SPACE_AS_SPACE)
                .append(this.fieldName);
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        if (context.visible() != Visible.BOTH && this.fieldName.equals(_MetaBridge.VISIBLE)) {
            throw _Exceptions.visibleField(context.visible(), this);
        }
        context.appendField(this);
    }


    /*################################## blow private method ##################################*/

    private static class DefaultSimpleFieldMeta<T extends IDomain> extends DefaultFieldMeta<T> {

        private DefaultSimpleFieldMeta(TableMeta<T> table, Field field) throws MetaException {
            super(table, field);
        }

    }

    private static class DefaultIndexFieldMeta<T extends IDomain> extends DefaultFieldMeta<T>
            implements IndexFieldMeta<T> {

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

    private static class DefaultUniqueFieldMeta<T extends IDomain> extends DefaultIndexFieldMeta<T>
            implements UniqueFieldMeta<T> {

        private DefaultUniqueFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta
                , @Nullable Boolean fieldAsc) throws MetaException {
            super(table, field, indexMeta, fieldAsc);
            if (!indexMeta.unique()) {
                String m = String.format("indexMeta[%s] non-unique.", indexMeta);
                throw new MetaException(m);
            }
        }
    }

    private static final class DefaultPrimaryFieldMeta<T extends IDomain> extends DefaultUniqueFieldMeta<T>
            implements PrimaryFieldMeta<T> {

        private DefaultPrimaryFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta
                , @Nullable Boolean fieldAsc) throws MetaException {
            super(table, field, indexMeta, fieldAsc);
            if (!_MetaBridge.ID.equals(field.getName())) {
                String m = String.format("indexMeta[%s] not primary.", indexMeta);
                throw new MetaException(m);
            }
        }


    }//DefaultPrimaryFieldMeta


}
