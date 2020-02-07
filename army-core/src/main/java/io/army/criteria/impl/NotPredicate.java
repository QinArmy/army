package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Predicate;
import io.army.criteria.SQLContext;
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
    protected void afterSpace(SQLContext context) {
        if (not) {
            context.stringBuilder()
                    .append(UnaryOperator.NOT.rendered())
                    .append(" ");
        }
        expression.appendSQL(context);
    }

    @Override
    public String toString() {
        String text = "";
        if(not){
            text = UnaryOperator.NOT.rendered() + " ";
        }
        return text + expression;
    }
}
