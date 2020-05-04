package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardSelect;

import java.util.EnumSet;

final class StandardSelectContext extends AbstractClauseContext implements SelectContext {

    static StandardSelectContext build(Dialect dialect, Visible visible, InnerStandardSelect select) {
        return new StandardSelectContext(dialect, visible);
    }

    static StandardSelectContext build(Dialect dialect, ComposeQueryContext composeQueryContext) {
        return new StandardSelectContext(dialect, composeQueryContext.visible, composeQueryContext.sqlBuilder);
    }

    static StandardSelectContext build(ClauseSQLContext original) {
        return null;
    }

    private static final EnumSet<Clause> SELECT_PREV_SET = EnumSet.noneOf(Clause.class);

    private static final EnumSet<Clause> SELECT_LIST_PREV_SET = EnumSet.of(Clause.SELECT);

    private static final EnumSet<Clause> FROM_PREV_SET = EnumSet.of(Clause.SELECT_LIST);

    private static final EnumSet<Clause> ON_PREV_SET = EnumSet.of(Clause.FROM, Clause.ON);

    private static final EnumSet<Clause> WHERE_PREV_SET = EnumSet.of(Clause.FROM, Clause.ON);

    private static final EnumSet<Clause> GROUP_BY_PREV_SET = EnumSet.of(Clause.FROM, Clause.ON, Clause.WHERE);

    private static final EnumSet<Clause> HAVING_PREV_SET = EnumSet.of(Clause.GROUP_BY);

    private static final EnumSet<Clause> ORDER_BY_PREV_SET = EnumSet.of(Clause.FROM, Clause.WHERE
            , Clause.GROUP_BY, Clause.HAVING);


    StandardSelectContext(Dialect dialect, Visible visible) {
        super(dialect, visible);
    }

    public StandardSelectContext(Dialect dialect, Visible visible, StringBuilder sqlBuilder) {
        super(dialect, visible, sqlBuilder);
    }

    @Override
    public void currentClause(final Clause clause) {

        switch (clause) {
            case SELECT:
                handleNextClause(clause, SELECT_PREV_SET);
                break;
            case SELECT_LIST:
                handleNextClause(clause, SELECT_LIST_PREV_SET);
                break;
            case FROM:
                handleNextClause(clause, FROM_PREV_SET);
                break;
            case ON:
                handleNextClause(clause, ON_PREV_SET);
                break;
            case WHERE:
                handleNextClause(clause, WHERE_PREV_SET);
                break;
            case GROUP_BY:
                handleNextClause(clause, GROUP_BY_PREV_SET);
                break;
            case HAVING:
                handleNextClause(clause, HAVING_PREV_SET);
                break;
            case ORDER_BY:
                handleNextClause(clause, ORDER_BY_PREV_SET);
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Clause[%s] is supported by %s", clause, this.getClass().getName()));
        }
    }



    /*################################## blow private method ##################################*/

    private void handleNextClause(final Clause next, final EnumSet<Clause> prevSet) {
        final Clause current = currentClause();
        if (!prevSet.contains(current)) {
            throw DialectUtils.createArmyCriteriaException();
        }
        if (!this.clauseStack.empty()) {
            this.clauseStack.pop();
        }
        this.clauseStack.push(next);
        next.appendSQL(this.sqlBuilder);
    }


}
