package io.army.dialect;

import io.army.criteria.Visible;

public class StandardSubQueryContext extends AbstractClauseContext {

    static StandardSubQueryContext build(ClauseSQLContext original) {
        return null;
    }

    private StandardSubQueryContext(Dialect dialect, Visible visible) {
        super(dialect, visible);
    }

    private StandardSubQueryContext(Dialect dialect, Visible visible, StringBuilder sqlBuilder) {
        super(dialect, visible, sqlBuilder);
    }

    @Override
    public void currentClause(Clause clause) {

    }
}
