package io.army.criteria.standard;


import io.army.criteria.Statement;

public interface StandardCrosses extends Statement.JoinBuilder,
        Statement._DynamicTabularItemClause<
                StandardStatement._DynamicJoinSpec,
                Statement._AsClause<StandardStatement._DynamicJoinSpec>>,
        StandardStatement._StandardDynamicNestedClause<StandardStatement._DynamicJoinSpec> {


}
