package io.army.criteria;

/**
 * this interface representing a predicate than at least contains a {@link io.army.meta.FieldExpression}.
 */
public interface SpecialPredicate extends IPredicate {

    @Override
    void appendSQL(SQLContext context);

    void appendPredicate(SQLContext context);
}
