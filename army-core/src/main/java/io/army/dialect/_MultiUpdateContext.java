package io.army.dialect;

import io.army.criteria.TableField;

public interface _MultiUpdateContext extends _UpdateContext {


    String safeTableAliasFor(TableField<?> field);


}
