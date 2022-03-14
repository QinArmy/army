package io.army.dialect;

import io.army.criteria.GenericField;

public interface _MultiUpdateContext extends _UpdateContext {


    String safeTableAliasFor(GenericField<?> field);


}
