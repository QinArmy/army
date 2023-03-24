package io.army.criteria.impl;

import io.army.criteria.impl.inner._SelectionGroup;
import io.army.meta.TableMeta;

interface TableFieldGroup extends _SelectionGroup {


    boolean isIllegalGroup(TableMeta<?> table);

}
