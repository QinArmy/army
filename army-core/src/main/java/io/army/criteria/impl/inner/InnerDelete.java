package io.army.criteria.impl.inner;

import io.army.criteria.Delete;
import io.army.criteria.SQLModifier;

import java.util.List;

@DeveloperForbid
public interface InnerDelete extends Delete, InnerSQL {

    /**
     * @return a unmodifiable list
     */
    List<SQLModifier> modifierList();

}
