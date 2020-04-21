package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SQLContext;

/**
 * created  on 2018/11/25.
 */
final class NotPredicate extends AbstractPredicate {

    static IPredicate build(Expression<?> expression) {
        IPredicate IPredicate;
        if (expression instanceof NotPredicate) {
            NotPredicate originalPredicate = (NotPredicate) (expression);
            if (originalPredicate.expression instanceof NotPredicate) {
                IPredicate = (NotPredicate) originalPredicate.expression;
            } else {
                IPredicate = new NotPredicate(expression, !originalPredicate.not);
            }
        } else {
            IPredicate = new NotPredicate(expression, true);
        }
        return IPredicate;
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
            context.sqlBuilder()
                    .append(UnaryOperator.NOT.rendered())
                    .append(" ");
        }
        expression.appendSQL(context);
    }

    @Override
    public String beforeAs() {
        String text = "";
        if(not){
            text = UnaryOperator.NOT.rendered() + " ";
        }
        return text + expression;
    }
}
