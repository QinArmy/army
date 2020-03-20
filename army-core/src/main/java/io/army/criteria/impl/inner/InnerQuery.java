package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SQLModifier;
import io.army.criteria.SelectPart;

import java.util.List;

@DeveloperForbid
public interface InnerQuery extends InnerSQL {

    /**
     * @return a unmodifiable list
     */
    List<SQLModifier> modifierList();

    /**
     * @return a unmodifiable list
     */
    List<SelectPart> selectPartList();

    /**
     * @return a unmodifiable list
     */
    List<Expression<?>> groupExpList();

    /**
     * @return a unmodifiable list
     */
    List<IPredicate> havingList();

    /**
     * @return a unmodifiable list
     */
    List<Expression<?>> sortExpList();

    int offset();

    int rowCount();

}
