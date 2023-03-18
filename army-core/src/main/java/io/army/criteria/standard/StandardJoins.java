package io.army.criteria.standard;

import io.army.criteria.Statement;

public interface StandardJoins extends Statement.JoinBuilder,
        Statement._DynamicTabularItemClause<
                Statement._OnClause<StandardStatement._DynamicJoinSpec>,
                Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>>>,
        StandardStatement._StandardDynamicNestedClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>> {

}
