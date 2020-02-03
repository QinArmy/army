package io.army.criteria.impl;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.annotation.Column;
import io.army.criteria.LongExpression;
import io.army.criteria.MetaException;
import io.army.criteria.NumberExpression;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;
import io.army.util.ClassUtils;
import io.army.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * created  on 2018/11/18.
 */
class DefaultFieldMeta<T extends IDomain, F> extends AbstractExpression<F> implements FieldMeta<T, F> {

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

        if (field.getType() == Long.class) {
            fieldMeta = new DefaultLongFieldMeta<>(table, field, false, false);
        } else if (ClassUtils.isAssignable(Number.class, field.getType())) {
            fieldMeta = new DefaultNumberFieldMeta<>(table, field, false, false);
        } else {
            fieldMeta = new DefaultFieldMeta<>(table, field, false, false);
        }
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
        if (field.getType() == Long.class) {
            fieldMeta = (IndexFieldMeta<T, F>) new DefaultLongIndexFieldMeta<>(table, field, indexMeta
                    , fieldUnique, fieldAsc);
        } else if (ClassUtils.isAssignable(Number.class, field.getType())) {
            NumberIndexFieldMeta<T, ?> numberIndexFieldMeta = new DefaultNumberIndexFieldMeta<>(table, field, indexMeta
                    , fieldUnique, fieldAsc);
            fieldMeta = (IndexFieldMeta<T, F>) numberIndexFieldMeta;
        } else {
            fieldMeta = new DefaultIndexFieldMeta<>(table, field, indexMeta, fieldUnique, fieldAsc);
        }

        FieldMeta<?, ?> actualFieldMeta;
        if (isId) {
            actualFieldMeta = ID_INSTANCE_MAP.putIfAbsent(table.javaType(), fieldMeta);
        } else {
            actualFieldMeta = INSTANCE_MAP.putIfAbsent(field, fieldMeta);
        }
        if (actualFieldMeta != null && actualFieldMeta != fieldMeta) {
            fieldMeta = (IndexFieldMeta<T, F>)actualFieldMeta;
        }
        return fieldMeta;
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
            throw new MetaException(ErrorCode.META_ERROR, e, "debugSQL entity[%s] mapping property[%s] meta error"
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


    private static class DefaultNumberFieldMeta<T extends IDomain, F extends Number> extends DefaultFieldMeta<T, F>
            implements NumberFieldMeta<T, F> {

        private DefaultNumberFieldMeta(TableMeta<T> table, Field field, boolean unique, boolean index)
                throws MetaException {
            super(table, field, unique, index);
        }

        @Override
        public final NumberExpression<F> mod(NumberExpression<F> operator) {
            return null;
        }

        @Override
        public final NumberExpression<F> mod(F f) {
            return null;
        }

        @Override
        public final NumberExpression<F> multiply(NumberExpression<F> multiplicand) {
            return null;
        }

        @Override
        public final NumberExpression<F> multiply(F f) {
            return null;
        }

        @Override
        public final NumberExpression<F> add(NumberExpression<F> augend) {
            return null;
        }

        @Override
        public final NumberExpression<F> add(F f) {
            return null;
        }

        @Override
        public final NumberExpression<F> subtract(NumberExpression<F> subtrahend) {
            return null;
        }

        @Override
        public final NumberExpression<F> subtract(F f) {
            return null;
        }

        @Override
        public final NumberExpression<F> divide(NumberExpression<F> divisor) {
            return null;
        }

        @Override
        public final NumberExpression<F> divide(F f) {
            return null;
        }

        @Override
        public final NumberExpression<F> negate() {
            return null;
        }
    }

    private static class DefaultLongFieldMeta<T extends IDomain> extends DefaultNumberFieldMeta<T, Long>
            implements LongFieldMeta<T> {

        private DefaultLongFieldMeta(TableMeta<T> table, Field field, boolean unique, boolean index)
                throws MetaException {
            super(table, field, unique, index);
        }

        @Override
        public final LongExpression and(LongExpression operator) {
            return null;
        }

        @Override
        public final LongExpression and(long operator) {
            return null;
        }

        @Override
        public final LongExpression or(LongExpression operator) {
            return null;
        }

        @Override
        public final LongExpression or(long operator) {
            return null;
        }

        @Override
        public final LongExpression xor(LongExpression operator) {
            return null;
        }

        @Override
        public final LongExpression xor(long operator) {
            return null;
        }

        @Override
        public final LongExpression inversion(LongExpression operator) {
            return null;
        }

        @Override
        public final LongExpression inversion(long operator) {
            return null;
        }

        @Override
        public final LongExpression rightShift(long operator) {
            return null;
        }

        @Override
        public final LongExpression rightShift(LongExpression operator) {
            return null;
        }

        @Override
        public final LongExpression leftShift(long operator) {
            return null;
        }

        @Override
        public final LongExpression leftShift(LongExpression operator) {
            return null;
        }
    }

    private static class DefaultNumberIndexFieldMeta<T extends IDomain, F extends Number>
            extends DefaultNumberFieldMeta<T, F> implements NumberIndexFieldMeta<T, F> {

        private final IndexMeta<T> indexMeta;

        private final Boolean fieldAsc;

        private DefaultNumberIndexFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta, boolean fieldUnique,
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

    private static final class DefaultLongIndexFieldMeta<T extends IDomain> extends DefaultNumberIndexFieldMeta<T, Long>
            implements LongIndexFieldMeta<T> {

        public DefaultLongIndexFieldMeta(TableMeta<T> table, Field field, IndexMeta<T> indexMeta
                , boolean fieldUnique, @Nullable Boolean fieldAsc) throws MetaException {
            super(table, field, indexMeta, fieldUnique, fieldAsc);
        }

        @Override
        public LongExpression and(LongExpression operator) {
            return null;
        }

        @Override
        public LongExpression and(long operator) {
            return null;
        }

        @Override
        public LongExpression or(LongExpression operator) {
            return null;
        }

        @Override
        public LongExpression or(long operator) {
            return null;
        }

        @Override
        public LongExpression xor(LongExpression operator) {
            return null;
        }

        @Override
        public LongExpression xor(long operator) {
            return null;
        }

        @Override
        public LongExpression inversion(LongExpression operator) {
            return null;
        }

        @Override
        public LongExpression inversion(long operator) {
            return null;
        }

        @Override
        public LongExpression rightShift(long operator) {
            return null;
        }

        @Override
        public LongExpression rightShift(LongExpression operator) {
            return null;
        }

        @Override
        public LongExpression leftShift(long operator) {
            return null;
        }

        @Override
        public LongExpression leftShift(LongExpression operator) {
            return null;
        }
    }


}
