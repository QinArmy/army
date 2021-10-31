package io.army.criteria.impl;

import io.army.criteria.ConvertExpression;
import io.army.criteria.Expression;
import io.army.criteria.FieldExpression;
import io.army.criteria.SQLContext;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;

import java.util.Collection;

class ConvertExpressionImpl<E> extends AbstractExpression<E> implements ConvertExpression<E> {

    static <O> ConvertExpressionImpl<O> build(Expression<?> original, MappingType convertType) {
        return original instanceof FieldExpression
                ? new FieldConvertExpressionImpl<>(original, convertType)
                : new ConvertExpressionImpl<>(original, convertType);
    }

    final Expression<?> original;

    private final MappingType convertType;

    private ConvertExpressionImpl(Expression<?> original, MappingType convertType) {
        this.original = original;
        this.convertType = convertType;
    }

    @Override
    public final void appendSQL(SQLContext context) {
        this.original.appendSQL(context);
    }


    @Override
    public final MappingType mappingMeta() {
        return this.convertType;
    }

    @Override
    public final Expression<?> originalExp() {
        return this.original;
    }

    @Override
    public final boolean containsSubQuery() {
        return this.original.containsSubQuery();
    }

    @Override
    public final String toString() {
        return original.toString();
    }

    private static final class FieldConvertExpressionImpl<E> extends ConvertExpressionImpl<E>
            implements FieldExpression<E> {

        private FieldConvertExpressionImpl(Expression<?> original, MappingType convertType) {
            super(original, convertType);
        }

        @Override
        public final boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
            return this.original.containsField(fieldMetas);
        }

        @Override
        public final boolean containsFieldOf(TableMeta<?> tableMeta) {
            return this.original.containsFieldOf(tableMeta);
        }

        @Override
        public final int containsFieldCount(TableMeta<?> tableMeta) {
            return this.original.containsFieldCount(tableMeta);
        }
    }


}
