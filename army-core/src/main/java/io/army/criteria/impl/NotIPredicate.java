package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SQLContext;

/**
 * created  on 2018/11/25.
 */
final class NotIPredicate extends AbstractIPredicate {

    static IPredicate build(Expression<?> expression) {
        IPredicate IPredicate;
        if (expression instanceof NotIPredicate) {
            NotIPredicate originalPredicate = (NotIPredicate) (expression);
            if (originalPredicate.expression instanceof NotIPredicate) {
                IPredicate = (NotIPredicate) originalPredicate.expression;
            } else {
                IPredicate = new NotIPredicate(expression, !originalPredicate.not);
            }
        } else {
            IPredicate = new NotIPredicate(expression, true);
        }
        return IPredicate;
    }

    private final Expression<?> expression;

    private final boolean not;

    private NotIPredicate(Expression<?> expression, boolean not) {
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
    public String beforeAs() {
        String text = "";
        if(not){
            text = UnaryOperator.NOT.rendered() + " ";
        }
        return text + expression;
    }
}
