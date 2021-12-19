package io.army.criteria.impl;

import io.army.criteria.FieldExpression;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

class BracketsExpression<E> extends AbstractExpression<E> {

    static <E> BracketsExpression<E> build(_Expression<E> expression) {
        BracketsExpression<E> bracketsExpression;
        if (expression instanceof FieldExpression) {
            bracketsExpression = new FieldBracketsExpression<>(expression);
        } else {
            bracketsExpression = new BracketsExpression<>(expression);
        }
        return bracketsExpression;
    }

    final _Expression<E> exp;

    private BracketsExpression(_Expression<E> exp) {
        this.exp = exp;
    }

    @Override
    public final void appendSql(_SqlContext context) {
        StringBuilder builder = context.sqlBuilder();
        builder.append(" ( ");
        exp.appendSql(context);
        builder.append(" )");
    }

    @Override
    public final MappingType mappingType() {
        return exp.mappingType();
    }

    @Override
    public final String toString() {
        return "(" + exp + ")";
    }

    @Override
    public final boolean containsSubQuery() {
        return this.exp.containsSubQuery();
    }


    private static final class FieldBracketsExpression<E> extends BracketsExpression<E> implements FieldExpression<E> {

        private FieldBracketsExpression(_Expression<E> exp) {
            super(exp);
        }

        @Override
        public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
            return exp.containsField(fieldMetas);
        }

        @Override
        public boolean containsFieldOf(TableMeta<?> tableMeta) {
            return exp.containsFieldOf(tableMeta);
        }

        @Override
        public int containsFieldCount(TableMeta<?> tableMeta) {
            return exp.containsFieldCount(tableMeta);
        }
    }
}
