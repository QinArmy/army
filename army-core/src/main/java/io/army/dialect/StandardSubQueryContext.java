package io.army.dialect;

public class StandardSubQueryContext extends AbstractStandardDQLContext implements SubQueryContext {

    static StandardSubQueryContext build(ClauseSQLContext original) {
        return new StandardSubQueryContext(original);
    }

    StandardSubQueryContext(ClauseSQLContext original) {
        super(original);
    }
}
