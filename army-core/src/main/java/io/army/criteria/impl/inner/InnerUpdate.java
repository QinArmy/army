package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.SQLModifier;
import io.army.criteria.Update;
import io.army.meta.FieldMeta;

import java.util.List;

@DeveloperForbid
public interface InnerUpdate extends Update, InnerSQL {

    /**
     * @return a unmodifiable list
     */
    List<SQLModifier> modifierList();

    /**
     * @return a unmodifiable list
     */
    List<FieldMeta<?, ?>> targetFieldList();

    /**
     * @return a unmodifiable list
     */
    List<Expression<?>> valueExpList();

}
