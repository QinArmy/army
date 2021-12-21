package io.army.criteria.impl;

import io.army.criteria.ConvertExpression;
import io.army.criteria.Expression;
import io.army.criteria.FieldExpression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

class ConvertExpressionImpl<E> extends OperationExpression<E> implements ConvertExpression<E> {

    static <O> ConvertExpressionImpl<O> build(_Expression<?> original, MappingType convertType) {
        return original instanceof FieldExpression
                ? new FieldConvertExpressionImpl<>(original, convertType)
                : new ConvertExpressionImpl<>(original, convertType);
    }

    final _Expression<?> original;

    private final MappingType convertType;

    private ConvertExpressionImpl(_Expression<?> original, MappingType convertType) {
        this.original = original;
        this.convertType = convertType;
    }

    @Override
    public final void appendSql(_SqlContext context) {
        this.original.appendSql(context);
    }


    @Override
    public final MappingType mappingType() {
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

        private FieldConvertExpressionImpl(_Expression<?> original, MappingType convertType) {
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
