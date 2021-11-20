package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.FieldExpression;
import io.army.criteria.SqlContext;
import io.army.dialect.SqlBuilder;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

class BracketsExpression<E> extends AbstractExpression<E> {

    static <E> BracketsExpression<E> build(Expression<E> expression) {
        BracketsExpression<E> bracketsExpression;
        if (expression instanceof FieldExpression) {
            bracketsExpression = new FieldBracketsExpression<>(expression);
        } else {
            bracketsExpression = new BracketsExpression<>(expression);
        }
        return bracketsExpression;
    }

    final Expression<E> exp;

    private BracketsExpression(Expression<E> exp) {
        this.exp = exp;
    }

    @Override
    public final void appendSQL(SqlContext context) {
        SqlBuilder builder = context.sqlBuilder();
        builder.append(" ( ");
        exp.appendSQL(context);
        builder.append(" )");
    }

    @Override
    public final MappingType mappingMeta() {
        return exp.mappingMeta();
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

        private FieldBracketsExpression(Expression<E> exp) {
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
