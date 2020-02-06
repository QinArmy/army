package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Predicate;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;

import java.util.List;

/**
 * created  on 2018/11/25.
 */
final class NotPredicate extends AbstractPredicate {

    static Predicate build(Expression<?> expression) {
        Predicate predicate;
        if (expression instanceof NotPredicate) {
            NotPredicate originalPredicate = (NotPredicate) (expression);
            if (originalPredicate.expression instanceof NotPredicate) {
                predicate = (NotPredicate) originalPredicate.expression;
            } else {
                predicate = new NotPredicate(expression, !originalPredicate.not);
            }
        } else {
            predicate = new NotPredicate(expression, true);
        }
        return predicate;
    }

    private final Expression<?> expression;

    private final boolean not;

    private NotPredicate(Expression<?> expression, boolean not) {
        this.expression = expression;
        this.not = not;

    }

    @Override
    protected void appendSQLBeforeWhitespace(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        if (not) {
            builder.append(UnaryOperator.NOT.rendered());
            builder.append(" ");
        }
        expression.appendSQL(sql,builder, paramWrapperList);
    }

}
